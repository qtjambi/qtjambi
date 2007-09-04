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

#include "uiconverter.h"
#include "metajava.h"

#include "reporthandler.h"

#include <QtCore/QFileInfo>
#include <QtXml/QDomDocument>

void UiConverter::convertToJui(const QString &uiFile)
{
    ReportHandler::setContext(QLatin1String("UiConverter to .jui"));

    QFileInfo fileInfo(uiFile);

    if (!fileInfo.exists()) {
        ReportHandler::warning(QString::fromLatin1("Ui File %1 doesn't exist...\n").arg(uiFile));
        return;
    }

    if (fileInfo.suffix() != QLatin1String("ui")) {
        ReportHandler::warning(QString::fromLatin1("File doesn't have .ui extension: %1")
                               .arg(uiFile));
        return;
    }

    QString juiFile = fileInfo.absolutePath() + QLatin1Char('/') + fileInfo.baseName()
                      + QLatin1String(".jui");

    QFile inputFile(uiFile);

    if (!inputFile.open(QFile::ReadOnly | QFile::Text)) {
        ReportHandler::warning(QString::fromLatin1("Could not open '%1' for reading").arg(uiFile));
        return;
    }

    QDomDocument dom;
    QString error;
    if (!dom.setContent(&inputFile, false, &error)) {
        ReportHandler::warning(QString::fromLatin1("Xml loading %1 failed: %2")
                               .arg(uiFile).arg(error));
        inputFile.close();
        return;
    }
    inputFile.close();

    traverse(dom.documentElement(), &dom);

    QFile outputFile(juiFile);
    if (!outputFile.open(QFile::WriteOnly | QFile::Text)) {
        ReportHandler::warning(QString::fromLatin1("Could not open '%1' for writing")
                               .arg(juiFile));
        return;
    }

    outputFile.write(dom.toByteArray());
    outputFile.close();
}

void UiConverter::traverse(QDomNode node, QDomDocument *doc)
{
    if (node.isNull())
        return;

    QDomElement element = node.toElement();
    if (!element.isNull()) {
        if (element.nodeName() == QLatin1String("ui"))
            fixUiNode(element, doc);
        else if (element.nodeName() == QLatin1String("set"))
            fixSetNode(element, doc);
        else if (element.nodeName() == QLatin1String("enum"))
            fixEnumNode(element, doc);
        else if (element.nodeName() == QLatin1String("connection"))
            fixConnectionNode(element, doc);
        else if (element.nodeName() == QLatin1String("widget"))
            fixWidgetNode(element, doc);
    }

    QDomNodeList list = node.childNodes();
    for (int i=0; i<list.size(); ++i)
        traverse(list.at(i), doc);
}


void UiConverter::fixUiNode(QDomElement el, QDomDocument *)
{
    el.setAttribute("language", "jambi");
}


void UiConverter::fixSetNode(QDomElement el, QDomDocument *)
{
   QStringList cppSet = el.firstChild().nodeValue().split(QLatin1Char('|'));

    QStringList javaSet;
    for (int i=0; i<cppSet.size(); ++i)
        javaSet << translateEnumValue(cppSet.at(i));

    el.firstChild().setNodeValue(javaSet.join(QLatin1String("|")));
}


void UiConverter::fixEnumNode(QDomElement el, QDomDocument *)
{
    QDomNode valueNode = el.firstChild();
    if (valueNode.isNull()) {
        ReportHandler::warning(QString::fromLatin1("Bad enum value at '%1'").arg(el.nodeValue()));
        return;
    }

    QString cppEnumValue = valueNode.nodeValue();
    QString javaEnumValue = translateEnumValue(cppEnumValue);
    valueNode.setNodeValue(javaEnumValue);
}


