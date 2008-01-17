/****************************************************************************
 **
 ** (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
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


/**
 * The QJdbc class is responsible for implementing a Qt database
 * plugin based on a JDBC database driver. In addition to making use
 * of the QJdbc database driver an application also needs a JDBC
 * driver.
 *
 * Below you find an example use of the QJdbc database driver in
 * combination with a mysql JDBC driver and opens the output in
 * two separate QTableView's.

 <pre>
    public static void main(String args[])
    {
        QApplication.initialize(args);

        QJdbc.initialize();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) { System.err.println(ex); return; }

        QSqlDatabase db = QSqlDatabase.addDatabase(QJdbc.ID);
        db.setDatabaseName("jdbc:mysql://myhostname/mydatabase");
        db.setUserName("myusername");
        db.setPassword("mypassword");
        if (db.open()) {
            System.out.println("Connected!");
        } else {
            System.out.println("Connection Failed!");
            System.out.println(db.lastError().text());
            return;
        }

        QSqlTableModel model = new QSqlTableModel(null, db);
        model.setTable("mytablename");
        if (!model.select()) {
            System.err.println(model.lastError().text());
        }
        QTableView view = new QTableView();
        view.setModel(model);
        view.show();

        QTableView view2 = new QTableView();
        view2.setModel(model);
        view2.show();

        QApplication.exec();

        db.close();
    }
 </pre>

 */
public class QJdbc {

    /** The id string that should be used in calls to
     * QSqlDatabase.addDatabase() when setting up a new database
     * connection.
     */
    public static final String ID = "QJDBC";

    /** Sets up the QJdbc Database driver plugin. This function must
     * be called before the QJdbc driver plugin can be used.
     */
    public static void initialize() {
        QJdbcSqlDriver.initialize();
    }
}