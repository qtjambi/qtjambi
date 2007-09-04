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

#ifndef CUSTOMTYPES_H
#define CUSTOMTYPES_H

#include "typesystem.h"


class QModelIndexTypeEntry : public CustomTypeEntry
{
public:
    QModelIndexTypeEntry() : CustomTypeEntry("QModelIndex")
    {
        setCodeGeneration(GenerateNothing);
    }

    virtual QString javaPackage() const { return "com.trolltech.qt.core"; }

    virtual bool isValue() const { return true; }

    virtual void generateCppJavaToQt(QTextStream &s,
                                     const AbstractMetaType *java_type,
                                     const QString &env_name,
                                     const QString &qt_name,
                                     const QString &java_name) const;

    virtual void generateCppQtToJava(QTextStream &s,
                                     const AbstractMetaType *java_type,
                                     const QString &env_name,
                                     const QString &qt_name,
                                     const QString &java_name) const;

};

#endif
