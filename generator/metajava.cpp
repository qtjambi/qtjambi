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

#include "metajava.h"
#include <reporthandler.h>

#include <qdebug.h>


/*******************************************************************************
 * MetaJavaType
 */
MetaJavaType *MetaJavaType::copy() const
{
    MetaJavaType *cpy = new MetaJavaType;

    cpy->setTypeUsagePattern(typeUsagePattern());
    cpy->setConstant(isConstant());
    cpy->setReference(isReference());
    cpy->setIndirections(indirections());
	cpy->setInstantiations(instantiations());
    cpy->setArrayElementCount(arrayElementCount());
    cpy->setOriginalTypeDescription(originalTypeDescription());

    cpy->setArrayElementType(arrayElementType() ? arrayElementType()->copy() : 0);

    cpy->setTypeEntry(typeEntry());

    return cpy;
}

QString MetaJavaType::cppSignature() const
{
    QString s;

    if (isConstant())
        s += "const ";

    s += typeEntry()->name();

    if (hasInstantiationInCpp()) {
        QList<MetaJavaType *> types = instantiations();
        s += "<";
        for (int i=0; i<types.count(); ++i) {
            if (i > 0)
                s += ", ";
            s += types.at(i)->cppSignature();
        }
        s += " >";
    }

    if (actualIndirections()) {
        s += ' ';
        if (indirections())
            s += QString(indirections(), '*');
        if (isReference())
            s += '&';
    }
    return s;
}

/*******************************************************************************
 * MetaJavaArgument
 */
MetaJavaArgument *MetaJavaArgument::copy() const
{
    MetaJavaArgument *cpy = new MetaJavaArgument;
    cpy->setName(MetaJavaVariable::name());
    cpy->setDefaultValueExpression(defaultValueExpression());
    cpy->setType(type()->copy());
    cpy->setArgumentIndex(argumentIndex());

    return cpy;
}


QString MetaJavaArgument::argumentName() const
{
    QString n = MetaJavaVariable::name();
    if (n.isEmpty()) {
        return QString("arg__%2").arg(m_argument_index + 1);
    }
    return n;
}


QString MetaJavaArgument::indexedName() const
{
    QString n = MetaJavaVariable::name();
    if (n.isEmpty())
        return argumentName();
    return QString("%1%2").arg(n).arg(m_argument_index);
}

QString MetaJavaArgument::name() const
{
    Q_ASSERT_X(0, "MetaJavaArgument::name()", "use argumentName() or indexedName() instead");
    return QString();
}


/*******************************************************************************
 * MetaJavaFunction
 */
MetaJavaFunction::~MetaJavaFunction()
{
    qDeleteAll(m_arguments);
    delete m_type;
}

/*******************************************************************************
 * Indicates that this function has a modification that removes it
 */
bool MetaJavaFunction::isModifiedRemoved(int types) const
{
    FunctionModificationList mods = modifications(implementingClass());
    foreach (FunctionModification mod, mods) {
        if (!mod.isRemoveModifier())
            continue;

        if (!mod.isExclusive())
            return true;

        switch (mod.language) {
        case CodeSnip::JavaCode:
            if (types & JavaFunction)
                return true;
            else
                break ;

        case CodeSnip::ShellDeclaration:
        case CodeSnip::ShellCode:
            if (types & CppShellFunction)
                return true;
            else
                break ;

        case CodeSnip::NativeCode:
            if (types & CppNativeFunction)
                return true;
            else
                break ;

        case CodeSnip::PackageInitializer:
            break;
        }
    }

    return false;
}

bool MetaJavaFunction::needsCallThrough() const
{
    if (ownerClass()->isInterface())
        return false;
    if (argumentsHaveNativeId() || !isStatic())
        return true;

    foreach (const MetaJavaArgument *arg, arguments()) {
        if (arg->type()->isArray() || arg->type()->isJavaEnum() || arg->type()->isJavaFlags())
            return true;
    }

    if (type() && (type()->isArray() || type()->isJavaEnum() || type()->isJavaFlags()))
        return true;

    return false;
}

QString MetaJavaFunction::marshalledName() const
{
    QString returned = "__qt_" + name();
    MetaJavaArgumentList arguments = this->arguments();
    foreach (const MetaJavaArgument *arg, arguments) {
        returned += "_";
        if (arg->type()->isNativePointer()) {
            returned += "nativepointer";
        } else if (arg->type()->isIntegerEnum() || arg->type()->isIntegerFlags()) {
            returned += "int";
        } else {
            returned += arg->type()->name().replace("[]", "_3").replace(".", "_");
        }
    }
    return returned;
}

bool MetaJavaFunction::operator<(const MetaJavaFunction &other) const
{
    uint result = compareTo(&other);
    return result & NameLessThan;
}


/*!
    Returns a mask of CompareResult describing how this function is
    compares to another function
*/
uint MetaJavaFunction::compareTo(const MetaJavaFunction *other) const
{
    uint result = 0;

    // Enclosing class...
    if (ownerClass() == other->ownerClass()) {
        result |= EqualImplementor;
    }

    // Attributes
    if (attributes() == other->attributes()) {
        result |= EqualAttributes;
    }

    // Compare types
    MetaJavaType *t = type();
    MetaJavaType *ot = other->type();
    if ((!t && !ot) || ((t && ot && t->name() == ot->name()))) {
        result |= EqualReturnType;
    }

    // Compare names
    int cmp = originalName().compare(other->originalName());

    if (cmp < 0) {
        result |= NameLessThan;
    } else if (cmp == 0) {
        result |= EqualName;
    }

    // Compare arguments...
    MetaJavaArgumentList min_arguments;
    MetaJavaArgumentList max_arguments;
    if (arguments().size() < other->arguments().size()) {
        min_arguments = arguments();
        max_arguments = other->arguments();
    } else {
        min_arguments = other->arguments();
        max_arguments = arguments();
    }

    int min_count = min_arguments.size();
    int max_count = max_arguments.size();
    bool same = true;
    for (int i=0; i<max_count; ++i) {
        if (i < min_count) {
            const MetaJavaArgument *min_arg = min_arguments.at(i);
            const MetaJavaArgument *max_arg = max_arguments.at(i);
            if (min_arg->type()->name() != max_arg->type()->name()
                && (min_arg->defaultValueExpression().isEmpty() || max_arg->defaultValueExpression().isEmpty())) {
                same = false;
                break;
            }
        } else {
            if (max_arguments.at(i)->defaultValueExpression().isEmpty()) {
                same = false;
                break;
            }
        }
    }

    if (same)
        result |= min_count == max_count ? EqualArguments : EqualDefaultValueOverload;

    return result;
}

