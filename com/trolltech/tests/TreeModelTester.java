package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

class Node extends QObject {

    public Node(String s, Model model) {
        this.text = s;
        this.model = model;

        System.out.println("new node: " + s);

    }

    public String toString() { return text + ":" + counter; }

    List<Node> children = new ArrayList<Node>();
    String text;
    int counter;
    Model model;
}

class Model extends QTreeModel {

    public Object child(Object parent, int index) {
        System.out.println(" ---> child of " + parent + " at " + index);
        if (parent == null)
            return root;
        return ((Node) parent).children.get(index);
    }

    public int childCount(Object parent) {
        int count = parent == null ? 1 : ((Node) parent).children.size();
        System.out.println(" ---> childCount for " + parent + ", count=" + count);
        return count;
    }

    public String text(Object value) {
        return "" + value;
    }

    public Node root() { return root; }

    private Node root = new Node("Root", this);
}

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

        addAction(add);
        addAction(remove);
        addAction(increment);
    }


    private void add() {
        System.out.println("adding...");

        List<QModelIndex> pos = selectedIndexes();
        for (QModelIndex i : pos) {
            Node n = (Node) model.indexToValue(i);
            assert n != null;

            int size = n.children.size();

            Node child = new Node(names[(int) (Math.random() * names.length)], model);
            n.children.add(child);

            model.childrenInserted(i, size, size);
        }
    }

    private void remove() {
        System.out.println("removing...");

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

            n.dispose();
        }
    }

    private void increment() {
        System.out.println("increment");

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
        w.show();

        QApplication.exec();
    }
}
