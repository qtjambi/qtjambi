#ifndef QTJAMBICONCURRENT_P_H
#define QTJAMBICONCURRENT_P_H

#ifndef QT_NO_CONCURRENT

#include <qtjambi_core.h>
#include <QList>
#include <QFutureWatcher>

class FutureSequenceCleanUp: public QFutureWatcher<void> {
    Q_OBJECT
public:
    FutureSequenceCleanUp(QList<JObjectWrapper> *sequence);
    ~FutureSequenceCleanUp();

private slots:
    void cleanUp();

private:
    QList<JObjectWrapper> *m_sequence;
};

#endif // QT_NO_CONCURRENT

#endif
