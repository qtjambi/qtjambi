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

package com.trolltech.qt.internal;

import java.io.*;
import java.util.*;
//import java.util.jar.*;
import java.net.*;
//import java.security.*;

import javax.xml.parsers.*;

import org.xml.sax.helpers.*;

import com.trolltech.qt.Utilities;

import com.trolltech.qt.osinfo.OSInfo;

// !!NOTE!! This class can have no dependencies on Qt since
// it is required for loading the libraries.


class DeploymentSpecException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DeploymentSpecException(String msg) {
        super(msg);
    }
}

class WrongSystemException extends DeploymentSpecException {
    private static final long serialVersionUID = 1L;

    public WrongSystemException(String msg) {
        super(msg);
    }
}


/**
 * The NativeLibraryManager class is responsible for handling native
 * libraries in Qt Jambi. Native libraries can be loaded either
 * directly from the file system using
 * <code>-Djava.library.path</code> or indirectly JAR-file that
 * contain a deployment descriptor. For normal deployment, the JAR
 * approach is recommended.
 *
 * Loading libraries is done by calling the methods
 * <code>loadQtLibrary</code> and <code>loadLibrary</code>.
 *
 * When the indirect .jar file approach is taken, the .jar file will
 * be opened and the native library manager will search for a file
 * named "qtjambi-deployment.xml". This file contains a list of native
 * libraries that should unpacked to a temporary directory and loaded,
 * either right away or at a later time. There are three types of
 * libraries.
 *
 * <ll>
 *
 *   <li> System libraries; such as the system runtime
 *   libraries. These libraries are usually loaded automatically by
 *   the native library loader.
 *
 *   <li> Normal libraries; such as the QtCore and
 *   com_trolltech_qt_core libraries. These are loaded at runtime on
 *   demand.
 *
 *   <li> Plugins; such as qjpeg. These are never loaded explicitly by
 *   the native library manager, but are unpacked into the temporary
 *   folder so that Qt can find and load them from the file system.
 *
 * </ll>
 *
 * There are three possible deployment scenarios. The simplest and
 * most straightforward approach is when deploying a Pure Java
 * application based on Qt Jambi. In this case the prebuilt binaries
 * from the binary package can just be deployed as part of the
 * classpath and the rest will solve itself.
 *
 * When deploying a Qt Jambi application that is using native code
 * other than Qt Jambi, we recommend building a new .jar file with a
 * custom qtjambi-deployment.xml which contais the Qt Jambi libraries
 * and the custom native libraries. Deployment can then be done by
 * making sure that this new .jar file is available in the classpath.
 *
 * The final option for deployment is when users have a C++
 * application which starts and makes use of Qt Jambi. In this case we
 * suggest that all dependent libraries are available in the file
 * system and via <code>-Djava.library.path<code>
 *
 * To get runtime information about how library loading works, specify
 * the <code>-Dcom.trolltech.qt.verbose-loading</code> system property
 * to the Virtual Machine. It possible to specify that the native
 * library manager should load debug versions of libraries as
 * well. This is done by specifying the system property
 * </code>-Dcom.trolltech.qt.debug</code>
 *
 */
public class NativeLibraryManager {

    public static String DEPLOY_DESCRIPTOR_NAME = "qtjambi-deployment.xml";

    private static final String DEBUG_SUFFIX = "_debuglib";

    private static final boolean VERBOSE_LOADING = System.getProperty("com.trolltech.qt.verbose-loading") != null;

    private static final int LOAD_TRUE = 1;
    private static final int LOAD_FALSE = 2;
    private static final int LOAD_NEVER = 3;

    private static class LibraryEntry {
        public String name;
        public int load;
        public DeploymentSpec spec;
        public boolean loaded;
    }

    private static class DeploymentSpec {
        public String key;
        public String jarName;
        public List<LibraryEntry> libraries;
        public List<String> pluginPaths;

        public void addPluginPath(String path) {
            if (pluginPaths == null)
                pluginPaths = new ArrayList<String>();
            pluginPaths.add(path);
            reporter.report(" - plugin path='", path, "'");
        }

