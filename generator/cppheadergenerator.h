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

#ifndef CPP_HEADER_GENERATOR
#define CPP_HEADER_GENERATOR

#include "cppgenerator.h"
#include "metajava.h"

class CppHeaderGenerator : public CppGenerator
{
    Q_OBJECT

public:
    virtual QString fileNameForClass(const MetaJavaClass *cls) const;

    void write(QTextStream &s, const MetaJavaClass *java_class);
    void writeFunction(QTextStream &s, const MetaJavaFunction *java_function);
    void writePublicFunctionOverride(QTextStream &s, const MetaJavaFunction *java_function);
    void writeVirtualFunctionOverride(QTextStream &s, const MetaJavaFunction *java_function);
    void writeForwardDeclareSection(QTextStream &s, const MetaJavaClass *java_class);
    void writeVariablesSection(QTextStream &s, const MetaJavaClass *java_class);
    void writeFieldAccessors(QTextStream &s, const MetaJavaField *java_field);
    void writeSignalWrapper(QTextStream &s, const MetaJavaFunction *java_function);
    void writeSignalWrappers(QTextStream &s, const MetaJavaClass *java_class);
    void writeWrapperClass(QTextStream &s, const MetaJavaClass *java_class);
    void writeInjectedCode(QTextStream &s, const MetaJavaClass *java_class);

    bool shouldGenerate(const MetaJavaClass *java_class) const {
        return (java_class->generateShellClass()
                && CppGenerator::shouldGenerate(java_class))
            || (java_class->queryFunctions(MetaJavaClass::Signals).size() > 0
                && (java_class->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp));
    }
};

#endif // CPP_HEADER_GENERATOR
