/****************************************************************************
**
** Copyright (C) 2006-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include <QApplication>
#include "embedwidget.h"

//! [0]
int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    if (app.arguments().count() != 2) {
        qFatal("Error - expected window id as argument");
        return 1;
    }

    QString windowId(app.arguments()[1]);
    EmbedWidget window;
    window.embedInto(windowId.toULong());
    window.show();

    return app.exec();
}
//! [0]
