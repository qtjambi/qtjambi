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

#include "javagenerator.h"
#include "reporthandler.h"
#include "docparser.h"

#include <QtCore/QDir>
#include <QtCore/QTextStream>
#include <QtCore/QVariant>
#include <QDebug>

JavaGenerator::JavaGenerator()
    : m_doc_parser(0),
      m_docs_enabled(false)
{
}

QString JavaGenerator::fileNameForClass(const MetaJavaClass *java_class) const
{
    return QString("%1.java").arg(java_class->name());
}

void JavaGenerator::writeFieldAccessors(QTextStream &s, const MetaJavaField *field)
{
    Q_ASSERT(field->isPublic() || field->isProtected());

    const MetaJavaClass *declaringClass = field->enclosingClass();

    FieldModification mod = declaringClass->typeEntry()->fieldModification(field->name());

    // Set function
    if (mod.isWritable() && !field->type()->isConstant()) {
        const MetaJavaFunction *setter = field->setter();
        if (declaringClass->hasFunction(setter)) {
            QString warning =
                QString("class '%1' already has setter '%2' for public field '%3'")
                .arg(declaringClass->name()).arg(setter->name()).arg(field->name());
            ReportHandler::warning(warning);
        } else {
            writeFunction(s, setter);
        }
    }

    // Get function
    const MetaJavaFunction *getter = field->getter();
    if (mod.isReadable()) {
        if (declaringClass->hasFunction(getter)) {
            QString warning =
                QString("class '%1' already has getter '%2' for public field '%3'")
                .arg(declaringClass->name()).arg(getter->name()).arg(field->name());
            ReportHandler::warning(warning);
        } else {
            writeFunction(s, getter);
        }
    }
}

QString JavaGenerator::translateType(const MetaJavaType *java_type, Option option)
{
    QString s;
    if (!java_type) {
        s = "void";
    } else if (java_type->isArray()) {
        s = translateType(java_type->arrayElementType()) + "[]";
    } else if (java_type->isEnum() || java_type->isFlags()) {
        if (java_type->isEnum() && ((EnumTypeEntry *)java_type->typeEntry())->forceInteger()
            || java_type->isFlags() && ((FlagsTypeEntry *)java_type->typeEntry())->forceInteger()) {
            if (option & BoxedPrimitive)
                s = "java.lang.Integer";
            else
                s = "int";
        } else {
            if (option & EnumAsInts)
                s = "int";
            else
                s = java_type->fullName();
        }
    } else {
        if (java_type->isPrimitive() && (option & BoxedPrimitive)) {
            s = static_cast<const PrimitiveTypeEntry *>(java_type->typeEntry())->javaObjectFullName();

        } else if (java_type->isNativePointer()) {
            s = "com.trolltech.qt.QNativePointer";

        } else if (java_type->isContainer()) {
            s = java_type->typeEntry()->qualifiedJavaName();
            if ((option & SkipTemplateParameters) == 0) {
                s += '<';
                QList<MetaJavaType *> args = java_type->instantiations();
                for (int i=0; i<args.size(); ++i) {
                    if (i != 0)
                        s += ", ";
                    s += translateType(args.at(i), BoxedPrimitive);
                }
                s += '>';
            }

        } else {
            const TypeEntry *type = java_type->typeEntry();
            if (type->designatedInterface())
                type = type->designatedInterface();
            s = type->qualifiedJavaName();
        }
    }

    return s;
}

void JavaGenerator::writeArgument(QTextStream &s,
                                  const MetaJavaFunction *java_function,
                                  const MetaJavaArgument *java_argument,
                                  uint options)
{
    QString modified_type = java_function->typeReplaced(java_argument->argumentIndex() + 1);

    if (modified_type.isEmpty())
        s << translateType(java_argument->type(), (Option) options);
    else
        s << modified_type.replace('$', '.');

    if ((options & SkipName) == 0)
        s << " " << java_argument->argumentName();
}

void JavaGenerator::writeIntegerEnum(QTextStream &s, const MetaJavaEnum *java_enum)
{
    const MetaJavaEnumValueList &values = java_enum->values();

    s << "    public static class " << java_enum->name() << "{" << endl;
    for (int i=0; i<values.size(); ++i) {
        MetaJavaEnumValue *value = values.at(i);

        if (java_enum->typeEntry()->isEnumValueRejected(value->name()))
            continue;

        if (m_doc_parser)
            s << m_doc_parser->documentation(value);

        s << "        public static final int " << value->name() << " = " << value->value();
        s << ";";
        s << endl;
    }

    s << "    } // end of enum " << java_enum->name() << endl << endl;
}

