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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
    public static final String QTJAMBI_SONAME_VERSION_MAJOR;
    // We use a List<> to make the collection read-only an array would not be suitable
    private static final List<String> systemLibrariesList;
    private static final List<String> jniLibdirBeforeList;
    private static final List<String> jniLibdirList;

    private static final String K_qtjambi_version              = "qtjambi.version";
    private static final String K_qtjambi_soname_version_major = "qtjambi.soname.version.major";
    private static final String K_qtjambi_system_libraries     = "qtjambi.system.libraries";
    private static final String K_qtjambi_jni_libdir_before    = "qtjambi.jni.libdir.before";
    private static final String K_qtjambi_jni_libdir           = "qtjambi.jni.libdir";  // implicit meaning of "after"
    private static final Configuration DEFAULT_CONFIGURATION = Configuration.Release;

    public static final String K_Bundle_SymbolicName = "Bundle-SymbolicName";
    public static final String K_X_QtJambi_Build     = "X-QtJambi-Build";
    public static final String K_com_trolltech_qt    = "com.trolltech.qt";
    public static final String K_debug               = "debug";
    public static final String K_test                = "test";
    public static final String K_release             = "release";

    static {
        String tmpVERSION_STRING = null;
        String tmpVERSION_MAJOR_STRING = null;
        String tmpQTJAMBI_SONAME_VERSION_MAJOR = null;
        List<String> tmpSystemLibrariesList = null;
        List<String> tmpJniLibdirBeforeList = null;
        List<String> tmpJniLibdirList = null;
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
            tmpVERSION_STRING = props.getProperty(K_qtjambi_version);
            if (tmpVERSION_STRING == null)
                throw new ExceptionInInitializerError(K_qtjambi_version + " is not set!");

            int dotIndex = tmpVERSION_STRING.indexOf(".");	// "4.7.4" => "4"
            if(dotIndex > 0)	// don't allow setting it be empty
                tmpVERSION_MAJOR_STRING = tmpVERSION_STRING.substring(0, dotIndex);
            else
                tmpVERSION_MAJOR_STRING = tmpVERSION_STRING;

            tmpQTJAMBI_SONAME_VERSION_MAJOR = props.getProperty(K_qtjambi_soname_version_major);

            SortedMap<String,String> tmpSystemLibrariesMap = new TreeMap<String,String>();
            SortedMap<String,String> tmpJniLibdirBeforeMap = new TreeMap<String,String>();
            SortedMap<String,String> tmpJniLibdirMap = new TreeMap<String,String>();
            Enumeration<? extends Object> e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = props.getProperty(key);
                if (key.equals(K_qtjambi_system_libraries) || key.startsWith(K_qtjambi_system_libraries + ".")) {
                    tmpSystemLibrariesMap.put(key, value);
                } else if(key.equals(K_qtjambi_jni_libdir_before) || key.startsWith(K_qtjambi_jni_libdir_before + ".")) {
                    tmpJniLibdirBeforeMap.put(key, value);
                } else if(key.equals(K_qtjambi_jni_libdir) || key.startsWith(K_qtjambi_jni_libdir + ".")) {
                   tmpJniLibdirMap.put(key, value);
                }
            }
            // Map will automatically sort the lists { "", ".0", ".01", ".1", ".10", ".2", ".A", ".a" }
            tmpSystemLibrariesList = new ArrayList<String>();
            for (String v : tmpSystemLibrariesMap.values())
                tmpSystemLibrariesList.add(v);

            if (tmpSystemLibrariesList.size() > 0)
                tmpSystemLibrariesList = Collections.unmodifiableList(tmpSystemLibrariesList);
            else
                tmpSystemLibrariesList = null;

            tmpJniLibdirBeforeList = new ArrayList<String>();
            for (String v : tmpJniLibdirBeforeMap.values())
                tmpJniLibdirBeforeList.add(v);

            if (tmpJniLibdirBeforeList.size() > 0)
                tmpJniLibdirBeforeList = Collections.unmodifiableList(tmpJniLibdirBeforeList);
            else
                tmpJniLibdirBeforeList = null;

            tmpJniLibdirList = new ArrayList<String>();
            for (String v : tmpJniLibdirMap.values())
                tmpJniLibdirList.add(v);

            if (tmpJniLibdirList.size() > 0)
                tmpJniLibdirList = Collections.unmodifiableList(tmpJniLibdirList);
            else
                tmpJniLibdirList = null;
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {
            VERSION_STRING = tmpVERSION_STRING;
            VERSION_MAJOR_STRING = tmpVERSION_MAJOR_STRING;
            QTJAMBI_SONAME_VERSION_MAJOR = tmpQTJAMBI_SONAME_VERSION_MAJOR;
            systemLibrariesList = tmpSystemLibrariesList;
            jniLibdirBeforeList = tmpJniLibdirBeforeList;
            jniLibdirList = tmpJniLibdirList;
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
        Debug,
        Test
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

    public static String[] mergeJniLibdir(String[] middle) {
        List<String> newList = new ArrayList<String>();

        if(jniLibdirBeforeList != null)
            newList.addAll(jniLibdirBeforeList);
        if(middle != null) {
            for(String s : middle)
                newList.add(s);
        }
        if(jniLibdirList != null)
            newList.addAll(jniLibdirList);

        if(newList.size() == 0)
            return middle;   // maybe null or empty
        return newList.toArray(new String[newList.size()]);
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
        Configuration configuration = null;

        final String K_com_trolltech_qt_debug = "com.trolltech.qt.debug";
        String debugString = System.getProperty(K_com_trolltech_qt_debug);
        try {
            if(debugString != null) {
                Boolean booleanValue = Boolean.valueOf(debugString);
                if((booleanValue != null && booleanValue.booleanValue()) || debugString.length() == 0) {
                    configuration = Configuration.Debug;
                    // FIXME: When we can unambigiously auto-detect this from the MANIFEST we don't need to
                    //  emit this, we'd only emit it when both non-debug and debug were found and we selected
                    //  the debug kind.
                    System.err.println("-D" + K_com_trolltech_qt_debug + "=" + Boolean.TRUE + "; is set");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            // only because Configuration.Release is assumed
            System.err.println("-D" + K_com_trolltech_qt_debug + "=" + Boolean.FALSE + "; is assumed default");
        }

        if(configuration == null)
            configuration = decideDefaultConfiguration();

        if(configuration == null)
            configuration = DEFAULT_CONFIGURATION;

        return configuration;
    }

    private static Configuration decideDefaultConfiguration() {
        Configuration configuration = null;
        try {
            Enumeration<URL> enumUrls = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
            while(enumUrls.hasMoreElements()) {
                URL url = enumUrls.nextElement();
                InputStream inStream = null;
                try {
                    URLConnection urlConnection = url.openConnection();
                    inStream = urlConnection.getInputStream();
                    Manifest manifest = new Manifest(inStream);
                    inStream.close();
                    inStream = null;

                    Attributes attributes = manifest.getMainAttributes();
                    String tmpBundleSymbolicName = attributes.getValue(K_Bundle_SymbolicName);
                    String tmpXQtJambiBuild = attributes.getValue(K_X_QtJambi_Build);

                    Configuration tmpConfiguration = null;
                    if(K_com_trolltech_qt.equals(tmpBundleSymbolicName)) {
                        // We found the right bundle
                        if(K_release.equals(tmpXQtJambiBuild)) {
                            tmpConfiguration = Configuration.Release;
                        } else if(K_debug.equals(tmpXQtJambiBuild)) {
                            tmpConfiguration = Configuration.Debug;
                        } else if(K_test.equals(tmpXQtJambiBuild)) {
                            tmpConfiguration = Configuration.Test;
                        } else {
                            if(tmpXQtJambiBuild == null)
                                tmpXQtJambiBuild = "<notset>";
                            System.out.println("com.trolltech.qt.Utilities#decideDefaultConfiguration()  " + url.toString() + " invalid " + K_X_QtJambi_Build + ": " + tmpXQtJambiBuild);
                        }

                        // We keep checking them all
                        // If we find 2 matches this is a failure case right now, until we have resolution strategy implemented
                        if(configuration != null) {
                            // Multiple matches, ah well...
                            configuration = null;
                        } else {
                            configuration = tmpConfiguration;  // found
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    if(inStream != null) {
                        try {
                            inStream.close();
                        } catch(IOException eat) {
                        }
                        inStream = null;
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(SecurityException e) {
            e.printStackTrace();
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
