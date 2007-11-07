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
  main.cpp

  A simple example of how to view a model in several views, and share a
  selection model.
*/

#include <QtGui>

//! [0] //! [1]
int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QSplitter *splitter = new QSplitter;

//! [2] //! [3]
    QDirModel *model = new QDirModel;
//! [0] //! [2] //! [4] //! [5]
    QTreeView *tree = new QTreeView(splitter);
//! [3] //! [6]
    tree->setModel(model);
//! [4] //! [6] //! [7]
    tree->setRootIndex(model->index(QDir::currentPath()));
//! [7]

    QListView *list = new QListView(splitter);
    list->setModel(model);
    list->setRootIndex(model->index(QDir::currentPath()));

//! [5]
    QItemSelectionModel *selection = new QItemSelectionModel(model);
    tree->setSelectionModel(selection);
    list->setSelectionModel(selection);

//! [8]
    splitter->setWindowTitle("Two views onto the same directory model");
    splitter->show();
    return app.exec();
}
//! [1] //! [8]
