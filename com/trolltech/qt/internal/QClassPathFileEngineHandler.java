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
package com.trolltech.qt.internal;

import com.trolltech.qt.core.*;
import com.trolltech.qt.*;

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

public class QClassPathFileEngineHandler
{
	public native static void initialize();
}
