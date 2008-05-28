/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qt;

import java.io.*;
import java.net.*;
import java.util.*;

// !!NOTE!! This class can have no dependencies on Qt since
//          it is used by the NativeLibraryManager
import com.trolltech.qt.internal.RetroTranslatorHelper;
import com.trolltech.qt.internal.Version;
import com.trolltech.qt.internal.NativeLibraryManager;

/**
This class contains static members that gives information and performs Qt Jambi
related tasks.
*/
public class Utilities {
    /** The Qt Library's major version. */
    public static final int MAJOR_VERSION = Version.MAJOR;

    /** The Qt Library's minor version. */
    public static final int MINOR_VERSION = Version.MINOR;

    /** The Qt Library's patch version. */
    public static final int PATCH_VERSION = Version.PATCH;

    /** Qt Library build number */
    public static final int BUILD_NUMBER = Version.BUILD;

    /** A formated String with versioning*/
    public static final String VERSION_STRING = Version.STRING;

    /** Enum for defining the operation system. */
    public enum OperatingSystem {
    /** Windows */
        Windows,
    /** MacOSX */
        MacOSX,
    /** Linux */
        Linux
    }

    /** Defines whether Qt is build in Release or Debug. */
    public enum Configuration {
    /** Release build. */
        Release,
    /** Debug build. */
        Debug
    }

    /** The operating system Qt Jambi is running on. */
    public static OperatingSystem operatingSystem = decideOperatingSystem();
    /** The configuration of Qt Jambi. */
    public static Configuration configuration = decideConfiguration();

    /** Whether Qt Jambi has implicit loading.
        This variable is no longer in use...
     */
    @Deprecated
    public static boolean implicitLoading = false;

    /** The library sub path. */
    public static String libSubPath = decideLibSubPath();

    /** Whether Qt Jambi should prefer to load libraries from its cache.
        This variable is no longer in use...
     */
    @Deprecated
    public static boolean loadFromCache = matchProperty("com.trolltech.qt.load-from-cache", "true");

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
    }

    public static void loadQtLibrary(String library) {
        loadQtLibrary(library, "4");
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
        return OperatingSystem.Linux;
    }


    private static Configuration decideConfiguration() {
        if (System.getProperty("com.trolltech.qt.debug") != null)
            return Configuration.Debug;
        return Configuration.Release;
    }

    private static String decideLibSubPath() {
        return operatingSystem == OperatingSystem.Windows
                                ? "bin"
                                : "lib";
    }


    private static String stripLibraryName(String lib) {
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
    }

    public static String unpackPlugins() {
        return null;
    }
}
