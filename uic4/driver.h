/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ Trolltech AS. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef DRIVER_H
#define DRIVER_H

#include "option.h"
#include <QHash>
#include <QString>
#include <QStringList>
#include <QTextStream>

class QTextStream;
class DomUI;
class DomWidget;
class DomSpacer;
class DomLayout;
class DomLayoutItem;
class DomActionGroup;
class DomAction;

class Driver
{
public:
    Driver();
    virtual ~Driver();

    // tools
    bool printDependencies(const QString &fileName);
    bool uic(const QString &fileName, QTextStream *output = 0);
    bool uic(const QString &fileName, DomUI *ui, QTextStream *output = 0);

    // configuration
    inline QTextStream &output() const { return *m_output; }
    inline Option &option() { return m_option; }

    // initialization
    void reset();

    // error
    inline QStringList problems() { return m_problems; }
    inline void addProblem(const QString &problem) { m_problems.append(problem); }

    // utils
    static QString headerFileName(const QString &fileName);
    inline QString headerFileName() const
    { return headerFileName(m_option.outputFile.isEmpty() ? m_option.inputFile : m_option.outputFile); }

    static QString qtify(const QString &name);
    QString unique(const QString &instanceName=QString(),
                   const QString &className=QString());

    // symbol table
    QString findOrInsertWidget(DomWidget *ui_widget);
    QString findOrInsertSpacer(DomSpacer *ui_spacer);
    QString findOrInsertLayout(DomLayout *ui_layout);
    QString findOrInsertLayoutItem(DomLayoutItem *ui_layoutItem);
    QString findOrInsertName(const QString &name);
    QString findOrInsertActionGroup(DomActionGroup *ui_group);
    QString findOrInsertAction(DomAction *ui_action);

    inline bool hasName(const QString &name) const
    { return m_nameRepository.contains(name); }

    DomWidget *widgetByName(const QString &name) const;
    DomSpacer *spacerByName(const QString &name) const;
    DomLayout *layoutByName(const QString &name) const;
    DomActionGroup *actionGroupByName(const QString &name) const;
    DomAction *actionByName(const QString &name) const;

    // pixmap
    void insertPixmap(const QString &pixmap);
    bool containsPixmap(const QString &pixmap) const;

private:
    Option m_option;
    QTextStream m_stdout;
    QTextStream *m_output;

    QStringList m_problems;

    // symbol tables
    QHash<DomWidget*, QString> m_widgets;
    QHash<DomSpacer*, QString> m_spacers;
    QHash<DomLayout*, QString> m_layouts;
    QHash<DomActionGroup*, QString> m_actionGroups;
    QHash<DomAction*, QString> m_actions;
    QHash<QString, bool> m_nameRepository;
    QHash<QString, bool> m_pixmaps;
};

#endif // DRIVER_H