MetaJavaFunction *MetaJavaFunction::copy() const
{
    MetaJavaFunction *cpy = new MetaJavaFunction;
    cpy->setName(name());
    cpy->setOriginalName(originalName());
    cpy->setOwnerClass(ownerClass());
    cpy->setImplementingClass(implementingClass());
    cpy->setInterfaceClass(interfaceClass());
    cpy->setFunctionType(functionType());
    cpy->setAttributes(attributes());
    cpy->setDeclaringClass(declaringClass());
    if (type())
        cpy->setType(type()->copy());
	cpy->setConstant(isConstant());
    cpy->setOriginalAttributes(originalAttributes());

    foreach (MetaJavaArgument *arg, arguments())
        cpy->addArgument(arg->copy());

    Q_ASSERT((!type() && !cpy->type())
             || (type()->instantiations() == cpy->type()->instantiations()));

    return cpy;
}

QString MetaJavaFunction::signature() const
{
    QString s(m_original_name);

    s += "(";

    for (int i=0; i<m_arguments.count(); ++i) {
        if (i > 0)
            s += ", ";
        MetaJavaArgument *a = m_arguments.at(i);
        s += a->type()->cppSignature();

        // We need to have the argument names in the qdoc files
        s += " ";
        s += a->argumentName();
    }
    s += ")";

    if (isConstant())
        s += " const";

    return s;
}

int MetaJavaFunction::actualMinimumArgumentCount() const
{
    MetaJavaArgumentList arguments = this->arguments();

    int count = 0;
    for (int i=0; i<arguments.size(); ++i && ++count) {
        if (argumentRemoved(i + 1)) --count;
        else if (!arguments.at(i)->defaultValueExpression().isEmpty()) break;
    }

    return count;
}

QString MetaJavaFunction::replacedDefaultExpression(const MetaJavaClass *cls, int key) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        QList<ArgumentModification> argument_modifications = modification.argument_mods;
        foreach (ArgumentModification argument_modification, argument_modifications) {
            if (argument_modification.index == key
                && !argument_modification.replaced_default_expression.isEmpty()) {
                return argument_modification.replaced_default_expression;
            }
        }
    }

    return QString();
}

bool MetaJavaFunction::removedDefaultExpression(const MetaJavaClass *cls, int key) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        QList<ArgumentModification> argument_modifications = modification.argument_mods;
        foreach (ArgumentModification argument_modification, argument_modifications) {
            if (argument_modification.index == key
                && argument_modification.removed_default_expression) {
                return true;
            }
        }
    }

    return false;
}


QString MetaJavaFunction::conversionRule(CodeSnip::Language language, int key) const
{
    FunctionModificationList modifications = this->modifications(declaringClass());
    foreach (FunctionModification modification, modifications) {
        if (modification.language == language) {
            QList<ArgumentModification> argument_modifications = modification.argument_mods;
            foreach (ArgumentModification argument_modification, argument_modifications) {
                if (argument_modification.index == key) {
                    if (!argument_modification.code().isEmpty()) {
                        return argument_modification.code();
                    }
                }
            }
        }
    }

    return QString();
}

bool MetaJavaFunction::argumentRemoved(int key) const
{
    FunctionModificationList modifications = this->modifications(declaringClass());
    foreach (FunctionModification modification, modifications) {
        QList<ArgumentModification> argument_modifications = modification.argument_mods;
        foreach (ArgumentModification argument_modification, argument_modifications) {
            if (argument_modification.index == key) {
                if (argument_modification.removed) {
                    return true;
                }
            }
        }
    }

    return false;
}

bool MetaJavaFunction::disabledGarbageCollection(const MetaJavaClass *cls, int key) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        QList<ArgumentModification> argument_modifications = modification.argument_mods;
        foreach (ArgumentModification argument_modification, argument_modifications) {
            if (argument_modification.index == key
                && argument_modification.disable_gc) {
                return true;
            }
        }
    }

    return false;
}

bool MetaJavaFunction::disabledGarbageCollection(const MetaJavaClass *cls, CodeSnip::Language language, int key) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        if (modification.language == language) {
            QList<ArgumentModification> argument_modifications = modification.argument_mods;
            foreach (ArgumentModification argument_modification, argument_modifications) {
                if (argument_modification.index == key
                    && argument_modification.disable_gc) {
                    return true;
                }
            }
        }
    }

    return false;
}

bool MetaJavaFunction::isRemovedFromAllLanguages(const MetaJavaClass *cls) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        if (modification.isRemoveModifier() && !modification.isExclusive())
            return true;
    }

    return false;
}

bool MetaJavaFunction::isRemovedFrom(const MetaJavaClass *cls, CodeSnip::Language language) const
{
    FunctionModificationList modifications = this->modifications(cls);
    foreach (FunctionModification modification, modifications) {
        if (modification.isRemoveModifier()) {
            if (!modification.isExclusive() || modification.language == language)
                return true;
        }
    }

    return false;

}

QString MetaJavaFunction::typeReplaced(int key) const
{
    FunctionModificationList modifications = this->modifications(declaringClass());
    foreach (FunctionModification modification, modifications) {
        QList<ArgumentModification> argument_modifications = modification.argument_mods;
        foreach (ArgumentModification argument_modification, argument_modifications) {
            if (argument_modification.index == key
                && !argument_modification.modified_type.isEmpty()) {
                return argument_modification.modified_type;
            }
        }
    }

    return QString();
}

