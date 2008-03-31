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

import com.trolltech.qt.core.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import java.util.concurrent.locks.*;

interface QClassPathEntry {
    public String classPathEntryName();
}

class QFSEntryEngine extends QFSFileEngine implements QClassPathEntry {
    private String m_classPathEntryFileName;

    public QFSEntryEngine(String file, String classPathEntryFileName) {
        super(file);
        m_classPathEntryFileName = classPathEntryFileName;
    }

    public String classPathEntryName() {
        return m_classPathEntryFileName;
    }
}

class JarCache {

    private static void add(String dirName, JarFile jarFile) {
        List<JarFile> files = cache.get(dirName);
        if (files == null) {
            files = new ArrayList<JarFile>();
            files.add(jarFile);
            cache.put(dirName, files);

        } else {
            if (!files.contains(jarFile))
                files.add(jarFile);
        }
    }


    public static void reset(Set<String> jarFileList) {
        lock.writeLock().lock();

        cache = new HashMap<String, List<JarFile>>();
        classPathDirs = new ArrayList<String>();

        for (String jarFileName : jarFileList) {
            try {
                URL url = new URL("jar:" + jarFileName + "!/");
                JarFile file = ((JarURLConnection) url.openConnection()).getJarFile();

                // Add root dir for all jar files (event empty ones)
            	add("", file);

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
                        add(dirName, file);
                        int slashPos = dirName.lastIndexOf("/");
                        if (slashPos > 0)
                            dirName = dirName.substring(0, slashPos);
                        else
                            dirName = null;
                    }
                }

                // Make sure all files are registered under the empty
                // since all have roots
                add("", file);
            } catch (FileNotFoundException e) {
                // Expected as directories will fail when doing openConnection.getJarFile()
                classPathDirs.add(jarFileName);
            } catch (Exception e) {
            }
        }

//         for (String s : cache.keySet()) {
//             System.out.println(s);
//             for (JarFile f : cache.get(s)) {
//                 System.out.println(" - '" + f.getName() + "'");
//             }
//         }

        lock.writeLock().unlock();
    }

    public static List<JarFile> jarFiles(String entry) {
        lock.readLock().lock();
        List<JarFile> files = cache.get(entry);
        lock.readLock().unlock();
        return files;
    }

    public static List<String> classPathDirs() {
        return classPathDirs;
    }

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static HashMap<String, List<JarFile>> cache;
    private static List<String> classPathDirs;
}


class QJarEntryEngine extends QAbstractFileEngine implements QClassPathEntry
{
    // private String m_classPathEntryFileName = null;
    // private String m_jarFileName = null;
    private String m_entryFileName = null;

    private JarEntry m_entry = null;
    private JarFile m_jarFile = null;

    private InputStream m_stream = null;
    private BufferedReader m_reader = null;

    private long m_pos = -1;
    private int m_openMode;
    private boolean m_valid = false;
    private boolean m_directory = false;
    private String m_name;


    public QJarEntryEngine(JarFile jarFile, String fileName, boolean isDirectory)
    {
        m_jarFile = jarFile;
        m_directory = isDirectory;
        setFileName(fileName);
    }


    @Override
    public void setFileName(String fileName)
    {
        m_entry = null;
        if (m_jarFile == null)
            return ;

        if (fileName.length() == 0) {
            m_entryFileName = "";
            m_name = "";
            m_valid = true;
            m_directory = true;
            return ;
        }

        m_entryFileName = fileName;
        m_entry = m_jarFile.getJarEntry(m_entryFileName);

        if (m_entry == null && !m_directory)
            m_valid = false;
        else {
            if (m_entry == null)
                m_name = fileName;
            else
               m_name = m_entry.getName();
            m_valid = true;
        }

    }

    public String classPathEntryName() {
        return m_jarFile.getName();
    }

    public boolean isValid()
    {
        return m_valid;
    }

