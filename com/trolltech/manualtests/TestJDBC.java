package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.QVariant;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class TestJDBC {

    private enum Driver {
        PostgreSQL,
        Derby,
        MySQL
    }

    static private String driverNames[] = {
        "org.postgresql.Driver",
        "org.apache.derby.jdbc.EmbeddedDriver",
        "com.mysql.jdbc.Driver"
    };

    static private String dbNames[] = {
        "jdbc:postgresql://qfwfq.europe.nokia.com/main",
        "jdbc:derby:firstdb", // only usable locally, since no server is set up. Install derby and make the
                              // database on your local machine to run this test.
        "jdbc:mysql://qfwfq.europe.nokia.com/main"
    };

    static private String personTable[] = {
            "person",
            "PERSON",
            "person"
    };

    static private String countryTable[] = {
        "country",
        "COUNTRY",
        "country"
    };

    static private String userNames[] = {
            "qt",
            "", // login not required
            "qt"
    };

    static private String passwords[] = {
            "qqqqqq",
            "",
            "qqqqqq"
    };


    static {
        checkSize(driverNames, "driverNames");
        checkSize(dbNames, "dbNames");
        checkSize(personTable, "personTable");
        checkSize(countryTable, "countryTable");
        checkSize(userNames, "userNames");
        checkSize(passwords, "passwords");
    }

    private static void checkSize(Object array[], String name) {
        if (array.length < Driver.values().length) {
            String missingNames = "";
            for (int i=array.length; i<Driver.values().length; ++i) {
                if (missingNames.length() > 0)
                    missingNames += ", ";
                missingNames += Driver.values()[i].name();
            }

            throw new RuntimeException("Array '" + name + "' does not contain any entry for the following drivers: "
                    + missingNames);
        }
    }

    private static void makeRelationalTable(QTabWidget topLevel, QSqlDatabase db)  {
        QSqlRelationalTableModel model = new QSqlRelationalTableModel(null, db);
        model.setTable(personTable[ordinal]);
        model.setEditStrategy(QSqlTableModel.EditStrategy.OnFieldChange);

        int columnCount = model.columnCount();
        int columnIdx = -1;
        for (int i=0; i<columnCount; ++i) {
           String columnName = QVariant.toString(model.headerData(i, Qt.Orientation.Horizontal));
           if (columnName.toLowerCase().equals("countryid")) {
               columnIdx = i;
               break;
           }
        }

        if (columnIdx < 0)
            return;

        model.setRelation(columnIdx, new QSqlRelation(countryTable[ordinal], "id", "name"));
        if (!model.select()) {
            System.err.println(model.lastError().text());
            return;
        }

        QTableView view = new QTableView();
        view.setWindowTitle("QSqlRelationalTableModel");
        view.setItemDelegate(new QSqlRelationalDelegate(view));
        view.setModel(model);

        topLevel.addTab(view, "QSqlRelationalTableModel");
    }

    private static void makeRegularPersonTable(QTabWidget topLevel, QSqlDatabase db)  {
        QSqlTableModel model = new QSqlTableModel(null, db);
        model.setTable(personTable[ordinal]);
        if (!model.select()) {
            System.err.println(model.lastError().text());
            return ;
        }

        QTableView view = new QTableView(topLevel);
        view.setModel(model);
        topLevel.addTab(view, "person");
    }

    private static void makeRegularCountryTable(QTabWidget topLevel, QSqlDatabase db) {
        QSqlTableModel model = new QSqlTableModel(null, db);
        model.setTable(countryTable[ordinal]);
        if (!model.select()) {
            System.err.println(model.lastError().text());
            return;
        }
        QTableView view = new QTableView(topLevel);
        view.setModel(model);
        topLevel.addTab(view, "country");
    }

    private static int ordinal = -1;
    public static void main(String args[])  {
        QApplication.initialize(args);

        com.trolltech.qt.sql.QJdbc.initialize();

        List<String> items = new ArrayList<String>();
        for (Driver driver : Driver.values())
            items.add(driver.name());

        String selectedDriver = QInputDialog.getItem(null, "Select a driver", "Select a driver", items);

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
        if (userNames[ordinal].length() > 0) {
            db.setUserName(userNames[ordinal]);
            db.setPassword(passwords[ordinal]);
        }
        if (db.open()) {
            System.err.println("Connected!");
        } else {
            System.err.println("Connection Failed!");
            System.err.println(db.lastError().text());
            return;
        }

        QTabWidget topLevel = new QTabWidget();
        topLevel.setWindowTitle("Tables");

        makeRegularPersonTable(topLevel, db);
        makeRegularCountryTable(topLevel, db);
        makeRelationalTable(topLevel, db);

        topLevel.show();

        QApplication.exec();

        db.close();
    }
}

