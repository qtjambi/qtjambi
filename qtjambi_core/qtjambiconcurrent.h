#ifndef QTJAMBICONCURRENT_H
#define QTJAMBICONCURRENT_H

#ifndef QT_NO_CONCURRENT

#include <qtjambi_core.h>

#include <QVariant>
#include <qfuture.h>
#include <qfuturewatcher.h>
#include <qfuturesynchronizer.h>

typedef QFutureWatcher<void> QtJambiVoidFutureWatcher;
typedef QFuture<void> QtJambiVoidFuture;
typedef QFutureSynchronizer<void> QtJambiVoidFutureSynchronizer;
typedef QFuture<JObjectWrapper> QtJambiFuture;
typedef QFutureWatcher<JObjectWrapper> QtJambiFutureWatcher;
typedef QFutureSynchronizer<JObjectWrapper> QtJambiFutureSynchronizer;
typedef QFutureIterator<JObjectWrapper> QtJambiFutureIterator;

#endif // QT_NO_CONCURRENT

#endif

