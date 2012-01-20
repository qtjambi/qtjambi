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

import java.io.File;

import org.apache.tools.ant.BuildException;  
import org.apache.tools.ant.Task;
import org.apache.tools.ant.PropertyHelper;

import com.trolltech.qt.osinfo.OSInfo;

// NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class LibraryEntry extends Task {

    public static final String TYPE_DEFAULT            = "user";

    public static final String TYPE_DSO                = "dso";
    public static final String TYPE_PLUGIN             = "plugin";
    public static final String TYPE_SYSTEM             = "system";
    public static final String TYPE_QT                 = "qt";
    public static final String TYPE_QTJAMBI            = "qtjambi";
    public static final String TYPE_QTJAMBI_JNI        = "qtjambi-jni";
    public static final String TYPE_QTJAMBI_PLUGIN     = "qtjambi-plugin";
    public static final String TYPE_UNVERSIONED_PLUGIN = "unversioned-plugin";

    public static final String LOAD_DEFAULT            = "default";
    public static final String LOAD_YES                = "yes";
    public static final String LOAD_NEVER              = "never";

    /*
     *  set to specify where the plugin should be saved.
     *  Used to reduce redundancy of build.xml.
     *  TODO:
     *  Other variables could use same kind of solutions, I think.
     *  Whole path system needs to be rewritten to correspond
     *  new libdir, includedir, plugindir properties.
     *  Well I'm partially there for now with new destSubdir
     *  setting.
     */
    private String type = TYPE_DEFAULT;
    private String name;
    private File rootPath;
    private boolean kdephonon = false;
    private String subdir;
    private String destSubdir;
    private String load = LOAD_DEFAULT;
    private String srcPath;
    private boolean included = true;
    private String dsoVersion;

    public boolean getKdephonon() {
        return kdephonon;
    }
    public void setKdephonon(boolean enabled) {
        this.kdephonon = enabled;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public File getRootPath() {
        return rootPath;
    }
    public void setRootPath(File rootPath) {
        this.rootPath = rootPath;
    }

    public String getSubdir() {
        return subdir;
    }
    public void setSubdir(String subdir) {
        this.subdir = subdir;
    }

    public String getDestSubdir() {
        return destSubdir;
    }
    public void setDestSubdir(String destSubdir) {
        this.destSubdir = destSubdir;
    }

    public String getLoad() {
        return load;
    }
    public void setLoad(String load) {
        this.load = load;
    }


    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }
    public String getSrcPath() {
        return this.srcPath;
    }

    public void setIf(String included) {
        if(included == null || included.length() == 0 || "false".compareToIgnoreCase(included) == 0)
            this.included = false;
        else if("true".compareToIgnoreCase(included) == 0 || new File(included).exists())
            this.included = true;
        else
            this.included = false;
    }
    public String getIf() {
        return Boolean.valueOf(included).toString();
    }
    public boolean isIncluded() {
        return included;
    }

    public void setDsoVersion(String dsoVersion) {
        // canonicalize empty string to null, due to usage pattern in Ant XML
        if(dsoVersion != null && dsoVersion.length() == 0)
            dsoVersion = null;
        this.dsoVersion = dsoVersion;
    }
    public String getDsoVersion() {
        return dsoVersion;
    }

    @Override
    public void execute() throws BuildException {
        if(name == null || name.length() == 0)
            throw new BuildException("Required attribute 'name' missing");

        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        boolean debug = "debug".equals(propertyHelper.getProperty((String) null, InitializeTask.CONFIGURATION));

        // On windows the Qt plugins are versioned
        String qtVersion = (String) propertyHelper.getProperty((String) null, InitializeTask.QT_VERSION);
        String qtMajorVersion = (String) propertyHelper.getProperty((String) null, InitializeTask.QT_VERSION_MAJOR);
        String sonameVersion = (String) propertyHelper.getProperty((String) null, InitializeTask.QTJAMBI_SONAME_VERSION_MAJOR);

        boolean resolved = false;

        if(!resolved && srcPath != null) {
            File srcPathFile = new File(srcPath);
            if(srcPathFile.exists()) {
                // FIXME this should override everythiung
                //name = srcPathFile.getAbsolutePath();
                name = srcPathFile.getName();
                resolved = true;
            }
        }

        if(!resolved) {
            // Fix name
            if(type.equals(TYPE_PLUGIN)) {
                // MacOSX: uses *.dylib and _debug suffix
                String useDsoVersion = dsoVersion;
                if(dsoVersion != null && dsoVersion.compareToIgnoreCase("no-version") == 0) {
                    useDsoVersion = null;
                } else if(dsoVersion != null && dsoVersion.compareToIgnoreCase("use-qt-major-version") == 0) {
                    useDsoVersion = qtMajorVersion;
                } else if(dsoVersion != null && dsoVersion.compareToIgnoreCase("use-qt-version") == 0) {
                    useDsoVersion = qtVersion;
                } else if(dsoVersion != null && dsoVersion.compareToIgnoreCase("use-soname-version") == 0) {
                    useDsoVersion = sonameVersion;
                } else if(dsoVersion == null) {  // the default stratagy
                    if(OSInfo.os() == OSInfo.OS.Windows)
                        useDsoVersion = qtMajorVersion;
                    else
                        useDsoVersion = null;
                }
                name = formatPluginName(name, this.kdephonon, debug, useDsoVersion);
            } else if(type.equals(TYPE_QTJAMBI_PLUGIN)) {
                // MacOSX: uses *.dylib and _debuglib suffix
                name = formatQtJambiPluginName(name, debug, dsoVersion);
            } else if(type.equals(TYPE_QT)) {
                // MacOSX: uses *.dylib and _debug suffix
                name = formatQtName(name, debug, dsoVersion);
            } else if(type.equals(TYPE_QTJAMBI_JNI)) {  // JNI
                // MacOSX: uses *.jnilib and _debuglib suffix
                name = formatQtJambiJniName(name, debug, dsoVersion);
            } else if(type.equals(TYPE_QTJAMBI)) {  // non-JNI base library
                // MacOSX: uses *.dylib and _debuglib suffix
                name = formatQtJambiName(name, debug, dsoVersion);
            } else if(type.equals(TYPE_DSO) || type.equals(TYPE_SYSTEM)) {
                // name as-is
                name = formatQtName(name, false, "");
            } else if(type.equals(TYPE_UNVERSIONED_PLUGIN)) {
                // MacOSX: uses *.dylib and _debug any suffix
                name = formatUnversionedPluginName(name, debug);
            }
        }

        if(!load.equals(LOAD_YES) && !load.equals(LOAD_NEVER) && !load.equals(LOAD_DEFAULT))
            load = LOAD_DEFAULT;
    }

    public String absoluteSourcePath() {
        return getRootPath() + "/" + getSubdir() + "/" + getName();
    }

    public String relativePath() {
        return getSubdir() + "/" + getName();
    }

    public static String formatPluginName(String name, boolean kdephonon, boolean debug, String versionString) {
        if(versionString == null)
            versionString = "";		// FIXME we expect to always have a version?
        if(debug) {
            switch(OSInfo.os()) {
            case Windows:
                return name + "d" + versionString + ".dll";
            case MacOS:
                return "lib" + name + "_debug.dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return formatLinuxPluginName(name, kdephonon);
            }
        } else {
            switch(OSInfo.os()) {
            case Windows:
                return name + versionString + ".dll";
            case MacOS:
                return "lib" + name + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return formatLinuxPluginName(name, kdephonon);
            }
        }
        throw new BuildException("unhandled case...");
    }

    private static String formatLinuxPluginName(String name, boolean kdephonon) {
        String library = null;
        if(kdephonon == true) {
            library = name + ".so";
        } else {
            library = "lib" + name + ".so";
        }
        return library;
    }

    public static String formatQtName(String name, boolean debug, String versionString) {
        String tmpVersionString = (versionString != null) ? versionString : "";
        String tmpDotVersionString = (versionString != null) ? "." + versionString : "";
        if(debug) {
            String tmpDebugSuffix = "_" + "debug";
            switch(OSInfo.os()) {
            case Windows:
                return name + "d" + tmpVersionString + ".dll";
            case MacOS:
                return "lib" + name + tmpDebugSuffix + tmpDotVersionString + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so" + tmpDotVersionString;
            }
        } else {
            switch(OSInfo.os()) {
            case Windows:
                return name + tmpVersionString + ".dll";
            case MacOS:
                return "lib" + name + tmpDotVersionString + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so" + tmpDotVersionString;
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatUnversionedPluginName(String name, boolean debug) {
        if(debug) {
            String tmpDebugSuffix = "_" + "debug";
            switch(OSInfo.os()) {
            case Windows:
                return name + "d.dll";
            case MacOS:
                return "lib" + name + tmpDebugSuffix + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so";
            }
        } else {
            switch(OSInfo.os()) {
            case Windows:
                return name + ".dll";
            case MacOS:
                return "lib" + name + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatQtJambiJniName(String name, boolean debug, String versionString) {
        String tmpVersionString = (versionString != null) ? versionString : "";
        String tmpDotVersionString = (versionString != null) ? "." + versionString : "";
        if(debug) {
            String tmpDebugSuffix = "_" + "debuglib";
            switch(OSInfo.os()) {
            case Windows:
                return name + tmpVersionString + tmpDebugSuffix + ".dll";
            case MacOS:
                return "lib" + name + tmpDebugSuffix + tmpDotVersionString + ".jnilib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + tmpDebugSuffix + ".so" + tmpDotVersionString;
            }
        } else {
            switch(OSInfo.os()) {
            case Windows:
                return name + tmpVersionString + ".dll";
            case MacOS:
                return "lib" + name + tmpDotVersionString + ".jnilib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so" + tmpDotVersionString;
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatQtJambiName(String name, boolean debug, String versionString) {
        String tmpVersionString = (versionString != null) ? versionString : "";
        String tmpDotVersionString = (versionString != null) ? "." + versionString : "";
        if(debug) {
            String tmpDebugSuffix = "_" + "debuglib";
            switch(OSInfo.os()) {
            case Windows:
                return name + tmpVersionString + tmpDebugSuffix + ".dll";
            case MacOS:
                return "lib" + name + tmpDebugSuffix + tmpDotVersionString + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + tmpDebugSuffix + ".so" + tmpDotVersionString;
            }
        } else {
            switch(OSInfo.os()) {
            case Windows:
                return name + tmpVersionString + ".dll";
            case MacOS:
                return "lib" + name + tmpDotVersionString + ".dylib";
            case Solaris:
            case Linux:
            case FreeBSD:
                return "lib" + name + ".so" + tmpDotVersionString;
            }
        }
        throw new BuildException("unhandled case...");
    }


    public static String formatQtJambiPluginName(String name, boolean debug, String versionString) {
        String tmpVersionString = (versionString != null) ? versionString : "";
        String tmpDotVersionString = (versionString != null) ? "." + versionString : "";
         if(debug) {
            String tmpDebugSuffix = "_" + "debuglib";
             switch(OSInfo.os()) {
             case Windows:
                return name + tmpVersionString + tmpDebugSuffix + ".dll";
             case MacOS:
                return "lib" + name + tmpDebugSuffix + tmpDotVersionString + ".dylib";
             case Solaris:
             case Linux:
             case FreeBSD:
                return "lib" + name + tmpDebugSuffix + ".so" + tmpDotVersionString;
             }
         } else {
             switch(OSInfo.os()) {
             case Windows:
                 return name + tmpVersionString + ".dll";
             case MacOS:
                return "lib" + name + tmpDotVersionString + ".dylib";
             case Solaris:
             case Linux:
             case FreeBSD:
                return "lib" + name + ".so" + tmpDotVersionString;
             }
         }
         throw new BuildException("unhandled case...");
    }
}
