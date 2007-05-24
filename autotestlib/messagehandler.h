/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <qstring.h>

class MessageHandler
{
public:
    static void sendDebug(const QString &str) { qDebug(str.toLocal8Bit()); }
    static void sendWarning(const QString &str) { qWarning(str.toLocal8Bit()); }
    static void sendCritical(const QString &str) { qCritical(str.toLocal8Bit()); }
    static void sendFatal(const QString &str) { qFatal(str.toLocal8Bit()); }
};

#endif // MESSAGEHANDLER_H
