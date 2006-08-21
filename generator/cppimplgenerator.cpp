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

#include "cppimplgenerator.h"
#include "reporthandler.h"
#include <qnativepointer.h>

#include <QDir>
#include <QtDebug>

#define VOID_POINTER_ORDINAL 8

class Indentation {
public:
    Indentation() { ++indent; }
    ~Indentation() { --indent; }
    static int indent;
};

class Indentor {
public:
};

int Indentation::indent = 0;
inline QTextStream &operator <<(QTextStream &s, const Indentor &)
{
    for (int i=0; i<Indentation::indent; ++i)
        s << "    ";
    return s;
}

Indentor INDENT;

enum JNISignatureFormat {
    Underscores,        // Used in the jni exported function names
    SlashesAndStuff     // Used for looking up functions through jni
};

QString jni_signature(MetaJavaType *java_type, JNISignatureFormat format = Underscores)
{
    if (!java_type)
        return "V";

    static QHash<QString, QString> table;
    if (table.isEmpty()) {
        table["boolean"] = "Z";
        table["byte"] = "B";
        table["char"] = "C";
        table["short"] = "S";
        table["int"] = "I";
        table["long"] = "J";
        table["float"] = "F";
        table["double"] = "D";
    }

    if (java_type->isArray()) {
        return "_3" + jni_signature(java_type->arrayElementType(), format);
    } else if (java_type->isPrimitive()) {
        QString signature = table.value(java_type->name());
        Q_ASSERT(!signature.isEmpty());
        return signature;
    } else if (java_type->isNativePointer()) {
        if (format == Underscores)
            return "Lcom_trolltech_qt_QNativePointer_2";
        else
            return "Lcom/trolltech/qt/QNativePointer;";
    } else if (java_type->isEnum() || java_type->isFlags()) {
        return "I";
    } else if (java_type->isThread()) {
        if (format == Underscores)
            return "Ljava_lang_Thread_2";
        else
            return "Ljava/lang/Thread;";
    }

    QString signature;

    QString name = java_type->name();
    if (java_type->isObject()) {
        if (const InterfaceTypeEntry *ie
            = static_cast<const ObjectTypeEntry *>(java_type->typeEntry())->designatedInterface())
            name = ie->javaName();
    }

    if (format == Underscores) {
        signature = "L";
        signature += java_type->package().replace("_", "_1").replace(".", "_");
        signature += "_";
#if 0
        if (java_type->isEnum()) {
            const EnumTypeEntry *etype = static_cast<const EnumTypeEntry *>(java_type->typeEntry());

            signature += etype->javaQualifier().replace("_", "_1");
            signature += "_00024";
        }
#endif

        signature += name.replace("_", "_1");
        signature += "_2";
    } else {
        signature = "L";
        signature += java_type->package().replace(".", "/");
        signature += "/";
        signature += name;
        signature += ";";
    }

    return signature;
}

static QHash<QString, QString> table;
QString default_return_statement_qt(const MetaJavaType *java_type)
{
    if (!java_type)
        return "return";

    if (table.isEmpty()) {
        table["boolean"] = "false";
        table["byte"] = "0";
        table["char"] = "0";
        table["short"] = "0";
        table["int"] = "0";
        table["long"] = "0";
        table["float"] = "0f";
        table["double"] = "0.0";
    }

    QString signature = table.value(java_type->typeEntry()->javaName());

    if (!signature.isEmpty())
        return "return " + signature;

    Q_ASSERT(!java_type->isPrimitive());
    if (java_type->isVariant())
        return "return QVariant()";
    if (java_type->isJavaString())
        return "return QString()";
    if (java_type->isJavaChar())
        return "return QChar()";
    else if (java_type->isEnum())
        return "return " + java_type->typeEntry()->name() + "(0)";
    else if (java_type->isValue())
        return "return " + java_type->typeEntry()->name() + "()";
    else if (java_type->isContainer() && ((ContainerTypeEntry *)java_type->typeEntry())->type() == ContainerTypeEntry::StringListContainer)
        return "return " + java_type->typeEntry()->name() + "()";
    else if (java_type->isContainer())
        return "return " + java_type->cppSignature() + "()";
    else
        return "return 0";
}

QString default_return_statement_java(const MetaJavaType *java_type)
{
    if (!java_type)
        return "return";
    if (java_type->isArray())
        return "return null";

    if (table.isEmpty()) {
        table["boolean"] = "false";
        table["byte"] = "0";
        table["char"] = "0";
        table["short"] = "0";
        table["int"] = "0";
        table["long"] = "0";
        table["float"] = "0f";
        table["double"] = "0.0";
    }

    QString signature = table.value(java_type->typeEntry()->javaName());
    if (!signature.isEmpty())
        return "return " + signature;

    Q_ASSERT(!java_type->isPrimitive());
    return "return 0";
}

QByteArray jniTypeName(const MetaJavaType *java_type)
{
    if (!java_type)
        return "Void";

    if (java_type->isPrimitive()) {
        static QHash<QString, const char *> table;
        if (table.isEmpty()) {
            table["boolean"] = "Boolean";
            table["byte"] = "Byte";
            table["char"] = "Char";
            table["short"] = "Short";
            table["int"] = "Int";
            table["long"] = "Long";
            table["float"] = "Float";
            table["double"] = "Double";
        }
        return table[java_type->name()];
    } else if (java_type->isEnum() || java_type->isFlags()) {
        return "Int";
    } else {
        return "Object";
    }
}

QByteArray newXxxArray(const MetaJavaType *java_type)
{
    return "New" + jniTypeName(java_type) + "Array";
}

QByteArray setXxxArrayElement(const MetaJavaType *java_type)
{
    Q_ASSERT(java_type);
    return "Set" + jniTypeName(java_type) + "ArrayElement";
}

QByteArray getXxxArrayElement(const MetaJavaType *java_type)
{
    Q_ASSERT(java_type);
    return "Get" + jniTypeName(java_type) + "ArrayElement";
}

QByteArray getXxxArrayRegion(const MetaJavaType *java_type)
{
    Q_ASSERT(java_type);
    return "Get" + jniTypeName(java_type) + "ArrayRegion";
}

QByteArray setXxxArrayRegion(const MetaJavaType *java_type)
{
    Q_ASSERT(java_type);
    return "Set" + jniTypeName(java_type) + "ArrayRegion";
}

QByteArray callXxxMethod(const MetaJavaType *java_type)
{
    return "Call" + jniTypeName(java_type) + "Method";
}

QString jni_function_signature(QString package, QString class_name, const QString &function_name,
                               const QString &return_type)
{
    QString s;
    s += "extern \"C\" JNIEXPORT ";
    s += return_type;
    s += " Java_";
    s += package.replace("_", "_1").replace(".", "_");
    s += '_';
    s += class_name.replace("_", "_1");
    s += '_';
    s += QString(function_name).replace("_", "_1");
    return s;
}

QString CppImplGenerator::fileNameForClass(const MetaJavaClass *java_class) const
{
    return QString("qtjambishell_%1.cpp").arg(java_class->name());
}

void CppImplGenerator::writeSignalFunction(QTextStream &s, const MetaJavaFunction *signal, const MetaJavaClass *cls,
                                           int pos)
{
    writeFunctionSignature(s, signal, cls, signalWrapperPrefix(), NoOption, "QtJambi_SignalWrapper_");
    s << endl << "{" << endl;
    {
        MetaJavaArgumentList arguments = signal->arguments();
        Indentation indent;

        if (arguments.size() > 0)
            s << INDENT << "jvalue arguments[" << arguments.size() << "];" << endl;
        else
            s << INDENT << "jvalue *arguments = 0;" << endl;
        s << INDENT << "Q_ASSERT(link && link->environment());" << endl
          << INDENT << "JNIEnv *__jni_env = link->environment();" << endl
          << INDENT << "__jni_env->PushLocalFrame(100);" << endl;

        for (int i=0; i<arguments.size(); ++i) {
            const MetaJavaArgument *argument = arguments.at(i);
            writeQtToJava(s,
                          argument->type(),
                          argument->name(),
                          "__java_" + argument->name(),
                          BoxedPrimitive);
            s << INDENT << "arguments[" << i << "].l = __java_" << argument->name() << ";" << endl;
        }
        s << INDENT << "qtjambi_call_java_signal(__jni_env, m_signals[" << pos << "], arguments);"
                    << endl;

        s << INDENT << "__jni_env->PopLocalFrame(0);" << endl;
    }
    s << "}" << endl << endl;

    writeFinalFunction(s, signal, cls);
}

