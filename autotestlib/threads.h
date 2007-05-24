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

#ifndef THREADS_H
#define THREADS_H

#include <QtCore/QThread>

class Threads
{
public:
    static QObject *newQObject() {

        return new QObject();
    }
    static bool checkObjectThread(QObject *object)
    {
        return object->thread() == QThread::currentThread();
    }
};

#endif // THREADS_H
