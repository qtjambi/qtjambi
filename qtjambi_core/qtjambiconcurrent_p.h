/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

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
