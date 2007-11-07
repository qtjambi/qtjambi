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
#include <QStatusBar>

#include "model.h"
#include "window.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setWindowTitle("Selected items in a table model");

    model = new TableModel(8, 4, this);

    table = new QTableView(this);
    table->setModel(model);

    selectionModel = table->selectionModel();
    connect(selectionModel,
        SIGNAL(selectionChanged(const QItemSelection &, const QItemSelection &)),
        this, SLOT(updateSelection(const QItemSelection &, const QItemSelection &)));
    connect(selectionModel,
        SIGNAL(currentChanged(const QModelIndex &, const QModelIndex &)),
        this, SLOT(changeCurrent(const QModelIndex &, const QModelIndex &)));

    statusBar();
    setCentralWidget(table);
}

//! [0]
void MainWindow::updateSelection(const QItemSelection &selected,
    const QItemSelection &deselected)
{
    QModelIndex index;
    QModelIndexList items = selected.indexes();

    foreach (index, items) {
        QString text = QString("(%1,%2)").arg(index.row()).arg(index.column());
        model->setData(index, text);
//! [0] //! [1]
    }
//! [1]

//! [2]
    items = deselected.indexes();

    foreach (index, items)
        model->setData(index, "");
}
//! [2]

//! [3]
void MainWindow::changeCurrent(const QModelIndex &current,
    const QModelIndex &previous)
{
    statusBar()->showMessage(
        tr("Moved from (%1,%2) to (%3,%4)")
            .arg(previous.row()).arg(previous.column())
            .arg(current.row()).arg(current.column()));
}
//! [3]
