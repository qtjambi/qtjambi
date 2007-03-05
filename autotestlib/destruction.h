#ifndef DESTRUCTION_H
#define DESTRUCTION_H

#include <QtCore/QObject>

class OrdinarySuperclass
{
public:
    OrdinarySuperclass() { }
    virtual ~OrdinarySuperclass() { }
};

class OrdinaryDestroyed: public OrdinarySuperclass
{
public:
    OrdinaryDestroyed()
    {
        // nanana
    }

    virtual ~OrdinaryDestroyed()
    {
        increaseDestroyedCount();
    }

    virtual OrdinaryDestroyed *virtualGetObjectJavaOwnership() { return new OrdinaryDestroyed(); }
    virtual OrdinaryDestroyed *virtualGetObjectCppOwnership() { return new OrdinaryDestroyed(); }
    virtual void virtualSetDefaultOwnership(OrdinaryDestroyed *) { }

    static void deleteFromCpp(OrdinaryDestroyed *destroyed)
    {
        delete destroyed;
    }

    static void deleteFromCppOther(OrdinarySuperclass *destroyed)
    {
        delete destroyed;
    }

    static void setDestroyedCount(int count) { m_destroyed = count; }
    static void increaseDestroyedCount() { m_destroyed++; }
    static int destroyedCount() { return m_destroyed; }

    static OrdinaryDestroyed *callGetObjectJavaOwnership(OrdinaryDestroyed *_this)
    {
        return _this->virtualGetObjectJavaOwnership();
    }

    static OrdinaryDestroyed *callGetObjectCppOwnership(OrdinaryDestroyed *_this)
    {
        return _this->virtualGetObjectCppOwnership();
    }

    static void callSetDefaultOwnership(OrdinaryDestroyed *_this, OrdinaryDestroyed *obj)
    {
        return _this->virtualSetDefaultOwnership(obj);
    }

    // Set in type system
    static OrdinaryDestroyed *getObjectJavaOwnership() { return new OrdinaryDestroyed(); }

    // Default ownership
    static OrdinaryDestroyed *getObjectSplitOwnership() { return new OrdinaryDestroyed(); }

    // Set in type system
    static OrdinaryDestroyed *getObjectCppOwnership() { return new OrdinaryDestroyed(); }
    static void setDefaultOwnership(OrdinaryDestroyed *) { }


private:
    static int m_destroyed;
};

class QObjectDestroyed: public QObject
{
public:
    QObjectDestroyed(QObject *parent = 0)  : QObject(parent) { }

    static void deleteFromCpp(QObjectDestroyed *destroyed)
    {
        delete destroyed;
    }

    static void deleteFromCppOther(QObject *destroyed)
    {
        delete destroyed;
    }
};

#endif