        public void addLibraryEntry(LibraryEntry e) {
            if (libraries == null)
                libraries = new ArrayList<LibraryEntry>();
            libraries.add(e);
            reporter.report(" - library: name='", e.name, "', ",
                            (e.load == LOAD_TRUE ? "load" :
                             (e.load == LOAD_NEVER ? "never load" : ""))
                            );
        }
    }


    private static class XMLHandler extends DefaultHandler {
        public DeploymentSpec spec;

        public void startElement(String uri,
                                 String localName,
                                 String name,
                                 org.xml.sax.Attributes attributes) {
            if (name.equals("cache")) {
                String key = attributes.getValue("key");
                if (key == null) {
                    throw new DeploymentSpecException("<cache> element missing required attribute \"key\"");
                }
                spec.key = key;
                reporter.report(" - cache key='", spec.key, "'");

            } else if (name.equals("library")) {
                LibraryEntry e = new LibraryEntry();
                e.name = attributes.getValue("name");
                if (e.name == null) {
                    throw new DeploymentSpecException("<library> element missing required attribute \"name\"");
                }

                String load = attributes.getValue("load");
                if (load != null && load.equals("true")) e.load = LOAD_TRUE;
                else if (load != null && load.equals("never")) e.load = LOAD_NEVER;
                else e.load = LOAD_FALSE;

                e.spec = spec;

                String fileName = new File(e.name).getName();
                if (e.load == LOAD_NEVER) {
                    neverLoad.put(fileName, e);

                } else {
                    // Add library name to the global map of libraries...
                    LibraryEntry old = libraryMap.get(fileName);
                    if (old != null) {
                        throw new DeploymentSpecException("<library> '" + e.name
                                                          + "' is duplicated. Present in both '"
                                                          + spec.jarName + "' and '"
                                                          + old.spec.jarName + "'.");
                    }
                    reporter.report(" - adding '", fileName, "' to library map");
                    libraryMap.put(fileName, e);
                }

                spec.addLibraryEntry(e);

            } else if (name.equals("plugin")) {
                String path = attributes.getValue("path");
                if (path == null) {
                    throw new DeploymentSpecException("<plugin> element missing required attribute \"path\"");
                }
                spec.addPluginPath(path);
            } else if (name.equals("qtjambi-deploy")) {
                String system = attributes.getValue("system");
                if (system == null || system.length() == 0) {
                    throw new DeploymentSpecException("<qtjambi-deploy> element missing required attribute 'system'");
                } else if (!system.equals(OSInfo.osArchName())) {
                    throw new WrongSystemException("trying to load: '" + system
                                                   + "', expected: '" + OSInfo.osArchName() + "'");
                }
            }
        }
    }

    //no-one used this so I commented it out
   /*private static class ChecksumFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".chk");
        }
    }*/