QString MetaJavaFunction::minimalSignature() const
{
    if (!m_cached_minimal_signature.isEmpty())
        return m_cached_minimal_signature;

    QString minimalSignature = originalName() + "(";
    MetaJavaArgumentList arguments = this->arguments();

    for (int i=0; i<arguments.count(); ++i) {
        MetaJavaType *t = arguments.at(i)->type();

        if (i > 0)
            minimalSignature += ",";

        minimalSignature += t->minimalSignature();
    }
    minimalSignature += ")";
    if (isConstant())
        minimalSignature += "const";

    minimalSignature = QMetaObject::normalizedSignature(minimalSignature.toLocal8Bit().constData());

    const_cast<MetaJavaFunction *>(this)->m_cached_minimal_signature = minimalSignature;

    return minimalSignature;
}

FunctionModificationList MetaJavaFunction::modifications(const MetaJavaClass *implementor) const
{
    Q_ASSERT(implementor);
    return implementor->typeEntry()->functionModifications(minimalSignature());
}

bool MetaJavaFunction::hasModifications(const MetaJavaClass *implementor) const
{
    FunctionModificationList mods = modifications(implementor);
    return mods.count() > 0;
}

QString MetaJavaFunction::modifiedName() const
{
    FunctionModificationList mods = modifications(implementingClass());
    foreach (FunctionModification mod, mods) {
        if (mod.isRenameModifier())
            return mod.renamedToName;
    }
    return name();
}

QString MetaJavaFunction::javaSignature(bool minimal) const
{
    QString s;

    // Attributes...
    if (!minimal) {
        if (isPublic()) s += "public ";
        else if (isProtected()) s += "protected ";
        else if (isPrivate()) s += "private ";

//     if (isNative()) s += "native ";
//     else
        if (isFinalInJava()) s += "final ";
        else if (isAbstract()) s += "abstract ";

        if (isStatic()) s += "static ";

        // Return type
        if (type())
            s += type()->fullName() + " ";
        else
            s += "void ";
    }

    s += name();
    s += "(";

    for (int i=0; i<m_arguments.size(); ++i) {
        if (i != 0) {
            s += ",";
            if (!minimal)
                s += QLatin1Char(' ');
        }
        s += m_arguments.at(i)->type()->fullName();

        if (!minimal) {
            s += " ";
            s += m_arguments.at(i)->argumentName();
        }
    }

    s += ")";

    return s;
}


bool function_sorter(MetaJavaFunction *a, MetaJavaFunction *b)
{
    return *a < *b;
}

/*******************************************************************************
 * MetaJavaClass
 */
MetaJavaClass::~MetaJavaClass()
{
    qDeleteAll(m_functions);
    qDeleteAll(m_fields);
}

/*MetaJavaClass *MetaJavaClass::copy() const
{
    MetaJavaClass *cls = new MetaJavaClass;
    cls->setAttributes(attributes());
    cls->setBaseClass(baseClass());
    cls->setTypeEntry(typeEntry());
    foreach (MetaJavaFunction *function, functions()) {
        MetaJavaFunction *copy = function->copy();
        function->setImplementingClass(cls);
        cls->addFunction(copy);
    }
    cls->setEnums(enums());
    foreach (const MetaJavaField *field, fields()) {
        MetaJavaField *copy = field->copy();
        copy->setEnclosingClass(cls);
        cls->addField(copy);
    }
    cls->setInterfaces(interfaces());

    return cls;
}*/

/*******************************************************************************
 * Returns true if this class is a subclass of the given class
 */
bool MetaJavaClass::inheritsFrom(const MetaJavaClass *cls) const
{
    Q_ASSERT(cls != 0);

    const MetaJavaClass *clazz = this;
    while (clazz != 0) {
        if (clazz == cls)
            return true;

        clazz = clazz->baseClass();
    }

    return false;
}

/*******************************************************************************
 * Constructs an interface based on the functions and enums in this
 * class and returns it...
 */
MetaJavaClass *MetaJavaClass::extractInterface()
{
    Q_ASSERT(typeEntry()->designatedInterface());

    if (m_extracted_interface == 0) {
        MetaJavaClass *iface = new MetaJavaClass;
        iface->setAttributes(attributes());
        iface->setBaseClass(0);
        iface->setPrimaryInterfaceImplementor(this);

        iface->setTypeEntry(typeEntry()->designatedInterface());

        foreach (MetaJavaFunction *function, functions()) {
            if (!function->isConstructor())
                iface->addFunction(function->copy());
        }

//         iface->setEnums(enums());
//         setEnums(MetaJavaEnumList());

        foreach (const MetaJavaField *field, fields()) {
            if (field->isPublic()) {
                MetaJavaField *new_field = field->copy();
                new_field->setEnclosingClass(iface);
                iface->addField(new_field);
            }
        }

        m_extracted_interface = iface;
        addInterface(iface);        
    }

    return m_extracted_interface;
}

/*******************************************************************************
 * Returns a list of all the functions with a given name
 */
MetaJavaFunctionList MetaJavaClass::queryFunctionsByName(const QString &name) const
{
    MetaJavaFunctionList returned;
    MetaJavaFunctionList functions = this->functions();
    foreach (MetaJavaFunction *function, functions) {
        if (function->name() == name)
            returned.append(function);
    }

    return returned;
}

/*******************************************************************************
 * Returns a list of all the functions retrieved during parsing which should
 * be added to the Java API.
 */
