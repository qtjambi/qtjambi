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

package com.trolltech.qt.core;

public class QModelIndex
{
    @SuppressWarnings("unused")
    private QModelIndex(int row, int column, long internalId, QAbstractItemModel model) {
        this.row = row;
        this.column = column;
        this.internalId = internalId;
        this.model = model;
    }

    public QModelIndex() {
        row = -1;
        column = -1;
        internalId = 0;
        model = null;
    }

    public int row() { return row; }
    public int column() { return column; }
    public long internalId() { return internalId; }

    public  QModelIndex parent() {
        return model != null ? model.parent(this) : null;
    }

    public QModelIndex sibling(int arow, int acolumn) {
        return model != null ? model.index(arow, acolumn, model.parent(this)) : null;
    }

    public QModelIndex child(int arow, int acolumn) {
        return model != null ? model.index(arow, acolumn, this) : null;
    }

    public Object data() { return data(Qt.ItemDataRole.DisplayRole); }
    public Object data(int role) {
        return model != null ? model.data(this, role) : null;
    }

    public QAbstractItemModel model() {
        return model;
    }

    public boolean isValid() {
        return (row >= 0) && (column >= 0) && (model != null);
    }

    public boolean equals(Object other) {
        if (!(other instanceof QModelIndex))
            return false;
        QModelIndex oi = (QModelIndex) other;
        return oi.row == row
            && oi.column == column
            && oi.internalId == internalId
            && oi.model == model;
    }

    private int row;
    private int column;
    private long internalId;
    private QAbstractItemModel model;
}
