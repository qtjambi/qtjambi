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

#include "cppwriteincludes.h"
#include "driver.h"
#include "ui4.h"
#include "uic.h"
#include "databaseinfo.h"

#include <QTextStream>

namespace CPP {

struct ClassInfoEntry
{
    const char *klass;
    const char *module;
    const char *header;
};

static ClassInfoEntry qclass_lib_map[] = {
#define QT_CLASS_LIB(klass, module, header) { #klass, #module, #header },
#include "qclass_lib_map.h"
#undef QT_CLASS_LIB
    { 0, 0, 0 }
};

WriteIncludes::WriteIncludes(Uic *uic)
    : driver(uic->driver()), output(uic->output()), option(uic->option())
{
    this->uic = uic;

    ClassInfoEntry *it = &qclass_lib_map[0];
    while (it->klass != 0) {
        m_classToHeader.insert(QLatin1String(it->klass), QLatin1String(it->module) + QLatin1String("/") + QLatin1String(it->klass));
        m_oldHeaderToNewHeader.insert(QLatin1String(it->header), QLatin1String(it->module) + QLatin1String("/") + QLatin1String(it->klass));
        ++it;
    }
}

void WriteIncludes::acceptUI(DomUI *node)
{
    m_includes.clear();
    m_customWidgets.clear();

    if (node->elementIncludes())
        acceptIncludes(node->elementIncludes());

    if (node->elementCustomWidgets())
        TreeWalker::acceptCustomWidgets(node->elementCustomWidgets());

    add(QLatin1String("QApplication"));
    add(QLatin1String("QVariant"));
    add(QLatin1String("QAction"));

    add(QLatin1String("QButtonGroup")); // ### only if it is really necessary

    if (uic->hasExternalPixmap() && uic->pixmapFunction() == QLatin1String("qPixmapFromMimeSource"))
        add(QLatin1String("Q3Mimefactory"));

    if (uic->databaseInfo()->connections().size()) {
        add(QLatin1String("QSqlDatabase"));
        add(QLatin1String("Q3SqlCursor"));
        add(QLatin1String("QSqlRecord"));
        add(QLatin1String("Q3SqlForm"));
    }

    TreeWalker::acceptUI(node);

    QMapIterator<QString, bool> it(m_includes);
    while (it.hasNext()) {
        it.next();

        QString header = m_oldHeaderToNewHeader.value(it.key(), it.key());
        if (header.trimmed().isEmpty())
            continue;

        if (it.value())
            output << "#include <" << header << ">\n";
        else
            output << "#include \"" << header << "\"\n";
    }
    output << "\n";
}

void WriteIncludes::acceptWidget(DomWidget *node)
{
    add(node->attributeClass());
    TreeWalker::acceptWidget(node);
}

void WriteIncludes::acceptLayout(DomLayout *node)
{
    add(node->attributeClass());
    TreeWalker::acceptLayout(node);
}

void WriteIncludes::acceptSpacer(DomSpacer *node)
{
    add(QLatin1String("QSpacerItem"));
    TreeWalker::acceptSpacer(node);
}

void WriteIncludes::add(const QString &className)
{
    if (className.isEmpty())
        return;

    QString header = m_classToHeader.value(className, className.toLower() + QLatin1String(".h"));

    if (className == QLatin1String("Line")) { // ### hmm, deprecate me!
        add(QLatin1String("QFrame"));
    } else if (!m_includes.contains(header) && !m_customWidgets.contains(className)) {
        m_includes.insert(header, true);
    }

    if (uic->customWidgetsInfo()->extends(className, QLatin1String("Q3ListView"))
            || uic->customWidgetsInfo()->extends(className, QLatin1String("Q3Table"))) {
        add(QLatin1String("Q3Header"));
    }
}

void WriteIncludes::acceptCustomWidget(DomCustomWidget *node)
{
    if (node->elementClass().isEmpty())
        return;

    m_customWidgets.insert(node->elementClass(), true);

    bool global = true;
    if (node->elementHeader() && node->elementHeader()->text().size()) {
        global = node->elementHeader()->attributeLocation().toLower() == QLatin1String("global");
        m_includes.insert(node->elementHeader()->text(), global);
    } else {
        add(node->elementClass());
    }

}

void WriteIncludes::acceptCustomWidgets(DomCustomWidgets *node)
{
    Q_UNUSED(node);
}

void WriteIncludes::acceptIncludes(DomIncludes *node)
{
    TreeWalker::acceptIncludes(node);
}

void WriteIncludes::acceptInclude(DomInclude *node)
{
    bool global = true;
    if (node->hasAttributeLocation())
        global = node->attributeLocation() == QLatin1String("global");
    m_includes.insert(node->text(), global);
}

} // namespace CPP
