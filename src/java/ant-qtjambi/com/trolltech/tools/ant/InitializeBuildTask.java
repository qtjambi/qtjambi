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
public class InitializeBuildTask extends Task {

    private boolean verbose;
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

    private String pathVersionProperties            = "version.properties";
    private String pathVersionPropertiesTemplate    = "version.properties.template";

    private String[] generatorPreProcStageOneA;
    private String[] generatorPreProcStageTwoA;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        this.verboseLevel = (verbose == false) ? 0 : 1;
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
            if(verbose)
                System.out.println("sep is " + sep + " (auto-detect)");
        } else {
            if(verbose)
                System.out.println("sep is " + sep);
        }

        String psep = (String) propertyHelper.getProperty("psep");	// ANT 1.7.x
        if(psep == null) {
            psep = File.pathSeparator;
            propertyHelper.setNewProperty((String) null, "psep", psep);
            if(verbose)
                System.out.println("psep is " + psep + " (auto-detect)");
        } else {
            if(verbose)
                System.out.println("psep is " + psep);
        }

        final String[] emitA = {
            Constants.DIRECTORY,
            Constants.BINDIR,
            Constants.LIBDIR,
            Constants.INCLUDEDIR,
            Constants.PLUGINSDIR,
            Constants.QTJAMBI_PHONON_INCLUDEDIR,
            Constants.QTJAMBI_PHONON_LIBDIR,
            Constants.QTJAMBI_PHONON_PLUGINSDIR
        };
        for(String emit : emitA) {
            String value = (String) propertyHelper.getProperty(emit);	// ANT 1.7.x
            if(value == null) {
                if(verbose)
                    System.out.println(emit + ": <notset>");
            } else {
                if(verbose)
                    System.out.println(emit + ": " + (value.length() == 0 ? "<empty-string>" : value));
            }
        }

        FindCompiler finder = new FindCompiler(getProject(), propertyHelper);

        String osname = finder.decideOSName();
        propertyHelper.setNewProperty((String) null, Constants.OSNAME, osname);
        if(verbose)
            System.out.println(Constants.OSNAME + " is " + osname);

        Compiler compiler = finder.decideCompiler();
        propertyHelper.setNewProperty((String) null, Constants.COMPILER, compiler.toString());
        if(verbose)
            System.out.println(Constants.COMPILER + " is " + compiler.toString());

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

        finder.checkCompilerDetails();
        //finder.checkCompilerBits();

        mySetProperty(propertyHelper, -1, Constants.EXEC_STRIP, null, null, false);  // report value

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
//              if(Constants.CONFIG_RELEASE.equals(configuration))
//                  qmakeTargetDefault = configuration;
//              else if(Constants.CONFIG_DEBUG.equals(configuration))
//                  qmakeTargetDefault = configuration;
//              else if(Constants.CONFIG_DEBUG_AND_RELEASE.equals(configuration))
//                  qmakeTargetDefault = "all";
//              else
                    qmakeTargetDefault = "all";
                // FIXME: We want ${qtjambi.configuration} to set from QTDIR build kind *.prl data