void JavaGenerator::writeEnum(QTextStream &s, const MetaJavaEnum *java_enum)
{
    if (m_doc_parser) {
        s << m_doc_parser->documentation(java_enum);
    }

    if (java_enum->typeEntry()->forceInteger()) {
        writeIntegerEnum(s, java_enum);
        return;
    }

    // Generates Java 1.5 type enums
    s << "    public enum " << java_enum->name()
      << " implements com.trolltech.qt.QtEnumerator {" << endl;
    const MetaJavaEnumValueList &values = java_enum->values();
    EnumTypeEntry *entry = java_enum->typeEntry();

    for (int i=0; i<values.size(); ++i) {
        MetaJavaEnumValue *enum_value = values.at(i);

        if (java_enum->typeEntry()->isEnumValueRejected(enum_value->name()))
            continue;

        if (m_doc_parser)
            s << m_doc_parser->documentation(enum_value);

        s << "        " << enum_value->name() << "(" << enum_value->value() << ")";

        if (i != values.size() - 1 || entry->isExtensible()) {
            s << "," << endl;
        }
    }

    if (entry->isExtensible())
        s << "        CustomEnum(0)";

    s << ";" << endl << endl;

    s << "        " << java_enum->name() << "(int value) { this.value = value; }" << endl
      << "        public int value() { return value; }" << endl
      << endl;

    // Write out the createQFlags() function if its a QFlags enum
    if (entry->flags()) {
        FlagsTypeEntry *flags_entry = entry->flags();
        s << "        public static " << flags_entry->javaName() << " createQFlags("
          << entry->javaName() << " ... values) {" << endl
          << "            return new " << flags_entry->javaName() << "(values);" << endl
          << "        }" << endl;
    }

    // The resolve functions. The public one that returns the right
    // type and an internal one that has a generic signature. Makes it
    // easier to find the right one from JNI.
    s << "        public static " << java_enum->name() << " resolve(int value) {" << endl
      << "            return (" << java_enum->name() << ") resolve_internal(value);" << endl
      << "        }" << endl
      << "        private static Object resolve_internal(int value) {" << endl
      << "            switch (value) {" << endl;

    for (int i=0; i<values.size(); ++i) {
        MetaJavaEnumValue *e = values.at(i);

        if (java_enum->typeEntry()->isEnumValueRejected(e->name()))
            continue;

        s << "            case " << e->value() << ": return " << e->name() << ";" << endl;
    }

    s << "            }" << endl;

    if (entry->isExtensible()) {
        s << "            if (enumCache == null)" << endl
          << "                enumCache = new java.util.HashMap<Integer, " << java_enum->name()
          << ">();" << endl
          << "            " << java_enum->name() << " e = enumCache.get(value);" << endl
          << "            if (e == null) {" << endl
          << "                e = (" << java_enum->name() << ") com.trolltech.qt.QtJambiInternal.createExtendedEnum("
          << "value, CustomEnum.ordinal(), " << java_enum->name() << ".class, CustomEnum.name());"
          << endl
          << "                enumCache.put(value, e);" << endl
          << "            }" << endl
          << "            return e;" << endl;
    } else {
        s << "            throw new com.trolltech.qt.QNoSuchEnumValueException(value);" << endl;
    }


    s << "        }" << endl;

    s << "        private final int value;" << endl
      << endl;
    if (entry->isExtensible()) {
        s << "        private static java.util.HashMap<Integer, " << java_enum->name()
          << "> enumCache;";
    }
    s << "    }" << endl;

    // Write out the QFlags if present...
    FlagsTypeEntry *flags_entry = entry->flags();
    if (flags_entry) {
        QString flagsName = flags_entry->javaName();
        s << "    public static class " << flagsName << " extends com.trolltech.qt.QFlags<"
          << java_enum->name() << "> {" << endl
          << "        private static final long serialVersionUID = 1L;" << endl
          << "        public " << flagsName << "(" << flagsName << " other)"
          << " { super(other); }" << endl
          << "        public " << flagsName << "(" << java_enum->name() << " ... args)"
          << " { super(args); }" << endl
          << "        public " << flagsName << "(int value) { setValue(value); }" << endl
          << "    }" << endl << endl;
    }
}


void JavaGenerator::writePrivateNativeFunction(QTextStream &s, const MetaJavaFunction *java_function)
{

    int exclude_attributes = MetaJavaAttributes::Public | MetaJavaAttributes::Protected;
    int include_attributes = MetaJavaAttributes::Private;

    if (java_function->isEmptyFunction())
        exclude_attributes |= MetaJavaAttributes::Native;
    else
        include_attributes |= MetaJavaAttributes::Native;

    if (!java_function->isConstructor())
        include_attributes |= MetaJavaAttributes::Static;

    s << "    ";
    writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
                            EnumAsInts
                            | (java_function->isEmptyFunction()
                               || java_function->isNormal()
                               || java_function->isSignal() ? 0 : SkipReturnType));

    if (java_function->isConstructor())
        s << "void ";
    s << java_function->marshalledName();

    s << "(";

    MetaJavaArgumentList arguments = java_function->arguments();

    if (!java_function->isStatic() && !java_function->isConstructor())
        s << "long __this__nativeId";
    for (int i=0; i<arguments.count(); ++i) {
        const MetaJavaArgument *arg = arguments.at(i);

        if (!java_function->argumentRemoved(i+1)) {
            if (i > 0 || (!java_function->isStatic() && !java_function->isConstructor()))
                s << ", ";

            if (!arg->type()->hasNativeId())
                writeArgument(s, java_function, arg, EnumAsInts);
            else
                s << "long " << arg->argumentName();
        }
    }
    s << ")";

    // Make sure people don't call the private functions
    if (java_function->isEmptyFunction()) {
        s << endl
          << "    {" << endl
          << "        throw new com.trolltech.qt.QNoImplementationException();" << endl
          << "    }" << endl << endl;
    } else {
        s << ";" << endl << endl;
    }
}

static QString function_call_for_ownership(TypeSystem::Ownership owner) 
{
    if (owner == TypeSystem::CppOwnership) {
        return "disableGarbageCollection()";
    } else if (owner == TypeSystem::JavaOwnership) {
        return "setJavaOwnership()";
    } else if (owner == TypeSystem::DefaultOwnership) {
        return "reenableGarbageCollection()";

    } else {
        Q_ASSERT(false);
        return "bogus()";
    }
}

void JavaGenerator::writeOwnershipForContainer(QTextStream &s, TypeSystem::Ownership owner, 
                                               MetaJavaArgument *arg, const QString &indent)
{
    Q_ASSERT(arg->type()->isContainer());

    s << indent << "for (" << arg->type()->instantiations().at(0)->fullName() << " i : "
                << arg->argumentName() << ")" << endl
      << indent << "    if (i != null) i." << function_call_for_ownership(owner) << ";" << endl;
}