    /**
     * Returns a file that is used for caching native libraries. The
     * path is a subdirectory of <code>java.io.tmpdir</code>, named
     * <code>QtJambi_{user}_{architecture}_{version}_{key}</code>. The
     * key is the same as the cache key used in the deployment
     * descriptor. The purpose of using different keys is to avoid
     * binary compatibility when various configurations of Qt and Qt
     * Jambi are used on the same system.
     *
     * When deployment descriptors are not used, this location will
     * not be used.
     *
     * @param key The cache key to.
     * @return A File representing the location directory for
     * temporary content. The directory is not explicitly created in
     * this here.
     */
    public static File jambiTempDirBase(String key) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String user = System.getProperty("user.name");
        String arch = System.getProperty("os.arch");
        return new File(tmpDir, "QtJambi_" + user + "_" + arch + "_" + Utilities.VERSION_STRING + "_" + key);
    }


    /**
     * Returns the list of all plugin paths that are specified in the
     * deployment descriptors. If deployment descriptors are not used,
     * this list will be an empty list.
     *
     * @return The list of plugin paths
     */
    public static List<String> pluginPaths() {
        List<String> paths = new ArrayList<String>();
        for (DeploymentSpec spec : deploymentSpecs) {
            File root = jambiTempDirBase(spec.key);
            if (spec.pluginPaths != null)
                for (String path : spec.pluginPaths)
                    paths.add(new File(root, path).getAbsolutePath());
        }
        return paths;
    }


    /**
     * Loads a library with name specified in <code>library</code>.
     * The library name will be expanded to the JNI shared library
     * name for a given platform, so the name "qtjambi" will be
     * expanded like this:
     *
     * <ll>
     *   <li> Windows: qtjambi.dll
     *   <li> Linux / Unix: libqtjambi.so
     *   <li> Mac OS X: libqtjambi.jnilib
     * </ll>
     *
     * When using loading libraries from the filesystem, this method
     * simply calls <code>System.loadLibrary</code>.
     *
     * When the system property <code>-Dcom.trolltech.qt.debug</code>
     * is specified, the suffix <code>_debuglib</code> will be appended
     * to the filename, replacing "qtjambi" above with "qtjambi_debuglib".
     *
     * @param library The name of the library..
     */
    public static void loadLibrary(String library) {
        if (Utilities.configuration == Utilities.Configuration.Debug)
            library += DEBUG_SUFFIX;
        String lib = jniLibraryName(library);
        loadNativeLibrary(lib);
    }


    /**
     * Overload which passes the default value of "4" as the version
     */
    public static void loadQtLibrary(String library) {
        loadQtLibrary(library, "4");
    }

    /**
     * Loads a library with name specified in <code>library</code>.
     * The library name will be expanded to the default shared library
     * name for a given platform, so the name "QtCore" and version "4" will be
     * expanded like this:
     *
     * <ll>
     *   <li> Windows: QtCore4.dll
     *   <li> Linux / Unix: libQtCore.so.4
     *   <li> Mac OS X: libQtCore.4.dylib
     * </ll>
     *
     * When using loading libraries from the filesystem, this method
     * simply calls <code>System.loadLibrary</code>.
     *
     * @param library The name of the library..
     */
    public static void loadQtLibrary(String library, String version) {
        String lib = qtLibraryName(library, version);
        loadNativeLibrary(lib);
    }

    private static void unpack() {
        if (unpacked)
            return;
        try {
            synchronized(NativeLibraryManager.class) {
                if (unpacked)
                    return;
                unpack_helper();
                unpacked = true;
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to unpack native libraries, progress so far:\n"
                                       + reporter, t);
        }
    }

    private static void unpack_helper() throws Exception {
        ClassLoader loader = classLoader();
        Enumeration<URL> specs = loader.getResources(DEPLOY_DESCRIPTOR_NAME);
        int count = 0;
        while (specs.hasMoreElements()) {
            URL url = specs.nextElement();

            if (VERBOSE_LOADING)
                reporter.report("Found ", url.toString());

            if (url.getProtocol().equals("jar")) {
                String eform = url.toExternalForm();

                // Try to decide the name of the .jar file to have a
                // reference point for later..
                int start = 4; //"jar:".length();
                int end = eform.indexOf("!/", start);
                // eform has the "jar:url!/entry" format
                if (end != -1) {
                    URL jarUrl = new URL(eform.substring(start, end));
                    String jarName = new File(jarUrl.getFile()).getName();
                    if (VERBOSE_LOADING)
                        reporter.report("Loading ", jarName, " from ", eform);
                    unpackDeploymentSpec(url, jarName);
                    ++count;
                }
            }
        }

        if (count == 0) {
            reporter.report("No '", DEPLOY_DESCRIPTOR_NAME,
                            "' found in classpath, loading libraries via 'java.library.path'");
        }
    }

    /**
     * Returns a classloader for current context...
     * @return The classloader
     */
    private static ClassLoader classLoader() {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    if (loader == null) {
        loader = NativeLibraryManager.class.getClassLoader();
        assert loader != null;
    }
    return loader;
    }

    /**
     * Tries to load the specified library. It will first try to load
     * the library using the deploy spec, and if that fails, it will
     * try to load the library using a standard System.loadLibrary()
     * call.
     * @param lib The full name of the library to load, such as libQtCore.so.4
     */
    private static void loadNativeLibrary(String lib) {
        try {
            loadLibrary_helper(lib);
            if (VERBOSE_LOADING)
                System.out.println(reporter.recentReports());

        } catch (Throwable e) {
            throw new RuntimeException("Loading library failed, progress so far:\n" + reporter, e);
        }
    }


    private static void loadLibrary_helper(String lib) {
        unpack();

        reporter.report("Loading library: '", lib, "'...");

        // First of all verify that we're allowed to load this library...
        LibraryEntry e = neverLoad.get(lib);
        if (e != null) {
            throw new RuntimeException("Library '" + lib + "' cannot be loaded, deploy spec");
        }

        // Try to load via deploy spec...
        e = libraryMap.get(lib);
        if (e != null) {

            if (e.loaded) {
                reporter.report(" - already loaded, skipping...");
                return;
            }

            reporter.report(" - using deployment spec");
            File libFile = new File(jambiTempDirBase(e.spec.key), e.name);
            Runtime.getRuntime().load(libFile.getAbsolutePath());
            reporter.report(" - ok!");
            e.loaded = true;

        // Load via System.load() using default paths..
        } else {
            boolean loaded = false;
            String libPaths = System.getProperty("com.trolltech.qt.library-path-override");
            if (libPaths != null && libPaths.length() > 0) {
                reporter.report(" - using 'com.trolltech.qt.library-path-override'");
            } else {
                reporter.report(" - using 'java.library.path'");
                libPaths = System.getProperty("java.library.path");
            }

            String[] paths = null;
            if (libPaths != null)
                paths = RetroTranslatorHelper.split(libPaths, File.pathSeparator);
            paths = Utilities.mergeJniLibdir(paths);
            if(paths != null) {
                for (String path : paths) {
                    File f = new File(path, lib);
                    if (f.exists()) {
                        Runtime.getRuntime().load(f.getAbsolutePath());
                        reporter.report(" - ok, path was: " + f.getAbsolutePath());
                        loaded = true;
                        break;
                    }
                }
            }
            if (!loaded) {
                throw new RuntimeException("Library '" + lib +"' was not found in 'java.library.path="
                                           + libPaths + "'");
            }
        }
    }


    private static DeploymentSpec readDeploySpec(URL url, String jarName) throws Exception {
        reporter.report("Checking Archive '", jarName, "'");

        DeploymentSpec spec = new DeploymentSpec();
        spec.jarName = jarName;

        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser parser = fact.newSAXParser();

        XMLHandler handler = new XMLHandler();
        handler.spec = spec;

        try {
            parser.parse(url.openStream(), handler);
            if (spec.key == null) {
                throw new DeploymentSpecException("Deployment Specification doesn't include required <cache key='...'/>");
            }

            deploymentSpecs.add(spec);

            return spec;
        } catch (WrongSystemException e) {
            reporter.report(" - skipping because of wrong system: " + e.getMessage());
            return null;
        }
    }


    private static void unpackDeploymentSpec(URL deploymentSpec, String jarName) throws Exception {
        reporter.report("Unpacking .jar file: '", jarName, "'");

        DeploymentSpec spec = readDeploySpec(deploymentSpec, jarName);
        if (spec == null)
            return;

        File tmpDir = jambiTempDirBase(spec.key);

        reporter.report(" - using cache directory: '", tmpDir.getAbsolutePath(), "'");

        boolean shouldCopy = false;

        // If the dir exists and contains .dummy, sanity check the contents...
        File dummyFile = new File(tmpDir, ".dummy");
        if (dummyFile.exists()) {
            reporter.report(" - cache directory exists");
        } else {
            shouldCopy = true;
        }

        // If the dir doesn't exists or it was only half completed, copy the files over...
        if (shouldCopy) {
            reporter.report(" - starting to copy content to cache directory...");

            for (LibraryEntry e : spec.libraries) {
                reporter.report(" - copying over: '", e.name, "'...");
                InputStream in = null;

                Enumeration<URL> resources = classLoader().getResources(e.name);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    String eform = url.toExternalForm();
                    if (eform.contains(jarName)) {
                        in = url.openStream();
                        reporter.report("    - matched url: ", url.toExternalForm());
                    } else if (VERBOSE_LOADING) {
                        reporter.report("    - unmatched .jar file: ", eform);
                    }
                }

                if (in == null) {
                    throw new FileNotFoundException("Library '" + e.name
                                                    + "' specified in qtjambi-deployment.xml in '"
                                                    + jarName + "' does not exist");
                }

                File outFile = new File(tmpDir, e.name);
                File outFileDir = outFile.getParentFile();
                if (!outFileDir.exists()) {
                    reporter.report(" - creating directory: ", outFileDir.getAbsolutePath());
                    outFileDir.mkdirs();
                }

                OutputStream out = new FileOutputStream(new File(tmpDir, e.name));
                copy(in, out);
            }

            if (!dummyFile.createNewFile()) {
                throw new DeploymentSpecException("Can't create dummy file in cache directory");
            }
        }

        // Load the libraries tagged for loading...
        Runtime rt = Runtime.getRuntime();
        for (LibraryEntry e : spec.libraries) {
            if (e.load == LOAD_TRUE) {
                reporter.report(" - trying to load: ", e.name);
                File f = new File(tmpDir, e.name);
                rt.load(f.getAbsolutePath());
                reporter.report(" - ok!  load=\"true\"");
            }
        }
    }


    private static String jniLibraryName(String lib) {
        switch (Utilities.operatingSystem) {
        case Windows: return lib + ".dll";
        case MacOSX: return "lib" + lib + ".jnilib";
        case Linux:
        case FreeBSD: return "lib" + lib + ".so";
        }
        throw new RuntimeException("Unreachable statement");
    }


    private static String qtLibraryName(String lib, String version) {
        switch (Utilities.operatingSystem) {
        case Windows:
            return Utilities.configuration == Utilities.Configuration.Debug
                ? lib + "d" + version + ".dll"
                : lib + version + ".dll";
        case MacOSX:
            return Utilities.configuration == Utilities.Configuration.Debug
                ? "lib" + lib + "_debug." + version + ".dylib"
                : "lib" + lib + "." + version + ".dylib";
        case Linux:
        case FreeBSD:
            // Linux doesn't have a dedicated "debug" library since 4.2
            return "lib" + lib + ".so." + version;
        }
        throw new RuntimeException("Unreachable statement");
    }

    /**
     * Copies the data in the inputstream into the output stream.
     * @param in The source.
     * @param out The destination.
     *
     * @throws IOException when there is a problem...
     */
    private static boolean copy(InputStream in, OutputStream out) throws IOException {
        boolean bf = false;

        try {
            byte buffer[] = new byte[1024 * 8];
            int n;
            while((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }

            out.close();
            out = null;
            in.close();
            in = null;

            bf = true;
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch(IOException eat) {
                }
                out = null;
            }
            if(in != null) {
                try {
                    in.close();
                } catch(IOException eat) {
                }
                in = null;
            }
        }
        return bf;
    }

    public static boolean isUsingDeploymentSpec() {
        unpack();
        return deploymentSpecs != null && deploymentSpecs.size() != 0;
    }

    private static Map<String, LibraryEntry> libraryMap = new HashMap<String, LibraryEntry>();
    private static Map<String, LibraryEntry> neverLoad = new HashMap<String, LibraryEntry>();
    private static List<DeploymentSpec> deploymentSpecs = new ArrayList<DeploymentSpec>();
    private static Reporter reporter = new Reporter();

    private static boolean unpacked = false;

    public static void main(String args[]) throws Exception {

        unpack();

        loadQtLibrary("QtCore");
        loadQtLibrary("QtGui");
        loadLibrary("qtjambi");
        loadLibrary("com_trolltech_qt_core");
        loadLibrary("com_trolltech_qt_gui");
        loadQtLibrary("QtGui");
        loadQtLibrary("QtNetwork");
//         loadLibrary("com_trolltech_qt_network");

        for (String s : pluginPaths())
            System.out.println("PluginPath: " + s);

    }

}