//                sourceValue = " (set from ${qtjambi.configuration})";
            }
            mySetProperty(propertyHelper, -1, Constants.QMAKE_TARGET_DEFAULT, sourceValue, qmakeTargetDefault, false);  // report value
        }

        if(!decideQtVersion())
            throw new BuildException("Unable to determine Qt version, try editing: " + pathVersionPropertiesTemplate);
        s = String.valueOf(qtVersion);
        if(verbose)
            System.out.println(Constants.QT_VERSION + " is " + s + qtVersionSource);
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION, s);
        propertyHelper.setNewProperty((String) null, Constants.VERSION, s); // this won't overwrite existing value

        s = String.valueOf(qtMajorVersion);
        if(verbose)
            System.out.println(Constants.QT_VERSION_MAJOR + " is " + s);
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION_MAJOR, s);
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION_MAJOR_NEXT, String.valueOf(qtMajorVersion + 1));
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION_MINOR,      String.valueOf(qtMinorVersion));
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION_MINOR_NEXT, String.valueOf(qtMinorVersion + 1));
        propertyHelper.setNewProperty((String) null, Constants.QT_VERSION_PATCHLEVEL, String.valueOf(qtPatchlevelVersion));


        versionSuffix = (String) propertyHelper.getProperty(Constants.SUFFIX_VERSION);	// ANT 1.7.x
        mySetProperty(propertyHelper, -1, Constants.SUFFIX_VERSION, null, null, false);  // report

        String canonVersionSuffix;
        if(versionSuffix != null)
            canonVersionSuffix = versionSuffix;
        else
            canonVersionSuffix = "";
        String bundleVersionMode = (String) propertyHelper.getProperty(Constants.BUNDLE_VERSION_MODE);	// ANT 1.7.x
        if(bundleVersionMode != null) {
            if(bundleVersionMode.equals("auto-suffix-date")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                s = qtVersion + "." + sdf.format(new Date()) + canonVersionSuffix;
            }
        } else {
            s = qtVersion + canonVersionSuffix;
        }
        mySetProperty(propertyHelper, -1, Constants.BUNDLE_VERSION, null, s, false);

        if(OSInfo.isMacOS())
            mySetProperty(propertyHelper, 0, Constants.QTJAMBI_CONFIG_ISMACOSX, " (set by init)", "true", false);


        String sonameVersionMajor = Constants.DEFAULT_QTJAMBI_SONAME_VERSION_MAJOR;
        String sonameSource = " (set by init)";
        if(OSInfo.isWindows()) {   // skip setting it by default, only do for Linux/MacOSX/Unix set to soname major
            sonameVersionMajor = "";  // got to set it empty otherwise we get unsubstitued ${foo} are value
            sonameSource = " (set blank by init)";
        }
        mySetProperty(propertyHelper, -1, Constants.QTJAMBI_SONAME_VERSION_MAJOR, sonameSource, sonameVersionMajor, false);

        String cachekeyVersion = (String) propertyHelper.getProperty(Constants.CACHEKEY);	// ANT 1.7.x
        String cachekeyVersionSource = " (already set)";
        if(cachekeyVersion == null) {	// auto-configure
            cachekeyVersionSource = " (set by init)";
            // ${qtjambi.compiler}${qtjambi.configuration.dash}-${DSTAMP}-${TSTAMP}
            cachekeyVersion = propertyHelper.replaceProperties(null, "${qtjambi.compiler}${qtjambi.configuration.dash}-${DSTAMP}-${TSTAMP}", null);
        }
        mySetProperty(propertyHelper, -1, Constants.CACHEKEY, cachekeyVersionSource, cachekeyVersion, false);


        if(!decideGeneratorPreProc())
            throw new BuildException("Unable to determine generator pre-processor settings");
        s = Util.safeArrayToString(generatorPreProcStageOneA);
        if(verbose)
            System.out.println(Constants.GENERATOR_PREPROC_STAGE1 + " is " + ((s != null) ? s : "<unset>"));
        propertyHelper.setNewProperty((String) null, Constants.GENERATOR_PREPROC_STAGE1, Util.safeArrayJoinToString(generatorPreProcStageOneA, ","));
        s = Util.safeArrayToString(generatorPreProcStageTwoA);
        if(verbose)
            System.out.println(Constants.GENERATOR_PREPROC_STAGE2 + " is " + ((s != null) ? s : "<unset>"));
        propertyHelper.setNewProperty((String) null, Constants.GENERATOR_PREPROC_STAGE2, Util.safeArrayJoinToString(generatorPreProcStageTwoA, ","));


        Object qtjambiQtLibdirObject = propertyHelper.getProperty((String) null, Constants.LIBDIR);
        if(qtjambiQtLibdirObject != null) {
            String qtjambiQtLibdir = (String) qtjambiQtLibdirObject.toString();
            String sourceValue = null;
//            s = (String) propertyHelper.getProperty(Constants.QTJAMBI_MACOSX_QTMENUNIB_DIR);
//            if(s == null) {
                s = doesQtLibExistDir(qtjambiQtLibdir, "Resources/qt_menu.nib");
                if(s == null)
                    s = doesQtLibExistDir(qtjambiQtLibdir, "qt_menu.nib");
                //if(s == null)
                //    s= = doesQtLibExistDir(qtjambiQtLibdir, "src/gui/mac/qt_menu.nib");
                // FIXME: auto-detect, directroy from source, directory from QtSDK on MacOSX, directory from framework on MacOSX
                
                if(s != null)
                    sourceValue = " (auto-detected)";
//            }
            if(s == null) {
                if(OSInfo.isMacOS() == false)
                    sourceValue = " (expected for non-MacOSX platform)";
                else
                    sourceValue = " (WARNING you should resolve this for targetting MacOSX)";
                s = "";
            }
            mySetProperty(propertyHelper, -1, Constants.QTJAMBI_MACOSX_QTMENUNIB_DIR, sourceValue, s, false);
        }
        if(propertyHelper.getProperty(Constants.QTJAMBI_MACOSX_QTMENUNIB_DIR) == null)	// ANT 1.7.x
            propertyHelper.setProperty((String) null, Constants.QTJAMBI_MACOSX_QTMENUNIB_DIR, "", false);


        String clucene = decideCLucene();
        if("true".equals(clucene))
            propertyHelper.setNewProperty((String) null, Constants.CLUCENE, clucene);

        String core = decideCore();
        if("true".equals(core))
            propertyHelper.setNewProperty((String) null, Constants.CORE, core);

        String dbus = decideDBus();
        if("true".equals(dbus))
            propertyHelper.setNewProperty((String) null, Constants.DBUS, dbus);

        String declarative = decideDeclarative();
        if("true".equals(declarative))
            propertyHelper.setNewProperty((String) null, Constants.DECLARATIVE, declarative);

        String designer = decideDesigner();
        if("true".equals(designer))
            propertyHelper.setNewProperty((String) null, Constants.DESIGNER, designer);

        String designercomponents = decideDesignerComponents();
        if("true".equals(designercomponents))
            propertyHelper.setNewProperty((String) null, Constants.DESIGNERCOMPONENTS, designercomponents);

        String gui = decideGui();
        if("true".equals(gui))
            propertyHelper.setNewProperty((String) null, Constants.GUI, gui);

        String helptool = decideHelp();
        if("true".equals(helptool))
            propertyHelper.setNewProperty((String) null, Constants.HELP, helptool);

        String multimedia = decideMultimedia();
        if("true".equals(multimedia))
            propertyHelper.setNewProperty((String) null, Constants.MULTIMEDIA, multimedia);

        String network = decideNetwork();
        if("true".equals(network))
            propertyHelper.setNewProperty((String) null, Constants.NETWORK, network);

        String opengl = decideOpenGL();
        if("true".equals(opengl))
            propertyHelper.setNewProperty((String) null, Constants.OPENGL, opengl);

        String phonon = decidePhonon(propertyHelper);

        String script = decideScript();
        if("true".equals(script))
            propertyHelper.setNewProperty((String) null, Constants.SCRIPT, script);

        String scripttools = decideScripttools();
        if("true".equals(scripttools))
            propertyHelper.setNewProperty((String) null, Constants.SCRIPTTOOLS, scripttools);

        propertyHelper.setNewProperty((String) null, Constants.SQL, decideSql());

        propertyHelper.setNewProperty((String) null, Constants.SVG, decideSvg());

        propertyHelper.setNewProperty((String) null, Constants.TEST, decideTest());

        String webkit = decideWebkit();
        // Not sure why this is a problem "ldd libQtWebKit.so.4.7.4" has no dependency on libphonon for me,
        //  if you have headers and DSOs for WebKit then QtJambi should build the support.
        if("true".equals(webkit) && "true".equals(phonon) == false) {
            if(verbose) System.out.println("WARNING: " + Constants.WEBKIT + " is " + webkit + ", but " + Constants.PHONON + " is " + phonon);
        }
        if("true".equals(webkit))
            propertyHelper.setNewProperty((String) null, Constants.WEBKIT, webkit);

        String xml = decideXml();
        if("true".equals(xml))
            propertyHelper.setNewProperty((String) null, Constants.XML, xml);

        String xmlpatterns = decideXmlPatterns();
        if("true".equals(xmlpatterns))
            propertyHelper.setNewProperty((String) null, Constants.XMLPATTERNS, xmlpatterns);


        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS, decidePluginsAccessibleQtaccesswidgets());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_BEARER_CONNMANBEARER, decidePlugins(Constants.PLUGINS_BEARER_CONNMANBEARER, "bearer", "connmanbearer"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_BEARER_GENERICBEARER, decidePlugins(Constants.PLUGINS_BEARER_GENERICBEARER, "bearer", "genericbearer"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_BEARER_NATIVEWIFIBEARER, decidePlugins(Constants.PLUGINS_BEARER_NATIVEWIFIBEARER, "bearer", "nativewifibearer"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_BEARER_NMBEARER, decidePlugins(Constants.PLUGINS_BEARER_NMBEARER, "bearer", "nmbearer"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_CODECS_CNCODECS, decidePluginsCodecs(Constants.PLUGINS_CODECS_CNCODECS, "cncodecs"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_CODECS_JPCODECS, decidePluginsCodecs(Constants.PLUGINS_CODECS_JPCODECS, "jpcodecs"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_CODECS_KRCODECS, decidePluginsCodecs(Constants.PLUGINS_CODECS_KRCODECS, "krcodecs"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_CODECS_TWCODECS, decidePluginsCodecs(Constants.PLUGINS_CODECS_TWCODECS, "twcodecs"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_GRAPHICSSYSTEMS_GLGRAPHICSSYSTEM, decidePlugins(Constants.PLUGINS_GRAPHICSSYSTEMS_GLGRAPHICSSYSTEM, "graphicssystems", "glgraphicssystem"));
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_GRAPHICSSYSTEMS_TRACEGRAPHICSSYSTEM, decidePlugins(Constants.PLUGINS_GRAPHICSSYSTEMS_TRACEGRAPHICSSYSTEM, "graphicssystems", "tracegraphicssystem"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_ICONENGINES_SVGICON, decidePluginsIconenginesSvgicon());

        // These are only detecting if the plugins exist for these modules,
        // lack of a plugin does not necessarily mean Qt doesn't have support
        // since the implementation might be statically linked in.
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_GIF,  decidePluginsImageformatsGif());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_ICO,  decidePluginsImageformatsIco());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_JPEG, decidePluginsImageformatsJpeg());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_MNG,  decidePluginsImageformatsMng());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_PNG,  decidePluginsImageformatsPng());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_SVG,  decidePluginsImageformatsSvg());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_IMAGEFORMATS_TIFF, decidePluginsImageformatsTiff());

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_INPUTMETHODS_IMSW_MULTI, decidePlugins(Constants.PLUGINS_INPUTMETHODS_IMSW_MULTI, "inputmethods", "imsw-multi"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_QMLTOOLING_QMLDBG_TCP, decidePlugins(Constants.PLUGINS_QMLTOOLING_QMLDBG_TCP, "qmltooling", "mldbg_tcp"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SCRIPT_QTSCRIPTDBUS, decidePlugins(Constants.PLUGINS_SCRIPT_QTSCRIPTDBUS, "script", "tscriptdbus"));

        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLITE,     decidePluginsSqldriversSqlite());
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLITE2,    decidePluginsSqldriversSqlite2());
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLMYSQL,   decidePluginsSqldriversSqlmysql());
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLODBC,    decidePluginsSqldriversSqlodbc());
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLPSQL,    decidePluginsSqldriversSqlpsql());
        propertyHelper.setNewProperty((String) null, Constants.PLUGINS_SQLDRIVERS_SQLTDS,     decidePluginsSqldriversSqltds());

        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBSTDC___6,     decideQtBinDso(Constants.PACKAGING_DSO_LIBSTDC___6,     "libstdc++-6"));
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBGCC_S_DW2_1,  decideQtBinDso(Constants.PACKAGING_DSO_LIBGCC_S_DW2_1,  "libgcc_s_dw2-1"));
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBGCC_S_SJLJ_1, decideQtBinDso(Constants.PACKAGING_DSO_LIBGCC_S_SJLJ_1, "libgcc_s_sjlj-1"));
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_MINGWM10,        decideQtBinDso(Constants.PACKAGING_DSO_MINGWM10,        "mingwm10"));


        String packagingDsoLibeay32 = decideQtLibDso(Constants.PACKAGING_DSO_LIBEAY32, "libeay32", null);
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBEAY32, packagingDsoLibeay32);

        String packagingDsoLibssl32 = decideQtLibDso(Constants.PACKAGING_DSO_LIBSSL32, "libssl32", null, false);
        String packagingDsoSsleay32 = decideQtLibDso(Constants.PACKAGING_DSO_SSLEAY32, "ssleay32", null, false);
        // When building QtJambi against the offical Nokia Qt SDK they appear to provide duplicate
        // DLLs for the two naming variants libssl32.dll ssleay32.dll so we need to resolve this and
        // omit one.
        String packagingDsoLibssl32Message = "";
        String packagingDsoSsleay32Message = "";
        // "true" or a path, also means true.  Only "false" means false.
        if(("false".equals(packagingDsoLibssl32) == false && packagingDsoLibssl32 != null) && ("false".equals(packagingDsoSsleay32) == false && packagingDsoSsleay32 != null)) {
            // FIXME: Compare the files are actually the same
            if(compiler == Compiler.GCC || compiler == Compiler.OldGCC || compiler == Compiler.MinGW || compiler == Compiler.MinGW_W64) {
                packagingDsoSsleay32Message = " (was " + packagingDsoSsleay32 + "; auto-inhibited)";
                packagingDsoSsleay32 = "false";    // favour libssl32.dll
            } else {
                packagingDsoLibssl32Message = " (was " + packagingDsoLibssl32 + "; auto-inhibited)";
                packagingDsoLibssl32 = "false";    // favour ssleay32.dll
            }
        }
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBSSL32, packagingDsoLibssl32);
        if(verbose && (packagingDsoLibssl32Message.length() > 0 || ("false".equals(packagingDsoLibssl32) == false) && packagingDsoLibssl32 != null))
            System.out.println(Constants.PACKAGING_DSO_LIBSSL32 + ": " + packagingDsoLibssl32 + packagingDsoLibssl32Message);

        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_SSLEAY32, packagingDsoSsleay32);
        if(verbose && (packagingDsoSsleay32Message.length() > 0 || ("false".equals(packagingDsoSsleay32) == false) && packagingDsoSsleay32 != null))
            System.out.println(Constants.PACKAGING_DSO_SSLEAY32 + ": " + packagingDsoSsleay32 + packagingDsoSsleay32Message);

        String QTDIR = System.getenv("QTDIR");   // used here

        if(OSInfo.isWindows()) {
            String packagingDsoZlib1 = decideQtLibDso(Constants.PACKAGING_DSO_ZLIB1, "zlib1", null);
            propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_ZLIB1, packagingDsoZlib1);
        } else {
            // If the lib directory contains "libz.so.1" or "libssl.so" or "libcrypto.so.1.0.0"
            String sourceValue = null;
            String packagingDsoLibssl = decideQtLibDso(Constants.PACKAGING_DSO_LIBSSL, "ssl", new String[] { null, "1", "0" }, false);
            if(packagingDsoLibssl != null && packagingDsoLibssl.startsWith(QTDIR) == false) {
                sourceValue = " (detected: " + packagingDsoLibssl + "; but inhibited as not inside QTDIR)";
                packagingDsoLibssl = null;
            }
            mySetProperty(propertyHelper, -1, Constants.PACKAGING_DSO_LIBSSL, sourceValue, packagingDsoLibssl, false);

            // FIXME: Implement file globs and reverse sort
            sourceValue = null;
            String packagingDsoLibcrypto = decideQtLibDso(Constants.PACKAGING_DSO_LIBCRYPTO, "crypto", new String[] { "1.0.0h", "1.0.0g", "1.0.0", "0.0.0", null, "10" }, false);
            if(packagingDsoLibcrypto != null && packagingDsoLibcrypto.startsWith(QTDIR) == false) {
                sourceValue = " (detected: " + packagingDsoLibcrypto + "; but inhibited as not inside QTDIR)";
                packagingDsoLibcrypto = null;
            }
            mySetProperty(propertyHelper, -1, Constants.PACKAGING_DSO_LIBCRYPTO, sourceValue, packagingDsoLibcrypto, false);

            sourceValue = null;
            String packagingDsoLibz = decideQtLibDso(Constants.PACKAGING_DSO_LIBZ, "z", new String[] { "1", null }, false);
            if(packagingDsoLibz != null && packagingDsoLibz.startsWith(QTDIR) == false) {
                sourceValue = " (detected: " + packagingDsoLibz + "; but inhibited as not inside QTDIR)";
                packagingDsoLibz = null;
            }
            mySetProperty(propertyHelper, -1, Constants.PACKAGING_DSO_LIBZ, sourceValue, packagingDsoLibz, false);
        }

        // FIXME: On Macosx when we build and have qtjambi.dbus==true we should WARN when we can not locate libdbus-1.*.dylib
        // FIXME: On Macosx we should also search /usr/local/lib
        String packagingDsoLibdbus = decideQtLibDso(Constants.PACKAGING_DSO_LIBDBUS, "dbus-1", new String[] { "3", "2", null }, null);
        propertyHelper.setNewProperty((String) null, Constants.PACKAGING_DSO_LIBDBUS, packagingDsoLibdbus);

        // Other build information sanity testing and warning

        System.out.println("QTDIR is set: " + ((QTDIR != null) ? QTDIR : "<notset>"));

        String JAMBIDIR = System.getenv("JAMBIDIR");
        if(JAMBIDIR != null)
            System.out.println("JAMBIDIR is set: " + JAMBIDIR);

        if(OSInfo.isLinux()) {    // Check we have libQtCore.so.4 in one of the paths in LD_LIBRARY_PATH
            String LD_LIBRARY_PATH = System.getenv("LD_LIBRARY_PATH");
            System.out.println("LD_LIBRARY_PATH is set: " + ((LD_LIBRARY_PATH == null) ? "<notset>" : LD_LIBRARY_PATH));
            if(LD_LIBRARY_PATH != null) {
                String[] sA = LD_LIBRARY_PATH.split(File.pathSeparator);  // should regex escape it
                String filename = LibraryEntry.formatQtName("QtCore", debug, String.valueOf(qtMajorVersion));
                int found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    if(testDir.isDirectory() == false)
                        System.out.println("WARNING:    LD_LIBRARY_PATH directory does not exit: " + element);
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println("  FOUND:    LD_LIBRARY_PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
                if(found == 0)  // Maybe we should check to see if (QTDIR != null) before warning
                   System.out.println("WARNING: LD_LIBRARY_PATH environment variable is set, but does not contain a valid location for libQtCore.so.*; this is usually needed to allow 'generator' and 'juic' executables to run during the build");

                // FIXME: Refactor this duplicate code later (we look for !debug here but don't WARNING is we dont find it)
                filename = LibraryEntry.formatQtName("QtCore", !debug, String.valueOf(qtMajorVersion));
                found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    // we already warned about non-existing directory here
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println(" XFOUND:    LD_LIBRARY_PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
            } else {   // Maybe we should check to see if (QTDIR != null) before warning
                System.out.println("WARNING: LD_LIBRARY_PATH environment variable is not set; this is usually needed to allow 'generator' and 'juic' executables to run during the build");
            }
        }
        if(OSInfo.isWindows()) {    // Check we have QtCore4.dll in one of the paths in PATH
            String PATH = System.getenv("PATH");
            System.out.println("PATH is set: " + ((PATH == null) ? "<notset>" : PATH));
            if(PATH != null) {
                String[] sA = PATH.split(File.pathSeparator);  // should regex escape it
                String filename = LibraryEntry.formatQtName("QtCore", debug, String.valueOf(qtMajorVersion));
                int found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    if(testDir.isDirectory() == false)
                        System.out.println("WARNING:    PATH directory does not exit: " + element);
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println("  FOUND:    PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
                if(found == 0)
                   System.out.println("WARNING: PATH environment variable is set, but does not contain a valid location for QtCore*.dll; this is usually needed to allow 'generator' and 'juic' executables to run during the build");

                // FIXME: Refactor this duplicate code later (we look for !debug here but don't WARNING is we dont find it)
                filename = LibraryEntry.formatQtName("QtCore", !debug, String.valueOf(qtMajorVersion));
                found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    // we already warned about non-existing directory here
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println(" XFOUND:    PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
            } else {
                System.out.println("WARNING: PATH environment variable is not set; this is usually needed to allow 'generator' and 'juic' executables to run during the build");
            }
        }
        if(OSInfo.isMacOS()) {    // Check we have libQtCore.4.dylib in one of the paths in DYLD_LIBRARY_PATH
            String DYLD_LIBRARY_PATH = System.getenv("DYLD_LIBRARY_PATH");
            System.out.println("DYLD_LIBRARY_PATH is set: " + ((DYLD_LIBRARY_PATH == null) ? "<notset>" : DYLD_LIBRARY_PATH));
            if(DYLD_LIBRARY_PATH != null) {
                String[] sA = DYLD_LIBRARY_PATH.split(File.pathSeparator);  // should regex escape it
                String filename = LibraryEntry.formatQtName("QtCore", debug, String.valueOf(qtMajorVersion));
                int found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    if(testDir.isDirectory() == false)
                        System.out.println("WARNING:    DYLD_LIBRARY_PATH directory does not exit: " + element);
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println("  FOUND:    DYLD_LIBRARY_PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
                if(found == 0)
                   System.out.println("WARNING: DYLD_LIBRARY_PATH environment variable is set, but does not contain a valid location for libQtCore.*.dylib; this is usually needed to allow 'generator' and 'juic' executables to run during the build");

                // FIXME: Refactor this duplicate code later (we look for !debug here but don't WARNING is we dont find it)
                filename = LibraryEntry.formatQtName("QtCore", !debug, String.valueOf(qtMajorVersion));
                found = 0;
                for(String element : sA) {
                    File testDir = new File(element);
                    // we already warned about non-existing directory here
                    File testFile = new File(element, filename);
                    if(testFile.isFile()) {
                        System.out.println(" XFOUND:    DYLD_LIBRARY_PATH directory contains QtCore: " + testFile.getAbsolutePath());
                        found++;
                    }
                }
            } else {
                System.out.println("WARNING: DYLD_LIBRARY_PATH environment variable is not set; this is usually needed to allow 'generator' and 'juic' executables to run during the build");
            }
        }

        alreadyRun = true;
    }

    private boolean parseQtVersion(String versionString) {
        if(versionString == null)
            return false;

        // Remove leading, remove trailing whitespace
        versionString = Util.stripLeadingAndTrailingWhitespace(versionString);

        {
            // Check for valid character set "[0-9\.]+"
            final int len = versionString.length();
            for(int i = 0; i < len; i++) {
                char c = versionString.charAt(i);
                if((c >= '0' && c <= '9') || c == '.')
                    continue;
                return false;
            }
        }

        // Check for non-empty
        final int len = versionString.length();
        if(len == 0)
            return false;

        // Check for [0-9\.] and no double dots, no leading/trailing dots.
        if(versionString.charAt(0) == '.' || versionString.charAt(len - 1) == '.')
            return false;

        if(versionString.indexOf("..") >= 0)
            return false;

        // Split
        String[] versionParts = versionString.split("\\.");

        String tmpQtVersion = null;
        Integer tmpQtMajorVersion = null;
        Integer tmpQtMinorVersion = null;
        Integer tmpQtPatchlevelVersion = null;

        try {
            tmpQtVersion = versionString;
            if(versionParts.length < 1)
                return false;
            tmpQtMajorVersion = Integer.valueOf(versionParts[0]);
            if(versionParts.length < 2)
                return false;
            tmpQtMinorVersion = Integer.valueOf(versionParts[1]);
            if(versionParts.length < 3)
                tmpQtPatchlevelVersion = 0;
            else
                tmpQtPatchlevelVersion = Integer.valueOf(versionParts[2]);
        } catch(NumberFormatException e) {
            return false;
        }

        // Ok we happy
        qtVersion = tmpQtVersion;
        qtMajorVersion = tmpQtMajorVersion.intValue();
        qtMinorVersion = tmpQtMinorVersion.intValue();
        qtPatchlevelVersion = tmpQtPatchlevelVersion.intValue();
        return true;
    }

    private boolean decideQtVersion() {
        boolean versionFound = false;

        String tmpQtVersion = null;

        if(!versionFound) {
            tmpQtVersion = (String) propertyHelper.getProperty(Constants.QT_VERSION);	// ANT 1.7.x
            if(parseQtVersion(tmpQtVersion)) {
                versionFound = true;
                qtVersionSource = " (${" + Constants.QT_VERSION + "})";
            }
        }

        // If failure, open version.properties.template to get version
        if(!versionFound) {
            tmpQtVersion = null;
            InputStream inStream = null;
            Properties props = null;
            try {
                inStream = new FileInputStream(pathVersionPropertiesTemplate);
                props = new Properties();
                props.load(inStream);
                tmpQtVersion = (String) props.get(Constants.VERSION);
            } catch(FileNotFoundException e) {
                // Acceptable failure
                System.err.println(e.getMessage());
            } catch(IOException e) {
                throw new BuildException(e);
            } finally {
                if(inStream != null) {
                    try {
                        inStream.close();
                    } catch(IOException eat) {
                    }
                    inStream = null;
                }
            }

            if(tmpQtVersion != null) {
                if(parseQtVersion(tmpQtVersion)) {
                    versionFound = true;
                    qtVersionSource = " (" + pathVersionPropertiesTemplate + ")";
                }
            }
        }

        if(!versionFound) {
            // Run "qmake -query"
            String qmakeExe = (String) propertyHelper.getProperty("qmake.binary");	// ANT 1.7.x
            if(qmakeExe == null) {
                if(OSInfo.isWindows())
                    qmakeExe = "qmake.exe";
                else
                    qmakeExe = "qmake";
            }

            final String K_QT_VERSION = "QT_VERSION";

            List<String> qmakeArgs = new ArrayList<String>();
            qmakeArgs.add(qmakeExe);
            qmakeArgs.add("-query");
            qmakeArgs.add(K_QT_VERSION);

            try {
                File fileDir = new File(".");
                String[] sA = Exec.executeCaptureOutput(qmakeArgs, fileDir, getProject(), null, false);
                if(sA != null && sA.length == 2 && sA[0] != null)
                   tmpQtVersion = sA[0];		// stdout
                // Extract QT_VERSION:4.7.4
            } catch(InterruptedException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(tmpQtVersion != null) {
                if(tmpQtVersion.startsWith(K_QT_VERSION + ":"))
                    tmpQtVersion = tmpQtVersion.substring(K_QT_VERSION.length() + 1);
                tmpQtVersion = Util.stripLeadingAndTrailingWhitespace(tmpQtVersion);

                if(parseQtVersion(tmpQtVersion)) {
                    versionFound = true;
                    qtVersionSource = " (" + qmakeExe + " -query " + K_QT_VERSION + ")";
                }
            }
        }

        // This is last method as it is the target we are trying to set and also
        // debatable if it should be here at all.  Maybe the only use is with maybe
        // supporting really older Qt which does not allow: qmake -query
        if(!versionFound) {
            tmpQtVersion = (String) propertyHelper.getProperty(Constants.VERSION);	// ANT 1.7.x
            if(parseQtVersion(tmpQtVersion)) {
                versionFound = true;
                qtVersionSource = " (${" + Constants.VERSION + "})";
            }
        }

        return versionFound;
    }

    private boolean decideGeneratorPreProc() {
        List<String> generatorPreProcStageOneList = new ArrayList<String>();
        List<String> generatorPreProcStageTwoList = new ArrayList<String>();

        String compilerString = (String) propertyHelper.getProperty((String) null, Constants.COMPILER);
        if(compilerString == null)
            return false;

        String gccVersionMajor = "";
        if(Compiler.isCompiler(compilerString, Compiler.GCC, Compiler.MinGW, Compiler.MinGW_W64))
            gccVersionMajor = "=4";
        else if(Compiler.isCompiler(compilerString, Compiler.OldGCC))
            gccVersionMajor = "=3";

        Boolean is64bit = OSInfo.is64bit();
        if(OSInfo.isWindows()) {
            if(Compiler.is64Only(compilerString))
                generatorPreProcStageOneList.add("-DWIN64");
            generatorPreProcStageOneList.add("-DWIN32");	// always set this

            if(Compiler.isCompiler(compilerString, Compiler.MSVC2005, Compiler.MSVC2005_64)) {
                generatorPreProcStageOneList.add("-D_MSC_VER=1400");
            } else if(Compiler.isCompiler(compilerString, Compiler.MSVC2008, Compiler.MSVC2008_64)) {
                generatorPreProcStageOneList.add("-D_MSC_VER=1500");
            } else if(Compiler.isCompiler(compilerString, Compiler.MSVC2010, Compiler.MSVC2010_64)) {
                generatorPreProcStageOneList.add("-D_MSC_VER=1600");
            } else if(Compiler.isCompiler(compilerString, Compiler.GCC, Compiler.OldGCC, Compiler.MinGW, Compiler.MinGW_W64)) {
                generatorPreProcStageOneList.add("-D__GNUC__" + gccVersionMajor);
            }
        } else if(OSInfo.isLinux()) {
            generatorPreProcStageOneList.add("-D__unix__");
            generatorPreProcStageOneList.add("-D__linux__");
            generatorPreProcStageOneList.add("-D__GNUC__" + gccVersionMajor);
            if(is64bit != null) {
                if(is64bit.booleanValue())
                    generatorPreProcStageOneList.add("-D__x86_64__");
                else
                    generatorPreProcStageOneList.add("-D__i386__");
            }
        } else if(OSInfo.isMacOS()) {
            generatorPreProcStageOneList.add("-D__APPLE__");
            // FIXME: When we detect an alternative compiler is in use (LLVM)
            generatorPreProcStageOneList.add("-D__GNUC__" + gccVersionMajor);
            // if(OSInfo.isMacOSX64())
            //     generatorPreProcStageOneList.add("-D__LP64__");
        } else if(OSInfo.isFreeBSD()) {
            generatorPreProcStageOneList.add("-D__unix__");
            generatorPreProcStageOneList.add("-D__FreeBSD__");
            generatorPreProcStageOneList.add("-D__GNUC__" + gccVersionMajor);
            if(is64bit != null) {
                if(is64bit.booleanValue())
                    generatorPreProcStageOneList.add("-D__x86_64__");  // untested
                else
                    generatorPreProcStageOneList.add("-D__i386__");  // untested
            }
        } else if(OSInfo.isSolaris()) {
            generatorPreProcStageOneList.add("-D__unix__");
            generatorPreProcStageOneList.add("-Dsun");
        }

        if(generatorPreProcStageOneList.size() > 0) {
            generatorPreProcStageOneA = generatorPreProcStageOneList.toArray(new String[generatorPreProcStageOneList.size()]);
        } else {
            generatorPreProcStageOneA = null;
        }

        if(generatorPreProcStageTwoList.size() > 0) {
            generatorPreProcStageTwoA = generatorPreProcStageTwoList.toArray(new String[generatorPreProcStageTwoList.size()]);
        } else {
            generatorPreProcStageTwoA = null;
        }

        return true;
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

        if(verbose) System.out.println(Constants.CONFIGURATION + ": " + result);
        return result;
    }

    private String doesQtLibExistDir(String librarydir, String name) {
        File dir = new File(librarydir, name);
        if(dir.exists() && dir.isDirectory())
            return dir.getAbsolutePath();
        return null;
    }

    private String doesQtLibExist(String name, String version, String librarydir, Boolean debugValue) {
        StringBuilder path = new StringBuilder();

        if(librarydir != null) {
            path.append(librarydir);
        } else {
            Object qtjambiQtLibdirObject = propertyHelper.getProperty((String) null, Constants.LIBDIR);
            if(qtjambiQtLibdirObject != null)
                path.append(qtjambiQtLibdirObject.toString());
        }

        path.append(File.separator);
        boolean thisDebug = debug;
        if(debugValue != null)
            thisDebug = debugValue.booleanValue();
        path.append(LibraryEntry.formatQtName(name, thisDebug, version));
        File testForFile = new File(path.toString());
        if(verboseLevel > 1)
            System.out.println("Checking QtLib: " + path + " " + testForFile.exists());
        if(testForFile.exists())
            return testForFile.getAbsolutePath();
        return null;
    }

    private String doesQtLibExist(String name, String version) {
        return doesQtLibExist(name, version, null, null);
    }

    // FIXME: Phase this out (compatiblity method, use absolute path and != null to mean true/present)
    private boolean doesQtLibExistAsBoolean(String name, String version, String librarydir) {
        if(doesQtLibExist(name, version, librarydir, null) != null)
            return true;
        return false;
    }

    // FIXME: Phase this out (compatiblity method, use absolute path and != null to mean true/present)
    private boolean doesQtLibExistAsBoolean(String name, String version) {
        if(doesQtLibExist(name, version) != null)
            return true;
        return false;
    }

    // FIXME: This remains another method because of _debug and _debuglib differences in #formatQtJambiName() and #formatQtName()
    private boolean doesQtJambiLibExist(String name, String librarydir) {
        StringBuilder path = new StringBuilder();

        if(librarydir != null) {
            path.append(librarydir);
        } else {
            Object qtjambiQtLibdirObject = propertyHelper.getProperty((String) null, Constants.LIBDIR);
            if(qtjambiQtLibdirObject != null)
                path.append(qtjambiQtLibdirObject.toString());
        }

        path.append(File.separator);
        path.append(LibraryEntry.formatQtJambiName(name, debug, String.valueOf(qtMajorVersion)));
        //System.out.println("Checking QtLib: " + path);
        return new File(path.toString()).exists();
    }

    private boolean doesQtBinExist(String name, String librarydir) {
        StringBuilder path = new StringBuilder();

        if(librarydir != null) {
            path.append(librarydir);
        } else {
            Object qtjambiQtBindirObject = propertyHelper.getProperty((String) null, Constants.BINDIR);
            if(qtjambiQtBindirObject != null)
                path.append(qtjambiQtBindirObject.toString());
        }

        path.append(File.separator);
        path.append(LibraryEntry.formatQtJambiName(name, false, null));  // unversioned
        //System.out.println("Checking QtBin: " + path);
        return new File(path.toString()).exists();
    }

    private boolean doesQtPluginExist(String name, String subdir, boolean noLibPrefix) {
        StringBuilder path = new StringBuilder();
        String pluginsDirPropertyName;
        if(noLibPrefix)
            pluginsDirPropertyName = Constants.QTJAMBI_PHONON_PLUGINSDIR;
        else
            pluginsDirPropertyName = Constants.PLUGINSDIR;
        String pluginsPath = (String) propertyHelper.getProperty((String) null, pluginsDirPropertyName);
        if(pluginsPath == null)
            return false;
        File filePluginsPath = new File(pluginsPath);
        if(filePluginsPath.isDirectory() == false)
            return false;
        path.append(pluginsPath);
        path.append(File.separator);
        // We make buildpath.properties also set the plugins/ part (like lib/)
        //path.append("plugins");
        //path.append(File.separator);
        path.append(subdir);
        path.append(File.separator);

        //! TODO: useful?
        path.append(LibraryEntry.formatPluginName(name, noLibPrefix, debug, String.valueOf(qtMajorVersion)));
        return new File(path.toString()).exists();
    }

    private boolean doesQtPluginExist(String name, String subdir) {
        return doesQtPluginExist(name, subdir, false);
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

        if((verboseMode == -1 && verbose) || (verboseMode > 0)) {
            String prettyCurrentValue = currentValue;
            if(prettyCurrentValue == null)
                prettyCurrentValue = "<notset>";
            else if(prettyCurrentValue.length() == 0)
                prettyCurrentValue = "<empty-string>";
            System.out.println(attrName + ": " + prettyCurrentValue + sourceValue);
        }

        return currentValue;
    }

    /**
     * Decide whether we have phonon plugin and check
     * correct phonon backend to use for this OS.
     */
    private String decidePhonon(PropertyHelper propertyHelper) {
        String phononLibDir = (String) propertyHelper.getProperty((String) null, Constants.QTJAMBI_PHONON_LIBDIR);
        boolean exists = doesQtLibExistAsBoolean("phonon", String.valueOf(qtMajorVersion), phononLibDir);
        String result = String.valueOf(exists);

        result = mySetProperty(propertyHelper, -1, Constants.PHONON, " (auto-detected)", result, false);
        if("false".equals(result))
            return result;

        addToQtConfig("phonon");

        // We now just do plugin detection and emit what we see, regardless of platform
        decidePluginsPhononBackendPhononDs9();

        decidePluginsPhononBackendPhononGstreamer();

        decidePluginsPhononBackendPhononQt7();

        return result;
    }

    private String decidePluginsPhononBackendPhononDs9() {
        boolean exists = doesQtPluginExist("phonon_ds9", "phonon_backend");
        String result = String.valueOf(exists);
        String sourceValue = null;
        if(exists)
            sourceValue = " (auto-detected)";
        else if(OSInfo.isWindows() == false)
            sourceValue = " (expected for non-Windows platform)";
        mySetProperty(propertyHelper, -1, Constants.PHONON_DS9, sourceValue, result, false);
        return result;
    }

    private String decidePluginsPhononBackendPhononGstreamer() {
        boolean exists;
        String result;
        String sourceValue = null;
        Boolean autodetectKdePhonon = null;

        String kdephonon = (String) propertyHelper.getProperty((String) null, Constants.QTJAMBI_PHONON_KDEPHONON);
        if(kdephonon != null && "true".equals(kdephonon)) {
            // build configuration states use KDE phonon
            exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend", true);  // auto-detect Kde phonon
            result = String.valueOf(exists);
            if(exists == false) {  // not found
                exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend");  // try for Qt phonon anyway?
                result = String.valueOf(exists);
                if(exists)  // found
                    sourceValue = " (WARNING auto-detected qt phonon; but " + Constants.QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon +"\"; so turning this off)";
                autodetectKdePhonon = Boolean.FALSE;
            } else {
                sourceValue = " (auto-detected kind:kdephonon)";
            }
        } else {
            // build configuration states use Qt phonon
            exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend");   // auto-detect Qt phonon
            result = String.valueOf(exists);
            if(exists) {
                if(kdephonon != null && "true".equals(kdephonon))  // but user has setup kde phonon as well?
                    sourceValue = " (auto-detected; WARNING " + Constants.QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon + "\"; so \"lib\" prefix removed)";
            } else {
                exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend", true);  // try for KDE phonon anyway?
                result = String.valueOf(exists);
                if(exists) {
                    if(kdephonon == null)
                        sourceValue = " (auto-detected as kind:kdephonon)";
                    else
                        sourceValue = " (auto-detected kde phonon; but " + Constants.QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon + "\"; so \"lib\" prefix removed)";
                    autodetectKdePhonon = Boolean.TRUE;
                } else {
                    autodetectKdePhonon = Boolean.FALSE;  // FWIW
                }
            }
        }
        if(sourceValue == null) {
            if(exists)
                sourceValue = " (auto-detected)";
            else if(OSInfo.isWindows() || OSInfo.isMacOS())
                sourceValue = " (expected for non-Unix platform)";
        }
        mySetProperty(propertyHelper, -1, Constants.PHONON_GSTREAMER, sourceValue, result, false);
        if(autodetectKdePhonon != null) {
            if(autodetectKdePhonon.booleanValue())
                mySetProperty(propertyHelper, -1, Constants.QTJAMBI_PHONON_KDEPHONON, " (auto-detected)", "true", false);
            else
                mySetProperty(propertyHelper, -1, Constants.QTJAMBI_PHONON_KDEPHONON, null, "false", true);  // force off
        }
        return result;
    }

    private String decidePluginsPhononBackendPhononQt7() {
        boolean exists = doesQtPluginExist("phonon_qt7", "phonon_backend");
        String result = String.valueOf(exists);
        String sourceValue = null;
        if(exists)
            sourceValue = " (auto-detected)";
        else if(OSInfo.isMacOS() == false)
            sourceValue = " (expected for non-MacOSX platform)";
        mySetProperty(propertyHelper, -1, Constants.PHONON_QT7, sourceValue, result, false);
        return result;
    }

    public static boolean findInString(String haystack, String needle, char delimChar) {
        final int nLen = needle.length();
        final int hLen = haystack.length();
        int o = 0;
        boolean found = false;
        while(o < hLen) {
            int stringOffset = haystack.indexOf(needle, o);
            if(stringOffset < 0)
                break;
            if(stringOffset == 0 || haystack.charAt(stringOffset - 1) == delimChar) {
                if(hLen <= stringOffset + nLen || haystack.charAt(stringOffset + nLen) == delimChar) {
                   // found
                   found = true;
                   break;
                }
            }
            o = stringOffset + nLen;
        }
        return found;
    }

    /**
     * Adds new library to qtjambi.qtconfig property, which is used
     * to specify additional qt libraries compiled.
     * @param config Library to add
     */
    private void addToQtConfig(String config) {
        String oldConfig = (String) propertyHelper.getProperty((String) null, Constants.QTCONFIG);
        String newConfig = null;

        if(oldConfig != null) {
            final char delimChar = ' ';
            // Check it doesn't already exist (and don't add it again), this
            //  happens when InitializeTask runs twice due to multiple ant
            //  targets being set.
            boolean found = findInString(oldConfig, config, delimChar);
            if(!found) {
                newConfig = oldConfig + delimChar + config;
            }
        } else {
            newConfig = config;
        }

        if(newConfig != null)
            propertyHelper.setProperty((String) null, Constants.QTCONFIG, newConfig, false);
    }

    private String decideSql() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtSql", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.SQL + ": " + result);
        if("true".equals(result)) addToQtConfig("sql");
        return result;
    }

    private String decideSvg() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtSvg", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.SVG + ": " + result);
        if("true".equals(result)) addToQtConfig("svg");
        return result;
    }

    private String decideTest() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtTest", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.TEST + ": " + result);
        if("true".equals(result)) addToQtConfig("qtestlib");
        return result;
    }

    private String decidePlugins(String attrName, String pluginPath, String name) {
        String result = String.valueOf(doesQtPluginExist("q" + name, pluginPath));
        if(verbose) System.out.println(attrName + ": " + result);
        return result;
    }

    private String decidePluginsAccessibleQtaccesswidgets() {
        String result = String.valueOf(doesQtPluginExist("qtaccessiblewidgets", "accessible"));
        if(verbose) System.out.println(Constants.PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS + ": " + result);
        return result;
    }

    private String decidePluginsCodecs(String attrName, String name) {
        String result = String.valueOf(doesQtPluginExist("q" + name, "codecs"));
        if(verbose) System.out.println(attrName + ": " + result);
        return result;
    }

    private String decidePluginsIconenginesSvgicon(){
        String result = String.valueOf(doesQtPluginExist("qsvgicon", "iconengines"));
        if(verbose) System.out.println(Constants.PLUGINS_ICONENGINES_SVGICON + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsGif(){
        String result = String.valueOf(doesQtPluginExist("qgif", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_GIF + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsIco(){
        String result = String.valueOf(doesQtPluginExist("qico", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_ICO + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsJpeg(){
        String result = String.valueOf(doesQtPluginExist("qjpeg", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_JPEG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsMng(){
        String result = String.valueOf(doesQtPluginExist("qmng", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_MNG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsPng(){
        String result = String.valueOf(doesQtPluginExist("qpng", "imageformats"));
        String extra = "";
        if("false".equals(result))
            extra = " (probably a built-in)";
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_PNG + ": " + result + extra);
        return result;
    }

    private String decidePluginsImageformatsSvg(){
        String result = String.valueOf(doesQtPluginExist("qsvg", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_SVG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsTiff() {
        String result = String.valueOf(doesQtPluginExist("qtiff", "imageformats"));
        if(verbose) System.out.println(Constants.PLUGINS_IMAGEFORMATS_TIFF + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlite() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlite", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLITE + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlite2() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlite2", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLITE2 + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlmysql() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlmysql", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLMYSQL + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlodbc() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlodbc", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLODBC + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlpsql() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlpsql", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLPSQL + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqltds() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqltds", "sqldrivers"));
        if(verbose) System.out.println(Constants.PLUGINS_SQLDRIVERS_SQLTDS + ": " + result);
        return result;
    }

    private String decideCore() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtCore", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.CORE + ": " + result);
        if("true".equals(result)) addToQtConfig("core");
        return result;
    }

    private String decideCLucene() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtCLucene", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.CLUCENE + ": " + result);
        return result;
    }

    private String decideDBus() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtDBus", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.DBUS + ": " + result);
        if("true".equals(result)) addToQtConfig("dbus");
        return result;
    }

    private String decideDeclarative() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtDeclarative", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.DECLARATIVE + ": " + result);
        if("true".equals(result)) addToQtConfig("declarative");
        return result;
    }

    private String decideDesigner() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtDesigner", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.DESIGNER + ": " + result);
        if("true".equals(result)) addToQtConfig("designer");
        return result;
    }

    private String decideDesignerComponents() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtDesignerComponents", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.DESIGNERCOMPONENTS + ": " + result);
        return result;
    }

    private String decideGui() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtGui", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.GUI + ": " + result);
        if("true".equals(result)) addToQtConfig("gui");
        return result;
    }

    private String decideHelp() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtHelp", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.HELP + ": " + result);
        if("true".equals(result)) addToQtConfig("help");
        return result;
    }

    private String decideMultimedia() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtMultimedia", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.MULTIMEDIA + ": " + result);
        if("true".equals(result)) addToQtConfig("multimedia");
        return result;
    }

    private String decideNetwork() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtNetwork", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.NETWORK + ": " + result);
        if("true".equals(result)) addToQtConfig("network");
        return result;
    }

    private String decideOpenGL() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtOpenGL", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.OPENGL + ": " + result);
        if("true".equals(result)) addToQtConfig("opengl");
        return result;
    }

    private String decideScript() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtScript", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.SCRIPT + ": " + result);
        if("true".equals(result)) addToQtConfig("script");
        return result;
    }

    private String decideScripttools() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtScriptTools", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.SCRIPTTOOLS + ": " + result);
        if("true".equals(result)) addToQtConfig("scripttools");
        return result;
    }

    private String decideWebkit() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtWebKit", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.WEBKIT + ": " + result);
        if("true".equals(result)) addToQtConfig("webkit");
        return result;
    }

    private String decideXml() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtXml", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.XML + ": " + result);
        if("true".equals(result)) addToQtConfig("xml");
        return result;
    }

    private String decideXmlPatterns() {
        String result = String.valueOf(doesQtLibExistAsBoolean("QtXmlPatterns", String.valueOf(qtMajorVersion)));
        if(verbose) System.out.println(Constants.XMLPATTERNS + ": " + result);
        if("true".equals(result)) addToQtConfig("xmlpatterns");
        return result;
    }

    private String decideQtLibDso(String attrName, String name, String version, boolean verboseFlag) {
        String path = doesQtLibExist(name, version, null, Boolean.FALSE);
        if(verboseFlag && path != null) System.out.println(attrName + ": " + path);
        return path;
    }

    private String decideQtLibDso(String attrName, String name, String version) {
        return decideQtLibDso(attrName, name, version, verbose);
    }

    private String decideQtLibDso(String attrName, String name, String[] tryVersionA, Boolean verboseBoolean) {
        boolean thisVerbose = verbose;
        if(verboseBoolean != null)
            thisVerbose = verboseBoolean.booleanValue();

        if(tryVersionA == null)
            return decideQtLibDso(attrName, name, null, thisVerbose);  // run at least once

        String rv = null;
        for(String tryVersion : tryVersionA) {
            if(tryVersion != null)
                rv = decideQtLibDso(attrName, name, tryVersion, thisVerbose);
            else
                rv = decideQtLibDso(attrName, name, null, thisVerbose);
            if(rv != null)
                return rv;
        }
        return rv;
    }

    private String decideQtBinDso(String attrName, String name) {
        boolean bf = doesQtBinExist(name, null);
        String result = String.valueOf(bf);
        if(verbose && bf) System.out.println(attrName + ": " + result);
        return result;
    }

}