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

import com.trolltech.qt.internal.*;

/**
This class contains static members that gives information and performs Qt Jambi
related tasks.
*/
public class Utilities {
    private static HashSet<String> LOADED_LIBS = new HashSet<String>();

    /** The Qt Library's major version. */
    public static final int MAJOR_VERSION = Version.MAJOR;

    /** The Qt Library's minor version. */
    public static final int MINOR_VERSION = Version.MINOR;

    /** The Qt Library's patch version. */
    public static final int PATCH_VERSION = Version.PATCH;

    /** Qt Library build number */
    public static final int BUILD_NUMBER = Version.BUILD;

    private static final boolean VERBOSE_LOADING = System.getProperty("com.trolltech.qt.verbose-loading") != null;

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
    };

	/** Defines whether Qt is build in Release or Debug. */
    public enum Configuration {
	/** Release build. */
        Release,
	/** Debug build. */
        Debug
    };

	/** The operating system Qt Jambi is running on. */
    public static OperatingSystem operatingSystem = decideOperatingSystem();
	/** The Configuration of Qt Jambi. */
    public static Configuration configuration = decideConfiguration();
	/** Wheter Qt Jambi has implicit loading. */
    public static boolean implicitLoading = !matchProperty("com.trolltech.qt.implicit-loading", "false");
	/** The library sub path. */
    public static String libSubPath = decideLibSubPath();
    /** Whether Qt Jambi should prefer to load libraries from its cache */
    public static boolean loadFromCache = matchProperty("com.trolltech.qt.load-from-cache", "true");
    /** Wheter Qt Jambi should throw exceptions on warnings, debug, critical or/and fatal messages from c++ code. */

    private static final String DEBUG_SUFFIX = "_debuglib";



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
        com.trolltech.qt.internal.NativeLibraryManager.loadQtLibrary(library);

