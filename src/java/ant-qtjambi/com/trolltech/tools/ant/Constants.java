/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
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
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

public abstract class Constants {

    public static final String DEFAULT_QTJAMBI_SONAME_VERSION_MAJOR = "1";

    /*
     * These properties are set outside of this task
     *
     * TODO: These flags should be documented here and if possibly, outside in
     * build documentation.
     * Or rather these binds shouldn't exist, how much of this could be moved to
     * xml side?
     */
    public static final String DIRECTORY                = "jambi.directory";
    public static final String DIRECTORY_ABSPATH        = "jambi.directory.abspath";
    public static final String QMAKE                    = "qtjambi.qt.qmake";
    public static final String QMAKE_ABSPATH            = "qtjambi.qt.qmake.abspath";
    public static final String BINDIR                   = "qtjambi.qt.bindir";
    public static final String LIBDIR                   = "qtjambi.qt.libdir";
    public static final String INCLUDEDIR               = "qtjambi.qt.includedir";
    public static final String PLUGINSDIR               = "qtjambi.qt.pluginsdir";
    public static final String GENERATOR_PREPROC_STAGE1 = "qtjambi.generator.preproc.stage1";
    public static final String GENERATOR_PREPROC_STAGE2 = "qtjambi.generator.preproc.stage2";
    public static final String JAVALIBDIR               = "qtjambi.java.library.path";
    public static final String JAMBILIBDIR              = "qtjambi.jambi.libdir";
    public static final String JAMBIPLUGINSDIR          = "qtjambi.jambi.pluginsdir";
    public static final String CACHEKEY                 = "qtjambi.version.cachekey";
    public static final String VERSION                  = "qtjambi.version";
    public static final String BUNDLE_VERSION           = "qtjambi.version.bundle";
    public static final String BUNDLE_VERSION_MODE      = "qtjambi.version.bundle.mode";
    public static final String SUFFIX_VERSION           = "qtjambi.version.suffix";
    public static final String JAVA_HOME_TARGET         = "java.home.target"; // build
    public static final String JAVA_OSARCH_TARGET       = "java.osarch.target"; // target
    public static final String JAVA_OSCPU               = "java.oscpu"; // build
    public static final String JAVA_OSCPU_TARGET        = "java.oscpu.target"; // target
    public static final String EXEC_STRIP               = "exec.strip";

    public static final String QT_VERSION_MAJOR         = "qt.version.major";
    public static final String QT_VERSION_MINOR         = "qt.version.minor";
    public static final String QT_VERSION_PATCHLEVEL    = "qt.version.patchlevel";
    public static final String QT_VERSION               = "qt.version";

    public static final String QT_VERSION_MAJOR_NEXT    = "qt.version.major.next";
    public static final String QT_VERSION_MINOR_NEXT    = "qt.version.minor.next";

    public static final String QT_VERSION_PROPERTIES          = "version.properties";
    public static final String QT_VERSION_PROPERTIES_TEMPLATE = "version.properties.template";

    public static final String CONFIG_RELEASE           = "release";
    public static final String CONFIG_DEBUG             = "debug";
    public static final String CONFIG_TEST              = "test";
    public static final String CONFIG_DEBUG_AND_RELEASE = "debug_and_release";

    public static final String QMAKE_TARGET_DEFAULT     = "qtjambi.qmake.target.default";
    public static final String GENERATOR_INCLUDEPATHS   = "generator.includepaths";

