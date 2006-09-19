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

class QJarEntryEngine extends QAbstractFileEngine
{
    private String m_jarFileName = null;
    private String m_entryFileName = null;

    private JarEntry m_entry = null;
    private JarFile m_jarFile = null;

    private InputStream m_stream = null;
    private BufferedReader m_reader = null;

    private long m_pos = -1;
    private int m_openMode;
    private boolean m_valid = false;

    public QJarEntryEngine(JarFile jarFile, String jarFileName, String fileName)
    {
        super();

        if (jarFile != null && jarFileName.length() > 0) {
            m_jarFile = jarFile;
            m_jarFileName = jarFileName;

            setFileName(fileName);
        }
    }

    public void setFileName(String fileName)
    {
        m_entry = null;
        if (m_jarFile == null)
            return ;

        if (fileName.length() == 0) {
            m_entryFileName = "";
            m_valid = true;
            return ;
        }

        if (!fileName.endsWith("/")) {
            setFileName(fileName + "/");
            if (m_entry != null)
                return ;
        }

        m_entryFileName = fileName;
        m_entry = m_jarFile.getJarEntry(m_entryFileName);
        m_valid = m_entry != null;
    }

    public boolean isValid()
    {
        return m_valid;
    }

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
        long bytes_to_read = 0;

        if (sz > 0) {
            do {
                bytes_to_read = Math.min(sz - i, BUFFER_SIZE);
                bytes_to_read = read(buffer, bytes_to_read);
                if (bytes_to_read > 0 && newFile.write(buffer, bytes_to_read) != bytes_to_read)
                    return false;
            } while (i < sz && bytes_to_read > 0);
        }

        newFile.close();
        if (!close())
            return false;

