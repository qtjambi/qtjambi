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
import com.trolltech.qt.Utilities;
import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QAbstractFileEngineIterator;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QIODevice;

import com.trolltech.qt.internal.RetroTranslatorHelper;
import com.trolltech.qt.osinfo.OSInfo;

public class QClassPathEngine extends QAbstractFileEngine {
    public final static String FileNameDelim = "#";
    public final static String FileNameIndicator = "classpath";
    public final static String FileNamePrefix = FileNameIndicator + ":";

    // JarCache should not be global but instated here
    private static HashSet<String> classpaths;

    private String m_fileName = "";
    private String m_baseName = "";
    private String m_selectedSource = "*";
    private LinkedList<QAbstractFileEngine> m_engines = new LinkedList<QAbstractFileEngine>();  // ConcurrentLinkedList ?

    private static String currentDirectory;

    private String namespaceSchemePrefix = "classpath";

    public QClassPathEngine(String fileName) {
        setFileName(fileName);
    }

    public QClassPathEngine(String fileName, String namespaceSchemePrefix) {
        this.namespaceSchemePrefix = namespaceSchemePrefix;
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

            // FIXME: Maybe user.dir should be priority here ?

            File fileCurDir = new File(".");
            if(fileCurDir.isDirectory()) {
                // getParentFile() does not do the trick for us (it will/can be null in this circumstance)
                //  this method ensures it does not end with "/." or "\\." which is unwanted
                tmpCurrentDirectory = fileCurDir.getAbsolutePath();
                String removeDelimAndDot = File.separator + ".";  // "/." on unix, "\\." on windows
                if(tmpCurrentDirectory.endsWith(removeDelimAndDot))
                    tmpCurrentDirectory = tmpCurrentDirectory.substring(0, tmpCurrentDirectory.length() - removeDelimAndDot.length());
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

    // FIXME: This API needs improving, I think URL() can be heavy weight and I guess most
    //  user of this API probably want that.
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
                // FIXME: ../../foo/bar   ./foo/bar
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
                // Does a windows path C:/foo/bar/file.dat end up as scheme=C or protocol=C ?
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
String xPath = path.replace('\\', '/');
String xPrefix;
if(path.length() > 0 && xPath.charAt(0) != '/')
    xPrefix = "file:///";
else
    xPrefix = "file://";
                String newTmpPath = xPrefix;
                if(File.separatorChar == '\\')
                    newTmpPath += path.replace('\\', '/');  // windows
                else
                    newTmpPath += path;
                String newTmpPathY = Utilities.convertAbsolutePathStringToFileUrlString(path);
                URL url = new URL(newTmpPath);
                if(url.getProtocol().length() > 0) {
                     goodPath = newTmpPath;	// This must be converted to URL valid form like "file:///C:/foobar/cp"
                }
            } catch(Exception e) {
                urlParseException2 = e;
                //e.printStackTrace();
            }
        }

