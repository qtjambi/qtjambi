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

package com.trolltech.qt;

import java.io.*;
import java.util.*;

// !!NOTE!! This class can have no dependencies on Qt since
//          it is used by the NativeLibraryManager
import com.trolltech.qt.internal.NativeLibraryManager;

/**
This class contains static members that gives information and performs Qt Jambi
related tasks.
*/
public class Utilities {

    public static final String VERSION_STRING;
    public static final String VERSION_MAJOR_STRING;
    private static final List<String> systemLibrariesList;

    private static final String K_qtjambi_system_libraries = "qtjambi.system.libraries";
    private static final Configuration DEFAULT_CONFIGURATION = Configuration.Release;

    static {
        String tmpVERSION_STRING = null;
        String tmpVERSION_MAJOR_STRING = null;
        List<String> tmpSystemLibrariesList = null;
        try {
            final Properties props = new Properties();
            final ClassLoader loader = Utilities.class.getClassLoader();
            if (loader == null)
                throw new ExceptionInInitializerError("Could not get classloader!");
            final InputStream in = loader.getResourceAsStream("com/trolltech/qt/version.properties");
            if (in == null)
                throw new ExceptionInInitializerError("version.properties not found!");
            try {
                props.load(in);
            } catch (Exception ex) {
                throw new ExceptionInInitializerError("Cannot read properties!");
            }
            tmpVERSION_STRING = props.getProperty("qtjambi.version");
            if (tmpVERSION_STRING == null)
                throw new ExceptionInInitializerError("qtjambi.version is not set!");

            int dotIndex = tmpVERSION_STRING.indexOf(".");	// "4.7.4" => "4"
            if(dotIndex > 0)	// don't allow setting it be empty
                tmpVERSION_MAJOR_STRING = tmpVERSION_STRING.substring(0, dotIndex);
            else
                tmpVERSION_MAJOR_STRING = tmpVERSION_STRING;

            SortedMap<String,String> tmpSystemLibrariesMap = new TreeMap<String,String>();
            Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = props.getProperty(key);
                if (key.equals(K_qtjambi_system_libraries) || key.startsWith(K_qtjambi_system_libraries + ".")) {
                    tmpSystemLibrariesMap.put(key, value);
                }
            }
            // Sort the list { "", ".0", ".01", ".1", ".10", ".2", ".A", ".a" }
            tmpSystemLibrariesList = new ArrayList<String>();
            for (String v : tmpSystemLibrariesMap.values())
                tmpSystemLibrariesList.add(v);

            if (tmpSystemLibrariesList.size() > 0)
                tmpSystemLibrariesList = Collections.unmodifiableList(tmpSystemLibrariesList);
            else
                tmpSystemLibrariesList = null;
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {
            VERSION_STRING = tmpVERSION_STRING;
            VERSION_MAJOR_STRING = tmpVERSION_MAJOR_STRING;
            systemLibrariesList = tmpSystemLibrariesList;
        }
    }

    /** Enum for defining the operation system. */
    public enum OperatingSystem {
        Windows,
        MacOSX,
        Linux,
        FreeBSD,
        SunOS
    }

    /** Defines whether Qt is build in Release or Debug. */
    public enum Configuration {
        Release,
        Debug
    }

    /** The operating system Qt Jambi is running on. */
    public static OperatingSystem operatingSystem = decideOperatingSystem();

    /** The configuration of Qt Jambi. */
    public static Configuration configuration = decideConfiguration();

    /** The library sub path. */
    public static String libSubPath = decideLibSubPath();

    /**
     * Returns true if the system property name contains any of the specified
     * substrings. If substrings is null or empty the function returns true
     * if the  value is non-null.
     */
    public static boolean matchProperty(String name, String ... substrings) {
        String value = System.getProperty(name);
        if (value == null)
            return false;
        if (substrings == null || substrings.length == 0)
            return value != null;
        for (String s : substrings)
            if (value.contains(s))
                return true;
        return false;
    }

    public static void loadSystemLibraries() {
        if (systemLibrariesList != null) {
            for (String s : systemLibrariesList) {
                // FIXME: We want only append the suffix (no prefix, no Qt version, no debug extra)
                //  currently is does add a prefix (maybe also debug extra).
                loadLibrary(s);
            }
        }
    }

    public static void loadQtLibrary(String library) {
        loadQtLibrary(library, VERSION_MAJOR_STRING);
    }

    public static void loadQtLibrary(String library, String version) {
        NativeLibraryManager.loadQtLibrary(library, version);
    }

    public static void loadJambiLibrary(String library) {
        NativeLibraryManager.loadLibrary(library);
    }

    public static boolean loadLibrary(String lib) {
        try {
            NativeLibraryManager.loadLibrary(lib);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File jambiTempDir() {
        return NativeLibraryManager.jambiTempDirBase("");
    }

    private static OperatingSystem decideOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) return OperatingSystem.Windows;
        if (osName.startsWith("mac os x")) return OperatingSystem.MacOSX;
        if (osName.startsWith("freebsd")) return OperatingSystem.FreeBSD;
        if (osName.equals("sunos")) return OperatingSystem.SunOS;	// SunOS
        return OperatingSystem.Linux;
    }

    private static Configuration decideConfiguration() {
        final String K_com_trolltech_qt_debug = "com.trolltech.qt.debug";
        Configuration configuration = DEFAULT_CONFIGURATION;
        String debugString = System.getProperty(K_com_trolltech_qt_debug);
        try {
            if(debugString != null) {
                Boolean booleanValue = Boolean.valueOf(debugString);
                if((booleanValue != null && booleanValue.booleanValue()) || debugString.length() == 0) {
                    configuration = Configuration.Debug;
                    // FIXME: When we can unambigiously auto-detect this from the MANIFEST we don't need to
                    //  emit this, we'd only emit it when both non-debug and debug were found and we selected
                    //  the debug kind.
                    System.err.println("Using: -D" + K_com_trolltech_qt_debug + "=" + Boolean.TRUE);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Using: -D" + K_com_trolltech_qt_debug + "=" + Boolean.FALSE);
        }
        return configuration;
    }

    private static String decideLibSubPath() {
        return operatingSystem == OperatingSystem.Windows
                                ? "bin"
                                : "lib";
    }


    //not used
    /*private static String stripLibraryName(String lib) {
        // Strip away "lib" prefix
        if (operatingSystem != OperatingSystem.Windows)
            lib = lib.substring(3);

        int dot = -1;

        switch (operatingSystem) {
        case Windows:
            dot = lib.indexOf(".dll");
            break;
        case Linux:
            dot = lib.indexOf(".so");
            break;
        case MacOSX:
            dot = lib.indexOf("."); // makes a fair attemt at matching /.[0-9]*.(jni)|(dy)lib/
            break;
        }

        // Strip away the library postfix...
        return lib.substring(0, dot);
    }*/

    public static String unpackPlugins() {
        return null;
    }
}