void CppImplGenerator::write(QTextStream &s, const MetaJavaClass *java_class)
{

    bool shellClass = java_class->generateShellClass();

    // Includes
    writeExtraIncludes(s, java_class);
    bool shellInclude = (java_class->generateShellClass()
                         || java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible).size() > 0);

    // need to includ QPainter for all widgets...
    {
        const MetaJavaClass *qwidget = java_class;
        while (qwidget && qwidget->name() != "QWidget") {
            qwidget = qwidget->baseClass();
        }
        if (qwidget)
            s << "#include <QPainter>" << endl << endl;
    }


    if (shellInclude)
        s << "#include \"qtjambishell_" << java_class->name() << ".h\"" << endl;

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
    s << endl;

    s << "#include \"qtjambi_core.h\"" << endl
      << "#include \"qtjambifunctiontable.h\"" << endl
      << "#include \"qtjambilink.h\"" << endl;

    if (java_class->isQObject()) { // ### remove for 4.2
        s << "#include <QtCore/QMutex>" << endl
          << "#include <QtCore/QMutexLocker>" << endl << endl
          << "Q_GLOBAL_STATIC(QMutex, qobject_constructor_lock);" << endl << endl;
    }


   if (!java_class->isQObject() && !java_class->typeEntry()->isValue())
        writeFinalDestructor(s, java_class);

    if (shellInclude)
        writeShellSignatures(s, java_class);

    if (shellClass) {
        foreach (MetaJavaFunction *function, java_class->functions()) {
            if (function->isConstructor() && !function->isPrivate())
                writeShellConstructor(s, function);
        }
        writeShellDestructor(s, java_class);

        // Functions in shell class
        MetaJavaFunctionList shell_functions = java_class->functionsInShellClass();

        int pos = -1;
        for (int i=0; i<shell_functions.size(); ++i) {
            const MetaJavaFunction *function = shell_functions.at(i);

            if (!function->isFinalInCpp())
                ++pos;

            writeShellFunction(s, function, java_class, pos);
        }

        // Write public overrides for functions that are protected in the base class
        // so they can be accessed from the native callback
        MetaJavaFunctionList public_override_functions = java_class->publicOverrideFunctions();
        foreach (MetaJavaFunction *function, public_override_functions) {
            writePublicFunctionOverride(s, function, java_class);
        }

        // Write virtual function overries used to decide on static/virtual calls
        MetaJavaFunctionList virtual_functions = java_class->virtualOverrideFunctions();
        foreach (const MetaJavaFunction *function, virtual_functions) {            
            writeVirtualFunctionOverride(s, function, java_class);
        }

        writeExtraFunctions(s, java_class);
    }

    // Signals
    MetaJavaFunctionList signal_functions =
        java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible);
    for (int i=0; i<signal_functions.size(); ++i)
        writeSignalFunction(s, signal_functions.at(i), java_class, i);

    // Native callbacks (all java functions require native callbacks)
    MetaJavaFunctionList class_funcs = java_class->functionsInJava();
    foreach (MetaJavaFunction *function, class_funcs) {
        if ((!function->isAbstract() || function->isFinalInJava()) && !function->isEmptyFunction())
            writeFinalFunction(s, function, java_class);
    }

    // Field accessors
    foreach (MetaJavaField *field, java_class->fields()) {
        if (field->wasPublic() || (field->wasProtected() && !java_class->isFinal()))
            writeFieldAccessors(s, field);
    }


    if (!java_class->isQObject())
        writeFromNativeFunction(s, java_class);

    if (java_class->typeEntry()->isValue())
        writeFromArrayFunction(s, java_class);

    // generate the __qt_cast_to_Xxx functions
    if (!java_class->isNamespace() && !java_class->isInterface()) {
        MetaJavaClassList interfaces = java_class->interfaces();
        foreach (MetaJavaClass *iface, interfaces)
            writeInterfaceCastFunction(s, java_class, iface);
    }

    writeSignalInitialization(s, java_class);

    s << endl << endl;
}

void CppImplGenerator::writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class)
{
    const ComplexTypeEntry *class_type = java_class->typeEntry();
    Q_ASSERT(class_type);

    CodeSnipList code_snips = class_type->codeSnips();
    foreach (const CodeSnip &snip, code_snips) {
        if (snip.language == CodeSnip::ShellCode || snip.language == CodeSnip::NativeCode) {
            s << snip.code << endl;
        }
    }
}

void CppImplGenerator::writeShellSignatures(QTextStream &s, const MetaJavaClass *java_class)
{
    bool has_constructors = !java_class->queryFunctions(MetaJavaClass::Constructors).isEmpty();

    // Match the query in the generation of the shell class implementation, so that we get the
    // same index for each function
    MetaJavaFunctionList virtual_functions = java_class->functionsInShellClass();
    MetaJavaFunctionList inconsistents = java_class->cppInconsistentFunctions();
    MetaJavaFunctionList signal_functions = java_class->cppSignalFunctions();

    // Write the function names...
    if (has_constructors && virtual_functions.size()) {
        {
            Indentation indent;

            int pos = -1;
            foreach (MetaJavaFunction *function, virtual_functions) {
                if (!function->isFinalInCpp())
                    ++pos;
                else
                    continue ;

                if (pos == 0)
                    s << "static const char *qtjambi_method_names[] = {";
                else
                    s << ",";
                s << endl
                  << "/* " << QString("%1").arg(QString::number(pos), 3) << " */ "
                  << "\"" << function->name() << "\"";
            }
            if (pos >= 0)
                s << endl << "};" << endl << endl;
            else
                s << "static const char **qtjambi_method_names = 0;" << endl;
        }

        // Write the function signatures
        {
            Indentation indent;


            int pos = -1;
            foreach (MetaJavaFunction *function, virtual_functions) {
                if (!function->isFinalInCpp())
                    ++pos;
                else
                    continue ;

                if (pos == 0)
                    s << "static const char *qtjambi_method_signatures[] = {";
                else
                    s << ",";
                s << endl
                  << "/* " << QString("%1").arg(QString::number(pos), 3) << " */ "
                  << "\"(";

                MetaJavaArgumentList arguments = function->arguments();
                foreach (const MetaJavaArgument *argument, arguments)
                    s << jni_signature(argument->type(), SlashesAndStuff);

                s << ")" << jni_signature(function->type(), SlashesAndStuff) << "\"";
            }
            if (pos >= 0)
                s << endl << "};" << endl;
            else
                s << "static const char **qtjambi_method_signatures = 0;" << endl;
            s << "static const int qtjambi_method_count = " << QString::number(pos + 1) << ";" << endl
              << endl;
        }
    }

    if (has_constructors && inconsistents.size()) {
        // Write the inconsistent function names...
        {
            Indentation indent;
            s << "static const char *qtjambi_inconsistent_names[] = {";
            for (int i=0; i<inconsistents.size(); ++i) {
                if (i != 0)
                    s << ",";
                s << endl << INDENT << "\"" << inconsistents.at(i)->name() << "\"";
            }
            s << endl << "};" << endl << endl;
        }

        // Write the function signatures
        {
            Indentation indent;
            s << "static const char *qtjambi_inconsistent_signatures[] = {";
            for (int i=0; i<inconsistents.size(); ++i) {
                if (i != 0)
                    s << ",";
                s << endl << INDENT << "\"(";

                MetaJavaArgumentList arguments = inconsistents.at(i)->arguments();
                foreach (const MetaJavaArgument *argument, arguments)
                    s << jni_signature(argument->type(), SlashesAndStuff);
                s << ")" << jni_signature(inconsistents.at(i)->type(), SlashesAndStuff) << "\"";
            }
            s << endl << "};" << endl << endl;
        }
        s << "static const int qtjambi_inconsistent_count = " << inconsistents.size() << ";" << endl
          << endl;
    }



    if (signal_functions.size()) {
        Indentation indent;
        s << "static const char *qtjambi_signal_names[] = {";
        for (int i=0; i<signal_functions.size(); ++i) {
            if (i != 0)
                s << ",";

            const MetaJavaFunction *f = signal_functions.at(i);

            QString signalName = f->name();

            s << endl << INDENT << "\"" << signalName << "\"";
        }
        s << endl << "};" << endl << endl;

        s << "static const int qtjambi_signal_argumentcounts[] = {";
        for (int i=0; i<signal_functions.size(); ++i) {
            if (i != 0)
                s << ",";
            s << endl << INDENT << signal_functions.at(i)->arguments().count();
        }
        s << endl << "};" << endl << endl;
        s << "static const int qtjambi_signal_count = " << signal_functions.size() << ";" << endl
          << endl;
    }
}

void CppImplGenerator::writeSetupConnections(QTextStream &s, const MetaJavaClass *java_class,
                                             const QString &sender_name, const QString &receiver_name)
{
    MetaJavaFunctionList signal_funcs =
        java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible);

    foreach (const MetaJavaFunction *signal, signal_funcs) {
        s << INDENT << "QObject::connect(" << sender_name << ", SIGNAL(";
        writeFunctionSignature(s, signal, 0, QString(), Option(SkipName | SkipReturnType | OriginalName));
        s << "), " << receiver_name << ", SLOT(";
        writeFunctionSignature(s, signal, 0, signalWrapperPrefix(), Option(SkipName | SkipReturnType));
        s << "));" << endl;
    }
}

void CppImplGenerator::writeShellConstructor(QTextStream &s, const MetaJavaFunction *java_function)
{
    if (java_function->isModifiedRemoved(MetaJavaFunction::CppShellFunction))
        return;

    const MetaJavaClass *cls = java_function->ownerClass();
    MetaJavaArgumentList arguments = java_function->arguments();

    writeFunctionSignature(s, java_function, cls);

    s << endl;
    s << "    : " << cls->qualifiedCppName() << "(";
    for (int i=0; i<arguments.size(); ++i) {
        s << arguments.at(i)->name();
        if (i != arguments.size() - 1)
            s << ", ";
    }
    s << ")," << endl;
    s << "      m_vtable(0)," << endl
        << "      m_link(0)" << endl;

    s << "{" << endl;

    writeCodeInjections(s, java_function, cls, CodeSnip::Beginning);
    writeCodeInjections(s, java_function, cls, CodeSnip::End);

    s << "}" << endl << endl;
}