void JavaGenerator::writeJavaCallThroughContents(QTextStream &s, const MetaJavaFunction *java_function)
{
    if (java_function->implementingClass()->isQObject()
        && !java_function->isStatic()
        && !java_function->isConstructor()
        && java_function->name() != QLatin1String("thread")
        && java_function->name() != QLatin1String("disposeLater")) {
        s << "        com.trolltech.qt.QtJambiInternal.threadCheck(this);" << endl;
    }

    MetaJavaArgumentList arguments = java_function->arguments();

    if (!java_function->isConstructor()) {
        TypeSystem::Ownership owner = java_function->ownership(java_function->implementingClass(), TypeSystem::JavaCode, -1);
        if (owner != TypeSystem::InvalidOwnership)
            s << "        this." << function_call_for_ownership(owner) << ";" << endl;
    }
       
    for (int i=0; i<arguments.count(); ++i) {
        MetaJavaArgument *arg = arguments.at(i);
        MetaJavaType *type = arg->type();

        if (!java_function->argumentRemoved(i+1)) {
            TypeSystem::Ownership owner = java_function->ownership(java_function->implementingClass(), TypeSystem::JavaCode, i+1);
            if (owner != TypeSystem::InvalidOwnership) {
                s << "        "
                << "if (" << arg->argumentName() << " != null) {" << endl;

                if (arg->type()->isContainer())
                    writeOwnershipForContainer(s, owner, arg, "            ");
                else
                    s << "            " << arg->argumentName() << "." << function_call_for_ownership(owner) << ";" << endl;
                s << "        }" << endl;
            }

            if (type->isArray()) {
                s << "        "
                << "if (" << arg->argumentName() << ".length != " << type->arrayElementCount() << ")" << endl
                << "            "
                << "throw new IllegalArgumentException(\"Wrong number of elements in array. Found: \" + "
                << arg->argumentName() << ".length + \", expected: " << type->arrayElementCount() << "\");"
                << endl << endl;
            }

            if (type->isEnum()) {
                EnumTypeEntry *et = (EnumTypeEntry *) type->typeEntry();
                if (et->forceInteger()) {
                    if (!et->lowerBound().isEmpty()) {
                        s << "        if (" << arg->argumentName() << " < " << et->lowerBound() << ")" << endl
                        << "            throw new IllegalArgumentException(\"Argument " << arg->argumentName()
                        << " is less than lowerbound " << et->lowerBound() << "\");" << endl;
                    }
                    if (!et->upperBound().isEmpty()) {
                        s << "        if (" << arg->argumentName() << " > " << et->upperBound() << ")" << endl
                        << "            throw new IllegalArgumentException(\"Argument " << arg->argumentName()
                        << " is greated than upperbound " << et->upperBound() << "\");" << endl;
                    }
                }
            }
        }
    }

    if (!java_function->isConstructor() && !java_function->isStatic()) {
        s << "        if (nativeId() == 0)" << endl
          << "            throw new com.trolltech.qt.QNoNativeResourcesException(\"Function call on incomplete object of type: \" +getClass().getName());" << endl;
    }

    for (int i=0; i<arguments.size(); ++i) {
        if (java_function->nullPointersDisabled(java_function->implementingClass(), i + 1)) {
            s << "        if (" << arguments.at(i)->argumentName() << " == null)" << endl
              << "            throw new NullPointerException(\"Argument '" << arguments.at(i)->argumentName() << "': null not expected.\");" << endl;
        }
    }

    s << "        ";


    MetaJavaType *return_type = java_function->type();
    QString new_return_type = java_function->typeReplaced(0);
    bool has_return_type = new_return_type != "void"
        && (!new_return_type.isEmpty() || return_type != 0);

    if (has_return_type) {
        TypeSystem::Ownership owner = java_function->ownership(java_function->implementingClass(), TypeSystem::JavaCode, 0);
        if (owner != TypeSystem::InvalidOwnership) {
            if (new_return_type.isEmpty())
                s << translateType(return_type);
            else
                s << new_return_type.replace('$', '.');

            s << " __qt_return_value = ";
        } else {
            s << "return ";
        }

        if (return_type && return_type->isJavaEnum()) {
            s << ((EnumTypeEntry *) return_type->typeEntry())->qualifiedJavaName() << ".resolve(";
        } else if (return_type && return_type->isJavaFlags()) {
            s << "new " << return_type->typeEntry()->qualifiedJavaName() << "(";
        }
    }

    s << java_function->marshalledName() << "(";

    if (!java_function->isConstructor() && !java_function->isStatic())
        s << "nativeId()";


    for (int i=0; i<arguments.count(); ++i) {
        const MetaJavaArgument *arg = arguments.at(i);
        const MetaJavaType *type = arg->type();

        if (!java_function->argumentRemoved(i+1)) {
            if (i > 0 || (!java_function->isStatic() && !java_function->isConstructor()))
                s << ", ";

            if (type->isJavaEnum() || type->isJavaFlags()) {
                s << arg->argumentName() << ".value()";
            } else if (!type->hasNativeId()) {
                s << arg->argumentName();
            } else {
                s << arg->argumentName() << " == null ? ";
                // Try to call default constructor for value types...
                if (type->isValue() && hasDefaultConstructor(type))
                    s << "(" << arg->argumentName() << " = new " << type->typeEntry()->qualifiedJavaName() << "()).nativeId()";
                else
                    s << "0";
                s << " : " << arg->argumentName() << ".nativeId()";
            }
        }
    }
    s << ")";

    if (return_type && (return_type->isJavaEnum() || return_type->isJavaFlags()))
        s << ")";

    TypeSystem::Ownership owner = java_function->ownership(java_function->implementingClass(), TypeSystem::JavaCode, 0);
    if (has_return_type && owner != TypeSystem::InvalidOwnership) {
        s << ";" << endl
          << "        if (__qt_return_value != null) __qt_return_value." << function_call_for_ownership(owner) << ";" << endl
          << "        return __qt_return_value";
    }
    s << ";" << endl;

    if (java_function->isConstructor()) {
        TypeSystem::Ownership owner = java_function->ownership(java_function->implementingClass(), TypeSystem::JavaCode, -1);
        if (owner != TypeSystem::InvalidOwnership && java_function->isConstructor())
            s << "        this." << function_call_for_ownership(owner) << ";" << endl;
    }
}

