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

#include "cppheadergenerator.h"

#include <QtCore/QDir>

#include <qdebug.h>

QString CppHeaderGenerator::fileNameForClass(const MetaJavaClass *java_class) const
{
    return QString("qtjambishell_%1.h").arg(java_class->name());
}

void CppHeaderGenerator::writeFieldAccessors(QTextStream &s, const MetaJavaField *java_field)
{
    Q_ASSERT(java_field->isProtected());

    const MetaJavaFunction *setter = java_field->setter();
    const MetaJavaFunction *getter = java_field->getter();

    if (!java_field->type()->isConstant())
        writeFunction(s, setter);

    writeFunction(s, getter);
}

void CppHeaderGenerator::writeSignalWrapper(QTextStream &s, const MetaJavaFunction *signal)
{
    s << "    ";
    writeFunctionSignature(s, signal, 0, signalWrapperPrefix(), 
                           Option(OriginalName | OriginalTypeDescription | IncludeDefaultExpression));
    s << ";" << endl;
}

void CppHeaderGenerator::writeSignalWrappers(QTextStream &s, const MetaJavaClass *java_class)
{
    MetaJavaFunctionList signal_funcs =
        java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible | MetaJavaClass::NotRemovedFromJava);

    if (signal_funcs.size() > 0) {
        s << endl << "public slots:" << endl;
        foreach (const MetaJavaFunction *signal, signal_funcs) {
            writeSignalWrapper(s, signal);
        }
    }
}

void CppHeaderGenerator::writeWrapperClass(QTextStream &s, const MetaJavaClass *java_class)
{
    MetaJavaFunctionList signal_functions =
        java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible | MetaJavaClass::NotRemovedFromJava);
    if (signal_functions.size() == 0)
        return ;

    s << "class QtJambi_SignalWrapper_" << java_class->name() << ": public QObject" << endl
      << "{" << endl
      << "    Q_OBJECT" << endl;
    writeSignalWrappers(s, java_class);
    s << endl << "public:" << endl
      << "    QtJambiSignalInfo m_signals[" << signal_functions.size() << "];" << endl
      << "    QtJambiLink *link;" << endl
      << "};" << endl << endl;
}

void CppHeaderGenerator::write(QTextStream &s, const MetaJavaClass *java_class)
{
    QString include_block = "QTJAMBISHELL_" + java_class->name().toUpper() + "_H";

    s << "#ifndef " << include_block << endl
      << "#define " << include_block << endl << endl
      << "#include <qtjambi_core.h>" << endl;

    Include inc = java_class->typeEntry()->include();
    s << "#include ";
    if (inc.type == Include::IncludePath)
        s << "<";
    else
        s << "\"";
    s << inc.name;
    if (inc.type == Include::IncludePath)
        s << ">";
    else
        s << "\"";
    s << endl << endl;

    IncludeList list = java_class->typeEntry()->extraIncludes();
    foreach (const Include &inc, list) {
        if (inc.type == Include::JavaImport)
            continue;

        s << "#include ";
        if (inc.type == Include::LocalPath)
            s << "\"";
        else
            s << "<";

        s << inc.name;

        if (inc.type == Include::LocalPath)
            s << "\"";
        else
            s << ">";

        s << endl;
    }

    writeForwardDeclareSection(s, java_class);

    writeWrapperClass(s, java_class);

    if (!java_class->generateShellClass()) {
        s << "#endif" << endl << endl;
        return ;
    }

    s << "class " << shellClassName(java_class)
      << " : public " << java_class->qualifiedCppName() << endl
      << "{" << endl;

    s << "public:" << endl;
    foreach (const MetaJavaFunction *function, java_class->functions()) {
        if (function->isConstructor() && !function->isPrivate())
            writeFunction(s, function);
    }

    s << "    ~" << shellClassName(java_class) << "();" << endl;
    s << endl;

    // All functions in original class that should be reimplemented in shell class
    MetaJavaFunctionList shell_functions = java_class->functionsInShellClass();
    foreach (const MetaJavaFunction *function, shell_functions) {
        writeFunction(s, function);
    }

    // Public call throughs for protected functions
    MetaJavaFunctionList public_overrides = java_class->publicOverrideFunctions();
    foreach (const MetaJavaFunction *function, public_overrides) {
        writePublicFunctionOverride(s, function);
    }

    // Override all virtual functions to get the decision on static/virtual call
    MetaJavaFunctionList virtual_functions = java_class->virtualOverrideFunctions();
    foreach (const MetaJavaFunction *function, virtual_functions) {
        writeVirtualFunctionOverride(s, function);
    }

    // Field accessors
    foreach (const MetaJavaField *field, java_class->fields()) {
        if (field->isProtected())
            writeFieldAccessors(s, field);
    }

    writeVariablesSection(s, java_class);
    writeInjectedCode(s, java_class);

    s  << "};" << endl << endl
       << "#endif // " << include_block << endl;
}


/*!
    Writes out declarations of virtual C++ functions so that they
    can be reimplemented from the java side.
*/
void CppHeaderGenerator::writeFunction(QTextStream &s, const MetaJavaFunction *java_function)
{
    if (java_function->isModifiedRemoved(TypeSystem::ShellCode))
        return;

    s << "    ";
    writeFunctionSignature(s, java_function, 0, QString(), Option(OriginalName | ShowStatic));
    s << ";" << endl;
}

void CppHeaderGenerator::writePublicFunctionOverride(QTextStream &s,
                                                     const MetaJavaFunction *java_function)
{
    s << "    ";
    writeFunctionSignature(s, java_function, 0, "__public_", Option(EnumAsInts | ShowStatic | UnderscoreSpaces));
    s << ";" << endl;
}


void CppHeaderGenerator::writeVirtualFunctionOverride(QTextStream &s,
                                                      const MetaJavaFunction *java_function)
{
    if (java_function->isModifiedRemoved(TypeSystem::NativeCode))
        return;

    s << "    ";
    writeFunctionSignature(s, java_function, 0, "__override_", Option(EnumAsInts | ShowStatic | UnderscoreSpaces), QString(), QStringList() << "bool static_call");
    s << ";" << endl;
}


void CppHeaderGenerator::writeForwardDeclareSection(QTextStream &s, const MetaJavaClass *)
{
    s << endl
      << "class QtJambiFunctionTable;" << endl
      << "class QtJambiLink;" << endl;
}


void CppHeaderGenerator::writeVariablesSection(QTextStream &s, const MetaJavaClass *)
{
    s << endl
      << "    QtJambiFunctionTable *m_vtable;" << endl
      << "    QtJambiLink *m_link;" << endl;
}

void CppHeaderGenerator::writeInjectedCode(QTextStream &s, const MetaJavaClass *java_class)
{
    CodeSnipList code_snips = java_class->typeEntry()->codeSnips();
    foreach (const CodeSnip &cs, code_snips) {
        if (cs.language == TypeSystem::ShellDeclaration) {
            s << cs.code() << endl;
        }
    }
}
