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

#include <QDir>
#include <QTextStream>
#include <QDebug>

JavaGenerator::JavaGenerator()
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
                QString("Class '%1' has setter '%2' for public field '%3'")
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
                QString("Class '%1' has getter '%2' for public field '%3'")
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
        if (option & BoxedPrimitive)
            s = "java.lang.Integer";
        else
            s = "int";
    } else {
        if (java_type->isPrimitive() && (option & BoxedPrimitive)) {
            s = static_cast<const PrimitiveTypeEntry *>(java_type->typeEntry())->javaObjectFullName();

        } else if (java_type->isNativePointer()) {
            s = "QNativePointer";

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

void JavaGenerator::writeArgument(QTextStream &s, const MetaJavaVariable *java_variable,
                                  uint options)
{
    s << translateType(java_variable->type(), (Option) options);

    if ((options & SkipName) == 0)
        s << " " << java_variable->name();
}

void JavaGenerator::writeEnum(QTextStream &s, const MetaJavaEnum *java_enum)
{
    const MetaJavaEnumValueList &values = java_enum->values();

    s << "    // enum " << java_enum->name() << endl;
    for (int i=0; i<values.size(); ++i) {
        MetaJavaEnumValue *enum_value = values.at(i);
        s << "    public static final int " << enum_value->name() << " = ";
        if (enum_value->value().isEmpty()) {
            if (i == 0)
                s << 0;
            else
                s << values.at(i-1)->name() << " + 1";
        } else {
            QString expr = enum_value->value().replace("::", ".");

            int pos = expr.indexOf(".");
            if (pos > 0) {
                QString someName = expr.left(pos);

                ComplexTypeEntry *ctype = TypeDatabase::instance()->findComplexType(someName);
                if (ctype != 0)
                    expr = expr.replace(someName + ".", ctype->javaPackage() + "." + someName + ".");
            }
            s << expr;
        }

        s << ";";

        s << endl;
    }

    s << endl;
}

#if 0
void JavaGenerator::write1_dot_5_enum(QTextStream &s, const MetaJavaEnum *java_enum)
{
    // Generates Java 1.5 type enums
    s << "    public enum " << java_enum->name() << " {" << endl;
    const MetaJavaEnumValueList &values = java_enum->values();

    for (int i=0; i<values.size(); ++i) {
        MetaJavaEnumValue *enum_value = values.at(i);
        s << "        " << enum_value->name();
        s << "(";
        if (enum_value->value().isEmpty()) {
            if (i == 0)
                s << 0;
            else
                s << values.at(i-1)->name() << ".value() + 1";
        } else
            s << enum_value->value();
        s << ")";

        if (i != values.size() - 1) {
            s << ",";
            s << endl;
        }
    }

    s << ";" << endl << endl
      << "        " << java_enum->name() << "(int value) { this.value = value; }" << endl
      << "        " << java_enum->name() << "(" << java_enum->name() << " other) { "
      << "this.value = other.value; }" << endl
      << "        private final int value;" << endl
      << "        public int value() { return value; }" << endl;

    s << "    }" << endl;
}
#endif


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
        java_function->isEmptyFunction() || java_function->isNormal() || java_function->isSignal() ? 0 : SkipReturnType);

    if (java_function->isConstructor())
        s << "void ";
    s << java_function->marshalledName();

    s << "(";

    MetaJavaArgumentList arguments = java_function->arguments();

    if (!java_function->isStatic() && !java_function->isConstructor())
        s << "long __this__nativeId";
    for (int i=0; i<arguments.count(); ++i) {
        const MetaJavaArgument *arg = arguments.at(i);

        if (i > 0 || (!java_function->isStatic() && !java_function->isConstructor()))
            s << ", ";

        if (!arg->type()->hasNativeId())
            writeArgument(s, arg);
        else
            s << "long " << arg->name();
    }
    s << ")";

    // Make sure people don't call the private functions
    if (java_function->isEmptyFunction()) {
        s << endl
          << "    {" << endl
          << "        throw new QNoImplementationException();" << endl
          << "    }" << endl << endl;
    } else {
        s << ";" << endl << endl;
    }
}

