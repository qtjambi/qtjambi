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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QAbstractFileEngineIterator;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QIODevice;

public class QClassPathEngine extends QAbstractFileEngine
{
    public final static String FileNameDelim = "#";
    public final static String FileNameIndicator = "classpath";
    public final static String FileNamePrefix = FileNameIndicator + ":";

    private static HashSet<String> classpaths;

    private String m_fileName = "";
    private String m_baseName = "";
    private String m_selectedSource = "*";
    private List<QAbstractFileEngine> m_engines = new LinkedList<QAbstractFileEngine>();

    private static String currentDirectory;

    public QClassPathEngine(String fileName) {
        setFileName(fileName);
    }

    private static String resolveCurrentDirectory() {
        String tmpCurrentDirectory = currentDirectory;
        if(tmpCurrentDirectory != null)
            return tmpCurrentDirectory;

        synchronized(QClassPathEngine.class) {
            tmpCurrentDirectory = currentDirectory;
            // retest
            if(tmpCurrentDirectory != null)
                return tmpCurrentDirectory;

            File fileCurDir = new File(".");
            if(fileCurDir.isDirectory()) {
                tmpCurrentDirectory = fileCurDir.getAbsolutePath();
            } else {
                tmpCurrentDirectory = System.getProperty("user.dir");
            }

            currentDirectory = tmpCurrentDirectory;
        }
        return tmpCurrentDirectory;
    }

    static void setCurrentDirectory(String newCurrentDirectory) {
        synchronized(QClassPathEngine.class) {
            currentDirectory = newCurrentDirectory;
        }
    }

    private static String makeUrl(String path) {
        String goodPath = null;

        // FIXME: Need special handling of "." to mean current directory.  But when
        //  conversion process should be controlled.  Cwd at app startup?

        if(path == null)
            return goodPath;
        final int pathLength = path.length();

        boolean skipTryAsis = false;	// attempt to not use exceptions for common situations
        if(pathLength > 0) {
            char firstChar = path.charAt(0);
            // Both a "/" and "\\" are illegal characters in the scheme/protocol.
            if(firstChar == File.separatorChar) {
                skipTryAsis = true;
            } else if(firstChar == '.') {
                // Special case for current directory
                String tmpPath = resolveCurrentDirectory();
                if(tmpPath != null) {
                    path = tmpPath;
                    skipTryAsis = true;
                }
            } else if(pathLength > 2) {
                // Windows "C:\\..." for which "\\" is incorrect for URLs
                char secondChar = path.charAt(1);
                char thirdChar = path.charAt(2);
                if((firstChar >= 'A' && firstChar <= 'Z') || (firstChar >= 'a' && firstChar <= 'z')) {
                    // We don't check for '/' since that might be a real URL "a://host:port/path?qs"
                    // and would be invalid for windows using java.io.File API anyway.
                    if(secondChar == ':' && thirdChar == '\\')
                        skipTryAsis = true;
                }
            }
        }

        Exception urlParseException = null;
        if(goodPath == null && skipTryAsis == false) {
            try {
                // See if the data passed is a well-formed URL
                // Relative paths end up here and throwing exceptions
                // Maybe we should look for "://" in path before using this method?
                // What is the resolution mechanism for using URL(path) with arbitrary string?
                URL url = new URL(path);
                if(url.getProtocol().length() > 0)
                    goodPath = path;
            } catch(Exception e) {
                urlParseException = e;
                //e.printStackTrace();
            }
        }

        Exception urlParseException2 = null;
        if(goodPath == null) {
            try {
                // Validate the URL we build is well-formed
                // FIXME: file://
                String tmpPath = "file:" + path;
                URL url = new URL(tmpPath);
                if(url.getProtocol().length() > 0)
                    goodPath = tmpPath;
            } catch(Exception e) {
                urlParseException2 = e;
                //e.printStackTrace();
            }
        }

        if(goodPath == null) {
            File f = new File(path);
            System.out.println("makeUrl(path=" + path + ") exists()=" + f.exists() + "; isDirectory()=" + f.isDirectory() + "; isFile()=" + f.isFile());
            if(urlParseException != null)
                urlParseException.printStackTrace();
            if(urlParseException2 != null)
                urlParseException2.printStackTrace();
        }

        return goodPath;
    }

