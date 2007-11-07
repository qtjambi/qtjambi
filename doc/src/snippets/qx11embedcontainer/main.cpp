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

#include <QtGui>
#include <QX11EmbedContainer>

//! [0]
int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    if (app.arguments().count() != 2) {
        qFatal("Error - expected executable path as argument");
        return 1;
    }

    QX11EmbedContainer container;
    container.show();

    QProcess process(&container);
    QString executable(app.arguments()[1]);
    QStringList arguments;
    arguments << QString::number(container.winId());
    process.start(executable, arguments);

    int status = app.exec();
    process.close();
    return status;
}
//! [0]