void JavaGenerator::writeDisableGCForContainer(QTextStream &s, MetaJavaArgument *arg,
                                               const QString &indent)
{
    Q_ASSERT(arg->type()->isContainer());

    s << indent << "for (" << arg->type()->instantiations().at(0)->fullName() << " i : "
                << arg->name() << ")" << endl
      << indent << "    if (i != null) i.disableGarbageCollection();" << endl;
}

void JavaGenerator::writeJavaCallThroughContents(QTextStream &s, const MetaJavaFunction *java_function,
                                                 const QHash<int, bool> &disabled_gc_arguments)
{
    MetaJavaArgumentList arguments = java_function->arguments();

    if (disabled_gc_arguments.value(0, false) && !java_function->isConstructor())
        s << "        this.disableGarbageCollection();" << endl;

    for (int i=0; i<arguments.count(); ++i) {
        MetaJavaArgument *arg = arguments.at(i);
        MetaJavaType *type = arg->type();

        if (disabled_gc_arguments.value(i + 1, false)) {
            s << "        "
              << "if (" << arg->name() << " != null) {" << endl;

            if (arg->type()->isContainer())
                writeDisableGCForContainer(s, arg, "            ");
            else
                s << "            " << arg->name() << ".disableGarbageCollection();" << endl;
            s << "        }" << endl;
        }

        if (type->isArray()) {
            s << "        "
              << "if (" << arg->name() << ".length != " << type->arrayElementCount() << ")" << endl
              << "            "
              << "throw new IllegalArgumentException(\"Wrong number of elements in array. Found: \" + "
              << arg->name() << ".length + \", expected: " << type->arrayElementCount() << "\");"
              << endl << endl;
        }

        if (type->isEnum()) {
            EnumTypeEntry *et = (EnumTypeEntry *) type->typeEntry();
            if (!et->lowerBound().isEmpty()) {
                s << "        if (" << arg->name() << " < " << et->lowerBound() << ")" << endl
                  << "            throw new IllegalArgumentException(\"Argument " << arg->name()
                  << " is less than lowerbound " << et->lowerBound() << "\");" << endl;
            }
            if (!et->upperBound().isEmpty()) {
                s << "        if (" << arg->name() << " > " << et->upperBound() << ")" << endl
                  << "            throw new IllegalArgumentException(\"Argument " << arg->name()
                  << " is greated than upperbound " << et->upperBound() << "\");" << endl;
            }
        }
    }

    if (!java_function->isConstructor() && !java_function->isStatic()) {
        s << "        if (nativeId() == 0)" << endl
          << "            throw new QNoNativeResourcesException(\"Function call on incomplete object\");" << endl;
    }


    s << "        ";
    if (java_function->type())
        s << "return ";
    s << java_function->marshalledName() << "(";

    if (!java_function->isConstructor() && !java_function->isStatic())
        s << "nativeId()";


    for (int i=0; i<arguments.count(); ++i) {
        const MetaJavaArgument *arg = arguments.at(i);
        const MetaJavaType *type = arg->type();

        if (i > 0 || (!java_function->isStatic() && !java_function->isConstructor()))
            s << ", ";

        if (!type->hasNativeId()) {
            s << arg->name();
        } else {
            s << arg->name() << " == null ? ";
            // Try to call default constructor for value types...
            if (type->isValue() && hasDefaultConstructor(type))
                s << "new " << type->typeEntry()->qualifiedJavaName() << "().nativeId()";
            else
                s << "0";
            s << " : " << arg->name() << ".nativeId()";
        }
    }
    s << ");" << endl;

    if (disabled_gc_arguments.value(0, false) && java_function->isConstructor())
        s << "        this.disableGarbageCollection();" << endl;
}

