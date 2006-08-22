#ifndef ABSTRACTCLASS_H
#define ABSTRACTCLASS_H

class AbstractClass
{
public:
    virtual void abstractFunction(const QString &something) = 0;    

    virtual AbstractClass *getAbstractClass() = 0;

    QString getS() { return s; }
    void setS(QString str) { s = str; }

private:
    QString s;
};

class NonAbstractSubclass: public AbstractClass
{
public:
    virtual void abstractFunction(const QString &something)
    {
        setS(something);
    }

    virtual AbstractClass *getAbstractClass() 
    {
        return 0;
    }
};

class AnotherNonAbstractSubclass: public AbstractClass
{
public:
    virtual AbstractClass *getAbstractClass()
    {
        return new NonAbstractSubclass;
    }

    void doVirtualCall(AbstractClass *cls, const QString &something) 
    {
        cls->abstractFunction(something);
    }

private:
    virtual void abstractFunction(const QString &something)
    {
        setS("Not " + something);
    }
};



#endif