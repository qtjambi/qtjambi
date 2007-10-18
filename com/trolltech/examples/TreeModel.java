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

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

@QtJambiExample(name = "A Jambi Tree Model")
public class TreeModel extends QTreeView
{
    private static class Model extends QTreeModel {
        public Model(QObject parent) {
            super(parent);
        }


        /**
         * Returns the number of children this parent has.
         */
        @Override
        public int childCount(Object parent) {
            return entryList(dir(parent)).size();
        }


        /**
         * Converts the parent to a QDir and accesses its entry list to
         * create a new QDir as the child.
         */
        @Override
        public Object child(Object parent, int row) {
            QDir d = dir(parent);
            return new QDir(d.absoluteFilePath(entryList(d).get(row)));
        }


        /**
         * Converts the directory to a QDir and returns it.
         */
        @Override
        public String text(Object value) {
            return ((QDir) value).dirName();
        }


        /**
         * Convenience method for getting the entry list of a
         * directory. This method also caches the previous entry list, as
         * the same entry list is often used again and again.
         */
        private List<String> entryList(QDir dir) {
            if (dir == lastDirectory) {
                return lastEntryList;
            }
            lastEntryList = dir.entryList(entryListFlags);
            lastDirectory = dir;
            return lastEntryList;
        }

        /**
         * Convenience method for converting the object to a QDir. If the
         * value is null, its the root.
         */
        private QDir dir(Object value) {
            return value != null ? (QDir) value : QDir.root();
        }


        private QDir lastDirectory;
        private List<String> lastEntryList;
        private QDir.Filters entryListFlags = new QDir.Filters(QDir.Filter.NoDotAndDotDot,
                                                               QDir.Filter.Dirs,
                                                               QDir.Filter.Files);
    }

    public TreeModel() {
        this(null);
    }

    public TreeModel(QWidget parent) {
        // Hide my header...
        header().hide();

        // Create our jambi file model
        Model model = new Model(this);
        setModel(model);
        show();

        // The QTreeModel does some caching of datastructures
        // behind the scenes. To avoid memory pooling of this
        // datastructure we can call the releaseChildren method to
        // release parts of this pool when its no longer needed. One
        // such case is when the view collapses a subtree and it is no
        // longer visible.
        collapsed.connect(model, "releaseChildren(QModelIndex)");
        setWindowTitle("A Qt Jambi Tree Model");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        // Create the tree view.
        TreeModel view = new TreeModel();
        view.show();

        // Spin the event loop
        QApplication.exec();
    }
}
