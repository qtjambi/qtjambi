package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.sql.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class TestJDBC {

    private enum Driver {
        PostgreSQL,
        Derby
    }

    static private String driverNames[] = {
        "org.postgresql.Driver",
        "org.apache.derby.jdbc.EmbeddedDriver"
    };

    static private String dbNames[] = {
        "jdbc:postgresql://qfwfq.europe.nokia.com/main",
        "jdbc:derby:firstdb" // only usable locally, since no server is set up. Install derby and make the
                             // database on your local machine to run this test.
    };

    private static void makeRelationalTable(QTabWidget topLevel, QSqlDatabase db)  {
        QSqlRelationalTableModel model = new QSqlRelationalTableModel(null, db);
        model.setTable("person");
        model.setEditStrategy(QSqlTableModel.EditStrategy.OnFieldChange);
        model.setRelation(2, new QSqlRelation("country", "id", "name"));
        if (!model.select()) {
            System.err.println(model.lastError().text());
        }
        QTableView view = new QTableView();
        view.setWindowTitle("QSqlRelationalTableModel");
        view.setModel(model);
        view.setItemDelegate(new QSqlRelationalDelegate(view));
        topLevel.addTab(view, "QSqlRelationalTableModel");
    }

    private static void makeRegularTable(QTabWidget topLevel, QSqlDatabase db)  {
        QSqlTableModel model = new QSqlTableModel(null, db);
        model.setTable("person");
        model.select();
        QTableView view = new QTableView(topLevel);
        view.setModel(model);
        topLevel.addTab(view, "person");

        model = new QSqlTableModel(null, db);
        model.setTable("country");
        model.select();
        view = new QTableView(topLevel);
        view.setModel(model);
        topLevel.addTab(view, "country");
    }

    public static void main(String args[])  {
        QApplication.initialize(args);

        com.trolltech.qt.sql.QJdbc.initialize();

        List<String> items = new ArrayList<String>();
        for (Driver driver : Driver.values())
            items.add(driver.name());

        String selectedDriver = QInputDialog.getItem(null, "Select a driver", "Select a driver", items);
        int ordinal = -1;

        for (Driver driver : Driver.values()) {
            if (driver.name().equals(selectedDriver))
                ordinal = driver.ordinal();
        }

        if (ordinal < 0)
            return;

        try {
            System.err.println("Loading driver '" + driverNames[ordinal] + "'");
            Class.forName(driverNames[ordinal]).newInstance();
        } catch (Exception ex) { System.err.println(ex); return; }

        QSqlDatabase db = QSqlDatabase.addDatabase("QJDBC");
        System.err.println("Selecting database '" + dbNames[ordinal] + "'");
        db.setDatabaseName(dbNames[ordinal]);
        db.setUserName("qt");
        db.setPassword("qqqqqq");
        if (db.open()) {
            System.err.println("Connected!");
        } else {
            System.err.println("Connection Failed!");
            System.err.println(db.lastError().text());
            return;
        }

        QTabWidget topLevel = new QTabWidget();
        topLevel.setWindowTitle("Tables");

        makeRelationalTable(topLevel, db);
        makeRegularTable(topLevel, db);

        topLevel.show();

        QApplication.exec();

        db.close();
    }
}