        return (i == sz);
    }

    public boolean setPermissions(int perms)
    {
        return false;
    }

    public boolean caseSensitive()
    {
        return true;
    }

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

    public List<String> entryList(QDir.Filters filters, List<String> filterNames)
    {
        if (m_entry != null && !m_entry.isDirectory())
            return new LinkedList<String>();

        List<String> result = new LinkedList<String>();
        
        // Default to readable
        if (!filters.isSet(QDir.Filter.Readable, QDir.Filter.Writable, QDir.Filter.Executable))
            filters.set(QDir.Filter.Readable);

        String mentryName = m_entry == null ? "" : m_entry.getName();
        if (!mentryName.endsWith("/") && mentryName.length() > 0)
            mentryName = mentryName + "/";

        Enumeration<JarEntry> entries = m_jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            String entryName = entry.getName();
            int pos = entryName.lastIndexOf('/', entryName.length() - 2);

            String dirName = "";
            if (pos > 0)
                dirName = entryName.substring(0, pos + 1);

            if (!entryName.equals(mentryName) && dirName.equals(mentryName)) {

                boolean isDir = entry.isDirectory();

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
                result.add(entryName.substring(dirName.length()));
            }
        }

        return result;
    }

    public FileFlags fileFlags(FileFlags type)
    {
        int flags = 0;

         QFileInfo info = new QFileInfo(m_jarFileName);
         if (info.exists()) {
             flags |= info.permissions().value() 
                      & (FileFlag.ReadOwnerPerm.value() 
                         | FileFlag.ReadGroupPerm.value() 
                         | FileFlag.ReadOtherPerm.value() 
                         | FileFlag.ReadUserPerm.value());
         }

         if (m_entry == null || m_entry.isDirectory())
             flags |= FileFlag.DirectoryType.value();
         else
             flags |= FileFlag.FileType.value();
         

         return new FileFlags((flags | FileFlag.ExistsFlag.value()) & type.value());
    }

    public String fileName(FileName file)
    {
        String entryFileName = m_entryFileName;

        if (file == FileName.LinkName) {
            return "";
        } else if (file == FileName.DefaultName 
                || file == FileName.AbsoluteName 
                || file == FileName.CanonicalName) {
            return QClassPathEngine.FileNamePrefix + m_jarFileName + QClassPathEngine.FileNameDelim + entryFileName;
        } else if (file == FileName.BaseName) {
            int pos = m_entryFileName.lastIndexOf("/", m_entryFileName.length() - 2);
            return pos >= 0 ? m_entryFileName.substring(pos + 1) : entryFileName;
        } else if (file == FileName.PathName) {
            int pos = m_entryFileName.lastIndexOf("/", m_entryFileName.length() - 2);
            return pos >= 0 ? m_entryFileName.substring(0, pos) : "/";
        } else if (file == FileName.CanonicalPathName || file == FileName.AbsolutePathName) {
            return QClassPathEngine.FileNamePrefix + m_jarFileName + QClassPathEngine.FileNameDelim + fileName(FileName.PathName);
        } else {
            throw new IllegalArgumentException("Unknown file name type: " + file);
        }
    }

    public QDateTime fileTime(int time)
    {
        if (m_entry == null) {
            QFileInfo info = new QFileInfo(m_jarFileName);

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

        return new QDateTime(new QDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)),
                             new QTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                                       calendar.get(Calendar.MILLISECOND)));
    }

    public boolean link(String newName)
    {
        return false;
    }

    public boolean mkdir(String dirName, boolean createParentDirectories)
    {
        return false;
    }

    public boolean open(QIODevice.OpenMode openMode)
    {
        if (m_entry == null)
            return false;

        if ((openMode.value() & ~QIODevice.OpenModeFlag.Text.value()) == QIODevice.OpenModeFlag.ReadOnly.value()) {
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

    public long pos()
    {
        return m_pos;
    }

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

    public boolean remove()
    {
        return false;
    }

    public boolean rename(String newName)
    {
        return false;
    }

    public boolean rmdir(String dirName, boolean recursive)
    {
        return false;
    }

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

    public String owner(int owner)
    {
        return "";
    }

    public int ownerId(int owner)
    {
        return -2;
    }

    public boolean isRelativePath()
    {
        return false;
    }

    public boolean isSequential()
    {
        return false;
    }

    public boolean setSize(long sz)
    {
        return false;
    }

    public long size()
    {
        return m_entry == null ? 0 : m_entry.getSize();
    }

    public long write(QNativePointer data, long len)
    {
        return -1;
    }
}


// For class path entries in the OS file system. Uses the FS engine, but converts filenames to match the syntax of
// resource file specifications
class QDirEntryEngine extends QFSFileEngine
{
    private String m_path = "";
    private String m_fileName = "";

    public QDirEntryEngine(String path, String fileName)
    {
        super(path + "/" + fileName);

        m_path = path;
        m_fileName = fileName;
    }

    public String fileName(FileName file)
    {
        String fileName = m_fileName;

        int pos;
        switch (file) {
        case LinkName:
        case CanonicalName:
            return super.fileName(FileName.AbsoluteName);
        case CanonicalPathName:
            return super.fileName(FileName.AbsolutePathName);
        case DefaultName:
            return QClassPathEngine.FileNamePrefix + m_path + QClassPathEngine.FileNameDelim + fileName;
        case BaseName:
            pos = m_fileName.lastIndexOf('/', m_fileName.length() - 2);
            return pos >= 0 ? m_fileName.substring(pos + 1) : fileName;
        case AbsoluteName:
            return QClassPathEngine.FileNamePrefix + m_path + QClassPathEngine.FileNameDelim + fileName;
        case AbsolutePathName:
            return QClassPathEngine.FileNamePrefix + m_path + QClassPathEngine.FileNameDelim + fileName(FileName.PathName);
        case PathName:
            pos = m_fileName.lastIndexOf('/', m_fileName.length() - 2);
            return pos >= 0 ? m_fileName.substring(0, pos) : "/";
        default:
            throw new IllegalArgumentException("Unknown filename type: " + file);
        }
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

    public static void addSearchPath(String path)
    {
        if (classpaths == null)
            findClassPaths();
        classpaths.add(path);
    }

    public static void removeSearchPath(String path)
    {
        if (classpaths == null)
            findClassPaths();
        classpaths.remove(path);
    }

    public void setFileName(String fileName)
    {
        cleanUp();
        if (!fileName.startsWith(FileNamePrefix))
            throw new IllegalArgumentException("Invalid format of path: " + fileName);
        m_fileName = fileName.substring(FileNamePrefix.length());

        String searchPath[] = m_fileName.split(FileNameDelimRegExp, 2);

        m_selectedSource = "*";
        if (searchPath.length == 1) {
            m_baseName = searchPath[0];
        } else {
            m_baseName = searchPath[1];
            m_selectedSource = searchPath[0];
        }

        if (m_selectedSource.equals("*")) {
            if (classpaths == null)
                findClassPaths();

            for (String path : classpaths) {
                addFromPath(path, m_baseName);
            }
        } else {
            addFromPath(m_selectedSource, m_baseName);
        }
    }

    public boolean copy(String newName)
    {
        if (m_engines.size() > 0)
            return m_engines.get(0).copy(newName);
        else
            return false;
    }

    public boolean setPermissions(int perms)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.setPermissions(perms))
                return true;
        }

        return false;
    }

    public boolean caseSensitive()
    {
        return true;
    }

    public boolean close()
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).close();
    }

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

    public FileFlags fileFlags(FileFlags type)
    {
        FileFlags flags = new FileFlags();

        for (QAbstractFileEngine engine : m_engines)
            flags.set(engine.fileFlags(type));

        if (fileName(FileName.PathName).equals("/"))
            flags.set(QAbstractFileEngine.FileFlag.RootFlag);

        return flags;
    }

    public String fileName(FileName file)
    {
        if (m_engines.size() == 0)
            return "";

        String fn = m_engines.get(0).fileName(file);

        if (fn.endsWith("/") && fn.length() > 1)
            fn = fn.substring(0, fn.length() - 1);
        for (int i=1; i<m_engines.size(); ++i) {
            String tmp = m_engines.get(i).fileName(file);
            if (tmp.endsWith("/") && tmp.length() > 1)
                tmp = tmp.substring(0, tmp.length() - 2);

            if (!fn.equals(tmp)) {
                fn = "";
                break ;
            }
        }
        if (fn.length() > 0)
            return fn;

        String result = "";
        if (file == FileName.DefaultName) {
            result = QClassPathEngine.FileNamePrefix + m_fileName;
        } else if (file == FileName.AbsoluteName || file == FileName.CanonicalName || file == FileName.LinkName) {
            result = QClassPathEngine.FileNamePrefix + "*" + FileNameDelim + m_baseName;
        } else if (file == FileName.BaseName) {
            int pos = m_baseName.lastIndexOf("/", m_baseName.length() - 2);
            result = pos >= 0 ? m_baseName.substring(pos + 1) : m_baseName;
        } else if (file == FileName.PathName) {
            int pos = m_baseName.lastIndexOf("/", m_baseName.length() - 2);
            result = pos >= 0 ? m_baseName.substring(0, pos) : "/";
        } else if (file == FileName.AbsolutePathName || file == FileName.CanonicalPathName) {
            result = QClassPathEngine.FileNamePrefix + "*" + FileNameDelim + fileName(FileName.PathName);
        } else {
            throw new IllegalArgumentException("Unknown file name type: " + file);
        }

        if (result.endsWith("/") && result.length() > 1)
            result = result.substring(0, result.length() - 1);

        return result;
    }

    public QDateTime fileTime(FileTime time)
    {
        if (m_engines.size() == 0)
            return new QDateTime();
        else
            return m_engines.get(0).fileTime(time);
    }

    public boolean link(String newName)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.link(newName))
                return true;
        }
        return false;
    }

    public boolean mkdir(String dirName, boolean createParentDirectories)
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.mkdir(dirName, createParentDirectories))
                return true;
        }
        return false;
    }

    public boolean open(QIODevice.OpenMode openMode)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).open(openMode);
    }

    public long pos()
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).pos();
    }

    public long read(QNativePointer data, long maxlen)
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).read(data, maxlen);
    }

    public long readLine(QNativePointer data, long maxlen)
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).readLine(data, maxlen);
    }

    public boolean remove()
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.remove();
        return ok;
    }

    public boolean rename(String newName)
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.rename(newName);
        return ok;
    }

    public boolean rmdir(String dirName, boolean recursive)
    {
        boolean ok = true;
        for (QAbstractFileEngine engine : m_engines)
            ok = ok && engine.rmdir(dirName, recursive);
        return ok;
    }

    public boolean seek(long offset)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).seek(offset);
    }

    public String owner(FileOwner owner)
    {
        String result = "";
        int i = 0;
        while (result.length() == 0 && i < m_engines.size())
            result = m_engines.get(i++).owner(owner);

        return result;
    }

    public int ownerId(FileOwner owner)
    {
        int result = -2;
        int i = 0;
        while (result == -2 && i < m_engines.size())
            result = m_engines.get(i++).ownerId(owner);

        return result;
    }

    public boolean isRelativePath()
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.isRelativePath())
                return true;
        }

        return false;
    }

    public boolean isSequential()
    {
        for (QAbstractFileEngine engine : m_engines) {
            if (engine.isSequential())
                return true;
        }

        return false;
    }

    public boolean setSize(long sz)
    {
        if (m_engines.size() == 0)
            return false;
        else
            return m_engines.get(0).setSize(sz);
    }

    public long size()
    {
        if (m_engines.size() == 0)
            return -1;
        else
            return m_engines.get(0).size();
    }

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

    private void addFromPath(String path, String fileName)
    {
        String qtified_path = path.replace(File.separator, "/");

        QFileInfo file = new QFileInfo(qtified_path);
        if (file.isDir()
            && file.exists()
            && new QFileInfo(qtified_path + "/" + fileName).exists()) {
            addEngine(new QDirEntryEngine(qtified_path, fileName));
        } else {
            JarFile jarFile;
            try {
                jarFile = new JarFile(path.replace("/", File.separator));
            } catch (IOException e) {
                return ;
            }

            fileName = QDir.cleanPath(fileName);
            while (fileName.startsWith("/"))
                fileName = fileName.substring(1);

            QJarEntryEngine engine = new QJarEntryEngine(jarFile, qtified_path, fileName);

            if (engine.isValid())
                addEngine(engine);
        }
    }

    private void addEngine(QAbstractFileEngine engine)
    {
        if (m_engines == null)
            m_engines = new LinkedList<QAbstractFileEngine>();

        m_engines.add(engine);
    }

    private static void findClassPaths() {
        classpaths = new HashSet<String>();

        String paths[] = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String p : paths)
            classpaths.add(p);

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                if (url.getProtocol().equals("jar")) {
                    String f = new URL(url.getFile()).getFile();
                    int bang = f.indexOf("!");
                    String jarFile = f.substring(0, bang).replace("%20", " ");
                    classpaths.add(jarFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


public class QClassPathFileEngineHandler extends QAbstractFileEngineHandler
{
    static List<QClassPathEngine> engines = new LinkedList<QClassPathEngine>();


    public QClassPathFileEngineHandler()
    {
        super();
    }

    public QAbstractFileEngine create(String fileName)
    {
        if (fileName.startsWith(QClassPathEngine.FileNamePrefix))
            return new QClassPathEngine(fileName);
        else
            return null;
    }

}
