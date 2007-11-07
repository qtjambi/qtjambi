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

#include <QMainWindow>
#include <QString>
#include <QWidget>

#include "model.h"
#include "view.h"

class MainWindow : public QMainWindow
{
    Q_OBJECT
public:
    MainWindow::MainWindow(QWidget *parent = 0);

public slots:
    void selectOpenFile();

private:
    void setupModelView();
    void openFile(const QString &fileName);

    LinearModel *model;
    LinearView *view;
};

#endif
