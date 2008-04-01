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

#ifndef QTJAMBIVARIANT_H
#define QTJAMBIVARIANT_H

//
//  W A R N I N G
//  -------------
//
// This file is not part of the Qt Jambi API.
// This header file may change from version to version without notice,
// or even be removed.
//
// We mean it.
//
//

#include <QVariant>
#include "qtjambi_core.h"

class QtJambiVariant: private QVariant
{

 public:
    static const QVariant::Handler *getLastHandler()
        {
            return lastHandler;
        }

    static int registerHandler()
        {
            lastHandler = QVariant::handler;
            setHandler(&handler);
            return 1;
        }

    static int unregisterHandler()
        {
            setHandler(lastHandler);
            lastHandler = 0;
            return 1;
        }

 private:
    static const QVariant::Handler handler;

    static void setHandler(const Handler *handler) {
        QVariant::handler = handler;
    }

    static const Handler *getHandler() {
        return QVariant::handler;
    }

    static const QVariant::Handler *lastHandler;

};

#endif // QTJAMBIVARIANT_H