    public static boolean addSearchPath(String path, boolean allowDuplicate) {
        boolean bf = false;
        String urlString = makeUrl(path);
        synchronized(QClassPathEngine.class){
            // FIXME: This should not be here, it should execute once by default
            // and once each time the user explicitly request such.  The user should
            // be allowed to add
            if(classpaths == null)
                findClassPaths();

            // Do not disurb the order of the existing classpaths, such
            // things are sensitive matters.
            if(urlString != null) {
                // FWIW this is a Set so we can't duplicate, but we probably should be a list
                if(allowDuplicate || classpaths.contains(urlString) == false) {
                    classpaths.add(urlString);
                    bf = true;
                }
            }

            JarCache.reset(classpaths);
        }
        return bf;
    }

    public static boolean removeSearchPath(String path) {
        boolean bf = false;
        synchronized(QClassPathEngine.class){
            if(classpaths != null) {
                String urlString = makeUrl(path);
                if(urlString != null) {
                    bf = classpaths.remove(urlString);
                    JarCache.reset(classpaths);
                }
            }
        }
        return bf;
    }

    @Override
    public void setFileName(String fileName)
    {
        if (fileName.equals(fileName()))
            return;

        cleanUp();
        if (!fileName.startsWith(FileNamePrefix))
            throw new IllegalArgumentException("Invalid format of path: '" + fileName + "'");
        m_fileName = fileName.substring(FileNamePrefix.length());

        String searchPath[] = RetroTranslatorHelper.split(m_fileName, "#", 2);

        m_selectedSource = "*";
        if (searchPath.length == 1) {
            m_baseName = searchPath[0];
        } else {
            m_baseName = searchPath[1];
            m_selectedSource = searchPath[0];
        }

        int first = 0;
        int last = m_baseName.length();

        while (first < last && m_baseName.charAt(first) == '/')
            ++first;
        if (m_baseName.endsWith("/"))
            --last;

        if (last < first)
            m_baseName = "";
        else
            m_baseName = m_baseName.substring(first, last).replace('\\', '/');

        if (classpaths == null)
            findClassPaths();

        if (m_selectedSource.equals("*")) {
            List<JarFile> potentialJars = JarCache.jarFiles(m_baseName);

            if (potentialJars != null) { // Its at least a directory which exists in jar files
                for (JarFile path : potentialJars) {
                    addJarFileFromPath(path, m_baseName, true);
                }
            } else { // Its a file or directory, look for jar files which contains its a directory

                String parentSearch;

                int pos = m_baseName.lastIndexOf("/");
                if (pos < 0)
                    parentSearch = "";
                else
                    parentSearch = m_baseName.substring(0, pos);

                List<JarFile> parentDirJars = JarCache.jarFiles(parentSearch);
                if (parentDirJars != null) {
                    for (JarFile path : parentDirJars) {
                        addJarFileFromPath(path, m_baseName, false);
                    }
                }

                for (String path : JarCache.classPathDirs()) try {
                    addFromPath(new URL(makeUrl(path)), m_baseName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else
            try {
                String urlString = makeUrl(m_selectedSource);

                // If it's a file (it should be), strip away the getProtocol() and check whether the
                // file is a directory. Otherwise it's assumed to be a .jar file
                URL url = new URL(urlString);
                File file;
                if(url.getProtocol().equals("file")) {
                    file = new File(url.getFile());
                    if(file.isDirectory()) {
                    }
                }
                if (urlString.startsWith("file:") && new File(urlString.substring(5)).isDirectory())
                    addFromPath(url, m_baseName);
                else
                    addJarFileFromPath(new URL("jar:" + urlString + "!/"), m_baseName);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public boolean copy(String newName)
    {
        if (m_engines.size() > 0)
            return m_engines.get(0).copy(newName);
        else
            return false;
    }

    @Override
    public boolean setPermissions(int perms)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.setPermissions(perms))
                return true;
        }

        return false;
    }

    @Override
    public boolean caseSensitive()
    {
        return true;
    }

    @Override
    public boolean close()
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).close();
    }

    @Override
    public List<String> entryList(QDir.Filters filters, List<String> filterNames)
    {
        List<String> result = null;
        for (QAbstractFileEngine engine : m_engines) {
            if (result == null) {
                result = engine.entryList(filters, filterNames);
            } else {
                List<String> list = engine.entryList(filters, filterNames);
                result.removeAll(list);
                result.addAll(list);
            }
        }

        return result;
    }

    @Override
    public FileFlags fileFlags(FileFlags type)
    {
        FileFlags flags = new FileFlags();

        for (QAbstractFileEngine engine : m_engines)
            flags.set(engine.fileFlags(type));

        if (fileName(FileName.PathName).equals("/"))
            flags.set(QAbstractFileEngine.FileFlag.RootFlag);

        flags.clear(FileFlag.LocalDiskFlag);

        return flags;
    }

    @Override
    public String fileName(FileName file)
    {
        if (m_engines.size() == 0) {
            return "";
        }

        String classPathEntry = "";
        if (m_engines.size() == 1) {
            QAbstractFileEngine engine = m_engines.get(0);

            if (engine instanceof QClassPathEntry)
                classPathEntry = ((QClassPathEntry) engine).classPathEntryName();
            else
                throw new RuntimeException("Bogus engine in class path file engine");

        } else {
            classPathEntry = "*";
        }

        String result = "";
        if (file == FileName.DefaultName) {
            result = QClassPathEngine.FileNamePrefix + m_fileName;
        } else if (file == FileName.CanonicalName || file == FileName.LinkName) {
            result = fileName(FileName.CanonicalPathName) + "/" + fileName(FileName.BaseName);
        } else if (file == FileName.AbsoluteName || file == FileName.LinkName) {
            result = QClassPathEngine.FileNamePrefix + classPathEntry + FileNameDelim + m_baseName;
        } else if (file == FileName.BaseName) {
            int pos = m_baseName.lastIndexOf("/");
            result = pos > 0 ? m_baseName.substring(pos + 1) : m_baseName;
        } else if (file == FileName.PathName) {
            int pos = m_baseName.lastIndexOf("/");
            result = pos > 0 ? m_baseName.substring(0, pos) : "";
        } else if (file == FileName.AbsolutePathName) {
            result = QClassPathEngine.FileNamePrefix + classPathEntry + FileNameDelim + fileName(FileName.PathName);
        } else if (file == FileName.CanonicalPathName) {
            result = m_engines.get(0).fileName(file);
        } else {
            throw new IllegalArgumentException("Unknown file name type: " + file);
        }


        return result;
    }

    @Override
    public QDateTime fileTime(FileTime time)
    {
        if (m_engines.size() == 0)
            return new QDateTime();
        else
            return m_engines.get(0).fileTime(time);
    }

    @Override
    public boolean link(String newName)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.link(newName))
                return true;
        }
        return false;
    }

    @Override
    public boolean mkdir(String dirName, boolean createParentDirectories)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.mkdir(dirName, createParentDirectories))
                return true;
        }
        return false;
    }

    @Override
    public boolean open(QIODevice.OpenMode openMode)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).open(openMode);
    }

    @Override
    public long pos()
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).pos();
    }

    @Override
    public long read(QNativePointer data, long maxlen)
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).read(data, maxlen);
    }

    @Override
    public long readLine(QNativePointer data, long maxlen)
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).readLine(data, maxlen);
    }

    @Override
    public boolean remove()
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.remove();
        return ok;
    }

    @Override
    public boolean rename(String newName)
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.rename(newName);
        return ok;
    }

    @Override
    public boolean rmdir(String dirName, boolean recursive)
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.rmdir(dirName, recursive);
        return ok;
    }

    @Override
    public boolean seek(long offset)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).seek(offset);
    }

    @Override
    public String owner(FileOwner owner)
    {
        String result = "";
        int i = 0;
        while (result.length() == 0 && i < m_engines.size())
            result = m_engines.get(i++).owner(owner);

        return result;
    }

    @Override
    public int ownerId(FileOwner owner)
    {
        int result = -2;
        int i = 0;
        while (result == -2 && i < m_engines.size())
            result = m_engines.get(i++).ownerId(owner);

        return result;
    }

    @Override
    public boolean isRelativePath()
    {
        return false;
    }

    @Override
    public boolean isSequential()
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.isSequential())
                return true;
        }

        return false;
    }

    @Override
    public boolean setSize(long sz)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).setSize(sz);
    }

    @Override
    public long size()
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).size();
    }

    @Override
    public long write(QNativePointer data, long len)
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).write(data, len);
    }

    private void cleanUp()
    {
        if (m_engines != null)
            m_engines.clear();
    }

    private void addFromPath(URL url, String fileName)
    {
        String qtified_path = url.getFile().replace('\\', '/');

        // If it is a plain file on the disk, just read it from the disk
        if (url.getProtocol().equals("file")) {
            QFileInfo file = new QFileInfo(qtified_path);
            if (file.isDir()
                    && file.exists()
                    && new QFileInfo(qtified_path + "/" + fileName).exists()) {
                addEngine(new QFSEntryEngine(qtified_path + "/" + fileName, url.toExternalForm()));
                return ;
            }
        }
    }

    private void addJarFileFromPath(JarFile jarFile, String fileName, boolean directory)
    {
        QJarEntryEngine engine = new QJarEntryEngine(jarFile, fileName, directory);
        if (engine.isValid())
            addEngine(engine);
    }

    /**
     * The JarEntry.isDirectory() method in Java returns false
     * even for directories, so we need this extra check
     * which tries to read a byte from the entry in order
     * to trigger an exception when the entry is a directory.
     */
    static boolean checkIsDirectory(JarFile jarFile, JarEntry fileInJar) {
        InputStream inStream = null;
        try {
            inStream = jarFile.getInputStream(fileInJar);
            if(inStream == null)
                return true;	// avoid NPE
            inStream.read();
        } catch(IOException e) {
            return true;
        } catch(Exception e) {	// NPE
            return true;
        } finally {
            if(inStream != null) {
                try {
                    inStream.close();
                } catch(IOException eat) {
                }
                inStream = null;
            }
        }

        return false;
    }

    private void addJarFileFromPath(URL jarFileURL, String fileName) {
        URLConnection urlConnection = null;
        JarFile jarFile = null;
        try {
            urlConnection = jarFileURL.openConnection();
            if((urlConnection instanceof JarURLConnection) == false)
                throw new RuntimeException("not a JarURLConnection type: " + urlConnection.getClass().getName());
            JarURLConnection jarUrlConnection = (JarURLConnection) urlConnection;
            try {
                jarFile = jarUrlConnection.getJarFile();
            } catch(ZipException e) {
                // This often fails with "java.util.zip.ZipException: error in opening zip file" but never discloses the filename
                throw new ZipException(e.getMessage() + ": " + jarFileURL);
            }

            boolean isDirectory = false;
            JarEntry fileInJar = jarFile.getJarEntry(fileName);

            // If the entry exists in the given file, look it up and
            // check if its a dir or not
            if (fileInJar != null) {
                isDirectory = fileInJar.isDirectory();
                if (!isDirectory) {
                    boolean tmpIsDirectory = checkIsDirectory(jarFile, fileInJar);
                    isDirectory = tmpIsDirectory;
                } else {
                }
            }

            if (!isDirectory) {
                // Otherwise, look if the directory exists in the
                // cache...
                List<JarFile> files = JarCache.jarFiles(fileName);
                String jarFileName = jarFile.getName();
                if (files != null) {
                    for (JarFile f : files) {
                        if (f.getName().equals(jarFileName)) {
                            isDirectory = true;
                            break;
                        }
                    }
                }

                // Nasty fallback... Iterate through the .jar file and try to check if
                // fileName is the prefix (hence directory) of any of the entries...
                if (!isDirectory) {
                    String fileNameWithSlash = fileName + "/";
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.startsWith(fileNameWithSlash)) {
                            isDirectory = true;
                            break;
                        }
                    }

                }
            }

            addJarFileFromPath(jarFile, fileName, isDirectory);
            jarFile = null;	// CHECKME invalidate close!

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(jarFile != null) {
                jarFile = null;
            }
            if(urlConnection != null) {
                urlConnection = null;
            }
        }
    }

    private static void urlConnectionCloser(URLConnection conn) {
        if(conn instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
            httpURLConnection.disconnect();
            return;
        }
        if(conn instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) conn;
            return;
        }
    }

    private void addEngine(QAbstractFileEngine engine)
    {
        if (m_engines == null)
            m_engines = new LinkedList<QAbstractFileEngine>();

        m_engines.add(engine);
    }



    private static void findClassPaths() {
        synchronized(QClassPathEngine.class) {
            classpaths = new HashSet<String>();

            List<URL> cpUrls = new ArrayList<URL>();

            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null)
                    loader = QClassPathFileEngineHandler.class.getClassLoader();

                Enumeration<URL> urls = loader.getResources("META-INF/MANIFEST.MF");
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if (url.getProtocol().equals("jar")) try {
                        String f = url.getFile();
                        int bang = f.indexOf("!");
                        if (bang >= 0)
                            f = f.substring(0, bang);

                        if (f.trim().length() > 0) {
                            classpaths.add(f);
                            cpUrls.add(new URL(f));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String paths[] = RetroTranslatorHelper.split(System.getProperty("java.class.path"),
                                                         File.pathSeparator);

            // Only add the .jar files that are not already added...
            int k=0;
            for (String p : paths) {
                if (p.trim().length() > 0) {
                    k++; // count all paths, invalid and valid

                    String url = makeUrl(p);
                    boolean match = false;

                    URLConnection urlConnection2 = null;
                    JarFile jarFile2 = null;
                    URLConnection urlConnection1 = null;
                    JarFile jarFile1 = null;
                    try {
                        URL url2 = new URL("jar:" + url.toString() + "!/");
                        urlConnection2 = url2.openConnection();
                        if(!(urlConnection2 instanceof JarURLConnection))
                            throw new RuntimeException("not a JarURLConnection type: " + urlConnection2.getClass().getName());
                        JarURLConnection jarUrlConnection2 = (JarURLConnection) urlConnection2;
                        try {
                            jarFile2 = jarUrlConnection2.getJarFile();
                        } catch(ZipException e) {
                            // This often fails with "java.util.zip.ZipException: error in opening zip file" but never discloses the filename
                            throw new ZipException(e.getMessage() + ": " + url2);
                        }

                        for (URL otherURL : cpUrls) {
                            URL url1 = new URL("jar:" + otherURL.toString() + "!/");
                            urlConnection1 = url1.openConnection();
                            if(!(urlConnection1 instanceof JarURLConnection))
                                throw new RuntimeException("not a JarURLConnection type: " + urlConnection1.getClass().getName());
                            JarURLConnection jarUrlConnection1 = (JarURLConnection) urlConnection1;
                            try {
                                jarFile1 = jarUrlConnection1.getJarFile();
                            } catch(ZipException e) {
                                // This often fails with "java.util.zip.ZipException: error in opening zip file" but never discloses the filename
                                throw new ZipException(e.getMessage() + ": " + url1);
                            }

                            File file1 = new File(jarFile1.getName());
                            File file2 = new File(jarFile2.getName());
                            if (file1.getCanonicalPath().equals(file2.getCanonicalPath())) {
                                match = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if(jarFile2 != null) {
                            jarFile2 = null;
                        }
                        if(urlConnection2 != null) {
                            urlConnection2 = null;
                        }
                        if(jarFile1 != null) {
                            jarFile1 = null;
                        }
                        if(urlConnection2 != null) {
                            urlConnection2 = null;
                        }
                    }

                    if (!match)
                        classpaths.add(url);
                }
            }

            // If there are no paths set in java.class.path, we do what Java does and
            // add the current directory; at least ask Java what the current directory
            // is not Qt.
            if (k == 0) {
                // FIXME: Use JVM cwd notion
                classpaths.add("file:" + QDir.currentPath());	// CHECKME "file://" ?
            }
        }
        JarCache.reset(classpaths);
    }
    
    @Override
    public QAbstractFileEngineIterator beginEntryList(QDir.Filters filters, java.util.List<String> nameFilters) {
    	String path = "";
    	// CHECKME: Doesn't this have to check for "classpath:" ?
    	if(m_baseName.startsWith("classpath"))
    		path = m_baseName;
    	else
    		path = "classpath:" + m_baseName;
        System.out.println("QClassPathEngine.beginEntryList(...) path=" + path);
    	return new QClassPathFileEngineIterator(path, filters, nameFilters);
    }
    
    @Override
    public QAbstractFileEngineIterator endEntryList() {
    	return null;
    }
}
