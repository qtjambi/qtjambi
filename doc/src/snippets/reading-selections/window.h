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

#ifndef WINDOW_H
#define WINDOW_H

#include <QItemSelection>
#include <QMainWindow>
#include <QAbstractItemModel>
#include <QWidget>
#include <QTableView>

class MainWindow : public QMainWindow
{
    Q_OBJECT
public:
    MainWindow(QWidget *parent = 0);

private slots:
    void fillSelection();
    void clearSelection();
    void selectAll();

private:
    QAbstractItemModel *model;
    QItemSelectionModel *selectionModel;
    QTableView *table;
};

#endif
