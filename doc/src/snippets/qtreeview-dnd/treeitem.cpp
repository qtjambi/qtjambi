/****************************************************************************
**
** Copyright (C) 2005-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
    treeitem.cpp

    A container for items of data supplied by the simple tree model.
*/

#include <QStringList>

#include "treeitem.h"

TreeItem::TreeItem(const QList<QVariant> &data, TreeItem *parent)
{
    parentItem = parent;
    itemData = data;
}

TreeItem::~TreeItem()
{
    qDeleteAll(childItems);
}

void TreeItem::appendChild(TreeItem *item)
{
    childItems.append(item);
}

TreeItem *TreeItem::child(int row)
{
    return childItems.value(row);
}

int TreeItem::childCount() const
{
    return childItems.count();
}

int TreeItem::columnCount() const
{
    return itemData.count();
}

QVariant TreeItem::data(int column) const
{
    return itemData.value(column);
}

bool TreeItem::insertChild(int row, TreeItem *item)
{
    if (row < 0 || row > childItems.count())
        return false;

    childItems.insert(row, item);
    return true;
}

TreeItem *TreeItem::parent()
{
    return parentItem;
}

bool TreeItem::removeChild(int row)
{
    if (row < 0 || row >= childItems.count())
        return false;

    delete childItems.takeAt(row);
    return true;
}

int TreeItem::row() const
{
    if (parentItem)
        return parentItem->childItems.indexOf(const_cast<TreeItem*>(this));

    return 0;
}

bool TreeItem::setData(int column, const QVariant &data)
{
    if (column < 0 || column >= itemData.count())
        return false;

    itemData.replace(column, data);
    return true;
}
