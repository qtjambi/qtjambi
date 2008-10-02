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

#ifndef JAVAWRITEINITIALIZATION_H
#define JAVAWRITEINITIALIZATION_H

#include "treewalker.h"
#include "option.h"

#include <QPair>
#include <QHash>
#include <QStack>
#include <QTextStream>

class Driver;
class Uic;

class DomBrush;

namespace Java {

struct WriteInitialization : public TreeWalker
{
    WriteInitialization(Uic *uic);

//
// widgets
//
    void acceptUI(DomUI *node);
    void acceptWidget(DomWidget *node);
    void acceptLayout(DomLayout *node);
    void acceptSpacer(DomSpacer *node);
    void acceptLayoutItem(DomLayoutItem *node);

//
// actions
//
    void acceptActionGroup(DomActionGroup *node);
    void acceptAction(DomAction *node);
    void acceptActionRef(DomActionRef *node);

//
// tab stops
//
    void acceptTabStops(DomTabStops *tabStops);

//
// custom widgets
//
    void acceptCustomWidgets(DomCustomWidgets *node);
    void acceptCustomWidget(DomCustomWidget *node);

//
// layout defaults/functions
//
    void acceptLayoutDefault(DomLayoutDefault *node);
    void acceptLayoutFunction(DomLayoutFunction *node);

//
// signal/slot connections
//
    void acceptConnection(DomConnection *connection);

//
// images
//
    void acceptImage(DomImage *image);

private:
    static QString domColor2QString(DomColor *c);
    static QString domBrush2QString(DomBrush *c);

    QString pixCall(DomProperty *prop) const;
    QString trCall(const QString &str, const QString &comment = QString()) const;
    QString trCall(DomString *str) const;

    void writeProperties(const QString &varName, const QString &className,
                         const QList<DomProperty*> &lst);
    void writeColorGroup(DomColorGroup *colorGroup, const QString &group, const QString &paletteName);
    void writeBrush(const DomBrush *brush, const QString &brushName);

//
// special initialization
//
    void initializeMenu(DomWidget *w, const QString &parentWidget);
    void initializeComboBox(DomWidget *w);
    void initializeListWidget(DomWidget *w);
    void initializeTreeWidget(DomWidget *w);
    void initializeTreeWidgetItems(const QString &className, const QString &varName, const QList<DomItem *> &items);
    void initializeTableWidget(DomWidget *w);

    DomWidget *findWidget(const QLatin1String &widgetClass);
    DomImage *findImage(const QString &name) const;

    bool isValidObject(const QString &name) const;

private:
    Uic *uic;
    Driver *driver;
    QTextStream &output;
    Option option;
    bool m_stdsetdef;

    struct Buddy
    {
        Buddy(const QString &oN, const QString &b)
            : objName(oN), buddy(b) {}
        QString objName;
        QString buddy;
    };

    QStack<DomWidget*> m_widgetChain;
    QStack<DomLayout*> m_layoutChain;
    QStack<DomActionGroup*> m_actionGroupChain;
    QList<Buddy> m_buddies;

    QHash<QString, QString> m_buttonGroups;
    QHash<QString, DomWidget*> m_registeredWidgets;
    QHash<QString, DomImage*> m_registeredImages;
    QHash<QString, DomAction*> m_registeredActions;

    // layout defaults
    int m_defaultMargin;
    int m_defaultSpacing;
    QString m_marginFunction;
    QString m_spacingFunction;

    QString m_generatedClass;

    QString m_delayedInitialization;
    QTextStream delayedOut;

    QString m_refreshInitialization;
    QTextStream refreshOut;

    QString m_delayedActionInitialization;
    QTextStream actionOut;
};

} // namespace Java

#endif // JAVAWRITEINITIALIZATION_H
