package com.trolltech.examples;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.util.*;

class StarWindow extends QWidget {
    private QTableWidget table;

    private Object tableContent[][] = {
        { tr("Mass in B-Minor"), tr("Baroque"), tr("JS Bach"),
          new StarRating(5) },
        { tr("Sex Bomb"), tr("Pop"), tr("Tom Jones"), new StarRating(2) },
        { tr("Three More Foxes"), tr("jazz"), tr("Maynard Ferguson"),
          new StarRating(4) },
        { tr("Barbie Girl"), tr("Pop"), tr("Aqua"), new StarRating(5) }
    };

    public StarWindow()
    {
        createTable();

        QGridLayout layout = new QGridLayout();
        layout.addWidget(table, 0, 0);
        setLayout(layout);
        setWindowTitle(tr("Star Delegate"));
    }

    public void createTable()
    {
        LinkedList<String> headers = new LinkedList<String>();

        table = new QTableWidget(4, 4);

        table.setItemDelegate(new StarDelegate(table));

        table.setEditTriggers(QAbstractItemView.EditTrigger.DoubleClicked,
                              QAbstractItemView.EditTrigger.SelectedClicked);
        table.setSelectionBehavior(
            QAbstractItemView.SelectionBehavior.SelectRows);

        headers.add(tr("Title"));
        headers.add(tr("Genre"));
        headers.add(tr("Artist"));
        headers.add(tr("Rating"));
        table.setHorizontalHeaderLabels(headers);

        for (int i = 0; i < tableContent.length; i++) {
            table.setItem(i, 0,
                new QTableWidgetItem((String) tableContent[i][0]));
            table.setItem(i, 1,
                new QTableWidgetItem((String) tableContent[i][1]));
            table.setItem(i, 2,
                new QTableWidgetItem((String) tableContent[i][2]));

            QTableWidgetItem rRating = new QTableWidgetItem();
            rRating.setData(Qt.ItemDataRole.DisplayRole, tableContent[i][3]);
            table.setItem(i, 3, rRating);
        }

        table.resizeColumnsToContents();
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        QWidget widget = new StarWindow();
        widget.show();
        widget.resize(470, 200);

        QApplication.exec();
    }
}
