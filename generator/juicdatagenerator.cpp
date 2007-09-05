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

#include "juicdatagenerator.h"

#include "reporthandler.h"

#include <QDebug>
#include <QDomDocument>
#include <QFile>

static QString create_cpp_signature(MetaJavaFunction *f);
static QString create_cpp_signature(MetaJavaType *f);

static QString create_java_signature(MetaJavaFunction *f);
static QString create_java_signature(MetaJavaType *f);

void JuicDataGenerator::generate()
{
    ReportHandler::setContext("Juic Data Generator");

    QFile f(m_file_name);
    if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        ReportHandler::warning("failed to open " + m_file_name);
        return;
    }

    QDomDocument doc;
    QDomElement root = doc.createElement("JUIC");

    QDomElement signatures = doc.createElement("signatures");
    generateSignatures(&doc, &signatures);

    QDomElement hierarchy = doc.createElement("hierarchy");
    generateHierarchy(&doc, &hierarchy);

    QDomElement mods = doc.createElement("modifications");
    generateModifications(&doc, &mods);

    QDomElement enums = doc.createElement("enumerators");
    generateEnumerators(&doc, &enums);

    doc.appendChild(root);
    root.appendChild(signatures);
    root.appendChild(hierarchy);
    root.appendChild(mods);
    root.appendChild(enums);

    f.write(doc.toByteArray(4));
}


void JuicDataGenerator::generateModifications(QDomDocument *doc_node, QDomElement *mods_node)
{
    foreach (AbstractMetaClass *c, m_classes) {
        AbstractMetaFunctionList functions = c->functions();
        foreach (AbstractMetaFunction *f, functions) {
            FunctionModificationList mods = f->modifications(c);

            if (mods.size()) {
                QDomElement mod_node = doc_node->createElement("modification");
                mod_node.setAttribute("class", c->typeEntry()->qualifiedCppName());
                mod_node.setAttribute("function-name", f->originalName());
                mod_node.setAttribute("signature", create_cpp_signature(f));

                int skipped = 0;
                for (int i=0; i<mods.size(); ++i) {
                    const FunctionModification &m = mods.at(i);
                    if (m.isRemoveModifier())
                        mod_node.setAttribute("removed", "yes");
                    else if (m.isAccessModifier())
                        mod_node.setAttribute("access", m.accessModifierString());
                    else if (m.isRenameModifier())
                        mod_node.setAttribute("renamed", m.renamedTo());
                    else
                        ++skipped;
                }

                // Unless we skipped all, add the node.
                if (skipped != mods.size())
                    mods_node->appendChild(mod_node);
            }
        }
    }
}


void JuicDataGenerator::generateHierarchy(QDomDocument *doc_node, QDomElement *hierarchy_node)
{
    foreach (MetaJavaClass *c, m_classes) {
        QDomElement class_node = doc_node->createElement("class");
        class_node.setAttribute("name", c->typeEntry()->qualifiedCppName());
        if (c->baseClass())
            class_node.setAttribute("baseclass", c->baseClass()->typeEntry()->qualifiedCppName());
        hierarchy_node->appendChild(class_node);
    }
}


void JuicDataGenerator::generateSignatures(QDomDocument *doc_node, QDomElement *signatures_node)
{
    QHash<QString, QString> signatures;

    foreach (MetaJavaClass *c, m_classes) {
        MetaJavaFunctionList functions = c->functions();
        foreach (MetaJavaFunction *f, functions) {
            QString cpp = create_cpp_signature(f);
            QString java = create_java_signature(f);

            signatures[cpp] = java;
        }
    }

    for (QHash<QString, QString>::const_iterator it = signatures.constBegin();
         it != signatures.constEnd(); ++it) {
        QDomElement elm = doc_node->createElement("signature");
        elm.setAttribute("cpp-signature", it.key());
        elm.setAttribute("java-signature", it.value());
        signatures_node->appendChild(elm);
    }
}

