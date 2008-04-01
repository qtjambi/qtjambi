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

#ifndef METAJAVABUILDER_H
#define METAJAVABUILDER_H

#include "abstractmetabuilder.h"
#include "metajava.h"

class MetaJavaBuilder : public AbstractMetaBuilder
{

 protected:
    virtual MetaJavaClass *createMetaClass()
        {
            return new MetaJavaClass();
        };

    virtual MetaJavaEnum *createMetaEnum()
        {
            return new MetaJavaEnum();
        };

    virtual MetaJavaEnumValue *createMetaEnumValue()
        {
            return new MetaJavaEnumValue();
        };

    virtual MetaJavaField *createMetaField()
        {
            return new MetaJavaField();
        };

    virtual MetaJavaFunction *createMetaFunction()
        {
            return new MetaJavaFunction();
        };

    virtual MetaJavaArgument *createMetaArgument()
        {
            return new MetaJavaArgument();
        };

    virtual MetaJavaType *createMetaType()
        {
            return new MetaJavaType();
        };

};

#endif // METAJAVABUILDER_H
