package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.sql.*;

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

