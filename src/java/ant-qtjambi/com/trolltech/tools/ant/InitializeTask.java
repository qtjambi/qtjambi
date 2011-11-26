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

    public static final String DEFAULT_QTJAMBI_SONAME_VERSION_MAJOR = "1";

    private boolean verbose;
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

    /*
     * These properties are set outside of this task
     *
     * TODO: These flags should be documented here and if possibly, outside in
     * build documentation.
     * Or rather these binds shouldn't exist, how much of this could be moved to
     * xml side?
     */
    public static final String BINDIR                   = "qtjambi.qt.bindir";
    public static final String LIBDIR                   = "qtjambi.qt.libdir";
    public static final String INCLUDEDIR               = "qtjambi.qt.includedir";
    public static final String PLUGINSDIR               = "qtjambi.qt.pluginsdir";
    public static final String PHONONPLUGINSDIR         = "qtjambi.phonon.pluginsdir";
    public static final String GENERATOR_PREPROC_STAGE1 = "qtjambi.generator.preproc.stage1";
    public static final String GENERATOR_PREPROC_STAGE2 = "qtjambi.generator.preproc.stage2";
    public static final String PHONONLIBDIR             = "qtjambi.phonon.libdir";
    public static final String JAVALIBDIR               = "qtjambi.java.library.path";
    public static final String JAMBILIBDIR              = "qtjambi.jambi.libdir";
    public static final String JAMBIPLUGINSDIR          = "qtjambi.jambi.pluginsdir";
    public static final String VERSION                  = "qtjambi.version";
    public static final String BUNDLE_VERSION           = "qtjambi.version.bundle";
    public static final String BUNDLE_VERSION_MODE      = "qtjambi.version.bundle.mode";
    public static final String SUFFIX_VERSION           = "qtjambi.version.suffix";
    public static final String JAVA_HOME_TARGET         = "java.home.target";
    public static final String JAVA_OSARCH_TARGET       = "java.osarch.target";

    public static final String QT_VERSION_MAJOR         = "qt.version.major";
    public static final String QT_VERSION_MINOR         = "qt.version.minor";
    public static final String QT_VERSION_PATCHLEVEL    = "qt.version.patchlevel";
    public static final String QT_VERSION               = "qt.version";

    public static final String QT_VERSION_MAJOR_NEXT    = "qt.version.major.next";
    public static final String QT_VERSION_MINOR_NEXT    = "qt.version.minor.next";

    public static final String QT_VERSION_PROPERTIES          = "version.properties";
    public static final String QT_VERSION_PROPERTIES_TEMPLATE = "version.properties.template";

    public static final String CONFIG_RELEASE     = "release";
    public static final String CONFIG_DEBUG       = "debug";
    public static final String CONFIG_TEST        = "test";

    /*
     * This is needed for Linux/Unix/MacOSX so that the bundled item filename matches the
     *  one referenced by the dynamic linker.
     * This is not needed on Windows.
     */
    public static final String QTJAMBI_SONAME_VERSION_MAJOR   = "qtjambi.soname.version.major";

    /*
     * These properties are set inside this task
     */
    public static final String CLUCENE            = "qtjambi.clucene";
    public static final String COMPILER           = "qtjambi.compiler";
    public static final String CONFIG             = "qtjambi.config";
    public static final String CONFIGURATION      = "qtjambi.configuration";
    public static final String CONFIGURATION_OSGI = "qtjambi.configuration.osgi";
    public static final String CORE               = "qtjambi.core";		// mandatory with <= 4.7.x
    public static final String DBUS               = "qtjambi.dbus";
    public static final String DECLARATIVE        = "qtjambi.declarative";
    public static final String DESIGNER           = "qtjambi.designer";
    public static final String DESIGNERCOMPONENTS = "qtjambi.designercomponents";
    public static final String GUI                = "qtjambi.gui";              // mandatory with <= 4.7.x
    public static final String HELP               = "qtjambi.help";
    public static final String MULTIMEDIA         = "qtjambi.multimedia";
    public static final String NETWORK            = "qtjambi.network";          // mandatory with <= 4.7.x
    public static final String OPENGL             = "qtjambi.opengl";
    public static final String OSNAME             = "qtjambi.osname";
    public static final String OSPLATFORM         = "qtjambi.osplatform";	// linux windows macosx
    public static final String OSCPU              = "qtjambi.oscpu";		// i386 x86_64 x86 x32
    public static final String PHONON             = "qtjambi.phonon";
    public static final String PHONON_DS9         = "qtjambi.phonon_ds9";
    public static final String PHONON_GSTREAMER   = "qtjambi.phonon_gstreamer";
    public static final String PHONON_QT7         = "qtjambi.phonon_qt7";
    public static final String QMAKESPEC          = "qtjambi.qmakespec";
    public static final String SCRIPT             = "qtjambi.script";
    public static final String SCRIPTTOOLS        = "qtjambi.scripttools";
    public static final String SQL                = "qtjambi.sql";
    public static final String SVG                = "qtjambi.svg";
    public static final String TEST               = "qtjambi.test";
    public static final String WEBKIT             = "qtjambi.webkit";
    public static final String XML                = "qtjambi.xml";              // mandatory with <= 4.7.x
    public static final String XMLPATTERNS        = "qtjambi.xmlpatterns";
    public static final String QTCONFIG           = "qtjambi.qtconfig";

    public static final String PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS  = "qtjambi.plugins.accessible.qtaccessiblewidgets";

    public static final String QTJAMBI_PHONON_KDEPHONON           = "qtjambi.phonon.kdephonon";
    public static final String QTJAMBI_PHONON_PLUGINSDIR          = "qtjambi.phonon.pluginsdir";

    public static final String PLUGINS_BEARER_CONNMANBEARER       = "qtjambi.plugins.bearer.connmanbearer";
    public static final String PLUGINS_BEARER_GENERICBEARER       = "qtjambi.plugins.bearer.genericbearer";
    public static final String PLUGINS_BEARER_NATIVEWIFIBEARER    = "qtjambi.plugins.bearer.nativewifibearer";
    public static final String PLUGINS_BEARER_NMBEARER            = "qtjambi.plugins.bearer.nmbearer";

    public static final String PLUGINS_CODECS_CNCODECS      = "qtjambi.plugins.codecs.cncodecs";
    public static final String PLUGINS_CODECS_JPCODECS      = "qtjambi.plugins.codecs.jpcodecs";
    public static final String PLUGINS_CODECS_KRCODECS      = "qtjambi.plugins.codecs.krcodecs";
    public static final String PLUGINS_CODECS_TWCODECS      = "qtjambi.plugins.codecs.twcodecs";

    public static final String PLUGINS_DESIGNER_ARTHURPLUGIN         = "qtjambi.plugins.designer.arthurplugin";
    public static final String PLUGINS_DESIGNER_CONTAINEREXTENSION   = "qtjambi.plugins.designer.containerextension";
    public static final String PLUGINS_DESIGNER_CUSTOMWIDGETPLUGIN   = "qtjambi.plugins.designer.customwidgetplugin";
    public static final String PLUGINS_DESIGNER_PHONONWIDGETS        = "qtjambi.plugins.designer.phononwidgets";
    public static final String PLUGINS_DESIGNER_QAXWIDGET            = "qtjambi.plugins.designer.qaxwidget";
    public static final String PLUGINS_DESIGNER_QDECLARATIVEVIEW     = "qtjambi.plugins.designer.qdeclarativeview";
    public static final String PLUGINS_DESIGNER_QWEBVIEW             = "qtjambi.plugins.designer.qwebview";
    public static final String PLUGINS_DESIGNER_TASKMENUEXTENSION    = "qtjambi.plugins.designer.taskmenuextension";
    public static final String PLUGINS_DESIGNER_WORLDTIMECLOCKPLUGIN = "qtjambi.plugins.designer.worldtimeclockplugin";

    public static final String PLUGINS_GRAPHICSSYSTEMS_GLGRAPHICSSYSTEM    = "qtjambi.plugins.graphicssystems.glgraphicssystem";
    public static final String PLUGINS_GRAPHICSSYSTEMS_TRACEGRAPHICSSYSTEM = "qtjambi.plugins.graphicssystems.tracegraphicssystem";

    public static final String PLUGINS_ICONENGINES_SVGICON  = "qtjambi.plugins.iconengines.svgicon";

    public static final String PLUGINS_IMAGEFORMATS_GIF     = "qtjambi.plugins.imageformats.gif";
    public static final String PLUGINS_IMAGEFORMATS_ICO     = "qtjambi.plugins.imageformats.ico";
    public static final String PLUGINS_IMAGEFORMATS_JPEG    = "qtjambi.plugins.imageformats.jpeg";
    public static final String PLUGINS_IMAGEFORMATS_MNG     = "qtjambi.plugins.imageformats.mng";
    // PNG not seen in wild due to being statically linked into Qt DSOs
    public static final String PLUGINS_IMAGEFORMATS_PNG     = "qtjambi.plugins.imageformats.png";
    public static final String PLUGINS_IMAGEFORMATS_SVG     = "qtjambi.plugins.imageformats.svg";
    public static final String PLUGINS_IMAGEFORMATS_TIFF    = "qtjambi.plugins.imageformats.tiff";

    public static final String PLUGINS_INPUTMETHODS_IMSW_MULTI    = "qtjambi.plugins.inputmethods.imsw-multi";

    public static final String PLUGINS_QMLTOOLING_QMLDBG_TCP      = "qtjambi.plugins.qmltooling.qmldbg_tcp";

    public static final String PLUGINS_SCRIPT_QMLDBG_QTSCRIPTDBUS = "qtjambi.plugins.script.qtscriptdbus";

    public static final String PLUGINS_SQLDRIVERS_SQLITE    = "qtjambi.plugins.sqldrivers.sqlite";
    public static final String PLUGINS_SQLDRIVERS_SQLITE2   = "qtjambi.plugins.sqldrivers.sqlite2";
    public static final String PLUGINS_SQLDRIVERS_SQLMYSQL  = "qtjambi.plugins.sqldrivers.sqlmysql";
    public static final String PLUGINS_SQLDRIVERS_SQLODBC   = "qtjambi.plugins.sqldrivers.sqlodbc";
    public static final String PLUGINS_SQLDRIVERS_SQLPSQL   = "qtjambi.plugins.sqldrivers.sqlpsql";
    public static final String PLUGINS_SQLDRIVERS_SQLTDS    = "qtjambi.plugins.sqldrivers.sqltds";

    public static final String PACKAGING_DSO_LIBSTDC___6     = "qtjambi.packaging.dso.libstdc++-6";     // Windows MinGW runtime pre-req
    public static final String PACKAGING_DSO_LIBGCC_S_DW2_1  = "qtjambi.packaging.dso.libgcc_s_dw2-1";  // Windows MinGW runtime pre-req
    public static final String PACKAGING_DSO_LIBGCC_S_SJLJ_1 = "qtjambi.packaging.dso.libgcc_s_sjlj-1"; // Windows MinGW-W64 runtime pre-req
    public static final String PACKAGING_DSO_MINGWM10        = "qtjambi.packaging.dso.mingwm10";        // Windows older MinGW runtime pre-req

    public static final String PACKAGING_DSO_ZLIB1    = "qtjambi.packaging.dso.zlib1";      // Windows
    public static final String PACKAGING_DSO_LIBSSL32 = "qtjambi.packaging.dso.libssl32";   // Windows MinGW
    public static final String PACKAGING_DSO_SSLEAY32 = "qtjambi.packaging.dso.ssleay32";   // Windows MSVC
    public static final String PACKAGING_DSO_LIBEAY32 = "qtjambi.packaging.dso.libeay32";   // Windows

    public static final String PACKAGING_DSO_CPLUSPLUSRUNTIME = "qtjambi.packaging.dso.cplusplusruntime";

    public static final String QTJAMBI_MACOSX_QTMENUNIB_DIR = "qtjambi.macosx.qtmenunib.dir";

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
        // we should only run this once per ANT invocation
        if(alreadyRun)
            return;

        propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        String sep = (String) propertyHelper.getProperty("sep");
        if(sep == null) {
            sep = File.separator;
            propertyHelper.setNewProperty((String) null, "sep", sep);
            if(verbose)
                System.out.println("sep is " + sep + " (auto-detect)");
        } else {
            if(verbose)
                System.out.println("sep is " + sep);
        }

        String psep = (String) propertyHelper.getProperty("psep");
        if(psep == null) {
            psep = File.pathSeparator;
            propertyHelper.setNewProperty((String) null, "psep", psep);
            if(verbose)
                System.out.println("psep is " + psep + " (auto-detect)");
        } else {
            if(verbose)
                System.out.println("psep is " + psep);
        }

        FindCompiler finder = new FindCompiler(getProject(), propertyHelper);

        String osname = finder.decideOSName();
        propertyHelper.setNewProperty((String) null, OSNAME, osname);
        if(verbose)
            System.out.println(OSNAME + " is " + osname);

        Compiler compiler = finder.decideCompiler();
        propertyHelper.setNewProperty((String) null, COMPILER, compiler.toString());
        if(verbose)
            System.out.println(COMPILER + " is " + compiler.toString());

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
            propertyHelper.setNewProperty((String) null, OSPLATFORM, s);

        s = null;
        // FIXME incorrect for windows x86/x64, sunos
        if(osname.endsWith("64"))
            s = "x86_64";
        else
            s = "i386";
        if(s != null)
            propertyHelper.setNewProperty((String) null, OSCPU, s);

        finder.checkCompilerDetails();
        //finder.checkCompilerBits();

        propertyHelper.setNewProperty((String) null, JAVA_HOME_TARGET, decideJavaHomeTarget());
        propertyHelper.setNewProperty((String) null, JAVA_OSARCH_TARGET, decideJavaOsarchTarget());

        String configuration = decideConfiguration();
        propertyHelper.setNewProperty((String) null, CONFIGURATION, configuration);
        s = null;
        if("release".equals(configuration))
            s = "";	// empty
        else if("debug".equals(configuration))
            s = ".debug";
        else
            s = ".test";
        if(s != null)
            propertyHelper.setNewProperty((String) null, CONFIGURATION_OSGI, s);
        

        if(!decideQtVersion())
            throw new BuildException("Unable to determine Qt version, try editing: " + pathVersionPropertiesTemplate);
        s = String.valueOf(qtVersion);
        if(verbose)
            System.out.println(QT_VERSION + " is " + s + qtVersionSource);
        propertyHelper.setNewProperty((String) null, QT_VERSION, s);
        propertyHelper.setNewProperty((String) null, VERSION, s); // this won't overwrite existing value

        s = String.valueOf(qtMajorVersion);
        if(verbose)
            System.out.println(QT_VERSION_MAJOR + " is " + s);
        propertyHelper.setNewProperty((String) null, QT_VERSION_MAJOR, s);
        propertyHelper.setNewProperty((String) null, QT_VERSION_MAJOR_NEXT, String.valueOf(qtMajorVersion + 1));
        propertyHelper.setNewProperty((String) null, QT_VERSION_MINOR,      String.valueOf(qtMinorVersion));
        propertyHelper.setNewProperty((String) null, QT_VERSION_MINOR_NEXT, String.valueOf(qtMinorVersion + 1));
        propertyHelper.setNewProperty((String) null, QT_VERSION_PATCHLEVEL, String.valueOf(qtPatchlevelVersion));


        versionSuffix = (String) propertyHelper.getProperty(SUFFIX_VERSION);
        mySetProperty(propertyHelper, -1, SUFFIX_VERSION, null, null, false);  // report

        String canonVersionSuffix;
        if(versionSuffix != null)
            canonVersionSuffix = versionSuffix;
        else
            canonVersionSuffix = "";
        String bundleVersionMode = (String) propertyHelper.getProperty(BUNDLE_VERSION_MODE);
        if(bundleVersionMode != null) {
            if(bundleVersionMode.equals("auto-suffix-date")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                s = qtVersion + "." + sdf.format(new Date()) + canonVersionSuffix;
            }
        } else {
            s = qtVersion + canonVersionSuffix;
        }
        mySetProperty(propertyHelper, -1, BUNDLE_VERSION, null, s, true);


        if(OSInfo.isWindows() == false)   // skip setting it by default, only do for Linux/MacOSX/Unix set to soname major
            mySetProperty(propertyHelper, -1, QTJAMBI_SONAME_VERSION_MAJOR, " (set by init)", DEFAULT_QTJAMBI_SONAME_VERSION_MAJOR, false);


        if(!decideGeneratorPreProc())
            throw new BuildException("Unable to determine generator pre-processor settings");
        s = Util.safeArrayToString(generatorPreProcStageOneA);
        if(verbose)
            System.out.println(GENERATOR_PREPROC_STAGE1 + " is " + ((s != null) ? s : "<unset>"));
        propertyHelper.setNewProperty((String) null, GENERATOR_PREPROC_STAGE1, Util.safeArrayJoinToString(generatorPreProcStageOneA, ","));
        s = Util.safeArrayToString(generatorPreProcStageTwoA);
        if(verbose)
            System.out.println(GENERATOR_PREPROC_STAGE2 + " is " + ((s != null) ? s : "<unset>"));
        propertyHelper.setNewProperty((String) null, GENERATOR_PREPROC_STAGE2, Util.safeArrayJoinToString(generatorPreProcStageTwoA, ","));


        String d = (String) propertyHelper.getProperty((String) null, LIBDIR).toString();
        if(d != null) {
            String sourceValue = null;
//            s = (String) propertyHelper.getProperty(QTJAMBI_MACOSX_QTMENUNIB_DIR);
//            if(s == null) {
                s = doesQtLibExistDir(d, "qt_menu.nib");
                //if(s == null)
                //    s= = doesQtLibExistDir(d, "src/gui/mac/qt_menu.nib");
                // FIXME: auto-detect, directroy from source, directory from QtSDK on MacOSX, directory from framework on MacOSX
                if(s != null)
                    sourceValue = " (auto-detected)";
                
//            }
            if(s == null) {
                if(OSInfo.isMacOS() == false)
                    sourceValue = " (expected for non-MacOSX platform)";
                else
                    sourceValue = " (WARNING you should resolve this for targetting MacOSX)";
            }
            mySetProperty(propertyHelper, -1, QTJAMBI_MACOSX_QTMENUNIB_DIR, sourceValue, s, false);
        }
        if(propertyHelper.getProperty(QTJAMBI_MACOSX_QTMENUNIB_DIR) == null)
            propertyHelper.setProperty((String) null, QTJAMBI_MACOSX_QTMENUNIB_DIR, "", false);


        String clucene = decideCLucene();
        if("true".equals(clucene))
            propertyHelper.setNewProperty((String) null, CLUCENE, clucene);

        String core = decideCore();
        if("true".equals(core))
            propertyHelper.setNewProperty((String) null, CORE, core);

        String dbus = decideDBus();
        if("true".equals(dbus))
            propertyHelper.setNewProperty((String) null, DBUS, dbus);

        String declarative = decideDeclarative();
        if("true".equals(declarative))
            propertyHelper.setNewProperty((String) null, DECLARATIVE, declarative);

        String designer = decideDesigner();
        if("true".equals(designer))
            propertyHelper.setNewProperty((String) null, DESIGNER, designer);

        String designercomponents = decideDesignerComponents();
        if("true".equals(designercomponents))
            propertyHelper.setNewProperty((String) null, DESIGNERCOMPONENTS, designercomponents);

        String gui = decideGui();
        if("true".equals(gui))
            propertyHelper.setNewProperty((String) null, GUI, gui);

        String helptool = decideHelp();
        if("true".equals(helptool))
            propertyHelper.setNewProperty((String) null, HELP, helptool);

        String multimedia = decideMultimedia();
        if("true".equals(multimedia))
            propertyHelper.setNewProperty((String) null, MULTIMEDIA, multimedia);

        String network = decideNetwork();
        if("true".equals(network))
            propertyHelper.setNewProperty((String) null, NETWORK, network);

        String opengl = decideOpenGL();
        if("true".equals(opengl))
            propertyHelper.setNewProperty((String) null, OPENGL, opengl);

        String phonon = decidePhonon(propertyHelper);

        String script = decideScript();
        if("true".equals(script))
            propertyHelper.setNewProperty((String) null, SCRIPT, script);

        String scripttools = decideScripttools();
        if("true".equals(scripttools))
            propertyHelper.setNewProperty((String) null, SCRIPTTOOLS, scripttools);

        propertyHelper.setNewProperty((String) null, SQL, decideSql());

        propertyHelper.setNewProperty((String) null, SVG, decideSvg());

        propertyHelper.setNewProperty((String) null, TEST, decideTest());

        String webkit = decideWebkit();
        // Not sure why this is a problem "ldd libQtWebKit.so.4.7.4" has no dependency on libphonon for me,
        //  if you have headers and DSOs for WebKit then QtJambi should build the support.
        if("true".equals(webkit) && "true".equals(phonon) == false) {
            if(verbose) System.out.println("WARNING: " + WEBKIT + " is " + webkit + ", but " + PHONON + " is " + phonon);
        }
        if("true".equals(webkit))
            propertyHelper.setNewProperty((String) null, WEBKIT, webkit);

        String xml = decideXml();
        if("true".equals(xml))
            propertyHelper.setNewProperty((String) null, XML, xml);

        String xmlpatterns = decideXmlPatterns();
        if("true".equals(xmlpatterns))
            propertyHelper.setNewProperty((String) null, XMLPATTERNS, xmlpatterns);


        propertyHelper.setNewProperty((String) null, PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS, decidePluginsAccessibleQtaccesswidgets());

        propertyHelper.setNewProperty((String) null, PLUGINS_CODECS_CNCODECS, decidePluginsCodecs(PLUGINS_CODECS_CNCODECS, "cncodecs"));
        propertyHelper.setNewProperty((String) null, PLUGINS_CODECS_JPCODECS, decidePluginsCodecs(PLUGINS_CODECS_JPCODECS, "jpcodecs"));
        propertyHelper.setNewProperty((String) null, PLUGINS_CODECS_KRCODECS, decidePluginsCodecs(PLUGINS_CODECS_KRCODECS, "krcodecs"));
        propertyHelper.setNewProperty((String) null, PLUGINS_CODECS_TWCODECS, decidePluginsCodecs(PLUGINS_CODECS_TWCODECS, "twcodecs"));

        propertyHelper.setNewProperty((String) null, PLUGINS_ICONENGINES_SVGICON, decidePluginsIconenginesSvgicon());

        // These are only detecting if the plugins exist for these modules,
        // lack of a plugin does not necessarily mean Qt doesn't have support
        // since the implementation might be statically linked in.
        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_GIF,  decidePluginsImageformatsGif());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_ICO,  decidePluginsImageformatsIco());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_JPEG, decidePluginsImageformatsJpeg());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_MNG,  decidePluginsImageformatsMng());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_PNG,  decidePluginsImageformatsPng());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_SVG,  decidePluginsImageformatsSvg());

        propertyHelper.setNewProperty((String) null, PLUGINS_IMAGEFORMATS_TIFF, decidePluginsImageformatsTiff());

        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLITE,     decidePluginsSqldriversSqlite());
        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLITE2,    decidePluginsSqldriversSqlite2());
        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLMYSQL,   decidePluginsSqldriversSqlmysql());
        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLODBC,    decidePluginsSqldriversSqlodbc());
        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLPSQL,    decidePluginsSqldriversSqlpsql());
        propertyHelper.setNewProperty((String) null, PLUGINS_SQLDRIVERS_SQLTDS,     decidePluginsSqldriversSqltds());

        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_LIBSTDC___6,     decideQtBinDso(PACKAGING_DSO_LIBSTDC___6,     "libstdc++-6"));
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_LIBGCC_S_DW2_1,  decideQtBinDso(PACKAGING_DSO_LIBGCC_S_DW2_1,  "libgcc_s_dw2-1"));
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_LIBGCC_S_SJLJ_1, decideQtBinDso(PACKAGING_DSO_LIBGCC_S_SJLJ_1, "libgcc_s_sjlj-1"));
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_MINGWM10,        decideQtBinDso(PACKAGING_DSO_MINGWM10,        "mingwm10"));


        String packagingDsoLibeay32 = decideQtLibDso(PACKAGING_DSO_LIBEAY32, "libeay32");
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_LIBEAY32, packagingDsoLibeay32);

        String packagingDsoLibssl32 = decideQtLibDso(PACKAGING_DSO_LIBSSL32, "libssl32", false);
        String packagingDsoSsleay32 = decideQtLibDso(PACKAGING_DSO_SSLEAY32, "ssleay32", false);
        // When building QtJambi against the offical Nokia Qt SDK they appear to provide duplicate
        // DLLs for the two naming variants libssl32.dll ssleay32.dll so we need to resolve this and
        // omit one.
        String packagingDsoLibssl32Message = "";
        String packagingDsoSsleay32Message = "";
        if("true".equals(packagingDsoLibssl32) && "true".equals(packagingDsoSsleay32)) {
            // FIXME: Compare the files are actually the same
            if(compiler == Compiler.GCC || compiler == Compiler.OldGCC || compiler == Compiler.MinGW || compiler == Compiler.MinGW_W64) {
                packagingDsoSsleay32 = "false";    // favour libssl32.dll
                packagingDsoSsleay32Message = " (was true; auto-inhibited)";
            } else {
                packagingDsoLibssl32 = "false";    // favour ssleay32.dll
                packagingDsoLibssl32Message = " (was true; auto-inhibited)";
            }
        }
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_LIBSSL32, packagingDsoLibssl32);
        if(verbose && (packagingDsoLibssl32Message.length() > 0 || "true".equals(packagingDsoLibssl32)))
            System.out.println(PACKAGING_DSO_LIBSSL32 + ": " + packagingDsoLibssl32 + packagingDsoLibssl32Message);

        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_SSLEAY32, packagingDsoSsleay32);
        if(verbose && (packagingDsoSsleay32Message.length() > 0 || "true".equals(packagingDsoSsleay32)))
            System.out.println(PACKAGING_DSO_SSLEAY32 + ": " + packagingDsoSsleay32 + packagingDsoSsleay32Message);

        String packagingDsoZlib1    = decideQtLibDso(PACKAGING_DSO_ZLIB1,    "zlib1");
        propertyHelper.setNewProperty((String) null, PACKAGING_DSO_ZLIB1,    packagingDsoZlib1);

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
            tmpQtVersion = (String) propertyHelper.getProperty(QT_VERSION);
            if(parseQtVersion(tmpQtVersion)) {
                versionFound = true;
                qtVersionSource = " (${" + QT_VERSION + "})";
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
                tmpQtVersion = (String) props.get(VERSION);
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
            String qmakeExe = (String) propertyHelper.getProperty("qmake.binary");
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
                String[] sA = Exec.executeCaptureOutput(qmakeArgs, fileDir, getProject(), null);
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
            tmpQtVersion = (String) propertyHelper.getProperty(VERSION);
            if(parseQtVersion(tmpQtVersion)) {
                versionFound = true;
                qtVersionSource = " (${" + VERSION + "})";
            }
        }

        return versionFound;
    }

    private boolean decideGeneratorPreProc() {
        List<String> generatorPreProcStageOneList = new ArrayList<String>();
        List<String> generatorPreProcStageTwoList = new ArrayList<String>();

        String compilerString = (String) propertyHelper.getProperty((String) null, COMPILER);
        if(compilerString == null)
            return false;

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
                generatorPreProcStageOneList.add("-D__GNUC__");
            }

        } else if(OSInfo.isLinux()) {
            generatorPreProcStageOneList.add("-D__unix__");
            generatorPreProcStageOneList.add("-D__linux__");
            generatorPreProcStageOneList.add("-D__GNUC__");
        } else if(OSInfo.isMacOS()) {
            generatorPreProcStageOneList.add("-D__APPLE__");
            // FIXME: When we detect an alternative compiler is in use (LLVM)
            generatorPreProcStageOneList.add("-D__GNUC__");
            // if(OSInfo.isMacOSX64())
            //     generatorPreProcStageOneList.add("-D__LP64__");
        } else if(OSInfo.isFreeBSD()) {
            generatorPreProcStageOneList.add("-D__unix__");
            generatorPreProcStageOneList.add("-D__FreeBSD__");
            generatorPreProcStageOneList.add("-D__GNUC__");
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
        String s = (String) propertyHelper.getProperty((String) null, JAVA_HOME_TARGET);
        if(s == null)
            s = System.getenv("JAVA_HOME_TARGET");
        if(s == null)
            s = System.getenv("JAVA_HOME");
        String result = s;
        propertyHelper.setProperty((String) null, "env.JAVA_HOME_TARGET", result, false);    //TODO: does this work?
        propertyHelper.setProperty("env", "JAVA_HOME_TARGET", result, false);    //TODO: does this work?
        if(verbose) System.out.println(JAVA_HOME_TARGET + ": " + result);
        return result;
    }

    private String decideJavaOsarchTarget() {
        String method = "";
        String s = (String) propertyHelper.getProperty((String) null, JAVA_OSARCH_TARGET);

        if(s == null) {
            s = System.getenv("JAVA_OSARCH_TARGET");
        }

        if(s == null) {    // auto-detect using what we find

            // This is based on a token observation that the include direcory
            //  only had one sub-directory (this is needed for jni_md.h).
            File includeDir = new File((String)propertyHelper.getProperty((String) null, JAVA_HOME_TARGET), "include");
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
                method = " (auto-detected)";
            }
        }

        String result = s;
        if(verbose) System.out.println(JAVA_OSARCH_TARGET + ": " + result + method);
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

        if(verbose) System.out.println(CONFIGURATION + ": " + result);
        return result;
    }

    private String doesQtLibExistDir(String librarydir, String name) {
        File dir = new File(librarydir, name);
        if(dir.exists() && dir.isDirectory())
            return dir.getAbsolutePath();
        return null;
    }

    private boolean doesQtLibExist(String name, int version, String librarydir) {
        StringBuilder path = new StringBuilder();
        path.append(librarydir);
        path.append(File.separator);
        path.append(LibraryEntry.formatQtName(name, debug, String.valueOf(version)));
        //System.out.println("Checking QtLib: " + path);
        return new File(path.toString()).exists();
    }

    private boolean doesQtLibExist(String name, int version) {
        return doesQtLibExist(name, version, propertyHelper.getProperty((String) null, LIBDIR).toString());
    }

    private boolean doesQtLibExist(String name, String librarydir) {
        StringBuilder path = new StringBuilder();

        if(librarydir != null) {
            path.append(librarydir);
        } else {
            path.append(propertyHelper.getProperty((String) null, LIBDIR).toString());
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
            path.append(propertyHelper.getProperty((String) null, BINDIR).toString());
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
            pluginsDirPropertyName = PHONONPLUGINSDIR;
        else
            pluginsDirPropertyName = PLUGINSDIR;
        String pluginsPath = (String) propertyHelper.getProperty((String) null, pluginsDirPropertyName);
        if(pluginsPath == null)
            return false;
        File filePluginsPath = new File(pluginsPath);
        if(filePluginsPath.isDirectory() == false)
            return false;
        path.append(pluginsPath);
        path.append(File.separator);
        path.append("plugins");
        path.append(File.separator);
        path.append(subdir);
        path.append(File.separator);

        //! TODO: useful?
        path.append(LibraryEntry.formatPluginName(name, noLibPrefix, debug, String.valueOf(qtMajorVersion)));
        return new File(path.toString()).exists();
    }

    private boolean doesQtPluginExist(String name, String subdir) {
        return doesQtPluginExist(name, subdir, false);
    }

    private String mySetProperty(PropertyHelper propertyHelper, int verboseMode, String attrName, String sourceValue, String newValue, boolean forceNewValue) {
        String currentValue = (String) propertyHelper.getProperty((String) null, attrName);
        if(newValue != null) {
            if(currentValue != null) {
                sourceValue = " (already set; detected as: " + newValue + ")";
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
            System.out.println(attrName + ": " + prettyCurrentValue + sourceValue);
        }

        return currentValue;
    }

    /**
     * Decide whether we have phonon plugin and check
     * correct phonon backend to use for this OS.
     */
    private String decidePhonon(PropertyHelper propertyHelper) {
        boolean exists = doesQtLibExist("phonon", qtMajorVersion, (String) propertyHelper.getProperty((String) null, PHONONLIBDIR));
        String result = String.valueOf(exists);

        result = mySetProperty(propertyHelper, -1, PHONON, " (auto-detected)", result, false);
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
        mySetProperty(propertyHelper, -1, PHONON_DS9, sourceValue, result, false);
        return result;
    }

    private String decidePluginsPhononBackendPhononGstreamer() {
        boolean exists;
        String result;
        String sourceValue = null;
        Boolean autodetectKdePhonon = null;

        String kdephonon = (String) propertyHelper.getProperty((String) null, QTJAMBI_PHONON_KDEPHONON);
        if(kdephonon != null && "true".equals(kdephonon)) {
            // build configuration states use KDE phonon
            exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend", true);  // auto-detect Kde phonon
            result = String.valueOf(exists);
            if(exists == false) {  // not found
                exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend");  // try for Qt phonon anyway?
                result = String.valueOf(exists);
                if(exists)  // found
                    sourceValue = " (WARNING auto-detected qt phonon; but " + QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon +"\"; so turning this off)";
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
                    sourceValue = " (auto-detected; WARNING " + QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon + "\"; so \"lib\" prefix removed)";
            } else {
                exists = doesQtPluginExist("phonon_gstreamer", "phonon_backend", true);  // try for KDE phonon anyway?
                result = String.valueOf(exists);
                if(exists) {
                    if(kdephonon == null)
                        sourceValue = " (auto-detected as kind:kdephonon)";
                    else
                        sourceValue = " (auto-detected kde phonon; but " + QTJAMBI_PHONON_KDEPHONON + "=\"" + kdephonon + "\"; so \"lib\" prefix removed)";
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
        mySetProperty(propertyHelper, -1, PHONON_GSTREAMER, sourceValue, result, false);
        if(autodetectKdePhonon != null && autodetectKdePhonon.booleanValue())
            mySetProperty(propertyHelper, -1, QTJAMBI_PHONON_KDEPHONON, " (auto-detected)", "true", false);
        return result;
    }

    private String decidePluginsPhononBackendPhononQt7() {
        boolean exists = doesQtPluginExist("phonon_qt7", "phonon_backend");
        String result = String.valueOf(exists);
        String sourceValue = null;
        if(exists)
            sourceValue = " (auto-detected)";
        else if(OSInfo.isWindows() == false)
            sourceValue = " (expected for non-MacOSX platform)";
        mySetProperty(propertyHelper, -1, PHONON_QT7, sourceValue, result, false);
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
        String oldConfig = (String) propertyHelper.getProperty((String) null, QTCONFIG);
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
            propertyHelper.setProperty((String) null, QTCONFIG, newConfig, false);
    }

    private String decideSql() {
        String result = String.valueOf(doesQtLibExist("QtSql", qtMajorVersion));
        if(verbose) System.out.println(SQL + ": " + result);
        if("true".equals(result)) addToQtConfig("sql");
        return result;
    }

    private String decideSvg() {
        String result = String.valueOf(doesQtLibExist("QtSvg", qtMajorVersion));
        if(verbose) System.out.println(SVG + ": " + result);
        if("true".equals(result)) addToQtConfig("svg");
        return result;
    }

    private String decideTest() {
        String result = String.valueOf(doesQtLibExist("QtTest", qtMajorVersion));
        if(verbose) System.out.println(TEST + ": " + result);
        if("true".equals(result)) addToQtConfig("qtestlib");
        return result;
    }

    private String decidePluginsAccessibleQtaccesswidgets() {
        String result = String.valueOf(doesQtPluginExist("qtaccessiblewidgets", "accessible"));
        if(verbose) System.out.println(PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS + ": " + result);
        return result;
    }

    private String decidePluginsCodecs(String attrName, String name) {
        String result = String.valueOf(doesQtPluginExist("q" + name, "codecs"));
        if(verbose) System.out.println(attrName + ": " + result);
        return result;
    }

    private String decidePluginsIconenginesSvgicon(){
        String result = String.valueOf(doesQtPluginExist("qsvgicon", "iconengines"));
        if(verbose) System.out.println(PLUGINS_ICONENGINES_SVGICON + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsGif(){
        String result = String.valueOf(doesQtPluginExist("qgif", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_GIF + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsIco(){
        String result = String.valueOf(doesQtPluginExist("qico", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_ICO + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsJpeg(){
        String result = String.valueOf(doesQtPluginExist("qjpeg", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_JPEG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsMng(){
        String result = String.valueOf(doesQtPluginExist("qmng", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_MNG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsPng(){
        String result = String.valueOf(doesQtPluginExist("qpng", "imageformats"));
        String extra = "";
        if("false".equals(result))
            extra = " (probably a built-in)";
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_PNG + ": " + result + extra);
        return result;
    }

    private String decidePluginsImageformatsSvg(){
        String result = String.valueOf(doesQtPluginExist("qsvg", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_SVG + ": " + result);
        return result;
    }

    private String decidePluginsImageformatsTiff() {
        String result = String.valueOf(doesQtPluginExist("qtiff", "imageformats"));
        if(verbose) System.out.println(PLUGINS_IMAGEFORMATS_TIFF + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlite() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlite", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLITE + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlite2() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlite2", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLITE2 + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlmysql() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlmysql", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLMYSQL + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlodbc() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlodbc", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLODBC + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqlpsql() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqlpsql", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLPSQL + ": " + result);
        return result;
    }

    private String decidePluginsSqldriversSqltds() {
        // FIXME: Detect the case when this module was compiled into QtSql
        String result = String.valueOf(doesQtPluginExist("qsqltds", "sqldrivers"));
        if(verbose) System.out.println(PLUGINS_SQLDRIVERS_SQLTDS + ": " + result);
        return result;
    }

    private String decideCore() {
        String result = String.valueOf(doesQtLibExist("QtCore", qtMajorVersion));
        if(verbose) System.out.println(CORE + ": " + result);
        if("true".equals(result)) addToQtConfig("core");
        return result;
    }

    private String decideCLucene() {
        String result = String.valueOf(doesQtLibExist("QtCLucene", qtMajorVersion));
        if(verbose) System.out.println(CLUCENE + ": " + result);
        return result;
    }

    private String decideDBus() {
        String result = String.valueOf(doesQtLibExist("QtDBus", qtMajorVersion));
        if(verbose) System.out.println(DBUS + ": " + result);
        if("true".equals(result)) addToQtConfig("dbus");
        return result;
    }

    private String decideDeclarative() {
        String result = String.valueOf(doesQtLibExist("QtDeclarative", qtMajorVersion));
        if(verbose) System.out.println(DECLARATIVE + ": " + result);
        if("true".equals(result)) addToQtConfig("declarative");
        return result;
    }

    private String decideDesigner() {
        String result = String.valueOf(doesQtLibExist("QtDesigner", qtMajorVersion));
        if(verbose) System.out.println(DESIGNER + ": " + result);
        if("true".equals(result)) addToQtConfig("designer");
        return result;
    }

    private String decideDesignerComponents() {
        String result = String.valueOf(doesQtLibExist("QtDesignerComponents", qtMajorVersion));
        if(verbose) System.out.println(DESIGNERCOMPONENTS + ": " + result);
        return result;
    }

    private String decideGui() {
        String result = String.valueOf(doesQtLibExist("QtGui", qtMajorVersion));
        if(verbose) System.out.println(GUI + ": " + result);
        if("true".equals(result)) addToQtConfig("gui");
        return result;
    }

    private String decideHelp() {
        String result = String.valueOf(doesQtLibExist("QtHelp", qtMajorVersion));
        if(verbose) System.out.println(HELP + ": " + result);
        if("true".equals(result)) addToQtConfig("help");
        return result;
    }

    private String decideMultimedia() {
        String result = String.valueOf(doesQtLibExist("QtMultimedia", qtMajorVersion));
        if(verbose) System.out.println(MULTIMEDIA + ": " + result);
        if("true".equals(result)) addToQtConfig("multimedia");
        return result;
    }

    private String decideNetwork() {
        String result = String.valueOf(doesQtLibExist("QtNetwork", qtMajorVersion));
        if(verbose) System.out.println(NETWORK + ": " + result);
        if("true".equals(result)) addToQtConfig("network");
        return result;
    }

    private String decideOpenGL() {
        String result = String.valueOf(doesQtLibExist("QtOpenGL", qtMajorVersion));
        if(verbose) System.out.println(OPENGL + ": " + result);
        if("true".equals(result)) addToQtConfig("opengl");
        return result;
    }

    private String decideScript() {
        String result = String.valueOf(doesQtLibExist("QtScript", qtMajorVersion));
        if(verbose) System.out.println(SCRIPT + ": " + result);
        if("true".equals(result)) addToQtConfig("script");
        return result;
    }

    private String decideScripttools() {
        String result = String.valueOf(doesQtLibExist("QtScriptTools", qtMajorVersion));
        if(verbose) System.out.println(SCRIPTTOOLS + ": " + result);
        if("true".equals(result)) addToQtConfig("scripttools");
        return result;
    }

    private String decideWebkit() {
        String result = String.valueOf(doesQtLibExist("QtWebKit", qtMajorVersion));
        if(verbose) System.out.println(WEBKIT + ": " + result);
        if("true".equals(result)) addToQtConfig("webkit");
        return result;
    }

    private String decideXml() {
        String result = String.valueOf(doesQtLibExist("QtXml", qtMajorVersion));
        if(verbose) System.out.println(XML + ": " + result);
        if("true".equals(result)) addToQtConfig("xml");
        return result;
    }

    private String decideXmlPatterns() {
        String result = String.valueOf(doesQtLibExist("QtXmlPatterns", qtMajorVersion));
        if(verbose) System.out.println(XMLPATTERNS + ": " + result);
        if("true".equals(result)) addToQtConfig("xmlpatterns");
        return result;
    }

    private String decideQtLibDso(String attrName, String name, boolean verboseFlag) {
        boolean bf = doesQtLibExist(name, null);
        String result = String.valueOf(bf);
        if(verboseFlag && bf) System.out.println(attrName + ": " + result);
        return result;
    }

    private String decideQtLibDso(String attrName, String name) {
        return decideQtLibDso(attrName, name, verbose);
    }

    private String decideQtBinDso(String attrName, String name) {
        boolean bf = doesQtBinExist(name, null);
        String result = String.valueOf(bf);
        if(verbose && bf) System.out.println(attrName + ": " + result);
        return result;
    }

}