void UiConverter::fixConnectionNode(QDomElement el, QDomDocument *)
{
    QString senderName = el.namedItem("sender").firstChild().nodeValue();
    AbstractMetaClass *senderClass = m_named_widgets[senderName];
    if (!senderClass) {
        ReportHandler::warning(QString::fromLatin1("sender unknown '%1'").arg(senderName));
        return;
    }
    QDomNode signalSignatureNode = el.namedItem("signal").toElement().firstChild();
    QString signalSignature = signalSignatureNode.nodeValue();
    const AbstractMetaFunction *signalFunction = findFunction(senderClass,
                                                          signalSignature,
                                                          SignalSearch);
    if (!signalFunction) {
        ReportHandler::warning(QString::fromLatin1("Signal not found '%1' in '%2'")
                               .arg(signalSignature).arg(senderClass->qualifiedCppName()));
        return;
    }
    signalSignatureNode.setNodeValue(signalFunction->modifiedName());

    QString receiverName = el.namedItem("receiver").firstChild().nodeValue();
    AbstractMetaClass *receiverClass = m_named_widgets[receiverName];
    if (!receiverClass) {
        ReportHandler::warning(QString::fromLatin1("receiver unknown '%1'").arg(receiverName));
        return;
    }

    QDomNode slotSignatureNode = el.namedItem("slot").firstChild();
    QString slotSignature = slotSignatureNode.nodeValue();
    const AbstractMetaFunction *slotFunction = findFunction(receiverClass, slotSignature, SlotSearch);
    if (!signalFunction) {
        ReportHandler::warning(QString::fromLatin1("Slot not found '%1' in '%2'")
                               .arg(slotSignature).arg(receiverClass->qualifiedCppName()));
        return;
    }

    slotSignatureNode.setNodeValue(slotFunction->javaSignature(true));
}


void UiConverter::fixWidgetNode(QDomElement el, QDomDocument *)
{
    QString className = el.attribute(QLatin1String("class"));
    AbstractMetaClass *javaClass = m_java_classes.findClass(className);
    if (!javaClass) {
        ReportHandler::warning(QString::fromLatin1("Class '%1' is unknown").arg(className));
        return;
    }

    if (javaClass->package() != QLatin1String("com.trolltech.qt.gui"))
        el.setAttribute(QLatin1String("class"), javaClass->fullName());

    m_named_widgets.insert(el.attribute(QLatin1String("name")), javaClass);
}


QString UiConverter::translateEnumValue(const QString &cppEnumValue) {
    if (!cppEnumValue.contains(QLatin1String("::"))) {
        ReportHandler::warning(QString::fromLatin1("Expected '::' in enum value '%1'")
                               .arg(cppEnumValue));
        return QString();
    }

    QStringList names = cppEnumValue.split(QLatin1String("::"));
    AbstractMetaClass *javaClass = m_java_classes.findClass(names.at(0));

    if (!javaClass) {
        ReportHandler::warning(QString::fromLatin1("Class '%1' is unknown").arg(names.at(0)));
        return QString();
    }

    AbstractMetaEnum *javaEnum = javaClass->findEnumForValue(names.at(1));
    if (!javaEnum) {
        ReportHandler::warning(QString::fromLatin1("Enum value '%1' was not found in '%2'")
                               .arg(names.at(1)).arg(names.at(0)));
        return QString();
    }

    AbstractMetaEnumValueList enumValues = javaEnum->values();
    AbstractMetaEnumValue *enumValue = enumValues.find(names.at(1));
    int value = enumValue->value();

    if (javaEnum->typeEntry()->isEnumValueRejected(enumValue->name())) {
        for (int i=0; i<enumValues.size(); ++i) {
            AbstractMetaEnumValue *ev = enumValues.at(i);
            if (ev->value() == value) {
                enumValue = ev;
                break;
            }
        }
    }

    return javaEnum->fullName() + QLatin1String(".") + enumValue->name();
}

const AbstractMetaFunction *UiConverter::findFunction(AbstractMetaClass *javaClass,
                                                  const QString &signature,
                                                  SearchType type)
{
    AbstractMetaFunctionList senderFunctions = javaClass->functions();
    foreach (const AbstractMetaFunction *f, senderFunctions) {
        if (type == SignalSearch && !f->isSignal())
            continue;

        QString fsig = f->minimalSignature();


        int pos = 0;
        while (pos < signature.length()
               && fsig.constData()[pos] == signature.constData()[pos]) ++pos;

        if (pos == signature.length()
            || (type == SignalSearch
                && pos == signature.length() - 1
                && signature.constData()[pos] == QLatin1Char(')'))) {
            return f;
        }
    }

    return 0;
}
