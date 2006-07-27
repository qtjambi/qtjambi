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
};

#endif // JAVANAMETABLE_H
