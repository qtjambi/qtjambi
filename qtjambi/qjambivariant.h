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


class QJambiVariant: private QVariant
{
   
 public:
    static void setHandler(const Handler *_handler) {
        handler = _handler;
    }

    static const Handler *getHandler() {
        return handler;
    }
};

#endif // QJAMBIVARIANT_H
