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

import java.util.*;

public class ResourceBrowserModel extends QAbstractItemModel {

    public static final QSize PIXMAP_SIZE = new QSize(16, 16);
    private static final String DEFAULT_PACKAGE = "<default package>";
    private static final QPixmap FOLDER_PIXMAP =
            new QPixmap("classpath:com/trolltech/tools/designer/folder.png").
                    scaled(PIXMAP_SIZE,
                            Qt.AspectRatioMode.KeepAspectRatio,
                            Qt.TransformationMode.SmoothTransformation);

    private static class NamedItem implements Comparable {
        public NamedItem(String name) {
            this.name = name;
        }
        @Override
        public String toString() { return name; }
        public int compareTo(Object o) { return name.compareTo(o.toString()); }
        String name;
    }

    private static class Path extends NamedItem implements Comparable {
        List<Resource> images;

        public Path(String name) {
            super(name);
            images = new ArrayList<Resource>();
        }

        public void addResource(String name, String fullName, QImage image) {
            Resource r = new Resource(name);
            r.fullName = fullName;
            r.image = image;
            images.add(r);
        }
    }

    public static class FilterModel extends QSortFilterProxyModel {

        public FilterModel(QObject parent) {
            super(parent);
        }

        private boolean pathContains(Path p, QRegExp re) {
            for (Resource r : p.images)
                if (re.indexIn(r.name) >= 0)
                    return true;
            return false;
        }

        @Override
        protected boolean filterAcceptsRow(int row, QModelIndex parent) {
            ResourceBrowserModel m = (ResourceBrowserModel) sourceModel();

            QRegExp re = filterRegExp();
            if (parent == null) {
                Path p = m.roots.get(row);
                return re.indexIn(p.name) >= 0 || pathContains(p, re);
            } else {
                Path p = m.roots.get(parent.row());
                Resource r = p.images.get(row);
                return re.indexIn(p.name) >= 0 || re.indexIn(r.name) >= 0;
            }
        }
    }

    public static class Resource extends NamedItem implements Comparable {
        public Resource(String name) {
            super(name);
        }
        String fullName;
        QImage image;
    }

    public Signal1<QModelIndex> rowsAdded = new Signal1<QModelIndex>();

    public ResourceBrowserModel() {
        QPalette p = QApplication.palette();

        QLinearGradient lg = new QLinearGradient(0, 0, 0, 16);
        lg.setColorAt(0, p.color(QPalette.ColorRole.AlternateBase));
        lg.setColorAt(0.1, p.color(QPalette.ColorRole.Base));
        lg.setColorAt(1, p.color(QPalette.ColorRole.AlternateBase));
        gradient = new QBrush(lg);
    }

    public String resource(QModelIndex index) {
        int id = (int) index.internalId();
        if (id > 0) {
            Path p = roots.get(id - 1);
            return p.images.get(index.row()).fullName;
        }
        return null;
    }

    private static String pathName(String name) {
        int pathPos = Math.max(name.indexOf(':'), name.indexOf('#')) + 1;
        if (name.charAt(pathPos) == '/')
            ++pathPos;
        int filePos = name.lastIndexOf('/');
        if (pathPos >= filePos)
            return null;
        return name.substring(pathPos, filePos).replace('/', '.');
    }

    private static String imageName(String name) {
        int filePos = name.lastIndexOf('/');
        return name.substring(filePos+1);
    }

    public QModelIndex indexForPath(String s) {
        String pathName = pathName(s).replace('/', '.');
        String imageName = null;

        for (int i=0; i<roots.size(); ++i) {
            Path p = roots.get(i);
            if (p.name.equals(pathName)) {
                if (imageName == null)
                    imageName = imageName(s);
                for (int j=0; j<p.images.size(); ++j) {
                    Resource r = p.images.get(j);
                    if (imageName.equals(r.name)) {
                        return createIndex(j, 0, i+1);
                    }
                }
            }

        }

        return null;
    }

    public void addResource(String name, QImage image) {
//        if (!name.startsWith("classpath:"))
//            throw new RuntimeException("Bad resource name: " + name);
        String pathName = pathName(name);
        String imageName = imageName(name);

        if (pathName == null)
            return;

        if (pathName.length() == 0)
            pathName = DEFAULT_PACKAGE;

        int pathIndex = resolvePath(pathName);
        Path path = roots.get(pathIndex);

        int s = path.images.size();

        QModelIndex parent = index(pathIndex, 0, null);
        beginInsertRows(parent, s, s);
        path.addResource(imageName, name, image);
        endInsertRows();

        rowsAdded.emit(parent);
    }


    @SuppressWarnings("unchecked")
    private int resolvePath(String name) {
        name = name.intern();
        for (int i=0; i<roots.size(); ++i) {
            Path p = roots.get(i);
            if (p.name == name) {
                return i;
            }
        }
        int s = roots.size();
        beginInsertRows(null, s, s);

        Path p = new Path(name);
        roots.add(p);

        endInsertRows();

        return s;
    }

    @Override
    public int columnCount(QModelIndex parent) {
        return 1;
    }

    @Override
    public Object data(QModelIndex index, int role) {
        if (index == null)
            return null;

        if (index.internalId() > 0) {
            Path p = roots.get((int) index.internalId()-1);
            Resource r = p.images.get(index.row());
            if (role == Qt.ItemDataRole.DisplayRole)
                return r.name;
            else if (role == Qt.ItemDataRole.DecorationRole)
                return r.image;
        } else {
            if (role == Qt.ItemDataRole.DisplayRole)
                return roots.get(index.row()).name;
            else if (role == Qt.ItemDataRole.BackgroundRole)
                return gradient;
//            else if (role == Qt.ItemDataRole.BackgroundRole)
//                return QApplication.palette().alternateBase().color();
            else if (role == Qt.ItemDataRole.DecorationRole)
                return FOLDER_PIXMAP;
        }

        return null;
    }

    @Override
    public QModelIndex index(int row, int column, QModelIndex parent) {
        if (parent == null) {
            return createIndex(row, column);
        } else {
            return createIndex(row, column, parent.row() + 1);
        }
    }

    @Override
    public QModelIndex parent(QModelIndex child) {
        if (child.internalId() == 0)
            return null;
        return createIndex((int) child.internalId() - 1, 0);
    }

    @Override
    public int rowCount(QModelIndex parent) {
        if (parent == null)
            return roots.size();
        else if (parent.internalId() == 0)
            return roots.get(parent.row()).images.size();
        else
            return 0;
    }

    private List<Path> roots = new ArrayList<Path>();

    private QBrush gradient;
}
