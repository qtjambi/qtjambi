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

package com.trolltech.demos.imageviewer;

import java.util.*;
import java.util.concurrent.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class ImageTableModel extends QAbstractItemModel {
    
    public static final int NAME_COL = 0;
    public static final int PIXMAP_COL = 1;
    public static final int DIMENSION_COL = 2;
    public static final int SIZE_COL = 3;

    public ImageTableModel() {
        for (int i = 0; i < 5; ++i) {
            Thread thread = new Thread() {
                public synchronized void run() {
                    while (true) {
                        try {
                            loadQueue.take().loadThumbNail();
                        } catch (InterruptedException e) { }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    public QDir getDirectory() { return directory; }
    
    public void setDirectory(QDir directory) {
        this.directory = directory;
        update();        
    }    
    
    public QImage imageAt(int row) {
        return pixmaps[row].image();
    }

    public int columnCount(QModelIndex parent) {
        return 4;
    }
    
    public QModelIndex parent(QModelIndex child) {
        return null;
    }
        
    public QModelIndex index(int row, int column, QModelIndex parent) {
        if (parent == null || !parent.isValid())
            return createIndex(row, column);
        return null;
    }

    public Object data(QModelIndex index, int role) {
        int row = index.row();
        int col = index.column();
        
        if (role == Qt.ItemDataRole.DisplayRole) {
            QFileInfo info = infos.get(row);
            if (col == NAME_COL) {
                return info.fileName();
            } else if (col == SIZE_COL) {
                return info.size();
            } else if (col == DIMENSION_COL) {
                if (pixmaps[row].isValid()) {
                    QSize size = pixmaps[row].size();
                    return "[" + size.width() + ", " + size.height() + "]";
                } else {
                    return "loading";
                }
            } else if (col == PIXMAP_COL) {
                return pixmaps[row].isValid() ? null : tr("loading");
            }
        } else if (role == Qt.ItemDataRole.DecorationRole) {
            if (col == PIXMAP_COL) 
                return pixmaps[row].isValid() ? pixmaps[row].thumbNail() : null;
        } else if (role == Qt.ItemDataRole.SizeHintRole) {
            return LazyPixmap.SMALL_SIZE;
        }        
        return null;
    }

    public int rowCount(QModelIndex parent__0) {
        int count = infos != null ? infos.size() : 0;
        return count;
    }
    
    public Object headerData(int section, Qt.Orientation orientation, int role) {
        if (orientation == Qt.Orientation.Horizontal 
            && (role == Qt.ItemDataRole.DisplayRole || role == Qt.ItemDataRole.EditRole)) {
            switch (section) {
            case NAME_COL: return "Name";
            case SIZE_COL: return "Size";
            case DIMENSION_COL: return "Dimension";
            case PIXMAP_COL: return "Preview";
            }
        }
        return super.headerData(section, orientation, role);
    }
    
    public void updateRow(int i) {
        dataChanged.emit(index(i, 2), index(i, 3));     
    }
    
    private void update() {        
        List<QByteArray> formats = QImageReader.supportedImageFormats();
        List<String> filters = new ArrayList<String>(formats.size());
        for (QByteArray ba : formats) {
            filters.add("*." + ba.toString());
        }
                
        infos = directory.entryInfoList(filters, 
                new QDir.Filters(QDir.Filter.Files), 
                new QDir.SortFlags(QDir.SortFlag.NoSort));
        
        mapper.mappedInteger.connect(this, "updateRow(int)");
        
        pixmaps = new LazyPixmap[infos.size()];
        for (int i=0; i<pixmaps.length; ++i) {
            pixmaps[i] = new LazyPixmap(infos.get(i).absoluteFilePath());
            pixmaps[i].loaded.connect(mapper, "map()");
            mapper.setMapping(pixmaps[i], i);
        }
        reset();     
        
        synchronized (loadQueue) {
            for (LazyPixmap l : pixmaps) 
                loadQueue.offer(l);
        }
    }
    
    private QDir directory;
    private List<QFileInfo> infos;    
    private LazyPixmap pixmaps[];
    private LinkedBlockingQueue<LazyPixmap> loadQueue = new LinkedBlockingQueue<LazyPixmap>();
    private QSignalMapper mapper = new QSignalMapper();
}