MetaJavaFunctionList MetaJavaClass::functionsInJava() const
{
    int default_flags = NormalFunctions | Visible | NotRemovedFromJava;

    // Interfaces don't implement functions
    default_flags |= isInterface() ? 0 : ClassImplements;

    // Only public functions in final classes
    // default_flags |= isFinal() ? WasPublic : 0;
    int public_flags = isFinal() ? WasPublic : 0;

    // Constructors
    MetaJavaFunctionList returned = queryFunctions(Constructors | default_flags | public_flags);

    // Final functions
    returned += queryFunctions(FinalInJavaFunctions | NonStaticFunctions | default_flags | public_flags);

    // Virtual functions
    returned += queryFunctions(VirtualInJavaFunctions | NonStaticFunctions | default_flags | public_flags);

    // Static functions
    returned += queryFunctions(StaticFunctions | default_flags | public_flags);

    // Empty, private functions, since they aren't caught by the other ones
    returned += queryFunctions(Empty | Invisible);

    return returned;
}

/*******************************************************************************
 * Returns a list of all functions that should be declared and implemented in
 * the shell class which is generated as a wrapper on top of the actual C++ class
 */
MetaJavaFunctionList MetaJavaClass::functionsInShellClass() const
{
    // Only functions and only protected and public functions
    int default_flags = NormalFunctions | Visible | WasVisible | NotRemovedFromShell;

    // All virtual functions
    MetaJavaFunctionList returned = queryFunctions(VirtualFunctions | default_flags);

    // All functions explicitly set to be implemented by the shell class
    // (mainly superclass functions that are hidden by other declarations)
    returned += queryFunctions(ForcedShellFunctions | default_flags);

    return returned;
}

/*******************************************************************************
 * Returns a list of all functions that require a public override function to
 * be generated in the shell class. This includes all functions that were originally
 * protected in the superclass.
 */
MetaJavaFunctionList MetaJavaClass::publicOverrideFunctions() const
{
    return queryFunctions(NormalFunctions | WasProtected | FinalInCppFunctions | NotRemovedFromJava)
           + queryFunctions(Signals | WasProtected | FinalInCppFunctions | NotRemovedFromJava);
}

MetaJavaFunctionList MetaJavaClass::virtualOverrideFunctions() const
{
    return queryFunctions(NormalFunctions | NonEmptyFunctions | Visible | VirtualInCppFunctions | NotRemovedFromShell) +
           queryFunctions(Signals | NonEmptyFunctions | Visible | VirtualInCppFunctions | NotRemovedFromShell);
}

void MetaJavaClass::setFunctions(const MetaJavaFunctionList &functions)
{
    m_functions = functions;

    // Functions must be sorted by name before next loop
    qSort(m_functions.begin(), m_functions.end(), function_sorter);

    QString currentName;
    bool hasVirtuals = false;
    MetaJavaFunctionList final_functions;
    foreach (MetaJavaFunction *f, m_functions) {
        f->setOwnerClass(this);

        m_has_virtuals |= !f->isFinal();
        m_has_nonpublic |= !f->isPublic();

        // If we have non-virtual overloads of a virtual function, we have to implement
        // all the overloads in the shell class to override the hiding rule
        if (currentName == f->name()) {
            hasVirtuals = hasVirtuals || !f->isFinal();
            if (f->isFinal())
                final_functions += f;
        } else {
            if (hasVirtuals && final_functions.size() > 0) {
                foreach (MetaJavaFunction *final_function, final_functions) {
                    *final_function += MetaJavaAttributes::ForceShellImplementation;

                    QString warn = QString("hiding of function '%1' in class '%2'")
                        .arg(final_function->name()).arg(name());
                    ReportHandler::warning(warn);
                }
            }

            hasVirtuals = !f->isFinal();
            final_functions.clear();
            if (f->isFinal())
                final_functions += f;
            currentName = f->name();
        }
    }

#ifndef QT_NO_DEBUG
    bool duplicate_function = false;
    for (int j=0; j<m_functions.size(); ++j) {
        FunctionModificationList mods = m_functions.at(j)->modifications(m_functions.at(j)->implementingClass());

        bool removed = false;
        foreach (const FunctionModification &mod, mods) {
            if (mod.isRemoveModifier()) {
                removed = true;
                break ;
            }
        }
        if (removed)
            continue ;

        for (int i=0; i<m_functions.size() - 1; ++i) {
            if (j == i)
                continue;

            mods = m_functions.at(i)->modifications(m_functions.at(i)->implementingClass());
            bool removed = false;
            foreach (const FunctionModification &mod, mods) {
                if (mod.isRemoveModifier()) {
                    removed = true;
                    break ;
                }
            }
            if (removed)
                continue ;

            uint cmp = m_functions.at(i)->compareTo(m_functions.at(j));
            if ((cmp & MetaJavaFunction::EqualName) && (cmp & MetaJavaFunction::EqualArguments)) {
                printf("%s.%s mostly equal to %s.%s\n",
                       qPrintable(m_functions.at(i)->implementingClass()->typeEntry()->qualifiedCppName()),
                       qPrintable(m_functions.at(i)->signature()),
                       qPrintable(m_functions.at(j)->implementingClass()->typeEntry()->qualifiedCppName()),
                       qPrintable(m_functions.at(j)->signature()));
                duplicate_function = true;
            }
        }
    }
    //Q_ASSERT(!duplicate_function);
#endif
}

bool MetaJavaClass::hasFieldAccessors() const
{
    foreach (const MetaJavaField *field, fields()) {
        if (field->getter() || field->setter())
            return true;
    }

    return false;
}

void MetaJavaClass::addFunction(MetaJavaFunction *function)
{
    function->setOwnerClass(this);

    if (!function->isDestructor()) {
        m_functions << function;
        qSort(m_functions.begin(), m_functions.end(), function_sorter);
    }



    m_has_virtuals |= !function->isFinal();
    m_has_nonpublic |= !function->isPublic();
}

bool MetaJavaClass::hasSignal(const MetaJavaFunction *other) const
{
    if (!other->isSignal())
        return false;

    foreach (const MetaJavaFunction *f, functions()) {
        if (f->isSignal() && f->compareTo(other) & MetaJavaFunction::EqualName)
            return other->modifiedName() == f->modifiedName();
    }

    return false;
}


