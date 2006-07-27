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

#ifndef CPPGENERATOR_H
#define CPPGENERATOR_H

#include "generator.h"
#include "metajava.h"

class CppGenerator : public Generator
{
    Q_OBJECT

public:
    virtual QString subDirectoryForClass(const MetaJavaClass *cls) const
    {
        return "cpp/" + cls->package().replace(".", "_") + "/";
    }

    static void writeTypeInfo(QTextStream &s, const MetaJavaType *type, Option option = NoOption);
    static void writeFunctionSignature(QTextStream &s, const MetaJavaFunction *java_function,
                                const MetaJavaClass *implementor = 0,
                                const QString &name_prefix = QString(),
                                Option option = NoOption,
                                const QString &classname_prefix = QString(),
                                const QStringList &extra_arguments = QStringList());
    static void writeFunctionArguments(QTextStream &s, const MetaJavaArgumentList &arguments,
                                Option option = NoOption);

    QString signalWrapperPrefix() const { return "__qt_signalwrapper_"; }

    bool shouldGenerate(const MetaJavaClass *java_class) const {
        return !java_class->isNamespace() && !java_class->isInterface()
            && !java_class->typeEntry()->isVariant()
            && (java_class->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp);
    }

    static QString shellClassName(const MetaJavaClass *java_class) {
        return java_class->generateShellClass()
               ? "QtJambiShell_" + java_class->name()
               : java_class->qualifiedCppName();
    }

};


#endif // CPPGENERATOR_H
