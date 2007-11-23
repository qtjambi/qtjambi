package com.trolltech.qt.internal;


import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

class DeploymentSpecException extends RuntimeException {
    public DeploymentSpecException(String msg) {
        super(msg);
    }
}

public class NativeLibraryManager {

    private static class LibraryEntry {
        public String name;
        public boolean load;
    }


    private static class DeploymentSpec {
        public String key;
        public String jar;
        public List<LibraryEntry> libraries = new ArrayList<LibraryEntry>();

//         public DeploymentSpec(XMLStreamReader xml) throws XMLStreamException {
//             parseNext(xml);
//         }

//         private void parseNext(XMLStreamReader xml) throws XMLStreamException {
//             int id = xml.next();

//             switch (id) {
//             case XMLStreamConstants.START_DOCUMENT:
//             case XMLStreamConstants.END_DOCUMENT:
//                 if (!name.equals("qtjambi-deploy"))
//                     throw new DeploymentSpecException("Deployment Specification does not have <qtjambi-deploy> as root");
//                 break;

//             case XMLStreamConstants.START_ELEMENT:
//                 String name = xml.getLocalName();
//                 if (name.equals("cache"))
//                     parseCache(xml);
//                 else if (name.equals("library"))
//                     parseLibrary(xml);
//             }

//         }

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
                                 Attributes attributes) {
            if (name.equals("cache")) {
                spec.key = attributes.getValue("key");
                if (spec.key == null) {
                    throw new DeploymentSpecException("<cache> element missing required attribute \"key\"");
                }
            } else if (name.equals("library")) {
                LibraryEntry e = new LibraryEntry();
                e.name = attributes.getValue("name");
                String load = attributes.getValue("load");
                e.load = load == null || !load.equals("false");
                if (e.name == null) {
                    throw new DeploymentSpecException("<library> element missing required attribute \"name\"");
                }
                spec.libraries.add(e);
            }
        }
    }

    public static void main(String args[]) {

        String fileName;

        if (args.length >= 1) {
            fileName = args[0];
        } else {
            System.out.println("   USAGE:\n" +
                               " > java " + NativeLibraryManager.class.getName() + " [xmlfile]");
            return;
        }

        try {
            InputStream stream = new BufferedInputStream(new FileInputStream(fileName));
            SAXParserFactory fact = SAXParserFactory.newInstance();
            SAXParser parser = fact.newSAXParser();

            XMLHandler handler = new XMLHandler();
            handler.spec = new DeploymentSpec();

            parser.parse(stream, handler);

            handler.spec.dump();
        } catch (Exception e)  {
            e.printStackTrace();
        }

    }

}