QString MetaJavaClass::name() const
{
    return QString(m_type_entry->javaName()).replace("::", "_");
}

bool MetaJavaClass::hasFunction(const QString &str) const
{
    foreach (const MetaJavaFunction *f, functions())
        if (f->name() == str)
            return true;
    return false;
}

/* Returns true if this class has one or more functions that are
   protected. If a class has protected members we need to generate a
   shell class with public accessors to the protected functions, so
   they can be called from the native functions.
*/
bool MetaJavaClass::hasProtectedFunctions() const {
    foreach (MetaJavaFunction *func, m_functions) {
        if (func->isProtected())
            return true;
    }
    return false;
}

bool MetaJavaClass::generateShellClass() const
{
    return m_force_shell_class ||
        (!isFinal()
         && (hasVirtualFunctions()
             || hasProtectedFunctions()
             || hasFieldAccessors()));
}


static bool functions_contains(const MetaJavaFunctionList &l, const MetaJavaFunction *func)
{
    foreach (const MetaJavaFunction *f, l) {
		if ((f->compareTo(func) & MetaJavaFunction::PrettySimilar) == MetaJavaFunction::PrettySimilar)
            return true;
    }
    return false;
}

MetaJavaField::MetaJavaField() : m_getter(0), m_setter(0), m_class(0)
{
}

MetaJavaField::~MetaJavaField()
{
    delete m_setter;
    delete m_getter;
}
ushort        painters;                        // refcount
MetaJavaField *MetaJavaField::copy() const
{
    MetaJavaField *returned = new MetaJavaField;
    returned->setEnclosingClass(0);
    returned->setAttributes(attributes());
    returned->setName(name());
    returned->setType(type()->copy());
    returned->setOriginalAttributes(originalAttributes());

    return returned;
}

static QString upCaseFirst(const QString &str) {
    Q_ASSERT(!str.isEmpty());
    QString s = str;
    s[0] = s.at(0).toUpper();
    return s;
}

static MetaJavaFunction *createXetter(const MetaJavaField *g, const QString &name, uint type) {
    MetaJavaFunction *f = new MetaJavaFunction;



    f->setName(name);
    f->setOriginalName(name);
    f->setOwnerClass(g->enclosingClass());
    f->setImplementingClass(g->enclosingClass());
    f->setDeclaringClass(g->enclosingClass());

    uint attr = MetaJavaAttributes::Native
                | MetaJavaAttributes::Final
                | type;
    if (g->isStatic())
        attr |= MetaJavaAttributes::Static;
    if (g->isPublic())
        attr |= MetaJavaAttributes::Public;
    else if (g->isProtected())
        attr |= MetaJavaAttributes::Protected;
    else
        attr |= MetaJavaAttributes::Private;
    f->setAttributes(attr);
    f->setOriginalAttributes(attr);

    FieldModificationList mods = g->modifications();
    foreach (FieldModification mod, mods) {
        if (mod.isRenameModifier())
            f->setName(mod.renamedTo());
        if (mod.isAccessModifier()) {
            if (mod.isPrivate())
                f->setVisibility(MetaJavaAttributes::Private);
            else if (mod.isProtected())
                f->setVisibility(MetaJavaAttributes::Protected);
            else if (mod.isPublic())
                f->setVisibility(MetaJavaAttributes::Public);
            else if (mod.isFriendly())
                f->setVisibility(MetaJavaAttributes::Friendly);
        }

    }
    return f;
}

FieldModificationList MetaJavaField::modifications() const
{
    FieldModificationList mods = enclosingClass()->typeEntry()->fieldModifications();
    FieldModificationList returned;

    foreach (FieldModification mod, mods) {
        if (mod.name == name())
            returned += mod;
    }

    return returned;
}

const MetaJavaFunction *MetaJavaField::setter() const
{
    if (m_setter == 0) {
        m_setter = createXetter(this,
                                "set" + upCaseFirst(name()),
                                MetaJavaAttributes::SetterFunction);
        MetaJavaArgumentList arguments;
        MetaJavaArgument *argument = new MetaJavaArgument;
        argument->setType(type()->copy());
        argument->setName(name());
        arguments.append(argument);
        m_setter->setArguments(arguments);
    }
    return m_setter;
}

const MetaJavaFunction *MetaJavaField::getter() const
{
    if (m_getter == 0) {
        m_getter = createXetter(this,
                                name(),
                                MetaJavaAttributes::GetterFunction);
        m_getter->setType(type());
    }

    return m_getter;
}


bool MetaJavaClass::hasConstructors() const
{
    return queryFunctions(Constructors).size() != 0;
}

void MetaJavaClass::addDefaultConstructor()
{
    MetaJavaFunction *f = new MetaJavaFunction;
    f->setName(name());
    f->setOwnerClass(this);
    f->setFunctionType(MetaJavaFunction::ConstructorFunction);
    f->setArguments(MetaJavaArgumentList());
    f->setDeclaringClass(this);

    uint attr = MetaJavaAttributes::Native;
    attr |= MetaJavaAttributes::Public;
    f->setAttributes(attr);
    f->setImplementingClass(this);
    f->setOriginalAttributes(f->attributes());

    addFunction(f);
}

bool MetaJavaClass::hasFunction(const MetaJavaFunction *f) const
{
    return functions_contains(m_functions, f);
}

/* Goes through the list of functions and returns a list of all
   functions matching all of the criteria in \a query.
 */

