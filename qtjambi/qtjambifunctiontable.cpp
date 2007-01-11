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

#include "qtjambifunctiontable.h"
#include "qtjambi_cache.h"

#include <memory.h>

QtJambiFunctionTable::QtJambiFunctionTable(const QString &className,
                                         int size)
    : m_class_name(className),
      m_method_count(size),
      m_reference_count(1)
{
    m_method_ids = new jmethodID[size];

    for (int i=0; i<size; ++i)
        m_method_ids[i] = 0;
}

QtJambiFunctionTable::~QtJambiFunctionTable()
{
    removeFunctionTable(this);
    delete [] m_method_ids;
}

void QtJambiFunctionTable::ref()
{
    ++m_reference_count;
    Q_ASSERT(m_reference_count > 0);
}

void QtJambiFunctionTable::deref()
{
    --m_reference_count;
    Q_ASSERT(m_reference_count >= 0);
}
