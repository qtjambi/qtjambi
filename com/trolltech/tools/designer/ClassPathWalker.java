package com.trolltech.tools.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;

import java.util.*;

/**
 */
public class ClassPathWalker extends QObject implements Runnable {

    static {
        List<QByteArray> formats = QImageReader.supportedImageFormats();
        List<String> stringFormats = new ArrayList<String>();
        for (QByteArray f : formats) {
            if (f.toString().equals("svg"))
                continue;
            stringFormats.add("*." + f.toString());
        }
        imageFormats = stringFormats;
    }
    private static List<String> imageFormats;

    /**
     * The resourceFound signal is emitted with the resource name for each resource
     * that is found. The signal is emitted from the traversing thread.
     */
    public Signal2<String, QImage> resourceFound = new Signal2<String, QImage>();

    /**
     * The doneSearching signal is emitted when the classpath walker is done processing..
     * It is used internally to close call stop...
     */
    public Signal0 doneSearching = new Signal0();

    public Signal0 beginSearching = new Signal0();

    public ClassPathWalker() {
        if (roots == null) {
            String classpath = System.getProperty("java.class.path");
            roots = new ArrayList<String>();
            Collections.addAll(roots, classpath.split(java.io.File.pathSeparator));
        }
    }

    /**
     * Performs the traversal of the directory structure...
     */
    public void run() {
        beginSearching.emit();

        Stack<QPair<QDir, String>> stack = new Stack<QPair<QDir, String>>();
        for (String s : roots) {
            QDir d = new QDir(s);
            stack.push(new QPair<QDir, String>(d, d.absolutePath()));
        }

        Set<String> processedDirs = new HashSet<String>();

        while (!stopped && stack.size() != 0) {

            QPair<QDir, String> data = stack.pop();
            QDir dir = data.first;
            String dirPath = QDir.toNativeSeparators(dir.absolutePath());
            if (processedDirs.contains(dirPath))
                continue;
            processedDirs.add(dirPath);            

            traverse(dir, data.second);

            // Traverse the subdirs...
            QDir.Filters filters = new QDir.Filters();
            filters.set(QDir.Filter.NoDotAndDotDot);
            filters.set(QDir.Filter.Dirs);
            List<QFileInfo> dirs = dir.entryInfoList(filters);
            for (QFileInfo info: dirs) {
                QDir subDir = new QDir(info.absoluteFilePath());
                stack.push(new QPair<QDir, String>(subDir, data.second));
            }
        }
        if (!stopped)
            doneSearching.emit();
    }

    /**
     * Starts the traversal of the directory structure... This is done
     * in a separate thread and feedback can be received through the
     * resourceFound signal.
     */
    public void start() {
        if (thread != null) {
            throw new RuntimeException("Already running");
        }

        thread = new Thread(this);
        thread.setDaemon(false);
        thread.setPriority(Thread.MIN_PRIORITY);
        moveToThread(thread);

        stopped = false;
        thread.start();
    }

    public synchronized void kill() {
        stopped = true;
    }

    public void setPixmapSize(QSize size) {
        this.size = size;
    }


    public synchronized static void setRoots(List<String> r) {
        roots = r;
    }

    public synchronized static List<String> roots() {
        return roots;
    }


    /**
     * Traverses the directory and emits a signal for all the files that match the fileExtensions.
     * @param dir The directory to find files in...
     */
    private void traverse(QDir dir, String rootDir) {                
        QDir.Filters filters = new QDir.Filters();
        filters.set(QDir.Filter.Readable);
        filters.set(QDir.Filter.Files);
        List<String> imgs = dir.entryList(fileExtensions, filters);

        for (String name : imgs) {
            name = dir.absoluteFilePath(name).substring(new QDir(rootDir).canonicalPath().length() + 1);            
            name = "classpath:" + name;
                        
            QImage image = new QImage(name);
            if (image.isNull())
                continue;
            QImage smallImage = image.scaled(size,
                    Qt.AspectRatioMode.KeepAspectRatio,
                    Qt.TransformationMode.SmoothTransformation);
            image.dispose();                                   	

            synchronized (this) {
                if (stopped)
                    return;
                resourceFound.emit(name, smallImage);
            }
        }
    }

    private static List<String> roots;
    private Thread thread;
    private List<String> fileExtensions = imageFormats;
    private boolean stopped;

    private QSize size = new QSize(16, 16);
}