MetaJavaFunctionList MetaJavaClass::queryFunctions(uint query) const
{
    MetaJavaFunctionList functions;

    foreach (MetaJavaFunction *f, m_functions) {

        if ((query & NotRemovedFromJava) && f->isRemovedFrom(f->declaringClass(), CodeSnip::JavaCode)) {
            continue;
        }

        if ((query & NotRemovedFromShell) && f->isRemovedFrom(f->declaringClass(), CodeSnip::ShellCode)) {
            continue;
        }

        if ((query & Visible) && f->isPrivate()) {
            continue;
        }

        if ((query & VirtualInJavaFunctions) && f->isFinalInJava()) {
            continue;
        }

        if ((query & Invisible) && !f->isPrivate()) {
            continue;
        }

        if ((query & Empty) && !f->isEmptyFunction()) {
            continue;
        }

        if ((query & WasPublic) && !f->wasPublic()) {
            continue;
        }

        if ((query & WasVisible) && f->wasPrivate()) {
            continue;
        }

        if ((query & WasProtected) && !f->wasProtected()) {
            continue;
        }

        if ((query & ClassImplements) && f->ownerClass() != f->implementingClass()) {
            continue;
        }

        if ((query & Inconsistent) && (f->isFinalInCpp() == f->isFinalInJava() || f->isStatic())) {
            continue;
        }

        if ((query & FinalInJavaFunctions) && !f->isFinalInJava()) {
            continue;
        }

        if ((query & FinalInCppFunctions) && !f->isFinalInCpp()) {
            continue;
        }

        if ((query & VirtualInCppFunctions) && f->isFinalInCpp()) {
            continue;
        }

        if ((query & Signals) && (!f->isSignal())) {
            continue;
        }

        if ((query & ForcedShellFunctions)
            && (!f->isForcedShellImplementation()
                || !f->isFinal())) {
            continue;
        }

        if ((query & Constructors) && (!f->isConstructor()
                                       || f->ownerClass() != f->implementingClass())
            || f->isConstructor() && (query & Constructors) == 0) {
            continue;
        }

        // Destructors are never included in the functions of a class currently
        /*
           if ((query & Destructors) && (!f->isDestructor()
                                       || f->ownerClass() != f->implementingClass())
            || f->isDestructor() && (query & Destructors) == 0) {
            continue;
        }*/

        if ((query & VirtualFunctions) && (f->isFinal() || f->isSignal() || f->isStatic())) {
            continue;
        }

        if ((query & StaticFunctions) && (!f->isStatic() || f->isSignal())) {
            continue;
        }

        if ((query & NonStaticFunctions) && (f->isStatic())) {
            continue;
        }

        if ((query & NonEmptyFunctions) && (f->isEmptyFunction())) {
            continue;
        }

        if ((query & NormalFunctions) && (f->isSignal())) {
            continue;
        }

        if ((query & AbstractFunctions) && !f->isAbstract()) {
            continue;
        }

        functions << f;
    }

//    qDebug() << "queried" << m_type_entry->qualifiedCppName() << "got" << functions.size() << "out of" << m_functions.size();

    return functions;
}


bool MetaJavaClass::hasInconsistentFunctions() const
{
    return cppInconsistentFunctions().size() > 0;
}

bool MetaJavaClass::hasSignals() const
{
    return cppSignalFunctions().size() > 0;
}


/**
 * Adds the specified interface to this class by adding all the
 * functions in the interface to this class.
 */
void MetaJavaClass::addInterface(MetaJavaClass *interface)
{
    Q_ASSERT(!m_interfaces.contains(interface));
    m_interfaces << interface;

    if (m_extracted_interface != 0 && m_extracted_interface != interface)
        m_extracted_interface->addInterface(interface);

    foreach (MetaJavaFunction *function, interface->functions())
        if (!hasFunction(function) && !function->isConstructor()) {
            MetaJavaFunction *cpy = function->copy();
            cpy->setImplementingClass(this);

            // Setup that this function is an interface class.
            cpy->setInterfaceClass(interface);
            *cpy += MetaJavaAttributes::InterfaceFunction;

            // Copy the modifications in interface into the implementing classes.
            FunctionModificationList mods = function->modifications(interface);
            foreach  (const FunctionModification &mod, mods) {
                m_type_entry->addFunctionModification(mod);
            }

            // It should be mostly safe to assume that when we implement an interface
            // we don't "pass on" pure virtual functions to our sublcasses...
//             *cpy -= MetaJavaAttributes::Abstract;

            addFunction(cpy);
        }
}


void MetaJavaClass::setInterfaces(const MetaJavaClassList &interfaces)
{
    m_interfaces = interfaces;
}


MetaJavaEnum *MetaJavaClass::findEnum(const QString &enumName)
{
    foreach (MetaJavaEnum *e, m_enums) {
        if (e->name() == enumName)
            return e;
    }

    if (typeEntry()->designatedInterface())
        return extractInterface()->findEnum(enumName);

    return 0;
}




/*!  Recursivly searches for the enum value named \a enumValueName in
  this class and its superclasses and interfaces. Values belonging to
  \a java_enum are excluded from the search.
*/
MetaJavaEnumValue *MetaJavaClass::findEnumValue(const QString &enumValueName, MetaJavaEnum *java_enum)
{
    foreach (MetaJavaEnum *e, m_enums) {
        if (e == java_enum)
            continue;
        foreach (MetaJavaEnumValue *v, e->values()) {
            if (v->name() == enumValueName)
                return v;
        }
    }

    if (typeEntry()->designatedInterface())
        return extractInterface()->findEnumValue(enumValueName, java_enum);

    if (baseClass() != 0)
        return baseClass()->findEnumValue(enumValueName, java_enum);

    return 0;
}


/*!
 * Searches through all of this class' enums for a value matching the
 * name \a enumValueName. The name is excluding the class/namespace
 * prefix. The function recursivly searches interfaces and baseclasses
 * of this class.
 */
MetaJavaEnum *MetaJavaClass::findEnumForValue(const QString &enumValueName)
{
    foreach (MetaJavaEnum *e, m_enums) {
        foreach (MetaJavaEnumValue *v, e->values()) {
            if (v->name() == enumValueName)
                return e;
        }
    }

    if (typeEntry()->designatedInterface())
        return extractInterface()->findEnumForValue(enumValueName);

    if (baseClass() != 0)
        return baseClass()->findEnumForValue(enumValueName);

    return 0;
}


