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

    virtual ~OrdinaryDestroyed() { }

    static void deleteFromCpp(OrdinaryDestroyed *destroyed)
    {
        delete destroyed;
    }

    static void deleteFromCppOther(OrdinarySuperclass *destroyed)
    {
        delete destroyed;
    }
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