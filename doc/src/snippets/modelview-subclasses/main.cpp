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

    An example of a main window application that used a subclassed model
    and view to display data from sound files.
*/

#include <QApplication>

#include "model.h"
#include "view.h"
#include "window.h"

/*!
    The main function for the linear model example. This creates and
    populates a model with long integers then displays the contents of the
    model using a QListView widget.
*/

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    MainWindow *window = new MainWindow;

    window->show();
    return app.exec();
}
