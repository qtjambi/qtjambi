#include "memorymanagement.h"

PolymorphicObjectType::~PolymorphicObjectType() {}

PolymorphicObjectType *PolymorphicObjectType::newInstance()
{
    return new PolymorphicObjectType;
}
