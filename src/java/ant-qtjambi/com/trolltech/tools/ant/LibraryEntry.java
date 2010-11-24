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
import java.io.File;
import com.trolltech.qt.internal.*;

// NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class LibraryEntry extends Task {

    public static final String TYPE_DEFAULT            = "user";
    public static final int VERSION_DEFAULT            = 4;

    public static final String TYPE_PLUGIN             = "plugin";
    public static final String TYPE_QT                 = "qt";
    public static final String TYPE_QTJAMBI            = "qtjambi";
    public static final String TYPE_UNVERSIONED_PLUGIN = "unversioned-plugin";

    public static final String LOAD_DEFAULT            = "default";
    public static final String LOAD_YES                = "yes";
    public static final String LOAD_NEVER              = "never";

    public static final String SUBDIR_DEFAULT          = "";
    
    /**
     *  set to specify where the plugin should be saved.
     *  Used to reduce redundancy of build.xml.
     *  TODO:
     *  Other variables could use same kind of solutions, I think. 
     *  Whole path system needs to be rewritten to correspond 
     *  new libdir, includedir, plugindir properties.  
     */
    public String output_directory        = "";

    private String type = TYPE_DEFAULT;
    private int version = VERSION_DEFAULT;
    private String name;
    private File rootpath;
    private boolean kdephonon = false;
    private String subdir = SUBDIR_DEFAULT;
    private String load = LOAD_DEFAULT;
    private boolean included = true;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    public File getRootpath() {
        return rootpath;
    }

    public void setRootpath(File rootpath) {
        this.rootpath = rootpath;
    }

    public String getSubdir() {
        return subdir;
    }

    public void setSubdir(String subdir) {
        this.subdir = subdir;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public void setIf(boolean included) {
        this.included = included;
    }

    public boolean isIncluded() {
        return included;
    }

    @Override
    public void execute() throws BuildException {
        if (name == null || name.length() == 0)
            throw new BuildException("Required attribute 'name' missing");

        PropertyHelper h = PropertyHelper.getPropertyHelper(getProject());

        boolean debug = "debug".equals(h.getProperty((String) null, InitializeTask.CONFIGURATION));

        // Fix name
        if (type.equals(TYPE_PLUGIN)) {
        	name = formatPluginName(name, this.kdephonon, debug);
        } else if (type.equals(TYPE_QT)){
        	name = formatQtName(name, debug, version);
        	//qt libraries are stored in "lib"
        	// "/" is needed in the end
        	output_directory = "lib/";
        } else if (type.equals(TYPE_QTJAMBI)) {
        	name = formatQtJambiName(name, debug);
        	output_directory = "lib/";
        } else if (type.equals(TYPE_UNVERSIONED_PLUGIN)) {
        	name = formatUnversionedPluginName(name, debug);
        }

        if (!load.equals(LOAD_YES) && !load.equals(LOAD_NEVER) && !load.equals(LOAD_DEFAULT))
            load = LOAD_DEFAULT;
    }

    public String absoluteSourcePath() {
        return rootpath + "/" + subdir + "/" + name;
    }

    public String relativePath() {
        return subdir + "/" + name;
    }

    public static String formatPluginName(String name, boolean kdephonon, boolean debug) {
        if (debug) {
            switch (OSInfo.os()) {
            case Windows: return name + "d4.dll";
            case MacOS: return "lib" + name + "_debug.dylib";
            case Solaris:
            case Linux: 
            	return formatLinuxPluginName(name, kdephonon);
            }
        } else {
            switch (OSInfo.os()) {
            case Windows: return name + "4.dll";
            case MacOS: return "lib" + name + ".dylib";
            case Solaris:
            case Linux: 
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

    public static String formatQtName(String name, boolean debug) {
        return formatQtName(name, debug, 4);
    }

    public static String formatQtName(String name, boolean debug, int version) {
        if (debug) {
            switch (OSInfo.os()) {
            case Windows: return name + "d" + version + ".dll";
            case MacOS: return "lib" + name + "_debug." + version + ".dylib";
            case Solaris:
            case Linux: return "lib" + name + ".so." + version;
            }
        } else {
            switch (OSInfo.os()) {
            case Windows: return name + version + ".dll";
            case MacOS: return "lib" + name + "." + version + ".dylib";
            case Solaris:
            case Linux: return "lib" + name + ".so." + version;
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatUnversionedPluginName(String name, boolean debug) {
        if (debug) {
            switch (OSInfo.os()) {
            case Windows: return name + "d.dll";
            case MacOS: return "lib" + name + "_debug.dylib";
            case Solaris:
            case Linux: return "lib" + name + ".so";
            }
        } else {
            switch (OSInfo.os()) {
            case Windows: return name + ".dll";
            case MacOS: return "lib" + name + ".dylib";
            case Solaris:
            case Linux: return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatQtJambiName(String name, boolean debug) {
        if (debug)  {
            switch (OSInfo.os()) {
            case Windows: return name + "_debuglib.dll";
            case MacOS: return "lib" + name + "_debuglib.jnilib";
            case Solaris:
            case Linux: return "lib" + name + "_debuglib.so";
            }
        } else {
            switch (OSInfo.os()) {
            case Windows: return name + ".dll";
            case MacOS: return "lib" + name + ".jnilib";
            case Solaris:
            case Linux: return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }

}