void JuicDataGenerator::generateEnumerators(QDomDocument *doc_node, QDomElement *enums_node)
{
    QMap<QString, QString> table;

    foreach (MetaJavaClass *c, m_classes) {
        foreach (MetaJavaEnum *e, c->enums()) {

            const EnumTypeEntry *et = e->typeEntry();

            QDomElement enum_node = doc_node->createElement("enumerator");
            enum_node.setAttribute("cpp-name", et->qualifiedCppName());
            enum_node.setAttribute("java-name", et->qualifiedJavaName());
            if (et->flags())
                enum_node.setAttribute("java-flags-name", et->flags()->qualifiedJavaName());

            foreach (MetaJavaEnumValue *ev, e->values()) {
                QDomElement value_node = doc_node->createElement("enum-value");
                value_node.setAttribute("cpp-value", QString("%1::%2").arg(et->qualifier())
                                                                      .arg(ev->name()));
                value_node.setAttribute("java-value", QString("%1.%2.%3.%4").arg(et->javaPackage())
                                                                            .arg(et->qualifier())
                                                                            .arg(et->javaName())
                                                                            .arg(ev->name()));
                enum_node.appendChild(value_node);
            }
            enums_node->appendChild(enum_node);
        }
    }
}


/*!
 * Creates a normalized c++ signature for the type \a t
 */
QString create_cpp_signature(MetaJavaType *t)
{
    QString s;

    s += t->typeEntry()->qualifiedCppName();

    // templates
    QList<MetaJavaType *> insts = t->instantiations();
    if (insts.size()) {
        s += '<';
        for (int i=0; i<insts.size(); ++i) {
            if (i != 0)
                s += ",";
            s += create_cpp_signature(insts.at(i));
        }
        if (s.at(s.length() - 1) == '>')
            s += ' ';
        s += '>';
    }

    // Arrays
    if (t->arrayElementCount()) {
        s += '[';
        s += create_cpp_signature(t->arrayElementType());
        s += ']';
    }

    if (t->isReference() && !t->isConstant())
        s += "&";

    if (t->indirections())
        s += QString(t->indirections(), '*');

    return s;
}


/*!
 * Creates a normalized c++ signature from the function \a f
 */
QString create_cpp_signature(MetaJavaFunction *f)
{
    QString s;
    MetaJavaArgumentList args = f->arguments();
    for (int i=0; i<args.size(); ++i) {
        if (i != 0)
            s += ",";
        s += create_cpp_signature(args.at(i)->type());
    }
    return s;
}


/*!
 * Creates a java signature from the the type \a t
 */
QString create_java_signature(MetaJavaType *t)
{
    QString s;

    switch (t->typeUsagePattern()) {
    case MetaJavaType::ArrayPattern:
    case MetaJavaType::ContainerPattern:
    case MetaJavaType::ObjectPattern:
    case MetaJavaType::PrimitivePattern:
    case MetaJavaType::QObjectPattern:
    case MetaJavaType::ValuePattern:
        s = t->fullName();
        break;
    case MetaJavaType::FlagsPattern:
    case MetaJavaType::EnumPattern:
        s = "int";
        break;
    case MetaJavaType::StringPattern:
        s = "String";
        break;
    case MetaJavaType::CharPattern:
        s = "char";
        break;
    case MetaJavaType::NativePointerPattern:
        s = "com.trolltech.qt.QNativePointer";
        break;
    case MetaJavaType::VariantPattern:
        s = "java.lang.Object";
        break;
    case MetaJavaType::ThreadPattern:
        s = "java.lang.Thread";
        break;
    default:
        ReportHandler::warning(QString("unhandled usage pattern %1").arg(t->typeUsagePattern()));
        break;
    };

    if (t->isContainer()) {
        s += '<';
        QList<MetaJavaType *> insts = t->instantiations();
        for (int i=0; i<insts.size(); ++i) {
            if (i != 0)
                s += ",";
            s += create_java_signature(insts.at(i));
        }
        if (s.at(s.length() - 1) == '>')
            s += ' ';
        s += '>';
    }

    if (t->isArray()) {
        s += '[';
        s += create_java_signature(t->arrayElementType());
        s += ']';
    }

    return s;
}


/*!
 * Creates a java signature from the function \a f
 */
QString create_java_signature(MetaJavaFunction *f)
{
    QString s;
    MetaJavaArgumentList args = f->arguments();
    for (int i=0; i<args.size(); ++i) {
        if (i != 0)
            s += ",";
        s += create_java_signature(args.at(i)->type());
    }
    return s;
}
