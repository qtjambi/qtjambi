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

#include "memorymanagement.h"

// PolymorphicObjectType
PolymorphicObjectType *PolymorphicObjectType::m_lastInstance = 0;
PolymorphicObjectType::PolymorphicObjectType()
{
    m_lastInstance = this;
}

PolymorphicObjectType::~PolymorphicObjectType()
{
    if (this == m_lastInstance)
        m_lastInstance = 0;
}

void PolymorphicObjectType::deleteLastInstance()
{
    delete m_lastInstance;
}

PolymorphicObjectType *PolymorphicObjectType::newInstance()
{
    return new PolymorphicObjectType;
}




// NonPolymorphicObjectType
NonPolymorphicObjectType *NonPolymorphicObjectType::m_lastInstance = 0;
NonPolymorphicObjectType::NonPolymorphicObjectType()
{
    m_lastInstance = this;
}

NonPolymorphicObjectType::~NonPolymorphicObjectType()
{
    if (this == m_lastInstance)
        m_lastInstance = 0;
}

void NonPolymorphicObjectType::deleteLastInstance()
{
    delete m_lastInstance;
}

NonPolymorphicObjectType *NonPolymorphicObjectType::newInstance()
{
    return new NonPolymorphicObjectType;
}




// ValueType
ValueType *ValueType::m_lastInstance = 0;
ValueType::ValueType()
{
    m_lastInstance = this;
}

ValueType::~ValueType()
{
    if (this == m_lastInstance)
        m_lastInstance = 0;
}

void ValueType::deleteLastInstance()
{
    delete m_lastInstance;
}

ValueType *ValueType::newInstance()
{
    return new ValueType;
}


// QObjectType
QObjectType *QObjectType::m_lastInstance = 0;
QObjectType::QObjectType()
{
    m_lastInstance = this;
}

QObjectType::~QObjectType()
{
    if (this == m_lastInstance)
        m_lastInstance = 0;
}

void QObjectType::deleteLastInstance()
{
    delete m_lastInstance;
}

QObjectType *QObjectType::newInstance()
{
    return new QObjectType;
}
