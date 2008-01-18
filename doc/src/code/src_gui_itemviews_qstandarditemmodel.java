/*   Ported from: src.gui.itemviews.qstandarditemmodel.cpp
<snip>
//! [0]
            QStandardItemModel model(4, 4);
            for (int row = 0; row < 4; ++row) {
                for (int column = 0; column < 4; ++column) {
                    QStandardItem *item = new QStandardItem(QString("row %0, column %1").arg(row).arg(column));
                    model.setItem(row, column, item);
                }
            }
//! [0]


//! [1]
            QStandardItemModel model;
            QStandardItem *parentItem = model.invisibleRootItem();
            for (int i = 0; i < 4; ++i) {
                QStandardItem *item = new QStandardItem(QString("item %0").arg(i));
                parentItem->appendRow(item);
                parentItem = item;
            }
//! [1]


//! [2]
        QTreeView *treeView = new QTreeView(this);
        treeView->setModel(myStandardItemModel);
        connect(treeView, SIGNAL(clicked(QModelIndex)),
                this, SLOT(clicked(QModelIndex)));
//! [2]


//! [3]
        void MyWidget::clicked(const QModelIndex &index)
        {
            QStandardItem *item = myStandardItemModel->itemFromIndex(index);
            // Do stuff with the item ...
        }
//! [3]


//! [4]
        treeView->scrollTo(item->index());
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_itemviews_qstandarditemmodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
    public void snippet0(QStandardItem item) {
//! [0]
            QStandardItemModel model = new QStandardItemModel(4, 4);
            for (int row = 0; row < 4; ++row) {
                for (int column = 0; column < 4; ++column) {
                    QStandardItem tem = new QStandardItem("row " + row +", column " + column);
                    model.setItem(row, column, item);
                }
            }
//! [0]
    }

    public void snippet1() {
//! [1]
            QStandardItemModel model = new QStandardItemModel();
            QStandardItem parentItem = model.invisibleRootItem();
            for (int i = 0; i < 4; ++i) {
                QStandardItem item = new QStandardItem("item " + i);
                parentItem.appendRow(item);
                parentItem = item;
            }
//! [1]
    }

class MyWidget extends QWidget {
    public void snippet2(QStandardItemModel myStandardItemModel) {
//! [2]
        QTreeView treeView = new QTreeView(this);
        treeView.setModel(myStandardItemModel);
        treeView.clicked.connect(this, "clicked(QModelIndex)");
//! [2]
    }

    QStandardItemModel myStandardItemModel;
//! [3]
        protected void clicked(QModelIndex index)
        {
            QStandardItem tem = myStandardItemModel.itemFromIndex(index);
            // Do stuff with the item ...
        }
//! [3]

    public void snippet3(QTreeView treeView, QStandardItem item) {
//! [4]
        treeView.scrollTo(item.index());
//! [4]
    }
}
}