    URLConnection urlConn = null;
    InputStream inStream = null;
    try {
        URL openUrl = new URL(goodPath);
        urlConn = openUrl.openConnection();
        inStream = urlConn.getInputStream();
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        if(inStream != null) {
            try {
                inStream.close();
            } catch(IOException eat) {
            }
        }
    }
        return goodPath;
    }

    // Need API to add one/one-or-more, separate API to JarCache.reset()
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
                // when we are a list, we should ignore the re-add if at a lower-priority position, but take not if higher-priority
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
            List<String> pathToPotentialJars = JarCache.pathToJarFiles(m_baseName);

            if (pathToPotentialJars != null) { // Its at least a directory which exists in jar files
                for (String pathToJar : pathToPotentialJars) {
                    addJarFileFromPath(pathToJar, m_baseName, true);
                }
            } else { // Its a file or directory, look for jar files which contains its a directory

                String parentSearch;

                int pos = m_baseName.lastIndexOf("/");
                if (pos < 0)
                    parentSearch = "";
                else
                    parentSearch = m_baseName.substring(0, pos);

                // This is all wrong... we need to maintain the ordered list of the mix then attempt
                //  to populate from each in turn (if we are exhaustive) otherwise
                List<String> pathToJars = JarCache.pathToJarFiles(parentSearch);
                if (pathToJars != null) {
                    for (String pathToJar : pathToJars) {
                        addJarFileFromPath(pathToJar, m_baseName, false);
                    }
                }

                for (String path : JarCache.classPathDirs()) {
                    try {
                        // FIXME: This maybe already URL or raw dir, I think we should just make this a
                        //  dir in the native String format
                        addFromPath(new URL(makeUrl(path)), m_baseName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else
            try {
                String urlString = makeUrl(m_selectedSource);

                // If it's a file (it should be), strip away the getProtocol() and check whether the
                // file is a directory. Otherwise it's assumed to be a .jar file
                URL url = new URL(urlString);
                File fileOnlyIfDirectory = null;
                if(url.getProtocol().equals("file")) {
                    String pathString = url.getPath();
                    if(File.separatorChar == '\\')
                        pathString = pathString.replace('/', '\\');  // windows
                    fileOnlyIfDirectory = new File(pathString);
                    if(fileOnlyIfDirectory.isDirectory() == false)
                        fileOnlyIfDirectory = null;
                }
                if(fileOnlyIfDirectory != null)
                    addFromPath(url, m_baseName);
                else
                    addJarFileFromPath(new URL("jar:" + urlString + "!/"), m_baseName);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public boolean copy(String newName) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.copy(newName);
        return false;
    }

    @Override
    public boolean setPermissions(int perms) {
        synchronized(QClassPathEngine.class) {
            for(QAbstractFileEngine engine : m_engines) {
                if(engine.setPermissions(perms))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean caseSensitive() {
        return true;
    }

    @Override
    public boolean close() {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.close();     // FIXME: How does the closed engine get removed from the list ?
        return false;
    }

    @Override
    public List<String> entryList(QDir.Filters filters, List<String> filterNames) {
        List<String> result = null;
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines) {
                if (result == null) {
                    result = engine.entryList(filters, filterNames);
                } else {
                    List<String> list = engine.entryList(filters, filterNames);
                    // FIXME: Surely the higher precedence engines get asked first and the first found has priority over the last
                    //   so why do we removeAll() here.  list.removeAll(result);  result.addAll(list); ?
                    result.removeAll(list);
                    result.addAll(list);
                }
            }
        }

        return result;
    }

    @Override
    public FileFlags fileFlags(FileFlags type) {
        FileFlags flags = new FileFlags();

        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines)
                flags.set(engine.fileFlags(type));
        }

        if (fileName(FileName.PathName).equals("/"))
            flags.set(QAbstractFileEngine.FileFlag.RootFlag);

        flags.clear(FileFlag.LocalDiskFlag);

        return flags;
    }

    @Override
    public String fileName(FileName file) {
        QAbstractFileEngine afe = null;
        int engineCount;
        synchronized(QClassPathEngine.class) {
            engineCount = m_engines.size();
            if(engineCount > 0)
                afe = m_engines.getFirst();
        }
        if (engineCount == 0) {
            return "";
        }

        String classPathEntry;
        if (engineCount == 1) {
            if (afe instanceof QClassPathEntry)
                classPathEntry = ((QClassPathEntry) afe).classPathEntryName();
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
            // FIXME: can afe==null ?
            result = afe.fileName(file);
        } else {
            throw new IllegalArgumentException("Unknown file name type: " + file);
        }

        return result;
    }

    @Override
    public QDateTime fileTime(FileTime time) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.fileTime(time);
        return new QDateTime();
    }

    @Override
    public boolean link(String newName) {
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines) {
                if (engine.link(newName))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mkdir(String dirName, boolean createParentDirectories) {
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines) {
                if (engine.mkdir(dirName, createParentDirectories))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean open(QIODevice.OpenMode openMode) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.open(openMode);
        return false;
    }

    @Override
    public long pos() {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.pos();
        return -1;
    }

    @Override
    public long read(QNativePointer data, long maxlen) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.read(data, maxlen);
        return -1;
    }

    @Override
    public long readLine(QNativePointer data, long maxlen) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.readLine(data, maxlen);
        return -1;
    }

    @Override
    public boolean remove() {
        boolean ok = true;
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines)
                ok = ok && engine.remove();
        }
        return ok;
    }

    @Override
    public boolean rename(String newName) {
        boolean ok = true;
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines)
                ok = ok && engine.rename(newName);
        }
        return ok;
    }

    @Override
    public boolean rmdir(String dirName, boolean recursive) {
        boolean ok = true;
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines)
                ok = ok && engine.rmdir(dirName, recursive);
        }
        return ok;
    }

    @Override
    public boolean seek(long offset) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.seek(offset);
        return false;
    }

    @Override
    public String owner(FileOwner owner) {
        synchronized(QClassPathEngine.class) {
            for(QAbstractFileEngine afe : m_engines) {
                String result = afe.owner(owner);
                if(result != null && result.length() > 0)  // result.isEmpty() is Java 1.6+
                    return result;
            }
        }
        return ""; // FIXME: Why not null ?
    }

    @Override
    public int ownerId(FileOwner owner) {
        synchronized(QClassPathEngine.class) {
            for(QAbstractFileEngine afe : m_engines) {
                int result = afe.ownerId(owner);
                if (result != -2)
                    return result;
            }
        }
        return -2;
    }

    @Override
    public boolean isRelativePath() {
        return false;
    }

    @Override
    public boolean isSequential() {
        synchronized(QClassPathEngine.class) {
            for (QAbstractFileEngine engine : m_engines) {
                if (engine.isSequential())
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean setSize(long sz) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.setSize(sz);
        return false;
    }

    @Override
    public long size() {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return m_engines.get(0).size();
        return -1;
    }

    @Override
    public long write(QNativePointer data, long len) {
        QAbstractFileEngine afe = getFirstEngine();
        if(afe != null)
            return afe.write(data, len);
        return -1;
    }

    private void cleanUp() {
        synchronized(QClassPathEngine.class) {
            if (m_engines != null)
                m_engines.clear();
        }
    }

    // FIXME: private void addURLFromPath(URL url, String fileName) { }

    /**
     * 
     * @param url This maybe a file URL such as new URL("file:/C:/foobar/cp")
     * @param fileName
     * @return
     */
    private boolean addFromPath(URL url, String fileName) {
        String path = url.getPath();  // All URL paths have "/" separators
        if(OSInfo.isWindows()) {
            if(path.length() > 2 && path.charAt(2) == ':' && path.startsWith("/"))
                path = path.substring(1);	// Convert "/C:/foobar/cp" => "C:/foobar/cp"
        }

        // If it is a plain file on the disk, just read it from the disk
        if (url.getProtocol().equals("file")) {
            QFileInfo file = new QFileInfo(path);
            if (file.isDir()
                    && file.exists()
                    && new QFileInfo(path + "/" + fileName).exists()) {
                addEngine(new QFSEntryEngine(path + "/" + fileName, url.toExternalForm()));
                return true;
            }
        }
        return false;
    }

    // We are passed an open JarFile to take ownership of and use, if we throw exception caller is responsible for the still open JarFile
    private boolean addJarFileFromPath(JarFile jarFile, String fileName, boolean directory) throws IOException {
        QJarEntryEngine engine = new QJarEntryEngine(jarFile, fileName, directory);
        if(engine.isValid()) {
            addEngine(engine);
            return true;
        }
        return false;
    }

    private boolean addJarFileFromPath(String pathToJarFile, String fileName, boolean directory) {
        boolean bf = false;
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pathToJarFile);
            // Each engine must have its own instance of JarFile as it can not be shared across threads
            bf = addJarFileFromPath(jarFile, fileName, directory);
            if(bf)
                jarFile = null;  // stops it being closed in finally
        } catch(IOException eat) {
        } finally {
            // We are responsible to close in all cases except bf==true (such as Exception or bf==false)
            if(jarFile != null) {
                try {
                    jarFile.close();
                } catch(IOException eat) {
                }
                jarFile = null;
            }
        }
        return bf;
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
                List<String> pathToJarFiles = JarCache.pathToJarFiles(fileName);
                String jarFileName = jarFile.getName();
                if (pathToJarFiles != null) {
                    for (String thisPathToJar : pathToJarFiles) {
                        if (thisPathToJar.equals(jarFileName)) {
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

            addJarFileFromPath(jarFile, fileName, isDirectory);  // handover jarFile
            jarFile = null;	// inhibit jarFile.close() in finally block
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(jarFile != null) {
                try {
                    jarFile.close();
                } catch(IOException eat) {
                }
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

    private void addEngine(QAbstractFileEngine engine) {
        synchronized(QClassPathEngine.class) {
            m_engines.add(engine);
        }
    }


    private static void findClassPaths() {
        synchronized(QClassPathEngine.class) {
            classpaths = new HashSet<String>();

            List<URL> cpUrls = new ArrayList<URL>();

            try {
                // FIXME: QtJambi should not mix and match the method of obtaining the current ClassLoader
                //  all use this class or all use Thread.
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null)
                    loader = QClassPathFileEngineHandler.class.getClassLoader();

                Enumeration<URL> urls = loader.getResources("META-INF/MANIFEST.MF");
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if (url.getProtocol().equals("jar")) try {
                        String f = url.getPath();
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

                    String urlString = makeUrl(p);
                    boolean match = false;

                    try {
                        URL url = new URL(urlString);

                        if("file".equals(url.getProtocol())) {
                            File fileA = new File(url.getPath());
                            if(fileA.isDirectory()) {  // FIXME
                                classpaths.add(Utilities.convertAbsolutePathStringToFileUrlString(fileA));  // "file://" + "/C:/foo/bar"
                                continue;
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    URLConnection urlConnection2 = null;
                    JarFile jarFile2 = null;
                    URLConnection urlConnection1 = null;
                    JarFile jarFile1 = null;
                    try {
                        URL url2 = new URL("jar:" + urlString.toString() + "!/");
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
                        classpaths.add(urlString);
                }
            }

            // If there are no paths set in java.class.path, we do what Java does and
            // add the current directory; at least ask Java what the current directory
            // is and not Qt.
            if (k == 0) {
                // FIXME: Use JVM cwd notion
                classpaths.add("file:" + QDir.currentPath());	// CHECKME "file:///" ?
            }
        }
        JarCache.reset(classpaths);
    }
    
    @Override
    public QAbstractFileEngineIterator beginEntryList(QDir.Filters filters, java.util.List<String> nameFilters) {
    	String path = "";
    	if(m_baseName.startsWith(namespaceSchemePrefix + ":"))
    		path = m_baseName;
    	else
    		path = namespaceSchemePrefix + ":" + m_baseName;
    	return new QClassPathFileEngineIterator(path, filters, nameFilters);
    }
    
    @Override
    public QAbstractFileEngineIterator endEntryList() {
    	return null;
    }

    private QAbstractFileEngine getFirstEngine() {
        synchronized(QClassPathEngine.class) {
            if(m_engines.isEmpty() == false)
                return m_engines.getFirst();
        }
        return null;
    }
}
