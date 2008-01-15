#ifndef QTJAMBICONCURRENT_H
#define QTJAMBICONCURRENT_H

#ifndef QT_NO_CONCURRENT

#include <QVariant>
#include <qfuture.h>
#include <qfuturewatcher.h>

typedef QFutureWatcher<void> QtJambiVoidFutureWatcher;
typedef QFuture<void> QtJambiVoidFuture;
typedef QFuture<JObjectWrapper> QtJambiFuture;
typedef QFutureWatcher<JObjectWrapper> QtJambiFutureWatcher;

#endif // QT_NO_CONCURRENT

#endif