void CppImplGenerator::writeShellDestructor(QTextStream &s, const MetaJavaClass *java_class)
{
    s << shellClassName(java_class) << "::~"
      << shellClassName(java_class) << "()" << endl
      << "{" << endl;
    {
        Indentation indent;
        s << INDENT << "if (m_vtable)" << endl
          << INDENT << "    m_vtable->deref();" << endl
          << INDENT << "if (m_link) {" << endl
          << INDENT << "    m_link->setEnvironment(qtjambi_current_environment());" << endl
          << INDENT << "    m_link->resetObject();" << endl
          << INDENT << "}"
          << endl;
    }
    s << "}" << endl << endl;
}

void CppImplGenerator::writeCodeInjections(QTextStream &s, const MetaJavaFunction *java_function,
                                           const MetaJavaClass *implementor, CodeSnip::Position position)
{
    FunctionModificationList mods;
    const MetaJavaClass *cls = implementor;
    while (cls != 0) {
        mods += java_function->modifications(cls);

        if (cls == cls->baseClass())
            break;
        cls = cls->baseClass();
    }

    foreach (FunctionModification mod, mods) {
        if (mod.language != CodeSnip::ShellCode)
            continue ;
        if (mod.snips.count() <= 0)
            continue ;

        foreach (CodeSnip snip, mod.snips) {
            if (snip.position != position)
                continue ;

            if (position == CodeSnip::End)
                s << endl;

            QString code = snip.formattedCode("    ");
            ArgumentMap map = snip.argumentMap;
            ArgumentMap::iterator it = map.begin();
            for (;it!=map.end();++it) {
                int pos = it.key() - 1;
                QString meta_name = it.value();

                if (pos >= 0 && pos < java_function->arguments().count()) {
                    code = code.replace(meta_name, java_function->arguments().at(pos)->name());
                } else {
                    QString debug = QString("Argument map specifies invalid argument index %1"
                                            "for function '%2'")
                                            .arg(pos + 1).arg(java_function->name());
                    ReportHandler::warning(debug);
                }

            }
            s << code;
            if (position == CodeSnip::Beginning)
                s << endl;
        }
    }
}

void CppImplGenerator::writeDisableGarbageCollection(QTextStream &s,
                                                     const MetaJavaFunction *java_function,
                                                     const QString &var_name,
                                                     const MetaJavaClass *implementor)
{
    FunctionModificationList mods;
    const MetaJavaClass *cls = implementor;
    while (cls != 0) {
        mods += java_function->modifications(cls);

        if (cls == cls->baseClass())
            break;
        cls = cls->baseClass();
    }

    foreach (FunctionModification mod, mods) {
        if (mod.language != CodeSnip::ShellCode)
            continue ;
        if (mod.disable_gc_argument_indexes.count() == 0)
            continue ;

        s << INDENT << "if (" << var_name << " != 0) {" << endl;

        {
            Indentation indent;
            s << INDENT << "QtJambiLink *__link = QtJambiLink::findLink(__jni_env, "
                        << var_name << ");" << endl
              << INDENT << "Q_ASSERT(__link != 0);" << endl
              << INDENT << "__link->disableGarbageCollection(__jni_env, " << var_name << ");" << endl;
        }
        s << INDENT << "}" << endl;
        break ;
    }

}

void CppImplGenerator::writeShellFunction(QTextStream &s, const MetaJavaFunction *java_function,
                                          const MetaJavaClass *implementor, int id)
{
    writeFunctionSignature(s, java_function, implementor, QString(), OriginalName);

    s << endl
      << "{" << endl;

    writeCodeInjections(s, java_function, implementor, CodeSnip::Beginning);

    //     s << "    printf(\"%s : %s\\n\", \"" << java_function->enclosingClass()->name() << "\""
    //       << ", \"" << java_function->name() << "\");" << endl;

    if (!java_function->isFinalInCpp()) {
        Indentation indent;
        s << INDENT << "jmethodID method_id = m_vtable->method(" << id << ");" << endl;
        s << INDENT << "if (method_id) {" << endl;

        {
            Indentation indent;
            s << INDENT << "JNIEnv *__jni_env = qtjambi_current_environment();" << endl
              << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl
              << INDENT << "__jni_env->PushLocalFrame(100);" << endl;

            MetaJavaArgumentList arguments = java_function->arguments();
            foreach (const MetaJavaArgument *argument, arguments) {
                if (!argument->type()->isPrimitive())
                    writeQtToJava(s,
                                  argument->type(),
                                  argument->name(),
                                  "__java_" + argument->name());
            }

            MetaJavaType *function_type = java_function->type();

            s << INDENT;
            if (function_type)
                s << translateType(function_type) << " " << "__java_return_value = ";
            s << "__jni_env->" << callXxxMethod(java_function->type())
              << "(m_link->javaObject(__jni_env), method_id";
            if (arguments.size() > 0)
                s << ", ";
            writeFunctionCallArguments(s, java_function, "__java_", NoCasts);
            s << ");" << endl
              << INDENT << "qtjambi_exception_check(__jni_env);" << endl;

            if (function_type)
                writeJavaToQt(s, function_type, "__qt_return_value", "__java_return_value");

            writeDisableGarbageCollection(s, java_function, "__java_return_value", implementor);

            s << INDENT << "__jni_env->PopLocalFrame(0);" << endl;
            if (function_type)
                s << INDENT << "return __qt_return_value;" << endl;

        }

        s << INDENT << "} else {" << endl;
        {
            Indentation indent;
            writeBaseClassFunctionCall(s, java_function, implementor);
        }

        s << INDENT << "}" << endl;

        writeCodeInjections(s, java_function, implementor, CodeSnip::End);

        // A little trick to get exceptions when one forgets to close
        // painters at the end of paintevent.
        if (java_function->name() == "paintEvent") {
            QString space(29, ' ');

            s << INDENT << "QPaintDevice *pd = QPainter::redirected(this);" << endl
              << INDENT << "if (!pd) pd = this;" << endl
              << INDENT << "if (pd->paintingActive()) {" << endl;
            {
                Indentation indent;
                s << INDENT << "JNIEnv *env = qtjambi_current_environment();" << endl
                  << INDENT << "jclass cls = env->GetObjectClass(m_link->javaObject(env));" << endl
                  << INDENT << "QtJambiLink::throwQtException(env," << endl
                  << INDENT << space << "QString(\"Active QPainter after paint event in %1, "
                  << "use QPainter::end()\")"
                  << ".arg(qtjambi_class_name(env, cls))," << endl
                  << INDENT << space << "\"QPaintingOutsidePaintEventException\");" << endl;
            }
            s << INDENT << "}" << endl;
        }

        s << "}" << endl << endl;

    } else {
        {
            Indentation indent;
            writeBaseClassFunctionCall(s, java_function, implementor);
            writeCodeInjections(s, java_function, implementor, CodeSnip::End);
        }

        s << "}" << endl << endl;
    }

}

// ### kill implementor

void CppImplGenerator::writePublicFunctionOverride(QTextStream &s,
                                                   const MetaJavaFunction *java_function,
                                                   const MetaJavaClass *implementor)
{
    Q_ASSERT(java_function->originalAttributes()
             & (MetaJavaAttributes::Protected
                | MetaJavaAttributes::Final));

    // The write a public override version of this function to be used by native functions
    writeFunctionSignature(s, java_function, implementor, "__public_",
                           Option(EnumAsInts | UnderscoreSpaces
                           | (java_function->isAbstract() ? SkipName : NoOption)));
    s << endl
      << "{" << endl;
    Indentation indent;
    writeBaseClassFunctionCall(s, java_function, implementor);
    s << "}" << endl << endl;
}


void CppImplGenerator::writeVirtualFunctionOverride(QTextStream &s,
                                                    const MetaJavaFunction *java_function,
                                                    const MetaJavaClass *implementor)
{
    Q_ASSERT(!java_function->isFinalInCpp());

    Option options = Option(EnumAsInts | UnderscoreSpaces);

    // The write a public override version of this function to be used by native functions
    writeFunctionSignature(s, java_function, implementor, "__override_",
                           options,
                           QString(), // the class prefix
                           QStringList() << "bool static_call");
    s << endl
      << "{" << endl;
    Indentation indent;
    s << INDENT << "if (static_call) {" << endl;
    {
        Indentation indent;
        writeBaseClassFunctionCall(s, java_function, implementor);
    }
    s << INDENT << "} else {" << endl;
    {
        Indentation indent;
        writeBaseClassFunctionCall(s, java_function, implementor, VirtualCall);
    }

    s << INDENT << "}" << endl
      << "}" << endl << endl;
}


