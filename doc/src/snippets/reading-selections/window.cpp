/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of an example program for Qt.
** EDITIONS: NOLIMITS
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
  window.cpp

  A minimal subclass of QTableView with slots to allow the selection model
  to be monitored.
*/

#include <QAbstractItemModel>
#include <QItemSelection>
#include <QItemSelectionModel>
#include <QMenu>
#include <QMenuBar>
#include <QStatusBar>

#include "model.h"
#include "window.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setWindowTitle("Selected Items in a Table Model");

    model = new TableModel(8, 4, this);

    table = new QTableView(this);
    table->setModel(model);

    QMenu *actionMenu = new QMenu(tr("&Actions"), this);
    QAction *fillAction = actionMenu->addAction(tr("&Fill Selection"));
    QAction *clearAction = actionMenu->addAction(tr("&Clear Selection"));
    QAction *selectAllAction = actionMenu->addAction(tr("&Select All"));
    menuBar()->addMenu(actionMenu);

    connect(fillAction, SIGNAL(triggered()), this, SLOT(fillSelection()));
    connect(clearAction, SIGNAL(triggered()), this, SLOT(clearSelection()));
    connect(selectAllAction, SIGNAL(triggered()), this, SLOT(selectAll()));

    selectionModel = table->selectionModel();

    statusBar();
    setCentralWidget(table);
}

void MainWindow::fillSelection()
{
//! [0]
    QModelIndexList indexes = selectionModel->selectedIndexes();
    QModelIndex index;

    foreach(index, indexes) {
        QString text = QString("(%1,%2)").arg(index.row()).arg(index.column());
        model->setData(index, text);
    }
//! [0]
}

void MainWindow::clearSelection()
{
    QModelIndexList indexes = selectionModel->selectedIndexes();
    QModelIndex index;

    foreach(index, indexes)
        model->setData(index, "");
}

void MainWindow::selectAll()
{
//! [1]
    QModelIndex parent = QModelIndex();
//! [1] //! [2]
    QModelIndex topLeft = model->index(0, 0, parent);
    QModelIndex bottomRight = model->index(model->rowCount(parent)-1,
        model->columnCount(parent)-1, parent);
//! [2]

//! [3]
    QItemSelection selection(topLeft, bottomRight);
    selectionModel->select(selection, QItemSelectionModel::Select);
//! [3]
}
