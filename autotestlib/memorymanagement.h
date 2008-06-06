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

#ifndef MEMORYMANAGEMENT_H
#define MEMORYMANAGEMENT_H

#include <qtjambi_core.h>

class PolymorphicObjectType
{
public:
    PolymorphicObjectType();
    virtual ~PolymorphicObjectType();

    static PolymorphicObjectType *newInstance();
    static void deleteLastInstance();

private:
    static PolymorphicObjectType *m_lastInstance;
};

class QObjectType: public QObject
{
    Q_OBJECT
public:
    QObjectType();
    ~QObjectType();

    static QObjectType *newInstance();
    static void deleteLastInstance();

private:
    static QObjectType *m_lastInstance;
};

class NonPolymorphicObjectType
{
public:
    NonPolymorphicObjectType();
    ~NonPolymorphicObjectType();

    static NonPolymorphicObjectType *newInstance();
    static void deleteLastInstance();

private:
    static NonPolymorphicObjectType *m_lastInstance;
};

class ValueType
{
public:
    ValueType();
    ~ValueType();

    static ValueType *newInstance();
    static void deleteLastInstance();

private:
    static ValueType *m_lastInstance;

};

#define INVALIDATOR(T) class Invalidator##T \
{ \
public: \
    Invalidator##T() {} \
    void invalidateObject(T *meh) { \
        overrideMe(meh); \
    } \
    virtual void overrideMe(T *) = 0; \
} \

INVALIDATOR(PolymorphicObjectType);
INVALIDATOR(NonPolymorphicObjectType);
INVALIDATOR(ValueType);
INVALIDATOR(QObjectType);

#endif
