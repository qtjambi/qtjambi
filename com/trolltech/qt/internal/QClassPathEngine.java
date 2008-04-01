package com.trolltech.qt.internal;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QIODevice;

public class QClassPathEngine extends QAbstractFileEngine
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
