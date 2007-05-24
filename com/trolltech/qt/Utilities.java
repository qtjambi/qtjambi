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
import java.util.jar.*;

public class Utilities {

	public static final int MAJOR_VERSION = 4;
    public static final int MINOR_VERSION = 3;
    public static final int PATCH_VERSION = 0;

    public static final int BUILD_NUMBER = 1;

    public static final String VERSION_STRING = String.format("%1$d.%2$d.%3$d_%4$02d",
            MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION, BUILD_NUMBER);
    
    public enum OperatingSystem {
        Windows,
        MacOSX,
        Linux
    };

    public enum Configuration {
        Release,
        Debug
    };

    public static OperatingSystem operatingSystem = decideOperatingSystem();
    public static Configuration configuration = decideConfiguration();
    public static boolean implicitLoading = !matchProperty("com.trolltech.qt.implicit-loading", "false");
    public static String libSubPath = decideLibSubPath();

    private static final String DEBUG_SUFFIX = "_debuglib";

    private static final boolean VERBOSE_LOADING =
        System.getProperty("com.trolltech.qt.verbose-loading") != null;

    private static String EXCLUDE_STRING = "com.trolltech.qt.exclude-libraries";

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
        List<String> libs = readSystemLibraries();
        for (String s : libs) {
            loadLibrary(s);
        }
    }

    public static void loadQtLibrary(String library) {
    	String excludeLibraries = System.getProperty(EXCLUDE_STRING);
    	if (excludeLibraries != null) {
            StringTokenizer tokenizer = new StringTokenizer(excludeLibraries,
                                                            File.pathSeparator);
            while (tokenizer.hasMoreElements()) {
                if (library.equals(tokenizer.nextElement())) {
                    if (VERBOSE_LOADING) {
                        System.out.println("Skipped library (" + library
                                           + ") since it is listed in "
                                           + excludeLibraries);
                    }
                    return;
                }
            }
    	}
        String lib = qtLibraryName(library);
        loadLibrary(lib);
    }

    public static void loadJambiLibrary(String library) {
    	if (configuration == Configuration.Debug)
            library += DEBUG_SUFFIX;
    	String lib = jniLibraryName(library);
    	loadLibrary(lib);
    }

    private static boolean loadFromEnv(String env, String lib){
        try {
            String envPath = System.getProperty(env);
            if (envPath != null) {
                String envPaths[] = envPath.split(File.pathSeparator);
                for (String path : envPaths) {
                    File f = new File(path, lib);

                    if (f.exists()) {
                        Runtime.getRuntime().load(f.getAbsolutePath());
                        if (VERBOSE_LOADING)
                            System.out.println("Loaded(" + lib + ") using java env: " + env);
                        return true;
                    }
                }
                if (VERBOSE_LOADING && envPath.length() > 0) {
                    System.out.println("Failed to find " + lib + " in " + envPath);
                }
            }
        } catch (Throwable e) {
            if (VERBOSE_LOADING) System.out.println("Failed to load " + lib + " from " + env);
            return false;
        }
        return false;
    }


    public static boolean loadLibrary(String lib) {
        if (VERBOSE_LOADING) System.out.println("\nGoing to load: " + lib);

        if(loadFromEnv("com.trolltech.qt.library-path" , lib))
            return true;

        if(loadFromEnv("com.trolltech.qt.internal.jambipath" , lib))
            return true;

        // Try to search in the classpath, including .jar files and unpack to a temp directory, then load
        // from there.
        try {
            URL libUrl = Thread.currentThread().getContextClassLoader().getResource(lib);
            if (libUrl == null) {
                throw new RuntimeException("Library: '" + lib + "' could not be resolved");
            }

            File tmpLibDir = jambiTempDir();

            File destLib = new File(tmpLibDir, lib);
            if (!destLib.exists()) {
                tmpLibDir.mkdirs();
                copy(libUrl, destLib);
            }
            Runtime.getRuntime().load(destLib.getAbsolutePath());
            if (VERBOSE_LOADING) System.out.println("Loaded(" + lib + ") using cached");
        } catch (Throwable e) {
            if (VERBOSE_LOADING) e.printStackTrace();
        }

        // Try to load using relative path (relative to qtjambi.jar or root of package where class file are loaded from
        if(implicitLoading){
            try {
                URI uri = Utilities.class.getProtectionDomain().getCodeSource().getLocation().toURI();

                String basePath;
                File path = new File(uri);
                if (path.isDirectory())
                    basePath = path.getAbsolutePath();
                else
                    basePath = path.getParentFile().getAbsolutePath();

                String libraryPath = basePath + File.separator + libSubPath + File.separator + lib;
                if (new File(libraryPath).exists()) {
                    Runtime.getRuntime().load(libraryPath);
                    if (VERBOSE_LOADING)
                        System.out.println("Loaded(" + libraryPath + ") using deploy path, as " + lib);
                    return true;
                }

            } catch (Throwable e) {
                if (VERBOSE_LOADING)
                    e.printStackTrace();
            }
        }

        // Try to load in standard way.
        try {
            String stripped = stripLibraryName(lib);
            System.loadLibrary(stripped);
            if (VERBOSE_LOADING) System.out.println("Loaded(" + lib + ") in standard way as " + stripped);
            return true;
        } catch (Throwable e) {
            if (VERBOSE_LOADING) e.printStackTrace();
        }

        if(loadFromEnv("java.library.path" , lib))
            return true;


        if (VERBOSE_LOADING) System.out.println("Loading: " + lib + " failed.\n");
        return false;
    }

    public static File jambiTempDir() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String user = System.getProperty("user.name");
        String arch = System.getProperty("os.arch");
        return new File(tmpDir, "QtJambi_" + user + "_" + arch + "_" + VERSION_STRING);
    }

    public static void copy(URL sourceUrl, String destination) throws IOException {
        copy(sourceUrl.openStream(), new FileOutputStream(destination));
    }

    public static void copy(URL sourceUrl, File destination) throws IOException {
        copy(sourceUrl.openStream(), new FileOutputStream(destination));
    }

    public static void copy(String source, String destination) throws IOException {
        copy(new FileInputStream(source), new FileOutputStream(destination));
    }

    /**
     * Copies the data in the inputstream into the output stream.
     * @param in The source.
     * @param out The destination.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[1024 * 64];
        while (in.available() > 0) {
            int read = in.read(buffer);
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
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


    private static String jniLibraryName(String lib) {
        switch (operatingSystem) {
        case Windows: return lib + ".dll";
        case MacOSX: return "lib" + lib + ".jnilib";
        case Linux: return "lib" + lib + ".so";
        }
        throw new RuntimeException("Unreachable statement");
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


    private static String qtLibraryName(String lib) {
        switch (operatingSystem) {
        case Windows:
            return configuration == Configuration.Debug ? lib + "d4.dll" :   lib + "4.dll";
        case MacOSX:
            if (configuration == Configuration.Debug)
                return "lib" + lib + "_debug.4.dylib";
            return "lib" + lib + ".4.dylib";
        case Linux:
            // Linux doesn't have a dedicated "debug" library since 4.2
            return "lib" + lib + ".so.4";
        }
        throw new RuntimeException("Unreachable statement");
    }


    private static List<String> readSystemLibraries() {
        List<String> list = new ArrayList<String>();
        String liblist = System.getProperty("com.trolltech.qt.systemlibraries");
        if (liblist != null) {
            String libs[] = liblist.split(File.pathSeparator);
            for (String s : libs)
                list.add(s);
        } else {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("qt_system_libs");
            if (in == null && VERBOSE_LOADING)
                System.out.println("No 'qt_system_libs' file");

            if (in != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                // may return null, but that will be covered by the catch below...
                try {
                    String s = null;
                    while ((s = r.readLine()) != null)
                        list.add(s);
                } catch (Exception e) { if (VERBOSE_LOADING) e.printStackTrace(); }
            }
        }
        return list;
    }

    public static String unpackPlugins() {
        String pluginJars = System.getProperty("com.trolltech.qt.pluginjars");
        if (pluginJars != null) {
            File tmpDir = jambiTempDir();
            String jars[] = pluginJars.split(File.pathSeparator);
            String classpath = System.getProperty("java.class.path");
            for (String jar : jars) {

                if (new File(jar).exists()) {
                    unpackPlugins(jar);
                    continue;
                }

                URL libUrl = Thread.currentThread().getContextClassLoader().getResource(jar);
                if (libUrl == null) {
                    System.err.println("Plugin archive: '" + jar + "' could not be resolved");
                    continue;
                }

                try {
                    if (new File(libUrl.toURI()).exists())
                        unpackPlugins(jar);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            return tmpDir.getAbsolutePath() + "/plugins";
        }
        return null;
    }

    private static void unpackPlugins(String jarName) {
        try {
            JarFile jar = new JarFile(jarName);
            File tmpDir = jambiTempDir();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                InputStream stream = jar.getInputStream(entry);

                if (entry.getName().startsWith("plugins") && !entry.isDirectory()) {
                    File destination = new File(tmpDir.getAbsolutePath(), entry.getName());
                    if (!destination.exists()) {
                        File path = destination.getParentFile();
                        if (!path.exists())
                            path.mkdirs();
                        copy(stream, new FileOutputStream(destination));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
