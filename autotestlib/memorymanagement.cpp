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

void PolymorphicObjectType::invalidateObject(PolymorphicObjectType *)
{
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

void NonPolymorphicObjectType::invalidateObject(NonPolymorphicObjectType *)
{
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

void ValueType::invalidateObject(ValueType *)
{
}
