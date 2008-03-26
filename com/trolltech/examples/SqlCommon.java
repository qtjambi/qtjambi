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

package com.trolltech.examples;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.sql.QSqlDatabase;
import com.trolltech.qt.sql.QSqlQuery;

//! [0]
class SqlCommon 
{
    static boolean createConnection()
    {
        QSqlDatabase db = QSqlDatabase.addDatabase("QSQLITE", "qt_sql_default_connection");
        db.setDatabaseName(":memory:");
        if (!db.open()) {
            QMessageBox.critical(null, QApplication.instance().tr("Cannot open database"),
                QApplication.instance().tr("Unable to establish a database connection.\n" +
                         "This example needs SQLite support. Please read " +
                         "the Qt SQL driver documentation for information how " +
                         "to build it.\n\n" +
                         "Click Cancel to exit."), 
                         new QMessageBox.StandardButtons(QMessageBox.StandardButton.Cancel,
                                                         QMessageBox.StandardButton.NoButton));
            return false;
        }

        QSqlQuery query = new QSqlQuery();
        query.exec("create table person (id int primary key, " +
                   "firstname varchar(20), lastname varchar(20))");
        query.exec("insert into person values(101, 'Danny', 'Young')");
        query.exec("insert into person values(102, 'Christine', 'Holand')");
        query.exec("insert into person values(103, 'Lars', 'Gordon')");
        query.exec("insert into person values(104, 'Roberto', 'Robitaille')");
        query.exec("insert into person values(105, 'Maria', 'Papadopoulos')");
        
        return true;
        
    }
}
//! [0]
