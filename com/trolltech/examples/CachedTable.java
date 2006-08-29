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

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableView;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.sql.QSqlDatabase;
import com.trolltech.qt.sql.QSqlTableModel;

public class CachedTable extends QDialog {
    private QPushButton submitButton = null;
    private QPushButton revertButton = null;
    private QPushButton quitButton = null;
    private QSqlTableModel model = null;
    
    public static boolean canInstantiate() 
    {
        return QSqlDatabase.isDriverAvailable("QSQLITE");
    }
    
    public CachedTable(QWidget parent) 
    {
        super(parent);
        
        if (!SqlCommon.createConnection())
            throw new RuntimeException("Couldn't connect to SQLITE server");                   
            
        
        String tableName = "person";
        
        model = new QSqlTableModel(this);
        model.setTable(tableName);
        model.setEditStrategy(QSqlTableModel.OnManualSubmit);
        model.select();

        model.setHeaderData(0, Qt.Horizontal, tr("ID"));
        model.setHeaderData(1, Qt.Horizontal, tr("First name"));
        model.setHeaderData(2, Qt.Horizontal, tr("Last name"));

        QTableView view = new QTableView();
        view.setModel(model);
        
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        submitButton = new QPushButton(tr("Submit"));
        submitButton.setDefault(true);
        revertButton = new QPushButton(tr("&Revert"));
        quitButton = new QPushButton(tr("Quit"));

        submitButton.clicked.connect(this, "submit()");
        revertButton.clicked.connect(model, "revertAll()");
        quitButton.clicked.connect(this, "close()");

        QVBoxLayout buttonLayout = new QVBoxLayout();
        buttonLayout.addWidget(submitButton);
        buttonLayout.addWidget(revertButton);
        buttonLayout.addWidget(quitButton);
        buttonLayout.addStretch(1);

        QHBoxLayout mainLayout = new QHBoxLayout();
        mainLayout.addWidget(view);
        mainLayout.addLayout(buttonLayout);
        setLayout(mainLayout);

        setWindowTitle(tr("Cached Table"));
    }
    
    protected void submit() 
    {
        model.database().transaction();
        if (model.submitAll()) {
            model.database().commit();
        } else {
            model.database().rollback();
            QMessageBox.warning(this, tr("Cached Table"),
                                tr("The database reported an error: ") + model.lastError().text());
        }        
    }

    public static void main(String[] args) {
        QApplication.initialize(args);
        
        CachedTable table = new CachedTable(null);
        table.show();
        table.exec();       
    }

}
