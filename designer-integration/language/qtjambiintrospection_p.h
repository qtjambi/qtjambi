/****************************************************************************
**
** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef JAMBI_INTROSPECTION_H
#define JAMBI_INTROSPECTION_H

#include <private/abstractintrospection_p.h>

class QtJambiIntrospection: public QDesignerIntrospectionInterface
{
public:
    QtJambiIntrospection();
    virtual ~QtJambiIntrospection();

    virtual const QDesignerMetaObjectInterface* metaObject(const QObject *object) const;
};

#endif // JAMBI_INTROSPECTION_H
