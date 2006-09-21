#ifndef THREADS_H
#define THREADS_H

#include <QtCore/QThread>

class Threads
{
public:
    static QObject *newQObject() {

        return new QObject();
    }
    static bool checkObjectThread(QObject *object)
    {
        return object->thread() == QThread::currentThread();
    }
};

#endif // THREADS_H
