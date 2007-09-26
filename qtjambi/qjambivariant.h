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

#ifndef QJAMBIVARIANT_H
#define QJAMBIVARIANT_H

#include <QVariant>
#include "qtjambi_core.h"

class QTJAMBI_EXPORT QJambiVariant: private QVariant
{
   
 public:
    static const uint JOBJECTWRAPPER_TYPE;

    static const QVariant::Handler *getLastHandler()
        {
            return lastHandler;
        }

    static int QJambiVariant::qRegisterJambiVariant()
        {
            lastHandler = QJambiVariant::getHandler();
            setHandler(&handler);
            return 1;
        }
    
    static int QJambiVariant::qUnregisterJambiVariant()
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

#endif // QJAMBIVARIANT_H
