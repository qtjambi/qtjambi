#include <QCache>
#include <QMutex>
#include <QThreadStorage>

#include "threads.h"

//! [0]
void MyThread::run()
//! [0] //! [1]
{
//! [1] //! [2]
}
//! [2]

#define Counter ReentrantCounter

//! [3]
class Counter
//! [3] //! [4]
{
public:
    Counter() { n = 0; }

    void increment() { ++n; }
    void decrement() { --n; }
    int value() const { return n; }

private:
    int n;
};
//! [4]

#undef Counter
#define Counter ThreadSafeCounter

//! [5]
class Counter
//! [5] //! [6]
{
public:
    Counter() { n = 0; }

    void increment() { QMutexLocker locker(&mutex); ++n; }
    void decrement() { QMutexLocker locker(&mutex); --n; }
    int value() const { QMutexLocker locker(&mutex); return n; }

private:
    mutable QMutex mutex;
    int n;
};
//! [6]

typedef int SomeClass;

//! [7]
QThreadStorage<QCache<QString, SomeClass> *> caches;

void cacheObject(const QString &key, SomeClass *object)
//! [7] //! [8]
{
    if (!caches.hasLocalData())
        caches.setLocalData(new QCache<QString, SomeClass>);

    caches.localData()->insert(key, object);
}

void removeFromCache(const QString &key)
//! [8] //! [9]
{
    if (!caches.hasLocalData())
        return;

    caches.localData()->remove(key);
}
//! [9]

int main()
{
    return 0;
}
