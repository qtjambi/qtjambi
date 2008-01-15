/****************************************************************************
**
** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the Qt Jambi / JDBC project on Trolltech Labs.
**
** Licensees holding valid Trolltech Technology Preview licenses may
** use this file in accordance with the Trolltech Technology Preview
** License Agreement provided with the Software.
**
** See http://www.trolltech.com/pricing.html or email
** sales@trolltech.com for information about the Qt Commercial License
** Agreements.
** Contact info@trolltech.com if any conditions of this licensing are
** not clear to you.
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

import com.trolltech.qt.gui.*;
import com.trolltech.qt.sql.*;

public class JavaTest
{
    public static void main(String args[])
    {
        QApplication.initialize(args);

        QJdbcSqlDriver.initialize();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) { System.err.println(ex); return; }

        QSqlDatabase db = QSqlDatabase.addDatabase("QJDBC");
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
}

