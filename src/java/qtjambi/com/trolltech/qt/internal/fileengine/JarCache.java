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

package com.trolltech.qt.internal.fileengine;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class JarCache {
    // Must only be called with lock.writeLock() held.
    private static void add(HashMap<String, List<JarFile>> lookupCache, String dirName, JarFile jarFile) {
        List<JarFile> files = lookupCache.get(dirName);
        if (files == null) {
            files = new ArrayList<JarFile>();
            files.add(jarFile);
            lookupCache.put(dirName, files);
        } else {
            // CHECKME: Why are we checking for the instance ?  not the the file/path ?
            if (!files.contains(jarFile))
                files.add(jarFile);
        }
    }

    public static void reset(Set<String> jarFileList) {
        Lock thisLock = lock.writeLock();
        thisLock.lock();
        try {
            // FIXME: Argh... we can't invalidate the previous list since there are an unknown
            //   number of random users out there with references to the JarFile's we've provided
            //   and they don't expect us to close their file handle from underneth them use.
            // We need a reference counter and all users must obtain/release their handle.
            // This means we need to perform a kind of merge operation to add new entries found here
            //   but keep that reference counting intact for old entries.
            //invalidateLocked();

            HashMap<String, List<JarFile>> tmpCache = new HashMap<String, List<JarFile>>();
            classPathDirs = new ArrayList<String>();

            for (String jarFileName : jarFileList) {
                JarURLConnection jarUrlConnection = null;
                JarFile file = null;
                try {
                    URL url = new URL("jar:" + jarFileName + "!/");
                    URLConnection urlConnection = url.openConnection();
                    if((urlConnection instanceof JarURLConnection) == false)
                        throw new RuntimeException("no a JarURLConnection: " + urlConnection.getClass().getName());
                    jarUrlConnection = (JarURLConnection) urlConnection;
                    file = jarUrlConnection.getJarFile();

                    // Add root dir for all jar files (event empty ones)
                    add(tmpCache, "", file);

                    Enumeration<JarEntry> entries = file.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();

                        String dirName = "";

                        String entryName = entry.getName();
                        if (entry.isDirectory()) {
                            if (entryName.endsWith("/"))
                                dirName = entryName.substring(0, entryName.length() - 1);
                            else
                                dirName = entryName;
                        } else {
                            int slashPos = entryName.lastIndexOf("/");
                            if (slashPos > 0)
                                dirName = entryName.substring(0, slashPos);
                        }

                        // Remove potentially initial '/'
                        if (dirName.startsWith("/"))
                            dirName = dirName.substring(1);

                        while (dirName != null) {
                            add(tmpCache, dirName, file);
                            int slashPos = dirName.lastIndexOf("/");
                            if (slashPos > 0)
                                dirName = dirName.substring(0, slashPos);
                            else
                                dirName = null;
                        }
                    }

                    // Make sure all files are registered under the empty
                    // since all have roots
                    add(tmpCache, "", file);
                } catch (Exception e) {
                    // Expected as directories will fail when doing openConnection.getJarFile()
                    // Note that ZipFile throws different types of run time exceptions on different
                    // platforms (ZipException on Linux and FileNotFoundException on Windows)
                    classPathDirs.add(jarFileName);
                } finally {
                }
            }

            cache = tmpCache;

//         for (String s : cache.keySet()) {
//             System.out.println(s);
//             for (JarFile f : cache.get(s)) {
//                 System.out.println(" - '" + f.getName() + "'");
//             }
//         }
        } finally {
            thisLock.unlock();
        }
    }

    public static List<JarFile> jarFiles(String entry) {
        List<JarFile> files = null;
        Lock thisLock = lock.readLock();
        thisLock.lock();
        try {
            if(cache != null)
                files = cache.get(entry);
        } finally {
            thisLock.unlock();
        }
        return files;
    }

    private static void invalidateLocked() {
        if(cache == null)
            return;
        for(Map.Entry<String, List<JarFile>> entry : cache.entrySet()) {
            String key = entry.getKey();
            List<JarFile> value = entry.getValue();
            for(JarFile jarFile : value) {
                try {
                    jarFile.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        cache.clear();
        cache = null;
    }

    public static void invalidate() {
        Lock thisLock = lock.writeLock();
        thisLock.lock();
        try {
            invalidateLocked();
        } finally {
            thisLock.unlock();
        }
    }

    public static List<String> classPathDirs() {
        return classPathDirs;
    }

    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static HashMap<String, List<JarFile>> cache;
    private static List<String> classPathDirs;
}