void CppImplGenerator::writeBaseClassFunctionCall(QTextStream &s,
                                                  const MetaJavaFunction *java_function,
                                                  const MetaJavaClass *,
                                                  Option options)
{
    bool static_call = !(options & VirtualCall);
    s << INDENT;
    if (java_function->isAbstract() && static_call) {
        s << default_return_statement_qt(java_function->type()) << ";" << endl;
    } else {
        if (java_function->type())
            s << "return ";
        if (static_call) {
            const MetaJavaClass *implementor = java_function->implementingClass();
            if (java_function->isInterfaceFunction())
                implementor = java_function->interfaceClass()->primaryInterfaceImplementor();
            s << implementor->qualifiedCppName() << "::";
        }
        s << java_function->originalName() << "(";
        writeFunctionCallArguments(s, java_function, QString(), Option(options | ForceEnumCast));
        s << ");" << endl;
    }
}


void CppImplGenerator::writeFunctionName(QTextStream &s,
                                         const MetaJavaFunction *java_function,
                                         const MetaJavaClass *java_class)
{
    const MetaJavaClass *cls = java_class ? java_class : java_function->ownerClass();
    MetaJavaArgumentList arguments = java_function->arguments();

    // Function signature
    bool callThrough = java_function->needsCallThrough();
    QString return_type = translateType(java_function->type());
    QString function_name;

    if (!callThrough)
        function_name = java_function->name();
    else
        function_name = java_function->marshalledName();

    s << jni_function_signature(cls->package(), cls->name(), function_name, return_type);

    s << "__";
    if (callThrough && !java_function->isStatic() && !java_function->isConstructor())
        s << "J";

    if (arguments.isEmpty())
        return;


    foreach (const MetaJavaArgument *argument, arguments) {
        if (!argument->type()->hasNativeId())
            s << jni_signature(argument->type());
        else
            s << "J";
    }
}

void CppImplGenerator::writeFinalFunctionArguments(QTextStream &s, const MetaJavaFunction *java_function,
                                                   const QString &java_object_name)
{
    bool callThrough = java_function->needsCallThrough();

    s << "("
      << "JNIEnv *__jni_env," << endl;
    if (java_function->isStatic())
        s << " jclass";
    else
        s << " jobject " << java_object_name;

    bool hasNativeId = (callThrough && !java_function->isStatic() && !java_function->isConstructor());
    if (hasNativeId)
        s << "," << endl << " jlong __this_nativeId";

    // the function arguments
    MetaJavaArgumentList arguments = java_function->arguments();
    foreach (const MetaJavaArgument *argument, arguments) {

        s << "," << endl << " ";
        if (!argument->type()->hasNativeId())
            s << translateType(argument->type());
        else
            s << "jlong ";
        s << " " << argument->name();
    }
    s << ")" << endl << "{" << endl;
}


/*!
    Generates type conversion from Java -> Qt for all the arguments
    that are to be to be passed to the function
*/
void CppImplGenerator::writeFinalFunctionSetup(QTextStream &s, const MetaJavaFunction *java_function,
                                               const QString &qt_object_name,
                                               const MetaJavaClass *cls)
{
    // Translate each of the function arguments into qt types
    MetaJavaArgumentList arguments = java_function->arguments();
    foreach (const MetaJavaArgument *argument, arguments) {
        if (!argument->type()->isPrimitive()) {
                writeJavaToQt(s,
                            argument->type(),
                            "__qt_" + argument->name(),
                            argument->name(),
                            UseNativeIds);
        }
    }

    // Extract the qt equivalent to the this pointer and name it "qt_object_name"
    if (!java_function->isStatic() && !java_function->isConstructor()) {
        QString className = java_function->isFinalOverload() ? cls->name() : shellClassName(cls);
        s << INDENT
          << className << " *" << qt_object_name
          << " = (" << className << " *) qtjambi_from_jlong(__this_nativeId);"
          << endl
          << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl
          << INDENT << "Q_ASSERT(" << qt_object_name << ");" << endl;
    }
}


void CppImplGenerator::writeFinalFunction(QTextStream &s, const MetaJavaFunction *java_function,
                                          const MetaJavaClass *java_class)
{
    Q_ASSERT(java_class);

    if (java_function->isModifiedRemoved(MetaJavaFunction::CppNativeFunction))
        return;


    const MetaJavaClass *cls = java_class ? java_class : java_function->ownerClass();
    const MetaJavaType *function_type = java_function->type();

    const QString qt_object_name = java_function->isStatic() ? shellClassName(cls) : "__qt_this";
    const QString java_object_name = java_function->isStatic() ? "__jni_class" : "__jni_object";

    // function signature...
    bool callThrough = java_function->needsCallThrough();
    writeFunctionName(s, java_function, cls);
    s << endl;
    writeFinalFunctionArguments(s, java_function, java_object_name);

    Indentation indent;

    // Avoid compiler warnings when the variables are unused
    {
        s << INDENT << "Q_UNUSED(__jni_env)" << endl;

        if (!java_function->isStatic())
            s << INDENT << "Q_UNUSED(" << java_object_name << ")" << endl;

        bool hasNativeId = (callThrough && !java_function->isStatic() && !java_function->isConstructor());
        if (hasNativeId)
            s << INDENT << "Q_UNUSED(__this_nativeId)" << endl;
    }

    if (cls->isFinal() && (!java_function->isAbstract() || !java_function->isFinalInJava()) && !java_function->wasPublic()) {
        QString debug = QString("protected function '%1' in final class '%2'")
            .arg(java_function->signature()).arg(java_class->name());
        ReportHandler::warning(debug);
        s << INDENT << default_return_statement_qt(java_function->type()) << ";";

    } else {
        writeFinalFunctionSetup(s, java_function, qt_object_name, cls);
        if (java_function->isConstructor()) {
            writeFinalConstructor(s, java_function, qt_object_name, java_object_name);
        } else {

            QString function_prefix = "";
            QStringList extra_param;
            Option option = NoOption;

            if (java_function->isFinalOverload()) {
                // no prefix
            } else if (java_function->isFinalInCpp() && !java_function->wasPublic()) {
                function_prefix = "__public_";
            } else if (!java_function->isFinalInCpp() && !java_function->isStatic()) {
                function_prefix = "__override_";
                extra_param.append("__do_static_call");
                s << INDENT
                  << "bool __do_static_call = __this_nativeId ? ((QtJambiLink *) "
                  << "__this_nativeId)->createdByJava() : false;" << endl;
            } else {
                option = OriginalName;
            }

            // Call the Qt function on the java object
            s << "    ";
            if (function_type) {
                const QString qt_return_value = "__qt_return_value";
                const QString java_return_value = "__java_return_value";
                writeTypeInfo(s, function_type, EnumAsInts);
                s << " " << qt_return_value
                << " = ";

                writeFunctionCall(s, qt_object_name, java_function, function_prefix, option,
                                  extra_param);
                s << endl;

                writeQtToJava(s, function_type, qt_return_value, java_return_value);
                s << INDENT << "return " << java_return_value << ";";

            } else {
                writeFunctionCall(s, qt_object_name, java_function, function_prefix, option,
                                  extra_param);
            }
        }
    }
    s << endl << "}";
    s << endl << endl;
}

void CppImplGenerator::generate()
{
    CppGenerator::generate();

    // Generate library constructor for each package
    QHash<QString, bool> generatedHash;
    QHash<QString, QString> initializers;
    foreach (MetaJavaClass *cls, classes()) {
        if (!shouldGenerate(cls))
            continue;

        if (!generatedHash.value(cls->package(), false)) {
            generatedHash.insert(cls->package(), true);

            initializers[subDirectoryForClass(cls)] = cls->package();
        }
    }

    // Write out the library initializers...
    for (QHash<QString, QString>::const_iterator it = initializers.begin();
         it != initializers.end(); ++it) {
        QDir dir(outputDirectory() + QString("/") + it.key());
        dir.mkpath(dir.absolutePath());
        QFile f(dir.absoluteFilePath("qtjambi_libraryinitializer.cpp"));
        if (!f.open(QIODevice::WriteOnly)) {
            ReportHandler::warning(QString("failed to open file '%1' for writing")
                                   .arg(f.fileName()));
            return;
        }

        QString signature = jni_function_signature(it.value(), "QtJambi_LibraryInitializer",
                                                   "__qt_initLibrary", "void");
        QTextStream s(&f);
        s << "#include \"metainfo.h\"" << endl
          << "#include \"qtjambi_global.h\"" << endl << endl
          << signature << "(JNIEnv *, jclass)" << endl
          << "{" << endl
          << "    __metainfo_init();" << endl
          << "}" << endl << endl;
    }
}


void CppImplGenerator::writeAssignment(QTextStream &s, const QString &destName, const QString &srcName,
                                       const MetaJavaType *java_type)
{
    if (java_type->isArray()) {
        for (int i=0; i<java_type->arrayElementCount(); ++i) {
            writeAssignment(s, destName + "[" + QString::number(i) + "]",
                srcName + "[" + QString::number(i) + "]", java_type->arrayElementType());
        }
    } else {
        s << INDENT << destName << " = " << srcName << ";" << endl;
    }
}

