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

package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

/**
 * A pretty basic node implementation...
 */
class Node {

    public Node(String s, Model model, Node parent) {
        this.text = s;
        this.model = model;
        this.parent = parent;
    }

    @Override
    public String toString() { return text + ":" + counter; }

    List<Node> children = new ArrayList<Node>();
    String text;
    int counter;
    Model model;
    Node parent;
}

/**
 * An example model implementation. It reimplements child(), childCount() and text() to
 * represent the data in a tree of Node's
 */
class Model extends QTreeModel {

    /**
     * Called to query the child of parent at index. If parent is null we have only one child,
     * the root.
     */
    @Override
    public Object child(Object parent, int index) {
        if (parent == null)
            return root;
        return ((Node) parent).children.get(index);
    }

    /**
     * Called to query the number of children of the given object or the number of root objects if
     * parent is null.
     */
    @Override
    public int childCount(Object parent) {
        int count = parent == null ? 1 : ((Node) parent).children.size();
        return count;
    }

    /**
     * Convenience virtual function to get the textual value of an object. I could also
     * implement icon() for pixmap data or the data() function for other types of roles.
     */
    @Override
    public String text(Object value) {
        return "" + value;
    }

    public Node root() { return root; }

    private Node root = new Node("Root", this, null);
}

/**
 * A simple test application. It adds four actions, "add", "remove" and
 * "increment" and "swap" to test that we can add, remove and change the
 * value of nodes in the tree.
 */
public class TreeModelTester extends QTreeView {

    public TreeModelTester() {
        this(null);
    }

    public TreeModelTester(QWidget parent) {
        super(parent);

        setModel(model);

        QAction add = new QAction(this);
        add.setShortcut("Ctrl+A");
        add.triggered.connect(this, "add()");

        QAction remove = new QAction(this);
        remove.setShortcut("Ctrl+R");
        remove.triggered.connect(this, "remove()");

        QAction increment = new QAction(this);
        increment.setShortcut("Ctrl+I");
        increment.triggered.connect(this, "increment()");

        QAction swap = new QAction(this);
        swap.setShortcut("Ctrl+S");
        swap.triggered.connect(this, "swap()");

        addAction(add);
        addAction(remove);
        addAction(increment);
        addAction(swap);
    }

    public void setModel(Model model) {
        this.model = model;
        super.setModel(model);
    }


    private void swap() {
        System.out.println("Swapping?");
        List<QModelIndex> pos = selectedIndexes();
        for (QModelIndex i : pos) {
            Node me = (Node) model.indexToValue(i);

            Node parent = me.parent;

            if (me.parent != null) {
                int mepos = parent.children.indexOf(me);
                if (mepos < parent.children.size() - 1) {
                    Node other = parent.children.get(mepos);

                    QModelIndex parentIndex = model.valueToIndex(parent);

                    parent.children.remove(me);
                    model.childrenRemoved(parentIndex, mepos, mepos);

                    parent.children.add(mepos + 1, me);
                    model.childrenInserted(parentIndex, mepos + 1, mepos + 1);

                } else {
                    System.out.println("cannot swap last element...");
                }

            } else {
                System.out.println("cannot swap the root node..");
            }
        }
    }


    private void add() {
        List<QModelIndex> pos = selectedIndexes();
        for (QModelIndex i : pos) {
            Node n = (Node) model.indexToValue(i);
            assert n != null;

            int size = n.children.size();

            Node child = new Node(names[(int) (Math.random() * names.length)], model, n);
            n.children.add(child);

            model.childrenInserted(i, size, size);
        }
    }

    @SuppressWarnings("unused")
    private void remove() {
        List<QModelIndex> pos = selectedIndexes();
        for (QModelIndex i : pos) {
            Node n = (Node) model.indexToValue(i);
            assert n != null;

            if (n == model.root())
                continue;

            QModelIndex parentIndex = model.parent(i);
            Node parent = (Node) model.indexToValue(parentIndex);

            parent.children.remove(n);
            model.childrenRemoved(parentIndex, i.row(), i.row());
        }
    }

    @SuppressWarnings("unused")
    private void increment() {
        List<QModelIndex> pos = selectedIndexes();
        for (QModelIndex i : pos) {
            Node n = (Node) model.indexToValue(i);
            n.counter++;

            model.dataChanged.emit(i, i);
        }
    }

    private Model model = new Model();
    private String names[] = { "Alpha", "Beta", "Gamma", "Delta" };

    public static void main(String args[]) {
        QApplication.initialize(args);
        TreeModelTester w = new TreeModelTester();
        w.setWindowTitle("Tree Model Tester");
        w.show();

        if (args.length > 0 && args[0].equals("--extra-view")) {
            for (int i=0; i<3; ++i) {
                TreeModelTester t = new TreeModelTester();
                t.setWindowTitle("Extra view: " + (i+1));
                t.disableGarbageCollection();
                t.show();
                t.setModel(w.model);
            }
        }

        QApplication.exec();
    }
}