void JavaGenerator::writeSignal(QTextStream &s, const MetaJavaFunction *java_function)
{
    Q_ASSERT(java_function->isSignal());

    if (java_function->isModifiedRemoved(TypeSystem::JavaCode))
        return ;

    MetaJavaArgumentList arguments = java_function->arguments();
    int sz = arguments.count();

    QString signalTypeName("Signal");
    signalTypeName += QString::number(sz);
    if (sz > 0) {
        signalTypeName += "<";
        for (int i=0; i<sz; ++i) {
            if (i > 0)
                signalTypeName += ", ";

            QString modifiedType = java_function->typeReplaced(i+1);

            if (modifiedType.isEmpty())
                signalTypeName += translateType(arguments.at(i)->type(), BoxedPrimitive);
            else
                signalTypeName += modifiedType;
        }
        signalTypeName += ">";
    }

    int exclude_attributes = MetaJavaAttributes::Abstract
                             | MetaJavaAttributes::Native;
    int include_attributes = MetaJavaAttributes::Public;

    QString signalName = java_function->name();
    FunctionModificationList mods = java_function->modifications(java_function->implementingClass());
    foreach (FunctionModification mod, mods) {
        if (mod.isAccessModifier()) {
            exclude_attributes |= MetaJavaAttributes::Public
                                | MetaJavaAttributes::Protected
                                | MetaJavaAttributes::Private
                                | MetaJavaAttributes::Friendly;

            if (mod.isPublic())
                include_attributes |= MetaJavaAttributes::Public;
            else if (mod.isProtected())
                include_attributes |= MetaJavaAttributes::Protected;
            else if (mod.isPrivate())
                include_attributes |= MetaJavaAttributes::Private;
            else if (mod.isFriendly())
                include_attributes |= MetaJavaAttributes::Friendly;

            exclude_attributes &= ~(include_attributes);
        }
    }

    // Insert Javadoc
    if (m_doc_parser) {
        QString signature = functionSignature(java_function,
                                              include_attributes,
                                              exclude_attributes);
        QString docs = m_doc_parser->documentationForSignal(signature);
        if (docs.isEmpty()) {
            signature.replace(QLatin1String("public"), QLatin1String("protected"));
            docs = m_doc_parser->documentationForSignal(signature);
        }
        s << m_doc_parser->documentationForSignal(signature);
    }

    s << "    ";
    writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
                            SkipReturnType);
    s << signalTypeName;
    s << " " << signalName << " = new " << signalTypeName << "();" << endl << endl;

    s << "    @SuppressWarnings(\"unused\")" << endl;
    writeFunction(s, java_function,
                  MetaJavaAttributes::Private,
                  MetaJavaAttributes::Visibility);
}

void JavaGenerator::retrieveModifications(const MetaJavaFunction *java_function,
                                          const MetaJavaClass *java_class,
                                          uint *exclude_attributes,
                                          uint *include_attributes) const
{
    FunctionModificationList mods = java_function->modifications(java_class);
    foreach (FunctionModification mod, mods) {
        if (mod.isAccessModifier()) {
            *exclude_attributes |= MetaJavaAttributes::Public
                                | MetaJavaAttributes::Protected
                                | MetaJavaAttributes::Private
                                | MetaJavaAttributes::Friendly;

            if (mod.isPublic())
                *include_attributes |= MetaJavaAttributes::Public;
            else if (mod.isProtected())
                *include_attributes |= MetaJavaAttributes::Protected;
            else if (mod.isPrivate())
                *include_attributes |= MetaJavaAttributes::Private;
            else if (mod.isFriendly())
                *include_attributes |= MetaJavaAttributes::Friendly;

            *exclude_attributes &= ~(*include_attributes);
        }
    }
}

QString JavaGenerator::functionSignature(const MetaJavaFunction *java_function,
                                         uint included_attributes, uint excluded_attributes,
                                         Option option,
                                         int arg_count)
{
    MetaJavaArgumentList arguments = java_function->arguments();
    int argument_count = arg_count < 0 ? arguments.size() : arg_count;

    QString result;
    QTextStream s(&result);
    QString functionName = java_function->name();
    // The actual function
    if (!(java_function->isEmptyFunction() || java_function->isNormal() || java_function->isSignal()))
        option = Option(option | SkipReturnType);
    writeFunctionAttributes(s, java_function, included_attributes, excluded_attributes, option);

    s << functionName << "(";
    writeFunctionArguments(s, java_function, argument_count, option);
    s << ")";

    return result;
}

void JavaGenerator::setupForFunction(const MetaJavaFunction *java_function,
                                     uint *included_attributes,
                                     uint *excluded_attributes) const
{
    *excluded_attributes |= java_function->ownerClass()->isInterface() || java_function->isConstructor()
                            ? MetaJavaAttributes::Native | MetaJavaAttributes::Final
                            : 0;
    if (java_function->ownerClass()->isInterface())
        *excluded_attributes |= MetaJavaAttributes::Abstract;
    if (java_function->needsCallThrough())
        *excluded_attributes |= MetaJavaAttributes::Native;

    const MetaJavaClass *java_class = java_function->ownerClass();
    retrieveModifications(java_function, java_class, excluded_attributes, included_attributes);
}

void JavaGenerator::writeFunction(QTextStream &s, const MetaJavaFunction *java_function,
                                  uint included_attributes, uint excluded_attributes)
{

    if (java_function->isModifiedRemoved(TypeSystem::JavaCode))
        return ;
    QString functionName = java_function->name();
    setupForFunction(java_function, &included_attributes, &excluded_attributes);

    if (!java_function->ownerClass()->isInterface()) {
        writeEnumOverload(s, java_function, included_attributes, excluded_attributes);
        writeFunctionOverloads(s, java_function, included_attributes, excluded_attributes);
    }

    QString signature = functionSignature(java_function, included_attributes, excluded_attributes);

    if (m_doc_parser) {
        s << m_doc_parser->documentationForFunction(signature) << endl;
    }

    if (((excluded_attributes & MetaJavaAttributes::Private) == 0)
        && (java_function->isPrivate()
            || ((included_attributes & MetaJavaAttributes::Private) != 0))) {
        s << "    @SuppressWarnings(\"unused\")" << endl;
    }
    s << "    ";
    s << functionSignature(java_function, included_attributes, excluded_attributes);

    if (java_function->isConstructor()) {
        writeConstructorContents(s, java_function);
    } else if (java_function->needsCallThrough()) {
        if (java_function->isAbstract()) {
            s << ";" << endl;
        } else {
            s << " {" << endl;
            writeJavaCallThroughContents(s, java_function);
            s << "    }" << endl;
        }
        writePrivateNativeFunction(s, java_function);
    } else {
        s << ";" << endl << endl;
    }
}

