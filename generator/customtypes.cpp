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

#include "customtypes.h"

#include "metajava.h"

#include <QtCore/QDebug>
#include <QtCore/QTextStream>


void QModelIndexTypeEntry::generateCppJavaToQt(QTextStream &s,
                                               const MetaJavaType *,
                                               const QString &env_name,
                                               const QString &qt_name,
                                               const QString &java_name) const
{
    s << "QModelIndex " << qt_name << " = qtjambi_to_QModelIndex(" << env_name << ", "
      << java_name << ")";
}


void QModelIndexTypeEntry::generateCppQtToJava(QTextStream &s,
                                               const MetaJavaType *,
                                               const QString &env_name,
                                               const QString &qt_name,
                                               const QString &java_name) const
{
    s << "jobject " << java_name << " = qtjambi_from_QModelIndex(" << env_name << ", "
      << qt_name << ")";
}
