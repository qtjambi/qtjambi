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

#ifndef JAVANAMETABLE_H
#define JAVANAMETABLE_H

#include <QDomDocument>
#include <QMap>
#include <QSet>

class JavaNameTable
{
public:
    static JavaNameTable *instance()
    {
        static JavaNameTable me;
        return &me;
    }

    void loadXmlFile(const QString &fileName);

    QString javaSignature(const QString &cppSignature, const QString &className = QString());
    QString javaSignal(const QString &cppSignal, const QString &className = QString());
    QString javaEnum(const QString &cppEnum) const;
    QString cppEnum(const QString &javaEnum) const;
    QString javaFlagsName(const QString &cppEnumValue);

protected:
    struct XmlEntry {
        QDomDocument doc;
        QDomElement root;
        QDomElement signatures;
        QDomElement modifications;
    };

    JavaNameTable();

    QString javaSignature(const QString &cppSignature, const QString &className, const XmlEntry &entry);
    QString javaSignal(const QString &cppSignal, const QString &className, const XmlEntry &entry);
    QString toJavaSignature(const QString &cppSignature, const QString &className, const XmlEntry &entry);

    QList<XmlEntry> info;
    QMap<QString, QSet<QString> > hierarchy;

    // Map of qualified C++ enum value to qualified Java enum value.
    QHash<QString, QString> m_java_enum_values;
    QHash<QString, QString> m_cpp_enum_values;

    // Map of qualified C++ enum value to qualified Java flags name
    QHash<QString, QString> m_flags_names;
};

#endif // JAVANAMETABLE_H