void JavaGenerator::writeJavaLangObjectOverrideFunctions(QTextStream &s,
                                                         const MetaJavaClass *cls)
{
    if (cls->hasEqualsOperator() && cls->hasHashFunction()) {
        MetaJavaFunctionList equal_functions = cls->queryFunctionsByName("equals");
        bool found = false;
        foreach (const MetaJavaFunction *function, equal_functions) {
            if (function->actualMinimumArgumentCount() == 1
                && function->arguments().at(0)->type()->typeEntry()->isComplex()) {
                found = true;
                break;
            }
        }

        if (!found) {
            s << endl
              << "    public boolean equals(Object other) {" << endl
              << "        if (nativeId() == 0)" << endl
              << "            throw new com.trolltech.qt.QNoNativeResourcesException(\"Function call on incomplete object of type: \" +getClass().getName());" << endl
              << "        return !(other instanceof " << cls->fullName()
              << ") ? false : __qt_equals(nativeId(), ((" << cls->fullName() << ")other).nativeId());" << endl
              << "    }" << endl
              << "    private static native boolean __qt_equals(long __this_nativeId, long other);" << endl << endl;
        }
    }

    if (cls->hasHashFunction()) {
        MetaJavaFunctionList hashcode_functions = cls->queryFunctionsByName("hashCode");
        bool found = false;
        foreach (const MetaJavaFunction *function, hashcode_functions) {
            if (function->actualMinimumArgumentCount() == 0) {
                found = true;
                break;
            }
        }

        if (!found) {
            s << endl
              << "    public int hashCode() {" << endl
              << "        if (nativeId() == 0)" << endl
              << "            throw new com.trolltech.qt.QNoNativeResourcesException(\"Function call on incomplete object of type: \" +getClass().getName());" << endl
              << "        return __qt_hashCode(nativeId());" << endl
              << "    }" << endl
              << "    private static native int __qt_hashCode(long __this_nativeId);" << endl << endl;
        }
    }

    // Qt has a standard toString() conversion in QVariant?
    QVariant::Type type = QVariant::nameToType(cls->qualifiedCppName().toLatin1());
    if (QVariant(type).canConvert(QVariant::String)) {
        MetaJavaFunctionList tostring_functions = cls->queryFunctionsByName("toString");
        bool found = false;
        foreach (const MetaJavaFunction *function, tostring_functions) {
            if (function->actualMinimumArgumentCount() == 0) {
                found = true;
                break;
            }
        }

        if (!found) {
            s << endl
              << "    public String toString() {" << endl
              << "        if (nativeId() == 0)" << endl
              << "            throw new com.trolltech.qt.QNoNativeResourcesException(\"Function call on incomplete object of type: \" +getClass().getName());" << endl
              << "        return __qt_toString(nativeId());" << endl
              << "    }" << endl
              << "    private static native String __qt_toString(long __this_nativeId);" << endl << endl;
        }
    }
}

void JavaGenerator::writeEnumOverload(QTextStream &s, const MetaJavaFunction *java_function,
                                      uint include_attributes, uint exclude_attributes)
{
    MetaJavaArgumentList arguments = java_function->arguments();

    if ((java_function->implementingClass() != java_function->declaringClass())
        || (!java_function->isNormal() || java_function->isEmptyFunction() || java_function->isAbstract())) {
        return ;
    }
    include_attributes |= MetaJavaAttributes::FinalInJava;

    int generate_enum_overload = -1;
    for (int i=0; i<arguments.size(); ++i)
        generate_enum_overload = arguments.at(i)->type()->isJavaFlags() ? i : -1;

    if (generate_enum_overload >= 0) {
        if (m_doc_parser) {
            // steal documentation from main function
            QString signature = functionSignature(java_function, include_attributes,
                                                  exclude_attributes);
            s << m_doc_parser->documentationForFunction(signature) << endl;
        }

        s << endl << "    ";
        if (((exclude_attributes & MetaJavaAttributes::Private) == 0)
            && (java_function->isPrivate()
                || (include_attributes & MetaJavaAttributes::Private) != 0)) {
            s << "@SuppressWarnings(\"unused\")" << endl << "    ";
        }

        writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes, 0);
        s << java_function->name() << "(";
        if (generate_enum_overload > 0) {
            writeFunctionArguments(s, java_function, generate_enum_overload);
            s << ", ";
        }

        // Write the ellipsis convenience argument
        MetaJavaArgument *affected_arg = arguments.at(generate_enum_overload);
        EnumTypeEntry *originator = ((FlagsTypeEntry *)affected_arg->type()->typeEntry())->originator();

        s << originator->javaPackage() << "." << originator->javaQualifier() << "." << originator->javaName()
          << " ... " << affected_arg->argumentName() << ") {" << endl;

        s << "        ";
        QString new_return_type = java_function->typeReplaced(0);
        if (new_return_type != "void" && (!new_return_type.isEmpty() || java_function->type() != 0))
            s << "return ";

        if (java_function->isStatic())
            s << java_function->implementingClass()->fullName() << ".";
        else
            s << "this.";

        s << java_function->name() << "(";
        for (int i=0; i<generate_enum_overload; ++i) {
            s << arguments.at(i)->argumentName() << ", ";
        }
        s << "new " << affected_arg->type()->fullName() << "(" << affected_arg->argumentName() << "));" << endl
          << "    }" << endl;
    }
}

