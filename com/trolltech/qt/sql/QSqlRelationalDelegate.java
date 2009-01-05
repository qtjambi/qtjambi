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

package com.trolltech.qt.sql;

import com.trolltech.qt.gui.QItemDelegate;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QStyleOptionViewItem;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QAbstractItemModel;
import com.trolltech.qt.core.Qt;

public class QSqlRelationalDelegate extends QItemDelegate {
    public QSqlRelationalDelegate() {
        this(null);
    }

    public QSqlRelationalDelegate(QObject parent) {
        super(parent);
    }


    @Override
    public QWidget createEditor(QWidget parent,
                                QStyleOptionViewItem option,
                                QModelIndex index) {
        QSqlRelationalTableModel sqlModel = (QSqlRelationalTableModel)index.model();
        QSqlTableModel childModel = sqlModel != null ? sqlModel.relationModel(index.column()) : null;
        if (childModel == null)
            return super.createEditor(parent, option, index);

        QComboBox combo = new QComboBox(parent);
        combo.setModel(childModel);
        combo.setModelColumn(childModel.fieldIndex(sqlModel.relation(index.column()).displayColumn()));
        combo.installEventFilter(this);

        return combo;
    }

    @Override
    public void setEditorData(QWidget editor, QModelIndex index) {
        QSqlRelationalTableModel sqlModel = (QSqlRelationalTableModel)index.model();
        QComboBox combo = editor instanceof QComboBox ? (QComboBox) editor : null;
        if (sqlModel == null || combo == null) {
            super.setEditorData(editor, index);
            return;
        }
        combo.setCurrentIndex(combo.findText(sqlModel.data(index).toString()));
    }

    @Override
    public void setModelData(QWidget editor, QAbstractItemModel model, QModelIndex index) {
        if (index == null)
            return;

        QSqlRelationalTableModel sqlModel = (QSqlRelationalTableModel)model;
        QSqlTableModel childModel = sqlModel != null ? sqlModel.relationModel(index.column()) : null;
        QComboBox combo = editor instanceof QComboBox ? (QComboBox) editor : null;
        if (sqlModel == null || childModel == null || combo == null) {
            super.setModelData(editor, model, index);
            return;
        }

        int currentItem = combo.currentIndex();
        int childColIndex = childModel.fieldIndex(sqlModel.relation(index.column()).displayColumn());
        int childEditIndex = childModel.fieldIndex(sqlModel.relation(index.column()).indexColumn());
        sqlModel.setData(index,
                         childModel.data(childModel.index(currentItem, childColIndex), Qt.ItemDataRole.DisplayRole),
                         Qt.ItemDataRole.DisplayRole);
        sqlModel.setData(index,
                childModel.data(childModel.index(currentItem, childEditIndex), Qt.ItemDataRole.EditRole),
                Qt.ItemDataRole.EditRole);
    }
}
