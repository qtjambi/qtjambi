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

#ifndef QTJAMBIFUNCTIONTABLE_H
#define QTJAMBIFUNCTIONTABLE_H

#include "qtjambi_global.h"

#include <QtCore/QObject>

class QTJAMBI_EXPORT QtJambiFunctionTable
{
public:
    QtJambiFunctionTable(const QString &className, int size);
    ~QtJambiFunctionTable();

    inline int methodCount() const { return m_method_count; }

    inline QString className() const { return m_class_name; }

    inline jmethodID method(int pos) const;
    inline void setMethod(int pos, jmethodID methodId);

    void deref();
    void ref();

private:
    QString m_class_name;

    int m_method_count;
    jmethodID *m_method_ids;

    int m_reference_count;
};


QTJAMBI_EXPORT jmethodID QtJambiFunctionTable::method(int pos) const
{
    Q_ASSERT(pos >= 0);
    Q_ASSERT(pos < m_method_count);
    return m_method_ids[pos];
}


QTJAMBI_EXPORT void QtJambiFunctionTable::setMethod(int pos, jmethodID id)
{
    Q_ASSERT(pos >= 0);
    Q_ASSERT(pos < m_method_count);
    m_method_ids[pos] = id;
}


#endif // QTJAMBIFUNCTIONTABLE_H