void JavaGenerator::writeFunctionOverloads(QTextStream &s, const MetaJavaFunction *java_function,
                                           uint include_attributes, uint exclude_attributes)
{
    MetaJavaArgumentList arguments = java_function->arguments();
    int argument_count = arguments.size();

    // We only create the overloads for the class that actually declares the function
    // unless this is an interface, in which case we create the overloads for all
    // classes that directly implement the interface.
    const MetaJavaClass *decl_class = java_function->declaringClass();
    if (decl_class->isInterface()) {
        MetaJavaClassList interfaces = java_function->implementingClass()->interfaces();
        foreach (MetaJavaClass *iface, interfaces) {
            if (iface == decl_class) {
                decl_class = java_function->implementingClass();
                break;
            }
        }
    }
    if (decl_class != java_function->implementingClass())
        return;

    // Figure out how many functions we need to write out,
    // One extra for each default argument.
    int overload_count = 0;
    uint excluded_attributes = MetaJavaAttributes::Abstract
                            | MetaJavaAttributes::Native
                            | exclude_attributes;
    uint included_attributes = (java_function->isConstructor() ? 0 : MetaJavaAttributes::Final) | include_attributes;

    for (int i=0; i<argument_count; ++i) {
        if (!arguments.at(i)->defaultValueExpression().isEmpty() && !java_function->argumentRemoved(i+1))
            ++overload_count;
    }
    Q_ASSERT(overload_count <= argument_count);
    for (int i=0; i<overload_count; ++i) {
        int used_arguments = argument_count - i - 1;

        QString signature = functionSignature(java_function, included_attributes,
                                              excluded_attributes,
                                              java_function->isEmptyFunction()
                                              || java_function->isNormal()
                                              || java_function->isSignal() ? NoOption
                                                                           : SkipReturnType,
                                              used_arguments);

        if (m_doc_parser) {
            s << m_doc_parser->documentationForFunction(signature) << endl;
        }

        if (((excluded_attributes & MetaJavaAttributes::Private) == 0)
            && (java_function->isPrivate()
                || (included_attributes & MetaJavaAttributes::Private) != 0)) {
            s << endl << "    @SuppressWarnings(\"unused\")";
        }

        s << "\n    " << signature << " {\n        ";
        QString new_return_type = java_function->typeReplaced(0);
        if (new_return_type != "void" && (!new_return_type.isEmpty() || java_function->type()))
            s << "return ";
        if (java_function->isConstructor())
            s << "this";
        else
            s << java_function->name();
        s << "(";

        int written_arguments = 0;
        for (int j=0; j<argument_count; ++j) {
            if (!java_function->argumentRemoved(j+1)) {
                if (written_arguments++ > 0)
                    s << ", ";

                if (j < used_arguments) {
                    s << arguments.at(j)->argumentName();
                } else {
                    MetaJavaType *arg_type = 0;
                    QString modified_type = java_function->typeReplaced(j+1);
                    if (modified_type.isEmpty()) {
                        arg_type = arguments.at(j)->type();
                        if (arg_type->isNativePointer()) {
                            s << "(com.trolltech.qt.QNativePointer)";
                        } else {
                            const TypeEntry *type = arguments.at(j)->type()->typeEntry();
                            if (type->designatedInterface())
                                type = type->designatedInterface();
                            if (!type->isEnum() && !type->isFlags())
                                s << "(" << type->qualifiedJavaName() << ")";
                        }
                    } else {
                        s << "(" << modified_type.replace('$', '.') << ")";
                    }

                    QString defaultExpr = arguments.at(j)->defaultValueExpression();

                    int pos = defaultExpr.indexOf(".");
                    if (pos > 0) {
                        QString someName = defaultExpr.left(pos);
                        ComplexTypeEntry *ctype =
                            TypeDatabase::instance()->findComplexType(someName);
                        QString replacement;
                        if (ctype != 0 && ctype->isVariant())
                            replacement = "com.trolltech.qt.QVariant.";
                        else if (ctype != 0)
                            replacement = ctype->javaPackage() + "." + ctype->javaName() + ".";
                        else
                            replacement = someName + ".";
                        defaultExpr = defaultExpr.replace(someName + ".", replacement);
                    }

                    if (arg_type != 0 && arg_type->isFlags()) {
                        s << "new " << arg_type->fullName() << "(" << defaultExpr << ")";
                    } else {
                        s << defaultExpr;
                    }
                }
            }
        }
        s << ");\n    }" << endl;
    }
}

