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
#include "prigenerator.h"

class CppGenerator : public Generator
{
    Q_OBJECT

public:
    virtual QString subDirectoryForClass(const AbstractMetaClass *cls) const
    {
        return "cpp/" + cls->package().replace(".", "_") + "/";
    }

    static void writeTypeInfo(QTextStream &s, const AbstractMetaType *type, Option option = NoOption);
    static void writeFunctionSignature(QTextStream &s, const AbstractMetaFunction *java_function,
                                const AbstractMetaClass *implementor = 0,
                                const QString &name_prefix = QString(),
                                Option option = NoOption,
                                const QString &classname_prefix = QString(),
                                const QStringList &extra_arguments = QStringList(),
                                int numArguments = -1);
    static void writeFunctionArguments(QTextStream &s, const AbstractMetaArgumentList &arguments,
                                Option option = NoOption,
                                int numArguments = -1);

    static inline AbstractMetaFunctionList signalFunctions(const AbstractMetaClass *cls);

    QString signalWrapperPrefix() const { return "__qt_signalwrapper_"; }

    bool shouldGenerate(const AbstractMetaClass *java_class) const {
        return !java_class->isNamespace() && !java_class->isInterface()
            && !java_class->typeEntry()->isVariant()
            && (java_class->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp);
    }

    static QString shellClassName(const AbstractMetaClass *java_class) {
        return java_class->generateShellClass()
               ? "QtJambiShell_" + java_class->name()
               : java_class->qualifiedCppName();
    }

 protected:
    PriGenerator *priGenerator;

};

inline AbstractMetaFunctionList CppGenerator::signalFunctions(const AbstractMetaClass *cls) {
    return cls->queryFunctions(AbstractMetaClass::Signals
                               | AbstractMetaClass::Visible
                               | AbstractMetaClass::NotRemovedFromTargetLang);
}


#endif // CPPGENERATOR_H
