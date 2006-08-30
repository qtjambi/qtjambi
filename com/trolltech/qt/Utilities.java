package com.trolltech.qt;

import java.io.*;
import java.net.*;
import java.util.*;

public class Utilities {

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

    private static final String DEBUG_SUFFIX = "_debuglib";

    private static final boolean VERBOSE_LOADING =
        System.getProperty("com.trolltech.qt.verbose-loading") != null;

    public static void loadSystemLibraries() {
        List<String> libs = readSystemLibraries();
        for (String s : libs) {
            loadLibrary(s);
        }
    }

    public static void loadQtLibrary(String library) {
        String lib = qtLibraryName(library);
        loadLibrary(lib);
    }

    public static void loadJambiLibrary(String library) {
    	if (configuration == Configuration.Debug)
            library += DEBUG_SUFFIX;
    	String lib = jniLibraryName(library);
    	loadLibrary(lib);
    }

    public static boolean loadLibrary(String lib) {
        try {
            try {
                String stripped = stripLibraryName(lib);
                System.loadLibrary(stripped);
                if (VERBOSE_LOADING) System.out.println("Loaded(" + lib + ") in standard way as " + stripped);
                return true;
            } catch (Error e) {
                if (VERBOSE_LOADING) e.printStackTrace();
            }

            Runtime rt = Runtime.getRuntime();

            // First look in the library path for the libraries...
            String libraryPath = System.getProperty("java.library.path");
            String libraryPaths[] = libraryPath.split(File.pathSeparator);
            for (String path : libraryPaths) {
                File f = new File(path, lib);
                if (f.exists()) {
                    rt.load(f.getAbsolutePath());
                    if (VERBOSE_LOADING)
                        System.out.println("Loaded(" + lib + ") using absolute path");
                    return true;
                }
            }

            // If not in the library path, try to search in the classpath,
            // including .jar files and unpack to a temp directory, then load
            // from there.
            URL libUrl = Thread.currentThread().getContextClassLoader().getResource(lib);
            if (libUrl == null)
                throw new RuntimeException("Library: '" + lib + "' could not be resolved");

            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File tmpLibDir = new File(tmpDir, "QtJambi_" + QtJambi.VERSION_STRING);

            File destLib = new File(tmpLibDir, lib);
            if (!destLib.exists()) {
                tmpLibDir.mkdirs();
                copy(libUrl, destLib);
            }
            rt.load(destLib.getAbsolutePath());
            if (VERBOSE_LOADING) System.out.println("Loaded(" + lib + ") using cached");
        } catch (Throwable t) {
            if (VERBOSE_LOADING) t.printStackTrace();
            return false;
        }
        return true;
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
            if (configuration == Configuration.Debug)
                return "lib" + lib + "_debug.so.4";
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
}
