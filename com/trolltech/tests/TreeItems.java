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


class Item extends QStandardItem {
    public Item() {
        disableGarbageCollection();
    }

    @Override
    public Object data(int role) {
        if (role == Qt.ItemDataRole.DisplayRole)
            return "Item " + id;
        if (role == Qt.ItemDataRole.DecorationRole)
            return pixmap;
        return null;
    }

    public void setIcon(QPixmap p) {
        pixmap = p;
    }

    private static int idCounter = 0;
    private int id = ++idCounter;
    private QPixmap pixmap;
}


public class TreeItems {

    public static QStandardItemModel setupModel() {
        QStandardItemModel model = new QStandardItemModel();

        Item r1 = new Item();
        Item r2 = new Item();
        Item r3 = new Item();

        Item s11 = new Item();
        Item s12 = new Item();
        Item s21 = new Item();

        r1.appendRow(s11);
        r1.appendRow(s12);
        r2.appendRow(s21);

        model.appendRow(r1);
        model.appendRow(r2);
        model.appendRow(r3);

        return model;
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        QTreeView view = new QTreeView();
        view.show();

        view.setModel(setupModel());

        QApplication.exec();
    }
}