void JavaGenerator::writeSignal(QTextStream &s, const MetaJavaFunction *java_function)
{
    Q_ASSERT(java_function->isSignal());

    if (java_function->isModifiedRemoved(MetaJavaFunction::JavaFunction))
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
            signalTypeName += translateType(arguments.at(i)->type(), BoxedPrimitive);
        }
        signalTypeName += ">";
    }

    int exclude_attributes = MetaJavaAttributes::Abstract
        | MetaJavaAttributes::Native
        | MetaJavaAttributes::Final;
    int include_attributes = MetaJavaAttributes::Public;

    QString signalName = java_function->name();
    FunctionModificationList mods = java_function->modifications(java_function->implementingClass());
    foreach (FunctionModification mod, mods) {
        if (mod.language == CodeSnip::JavaCode) {
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
    }

    s << "    ";
    writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
                            SkipReturnType);
    s << signalTypeName;
    s << " " << signalName << ";" << endl << endl;

    writeFunction(s, java_function,
                  MetaJavaAttributes::Private,
                  MetaJavaAttributes::Visibility);
}

void JavaGenerator::retrieveModifications(const MetaJavaFunction *java_function, const MetaJavaClass *java_class,
                                          QHash<int, bool> *disabled_params, int *exclude_attributes, int *include_attributes) const
{    
    FunctionModificationList mods = java_function->modifications(java_class);
    foreach (FunctionModification mod, mods) {
        if (mod.language == CodeSnip::JavaCode) {
            if (mod.isDisableGCModifier())
                disabled_params->unite(mod.disable_gc_argument_indexes);

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
}

void JavaGenerator::writeFunction(QTextStream &s, const MetaJavaFunction *java_function,
                                  uint included_attributes, uint excluded_attributes)
{
    MetaJavaArgumentList arguments = java_function->arguments();
    const MetaJavaClass *java_class = java_function->ownerClass();
    int argument_count = arguments.size();

    if (java_function->isModifiedRemoved(MetaJavaFunction::JavaFunction))
        return ;

    bool callThrough = java_function->needsCallThrough();

    int exclude_attributes = java_class->isInterface() || java_function->isConstructor()
                            ? MetaJavaAttributes::Native
                            | MetaJavaAttributes::Final
                            : 0;
    if (java_class->isInterface())
        exclude_attributes |= MetaJavaAttributes::Abstract;

    int include_attributes = included_attributes;

    exclude_attributes |= excluded_attributes;
    if (callThrough)
        exclude_attributes |= MetaJavaAttributes::Native;

    QString functionName = java_function->name();    
    QHash<int, bool> disabled_params;
    retrieveModifications(java_function, java_class, &disabled_params, &exclude_attributes, &include_attributes);
    if (!java_class->isInterface()) {
        // The overloads
        writeFunctionOverloads(s, java_function, include_attributes, exclude_attributes);
    }

    // The actual function
    s << "    ";
    writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
        java_function->isEmptyFunction() || java_function->isNormal() || java_function->isSignal() ? 0 : SkipReturnType);


    s << functionName << "(";
    writeFunctionArguments(s, java_function, argument_count);
    s << ")";

    if (java_function->isConstructor()) {
        writeConstructorContents(s, java_function, disabled_params);
    } else if (callThrough) {
        if (java_function->isAbstract()) {
            s << ";" << endl;
        } else {
            s << " {" << endl;
            writeJavaCallThroughContents(s, java_function, disabled_params);
            s << "    }" << endl;
        }
        writePrivateNativeFunction(s, java_function);
    } else {
        s << ";" << endl << endl;
    }

    if (functionName == "operator_equal") {

        if (argument_count != 1
            || arguments.at(0)->type()->name() != java_class->name()
            || java_class->hasFunction("equals"))
            return;

        s << "    public boolean equals(Object other) {" << endl
          << "        if (other != null && other instanceof " << java_class->name() << ")" << endl
          << "            return operator_equal((" << java_class->name() << ") other);" << endl
          << "        return false;" << endl
          << "    }" << endl;
    }

}

void JavaGenerator::writeFunctionOverloads(QTextStream &s, const MetaJavaFunction *java_function,
                                           uint include_attributes, uint exclude_attributes)
{
    MetaJavaArgumentList arguments = java_function->arguments();
    int argument_count = arguments.size();

    // Figure out how many functions we need to write out,
    // One extra for each default argument.
    int overload_count = 0;
    uint excluded_attributes = MetaJavaAttributes::Abstract
                            | MetaJavaAttributes::Native
                            | exclude_attributes;

    for (int i=0; i<argument_count; ++i) {
        if (!arguments.at(i)->defaultValueExpression().isEmpty())
            ++overload_count;
    }
    Q_ASSERT(overload_count <= argument_count);
    for (int i=0; i<overload_count; ++i) {
        int used_arguments = argument_count - i - 1;
        s << "\n    ";
        writeFunctionAttributes(s, java_function, include_attributes, excluded_attributes,
            java_function->isEmptyFunction() || java_function->isNormal() || java_function->isSignal() ? 0 : SkipReturnType);
        s << java_function->name() << "(";
        writeFunctionArguments(s, java_function, used_arguments);
        s << ") {\n        ";
        if (java_function->type())
            s << "return ";
        if (java_function->isConstructor())
            s << "this";
        else
            s << java_function->name();
        s << "(";
        for (int j=0; j<argument_count; ++j) {
            if (j != 0)
                s << ", ";
            if (j < used_arguments) {
                s << arguments.at(j)->name();
            } else {

                MetaJavaType *arg_type = arguments.at(j)->type();
                if (arg_type->isNativePointer()) {
                    s << "(QNativePointer)";
                } else {
                    const TypeEntry *type = arguments.at(j)->type()->typeEntry();
                    if (type->designatedInterface())
                        type = type->designatedInterface();
                    if (!type->isEnum() && !type->isFlags())
                        s << "(" << type->qualifiedJavaName() << ")";
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
                s << defaultExpr;
            }
        }
        s << ");\n    }" << endl;
    }
}

void JavaGenerator::write(QTextStream &s, const MetaJavaClass *java_class)
{
    ReportHandler::debugSparse("Generating class: " + java_class->name());

    s << "package " << java_class->package() << ";" << endl << endl
      << "import com.trolltech.qt.*;" << endl;

    QList<Include> includes = java_class->typeEntry()->extraIncludes();
    foreach (const Include &inc, includes) {
        if (inc.type == Include::JavaImport) {
            s << inc.toString() << endl;
        }
    }

    s << endl;

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
        s << " extends QtObjectInterface";
    }

    // implementing interfaces...
    MetaJavaClassList interfaces = java_class->interfaces();
    if (!interfaces.isEmpty()) {
        s << endl << "    implements ";
        for (int i=0; i<interfaces.size(); ++i) {
            MetaJavaClass *iface = interfaces.at(i);
            if (i != 0)
                s << endl << "            ";
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
        s << "    private static class ConcreteWrapper extends " << java_class->fullName() << " {" << endl
          << "        protected ConcreteWrapper(QPrivateConstructor p) { super(p); }" << endl;

        int exclude_attributes = MetaJavaAttributes::Native | MetaJavaAttributes::Abstract;
        int include_attributes = 0;
        MetaJavaFunctionList functions = java_class->queryFunctions(MetaJavaClass::NormalFunctions | MetaJavaClass::AbstractFunctions | MetaJavaClass::NonEmptyFunctions);
        foreach (const MetaJavaFunction *java_function, functions) {
            QHash<int, bool> disabled_params;
            retrieveModifications(java_function, java_class, &disabled_params, &exclude_attributes, &include_attributes);

            s << "        ";
            writeFunctionAttributes(s, java_function, include_attributes, exclude_attributes,
                java_function->isNormal() || java_function->isSignal() ? 0 : SkipReturnType);

            s << java_function->name() << "(";
            writeFunctionArguments(s, java_function, java_function->arguments().count());
            s << ") {" << endl;
            writeJavaCallThroughContents(s, java_function, disabled_params);
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
                                                                   | MetaJavaClass::Visible);
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
        java_funcs = java_class->queryFunctions(MetaJavaClass::NormalFunctions | MetaJavaClass::AbstractFunctions);
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
    if (!java_class->isNamespace() && !java_class->isInterface()
        && !type->isObject()) {
        s << endl
          << "    public static native " << java_class->name() << " fromNativePointer("
          << "QNativePointer nativePointer);" << endl;
    }

    // The __qt_signalInitialization() function
    if (signal_funcs.size() > 0) {
        s << endl
          << "   protected void __qt_signalInitialization() { __qt_no_notify = true; __qt_signalInitialization(nativeId()); __qt_no_notify = false; m_cpp_signals_initialized = true; } " << endl
          << "   private native void __qt_signalInitialization(long ptr);" << endl;
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
          << "    public static native QNativePointer nativePointerArray(" << java_class->name()
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

    writeExtraFunctions(s, java_class);

    s << "}" << endl;
}


void JavaGenerator::writeFunctionAttributes(QTextStream &s, const MetaJavaFunction *java_function,
                                            uint included_attributes, uint excluded_attributes,
                                            uint options)
{
    uint attr = java_function->attributes() & (~excluded_attributes) | included_attributes;

    if ((attr & MetaJavaAttributes::Public) || (attr & MetaJavaAttributes::Protected)) {

        bool nativePointer = java_function->type() && java_function->type()->isNativePointer();
        MetaJavaArgumentList arguments = java_function->arguments();
        foreach (const MetaJavaArgument *argument, arguments) {
            if (argument->type()->isNativePointer() || nativePointer) {
                nativePointer = true;
                break ;
            }
        }

//         if (nativePointer) {
//             QString warning = QString("Public API function '%1' in class '%2' has QNativePointer argument/return type")
//                 .arg(java_function->signature(), java_function->ownerClass()->name());
//             ReportHandler::warning(warning);
//         }
    }

    if (attr & MetaJavaAttributes::Public) s << "public ";
    else if (attr & MetaJavaAttributes::Protected) s << "protected ";
    else if (attr & MetaJavaAttributes::Private) s << "private ";

    if (attr & MetaJavaAttributes::Native) s << "native ";
    else if (attr & MetaJavaAttributes::FinalInJava) s << "final ";
    else if (attr & MetaJavaAttributes::Abstract) s << "abstract ";

    if (attr & MetaJavaAttributes::Static) s << "static ";    

    if ((options & SkipReturnType) == 0) {
        s << translateType(java_function->type());
        s << " ";
    }
}

void JavaGenerator::writeConstructorContents(QTextStream &s, const MetaJavaFunction *java_function,
                                             const QHash<int, bool> &disabled_gc_arguments)
{
    // Write constructor
    s << " {" << endl
      << "        super((QPrivateConstructor)null);" << endl;

    writeJavaCallThroughContents(s, java_function, disabled_gc_arguments);
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
        if (i != 0)
            s << ", ";
        writeArgument(s, arguments.at(i), options);
    }

}


void JavaGenerator::writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class)
{
    const ComplexTypeEntry *class_type = java_class->typeEntry();
    Q_ASSERT(class_type);

    CodeSnipList code_snips = class_type->codeSnips();
    foreach (const CodeSnip &snip, code_snips) {
        if (snip.language == CodeSnip::JavaCode) {
            s << snip.code << endl;
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
