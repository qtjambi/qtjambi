package com.trolltech.qt.internal;


import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;
import java.security.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

class DeploymentSpecException extends RuntimeException {
    public DeploymentSpecException(String msg) {
        super(msg);
    }
}

public class NativeLibraryManager {

    private static final boolean REPORT = true;

    private static class LibraryEntry {
        public String name;
        public boolean load;
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
            if (REPORT) report(" - plugin path='" + path + "'");
        }

        public void addLibraryEntry(LibraryEntry e) {
            if (libraries == null)
                libraries = new ArrayList<LibraryEntry>();
            libraries.add(e);
            if (REPORT) report(" - library: name='" + e.name + "', load=" + e.load);
        }

        public void dump() {
            System.out.println("Deployment spec:");
            System.out.println(" - key: " + key);
            System.out.println(" - jar: " + jar);
            for (LibraryEntry e : libraries) {
                System.out.println("   - library: " + e.name + ", load=" + e.load);
            }
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
                if (REPORT) report(" - cache key='" + spec.key + "'");
            } else if (name.equals("library")) {
                LibraryEntry e = new LibraryEntry();
                e.name = attributes.getValue("name");
                String load = attributes.getValue("load");
                e.load = load == null || !load.equals("false");
                if (e.name == null) {
                    throw new DeploymentSpecException("<library> element missing required attribute \"name\"");
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


    private static void checkArchive(JarFile file) throws Exception {

        if (REPORT) report("Checking Archive '" + file.getName() + "'");

        JarEntry descriptor = file.getJarEntry("qtjambi-deployment.xml");
        if (descriptor == null) {
            if (REPORT) report(" - does not contain 'qtjambi-deployment.jar', skipping");
            return;
        }

        DeploymentSpec spec = new DeploymentSpec();
        spec.jar = file;

        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser parser = fact.newSAXParser();

        XMLHandler handler = new XMLHandler();
        handler.spec = spec;

        parser.parse(file.getInputStream(descriptor), handler);
    }

    private static byte[] md5(URI jarFile) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte data[] = new byte[1024];
        FileInputStream stream = new FileInputStream(new File(jarFile));

        int read;
        while ((read = stream.read(data)) > 0) {
            md.update(data);
        }

        stream.close();

        return md.digest();

    }

    private static void report(String msg) {
        System.out.println(msg);
    }

    public static void main(String args[]) throws Exception {

        String fileName;

        if (args.length >= 1) {
            fileName = args[0];
        } else {
            System.out.println("   USAGE:\n" +
                               " > java " + NativeLibraryManager.class.getName() + " [xmlfile]");
            return;
        }

//         checkArchive(new JarFile(fileName));

        URI jarFile = new URI(fileName);

        long l = System.currentTimeMillis();

        byte checksum[] = md5(jarFile);

        String s = "";
        for (int i=0; i<checksum.length; ++i)
            s += checksum[i] + " ";

        System.out.println(s + "\ntook: " + (System.currentTimeMillis() - l));

    }

}