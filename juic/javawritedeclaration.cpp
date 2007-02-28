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

#include "javawritedeclaration.h"
#include "javawriteinitialization.h"
#include "driver.h"
#include "ui4.h"
#include "uic.h"
#include "databaseinfo.h"
#include "customwidgetsinfo.h"

#include <QTextStream>

#define JUIC_NO_EMBEDDED_ICON

namespace Java {

WriteDeclaration::WriteDeclaration(Uic *uic)
    : driver(uic->driver()), output(uic->output()), option(uic->option())
{
    this->uic = uic;
}

void WriteDeclaration::acceptUI(DomUI *node)
{
    QString qualifiedClassName = node->elementClass() + option.postfix;
    QString className = qualifiedClassName;

    QString varName = driver->findOrInsertWidget(node->elementWidget());
    QString widgetName = node->elementWidget()->attributeClass();

    QString package = driver->option().javaPackage;

    if (!package.isEmpty())
        output << "package " << package << ";\n\n";

    output << "import com.trolltech.qt.core.*;\n"
           << "import com.trolltech.qt.gui.*;\n"
           << "\n";

    output << "public class " << option.prefix << className << "\n"
           << "{\n";

    TreeWalker::acceptWidget(node->elementWidget());

    output << "\n"
           << option.indent << "public " << option.prefix << className << "() { super(); }\n"
            << "\n";

    WriteInitialization(uic).acceptUI(node);

    extern bool generate_java_main_function;
    if (generate_java_main_function) {
        QString uiName = option.prefix + className;
        output << "    public static void main(String args[]) {" << endl
               << "        QApplication.initialize(args);" << endl
               << "        " << uiName << " ui = new " << uiName << "();" << endl
               << "        " << widgetName << " widget = new " << widgetName << "();" << endl
               << "        ui.setupUi(widget);" << endl
               << "        widget.show();" << endl
               << "        QApplication.exec();" << endl
               << "    }" << endl;
    }

    output << "}\n\n";
}

void WriteDeclaration::acceptWidget(DomWidget *node)
{
    QString className = QLatin1String("QWidget");
    if (node->hasAttributeClass())
        className = node->attributeClass();

    output << option.indent << "public " << uic->customWidgetsInfo()->realClassName(className) << " " << driver->findOrInsertWidget(node) << ";\n";

    TreeWalker::acceptWidget(node);
}

void WriteDeclaration::acceptLayout(DomLayout *node)
{
    QString className = QLatin1String("QLayout");
    if (node->hasAttributeClass())
        className = node->attributeClass();

    output << option.indent << "public " << className << " " << driver->findOrInsertLayout(node) << ";\n";

    TreeWalker::acceptLayout(node);
}

void WriteDeclaration::acceptSpacer(DomSpacer *node)
{
    output << option.indent << "public QSpacerItem " << driver->findOrInsertSpacer(node) << ";\n";

    TreeWalker::acceptSpacer(node);
}

void WriteDeclaration::acceptActionGroup(DomActionGroup *node)
{
    output << option.indent << "public QActionGroup " << driver->findOrInsertActionGroup(node) << ";\n";

    TreeWalker::acceptActionGroup(node);
}

void WriteDeclaration::acceptAction(DomAction *node)
{
    output << option.indent << "public QAction " << driver->findOrInsertAction(node) << ";\n";

    TreeWalker::acceptAction(node);
}

} // namespace Java