static void add_extra_include_for_type(MetaJavaClass *java_class, const MetaJavaType *type)
{
    Q_ASSERT(java_class != 0);
    const TypeEntry *entry = (type ? type->typeEntry() : 0);
    if (entry != 0 && entry->isComplex()) {
        const ComplexTypeEntry *centry = static_cast<const ComplexTypeEntry *>(entry);
        ComplexTypeEntry *class_entry = java_class->typeEntry();
        if (class_entry != 0 && centry->include().isValid())
            class_entry->addExtraInclude(centry->include());
    }
}

static void add_extra_includes_for_function(MetaJavaClass *java_class, const MetaJavaFunction *java_function)
{
    Q_ASSERT(java_class != 0);
    Q_ASSERT(java_function != 0);
    add_extra_include_for_type(java_class, java_function->type());

    MetaJavaArgumentList arguments = java_function->arguments();
    foreach (MetaJavaArgument *argument, arguments)
        add_extra_include_for_type(java_class, argument->type());
}

void MetaJavaClass::fixFunctions()
{
    if (m_functions_fixed)
        return;
    else
        m_functions_fixed = true;

    MetaJavaClass *super_class = baseClass();
    MetaJavaFunctionList funcs = functions();

//     printf("fix functions for %s\n", qPrintable(name()));

    if (super_class != 0)
        super_class->fixFunctions();
    int iface_idx = 0;
    while (super_class || iface_idx < interfaces().size()) {
//         printf(" - base: %s\n", qPrintable(super_class->name()));

        // Since we always traverse the complete hierarchy we are only
        // interrested in what each super class implements, not what
        // we may have propagated from their base classes again.
        MetaJavaFunctionList super_funcs;
        if (super_class)
            super_funcs = super_class->queryFunctions(MetaJavaClass::ClassImplements);
        else
            super_funcs = interfaces().at(iface_idx)->queryFunctions(MetaJavaClass::NormalFunctions);

        QSet<MetaJavaFunction *> funcs_to_add;
        for (int sfi=0; sfi<super_funcs.size(); ++sfi) {
            MetaJavaFunction *sf = super_funcs.at(sfi);
            // we generally don't care about private functions, but we have to get the ones that are
            // virtual in case they override abstract functions.
            bool add = (sf->isNormal() || sf->isSignal() || sf->isEmptyFunction());
            for (int fi=0; fi<funcs.size(); ++fi) {
                MetaJavaFunction *f = funcs.at(fi);
                uint cmp = f->compareTo(sf);

                if (cmp & MetaJavaFunction::EqualName) {
//                     printf("   - %s::%s similar to %s::%s %x vs %x\n",
//                            qPrintable(sf->implementingClass()->typeEntry()->qualifiedCppName()),
//                            qPrintable(sf->name()),
//                            qPrintable(f->implementingClass()->typeEntry()->qualifiedCppName()),
//                            qPrintable(f->name()),
//                            sf->attributes(),
//                            f->attributes());

                    add = false;
                    if (cmp & MetaJavaFunction::EqualArguments) {

//                         if (!(cmp & MetaJavaFunction::EqualReturnType)) {
//                             ReportHandler::warning(QString("%1::%2 and %3::%4 differ in retur type")
//                                                    .arg(sf->implementingClass()->name())
//                                                    .arg(sf->name())
//                                                    .arg(f->implementingClass()->name())
//                                                    .arg(f->name()));
//                         }

                        // Same function, propegate virtual...
                        if (!(cmp & MetaJavaFunction::EqualAttributes)) {
                            if (!f->isEmptyFunction()) {
                                if (!sf->isFinalInCpp() && f->isFinalInCpp()) {
                                    *f -= MetaJavaAttributes::FinalInCpp;
    //                                 printf("   --- inherit virtual\n");
                                }
                                if (!sf->isFinalInJava() && f->isFinalInJava()) {
                                    *f -= MetaJavaAttributes::FinalInJava;
    //                                 printf("   --- inherit virtual\n");
                                }
                                if (!f->isFinalInJava() && f->isPrivate()) {
                                    f->setFunctionType(MetaJavaFunction::EmptyFunction);
                                    f->setVisibility(MetaJavaAttributes::Protected);
                                    *f += MetaJavaAttributes::FinalInJava;
                                    ReportHandler::warning(QString("private virtual function '%1' in '%2'")
                                        .arg(f->signature())
                                        .arg(f->implementingClass()->name()));
                                }
                            }
                        }

                        if (f->visibility() != sf->visibility()) {
                            QString warn = QString("visibility of function '%1' modified in class '%2'")
                                .arg(f->name()).arg(name());
                            ReportHandler::warning(warn);

                            // If new visibility is private, we can't
                            // do anything. If it isn't, then we
                            // prefer the parent class's visibility
                            // setting for the function.
                            if (!f->isPrivate() && !sf->isPrivate())
                                f->setVisibility(sf->visibility());

                            // Private overrides of abstract functions have to go into the class or
                            // the subclasses will not compile as non-abstract classes.
                            // But they don't need to be implemented, since they can never be called.
                            if (f->isPrivate() && sf->isAbstract()) {
                                f->setFunctionType(MetaJavaFunction::EmptyFunction);
                                f->setVisibility(sf->visibility());
                                *f += MetaJavaAttributes::FinalInJava;
                                *f += MetaJavaAttributes::FinalInCpp;
                            }
                        }

                        // Set the class which first declares this function, afawk
                        f->setDeclaringClass(sf->declaringClass());
                    }

                    if (cmp & MetaJavaFunction::EqualDefaultValueOverload) {
                        MetaJavaArgumentList arguments;
                        if (f->arguments().size() < sf->arguments().size())
                            arguments = sf->arguments();
                        else
                            arguments = f->arguments();

                        for (int i=0; i<arguments.size(); ++i)
                            arguments[i]->setDefaultValueExpression(QString());
                    }

                    if (sf->isFinalInJava() && !sf->isPrivate()) {
                        // Shadowed funcion, need to make base class
                        // function non-virtual
                        *sf -= MetaJavaAttributes::FinalInJava;
//                         printf("   --- shadowing... force final in java\n");
                    }

                    // Otherwise we have function shadowing and we can
                    // skip the thing...
                }

            }

            if (add)
                funcs_to_add << sf;
        }

        foreach (MetaJavaFunction *f, funcs_to_add)
            funcs << f->copy();

        if (super_class)
            super_class = super_class->baseClass();
        else
            iface_idx++;
    }

    bool hasPrivateConstructors = false;
    bool hasPublicConstructors = false;
    foreach (MetaJavaFunction *func, funcs) {
        FunctionModificationList mods = func->modifications(this);
        foreach (const FunctionModification &mod, mods) {
            if (mod.isRenameModifier()) {
//                 qDebug() << name() << func->originalName() << func << " from "
//                          << func->implementingClass()->name() << "renamed to" << mod.renamedTo();
                func->setName(mod.renamedTo());
            }
        }

        // Make sure class is abstract if one of the functions is
        if (func->isAbstract()) {
            (*this) += MetaJavaAttributes::Abstract;
            (*this) -= MetaJavaAttributes::Final;
        }

        if (func->isConstructor()) {
            if (func->isPrivate())
                hasPrivateConstructors = true;
            else
                hasPublicConstructors = true;
        }



        // Make sure that we include files for all classes that are in use
        add_extra_includes_for_function(this, func);
    }

    if (hasPrivateConstructors && !hasPublicConstructors) {
        (*this) += MetaJavaAttributes::Abstract;
        (*this) -= MetaJavaAttributes::Final;
    }

    foreach (MetaJavaFunction *f1, funcs) {
        foreach (MetaJavaFunction *f2, funcs) {
            if (f1 != f2) {
                uint cmp = f1->compareTo(f2);
                if ((cmp & MetaJavaFunction::EqualName)
                    && !f1->isFinalInCpp()
                    && f2->isFinalInCpp()) {
                    *f2 += MetaJavaAttributes::FinalOverload;
//                     qDebug() << f2 << f2->implementingClass()->name() << "::" << f2->name() << f2->arguments().size() << " vs " << f1 << f1->implementingClass()->name() << "::" << f1->name() << f1->arguments().size();
//                     qDebug() << "    " << f2;
//                     MetaJavaArgumentList f2Args = f2->arguments();
//                     foreach (MetaJavaArgument *a, f2Args)
//                         qDebug() << "        " << a->type()->name() << a->name();
//                     qDebug() << "    " << f1;
//                     MetaJavaArgumentList f1Args = f1->arguments();
//                     foreach (MetaJavaArgument *a, f1Args)
//                         qDebug() << "        " << a->type()->name() << a->name();

                }
            }
        }
    }

    setFunctions(funcs);
}


