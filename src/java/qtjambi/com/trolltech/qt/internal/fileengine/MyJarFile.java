
package com.trolltech.qt.internal.fileengine;

import java.io.InputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

// Package private class and methods
class MyJarFile {
    private URLConnection urlConnection;
    private JarFile jarFile;
    private int refCount;

    // This contructor may never throw an exception
    MyJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
        this.refCount = 1;
    }

    MyJarFile(URLConnection urlConnection, JarFile jarFile) {
        this.urlConnection = urlConnection;
        this.jarFile = jarFile;
        this.refCount = 1;
    }

    // This method may never throw an exception
    void get() {
        synchronized(this) {
            refCount++;
        }
    }

    // This method may never throw an exception
    void put() {
        JarFile closeJarFile = null;
        synchronized(this) {
            refCount--;
            if(refCount == 0) {
                closeJarFile = jarFile;
                jarFile = null;
            }
        }
        if(closeJarFile != null) {
            try {
                closeJarFile.close();
            } catch(IOException eat) {
            }
            if(urlConnection != null) {
                urlConnection = null;
            }
        }
    }

    String getName() {
        return jarFile.getName();
    }

    Enumeration<JarEntry> entries() {
        return jarFile.entries();
    }

    JarEntry getJarEntry(String name) {
        return jarFile.getJarEntry(name);
    }

    InputStream getInputStream(ZipEntry ze) throws IOException {
        return jarFile.getInputStream(ze);
    }
}
