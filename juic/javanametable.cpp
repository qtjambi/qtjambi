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

#include "javanametable.h"
#include <QFile>
#include <QMetaObject>
#include <QString>
#include <QtDebug>

JavaNameTable::JavaNameTable()
{
    loadXmlFile(QString::fromUtf8(":/trolltech/juic/juic.xml"));
}

void JavaNameTable::loadXmlFile(const QString &fileName)
{
    QFile f(fileName);
    if (! f.open(QFile::ReadOnly)) {
        fprintf(stderr, "juic: cannot open '%s'\n", qPrintable(fileName));
        return;
    }

    QString errorMessage;
    int line, column;
    XmlEntry current;
    if (! current.doc.setContent(&f, &errorMessage, &line, &column)) {
        fprintf(stderr, "juic: cannot read '%s'. %s at line %d column %d\n",
            qPrintable(fileName), qPrintable(errorMessage), line, column);
        f.close();
        return;
    }

    f.close();
    current.root = current.doc.firstChild().toElement();
    current.signatures = current.root.namedItem("signatures").toElement();
    current.modifications = current.root.namedItem("modifications").toElement();

    // build the hierarchy
    QDomElement h = current.root.namedItem("hierarchy").toElement();
    for (QDomElement n = h.firstChild().toElement(); !n.isNull(); n = n.nextSibling().toElement()) {
        if (n.tagName() != QLatin1String("class")
                || !n.hasAttribute("name")
                || !n.hasAttribute("baseclass"))
            continue;

        hierarchy[n.attribute("name")].insert(n.attribute("baseclass"));
    }

    info.append(current);

    QDomElement enums = current.root.namedItem("enumerators").toElement();
    for (QDomElement enum_node = enums.firstChild().toElement();
         !enum_node.isNull(); enum_node = enum_node.nextSibling().toElement()) {

        QString flags_name = enum_node.attribute("java-flags-name");

        for (QDomElement enum_value = enum_node.firstChild().toElement();
             !enum_value.isNull(); enum_value = enum_value.nextSibling().toElement()) {

            QString cppValue = enum_value.attribute("cpp-value");
            QString javaValue = enum_value.attribute("java-value");

            m_java_enum_values[cppValue] = javaValue;
            m_cpp_enum_values[javaValue] = cppValue;

            if (!flags_name.isEmpty()) {
                m_flags_names[cppValue] = flags_name;
            }
        }

    }
}

QString JavaNameTable::javaSignature(const QString &cppSignature, const QString &className)
{
    foreach (XmlEntry e, info) {
        QString sig = javaSignature(cppSignature, className, e);
        if (! sig.isEmpty())
            return sig;
    }

    return QString();
}

QString JavaNameTable::javaSignal(const QString &cppSignal, const QString &className)
{
    foreach (XmlEntry e, info) {
        QString sig = javaSignal(cppSignal, className, e);
        if (! sig.isEmpty())
            return sig;
    }

    int lparen = cppSignal.indexOf('(');
    if (lparen == -1)
        return QString();

    return cppSignal.left(lparen);
}


QString JavaNameTable::javaEnum(const QString &cppEnum) const
{
    return m_java_enum_values[cppEnum];
}

QString JavaNameTable::cppEnum(const QString &javaEnum) const
{
    return m_cpp_enum_values[javaEnum];
}

QString JavaNameTable::javaFlagsName(const QString &cppEnumValue)
{
    return m_flags_names[cppEnumValue];
}


QString JavaNameTable::javaSignature(const QString &sig, const QString &className, const XmlEntry &entry)
{
    QString jsig = QString::fromUtf8(QMetaObject::normalizedSignature(sig.toUtf8()));

    int lparen = jsig.indexOf('(');
    int rparen = jsig.lastIndexOf(')');
    if (lparen == -1 || rparen == -1)
        return QString();

    QString name = jsig.left(lparen);

    jsig = toJavaSignature(jsig.mid(lparen + 1, rparen - (lparen + 1)), className, entry);

    jsig.prepend('(');
    jsig.prepend(name);
    jsig.append(')');

    return jsig;
}

QString JavaNameTable::javaSignal(const QString &cppSignal, const QString &className, const XmlEntry &entry)
{
    QString cppSignature = QString::fromUtf8 (QMetaObject::normalizedSignature(cppSignal.toUtf8()));

    int lparen = cppSignal.indexOf('(');
    if (lparen == -1)
        return QString();

    QString name = cppSignal.left(lparen);
    cppSignature = cppSignature.mid(lparen + 1, cppSignature.lastIndexOf(')') - (lparen + 1));

    QLatin1String attrClass("class");
    QLatin1String attrSignature("signature");
    QLatin1String attrRenamed("renamed");
    QLatin1String attrFunctionName("function-name");

    for (QDomElement it = entry.modifications.firstChild().toElement(); !it.isNull(); it = it.nextSibling().toElement()) {
        if (it.tagName() != QLatin1String("modification")
                || !it.hasAttribute(attrFunctionName)
                || !it.hasAttribute(attrSignature)
                || !it.hasAttribute(attrRenamed)
                || it.attribute(attrFunctionName) != name
                || it.attribute(attrSignature) != cppSignature)
            continue;

        QString klass = it.attribute(attrClass);
        if (klass == className || hierarchy.value(className).contains(klass))
            return it.attribute(attrRenamed);
    }

    return QString();
}

QString JavaNameTable::toJavaSignature(const QString &cppSignature, const QString &/*className*/, const XmlEntry &entry)
{
    QLatin1String attrCppSignature("cpp-signature");
    QLatin1String attrJavaSignature("java-signature");

    for (QDomElement it = entry.signatures.firstChild().toElement(); !it.isNull(); it = it.nextSibling().toElement()) {
        if (it.tagName() != QLatin1String("signature")
                || !it.hasAttribute(attrCppSignature)
                || !it.hasAttribute(attrJavaSignature)
                || it.attribute(attrCppSignature) != cppSignature)
            continue;

        return it.attribute(attrJavaSignature);
    }

    return cppSignature;
}