    @Override
    public boolean copy(String newName)
    {
        final int BUFFER_SIZE = 1024*1024;
        QNativePointer buffer = new QNativePointer(QNativePointer.Type.Byte, BUFFER_SIZE);

        QFile newFile = new QFile(newName);
        if (newFile.exists())
            return false;

        if (!open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly)))
            return false;

        if (!newFile.open(new QFile.OpenMode(QFile.OpenModeFlag.WriteOnly))) {
            close();
            return false;
        }

        long sz = size();
        long i = 0;
        int bytes_to_read = 0;

        if (sz > 0) {
            do {
                bytes_to_read = (int) Math.min(sz - i, BUFFER_SIZE);
                bytes_to_read = (int) read(buffer, bytes_to_read);

                byte bytes[] = new byte[bytes_to_read];
                for (int j=0; j<bytes_to_read; ++j)
                    bytes[j] = buffer.byteAt(j);

                if (bytes_to_read > 0 && newFile.write(bytes) != bytes_to_read)
                    return false;
            } while (i < sz && bytes_to_read > 0);
        }

        newFile.close();
        if (!close())
            return false;

        return (i == sz);
    }

    @Override
    public boolean setPermissions(int perms)
    {
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
        if (m_stream != null) {
            try {
                m_stream.close();
            } catch (IOException e) {
                return false;
            }

            m_stream = null;
        }

        return true;
    }

    @Override
    public List<String> entryList(QDir.Filters filters, List<String> filterNames)
    {
        if (!m_directory)
            return new LinkedList<String>();

        List<String> result = new LinkedList<String>();

        if (!filters.isSet(QDir.Filter.NoDotAndDotDot)) {
            result.add(".");
            if (m_entryFileName.length() > 0)
                result.add("..");
        }


        // Default to readable
        if (!filters.isSet(QDir.Filter.Readable, QDir.Filter.Writable, QDir.Filter.Executable))
            filters.set(QDir.Filter.Readable);

        String mentryName = m_name;
        if (!mentryName.endsWith("/") && mentryName.length() > 0)
            mentryName = mentryName + "/";

        Enumeration<JarEntry> entries = m_jarFile.entries();

        HashSet<String> used = new HashSet<String>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            String entryName = entry.getName();

            // Must be inside this directory
            if (entryName.length() <= mentryName.length() || !mentryName.equals(entryName.substring(0, mentryName.length())) || mentryName.equals(entryName))
                continue;

            // Only one level
            boolean isDir;
            int pos = entryName.indexOf("/", mentryName.length());
            if (pos > 0) {
                entryName = entryName.substring(0, pos);
                isDir = true;
            } else {
                isDir = entry.isDirectory();
            }

            if (!filters.isSet(QDir.Filter.Readable))
                continue ;

            if (!filters.isSet(QDir.Filter.Dirs) && isDir)
                continue ;

            if (!filters.isSet(QDir.Filter.Files) && !isDir)
                continue ;

            if (filterNames.size() > 0) {
                if ((!isDir || !filters.isSet(QDir.Filter.AllDirs))
                    && (!QDir.match(filterNames, entryName))) {
                    continue;
                }
            }

            if (entryName.endsWith("/") && entryName.length() > 1)
                entryName = entryName.substring(0, entryName.length() - 1);

            entryName = entryName.substring(mentryName.length());

            if (!used.contains(entryName)) {
                used.add(entryName);
                result.add(entryName);
            }
        }

        return result;
    }

    @Override
    public FileFlags fileFlags(FileFlags type)
    {
        try {
            int flags = 0;

            QFileInfo info = new QFileInfo(m_jarFile.getName());
             if (info.exists()) {
                 flags |= info.permissions().value()
                          & (FileFlag.ReadOwnerPerm.value()
                             | FileFlag.ReadGroupPerm.value()
                             | FileFlag.ReadOtherPerm.value()
                             | FileFlag.ReadUserPerm.value());
             }

             if (m_directory)
                 flags |= FileFlag.DirectoryType.value();
             else
                 flags |= FileFlag.FileType.value();


             return new FileFlags((flags | FileFlag.ExistsFlag.value()) & type.value());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String fileName(FileName file)
    {
        String entryFileName = m_entryFileName;

        if (file == FileName.LinkName) {
            return "";
        } else if (file == FileName.DefaultName
                || file == FileName.AbsoluteName
                || file == FileName.CanonicalName) {
            return QClassPathEngine.FileNamePrefix + m_jarFile.getName() + QClassPathEngine.FileNameDelim + entryFileName;
        } else if (file == FileName.BaseName) {
            int pos = m_entryFileName.lastIndexOf("/");
            return pos >= 0 ? m_entryFileName.substring(pos + 1) : entryFileName;
        } else if (file == FileName.PathName) {
            int pos = m_entryFileName.lastIndexOf("/");
            return pos > 0 ? m_entryFileName.substring(0, pos) : "";
        } else if (file == FileName.CanonicalPathName || file == FileName.AbsolutePathName) {
            return QClassPathEngine.FileNamePrefix + m_jarFile.getName() + QClassPathEngine.FileNameDelim + fileName(FileName.PathName);
        } else {
            throw new IllegalArgumentException("Unknown file name type: " + file);
        }
    }

    @Override
    public QDateTime fileTime(QAbstractFileEngine.FileTime time)
    {
        if (m_entry == null) {
            QFileInfo info = new QFileInfo(m_jarFile.getName());

            if (info.exists())
                return info.lastModified();
            else
                return new QDateTime();
        }

        long tm = m_entry.getTime();
        if (tm == -1)
            return new QDateTime();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(tm));

        return new QDateTime(new QDate(calendar.get(Calendar.YEAR),
                                       calendar.get(Calendar.MONTH) + 1,
                                       calendar.get(Calendar.DAY_OF_MONTH)),
                             new QTime(calendar.get(Calendar.HOUR_OF_DAY),
                                       calendar.get(Calendar.MINUTE),
                                       calendar.get(Calendar.SECOND),
                                       calendar.get(Calendar.MILLISECOND)));
    }

    @Override
    public boolean link(String newName)
    {
        return false;
    }

    @Override
    public boolean mkdir(String dirName, boolean createParentDirectories)
    {
        return false;
    }

    @Override
    public boolean open(QIODevice.OpenMode openMode)
    {
        if (m_entry == null)
            return false;

        if (!openMode.isSet(QIODevice.OpenModeFlag.WriteOnly) && !openMode.isSet(QIODevice.OpenModeFlag.Append)) {
            try {
                m_stream = m_jarFile.getInputStream(m_entry);
            } catch (IOException e) {
                return false;
            }

            if (m_stream != null) {
                if (openMode.isSet(QIODevice.OpenModeFlag.Text))
                    m_reader = new BufferedReader(new InputStreamReader(m_stream));
                m_pos = 0;
                m_openMode = openMode.value();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public long pos()
    {
        return m_pos;
    }

    @Override
    public long read(QNativePointer data, long maxlen)
    {
        if (m_stream == null)
            return -1;

        if (maxlen > Integer.MAX_VALUE)
            maxlen = Integer.MAX_VALUE;

        byte[] b = new byte[(int)maxlen];

        int bytes_read = 0;
        try {
            int read = 0;
            while (m_stream.available() > 0 && bytes_read < maxlen && read >= 0) {
                read = m_stream.read(b, 0, (int)maxlen - bytes_read);
                if (read > 0) {
                    for (int i=0; i<read; ++i)
                        data.setByteAt(i + bytes_read, b[i]);
                    bytes_read += read;
                }
            }
        } catch (IOException e) {
            return -1;
        }

        m_pos += bytes_read;
        return bytes_read;
    }

    @Override
    public long readLine(QNativePointer data, long maxlen)
    {
        if (m_stream == null || m_reader == null)
            return -1;

        int bytes_read = 0;
        try {
            while (m_stream.available() > 0 && bytes_read < maxlen) {
                int read = m_stream.read();
                if (read == -1)
                    break ;

                data.setByteAt(bytes_read++, (byte) read);
                if (read == '\n')
                    break ;
            }
        } catch (IOException e) {
            return -1;
        }

        m_pos += bytes_read;
        return bytes_read;
    }

    @Override
    public boolean remove()
    {
        return false;
    }

    @Override
    public boolean rename(String newName)
    {
        return false;
    }

    @Override
    public boolean rmdir(String dirName, boolean recursive)
    {
        return false;
    }

    @Override
    public boolean seek(long offset)
    {
        try {
            m_stream.close();
            if (!open(new QIODevice.OpenMode(m_openMode)))
                return false;

            m_pos = 0;
            while (m_pos < offset) {
                int skip = (int)Math.min(offset - m_pos, Integer.MAX_VALUE);

                m_stream.skip(skip);
                m_pos += skip;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public String owner(QAbstractFileEngine.FileOwner owner)
    {
        return "";
    }

    @Override
    public int ownerId(QAbstractFileEngine.FileOwner owner)
    {
        return -2;
    }

    @Override
    public boolean isRelativePath()
    {
        return false;
    }

    @Override
    public boolean isSequential()
    {
        return false;
    }

    @Override
    public boolean setSize(long sz)
    {
        return false;
    }

    @Override
    public long size()
    {
        return m_entry == null ? 0 : m_entry.getSize();
    }

    @Override
    public long write(QNativePointer data, long len)
    {
        return -1;
    }
}

class QClassPathEngine extends QAbstractFileEngine
{
    public final static String FileNameDelim = "#";
    public final static String FileNameDelimRegExp = "\\x23";
    public final static String FileNameIndicator = "classpath";
    public final static String FileNamePrefix = FileNameIndicator + ":";

    private static HashSet<String> classpaths;

    private String m_fileName = "";
    private String m_baseName = "";
    private String m_selectedSource = "*";
    private List<QAbstractFileEngine> m_engines = new LinkedList<QAbstractFileEngine>();

    public QClassPathEngine(String fileName)
    {
        setFileName(fileName);
    }

    private static String makeUrl(String path) {
        boolean hasProtocol = false;
        try {
            URL url = new URL(path);
            if (url.getProtocol().length() > 0) {
                hasProtocol = true;
            }
        } catch (Exception e) {
        }

        if (!hasProtocol)
            path = "file:" + path;

        return path;
    }

    public static void addSearchPath(String path)
    {
    	synchronized(QClassPathEngine.class){
            if (classpaths == null)
                findClassPaths();

            String url = makeUrl(path);
            classpaths.remove(url); // make sure it isn't added twice
            classpaths.add(makeUrl(path));

            JarCache.reset(classpaths);
    	}
    }

    public static void removeSearchPath(String path)
    {
    	synchronized(QClassPathEngine.class){
            if (classpaths == null)
                findClassPaths();
            classpaths.remove(makeUrl(path));
            JarCache.reset(classpaths);
    	}
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

        String searchPath[] = m_fileName.split(FileNameDelimRegExp, 2);

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
                } catch (Exception e) {}
            }
        } else
            try {
            	String url = makeUrl(m_selectedSource);
            	
            	// If it's a file (it should be), strip away the scheme and check whether the
            	// file is a directory. Otherwise it's assumed to be a .jar file
            	if (url.startsWith("file:") && new File(url.substring(5)).isDirectory())
                    addFromPath(new URL(url), m_baseName);
                else
                    addJarFileFromPath(new URL("jar:" + url + "!/"), m_baseName);
                
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

    private void addJarFileFromPath(URL jarFileURL, String fileName) {
        try {
            JarFile jarFile = ((JarURLConnection)jarFileURL.openConnection()).getJarFile();

            boolean isDirectory = false;
            JarEntry fileInJar = jarFile.getJarEntry(fileName);

            // If the entry exists in the given file, look it up and
            // check if its a dir or not
            if (fileInJar != null) {
                try {
                    isDirectory = fileInJar.isDirectory();

                    // JarEntry will return a directory even though it
                    // is not a directory, so we try to read a byte
                    // from the entry in order to trigger an exception
                    // in the case where it is a directory...
                    if (!isDirectory) {
                        InputStream s = jarFile.getInputStream(fileInJar);
                        int x = s.read();
                    }
                } catch (Exception e) {
                    isDirectory = true;
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

        } catch (Exception e) {
        }


    }

    private void addEngine(QAbstractFileEngine engine)
    {
        if (m_engines == null)
            m_engines = new LinkedList<QAbstractFileEngine>();

        m_engines.add(engine);
    }



    private static void findClassPaths() {
    	synchronized(QClassPathEngine.class){
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

	        String paths[] = System.getProperty("java.class.path").split(File.pathSeparator);

                // Only add the .jar files that are not already added...
		int k=0;
	        for (String p : paths) {
		    if (p.trim().length() > 0) {
			k++; // count all paths, invalid and valid
                        String url = makeUrl(p);
                        boolean match = false;
                        try {
                            JarFile jarFile2 = ((JarURLConnection) new URL("jar:" + url + "!/").openConnection()).getJarFile();
                            for (URL otherURL : cpUrls) {
                                JarFile jarFile1 = ((JarURLConnection) new URL("jar:" + otherURL.toString() + "!/").openConnection()).getJarFile();

                                if (new File(jarFile1.getName()).getCanonicalPath().equals(new File(jarFile2.getName()).getCanonicalPath())) {
                                    match = true;
                                    break;
                                }
                            }
                        } catch (Exception e) { }

                        if (!match)
                            classpaths.add(url);

		    }
		}


		// If there are no paths set in java.class.path, we do what Java does and
		// add the current directory
		if (k == 0)
		    classpaths.add("file:" + QDir.currentPath());
	    }
        JarCache.reset(classpaths);
    }
}

public abstract class QClassPathFileEngineHandler
{
	public native static void initialize();
}
