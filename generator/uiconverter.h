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

#ifndef UICONVERTER_H
#define UICONVERTER_H

#include "metajava.h"

#include <QtXml>

class UiConverter
{
public:
    enum SearchType {
        SignalSearch,
        SlotSearch
    };

    void setClasses(const MetaJavaClassList &classes) { m_java_classes = classes; }
    MetaJavaClassList classes() const { return m_java_classes; }

    void convertToJui(const QString &uiFile);

private:
    void traverse(QDomNode node, QDomDocument *doc);
    void fixUiNode(QDomElement node, QDomDocument *doc);
    void fixSetNode(QDomElement node, QDomDocument *doc);
    void fixEnumNode(QDomElement node, QDomDocument *doc);
    void fixConnectionNode(QDomElement node, QDomDocument *doc);
    void fixWidgetNode(QDomElement, QDomDocument *doc);
    QString translateEnumValue(const QString &enumValue);
    const MetaJavaFunction *findFunction(MetaJavaClass *javaClass, const QString &signature,
                                         SearchType type);


    MetaJavaClassList m_java_classes;
    QHash<QString, MetaJavaClass *> m_named_widgets;
};

#endif // UICONVERTER_H
