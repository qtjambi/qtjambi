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

#ifndef QDOC_GENERATOR
#define QDOC_GENERATOR

#include "javagenerator.h"
#include "metajava.h"

class QDocGenerator: public JavaGenerator
{
public:
    QDocGenerator();

    virtual void generate();
    virtual QString subDirectoryForClass(const AbstractMetaClass *java_class) const;
    virtual QString fileNameForClass(const AbstractMetaClass *java_class) const;
    virtual void write(QTextStream &s, const AbstractMetaClass *java_class);
    virtual void write(QTextStream &s, const AbstractMetaEnumValue *java_enum_value);
    virtual void write(QTextStream &s, const AbstractMetaEnum *java_enum);
    virtual void writeOverload(QTextStream &s, const AbstractMetaFunction *java_function, int arg_count);
    virtual void write(QTextStream &s, const AbstractMetaFunction *java_function);
    virtual void write(QTextStream &s, const AbstractMetaField *java_field);
    virtual void writeSignal(QTextStream &s, const AbstractMetaFunction *java_function);
};

#endif // QDOC_GENERATOR
