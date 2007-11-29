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

package com.trolltech.tools.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;

import java.util.*;

/**
 */
public class ClassPathWalker extends QObject {

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
            List<String> r = new ArrayList<String>();
            Collections.addAll(r, classpath.split(java.io.File.pathSeparator));
            setRoots(r);

            addRootsFromSettings();
        }
    }

    /**
     * Performs the traversal of the directory structure...
     */
    @Override
    protected void timerEvent(QTimerEvent e) {
        if(stopped)
            return;
        
		if (stack.isEmpty()) {
            kill();
			doneSearching.emit();            
			return;
		}

		QPair<Object, String> data = stack.pop();
		if (data.first instanceof QDir) {
			QDir dir = (QDir) data.first;
			String dirPath = QDir.toNativeSeparators(dir.absolutePath());
			if (processedDirs.contains(dirPath))
				return;
			processedDirs.add(dirPath);

			QDir.Filters filters = new QDir.Filters();
			filters.set(QDir.Filter.Readable);
			filters.set(QDir.Filter.Files);
			List<String> imgs = dir.entryList(fileExtensions, filters);

			for (String file : imgs) {
				stack.push(new QPair<Object, String>(new QFileInfo(dir.absoluteFilePath(file)), data.second));
			}

			filters.clear(QDir.Filter.Files);
			filters.set(QDir.Filter.NoDotAndDotDot);
			filters.set(QDir.Filter.Dirs);
			List<String> dirs = dir.entryList(filters);
			for (String dirName: dirs) {
				stack.push(new QPair<Object, String>(new QDir(dir.absoluteFilePath(dirName)), data.second));
			}
		} else if (data.first instanceof QFileInfo) {
		    String name = ((QFileInfo) data.first).absoluteFilePath();
            int pos = name.lastIndexOf('#') + 1;

            name = name.substring(pos);
            if (name.startsWith("/"))
                name = name.substring(1);
            name = "classpath:" + name;
            System.out.println(name);

			QImage image = new QImage(name);
			if (!image.isNull()) {
				QImage smallImage = image.scaled(size,
						Qt.AspectRatioMode.KeepAspectRatio,
						Qt.TransformationMode.SmoothTransformation);

				// aspect ration makes one dimension < 1, thus, problems...
				if (smallImage.isNull()) {
					smallImage = image.scaled(size,
							Qt.AspectRatioMode.IgnoreAspectRatio,
							Qt.TransformationMode.SmoothTransformation);
				}
				
				image.dispose();

				if (!smallImage.isNull()) {
                    resourceFound.emit(name, smallImage);
				}
			}
		}
	}

    /**
	 * Starts the traversal of the directory structure... This is done in a
	 * separate thread and feedback can be received through the resourceFound
	 * signal.
	 */
    private int timerId = 0;
    public void start() {
        stopped = false;
        
        stack = new Stack<QPair<Object, String>>();
        for (String s : roots) {
            s = "classpath:" + s + "#/";

            QDir d = new QDir(s);
            stack.push(new QPair<Object, String>(d, d.absolutePath()));
        }
        processedDirs = new HashSet<String>();

        beginSearching.emit();
        timerId = startTimer(50);
    }
    
    public synchronized void kill() {
        if (timerId != 0)
            killTimer(timerId);
        timerId = 0;
        stopped = true;
    }

    public void setPixmapSize(QSize size) {
        this.size = size;
    }

    public synchronized static void addRootsFromSettings() {
        QSettings settings = new QSettings("Trolltech", "Qt Jambi Resource Browser");
        Object path = settings.value("Extra paths");
        if (roots != null && path != null && path instanceof String) {
            String paths[] = ((String) path).split(java.io.File.pathSeparator);
            for (String p : paths) {
                if (!p.equals("")) {
                    QtJambiInternal.addSearchPathForResourceEngine(p);
                    roots.add(p);
                }
            }
        }
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
    @SuppressWarnings("unused")
    private void traverse(QDir dir, String rootDir) {
    }

    private static List<String> roots;
    //private Thread thread;
    private List<String> fileExtensions = imageFormats;
    private boolean stopped;
    private Stack<QPair<Object, String>> stack;
    private Set<String> processedDirs;


    private QSize size = new QSize(16, 16);
}