QString MetaJavaType::minimalSignature() const
{
    QString minimalSignature;
    if (isConstant())
        minimalSignature += "const ";
    minimalSignature += typeEntry()->name();
    if (hasInstantiations()) {
        QList<MetaJavaType *> instantiations = this->instantiations();
        minimalSignature += "<";
        for (int i=0;i<instantiations.size();++i) {
            if (i > 0)
                minimalSignature += ",";
            minimalSignature += instantiations.at(i)->minimalSignature();
        }
        minimalSignature += ">";
    }

    if (isReference())
        minimalSignature += "&";
    for (int j=0; j<indirections(); ++j)
        minimalSignature += "*";

    return minimalSignature;
}

bool MetaJavaType::hasNativeId() const
{
    return (isQObject() || isValue() || isObject()) && typeEntry()->isNativeIdBased();
}


/*******************************************************************************
 * Other stuff...
 */


MetaJavaEnum *MetaJavaClassList::findEnum(const EnumTypeEntry *entry) const
{
    Q_ASSERT(entry->isEnum());

    QString qualified_name = entry->qualifiedCppName();
    int pos = qualified_name.lastIndexOf("::");
    Q_ASSERT(pos > 0);

    QString enum_name = qualified_name.mid(pos + 2);
    QString class_name = qualified_name.mid(0, pos);

    MetaJavaClass *java_class = findClass(class_name);
    if (!java_class) {
        ReportHandler::warning(QString("MetaJava::findEnum(), unknown class '%1' in '%2'")
                               .arg(class_name).arg(entry->qualifiedCppName()));
        return 0;
    }

    return java_class->findEnum(enum_name);
}

MetaJavaEnumValue *MetaJavaEnumValueList::find(const QString &name) const
{
    for (int i=0; i<size(); ++i) {
        if (name == at(i)->name())
            return at(i);
    }
    return 0;
}

MetaJavaEnumValue *MetaJavaClassList::findEnumValue(const QString &name) const
{
    QStringList lst = name.split(QLatin1String("::"));

    Q_ASSERT_X(lst.size() == 2, "MetaJavaClassList::findEnumValue()", "Expected qualified enum");


    QString prefixName = lst.at(0);
    QString enumName = lst.at(1);

    MetaJavaClass *cl = findClass(prefixName);
    if (cl)
        return cl->findEnumValue(enumName, 0);

    ReportHandler::warning(QString("no matching enum '%1'").arg(name));
    return 0;
}

/*!
 * Searches the list after a class that mathces \a name; either as
 * C++, Java base name or complete Java package.class name.
 */

MetaJavaClass *MetaJavaClassList::findClass(const QString &name) const
{
    if (name.isEmpty())
        return 0;

    foreach (MetaJavaClass *c, *this) {
        if (c->qualifiedCppName() == name)
            return c;
    }

    foreach (MetaJavaClass *c, *this) {
        if (c->fullName() == name)
            return c;
    }

    foreach (MetaJavaClass *c, *this) {
        if (c->name() == name)
            return c;
    }

    return 0;
}
