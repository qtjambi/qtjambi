package com.trolltech.qt.internal;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;
import java.security.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.trolltech.qt.Utilities;


class DeploymentSpecException extends RuntimeException {
    public DeploymentSpecException(String msg) {
        super(msg);
    }
}

public class NativeLibraryManager {

    public static String DEPLOY_DESCRIPTOR_NAME = "qtjambi-deployment.xml";

    private static final boolean REPORT = true;

    private static final int LOAD_TRUE = 1;
    private static final int LOAD_FALSE = 2;
    private static final int LOAD_NEVER = 3;

    private static class LibraryEntry {
        public String name;
        public int load;
        public DeploymentSpec spec;
    }


    private static class DeploymentSpec {
        public String key;
        public JarFile jar;
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

                if (e.load != LOAD_NEVER) {
                    // Add library name to the global map of libraries...
                    String fileName = new File(e.name).getName();
                    LibraryEntry old = libraryMap.get(fileName);
                    if (old != null) {
                        throw new DeploymentSpecException("<library> '" + e.name
                                                          + "' is duplicated. Present in both '"
                                                          + spec.jar.getName() + "' and '"
                                                          + old.spec.jar.getName() + "'.");
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
            }
        }
    }

    private static class ChecksumFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".chk");
        }
    }


    private static DeploymentSpec readDeploySpec(JarFile file) throws Exception {
        reporter.report("Checking Archive '", file.getName(), "'");

        JarEntry descriptor = file.getJarEntry(DEPLOY_DESCRIPTOR_NAME);
        if (descriptor == null) {
            reporter.report(" - does not contain '", DEPLOY_DESCRIPTOR_NAME, "', skipping");
            return null;
        }

        DeploymentSpec spec = new DeploymentSpec();
        spec.jar = file;

        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser parser = fact.newSAXParser();

        XMLHandler handler = new XMLHandler();
        handler.spec = spec;

        parser.parse(file.getInputStream(descriptor), handler);

        if (spec.key == null) {
            throw new DeploymentSpecException("Deployment Specification doesn't include required <cache key='...'/>");
        }

        deploymentSpecs.add(spec);

        return spec;
    }


    private static String md5(URI name) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte data[] = new byte[1024];
        File file = new File(name);
        InputStream stream = new FileInputStream(file);

        int skip = stream.available() / 5;
        int read;
        while ((read = stream.read(data)) > 0) {
            md.update(data);
            stream.skip(skip - data.length);
        }
        stream.close();

        // Include the deployment descriptor in the md5 sum...
        JarFile jarFile = new JarFile(file);
        JarEntry entry = jarFile.getJarEntry(DEPLOY_DESCRIPTOR_NAME);
        stream = jarFile.getInputStream(entry);
        while ((read = stream.read(data)) > 0) {
            md.update(data);
        }
        stream.close();

        byte digest[] = md.digest();

        // Construct the md5 string...
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<digest.length; ++i) {
            int val = digest[i];
            val += 127;

            if (val == 0) buffer.append('0');
            if (val <= 0x0f) buffer.append('0');

            buffer.append(Integer.toString(digest[i] + 127, 16));
        }

        return buffer.toString();
    }


    public static void unpackJarFile(URI name) throws Exception {
        try {
            unpackJarFile_helper(name);
        } catch (Exception e) {
            throw new Exception("Failed to unpack .jar file, progress so far:\n"
                                + reporter.toString(), e);
        }
    }


    private static void unpackJarFile_helper(URI name) throws Exception {

        reporter.report("Unpacking .jar file: '", name.toString(), "'");

        File file = new File(name);
        JarFile jarFile = new JarFile(file);

        DeploymentSpec spec = readDeploySpec(jarFile);
        File tmpDir = jambiTempDirBase(spec.key);

        reporter.report(" - using cache directory: '", tmpDir.getAbsolutePath(), "'");

        String md5 = md5(name);

        boolean shouldCopy = false;

        // If the dir exists, sanity check the contents...
        if (tmpDir.exists()) {
            reporter.report(" - cache directory exists");

            File files[] = tmpDir.listFiles(new ChecksumFileFilter());

            if (files == null || files.length == 0) {
                reporter.report(" - cache directory doesn't have .chk file");
                shouldCopy = true;

            } else if (files.length != 1) {
                throw new RuntimeException(" - cache directory '" + tmpDir
                                           + "'has multiple .chk's files. Loading aborted!");

            } else if (new File(tmpDir, md5 + ".chk").equals(files[0])) {
                reporter.report(" - checksum ok!");

            } else {
                throw new RuntimeException("Failed to unpack contents of .jar file '"
                                           + name.toString() + "'. The cacke key '"
                                           + spec.key + "' is already in use with a different "
                                           + "set of native libraries. Please use a unique cache key!");
            }

        } else {
            shouldCopy = true;
        }

        // If the dir doesn't exists or it was only half completed, copy the files over...
        if (shouldCopy) {
            reporter.report(" - starting to copy content to cache directory...");

            for (LibraryEntry e : spec.libraries) {
                reporter.report(" - copying over: '", e.name, "'...");
                JarEntry entry = jarFile.getJarEntry(e.name);
                if (entry == null) {
                    throw new FileNotFoundException("Library '" + e.name
                                                    + "' specified in qtjambi-deployment.xml in '"
                                                    + name + "' does not exist");
                }
                InputStream in = jarFile.getInputStream(entry);

                File outFile = new File(tmpDir, e.name);
                File outFileDir = outFile.getParentFile();
                if (!outFileDir.exists()) {
                    reporter.report(" - creating directory: ", outFileDir.getAbsolutePath());
                    outFileDir.mkdirs();
                }

                OutputStream out = new FileOutputStream(new File(tmpDir, e.name));
                copy(in, out);
            }
        }


        // Load the libraries tagged for loading...
        Runtime rt = Runtime.getRuntime();
        for (LibraryEntry e : spec.libraries) {
            if (e.load == LOAD_TRUE) {
                reporter.report(" - trying to load: ", e.name);
                File f = new File(tmpDir, e.name);
                rt.load(f.getAbsolutePath());
                reporter.report(" - ok!");
            }
        }

        // plugin paths need to be handled somehow...
        File chk = new File(tmpDir, md5 + ".chk");
        chk.createNewFile();
    }


    public static File jambiTempDirBase(String key) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String user = System.getProperty("user.name");
        String arch = System.getProperty("os.arch");
        return new File(tmpDir, "QtJambi_" + user + "_" + arch + "_" + Utilities.VERSION_STRING + "_" + key);
    }


    public static List<String> pluginPaths() {
        List<String> paths = new ArrayList<String>();
        for (DeploymentSpec spec : deploymentSpecs) {
            File root = jambiTempDirBase(spec.key);
            for (String path : spec.pluginPaths)
                paths.add(new File(root, path).getAbsolutePath());
        }
        return paths;
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


    private static Map<String, LibraryEntry> libraryMap = new HashMap<String, LibraryEntry>();
    private static List<DeploymentSpec> deploymentSpecs = new ArrayList<DeploymentSpec>();
    private static Reporter reporter = new Reporter();

    public static void main(String args[]) throws Exception {
        String fileName;

        if (args.length >= 1) {
            fileName = args[0];
        } else {
            System.out.println("   USAGE:\n" +
                               " > java " + NativeLibraryManager.class.getName() + " [xmlfile]");
            return;
        }

        unpackJarFile(new URI(fileName));

        for (String s : pluginPaths())
            System.out.println("PluginPath: " + s);

        System.out.println(reporter.toString());
    }

}