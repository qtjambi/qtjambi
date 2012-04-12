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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.PropertyHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.trolltech.qt.osinfo.OSInfo;

import com.trolltech.tools.ant.FindCompiler.Compiler;

// NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class InitializeTask extends Task {

    private int verboseLevel;
    private PropertyHelper propertyHelper;
    private String configuration;
    private boolean debug;
    private boolean alreadyRun;

    private String qtVersion;
    private int qtMajorVersion;
    private int qtMinorVersion;
    private int qtPatchlevelVersion;
    private String qtVersionSource = "";
    private String versionSuffix;		// beta4

    public int getVerboseLevel() {
        return verboseLevel;
    }

    public void setVerbose(String verboseLevelString) {
		if("true".compareToIgnoreCase(verboseLevelString) == 0) {
			verboseLevel = 1;
			return;
		} if("false".compareToIgnoreCase(verboseLevelString) == 0) {
			verboseLevel = 0;
			return;
		}

		Integer i = Integer.valueOf(verboseLevelString);
        this.verboseLevel = i.intValue();
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void execute() throws BuildException {
        // we should only run this once per ANT invocation
        if(alreadyRun)
            return;

        propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        String sep = (String) propertyHelper.getProperty("sep");	// ANT 1.7.x
        if(sep == null) {
            sep = File.separator;
            propertyHelper.setNewProperty((String) null, "sep", sep);
            if(verboseLevel > 0)
                System.out.println("sep is " + sep + " (auto-detect)");
        } else {
            if(verboseLevel > 0)
                System.out.println("sep is " + sep);
        }

        String psep = (String) propertyHelper.getProperty("psep");	// ANT 1.7.x
        if(psep == null) {
            psep = File.pathSeparator;
            propertyHelper.setNewProperty((String) null, "psep", psep);
            if(verboseLevel > 0)
                System.out.println("psep is " + psep + " (auto-detect)");
        } else {
            if(verboseLevel > 0)
                System.out.println("psep is " + psep);
        }

	    final String[] emitA = {
            Constants.DIRECTORY
        };
        for(String emit : emitA) {
            String value = (String) propertyHelper.getProperty(emit);	// ANT 1.7.x
            if(value == null) {
                if(verboseLevel > 0)
                    System.out.println(emit + ": <notset>");
            } else {
                if(verboseLevel > 0)
                    System.out.println(emit + ": " + (value.length() == 0 ? "<empty-string>" : value));
            }
        }

		FindCompiler finder = new FindCompiler(getProject(), propertyHelper);
        String osname = finder.decideOSName();
        propertyHelper.setNewProperty((String) null, Constants.OSNAME, osname);
        if(verboseLevel > 0)
            System.out.println(Constants.OSNAME + " is " + osname);

        String s;

        s = null;
        if(OSInfo.isLinux())
            s = OSInfo.K_LINUX;
        else if(OSInfo.isWindows())
            s = OSInfo.K_WINDOWS;
        else if(OSInfo.isMacOS())
            s = OSInfo.K_MACOSX;
        else if(OSInfo.isFreeBSD())
            s = OSInfo.K_FREEBSD;
        else if(OSInfo.isSolaris())
            s = OSInfo.K_SUNOS;
        if(s != null)
            propertyHelper.setNewProperty((String) null, Constants.OSPLATFORM, s);

        s = null;
        // FIXME incorrect for windows x86/x64, sunos
        if(osname.endsWith("64"))
            s = "x86_64";
        else
            s = "i386";
        if(s != null)
            propertyHelper.setNewProperty((String) null, Constants.OSCPU, s);


        String javaHomeTarget = decideJavaHomeTarget();
        if(javaHomeTarget == null)
            throw new BuildException("Unable to determine JAVA_HOME_TARGET, setup environment variable JAVA_HOME (or JAVA_HOME_TARGET) or edit buildpath.properties");

        String javaOsarchTarget = decideJavaOsarchTarget();
        if(javaOsarchTarget == null) {
            if(OSInfo.isMacOS() == false)  // On MacOSX there is no sub-dir inside the JDK include directory that contains jni.h
                throw new BuildException("Unable to determine JAVA_OSARCH_TARGET, setup environment variable JAVA_OSARCH_TARGET or edit buildpath.properties");
        }

        String configuration = decideConfiguration();
        propertyHelper.setNewProperty((String) null, Constants.CONFIGURATION, configuration);
        s = null;
        if(Constants.CONFIG_RELEASE.equals(configuration))
            s = "";	// empty
        else if(Constants.CONFIG_DEBUG.equals(configuration))
            s = "-debug";
        else
            s = "-test";
        if(s != null)
            propertyHelper.setNewProperty((String) null, Constants.CONFIGURATION_DASH, s);
        s = null;
        if(Constants.CONFIG_RELEASE.equals(configuration))
            s = "";	// empty
        else if(Constants.CONFIG_DEBUG.equals(configuration))
            s = ".debug";
        else
            s = ".test";
        if(s != null)
            propertyHelper.setNewProperty((String) null, Constants.CONFIGURATION_OSGI, s);

        {
            String sourceValue = null;
            String qmakeTargetDefault = (String) propertyHelper.getProperty(Constants.QMAKE_TARGET_DEFAULT);   // ANT 1.7.x
            if(qmakeTargetDefault == null) {
                // We only need to override the default when the Qt SDK is debug_and_release but
                //  we are only building the project for one kind.
//              if(CONFIG_RELEASE.equals(configuration))
//                  qmakeTargetDefault = configuration;
//              else if(CONFIG_DEBUG.equals(configuration))
//                  qmakeTargetDefault = configuration;
//              else if(CONFIG_DEBUG_AND_RELEASE.equals(configuration))
//                  qmakeTargetDefault = "all";
//              else
                    qmakeTargetDefault = "all";
                // FIXME: We want ${qtjambi.configuration} to set from QTDIR build kind *.prl data
//                sourceValue = " (set from ${qtjambi.configuration})";
            }
            mySetProperty(propertyHelper, -1, Constants.QMAKE_TARGET_DEFAULT, sourceValue, qmakeTargetDefault, false);  // report value
        }


        versionSuffix = (String) propertyHelper.getProperty(Constants.SUFFIX_VERSION);	// ANT 1.7.x
        mySetProperty(propertyHelper, -1, Constants.SUFFIX_VERSION, null, null, false);  // report

 
        if(OSInfo.isMacOS())
            mySetProperty(propertyHelper, 0, Constants.QTJAMBI_CONFIG_ISMACOSX, " (set by init)", "true", false);

        alreadyRun = true;
    }

    private String decideJavaHomeTarget() {
        String sourceValue = null;
        String s = (String) propertyHelper.getProperty((String) null, Constants.JAVA_HOME_TARGET);
        if(s == null) {
            s = System.getenv("JAVA_HOME_TARGET");
            if(s != null)
                sourceValue = " (from envvar:JAVA_HOME_TARGET)";
        }
        if(s == null) {
            s = System.getenv("JAVA_HOME");
            if(s != null)
                sourceValue = " (from envvar:JAVA_HOME)";
        }
        String result = s;
        mySetProperty(propertyHelper, -1, Constants.JAVA_HOME_TARGET, sourceValue, result, false);
        return result;
    }

    private String decideJavaOsarchTarget() {
        String sourceValue = null;;
        String s = (String) propertyHelper.getProperty((String) null, Constants.JAVA_OSARCH_TARGET);

        if(s == null) {
            s = System.getenv("JAVA_OSARCH_TARGET");
            if(s != null)
                sourceValue = " (from envvar:JAVA_OSARCH_TARGET)";
        }

        if(s == null) {    // auto-detect using what we find
            // This is based on a token observation that the include direcory
            //  only had one sub-directory (this is needed for jni_md.h).
            File includeDir = new File((String)propertyHelper.getProperty((String) null, Constants.JAVA_HOME_TARGET), "include");
            File found = null;
            int foundCount = 0;

            if(includeDir.exists()) {
                File[] listFiles = includeDir.listFiles();
                for(File f : listFiles) {
                    if(f.isDirectory()) {
                        foundCount++;
                        found = f;
                    }
                }
            }

            if(foundCount == 1) {
                s = found.getName();
                sourceValue = " (auto-detected)";
            }
        }

        String result = s;
        mySetProperty(propertyHelper, -1, Constants.JAVA_OSARCH_TARGET, sourceValue, result, false);
        return result;
    }

    /**
     * Decides whether to use debug or release configuration
     *
     * @return string "debug" or "release" according config resolution
     */
    private String decideConfiguration() {
        String result = null;

        debug = "debug".equals(configuration);
        result = debug ? "debug" : "release";

        if(verboseLevel > 0) System.out.println(Constants.CONFIGURATION + ": " + result);
        return result;
    }

    private String mySetProperty(PropertyHelper propertyHelper, int verboseMode, String attrName, String sourceValue, String newValue, boolean forceNewValue) throws BuildException {
        String currentValue = (String) propertyHelper.getProperty((String) null, attrName);
        if(newValue != null) {
            if(currentValue != null) {
                if(currentValue.equals(newValue))
                    sourceValue = " (already set to same value)";
                else
                    sourceValue = " (already set; detected as: " + newValue + ")";
                // Don't error if we don't have to i.e. the two values are the same
                if(forceNewValue && newValue.equals(currentValue) == false)
                    throw new BuildException("Unable to overwrite property " + attrName + " with value " + newValue + " (current value is: " + currentValue + ")");
            } else {
                if(forceNewValue)
                    propertyHelper.setProperty((String) null, attrName, newValue, false);
                else
                    propertyHelper.setNewProperty((String) null, attrName, newValue);
                currentValue = newValue;
            }
        } else {
            if(currentValue != null)
                sourceValue = null;  // we don't use newValue in any way, and currentValue exists
        }

        if(sourceValue == null)
            sourceValue = "";

        if((verboseMode == -1 && verboseLevel > 0) || (verboseMode > 0)) {
            String prettyCurrentValue = currentValue;
            if(prettyCurrentValue == null)
                prettyCurrentValue = "<notset>";
            else if(prettyCurrentValue.length() == 0)
                prettyCurrentValue = "<empty-string>";
            System.out.println(attrName + ": " + prettyCurrentValue + sourceValue);
        }

        return currentValue;
    }
}
