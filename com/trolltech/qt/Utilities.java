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

import com.trolltech.qt.core.QMessageHandler;

/**
This class contains static members that gives information and performs Qt Jambi
related tasks.
*/
public class Utilities {
    private static HashSet<String> LOADED_LIBS = new HashSet<String>();
    
	/** The Qt Library's major version. */
    public static final int MAJOR_VERSION = 4;
	/** The Qt Library's minor version. */
    public static final int MINOR_VERSION = 3;
	/** The Qt Library's patch version. */
    public static final int PATCH_VERSION = 1;

	/** Qt Library build number */
    public static final int BUILD_NUMBER = 1;

	/** A formated String with versioning*/
    public static final String VERSION_STRING = String.format("%1$d.%2$d.%3$d_%4$02d",
            MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION, BUILD_NUMBER);

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
    public static String exceptionsForMessages = System.getProperty("com.trolltech.qt.exceptions-for-messages");
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
    	String excludeLibraries = System.getProperty(EXCLUDE_STRING);
    	if (excludeLibraries != null) {
            StringTokenizer tokenizer = new StringTokenizer(excludeLibraries,
                                                            File.pathSeparator);
            while (tokenizer.hasMoreElements()) {
                if (library.equals(tokenizer.nextElement())) {
                    VERBOSE_LOADING.DEBUG("Skipped library (" + library
                            + ") since it is listed in "
                            + excludeLibraries);
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

    private static boolean loadFromEnv(String env, String lib) {
        VERBOSE_LOADING.DEBUG(".. from environment: " + env + " ");
        try {
            String envPath = System.getProperty(env);
            if (envPath != null) {
                String envPaths[] = envPath.split(File.pathSeparator);
                for (String path : envPaths) {
                    File f = new File(path, lib);

                    if (f.exists()) {
                        Runtime.getRuntime().load(f.getAbsolutePath());
                        VERBOSE_LOADING.DEBUG("\n   Loaded from: " + path);
                        LOADED_LIBS.add(lib);
                        return true;
                    }
                }
                if (envPath.length() > 0) {
                    VERBOSE_LOADING.DEBUG("\n   Failed to find " + lib + " in " + env + "(" + envPath + ")");
                }
            } else {
                VERBOSE_LOADING.DEBUG("(Skipped, environment was empty)");
            }
        } catch (Throwable e) {
            VERBOSE_LOADING.DEBUG("\n   Failed to load " + lib + " from " + env);
            return false;
        }
        return false;
    }

    public static boolean loadLibrary(String lib) {
        
        if(LOADED_LIBS.contains(lib)){
            VERBOSE_LOADING.DEBUG("\nAlready loaded: " + lib + " skipping it.");
            return true;
        }
        
        VERBOSE_LOADING.DEBUG("\nGoing to load: " + lib);

        if (loadFromEnv("com.trolltech.qt.library-path", lib))
            return true;

        if (loadFromEnv("com.trolltech.qt.internal.jambipath", lib))
            return true;

        // Try to search in the classpath, including .jar files and unpack to a
        // temp directory, then load
        // from there.
        try {
            VERBOSE_LOADING.DEBUG(".. from classpath:");
            URL libUrl = Thread.currentThread().getContextClassLoader().getResource(lib);
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
                
                Runtime.getRuntime().load(destLib.getAbsolutePath());

                VERBOSE_LOADING.DEBUG("Loaded " + destLib.getAbsolutePath() + " as " + lib + " from class path");
            } else {
                Runtime.getRuntime().load(destLib.getAbsolutePath());
                VERBOSE_LOADING.DEBUG("Loaded " + destLib.getAbsolutePath() + " as " + lib + " using cached");
            }
            LOADED_LIBS.add(lib);
            return true;
        } catch (Throwable e) {
            VERBOSE_LOADING.DEBUG(e);
        }

        // Try to load using relative path (relative to qtjambi.jar or
        // root of package where class file are loaded from
        if (implicitLoading) {
            VERBOSE_LOADING.DEBUG(".. using relative path (com.trolltech.qt.implicit-loading).");
            try {
                String basePath = filePathForClasses();

                String libraryPath = basePath + File.separator + libSubPath + File.separator + lib;
                if (new File(libraryPath).exists()) {
                    Runtime.getRuntime().load(libraryPath);
                    VERBOSE_LOADING.DEBUG("Loaded(" + libraryPath + ") using deploy path, as " + lib);
                    LOADED_LIBS.add(lib);
                    return true;
                }

            } catch (Throwable e) {
                VERBOSE_LOADING.DEBUG(e);
            }
        }

        // Try to load in standard way.
        VERBOSE_LOADING.DEBUG(".. in standard way.");
        
        try {
            String stripped = stripLibraryName(lib);
            System.loadLibrary(stripped);
            VERBOSE_LOADING.DEBUG("Loaded(" + lib + ") in standard way as " + stripped);
            LOADED_LIBS.add(lib);
            return true;
        } catch (Throwable e) {
            VERBOSE_LOADING.DEBUG(e);
        }

        if (loadFromEnv("java.library.path", lib))
            return true;

        VERBOSE_LOADING.DEBUG("Loading: " + lib + " failed.\n");
        VERBOSE_LOADING.FAILED(lib);
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
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("qt_system_libs");
            if (in == null)
                VERBOSE_LOADING.DEBUG("No 'qt_system_libs' file");

            if (in != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                // may return null, but that will be covered by the catch below...
                try {
                    String s = null;
                    while ((s = r.readLine()) != null)
                        list.add(s);
                } catch (Exception e) {
                    VERBOSE_LOADING.DEBUG(e);
                }
            }
        }
        return list;
    }

    public static String unpackPlugins() {
        String pluginJars = System.getProperty("com.trolltech.qt.pluginjars");

        VERBOSE_LOADING.DEBUG("Loading plugins from: " + pluginJars);

        List<URL> urls = new ArrayList<URL>();
        try {
            Enumeration<URL> bases = Thread.currentThread().getContextClassLoader().getResources("plugins");
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
                    VERBOSE_LOADING.DEBUG("could not load plugin archive...: " + jar);
                    VERBOSE_LOADING.DEBUG(e);
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

        VERBOSE_LOADING.DEBUG("unpacked plugins from: " + jar);
    }
    
    public static QMessageHandler messageHandler() {
        if (exceptionsForMessages != null) {
            final String config = exceptionsForMessages.trim().toUpperCase();
            final boolean all = config.equals("") || config.equals("ALL") || config.equals("TRUE");
            final boolean critical = config.contains("CRITICAL");
            final boolean debug = config.contains("DEBUG");
            final boolean fatal = config.contains("FATAL");
            final boolean warning = config.contains("WARNING");

            if (all || critical || debug || fatal || warning) {
                return new QMessageHandler() {

                    public void critical(String message) {
                        if (critical || all)
                            throw new RuntimeException("Critical: " + message);
                        else
                            System.err.println("Critical: " + message);
                    }

                    public void debug(String message) {
                        if (debug || all)
                            throw new RuntimeException("Debug: " + message);
                        else
                            System.err.println("Debug: " + message);
                    }

                    public void fatal(String message) {
                        if (fatal || all)
                            throw new RuntimeException("Fatal: " + message);
                        else
                            System.err.println("Fatal: " + message);
                    }

                    public void warning(String message) {
                        if (warning || all)
                            throw new RuntimeException("Warning: " + message);
                        else
                            System.err.println("Warning: " + message);
                    }
                };
            }
        }
        return null;
    }
    
    private static class VERBOSE_LOADING {
        private static Vector<Object> debug = new Vector<Object>();
        private static final boolean VERBOSE_LOADING =
            System.getProperty("com.trolltech.qt.verbose-loading") != null;
        
        private static synchronized void DEBUG(String s){
            if (VERBOSE_LOADING) {
                System.out.println(s);
            }
            else {
                debug.add(s);
            }            
        }
  
        private static synchronized void DEBUG(Throwable e){
            if (VERBOSE_LOADING) {
                e.printStackTrace();
            }
            else {
                debug.add(e);
            }   
        }
        
        private static synchronized void FAILED(String failingLib){
            if(!VERBOSE_LOADING) {
                throw new RuntimeException("\nLog showing how we tried to load the library: " + failingLib + "\n\n" + format(debug) + "-- End load-library log --\n");  
            } else {    
                System.out.println(format(debug));
            }
            debug = new Vector<Object>();
        }
        
        private static String format(Vector<Object> debug){
            String res = "";
            for (Iterator<Object> iterator = debug.iterator(); iterator.hasNext();) {
                Object element = (Object) iterator.next();
                if(element instanceof String){
                    res += element.toString() + "\n";
                }
                else if(element instanceof Throwable){
                    Throwable throwable = (Throwable)element;
                    res += "   Failed with exception:\n";
                    res += "     " + throwable.toString() + "\n";
                    StackTraceElement[] stackTraceElementArray = throwable.getStackTrace();
                    for (int i = 0; i < stackTraceElementArray.length; i++) {
                        res += "     " + stackTraceElementArray[i].toString() + "\n";
                    }
                    res += "\n";
                }
            }
            return res;
        }
    }
}
