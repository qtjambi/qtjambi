/*   Ported from: src.gui.itemviews.qtreewidget.cpp
<snip>
//! [0]
    QTreeWidget *treeWidget = new QTreeWidget();
    treeWidget->setColumnCount(1);
    QList<QTreeWidgetItem *> items;
    for (int i = 0; i < 10; ++i)
        items.append(new QTreeWidgetItem((QTreeWidget*)0, QStringList(QString("item: %1").arg(i))));
    treeWidget->insertTopLevelItems(0, items);
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_itemviews_qtreewidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QTreeWidget reeWidget = new QTreeWidget();
    treeWidget.setColumnCount(1);
    QList<QTreeWidgetItem *> items;
    for (int i = 0; i < 10; ++i)
        items.append(new QTreeWidgetItem((QTreeWidget*)0, List<String>(QString("item: %1").arg(i))));
    treeWidget.insertTopLevelItems(0, items);
//! [0]


    }
}
