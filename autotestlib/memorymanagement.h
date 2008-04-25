#ifndef MEMORYMANAGEMENT_H
#define MEMORYMANAGEMENT_H

class PolymorphicObjectType
{
public:
    virtual ~PolymorphicObjectType();

    static PolymorphicObjectType *newInstance();
};

#endif