//     	String excludeLibraries = System.getProperty(EXCLUDE_STRING);
//     	if (excludeLibraries != null) {
//             StringTokenizer tokenizer = new StringTokenizer(excludeLibraries,
//                                                             File.pathSeparator);
//             while (tokenizer.hasMoreElements()) {
//                 if (library.equals(tokenizer.nextElement())) {
//                     if (VERBOSE_LOADING)
//                         System.out.println("Skipped library (" + library + ") since it is listed in " + excludeLibraries);
//                     return;
//                 }
//             }
//     	}
//         String lib = qtLibraryName(library);
//         loadLibrary(lib);
    }

    public static void loadJambiLibrary(String library) {
        com.trolltech.qt.internal.NativeLibraryManager.loadJambiLibrary(library);

//     	if (configuration == Configuration.Debug)
//             library += DEBUG_SUFFIX;
//     	String lib = jniLibraryName(library);
//     	loadLibrary(lib);
    }

    private static boolean loadFromEnv(String env, String lib, LibraryLoadingInfo debug) {
        debug.message(".. from environment: " + env + " ");
        try {
            String envPath = System.getProperty(env);
            if (envPath != null) {
                String envPaths[] = envPath.split(File.pathSeparator);
                for (String path : envPaths) {
                    File f = new File(path, lib);

                    if (f.exists()) {
                        Runtime.getRuntime().load(f.getAbsolutePath());
                        debug.success("   Loaded from: " + path);
                        LOADED_LIBS.add(lib);
                        return true;
                    }
                }
                if (envPath.length() > 0) {
                    debug.message("   Failed to find " + lib + " in " + env + "(" + envPath + ")");
                }
            } else {
                debug.message("(Skipped, environment was empty)");
            }
        } catch (Throwable e) {
            debug.message("   Failed to load " + lib + " from " + env);
            debug.failed();
            return false;
        }
        return false;
    }

    public static boolean loadLibrary(String lib) {
        LibraryLoadingInfo debug = new LibraryLoadingInfo(lib);

        if(LOADED_LIBS.contains(lib)){
            debug.success("Already loaded: " + lib + " skipping it.");
            return true;
        }

        debug.message("Going to load: " + lib);

        boolean onlyUnpack = operatingSystem == OperatingSystem.Windows
                             && (lib.equals("Microsoft.VC80.CRT.manifest")
                                 || lib.equals("msvcr80.dll")
                                 || lib.equals("msvcm80.dll")
                                 || lib.equals("msvcp80.dll"));

        if (!onlyUnpack && loadFromEnv("com.trolltech.qt.library-path", lib, debug))
            return true;

        if (!onlyUnpack && loadFromEnv("com.trolltech.qt.internal.jambipath", lib, debug))
            return true;

        // Try to search in the classpath, including .jar files and unpack to a
        // temp directory, then load
        // from there.
        try {
            debug.message(".. from classpath:");
            URL libUrl = classLoader().getResource(lib);
            if (libUrl == null) {
                throw new RuntimeException("Library: '" + lib + "' could not be resolved");
            }

            File tmpLibDir = jambiTempDir();

            File destLib = new File(tmpLibDir, lib);

            // If we prefer to load the cached copies of libraries *and* the library has
            // previously been copied out, we just load it. Otherwise we copy it again
            // (make sure default to updating the cache or we might end up in trouble.)
            // For applications such as webstart, use the use-cache-property.

            if (!destLib.exists() || !loadFromCache) {
                tmpLibDir.mkdirs();
                copy(libUrl, destLib);

                if (onlyUnpack) {
                    debug.success("Unpacked file: " + destLib.getAbsolutePath());
                    return true;
                }

                Runtime.getRuntime().load(destLib.getAbsolutePath());
                debug.success("Loaded " + destLib.getAbsolutePath() + " as " + lib + " from class path");
            } else {
                Runtime.getRuntime().load(destLib.getAbsolutePath());
                debug.success("Loaded " + destLib.getAbsolutePath() + " as " + lib + " using cached");
            }
            LOADED_LIBS.add(lib);
            return true;
        } catch (Throwable e) {
            debug.message(e);
        }

        if (onlyUnpack)
            return true;

        // Try to load using relative path (relative to qtjambi.jar or
        // root of package where class file are loaded from
        if (implicitLoading) {
            debug.message(".. using relative path (com.trolltech.qt.implicit-loading).");
            try {
                String basePath = filePathForClasses();

                String libraryPath = basePath + File.separator + libSubPath + File.separator + lib;
                if (new File(libraryPath).exists()) {
                    Runtime.getRuntime().load(libraryPath);
                    debug.success("Loaded(" + libraryPath + ") using deploy path, as " + lib);
                    LOADED_LIBS.add(lib);
                    return true;
                } else {
                    debug.message("   File not found: " + libraryPath);
                }
            } catch (Throwable e) {
                debug.message(e);
            }
        }

        // Try to load in standard way.
        debug.message(".. in standard way.");

        try {
            String stripped = stripLibraryName(lib);
            System.loadLibrary(stripped);
            debug.success("Loaded(" + lib + ") in standard way as " + stripped);
            LOADED_LIBS.add(lib);
            return true;
        } catch (Throwable e) {
            debug.message(e);
        }

        if (loadFromEnv("java.library.path", lib, debug))
            return true;

        debug.failed();
        return false;
    }

    static String filePathForClasses() throws URISyntaxException {
        URI uri = Utilities.class.getProtectionDomain().getCodeSource().getLocation().toURI();

        String basePath;
        File path = new File(uri);
        if (path.isDirectory())
            basePath = path.getAbsolutePath();
        else
            basePath = path.getParentFile().getAbsolutePath();
        return basePath;
    }

    public static File jambiTempDir() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String user = System.getProperty("user.name");
        String arch = System.getProperty("os.arch");
        return new File(tmpDir, "QtJambi_" + user + "_" + arch + "_" + VERSION_STRING);
    }

    private static void copy(URL sourceUrl, File destination) throws IOException {
        copy(sourceUrl.openStream(), new FileOutputStream(destination));
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
            InputStream in = classLoader().getResourceAsStream("qt_system_libs");
            if (in == null && VERBOSE_LOADING)
                System.out.println("No 'qt_system_libs' file");

            if (in != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                // may return null, but that will be covered by the catch below...
                try {
                    String s = null;
                    while ((s = r.readLine()) != null)
                        list.add(s);
                } catch (Exception e) {
		    if (VERBOSE_LOADING)
                        e.printStackTrace();
                }
            }
        }
        return list;
    }

    private static ClassLoader classLoader() {
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	if (loader == null) {
	    loader = Utilities.class.getClassLoader();
	    assert loader != null;
	}
	return loader;
    }

    public static String unpackPlugins() {
        String pluginJars = System.getProperty("com.trolltech.qt.pluginjars");

        if (VERBOSE_LOADING)
            System.out.println("Loading plugins from: " + pluginJars);

        List<URL> urls = new ArrayList<URL>();
        try {
            Enumeration<URL> bases = classLoader().getResources("plugins");
            while (bases.hasMoreElements())
                urls.add(bases.nextElement());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pluginJars != null) {
            File tmpDir = jambiTempDir();
            String jars[] = pluginJars.split(File.pathSeparator);

            for (String jar : jars) {
                try {
                    File f = new File(jar);
                    if (f.exists()) {
                        unpackPlugins(new JarFile(f));
                    } else {
                        for (URL url : urls) {
                            if (url.toString().contains(jar)) {
                                URLConnection connection = url.openConnection();
                                if (connection instanceof JarURLConnection)
                                    unpackPlugins(((JarURLConnection) connection).getJarFile());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (VERBOSE_LOADING) {
                        System.out.println("could not load plugin archive...: " + jar);
                        e.printStackTrace();
                    }
                }
            }
            return tmpDir.getAbsolutePath() + "/plugins";
        }
        return null;
    }

    private static void unpackPlugins(JarFile jar) throws IOException {
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
        if (VERBOSE_LOADING)
            System.out.println("unpacked plugins from: " + jar);
    }

    private static class LibraryLoadingInfo {
        private String libraryName;
        private boolean success = false;
        private Vector<Object> messages = new Vector<Object>();

        LibraryLoadingInfo(String libraryName) {
            this.libraryName = libraryName;
        }

        private void message(String s) {
            messages.add(s);
        }

        private void message(Throwable e) {
            messages.add(e);
        }

        private String format() {
            String res = "";
            if (success) {
                System.out.println(messages.lastElement().toString());
            } else {
                for (Iterator<Object> iterator = messages.iterator(); iterator.hasNext();) {
                    Object element = iterator.next();

                    if (element instanceof String) {
                        res += element.toString() + "\n";
                    } else if (element instanceof Throwable) {
                        Throwable throwable = (Throwable) element;
                        res += "   Failed with exception:\n";
                        res += "     " + throwable.toString() + "\n";
                        StackTraceElement[] stackTraceElementArray = throwable.getStackTrace();
                        for (int i = 0; i < stackTraceElementArray.length; i++) {
                            res += "     " + stackTraceElementArray[i].toString() + "\n";
                        }
                        res += "\n";
                    }
                }
            }
            return res;
        }

        private void success(String message) {
            message(message);
            success = true;
            if (VERBOSE_LOADING) {
                System.out.print(format());
            }
        }

        private void failed() {
            success = false;
            if (VERBOSE_LOADING) {
                System.out.println("Failed to laod : " + libraryName);
                System.out.println("Below you will se how we tried to load it:\n");
                System.out.println(format());
                System.out.println("... giving up loading library: " + libraryName);
            } else {
                throw new RuntimeException("Loading library: "
                                           + libraryName
                                           + " failed.\n"
                                           + "Log showing how we tried to load the library: "
                                           + libraryName + "\n"
                                           + format() + "-- End load-library log --\n");
            }
        }
    }
}