void CppImplGenerator::writeFieldAccessors(QTextStream &s, const MetaJavaField *java_field)
{
    Q_ASSERT(java_field);
    Q_ASSERT(java_field->isPublic() || java_field->isProtected());

    const MetaJavaFunction *setter = java_field->setter();
    const MetaJavaFunction *getter = java_field->getter();

    const MetaJavaClass *cls = java_field->enclosingClass();
    FieldModification mod = cls->typeEntry()->fieldModification(java_field->name());

    // Setter
    if (mod.isWritable() && !java_field->type()->isConstant()) {
        // Write public override for protected fields
        if (setter->isProtected()) {
            writeFunctionSignature(s, setter, setter->ownerClass());
            s << endl
              << "{" << endl;
            {
                Indentation indent;

                Q_ASSERT(setter->arguments().count() > 0);
                const MetaJavaArgument *argument = setter->arguments().at(0);

                QString thisRef = java_field->isStatic()
                    ? setter->ownerClass()->qualifiedCppName() + QString("::")
                    : QString("this->");
                writeAssignment(s, thisRef + java_field->name(), argument->name(), argument->type());
            }
            s << "}" << endl << endl;
        }

        writeFunctionName(s, setter, setter->ownerClass());
        s << endl;
        writeFinalFunctionArguments(s, setter, "__java_object");

        {
            Indentation indent;

            if (!java_field->isStatic())
                s << INDENT << "Q_UNUSED(__java_object);" << endl;
            s << INDENT << "Q_UNUSED(__jni_env);" << endl << endl;

            writeFinalFunctionSetup(s, setter, "__qt_object", setter->ownerClass());

            Q_ASSERT(setter->arguments().count() == 1);
            const MetaJavaArgument *argument = setter->arguments().at(0);

            QString dest;
            if (setter->isStatic())
                dest = shellClassName(setter->ownerClass()) + "::";
            else
                dest = "__qt_object->";

            QString src;
            if (!argument->type()->isPrimitive())
                src = "__qt_" + argument->name();
            else
                src = argument->name();

            if (setter->isPublic())
                writeAssignment(s, dest + java_field->name(), src, argument->type());
            else
                s << INDENT << dest << setter->name() << "_setter(" << src << ");" << endl;
        }
        s << "}" << endl << endl;
    }

    if (mod.isReadable()) {
        // Getter
        if (getter->isProtected()) {
            writeFunctionSignature(s, getter, getter->ownerClass());
            s << endl
              << "{" << endl;
            {
                Indentation indent;
                s << INDENT << "return " << java_field->name() << ";" << endl;
            }
            s << "}" << endl << endl;
        }


        writeFunctionName(s, getter, getter->ownerClass());
        s << endl;
        writeFinalFunctionArguments(s, getter, "__java_object");

        {
            Indentation indent;

        if (!java_field->isStatic())
            s << INDENT << "Q_UNUSED(__java_object);" << endl;
            s << INDENT << "Q_UNUSED(__jni_env);" << endl << endl;

            writeFinalFunctionSetup(s, getter, "__qt_object", getter->ownerClass());

            const QString java_return_value = "__java_return_value";
            QString qt_return_value;
            if (setter->isStatic())
                qt_return_value = shellClassName(setter->ownerClass()) + "::";
            else
                qt_return_value = "__qt_object->";


            // To avoid "taking address of tmp"
            s << INDENT;
            writeTypeInfo(s, getter->type(), Option(ArrayAsPointer));
            QString tmp_name = "__tmp_" + getter->name();
            s << tmp_name << " = ";

            if (getter->isPublic())
                qt_return_value += java_field->name();
            else
                qt_return_value += getter->name() + "_getter()";
            s << qt_return_value << ";" << endl;

            writeQtToJava(s, getter->type(), tmp_name, java_return_value);
            s << INDENT << "return " << java_return_value << ";" << endl;
        }
        s << "}" << endl << endl;
    }
}

void CppImplGenerator::writeFinalDestructor(QTextStream &s, const MetaJavaClass *cls)
{
    bool has_constructors = !cls->queryFunctions(MetaJavaClass::Constructors).isEmpty();
    if (has_constructors)
        s << INDENT << "static void qtjambi_destructor(void *ptr) { delete ("
          << shellClassName(cls) << " *)ptr; }" << endl << endl;
}

void CppImplGenerator::writeFinalConstructor(QTextStream &s,
                                         const MetaJavaFunction *java_function,
                                         const QString &qt_object_name,
                                         const QString &java_object_name)
{
    const MetaJavaClass *cls = java_function->ownerClass();
    MetaJavaArgumentList arguments = java_function->arguments();
    QString className = cls->name();

    if (cls->isQObject()) // ### remove for 4.2
        s << INDENT << "QMutexLocker locker(qobject_constructor_lock());" << endl;

    s << INDENT << shellClassName(cls) << " *" << qt_object_name
      << " = new " << shellClassName(cls)
      << "(";
    writeFunctionCallArguments(s, java_function, "__qt_");
    s << ");" << endl;

    s << INDENT << "QtJambiLink *__qt_java_link = ";
    if (cls->isQObject()) {
        s << "qtjambi_construct_qobject(__jni_env, " << java_object_name << ", "
          << qt_object_name << ", "
          << static_cast<const ObjectTypeEntry *>(cls->typeEntry())->isMemoryManaged() << ")";
    } else {
        s << "qtjambi_construct_object(__jni_env, " << java_object_name << ", " << qt_object_name;
        if (cls->typeEntry()->isValue())
            s << ", \"" << className << "\")";
        else // non-QObject, object type
            s << ", QMetaType::Void, qtjambi_destructor)";
    }
    s << ";" << endl
      << INDENT << "if (!__qt_java_link) {" << endl;
    {
        Indentation indent;
        s << INDENT << "qWarning(\"object construction failed for type: "
          << className << "\");" << endl
          << INDENT << "return;" << endl;
    }
    s << INDENT << "}" << endl;

    if (!cls->hasVirtualFunctions() && !cls->hasInconsistentFunctions())
        return;

    // Set up the link object
    s << INDENT << qt_object_name << "->m_link = __qt_java_link;" << endl
      << INDENT << qt_object_name << "->m_link->setCreatedByJava(true);" << endl;

    // Set up the vtable
    QString space(48, ' ');
    s << "    " << qt_object_name << "->m_vtable = qtjambi_setup_vtable(__jni_env, " << endl
      << space << "__jni_object, " << endl;

    if (cls->cppInconsistentFunctions().size()) {
        s << space << "qtjambi_inconsistent_count, " << endl
          << space << "qtjambi_inconsistent_names, " << endl
          << space << "qtjambi_inconsistent_signatures, " << endl;
    } else {
        s << "0, 0, 0, // no inconsistent functions" << endl;
    }

    if (cls->functionsInShellClass().size()) {
        s << space << "qtjambi_method_count, " << endl
          << space << "qtjambi_method_names, " << endl
          << space << "qtjambi_method_signatures" << endl;
    } else {
        s << "0, 0, 0 // no virtual functions" << endl;
    }

    s << space << ");" << endl;
}

void CppImplGenerator::writeSignalInitialization(QTextStream &s, const MetaJavaClass *java_class)
{
    if (!java_class->isQObject()
        || java_class->queryFunctions(MetaJavaClass::Signals | MetaJavaClass::Visible).size() == 0) {
        return ;
    }

    s << jni_function_signature(java_class->package(), java_class->name(), "__qt_signalInitialization", "void")
      << endl << "(JNIEnv *__jni_env, jobject java_this, jlong ptr)" << endl
      << "{" << endl
      << "   QtJambiLink *link = (QtJambiLink *) ptr;" << endl
      << "   if (link == 0)" << endl
      << "       return ;" << endl << endl
      << "   QObject *qt_this = link->qobject();" << endl
      << "   Q_ASSERT(qt_this);" << endl << endl
      << "   QtJambi_SignalWrapper_" << java_class->name() << " *qt_wrapper = "
      << "   (QtJambi_SignalWrapper_" << java_class->name() << " *) link->signalWrapper();" << endl
      << "   if (qt_wrapper != 0) " << endl
      << "       return ;" << endl
      << "   qt_wrapper = new QtJambi_SignalWrapper_" << java_class->name() << ";" << endl
      << "   link->setSignalWrapper(qt_wrapper);" << endl
      << "   qt_wrapper->link = link;" << endl << endl
      << "   qtjambi_setup_signals(__jni_env," << endl
      << "                        java_this," << endl
      << "                        qt_wrapper->m_signals," << endl
      << "                        qtjambi_signal_count," << endl
      << "                        qtjambi_signal_names," << endl
      << "                        qtjambi_signal_argumentcounts);" << endl << endl;

    {
        Indentation indent;
        writeSetupConnections(s, java_class, "qt_this", "qt_wrapper");
    }
    s << "}";
}


void CppImplGenerator::writeFromNativeFunction(QTextStream &s, const MetaJavaClass *java_class)
{
    s << jni_function_signature(java_class->package(),
                                java_class->name(),
                                "fromNativePointer",
                                "jobject");
    s << endl
      << "(JNIEnv *__jni_env," << endl
      << " jclass," << endl
      << " jobject nativePointer)" << endl
      << "{" << endl;
    {
        Indentation indent;
        s << INDENT << "void *ptr = qtjambi_to_cpointer(__jni_env, nativePointer, 1);" << endl
            << INDENT << "return qtjambi_from_object(__jni_env, ptr, \"" << java_class->typeEntry()->lookupName()
          << "\", \"" << java_class->package().replace(".", "/") << "/\");" << endl
          << "}" << endl;
    }
}

