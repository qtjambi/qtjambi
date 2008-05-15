#ifndef MEMORYMANAGEMENT_H
#define MEMORYMANAGEMENT_H

class PolymorphicObjectType
{
public:
    PolymorphicObjectType();
    virtual ~PolymorphicObjectType();

    static PolymorphicObjectType *newInstance();
    static void deleteLastInstance();

    static void invalidateObject(PolymorphicObjectType *t);


private:
    static PolymorphicObjectType *m_lastInstance;
};

#endif
