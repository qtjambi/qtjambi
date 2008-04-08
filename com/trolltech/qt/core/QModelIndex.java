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
    private QModelIndex(int row, int column, long internalId, QAbstractItemModel model) {
        this.row = row;
        this.column = column;
        this.internalId = internalId;
        this.model = model;
    }

    public int row() { return row; }

    public int column() { return column; }

    public long internalId() { return internalId; }

    /**
     * Queries the model for the parent of this item. This is equivalent to calling
     * <pre>
     * item.model().parent(item);
     * </re>
     * @return The parent index for this model index.
     */
    public QModelIndex parent() {
        return model != null ? model.parent(this) : null;
    }

    /**
     * Queries the model for a sibling of this model index.
     * @param arow The row for the sibling.
     * @param acolumn The column for the sibling.
     * @return The sibling of this item at the given position.
     */
    public QModelIndex sibling(int arow, int acolumn) {
        return model != null ? model.index(arow, acolumn, model.parent(this)) : null;
    }


    /**
     * Queries the model for a child index of this index.
     * @param arow the row of the child.
     * @param acolumn the column of the child.
     * @return The child at the given position.
     */
    public QModelIndex child(int arow, int acolumn) {
        return model != null ? model.index(arow, acolumn, this) : null;
    }

    /**
     * Queries the model for the default data for this index. The default data is
     * specified in <code>Qt.ItemDataRole.DisplayRole</code>.
     * @return The data for this index
     */
    public Object data() { return data(Qt.ItemDataRole.DisplayRole); }

    /**
     * Queries the model for data for this index.
     * @param role The data role to query for. The different data roles are available in
     * <code>com.trolltech.qt.core.Qt.ItemDataRole</code>
     * @return The data for this index.
     */
    public Object data(int role) {
        return model != null ? model.data(this, role) : null;
    }

    /**
     * @return The model for this index.
     */
    public QAbstractItemModel model() {
        return model;
    }

    /**
     * Compares this model index to another
     * @param other The object to compare to
     * @return True if the objects are equal; otherwise returns false.
     */
    public boolean equals(Object other) {
        if (!(other instanceof QModelIndex))
            return false;
        QModelIndex oi = (QModelIndex) other;
        return oi.row == row
            && oi.column == column
            && oi.internalId == internalId
            && oi.model == model;
    }

    public int hashCode() {
        return row << 4 + column + internalId;
    }

    public String toString() {
        return new StringBuilder()
                .append("QModelIndex(row=")
                .append(row)
                .append(",col=")
                .append(column)
                .append(",internal=")
                .append(internalId)
                .append(")").toString();
    }

    private int row;
    private int column;
    private long internalId;
    private QAbstractItemModel model;
}
