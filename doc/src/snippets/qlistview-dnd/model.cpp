/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of an example program for Qt.
** EDITIONS: NOLIMITS
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
  model.cpp

  A simple model that uses a QStringList as its data source.
*/

#include <QtGui>

#include "model.h"

DragDropListModel::DragDropListModel(const QStringList &strings,
                                     QObject *parent)
    : QStringListModel(strings, parent)
{
}

//! [0]
bool DragDropListModel::dropMimeData(const QMimeData *data,
    Qt::DropAction action, int row, int column, const QModelIndex &parent)
{
    if (action == Qt::IgnoreAction)
        return true;

    if (!data->hasFormat("application/vnd.text.list"))
        return false;

    if (column > 0)
//! [0] //! [1]
        return false;
//! [1]

//! [2]
    int beginRow;

    if (row != -1)
        beginRow = row;
//! [2] //! [3]
    else if (parent.isValid())
        beginRow = parent.row();
//! [3] //! [4]
    else
        beginRow = rowCount(QModelIndex());
//! [4]

//! [5]
    QByteArray encodedData = data->data("application/vnd.text.list");
    QDataStream stream(&encodedData, QIODevice::ReadOnly);
    QStringList newItems;
    int rows = 0;

    while (!stream.atEnd()) {
        QString text;
        stream >> text;
        newItems << text;
        ++rows;
    }
//! [5]

//! [6]
    insertRows(beginRow, rows, QModelIndex());
    foreach (QString text, newItems) {
        QModelIndex idx = index(beginRow, 0, QModelIndex());
        setData(idx, text);
        beginRow++;
    }

    return true;
}
//! [6]

//! [7]
Qt::ItemFlags DragDropListModel::flags(const QModelIndex &index) const
{
    Qt::ItemFlags defaultFlags = QStringListModel::flags(index);

    if (index.isValid())
        return Qt::ItemIsDragEnabled | Qt::ItemIsDropEnabled | defaultFlags;
    else
        return Qt::ItemIsDropEnabled | defaultFlags;
}
//! [7]

//! [8]
QMimeData *DragDropListModel::mimeData(const QModelIndexList &indexes) const
{
    QMimeData *mimeData = new QMimeData();
    QByteArray encodedData;

    QDataStream stream(&encodedData, QIODevice::WriteOnly);

    foreach (QModelIndex index, indexes) {
        if (index.isValid()) {
            QString text = data(index, Qt::DisplayRole).toString();
            stream << text;
        }
    }

    mimeData->setData("application/vnd.text.list", encodedData);
    return mimeData;
}
//! [8]

//! [9]
QStringList DragDropListModel::mimeTypes() const
{
    QStringList types;
    types << "application/vnd.text.list";
    return types;
}
//! [9]

//! [10]
Qt::DropActions DragDropListModel::supportedDropActions() const
{
    return Qt::CopyAction | Qt::MoveAction;
}
//! [10]
