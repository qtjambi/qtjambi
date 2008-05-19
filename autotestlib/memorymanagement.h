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

class NonPolymorphicObjectType
{
public:
    NonPolymorphicObjectType();
    ~NonPolymorphicObjectType();

    static NonPolymorphicObjectType *newInstance();
    static void deleteLastInstance();

    static void invalidateObject(NonPolymorphicObjectType *t);    

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

    static void invalidateObject(ValueType *t);


private:
    static ValueType *m_lastInstance;

};

#endif