    // Cross compiling, for example (host = intel x86, target = arm)
    public static final String TOOLS_BINDIR             = "tools.qt.bindir";
    public static final String TOOLS_LIBDIR             = "tools.qt.libdir";
    public static final String TOOLS_QMAKE              = "tools.qt.qmake";
    public static final String TOOLS_QMAKE_ABSPATH      = "tools.qt.qmake.abspath";

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
    public static final String CONFIGURATION_DASH = "qtjambi.configuration.dash";
    public static final String CONFIGURATION_OSGI = "qtjambi.configuration.osgi";
    public static final String CORE               = "qtjambi.core";         // mandatory with <= 4.7.x
    public static final String DBUS               = "qtjambi.dbus";
    public static final String DECLARATIVE        = "qtjambi.declarative";
    public static final String DESIGNER           = "qtjambi.designer";
    public static final String DESIGNERCOMPONENTS = "qtjambi.designercomponents";
    public static final String GUI                = "qtjambi.gui";          // mandatory with <= 4.7.x
    public static final String HELP               = "qtjambi.help";
    public static final String MULTIMEDIA         = "qtjambi.multimedia";
    public static final String NETWORK            = "qtjambi.network";      // mandatory with <= 4.7.x
    public static final String OPENGL             = "qtjambi.opengl";
    public static final String OSNAME             = "qtjambi.osname";
    public static final String OSPLATFORM         = "qtjambi.osplatform";   // linux windows macosx
    public static final String OSCPU              = "qtjambi.oscpu";        // i386 x86_64 x86 x32
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
    public static final String XML                = "qtjambi.xml";          // mandatory with <= 4.7.x
    public static final String XMLPATTERNS        = "qtjambi.xmlpatterns";
    public static final String QTCONFIG           = "qtjambi.qtconfig";

    public static final String PLUGINS_ACCESSIBLE_QTACCESSIBLEWIDGETS  = "qtjambi.plugins.accessible.qtaccessiblewidgets";

    public static final String QTJAMBI_PHONON_KDEPHONON           = "qtjambi.phonon.kdephonon";
    public static final String QTJAMBI_PHONON_INCLUDEDIR          = "qtjambi.phonon.includedir";
    public static final String QTJAMBI_PHONON_LIBDIR              = "qtjambi.phonon.libdir";
    public static final String QTJAMBI_PHONON_PLUGINSDIR          = "qtjambi.phonon.pluginsdir";
    public static final String QTJAMBI_PHONON_KDEPHONON_PATH      = "qtjambi.phonon.kdephonon-path";

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

    public static final String PLUGINS_SCRIPT_QTSCRIPTDBUS = "qtjambi.plugins.script.qtscriptdbus";

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

    public static final String PACKAGING_DSO_LIBZ      = "qtjambi.packaging.dso.libz";       // Linux
    public static final String PACKAGING_DSO_LIBSSL    = "qtjambi.packaging.dso.libssl";     // Linux
    public static final String PACKAGING_DSO_LIBCRYPTO = "qtjambi.packaging.dso.libcrypto";  // Linux

    public static final String PACKAGING_DSO_ZLIB1    = "qtjambi.packaging.dso.zlib1";      // Windows
    public static final String PACKAGING_DSO_LIBSSL32 = "qtjambi.packaging.dso.libssl32";   // Windows MinGW
    public static final String PACKAGING_DSO_SSLEAY32 = "qtjambi.packaging.dso.ssleay32";   // Windows MSVC
    public static final String PACKAGING_DSO_LIBEAY32 = "qtjambi.packaging.dso.libeay32";   // Windows

    public static final String PACKAGING_DSO_LIBDBUS  = "qtjambi.packaging.dso.libdbus-1";   // Macosx

    public static final String PACKAGING_DSO_CPLUSPLUSRUNTIME = "qtjambi.packaging.dso.cplusplusruntime";

    public static final String QTJAMBI_CONFIG_ISMACOSX      = "qtjambi.config.ismacosx";
    public static final String QTJAMBI_MACOSX_QTMENUNIB_DIR = "qtjambi.macosx.qtmenunib.dir";
    public static final String QTJAMBI_MACOSX_MAC_SDK       = "qtjambi.macosx.macsdk";

    // Windows specific vars...
    public static final String VSINSTALLDIR        = "qtjambi.vsinstalldir";
    public static final String VSREDISTDIR         = "qtjambi.vsredistdir";
    public static final String VSREDISTDIR_PACKAGE = "qtjambi.vsredistdir.package";

    public static final String QTJAMBI_DEBUG_TOOLS            = "qtjambi.debug-tools";
    public static final String QTJAMBI_DEBUG_REFTYPE          = "qtjambi.debug-reftype";
    public static final String QTJAMBI_DEBUG_LOCALREF_CLEANUP = "qtjambi.debug-localref-cleanup";
    public static final String QTJAMBI_QREALTYPE              = "qtjambi.qrealtype";

    // Initialize these to empty string if unset.
    public static final String QTJAMBI_CONFIG_JUMPTABLE    = "qtjambi.config.jumptable";
    public static final String QTJAMBI_GENERATOR_JUMPTABLE = "qtjambi.generator.jumptable";
}
