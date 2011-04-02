/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

import com.trolltech.qt.internal.*;

// NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class InitializeTask extends Task {

    private boolean verbose;
    private PropertyHelper props;
    private String configuration;
    private boolean debug;

    /*
     * These properties are set outside of this task
     *
     * TODO: These flags should be documented here and if possibly, outside in
     * build documentation.
     * Or rather these binds shouldn’t exist, how much of this could be moved to
     * xml side?
     */
    public static final String LIBDIR           = "qtjambi.qt.libdir";
    public static final String INCLUDEDIR       = "qtjambi.qt.includedir";
    public static final String PLUGINSDIR       = "qtjambi.qt.pluginsdir";
    public static final String PHONONLIBDIR     = "qtjambi.phonon.libdir";
    public static final String JAVALIBDIR		= "qtjambi.java.library.path";
    public static final String JAMBILIBDIR	 	= "qtjambi.jambi.libdir";
    public static final String VERSION          = "qtjambi.version";

    /*
     * These properties are set inside this task
     */
    public static final String COMPILER         = "qtjambi.compiler";
    public static final String CONFIGURATION    = "qtjambi.configuration";
    public static final String DBUS             = "qtjambi.dbus";
    public static final String OPENGL           = "qtjambi.opengl";
    public static final String OSNAME           = "qtjambi.osname";
    public static final String PHONON           = "qtjambi.phonon";
    public static final String PHONON_DS9       = "qtjambi.phonon_ds9";
    public static final String PHONON_GSTREAMER = "qtjambi.phonon_gstreamer";
    public static final String PHONON_QT7       = "qtjambi.phonon_qt7";
    public static final String QMAKESPEC        = "qtjambi.qmakespec";
    public static final String SQLITE           = "qtjambi.sqlite";
    public static final String WEBKIT           = "qtjambi.webkit";
    public static final String XMLPATTERNS      = "qtjambi.xmlpatterns";
    public static final String HELP             = "qtjambi.help";
    public static final String MULTIMEDIA       = "qtjambi.multimedia";
    public static final String SCRIPT           = "qtjambi.script";
    public static final String SCRIPTTOOLS      = "qtjambi.scripttools";
    public static final String QTCONFIG         = "qtjambi.qtconfig";

    // Windows specific vars...
    public static final String VSINSTALLDIR     = "qtjambi.vsinstalldir";
    public static final String VSREDISTDIR      = "qtjambi.vsredistdir";

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void execute() throws BuildException {
        props = PropertyHelper.getPropertyHelper(getProject());

        FindCompiler finder = new FindCompiler(props);

        props.setNewProperty((String) null, OSNAME, finder.decideOSName());
        props.setNewProperty((String) null, COMPILER, finder.decideCompiler());

        finder.checkCompilerDetails();
        //finder.checkCompilerBits();

        props.setNewProperty((String) null, CONFIGURATION, decideConfiguration());

        String phonon = decidePhonon(props);

        props.setNewProperty((String) null, SQLITE, decideSqlite());

        String webkit = decideWebkit();
        if ("true".equals(webkit) && "true".equals(phonon))
            props.setNewProperty((String) null, WEBKIT, webkit);

        String script = decideScript();
        if ("true".equals(script))
            props.setNewProperty((String) null, SCRIPT, script);

        String scripttools = decideScripttools();
        if ("true".equals(scripttools))
            props.setNewProperty((String) null, SCRIPTTOOLS, scripttools);

        String helptool = decideHelp();
        if ("true".equals(helptool))
            props.setNewProperty((String) null, HELP, helptool);

        String multimedia = decideMultimedia();
        if ("true".equals(multimedia))
            props.setNewProperty((String) null, MULTIMEDIA, multimedia);

        String patterns = decideXMLPatterns();
        if ("true".equals(patterns))
            props.setNewProperty((String) null, XMLPATTERNS, patterns);

        String opengl = decideOpenGL();
        if ("true".equals(opengl))
            props.setNewProperty((String) null, OPENGL, opengl);
    }

    /**
     * Decides whether to use debug or release configuration
     * @return
     */
    private String decideConfiguration() {
        String result = null;

        debug = "debug".equals(configuration);
        result = debug ? "debug" : "release";

        if (verbose) System.out.println(CONFIGURATION + ": " + result);
        return result;
    }

    private boolean doesQtLibExist(String name, int version, String librarydir) {
        StringBuilder path = new StringBuilder();
        path.append(librarydir);
        path.append("/");
        path.append(LibraryEntry.formatQtName(name, debug, version));
        //System.out.println("Checking QtLib: " + path);
        return new File(path.toString()).exists();
    }

    private boolean doesQtLibExist(String name, int version) {
        return doesQtLibExist(name, version, props.getProperty((String) null, LIBDIR).toString());
    }

    private boolean doesQtPluginExist(String name, String subdir) {
        StringBuilder path = new StringBuilder();
        path.append(props.getProperty((String) null, PLUGINSDIR));
        path.append("/plugins/");
        path.append(subdir);
        path.append("/");
        //! TODO: useful?
        path.append(LibraryEntry.formatPluginName(name, false, debug));
        return new File(path.toString()).exists();
    }

    /**
     * Decide whether we have phonon plugin and and 
     * check correct phonon backend to use for this OS.
     */
    private String decidePhonon(PropertyHelper props) {
        boolean exists = doesQtLibExist("phonon", 4, (String) props.getProperty((String) null, PHONONLIBDIR));
        String phonon = String.valueOf(exists);
        if (verbose) {
            System.out.println(PHONON + ": " + phonon);
        }

        if(!exists) return "false";
        else addToQtConfig("phonon");

        props.setNewProperty((String) null, PHONON, phonon);

        switch (OSInfo.os()) {
        case Windows:
            props.setNewProperty((String) null, PHONON_DS9, "true");
            break;
        case Linux:
            props.setNewProperty((String) null, PHONON_GSTREAMER, "true");
            if (doesQtLibExist("QtDBus", 4))
                props.setNewProperty((String) null, DBUS, "true");
            break;
        case MacOS:
            props.setNewProperty((String) null, PHONON_QT7, "true");
            if (doesQtLibExist("QtDBus", 4))
                props.setNewProperty((String) null, DBUS, "true");
            break;
        }

        return phonon;
    }

    /**
     * Adds new library to qtjambi.qtconfig property, which is used
     * to specify additional qt libraries compiled. 
     * @param config Library to add
     */
    private void addToQtConfig(String config) {
        String oldConfig = (String) props.getProperty((String) null, QTCONFIG);
        String newConfig;
        if(oldConfig != null) {
            newConfig = oldConfig + " " + config;
        } else {
            newConfig = config;
        }
        props.setProperty((String) null, QTCONFIG, newConfig, false);
    }

    private String decideSqlite() {
        String result = String.valueOf(doesQtPluginExist("qsqlite", "sqldrivers"));
        if (verbose) System.out.println(SQLITE + ": " + result);
        return result;
    }

    private String decideHelp() {
        String result = String.valueOf(doesQtLibExist("QtHelp", 4));
        if (verbose) System.out.println(HELP + ": " + result);
        return result;
    }

    private String decideMultimedia() {
        String result = String.valueOf(doesQtLibExist("QtMultimedia", 4));
        if (verbose) System.out.println(MULTIMEDIA + ": " + result);
        if("true".equals(result)) addToQtConfig("multimedia");
        return result;
    }

    private String decideScript() {
        String result = String.valueOf(doesQtLibExist("QtScript", 4));
        if (verbose) System.out.println(SCRIPT + ": " + result);
        if("true".equals(result)) addToQtConfig("script");
        return result;
    }

    private String decideScripttools() {
        String result = String.valueOf(doesQtLibExist("QtScriptTools", 4));
        if (verbose) System.out.println(SCRIPTTOOLS + ": " + result);
        if("true".equals(result)) addToQtConfig("scripttools");
        return result;
    }
    private String decideWebkit() {
        String result = String.valueOf(doesQtLibExist("QtWebKit", 4));
        if (verbose) System.out.println(WEBKIT + ": " + result);
        if("true".equals(result)) addToQtConfig("webkit");
        return result;
    }

    private String decideXMLPatterns() {
        String result = String.valueOf(doesQtLibExist("QtXmlPatterns", 4));
        if (verbose) System.out.println(XMLPATTERNS + ": " + result);
        return result;
    }

    private String decideOpenGL() {
        String result = String.valueOf(doesQtLibExist("QtOpenGL", 4));
        if (verbose) System.out.println(OPENGL + ": " + result);
        return result;
    }

}
