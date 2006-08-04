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

    public static void loadSystemLibraries() {

        System.out.println("CLASSPATH: " + System.getProperty("java.class.path"));

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
                System.loadLibrary(stripLibraryName(lib));
                return true;
            } catch (Error e) {
                e.printStackTrace();
            }

            Runtime rt = Runtime.getRuntime();

            // First look in the library path for the libraries...
            String libraryPath = System.getProperty("java.library.path");
            String libraryPaths[] = libraryPath.split(File.pathSeparator);
            for (String path : libraryPaths) {
                File f = new File(path, lib);
                if (f.exists()) {
                    System.out.println("loadLibrary(1): trying to load: " + f.getAbsolutePath());
                    rt.load(f.getAbsolutePath());
                    return true;
                }
            }

            // If not in the library path, try to search in the classpath,
            // including .jar files and unpack to a temp directory, then load
            // from there.
            URL libUrl = Utilities.class.getClassLoader().getResource(lib);
            if (libUrl == null)
                throw new RuntimeException("Library: '" + lib + "' could not be resolved");

            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File tmpLibDir = new File(tmpDir, "QtJambi_" + QtJambi.VERSION_STRING);

            File destLib = new File(tmpLibDir, lib);
            if (!destLib.exists()) {
                tmpLibDir.mkdirs();
                copy(libUrl, destLib);
            }
            System.out.println("loadLibrary(2): trying to load: " + destLib.getAbsolutePath());
            rt.load(destLib.getAbsolutePath());
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
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
        // Strip away the library postfix...
        int dot = lib.lastIndexOf('.');
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
                return "lib" + lib + "_debug.4.so";
            return "lib" + lib + ".4.so";
        }
        throw new RuntimeException("Unreachable statement");
    }


    private static List<String> readSystemLibraries() {
        String runtime = System.getProperty("com.trolltech.qt.runtimespec");
        if (runtime == null) {
            if (operatingSystem == OperatingSystem.Windows) runtime = "win";
            else if (operatingSystem == OperatingSystem.Linux) runtime = "linux";
            else if (operatingSystem == OperatingSystem.MacOSX) runtime = "macosx";
            else throw new RuntimeException("Unhandled operating system");
        }

        InputStream in = Utilities.class.getClassLoader().getResourceAsStream("com/trolltech/qt/resources/syslibs." + runtime);

        List<String> list = new ArrayList<String>();
        try {
            StreamTokenizer tok =
                new StreamTokenizer(new BufferedReader(new InputStreamReader(in)));
            while (tok.nextToken() != StreamTokenizer.TT_EOF) {
                list.add(tok.sval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