void CppImplGenerator::writeFromArrayFunction(QTextStream &s, const MetaJavaClass *java_class)
{
    s << jni_function_signature(java_class->package(),
                                java_class->name(),
                                "nativePointerArray",
                                "jobject");
    s << endl
      << "(JNIEnv *__jni_env," << endl
      << " jclass," << endl
      << " jobjectArray array)" << endl
      << "{" << endl;
    {
        Indentation indent;
        s << INDENT << "return qtjambi_array_to_nativepointer(__jni_env, " << endl
          << INDENT << "                                     array, " << endl
          << INDENT << "                                     sizeof("
                    << java_class->qualifiedCppName() << "));" << endl;
    }
    s << "}" << endl;
}


void CppImplGenerator::writeInterfaceCastFunction(QTextStream &s,
                                                  const MetaJavaClass *java_class,
                                                  const MetaJavaClass *interface)
{
    Q_ASSERT(interface->isInterface());
    const InterfaceTypeEntry *ie = static_cast<const InterfaceTypeEntry *>(interface->typeEntry());
    QString interface_name = ie->origin()->javaName();

    s << endl
      << jni_function_signature(java_class->package(),
                                java_class->name(),
                                QString("__qt_cast_to_%1").arg(interface_name),
                                "jlong");

    s << "__J" << endl
      << "(JNIEnv *," << endl
      << " jobject," << endl
      << " jlong ptr)" << endl
      << "{" << endl
      << "    return (jlong) (" << interface_name << " *) "
      << "(" << java_class->name() << " *) ptr;" << endl
      << "}" << endl;
}


void CppImplGenerator::writeJavaToQt(QTextStream &s,
                                     const MetaJavaClass *java_class,
                                     const MetaJavaType *function_return_type,
                                     const QString &qt_name,
                                     const QString &java_name)
{
    s << INDENT << shellClassName(java_class) << " *" << qt_name << " = ("
      << shellClassName(java_class) << " *) ";
    if (java_class->isQObject())
        s << "qtjambi_to_qobject";
    else
        s << "qtjambi_to_object";
    s << "(__jni_env, " << java_name << ");" << endl;
    if (java_class->isQObject()) {
        // ### throw exceptions when objects are null...
        s << INDENT << "if (!" << qt_name << ") "
          << default_return_statement_java(function_return_type) << ";"  << endl << endl;
    }
}


void CppImplGenerator::writeJavaToQt(QTextStream &s,
                                     const MetaJavaType *java_type,
                                     const QString &qt_name,
                                     const QString &java_name,
                                     Option options)
{
    if (java_type->isVariant()) {
        s << INDENT << "QVariant " << qt_name
          << " = qtjambi_to_qvariant(__jni_env, " << java_name << ");" << endl;
    } else if (java_type->isArray() && java_type->arrayElementType()->isPrimitive()) {
        MetaJavaType *elementType = java_type->arrayElementType();

        // ### Don't assert on wrong array lengths
        s << INDENT << "int __java_len = __jni_env->GetArrayLength((jarray) " << java_name << ");" << endl
          << INDENT << "Q_ASSERT(__java_len == " << java_type->arrayElementCount() << ");" << endl;

        s << INDENT;
        writeTypeInfo(s, elementType);
        s << " " << qt_name << "[" << java_type->arrayElementCount() << "];" << endl;

        s << INDENT << "__jni_env->" << getXxxArrayRegion(elementType) << "( (" << translateType(java_type)
          << ")" << java_name << ", 0, " << java_type->arrayElementCount() << ", "
          << "(" << translateType(elementType) << " *" << ")"
          << qt_name << ");" << endl;

    } else if (java_type->isArray()) {
        MetaJavaType *elementType = java_type->arrayElementType();

        s << INDENT << "int __java_len = __jni_env->GetArrayLength((jarray) " << java_name << ");" << endl
          << INDENT << "Q_ASSERT(__java_len == " << java_type->arrayElementCount() << ");" << endl;
        writeTypeInfo(s, elementType);
        s << "[" << java_type->arrayElementCount() << "]" << qt_name << ";" << endl;

        for (int i=0; i<java_type->arrayElementCount(); ++i) {
            writeJavaToQt(s, elementType, qt_name + "[" + QString::number(i) + "]",
                "__jni_env->GetObjectArrayElement(" + java_name + ", " + QString::number(i) + ")", options);
        }

    } else if (java_type->isJavaString()) {
        s << INDENT << "QString " << qt_name
          << " =  qtjambi_to_qstring(__jni_env, (jstring) " << java_name << ");" << endl;

    } else if (java_type->isJavaChar()) {
        s << INDENT << "QChar " << qt_name
          << " = (ushort)" << java_name << ";" << endl;

    } else if (java_type->isEnum() || java_type->isFlags()) {

        bool written = false;
        if (java_type->isEnum()) {
            MetaJavaEnum *java_enum =
                findEnum(m_java_classes, static_cast<const EnumTypeEntry *>(java_type->typeEntry()));
            if (java_enum && !java_enum->isPublic()) {

                s << INDENT << "int " << qt_name << " = ";
                written = true;
            }
        }

        if (!written) {
            QString qualified_name = java_type->typeEntry()->qualifiedCppName();
            s << INDENT << qualified_name << " " << qt_name
              << " = (" << qualified_name << ") ";
        }


        if (options & BoxedPrimitive) {
            const PrimitiveTypeEntry *pentry = TypeDatabase::instance()->findJavaPrimitiveType("int");
            Q_ASSERT(pentry);

            s << "qtjambi_to_" << pentry->name() << "(__jni_env, " << java_name << ");" << endl;

        } else {
            s << java_name << ';' << endl;
        }

    } else if (java_type->isContainer()) {
        writeJavaToQtContainer(s, java_type, qt_name, java_name);

    } else if (java_type->isThread()) {
        s << INDENT << "QThread *" << qt_name << " = qtjambi_to_thread(__jni_env, " << java_name
          << ");" << endl;

    } else if (java_type->typeEntry()->isCustom()) {
        const CustomTypeEntry *custom_type =
            static_cast<const CustomTypeEntry *>(java_type->typeEntry());
        s << INDENT;
        custom_type->generateCppJavaToQt(s, java_type, "__jni_env", qt_name, java_name);
        s << ";" << endl;

    } else {

        const TypeEntry *type = java_type->typeEntry();
        QString class_name = type->name();
        QString qualified_class_name = fixCppTypeName(type->qualifiedCppName());

        // Declaration and the c-cast
        s << INDENT;
        writeTypeInfo(s, java_type);
        s << ' ' << qt_name << " = (";
        writeTypeInfo(s, java_type);
        s << ") ";

        if (java_type->isPrimitive()) {
            if (options & BoxedPrimitive) {
                const PrimitiveTypeEntry *pentry = static_cast<const PrimitiveTypeEntry *>(type);

                if (!pentry->preferredConversion())
                    pentry = TypeDatabase::instance()->findJavaPrimitiveType(pentry->javaName());
                Q_ASSERT(pentry);

                s << "qtjambi_to_" << pentry->name() << "(__jni_env, " << java_name << ");" << endl;

            } else {
                s << java_name << ';' << endl;
            }

#if 0
        } else if (java_type->isEnum()) {
            s << "qtjambi_to_enum(__jni_env, " << java_name << ");" << endl;
#endif

        } else if ((java_type->isQObject() || java_type->isObject())
                   && static_cast<const ObjectTypeEntry *>(type)->designatedInterface()) {
            const InterfaceTypeEntry *ie =
                static_cast<const ObjectTypeEntry *>(type)->designatedInterface();
            s << "qtjambi_to_interface(__jni_env, (QtJambiLink *)" << java_name << ", "
              << "\"" << ie->javaName() << "\", \""
              << ie->javaPackage().replace(".", "/") << "/\", "
              << "\"__qt_cast_to_" << type->javaName() << "\");" << endl;

        } else if (java_type->isObject() || java_type->isQObject() || java_type->isNativePointer()) {
            if (java_type->isReference()) {
                s << "* (" << qualified_class_name << " "
                  << QString(java_type->actualIndirections(), '*') << ") ";
            }

            if (java_type->isNativePointer()) {
                s << "qtjambi_to_cpointer("
                << "__jni_env, "
                << java_name << ", "
                << java_type->actualIndirections() << ");" << endl;
            } else if (java_type->isQObject()) {
                if ((options & UseNativeIds) == 0)
                    s << "qtjambi_to_qobject(__jni_env, ";
                else
                    s << "qtjambi_from_jlong(";
                s << java_name;
                s << ");" << endl;
            } else {
                if ((options & UseNativeIds) == 0)
                    s << "qtjambi_to_object(__jni_env, ";
                else
                    s << "qtjambi_from_jlong(";
                s << java_name;
                s << ");" << endl;
            }

        } else {
            s << "*"
              << "(" << qualified_class_name << " *)";
            if ((options & UseNativeIds) == 0)
                s << "qtjambi_to_object(__jni_env, ";
            else
                s << "qtjambi_from_jlong(";
            s << java_name << ");" << endl;

        }
    }
    s << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl;
}

