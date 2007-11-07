/****************************************************************************
**
** Copyright (C) 2006-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include <QtGui>

int main(int argc, char **argv)
{
    QApplication app(argc, argv);

//! [0]
    QFontDatabase database;
    QTreeWidget fontTree;
    fontTree.setColumnCount(2);
    fontTree.setHeaderLabels(QStringList() << "Font" << "Smooth Sizes");

    foreach (QString family, database.families()) {
        QTreeWidgetItem *familyItem = new QTreeWidgetItem(&fontTree);
        familyItem->setText(0, family);

        foreach (QString style, database.styles(family)) {
            QTreeWidgetItem *styleItem = new QTreeWidgetItem(familyItem);
            styleItem->setText(0, style);

            QString sizes;
            foreach (int points, database.smoothSizes(family, style))
                sizes += QString::number(points) + " ";

            styleItem->setText(1, sizes.trimmed());
        }
//! [0] //! [1]
    }
//! [1]

    fontTree.resizeColumnToContents(0);
    fontTree.show();
    return app.exec();
}
