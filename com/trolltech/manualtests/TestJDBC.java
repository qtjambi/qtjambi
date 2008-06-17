package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.sql.*;

class QSqlRelationalDelegate extends QItemDelegate
{
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


public class TestJDBC {
    public static void main(String args[])  {
        QApplication.initialize(args);

        com.trolltech.qt.sql.QJdbc.initialize();

        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (Exception ex) { System.err.println(ex); return; }

        QSqlDatabase db = QSqlDatabase.addDatabase("QJDBC");
        db.setDatabaseName("jdbc:postgresql://localhost/main");
        db.setUserName("qt");
        db.setPassword("qqqqqq");
        if (db.open()) {
            System.out.println("Connected!");
        } else {
            System.out.println("Connection Failed!");
            System.out.println(db.lastError().text());
            return;
        }

        QSqlRelationalTableModel model = new QSqlRelationalTableModel(null, db);
        model.setTable("person");
        model.setEditStrategy(QSqlTableModel.EditStrategy.OnFieldChange);
        model.setRelation(2, new QSqlRelation("country", "id", "name"));
        if (!model.select()) {
            System.err.println(model.lastError().text());
        }
        QTableView view = new QTableView();
        view.setModel(model);
        view.setItemDelegate(new QSqlRelationalDelegate(view));
        view.show();

        QApplication.exec();

        db.close();
    }
}