static int nativePointerType(const MetaJavaType *java_type)
{
    Q_ASSERT(java_type);
    Q_ASSERT(java_type->isNativePointer());

    if (!java_type->typeEntry()->isPrimitive())
        return PointerType;

    if (java_type->indirections() > 1)
        return PointerType;

    static QHash<QString, int> types;
    if (types.isEmpty()) {
        types["boolean"] = BooleanType;
        types["byte"] = ByteType;
        types["char"] = CharType;
        types["short"] = ShortType;
        types["int"] = IntType;
        types["long"] = LongType;
        types["float"] = FloatType;
        types["double"] = DoubleType;
    }

    QString javaName = java_type->typeEntry()->javaName();
    if (!types.contains(javaName))
        return PointerType;

    return types[javaName];
}

void CppImplGenerator::writeQtToJava(QTextStream &s,
                                     const MetaJavaType *java_type,
                                     const QString &qt_name,
                                     const QString &java_name,
                                     Option option)
{
    if (java_type->isArray() && java_type->arrayElementType()->isPrimitive()) {
        MetaJavaType *elementType = java_type->arrayElementType();

        s << INDENT << translateType(java_type) << " " << java_name << " = __jni_env->" << newXxxArray(elementType)
          << "(" << java_type->arrayElementCount() << ");" << endl;

        s << INDENT << "__jni_env->" << setXxxArrayRegion(elementType) << "("
          << "(" << translateType(java_type) << ")" << java_name
          << ", 0, " << java_type->arrayElementCount() << ", "
          << "(" << translateType(elementType) << " *" << ")"
          << qt_name << ");" << endl;

    } else if (java_type->isArray()) {
        MetaJavaType *elementType = java_type->arrayElementType();

        s << INDENT << "jobject " << java_name << " = __jni_env->NewObjectArray("
          << java_type->arrayElementCount() << ");" << endl;

        s << "jobject __qt_element = 0;";

        for (int i=0; i<java_type->arrayElementCount(); ++i) {
            writeQtToJava(s, elementType, qt_name + "[" + QString::number(i) + "]",
                "__qt_element", option);
            s << "__jni_env->SetObjectArrayElement((jobjectArray) " << java_name << ", "
              << i << ", __qt_element);" << endl;
        }

    } else if (java_type->isPrimitive()) {
        const PrimitiveTypeEntry *type =
            static_cast<const PrimitiveTypeEntry *>(java_type->typeEntry());
        Q_ASSERT(type);
        if (option & BoxedPrimitive) {
            s << INDENT << "jobject " << java_name << " = qtjambi_from_" << type->javaName()
              << "(__jni_env, " << qt_name << ");" << endl;
        } else {
            s << INDENT << type->jniName() <<  " " << java_name << " = (" << type->jniName() << ") "
              << qt_name << ";" << endl;
        }
    } else if (java_type->isVariant()) {
        s << INDENT << "jobject " << java_name << " = qtjambi_from_qvariant(__jni_env, "
          << qt_name << ");" << endl;

    } else if (java_type->isJavaString()) {
        s << INDENT << "jstring " << java_name << " = qtjambi_from_qstring(__jni_env, "
          << qt_name << ");" << endl;

    } else if (java_type->isJavaChar()) {
        s << INDENT << "jchar " << java_name << " = " << qt_name << ".unicode();" << endl;

    } else if (java_type->isEnum() || java_type->isFlags()) {
        if (option & BoxedPrimitive) {
            s << INDENT << "jobject " << java_name << " = qtjambi_from_int(__jni_env, " << qt_name << ");" << endl;
        } else {
            s << INDENT << "int " << java_name << " = " << qt_name << ";" << endl;
        }
    } else if (java_type->isContainer()) {
        writeQtToJavaContainer(s, java_type, qt_name, java_name);

    } else if (java_type->isThread()) {
        s << INDENT << "jobject " << java_name << " = qtjambi_from_thread(__jni_env, " << qt_name
          << ");" << endl;

    } else if (!java_type->isNativePointer() && java_type->typeEntry()->isCustom()) {
        s << INDENT;
        static_cast<const CustomTypeEntry *>(java_type->typeEntry())
            ->generateCppQtToJava(s, java_type, "__jni_env", qt_name, java_name);
        s << ";" << endl;

    } else {
        s << INDENT << "jobject " << java_name << " = ";

        if (java_type->isQObject()) {
            s << "qtjambi_from_qobject(__jni_env, " << "(QObject *) ";

            if (java_type->isReference() && java_type->indirections() == 0)
                s << "&";

            s << qt_name
              << ", \"" << java_type->typeEntry()->lookupName() << "\""
              << ", \"" << java_type->package().replace(".", "/") << "/\""
              << ");" << endl;

#if 0
        } else if (java_type->isEnum()) {

            const EnumTypeEntry *et = static_cast<const EnumTypeEntry *>(java_type->typeEntry());
            s << "qtjambi_from_enum(__jni_env, " << qt_name << ", \""
              << et->javaQualifier() << "$" << et->javaName() << "\");" << endl;
#endif
        } else if (java_type->isNativePointer()) {
            s << "qtjambi_from_cpointer(__jni_env, ";
            if (java_type->isReference())
                s << "&";
            s << qt_name << ", " << nativePointerType(java_type) << ", "
              << java_type->actualIndirections() << ");" << endl;
        } else if (java_type->isValue()) {
            s << "qtjambi_from_object(__jni_env, &" << qt_name;

            s << ", \"" << java_type->typeEntry()->lookupName() << "\"";

            s << ", \"" << java_type->package().replace(".", "/") << "/\");" << endl;

        } else {
            s << "qtjambi_from_object(__jni_env, ";
            if (java_type->isReference())
                s << "&";
            s << qt_name
              << ", \"" << java_type->typeEntry()->lookupName() << "\""
              << ", \"" << java_type->package().replace(".", "/") << "/\");" << endl;

        }
    }

    s << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl;
}


void CppImplGenerator::writeQtToJavaContainer(QTextStream &s,
                                              const MetaJavaType *java_type,
                                              const QString &qt_name,
                                              const QString &java_name)
{
    Q_ASSERT(java_type->isContainer());
    const ContainerTypeEntry *type =
        static_cast<const ContainerTypeEntry *>(java_type->typeEntry());

    if (type->type() == ContainerTypeEntry::ListContainer
        || type->type() == ContainerTypeEntry::VectorContainer
        || type->type() == ContainerTypeEntry::StringListContainer
        || type->type() == ContainerTypeEntry::LinkedListContainer
        || type->type() == ContainerTypeEntry::StackContainer
        || type->type() == ContainerTypeEntry::SetContainer
        || type->type() == ContainerTypeEntry::QueueContainer) {
        Q_ASSERT(java_type->instantiations().size() == 1);
        MetaJavaType *targ = java_type->instantiations().first();

        s << endl
          << INDENT << "jobject " << java_name << " = ";

        switch (type->type()) {
        case ContainerTypeEntry::LinkedListContainer:
        case ContainerTypeEntry::QueueContainer:
            s << "qtjambi_linkedlist_new(__jni_env)";
            break;
        case ContainerTypeEntry::StackContainer:
            s << "qtjambi_stack_new(__jni_env)";
            break;
        case ContainerTypeEntry::SetContainer:
            s << "qtjambi_hashset_new(__jni_env)";
            break;
        default:
            s << "qtjambi_arraylist_new(__jni_env, " << qt_name << ".size())";
            break;
        }

        s << ";" << endl
          << INDENT;


        writeTypeInfo(s, java_type, ForceValueType);
        s << "::const_iterator __qt_end_iterator = " << qt_name << ".constEnd();" << endl
          << INDENT;
        s << "for (";
        writeTypeInfo(s, java_type, ForceValueType);
        s << "::const_iterator " << qt_name << "_it = " << qt_name << ".constBegin(); "
          << qt_name << "_it != __qt_end_iterator; ++" << qt_name << "_it) {" << endl;
        {
            Indentation indent;
            s << INDENT;
            writeTypeInfo(s, targ);
            s << " __qt_tmp = *" << qt_name << "_it;" << endl;
            writeQtToJava(s, targ, "__qt_tmp", "__java_tmp", BoxedPrimitive);
            s << INDENT << "qtjambi_collection_add(__jni_env, " << java_name << ", __java_tmp);"
              << endl;
        }
        s << INDENT << "}" << endl;

    } else if (type->type() == ContainerTypeEntry::PairContainer) {
        QList<MetaJavaType *> args = java_type->instantiations();
        Q_ASSERT(args.size() == 2);

        s << INDENT << "jobject " << java_name << ";" << endl
          << INDENT << "{" << endl;
        {
            Indentation indent;
            writeQtToJava(s, args.at(0), qt_name + ".first", "__java_tmp_first", BoxedPrimitive);
            writeQtToJava(s, args.at(1), qt_name + ".second", "__java_tmp_second", BoxedPrimitive);
            s << INDENT << java_name << " = qtjambi_pair_new(__jni_env, "
              << "__java_tmp_first, __java_tmp_second);" << endl;
        }

        s << INDENT << "}" << endl;

    } else if (type->type() == ContainerTypeEntry::MapContainer
               || type->type() == ContainerTypeEntry::HashContainer) {
        QString constructor = type->type() == ContainerTypeEntry::MapContainer
                              ? "qtjambi_treemap_new"
                              : "qtjambi_hashmap_new";

        Q_ASSERT(java_type->instantiations().size() == 2);
        MetaJavaType *targ_key = java_type->instantiations().at(0);
        MetaJavaType *targ_val = java_type->instantiations().at(1);

        s << endl
          << INDENT << "jobject " << java_name << " = " << constructor << "(__jni_env, " << qt_name
          << ".size());" << endl
          << INDENT;
        writeTypeInfo(s, java_type, Option(ExcludeReference | ExcludeConst));
        s << "::const_iterator it;" << endl
          << INDENT << "for (it=" << qt_name << ".constBegin(); it!=" << qt_name << ".constEnd(); ++it) {" << endl;
        {
            Indentation indent;
            s << INDENT;
            writeTypeInfo(s, targ_key);
            s << " __qt_tmp_key = it.key();" << endl
              << INDENT;
            writeTypeInfo(s, targ_val);
            s << " __qt_tmp_val = it.value();" << endl;
            writeQtToJava(s, targ_key, "__qt_tmp_key", "__java_tmp_key", BoxedPrimitive);
            writeQtToJava(s, targ_val, "__qt_tmp_val", "__java_tmp_val", BoxedPrimitive);
            s << INDENT << "qtjambi_map_put(__jni_env, " << java_name
              << ", __java_tmp_key, __java_tmp_val);" << endl;
        }
        s << INDENT << "}" << endl;

    } else {
        ReportHandler::warning(QString("Unable to generate container type %1, type=%2")
                               .arg(java_type->name()).arg(type->type()));
    }

    s << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl;
}