void JavaGenerator::write(QTextStream &s, const MetaJavaClass *java_class)
{
    ReportHandler::debugSparse("Generating class: " + java_class->fullName());

    if (m_docs_enabled) {
        m_doc_parser = new DocParser(m_doc_directory + "/" + java_class->name().toLower() + ".jdoc");
    }

    s << "package " << java_class->package() << ";" << endl << endl;

    QList<Include> includes = java_class->typeEntry()->extraIncludes();
    foreach (const Include &inc, includes) {
        if (inc.type == Include::JavaImport) {
            s << inc.toString() << endl;
        }
    }

    s << endl;

    if (m_doc_parser) {
        s << m_doc_parser->documentation(java_class) << endl << endl;
    }

    s << "@com.trolltech.qt.QtJambiGeneratedClass" << endl;

    if (java_class->isInterface()) {
        s << "public interface ";
    } else {
        if (java_class->isPublic())
            s << "public ";
        // else friendly

        if (java_class->isFinal())
            s << "final ";


        if (java_class->isNamespace()) {
            s << "interface ";
        } else {
            if (java_class->isAbstract())
                s << "abstract ";
            s << "class ";
        }

    }

    const ComplexTypeEntry *type = java_class->typeEntry();

    s << java_class->name();

    if (!java_class->isNamespace() && !java_class->isInterface()) {
        if (!java_class->baseClassName().isEmpty()) {
            s << " extends " << java_class->baseClass()->fullName();
        } else {
            QString sc = type->defaultSuperclass();

            if (!sc.isEmpty())
                s << " extends " << sc;
        }
    } else if (java_class->isInterface()) {
        s << " extends com.trolltech.qt.QtJambiInterface";
    }

    // implementing interfaces...
    MetaJavaClassList interfaces = java_class->interfaces();
    if (!interfaces.isEmpty()) {
        if (java_class->isInterface())
            s << ", ";
        else
            s << endl << "    implements ";
        for (int i=0; i<interfaces.size(); ++i) {
            MetaJavaClass *iface = interfaces.at(i);
            if (i != 0)
                s << "," << endl << "            ";
            s << iface->package() << "." << iface->name();
        }
    }

    s << endl << "{" << endl;


    if (!java_class->isInterface() && !java_class->isNamespace()
        && (java_class->baseClass() == 0 || java_class->package() != java_class->baseClass()->package())) {
        s << "    static {" << endl
          << "        " << java_class->package() << ".QtJambi_LibraryInitializer.init();" << endl
          << "    }" << endl;
    }

    if (!java_class->isInterface() && java_class->isAbstract()) {
        s << "    @SuppressWarnings(\"unused\")" << endl
          << "    private static class ConcreteWrapper extends " << java_class->fullName() << " {" << endl
          << "        protected ConcreteWrapper(QPrivateConstructor p) { super(p); }" << endl;

        uint exclude_attributes = MetaJavaAttributes::Native | MetaJavaAttributes::Abstract;
        uint include_attributes = 0;
        MetaJavaFunctionList functions = java_class->queryFunctions(MetaJavaClass::NormalFunctions | MetaJavaClass::AbstractFunctions | MetaJavaClass::NonEmptyFunctions | MetaJavaClass::NotRemovedFromJava);
        foreach (const MetaJavaFunction *java_function, functions) {
            retrieveModifications(java_function, java_class, &exclude_attributes, &include_attributes);

            s << "        ";
            writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
                java_function->isNormal() || java_function->isSignal() ? 0 : SkipReturnType);

            s << java_function->name() << "(";
            writeFunctionArguments(s, java_function, java_function->arguments().count());
            s << ") {" << endl;
            writeJavaCallThroughContents(s, java_function);
            s << "        }" << endl;
        }
        s  << "    }" << endl << endl;
    }

    // Enums
    foreach (MetaJavaEnum *java_enum, java_class->enums())
        writeEnum(s, java_enum);
    if (!java_class->enums().isEmpty() && !java_class->functions().isEmpty())
        s << endl;

    // Signals
    MetaJavaFunctionList signal_funcs = java_class->queryFunctions(MetaJavaClass::Signals
                                                                   | MetaJavaClass::ClassImplements
                                                                   | MetaJavaClass::Visible
                                                                   | MetaJavaClass::NotRemovedFromJava);
    for (int i=0; i<signal_funcs.size(); ++i)
        writeSignal(s, signal_funcs.at(i));

    // Functions
    MetaJavaFunctionList java_funcs = java_class->functionsInJava();
    for (int i=0; i<java_funcs.size(); ++i) {
        MetaJavaFunction *function = java_funcs.at(i);
        writeFunction(s, function);
        if (function->isConstructor() && i != java_funcs.size() - 1)
            s << endl;
    }

    // Just the private functions for abstract functions implemeneted in superclasses
    if (!java_class->isInterface() && java_class->isAbstract()) {
        java_funcs = java_class->queryFunctions(MetaJavaClass::NormalFunctions | MetaJavaClass::AbstractFunctions | MetaJavaClass::NotRemovedFromJava);
        foreach (MetaJavaFunction *java_function, java_funcs) {
            if (java_function->implementingClass() != java_class) {
                writePrivateNativeFunction(s, java_function);
                s << endl;
            }
        }
    }

    // Field accessors
    MetaJavaFieldList fields = java_class->fields();
    foreach (const MetaJavaField *field, fields) {
        if (field->wasPublic() || (field->wasProtected() && !java_class->isFinal()))
            writeFieldAccessors(s, field);
    }

    // the static fromNativePointer function...
    if (!java_class->isNamespace() && !java_class->isInterface()) {
        s << endl
          << "    public static native " << java_class->name() << " fromNativePointer("
          << "com.trolltech.qt.QNativePointer nativePointer);" << endl;
    }

    // The __qt_signalInitialization() function
    if (signal_funcs.size() > 0) {
        s << endl
          << "   @Override" << endl
          << "   protected boolean __qt_signalInitialization(String name) {" << endl
          << "       return (__qt_signalInitialization(nativeId(), name)" << endl
          << "               || super.__qt_signalInitialization(name));" << endl
          << "   } " << endl
          << "   private native boolean __qt_signalInitialization(long ptr, String name);" << endl;
    }

    // Add dummy constructor for use when constructing subclasses
    if (!java_class->isNamespace() && !java_class->isInterface()) {
        s << endl
          << "    "
          << "protected "
          << java_class->name()
          << "(QPrivateConstructor p) { super(p); } "
          << endl << endl;
    }

    // Add a function that converts an array of the value type to a QNativePointer
    if (java_class->typeEntry()->isValue()) {
        s << endl
          << "    public static native com.trolltech.qt.QNativePointer nativePointerArray(" << java_class->name()
          << " array[]);" << endl;
    }

    // write the cast to this function....
    if (java_class->isInterface()) {
        s << endl
          << "    public long __qt_cast_to_"
          << static_cast<const InterfaceTypeEntry *>(type)->origin()->javaName()
          << "(long ptr);" << endl;
    } else {
        foreach (MetaJavaClass *cls, interfaces) {
            s << endl
              << "    public native long __qt_cast_to_"
              << static_cast<const InterfaceTypeEntry *>(cls->typeEntry())->origin()->javaName()
              << "(long ptr);" << endl;
        }
    }

    writeJavaLangObjectOverrideFunctions(s, java_class);
    writeExtraFunctions(s, java_class);

    s << "}" << endl;

    if (m_docs_enabled) {
        delete m_doc_parser;
        m_doc_parser = 0;
    }
}

