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

    void setClasses(const AbstractMetaClassList &classes) { m_java_classes = classes; }
    AbstractMetaClassList classes() const { return m_java_classes; }

    void convertToJui(const QString &uiFile, const QString &customWidgetFiles);

private:
    typedef QPair<QString, AbstractMetaClass *> CustomWidget;

    void traverse(QDomNode node, QDomDocument *doc);
    void fixUiNode(QDomElement node, QDomDocument *doc);
    void fixSetNode(QDomElement node, QDomDocument *doc);
    void fixEnumNode(QDomElement node, QDomDocument *doc);
    void fixConnectionNode(QDomElement node, QDomDocument *doc);
    void fixWidgetNode(QDomElement, QDomDocument *doc);
    void fixCustomWidgetNode(QDomElement, QDomDocument *doc);
    
    void traverseCustomWidgets(const QString &customWidgetFiles);
    void traverseCustomWidgetFile(const QString &customWidgetFile);
    QString translateEnumValue(const QString &enumValue);
    const AbstractMetaFunction *findFunction(AbstractMetaClass *javaClass, const QString &signature,
                                         SearchType type);

    AbstractMetaClassList m_java_classes;
    QHash<QString, AbstractMetaClass *> m_named_widgets;
    QMultiMap<QString, CustomWidget> m_custom_widgets;
};

#endif // UICONVERTER_H