void CppImplGenerator::writeJavaToQtContainer(QTextStream &s,
                                              const MetaJavaType *java_type,
                                              const QString &qt_name,
                                              const QString &java_name)
{
    Q_ASSERT(java_type->isContainer());
    const ContainerTypeEntry *type =
        static_cast<const ContainerTypeEntry *>(java_type->typeEntry());

    if (type->type() == ContainerTypeEntry::ListContainer
        || type->type() == ContainerTypeEntry::VectorContainer
        || type->type() == ContainerTypeEntry::StringListContainer
        || type->type() == ContainerTypeEntry::LinkedListContainer
        || type->type() == ContainerTypeEntry::StackContainer
        || type->type() == ContainerTypeEntry::SetContainer
        || type->type() == ContainerTypeEntry::QueueContainer) {
        Q_ASSERT(java_type->instantiations().size() == 1);
        MetaJavaType *targ = java_type->instantiations().first();

        s << INDENT;
        writeTypeInfo(s, java_type, ForceValueType);
        s << qt_name << ";" << endl;

        s << INDENT << "if (" << java_name << " != 0) {" << endl;
        {
            Indentation indent;
            s << INDENT << "jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, "
              << java_name << ");" << endl
              << INDENT << "jsize __qt__size = __jni_env->GetArrayLength(__qt__array);" << endl;

            if (type->type() == ContainerTypeEntry::VectorContainer
                || type->type() == ContainerTypeEntry::StackContainer)
                s << INDENT << qt_name << ".reserve(__qt__size);" << endl;

            s << INDENT << "for (int i=0; i<__qt__size; ++i) {" << endl;
            {
                Indentation indent;
                s << INDENT << "jobject __java_element = "
                  << "__jni_env->GetObjectArrayElement(__qt__array, i);" << endl;
                writeJavaToQt(s, targ, "__qt_element", "__java_element", BoxedPrimitive);
                s << INDENT << qt_name << " << __qt_element;" << endl;
            }
            s << INDENT << "}" << endl;
        }
        s << INDENT << "}" << endl;
    } else if (type->type() == ContainerTypeEntry::PairContainer) {
        QList<MetaJavaType *> targs = java_type->instantiations();
        Q_ASSERT(targs.size() == 2);

        s << INDENT;
        writeTypeInfo(s, java_type, ForceValueType);
        s << " " << qt_name << ";" << endl
          << INDENT << "if (" << java_name << " != 0) {" << endl;
        {
            // separate scope required just in case function takes two QPair's.
            Indentation indent;
            s << INDENT << "jobject __java_first = qtjambi_pair_get(__jni_env, "
              << java_name << ", 0);" << endl;
            writeJavaToQt(s, targs.at(0), "__qt_first", "__java_first", BoxedPrimitive);

            s << INDENT << "jobject __java_second = qtjambi_pair_get(__jni_env, "
              << java_name << ", 1);" << endl;
            writeJavaToQt(s, targs.at(1), "__qt_second", "__java_second", BoxedPrimitive);

            s << INDENT << qt_name << ".first = __qt_first;" << endl
              << INDENT << qt_name << ".second = __qt_second;" << endl;
        }
        s << INDENT << "}" << endl;
    } else if (type->type() == ContainerTypeEntry::MapContainer
               || type->type() == ContainerTypeEntry::HashContainer) {
        Q_ASSERT(java_type->instantiations().size() == 2);
        MetaJavaType *targ_key = java_type->instantiations().at(0);
        MetaJavaType *targ_val = java_type->instantiations().at(1);

        s << INDENT;
        writeTypeInfo(s, java_type, ForceValueType);
        s << qt_name << ";" << endl;
        s << INDENT << "if (" << java_name << " != 0) {" << endl;
        {
            Indentation indent;
            s << INDENT << "int __qt_list_size = qtjambi_map_size(__jni_env, " << java_name
              << ");" << endl
              << INDENT
              << "jobjectArray __java_entry_set = qtjambi_map_entryset_array(__jni_env, " << java_name
              << ");" << endl;

            s << INDENT << "for (int i=0; i<__qt_list_size; ++i) {" << endl;
            {
                Indentation indent;
                s << INDENT
                  << "QPair<jobject, jobject> __java_entry = "
                  << "qtjambi_entryset_array_get(__jni_env, __java_entry_set, i);"
                  << endl
                  << INDENT << "jobject __java_key = __java_entry.first;" << endl
                  << INDENT << "jobject __java_val = __java_entry.second;" << endl;
                writeJavaToQt(s, targ_key, "__qt_key", "__java_key", BoxedPrimitive);
                writeJavaToQt(s, targ_val, "__qt_val", "__java_val", BoxedPrimitive);
                s << INDENT << qt_name << ".insert(__qt_key, __qt_val);" << endl;
            }
            s << INDENT << "}" << endl;
        }
        s << INDENT << "}" << endl;

    } else {
        ReportHandler::warning(QString("Unable to generate container type %1, %2")
                               .arg(java_type->name()).arg(type->type()));
    }

    s << INDENT << "QTJAMBI_EXCEPTION_CHECK(__jni_env);" << endl;
}


void CppImplGenerator::writeFunctionCall(QTextStream &s, const QString &object_name,
                                         const MetaJavaFunction *java_function,
                                         const QString &prefix,
                                         Option option,
                                         const QStringList &extra_arguments)
{
    QString function_name = option & OriginalName ? java_function->originalName() : java_function->name();
    s << object_name << (java_function->isStatic() ? "::" : "->")
      << prefix << function_name << "(";
    writeFunctionCallArguments(s, java_function, "__qt_");

    // The extra arguments...
    for (int i=0; i<extra_arguments.size(); ++i) {
        if (i > 0 || java_function->arguments().size() != 0)
            s << ", ";
        s << extra_arguments.at(i);
    }

    s << ");";
}


void CppImplGenerator::writeFunctionCallArguments(QTextStream &s,
                                                  const MetaJavaFunction *java_function,
                                                  const QString &prefix,
                                                  Option options)
{
    MetaJavaArgumentList arguments = java_function->arguments();
    for (int i=0; i<arguments.size(); ++i) {
        const MetaJavaArgument *argument = arguments.at(i);

        bool enum_as_int = (options & EnumAsInts) && (argument->type()->typeEntry()->isEnum()
                                                      || argument->type()->typeEntry()->isFlags());
        if (argument->type()->isEnum()) {
            MetaJavaEnum *java_enum =
                findEnum(m_java_classes,
                         static_cast<const EnumTypeEntry *>(argument->type()->typeEntry()));
            if (java_enum == 0) {
                ReportHandler::warning(QString("enum not found: '%1'")
                    .arg(argument->type()->typeEntry()->qualifiedCppName()));
            } else {
                enum_as_int |= !java_enum->isPublic();
            }
        }

        if ((!(options & NoCasts) && !enum_as_int)
            || ((options & ForceEnumCast) && argument->type()->isEnum())) {
            s << "(";
            writeTypeInfo(s, argument->type());
            s << ")";
        }
        if (!argument->type()->isPrimitive()) {
            s << prefix;
        }
        s << argument->name();
        if (i != arguments.size() - 1)
            s << ", ";
    }
}


QString CppImplGenerator::translateType(const MetaJavaType *java_type) const
{
    if (!java_type)
        return "void";

    if (java_type->isPrimitive()
        || java_type->isJavaString()
        || java_type->isVariant()
        || java_type->isJavaChar()
        || java_type->isArray()) {
        return java_type->typeEntry()->jniName();
    } else if (java_type->isEnum() || java_type->isFlags()) {
         return "jint";
     } else {
        return "jobject";
     }
}


void CppImplGenerator::writeExtraIncludes(QTextStream &s, const MetaJavaClass *java_class)
{
    IncludeList includes = java_class->typeEntry()->extraIncludes();

    int used = 0;
    foreach (const Include &i, includes) {
        if (i.type != Include::JavaImport) {
            s << i.toString() << endl;
            ++used;
        }
    }


    if (used)
        s << endl;

}