void JavaGenerator::generate()
{
    Generator::generate();

    const MetaJavaClass *last_class = 0;
    if (!m_nativepointer_functions.isEmpty()) {
        QFile file("mjb_nativepointer_api.log");
        if (file.open(QFile::WriteOnly)) {
            QTextStream s(&file);

            MetaJavaFunctionList nativepointer_functions;
            for (int i=0; i<m_nativepointer_functions.size(); ++i) {
                MetaJavaFunction *f = const_cast<MetaJavaFunction *>(m_nativepointer_functions[i]);
                if (f->ownerClass() == f->declaringClass() || f->isFinal())
                    nativepointer_functions.append(f);
            }

            s << "Number of public or protected functions with QNativePointer API: " << nativepointer_functions.size() << endl;
            foreach (const MetaJavaFunction *f, nativepointer_functions) {
                if (last_class != f->ownerClass()) {
                    last_class = f->ownerClass();
                    s << endl << endl<< "Class " << last_class->name() << ":" << endl;
                    s << "---------------------------------------------------------------------------------"
                    << endl;
                }

                s << f->minimalSignature() << endl;
            }

            m_nativepointer_functions.clear();
        }
    }
}

void JavaGenerator::writeFunctionAttributes(QTextStream &s, const MetaJavaFunction *java_function,
                                            uint included_attributes, uint excluded_attributes,
                                            uint options)
{
    uint attr = java_function->attributes() & (~excluded_attributes) | included_attributes;

    if ((attr & MetaJavaAttributes::Public) || (attr & MetaJavaAttributes::Protected)) {

        bool nativePointer = java_function->type() && java_function->type()->isNativePointer()
                             && java_function->typeReplaced(0).isEmpty();

        if (!nativePointer && java_function->type()
            && java_function->type()->hasInstantiations() && java_function->typeReplaced(0).isEmpty()) {
            QList<MetaJavaType *> instantiations = java_function->type()->instantiations();

            foreach (const MetaJavaType *type, instantiations) {
                if (type && type->isNativePointer()) {
                    nativePointer = true;
                    break;
                }
            }
        }

        MetaJavaArgumentList arguments = java_function->arguments();
        if (!nativePointer) foreach (const MetaJavaArgument *argument, arguments) {
            if (!java_function->argumentRemoved(argument->argumentIndex()+1)
                && java_function->typeReplaced(argument->argumentIndex()+1).isEmpty()) {

                if (argument->type()->isNativePointer()) {
                    nativePointer = true;
                    break ;
                } else if (argument->type()->hasInstantiations()) {
                    QList<MetaJavaType *> instantiations = argument->type()->instantiations();
                    foreach (MetaJavaType *type, instantiations) {
                        if (type && type->isNativePointer()) {
                            nativePointer = true;
                            break;
                        }
                    }
                    if (nativePointer)
                        break;
                }
             }
        }

        if (nativePointer && !m_nativepointer_functions.contains(java_function))
            m_nativepointer_functions.append(java_function);
    }

    if ((options & SkipAttributes) == 0) {
        if (java_function->isEmptyFunction()) s << "@Deprecated ";

        if (attr & MetaJavaAttributes::Public) s << "public ";
        else if (attr & MetaJavaAttributes::Protected) s << "protected ";
        else if (attr & MetaJavaAttributes::Private) s << "private ";

        if (attr & MetaJavaAttributes::Native) s << "native ";
        else if (attr & MetaJavaAttributes::FinalInJava) s << "final ";
        else if (attr & MetaJavaAttributes::Abstract) s << "abstract ";

        if (attr & MetaJavaAttributes::Static) s << "static ";
    }

    if ((options & SkipReturnType) == 0) {
        QString modified_type = java_function->typeReplaced(0);
        if (modified_type.isEmpty())
            s << translateType(java_function->type(), (Option) options);
        else
            s << modified_type.replace('$', '.');
        s << " ";
    }
}

void JavaGenerator::writeConstructorContents(QTextStream &s, const MetaJavaFunction *java_function)
{
    // Write constructor
    s << " {" << endl
      << "        super((QPrivateConstructor)null);" << endl;

    writeJavaCallThroughContents(s, java_function);

    // Write out expense checks if present...
    const MetaJavaClass *java_class = java_function->implementingClass();
    const ComplexTypeEntry *te = java_class->typeEntry();
    if (te->expensePolicy().isValid()) {
        s << endl;
        const ExpensePolicy &ep = te->expensePolicy();
        s << "        com.trolltech.qt.QtJambiInternal.countExpense(" << java_class->fullName()
          << ".class, " << ep.cost << ", " << ep.limit << ");" << endl;
    }

    s << "    }" << endl;

    // Write native constructor
    writePrivateNativeFunction(s, java_function);
}

void JavaGenerator::writeFunctionArguments(QTextStream &s, const MetaJavaFunction *java_function,
                                           int argument_count, uint options)
{
    MetaJavaArgumentList arguments = java_function->arguments();

    if (argument_count == -1)
        argument_count = arguments.size();

    for (int i=0; i<argument_count; ++i) {
        if (!java_function->argumentRemoved(i+1)) {
            if (i != 0)
                s << ", ";
            writeArgument(s, java_function, arguments.at(i), options);
        }
    }

}


void JavaGenerator::writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class)
{
    const ComplexTypeEntry *class_type = java_class->typeEntry();
    Q_ASSERT(class_type);

    CodeSnipList code_snips = class_type->codeSnips();
    foreach (const CodeSnip &snip, code_snips) {
        if (snip.language == TypeSystem::JavaCode) {
            s << snip.code() << endl;
        }
    }
}

bool JavaGenerator::hasDefaultConstructor(const MetaJavaType *type)
{
    QString full_name = type->typeEntry()->qualifiedJavaName();
    QString class_name = type->typeEntry()->javaName();

    foreach (const MetaJavaClass *java_class, m_java_classes) {
        if (java_class->typeEntry()->qualifiedJavaName() == full_name) {
            MetaJavaFunctionList functions = java_class->functions();
            foreach (const MetaJavaFunction *function, functions) {
                if (function->arguments().size() == 0 && function->name() == class_name)
                    return true;
            }
            return false;
        }
    }
    return false;
}
