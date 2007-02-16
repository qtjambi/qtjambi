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

class QJarEntryEngine extends QAbstractFileEngine implements QClassPathEntry
{
    private String m_classPathEntryFileName = null;
    private String m_jarFileName = null;
    private String m_entryFileName = null;

    private JarEntry m_entry = null;
    private JarFile m_jarFile = null;

    private InputStream m_stream = null;
    private BufferedReader m_reader = null;

    private long m_pos = -1;
    private int m_openMode;
    private boolean m_valid = false;

    public QJarEntryEngine(JarFile jarFile, String jarFileName, String fileName, String classPathEntryFileName)
    {
        super();
        
        if (jarFile != null && jarFileName.length() > 0) {
            m_jarFile = jarFile;
            m_jarFileName = jarFileName;
            m_classPathEntryFileName = classPathEntryFileName;

            setFileName(fileName);
        }
    }

    @Override
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
    
    public String classPathEntryName() {
        return m_classPathEntryFileName;
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

    @Override
    public FileFlags fileFlags(FileFlags type)
    {
        try {
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

    @Override
    public QDateTime fileTime(QAbstractFileEngine.FileTime time)
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
        if (classpaths == null)
            findClassPaths();
        
        classpaths.add(makeUrl(path));
    }

    public static void removeSearchPath(String path)
    {
        if (classpaths == null)
            findClassPaths();
        classpaths.remove(makeUrl(path));
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
            addFromPath(makeUrl(m_selectedSource), m_baseName);
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
            int pos = m_baseName.lastIndexOf("/", m_baseName.length() - 2);
            result = pos >= 0 ? m_baseName.substring(pos + 1) : m_baseName;
        } else if (file == FileName.PathName) {
            int pos = m_baseName.lastIndexOf("/", m_baseName.length() - 2);
            result = pos >= 0 ? m_baseName.substring(0, pos) : "/";
        } else if (file == FileName.AbsolutePathName) {
            result = QClassPathEngine.FileNamePrefix + classPathEntry + FileNameDelim + fileName(FileName.PathName);
        } else if (file == FileName.CanonicalPathName) {
            result = m_engines.get(0).fileName(file);
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
        URL url = null;
        try {
            url = new URL(path);
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
        
        // If the path is on the internets, we need to download it (this is for jar files,
        // won't help us if the file itself is remote.)
        if (!url.getProtocol().equals("file")) {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            tmpDir = new File(tmpDir, "QtJambi_" + QtJambi.VERSION_STRING);
            
            File tmpFile = new File(tmpDir, url.getFile().substring(url.getFile().lastIndexOf('/')));
            
        }
        
        String qtified_path = url.getFile().replace(File.separator, "/");
        JarFile jarFile = null;
        
        // If it is a plain file on the disk, just read it from the disk                        
        if (url.getProtocol().equals("file")) {
            QFileInfo file = new QFileInfo(qtified_path);
            if (file.isDir()
                    && file.exists()
                    && new QFileInfo(qtified_path + "/" + fileName).exists()) {
                addEngine(new QFSEntryEngine(qtified_path + "/" + fileName, path));
                return ;
            }

        }

        try {
            url = new URL("jar:" + url.toString() + "!/");                      
            jarFile = ((JarURLConnection) url.openConnection()).getJarFile();// JarFile(path.replace("/", File.separator));
        } catch (IOException e) {
            // It happens...
            return ;
        }            
        
        fileName = QDir.cleanPath(fileName);
        while (fileName.startsWith("/"))
            fileName = fileName.substring(1);

        QJarEntryEngine engine = new QJarEntryEngine(jarFile, qtified_path, fileName, path);

        if (engine.isValid())
            addEngine(engine);
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
            classpaths.add(makeUrl(p));        

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                                
                if (url.getProtocol().equals("jar")) try {
                    
                    String f = url.getFile();
                    int bang = f.indexOf("!");                    
                    if (bang >= 0)
                        f = f.substring(0, bang);
                                       
                    classpaths.add(f);
                } catch (Exception e) {
                    e.printStackTrace();
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
