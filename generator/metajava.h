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

#ifndef METAJAVA_H
#define METAJAVA_H

#include "codemodel.h"

#include "typesystem.h"

#include <QSet>
#include <QStringList>
#include <QTextStream>


class MetaJava;
class MetaJavaClass;
class MetaJavaField;
class MetaJavaFunction;
class MetaJavaType;
class MetaJavaVariable;
class MetaJavaArgument;
class MetaJavaEnumValue;
class MetaJavaEnum;

typedef QList<MetaJavaField *> MetaJavaFieldList;
typedef QList<MetaJavaArgument *> MetaJavaArgumentList;
typedef QList<MetaJavaFunction *> MetaJavaFunctionList;
class MetaJavaClassList : public  QList<MetaJavaClass *>
{
public:
    MetaJavaClass *findClass(const QString &name) const;
    MetaJavaEnumValue *findEnumValue(const QString &string) const;
    MetaJavaEnum *findEnum(const EnumTypeEntry *entry) const;

};



class MetaJavaAttributes
{
public:
    MetaJavaAttributes() : m_attributes(0) { };

    enum Attribute {
        None                        = 0x0000,

        Private                     = 0x0001,
        Protected                   = 0x0002,
        Public                      = 0x0004,
        Friendly                    = 0x0008,
        Visibility                  = 0x000f,

        Native                      = 0x0010,
        Abstract                    = 0x0020,
        Static                      = 0x0040,

        FinalInJava                 = 0x0080,
        FinalInCpp                  = 0x0100,
        ForceShellImplementation    = 0x0200,

        GetterFunction              = 0x0400,
        SetterFunction              = 0x0800,

        FinalOverload               = 0x1000,
        InterfaceFunction           = 0x2000,

        Final                       = FinalInJava | FinalInCpp
    };

    uint attributes() const { return m_attributes; }
    void setAttributes(uint attributes) { m_attributes = attributes; }

    uint originalAttributes() const { return m_originalAttributes; }
    void setOriginalAttributes(uint attributes) { m_originalAttributes = attributes; }

    uint visibility() const { return m_attributes & Visibility; }
    void setVisibility(uint visi) { m_attributes = (m_attributes & ~Visibility) | visi; }

    void operator+=(Attribute attribute) { m_attributes |= attribute; }
    void operator-=(Attribute attribute) { m_attributes &= ~attribute; }

    bool isNative() const { return m_attributes & Native; }
    bool isFinal() const { return (m_attributes & Final) == Final; }
    bool isFinalInJava() const { return m_attributes & FinalInJava; }
    bool isFinalInCpp() const { return m_attributes & FinalInCpp; }
    bool isAbstract() const { return m_attributes & Abstract; }
    bool isStatic() const { return m_attributes & Static; }
    bool isForcedShellImplementation() const { return m_attributes & ForceShellImplementation; }
    bool isInterfaceFunction() const { return m_attributes & InterfaceFunction; }
    bool isFinalOverload() const { return m_attributes & FinalOverload; }

    bool isPrivate() const { return m_attributes & Private; }
    bool isProtected() const { return m_attributes & Protected; }
    bool isPublic() const { return m_attributes & Public; }
    bool isFriendly() const { return m_attributes & Friendly; }

    bool wasPrivate() const { return m_originalAttributes & Private; }
    bool wasProtected() const { return m_originalAttributes & Protected; }
    bool wasPublic() const { return m_originalAttributes & Public; }
    bool wasFriendly() const { return m_originalAttributes & Friendly; }

private:
    uint m_attributes;
    uint m_originalAttributes;
};


class MetaJavaType
{
public:
    enum TypeUsagePattern {
        InvalidPattern,
        PrimitivePattern,
        FlagsPattern,
        EnumPattern,
        ValuePattern,
        StringPattern,
        CharPattern,
        ObjectPattern,
        QObjectPattern,
        NativePointerPattern,
        ContainerPattern,
        VariantPattern,
        ArrayPattern,
        ThreadPattern
    };

    MetaJavaType() :
        m_type_entry(0),
        m_array_element_count(0),
        m_array_element_type(0),
        m_pattern(InvalidPattern),
        m_constant(false),
        m_reference(false),
        m_cpp_instantiation(true),
        m_indirections(0)
    {
    }

    QString package() const { return m_type_entry->javaPackage(); }
    QString name() const { return m_type_entry->javaName(); }
    QString fullName() const { return m_type_entry->qualifiedJavaName(); }

    void setTypeUsagePattern(TypeUsagePattern pattern) { m_pattern = pattern; }
    TypeUsagePattern typeUsagePattern() const { return m_pattern; }

    // true when use pattern is container
    bool hasInstantiations() const { return !m_instantiations.isEmpty(); }
    void addInstantiation(MetaJavaType *inst) { m_instantiations << inst; }
	void setInstantiations(const QList<MetaJavaType *> &insts) { m_instantiations = insts; }
    QList<MetaJavaType *> instantiations() const { return m_instantiations; }
    void setInstantiationInCpp(bool incpp) { m_cpp_instantiation = incpp; }
    bool hasInstantiationInCpp() const { return hasInstantiations() && m_cpp_instantiation; }

    QString minimalSignature() const;

    // true when the type is a QtJambiObject subclass
    bool hasNativeId() const;

    // returns true if the typs is used as a non complex primitive, no & or *'s
    bool isPrimitive() const { return m_pattern == PrimitivePattern; }

    // returns true if the type is used as an enum
    bool isEnum() const { return m_pattern == EnumPattern; }

    // returns true if the type is used as a QObject *
    bool isQObject() const { return m_pattern == QObjectPattern; }

    // returns true if the type is used as an object, e.g. Xxx *
    bool isObject() const { return m_pattern == ObjectPattern; }

    // returns true if the type is used as an array, e.g. Xxx[42]
    bool isArray() const { return m_pattern == ArrayPattern; }

    // returns true if the type is used as a value type (X or const X &)
    bool isValue() const { return m_pattern == ValuePattern; }

    // returns true for more complex types...
    bool isNativePointer() const { return m_pattern == NativePointerPattern; }

    // returns true if the type was originally a QString or const QString & or equivalent for QLatin1String
    bool isJavaString() const { return m_pattern == StringPattern; }

    // returns true if the type was originally a QChar or const QChar &
    bool isJavaChar() const { return m_pattern == CharPattern; }

    // return true if the type was originally a QVariant or const QVariant &
    bool isVariant() const { return m_pattern == VariantPattern; }

    // returns true if the type was used as a container
    bool isContainer() const { return m_pattern == ContainerPattern; }

    // returns true if the type was used as a flag
    bool isFlags() const { return m_pattern == FlagsPattern; }

    // returns true if the type was used as a thread
    bool isThread() const { return m_pattern == ThreadPattern; }

    bool isConstant() const { return m_constant; }
    void setConstant(bool constant) { m_constant = constant; }

    bool isReference() const { return m_reference; }
    void setReference(bool ref) { m_reference = ref; }

    // Returns true if the type is to be implemented using Java enums, e.g. not plain ints.
    bool isJavaEnum() const { return isEnum() && !((EnumTypeEntry *) typeEntry())->forceInteger(); }
    bool isIntegerEnum() const { return isEnum() && !isJavaEnum(); }

    // Returns true if the type is to be implemented using Java QFlags, e.g. not plain ints.
    bool isJavaFlags() const {
        return isFlags() && !((FlagsTypeEntry *) typeEntry())->forceInteger(); }
    bool isIntegerFlags() const { return isFlags() && !isJavaFlags(); }

    int actualIndirections() const { return m_indirections + (isReference() ? 1 : 0); }
    int indirections() const { return m_indirections; }
    void setIndirections(int indirections) { m_indirections = indirections; }

    void setArrayElementCount(int n) { m_array_element_count = n; }
    int arrayElementCount() const { return m_array_element_count; }

    MetaJavaType *arrayElementType() const { return m_array_element_type; }
    void setArrayElementType(MetaJavaType *t) { m_array_element_type = t; }

    QString cppSignature() const;

    MetaJavaType *copy() const;

    const TypeEntry *typeEntry() const { return m_type_entry; }
    void setTypeEntry(const TypeEntry *type) { m_type_entry = type; }

    void setOriginalTypeDescription(const QString &otd) { m_original_type_description = otd; }
    QString originalTypeDescription() const { return m_original_type_description; }

private:
    const TypeEntry *m_type_entry;
    QList <MetaJavaType *> m_instantiations;
    QString m_package;
    QString m_original_type_description;

    int m_array_element_count;
    MetaJavaType *m_array_element_type;

    TypeUsagePattern m_pattern;
    uint m_constant : 1;
    uint m_reference : 1;
    uint m_cpp_instantiation : 1;
    short m_indirections : 4;
};


class MetaJavaVariable
{
public:
    MetaJavaVariable() : m_type(0) { }

    MetaJavaType *type() const { return m_type; }
    void setType(MetaJavaType *type) { m_type = type; }

    QString name() const { return m_name; }
    void setName(const QString &name) { m_name = name; }

private:
    QString m_name;
    MetaJavaType *m_type;
};



class MetaJavaArgument : public MetaJavaVariable
{
public:
    MetaJavaArgument() : m_argument_index(0) { };

    QString defaultValueExpression() const { return m_expression; }
    void setDefaultValueExpression(const QString &expr) { m_expression = expr; }

    QString originalDefaultValueExpression() const { return m_original_expression; }
    void setOriginalDefaultValueExpression(const QString &expr) { m_original_expression = expr; }

    QString toString() const { return type()->name() + " " + MetaJavaVariable::name() +
                                           (m_expression.isEmpty() ? "" :  " = " + m_expression); }

    int argumentIndex() const { return m_argument_index; }
    void setArgumentIndex(int argIndex) { m_argument_index = argIndex; }

    QString argumentName() const;
    QString indexedName() const;

    MetaJavaArgument *copy() const;

private:
    // Just to force people to call argumentName() And indexedName();
    QString name() const;

    QString m_expression;
    QString m_original_expression;
    int m_argument_index;
};


class MetaJavaField : public MetaJavaVariable, public MetaJavaAttributes
{
public:
    MetaJavaField();
    ~MetaJavaField();

    const MetaJavaClass *enclosingClass() const { return m_class; }
    void setEnclosingClass(const MetaJavaClass *cls) { m_class = cls; }

    const MetaJavaFunction *getter() const;
    const MetaJavaFunction *setter() const;

    FieldModificationList modifications() const;

    MetaJavaField *copy() const;

private:
    mutable MetaJavaFunction *m_getter;
    mutable MetaJavaFunction *m_setter;
    const MetaJavaClass *m_class;
};


class MetaJavaFunction : public MetaJavaAttributes
{
public:
    enum FunctionType {
        ConstructorFunction,
        DestructorFunction,
        NormalFunction,
        SignalFunction,
        EmptyFunction,
        SlotFunction
    };

    enum CompareResult {
        EqualName                   = 0x0001,
        EqualArguments              = 0x0002,
        EqualAttributes             = 0x0004,
        EqualImplementor            = 0x0008,
        EqualReturnType             = 0x0010,
        EqualDefaultValueOverload   = 0x0020,

        NameLessThan                = 0x1000,

        PrettySimilar               = EqualName | EqualArguments,
        Equal                       = 0x001f,
        NotEqual                    = 0x1000
    };

    MetaJavaFunction()
        : m_function_type(NormalFunction),
          m_type(0),
          m_class(0),
          m_implementing_class(0),
          m_declaring_class(0),
          m_interface_class(0),
          m_constant(false),
          m_invalid(false)
    {
    }

    ~MetaJavaFunction();

    QString name() const { return m_name; }
    void setName(const QString &name) { m_name = name; }

    QString originalName() const { return m_original_name.isEmpty() ? name() : m_original_name; }
    void setOriginalName(const QString &name) { m_original_name = name; }

    QString modifiedName() const;

    QString minimalSignature() const;

    QString marshalledName() const;

    // true if one or more of the arguments are of QtJambiObject subclasses
    bool argumentsHaveNativeId() const
    {
        foreach (const MetaJavaArgument *arg, m_arguments) {
            if (arg->type()->hasNativeId())
                return true;
        }

        return false;
    }

    bool isModifiedRemoved(int types = TypeSystem::All) const;

    MetaJavaType *type() const { return m_type; }
    void setType(MetaJavaType *type) { m_type = type; }

    // The class that has this function as a member.
    const MetaJavaClass *ownerClass() const { return m_class; }
    void setOwnerClass(const MetaJavaClass *cls) { m_class = cls; }

    // The first class in a hierarchy that declares the function
    const MetaJavaClass *declaringClass() const { return m_declaring_class; }
    void setDeclaringClass(const MetaJavaClass *cls) { m_declaring_class = cls; }

    // The class that actually implements this function
    const MetaJavaClass *implementingClass() const { return m_implementing_class; }
    void setImplementingClass(const MetaJavaClass *cls) { m_implementing_class = cls; }

    bool needsCallThrough() const;

    MetaJavaArgumentList arguments() const { return m_arguments; }
    void setArguments(const MetaJavaArgumentList &arguments) { m_arguments = arguments; }
    void addArgument(MetaJavaArgument *argument) { m_arguments << argument; }
    int actualMinimumArgumentCount() const;

    void setInvalid(bool on) { m_invalid = on; }
    bool isInvalid() const { return m_invalid; }
    bool isDestructor() const { return functionType() == DestructorFunction; }
    bool isConstructor() const { return functionType() == ConstructorFunction; }
    bool isNormal() const { return functionType() == NormalFunction || isSlot(); }
    bool isSignal() const { return functionType() == SignalFunction; }
    bool isSlot() const { return functionType() == SlotFunction; }
    bool isEmptyFunction() const { return functionType() == EmptyFunction; }
    FunctionType functionType() const { return m_function_type; }
    void setFunctionType(FunctionType type) { m_function_type = type; }

    QString signature() const;
    QString javaSignature(bool minimal = false) const;

    bool isConstant() const { return m_constant; }
    void setConstant(bool constant) { m_constant = constant; }

    QString toString() const { return m_name; }

    uint compareTo(const MetaJavaFunction *other) const;

    bool operator <(const MetaJavaFunction &a) const;

    MetaJavaFunction *copy() const;

    QString replacedDefaultExpression(const MetaJavaClass *cls, int idx) const;
    bool removedDefaultExpression(const MetaJavaClass *cls, int idx) const;
    QString conversionRule(TypeSystem::Language language, int idx) const;

    bool nullPointersDisabled(const MetaJavaClass *cls = 0, int argument_idx = 0) const;
    QString nullPointerDefaultValue(const MetaJavaClass *cls = 0, int argument_idx = 0) const;

    // Returns whether garbage collection is disabled for the argument in any context
    bool MetaJavaFunction::disabledGarbageCollection(const MetaJavaClass *cls, int key) const;

    // Returns the ownership rules for the given argument in the given context
    TypeSystem::Ownership ownership(const MetaJavaClass *cls, TypeSystem::Language language, int idx) const;

    QString typeReplaced(int argument_index) const;
    bool isRemovedFromAllLanguages(const MetaJavaClass *) const;
    bool isRemovedFrom(const MetaJavaClass *, TypeSystem::Language language) const;
    bool argumentRemoved(int) const;

    bool hasModifications(const MetaJavaClass *implementor) const;
    FunctionModificationList modifications(const MetaJavaClass *implementor) const;

    // If this function stems from an interface, this returns the
    // interface that declares it.
    const MetaJavaClass *interfaceClass() const { return m_interface_class; }
    void setInterfaceClass(const MetaJavaClass *cl) { m_interface_class = cl; }



private:
    QString m_name;
    QString m_original_name;
    QString m_cached_minimal_signature;

    FunctionType m_function_type;
    MetaJavaType *m_type;
    const MetaJavaClass *m_class;
    const MetaJavaClass *m_implementing_class;
    const MetaJavaClass *m_declaring_class;
    const MetaJavaClass *m_interface_class;
    MetaJavaArgumentList m_arguments;
    uint m_constant : 1;
    uint m_invalid  : 1;
};


class MetaJavaEnumValue
{
public:
    MetaJavaEnumValue()
        : m_value_set(false), m_value(0)
    {
    }

    int value() const { return m_value; }
    void setValue(int value) { m_value_set = true; m_value = value; }

    QString stringValue() const { return m_string_value; }
    void setStringValue(const QString &v) { m_string_value = v; }

    QString name() const { return m_name; }
    void setName(const QString &name) { m_name = name; }

    bool isValueSet() const { return m_value_set; }

private:
    QString m_name;
    QString m_string_value;

    bool m_value_set;
    int m_value;
};


class MetaJavaEnumValueList : public QList<MetaJavaEnumValue *>
{
public:
    MetaJavaEnumValue *find(const QString &name) const;
};

class MetaJavaEnum : public MetaJavaAttributes
{
public:
    MetaJavaEnum() : m_type_entry(0), m_class(0){}

    MetaJavaEnumValueList values() const { return m_enum_values; }
    void addEnumValue(MetaJavaEnumValue *enumValue) { m_enum_values << enumValue; }

    QString name() const { return m_type_entry->javaName(); }
    QString qualifier() const { return m_type_entry->javaQualifier(); }
    QString package() const { return m_type_entry->javaPackage(); }
    QString fullName() const { return package() + "." + qualifier()  + "." + name(); }

    EnumTypeEntry *typeEntry() const { return m_type_entry; }
    void setTypeEntry(EnumTypeEntry *entry) { m_type_entry = entry; }

    MetaJavaClass *enclosingClass() const { return m_class; }
    void setEnclosingClass(MetaJavaClass *c) { m_class = c; }

private:
    MetaJavaEnumValueList m_enum_values;
    EnumTypeEntry *m_type_entry;
    MetaJavaClass *m_class;
};

typedef QList<MetaJavaEnum *> MetaJavaEnumList;

class MetaJavaClass : public MetaJavaAttributes
{
public:
    enum FunctionQueryOption {
        Constructors            = 0x000001,   // Only constructors
        //Destructors             = 0x000002,   // Only destructors. Not included in class.
        VirtualFunctions        = 0x000004,   // Only virtual functions (virtual in both Java and C++)
        FinalInJavaFunctions    = 0x000008,   // Only functions that are non-virtual in Java
        FinalInCppFunctions     = 0x000010,   // Only functions that are non-virtual in C++
        ClassImplements         = 0x000020,   // Only functions implemented by the current class
        Inconsistent            = 0x000040,   // Only inconsistent functions (inconsistent virtualness in Java/C++)
        StaticFunctions         = 0x000080,   // Only static functions
        Signals                 = 0x000100,   // Only signals
        NormalFunctions         = 0x000200,   // Only functions that aren't signals
        Visible                 = 0x000400,   // Only public and protected functions
        ForcedShellFunctions    = 0x000800,   // Only functions that are overridden to be implemented in the shell class
        WasPublic               = 0x001000,   // Only functions that were originally public
        WasProtected            = 0x002000,   // Only functions that were originally protected
        NonStaticFunctions      = 0x004000,   // No static functions
        Empty                   = 0x008000,   // Empty overrides of abstract functions
        Invisible               = 0x010000,   // Only private functions
        VirtualInCppFunctions   = 0x020000,   // Only functions that are virtual in C++
        NonEmptyFunctions       = 0x040000,   // Only functions with JNI implementations
        VirtualInJavaFunctions  = 0x080000,   // Only functions which are virtual in Java
        AbstractFunctions       = 0x100000,   // Only abstract functions
        WasVisible              = 0x200000,   // Only functions that were public or protected in the original code
        NotRemovedFromJava      = 0x400000,   // Only functions that have not been removed from Java
        NotRemovedFromShell     = 0x800000    // Only functions that have not been removed from the shell class
    };

    MetaJavaClass()
        : m_namespace(false),
          m_qobject(false),
          m_has_virtuals(false),
          m_has_nonpublic(false),
          m_has_nonprivateconstructor(false),
          m_functions_fixed(false),
          m_has_public_destructor(true),
          m_force_shell_class(false),
          m_has_hash_function(false),
          m_has_equals_operator(false),
          m_enclosing_class(0),
          m_base_class(0),
          m_extracted_interface(0),
          m_primary_interface_implementor(0),
          m_type_entry(0)
    {
    }

    ~MetaJavaClass();

    MetaJavaClass *extractInterface();
    void fixFunctions();

    MetaJavaFunctionList functions() const { return m_functions; }
    void setFunctions(const MetaJavaFunctionList &functions);
    void addFunction(MetaJavaFunction *function);
    bool hasFunction(const MetaJavaFunction *f) const;
    bool hasFunction(const QString &str) const;
    bool hasSignal(const MetaJavaFunction *f) const;

    bool hasConstructors() const;

    void addDefaultConstructor();

    bool hasNonPrivateConstructor() const { return m_has_nonprivateconstructor; }
    void setHasNonPrivateConstructor(bool on) { m_has_nonprivateconstructor = on; }
    bool hasPublicDestructor() const { return m_has_public_destructor; }
    void setHasPublicDestructor(bool on) { m_has_public_destructor = on; }

    MetaJavaFunctionList queryFunctionsByName(const QString &name) const;
    MetaJavaFunctionList queryFunctions(uint query) const;
    inline MetaJavaFunctionList allVirtualFunctions() const;
    inline MetaJavaFunctionList allFinalFunctions() const;
    MetaJavaFunctionList functionsInJava() const;
    MetaJavaFunctionList functionsInShellClass() const;
    inline MetaJavaFunctionList cppInconsistentFunctions() const;
    inline MetaJavaFunctionList cppSignalFunctions() const;
    MetaJavaFunctionList publicOverrideFunctions() const;
    MetaJavaFunctionList virtualOverrideFunctions() const;

    MetaJavaFieldList fields() const { return m_fields; }
    void setFields(const MetaJavaFieldList &fields) { m_fields = fields; }
    void addField(MetaJavaField *field) { m_fields << field; }

    MetaJavaEnumList enums() const { return m_enums; }
    void setEnums(const MetaJavaEnumList &enums) { m_enums = enums; }
    void addEnum(MetaJavaEnum *e) { m_enums << e; }

    MetaJavaEnum *findEnum(const QString &enumName);
    MetaJavaEnum *findEnumForValue(const QString &enumName);
    MetaJavaEnumValue *findEnumValue(const QString &enumName, MetaJavaEnum *java_enum);

    MetaJavaClassList interfaces() const { return m_interfaces; }
    void addInterface(MetaJavaClass *interface);
    void setInterfaces(const MetaJavaClassList &interface);

    QString fullName() const { return package() + "." + name(); }
    QString name() const;

    QString baseClassName() const { return m_base_class ? m_base_class->name() : QString(); }

    MetaJavaClass *baseClass() const { return m_base_class; }
    void setBaseClass(MetaJavaClass *base_class) { m_base_class = base_class; }

    const MetaJavaClass *enclosingClass() const { return m_enclosing_class; }
    void setEnclosingClass(MetaJavaClass *cl) { m_enclosing_class = cl; }

    QString package() const { return m_type_entry->javaPackage(); }
    bool isInterface() const { return m_type_entry->isInterface(); }
    bool isNamespace() const { return m_type_entry->isNamespace(); }
    bool isQObject() const { return m_type_entry->isQObject(); }
    QString qualifiedCppName() const { return m_type_entry->qualifiedCppName(); }

    bool hasInconsistentFunctions() const;
    bool hasSignals() const;
    bool inheritsFrom(const MetaJavaClass *other) const;

    void setForceShellClass(bool on) { m_force_shell_class = on; }
    bool generateShellClass() const;

    bool hasVirtualFunctions() const { return !isFinal() && m_has_virtuals; }
    bool hasProtectedFunctions() const;

    bool hasFieldAccessors() const;

    // only valid during metajavabuilder's run
    QStringList baseClassNames() const { return m_base_class_names; }
    void setBaseClassNames(const QStringList &names) { m_base_class_names = names; }

    MetaJavaClass *primaryInterfaceImplementor() const { return m_primary_interface_implementor; }
    void setPrimaryInterfaceImplementor(MetaJavaClass *cl) { m_primary_interface_implementor = cl; }

    const ComplexTypeEntry *typeEntry() const { return m_type_entry; }
    ComplexTypeEntry *typeEntry() { return m_type_entry; }
    void setTypeEntry(ComplexTypeEntry *type) { m_type_entry = type; }

    void setHasHashFunction(bool on) { m_has_hash_function = on; }
    bool hasHashFunction() const { return m_has_hash_function; }

    void setHasEqualsOperator(bool on) { m_has_equals_operator = on; }
    bool hasEqualsOperator() const { return m_has_equals_operator; }

private:
    uint m_namespace : 1;
    uint m_qobject : 1;
    uint m_has_virtuals : 1;
    uint m_has_nonpublic : 1;
    uint m_has_nonprivateconstructor : 1;
    uint m_functions_fixed : 1;
    uint m_has_public_destructor : 1;
    uint m_force_shell_class : 1;

    uint m_has_hash_function : 1;
    uint m_has_equals_operator : 1;

    const MetaJavaClass *m_enclosing_class;
    MetaJavaClass *m_base_class;
    MetaJavaFunctionList m_functions;
    MetaJavaFieldList m_fields;
    MetaJavaEnumList m_enums;
    MetaJavaClassList m_interfaces;
    MetaJavaClass *m_extracted_interface;
    MetaJavaClass *m_primary_interface_implementor;

    QStringList m_base_class_names;
    ComplexTypeEntry *m_type_entry;
};

inline MetaJavaFunctionList MetaJavaClass::allVirtualFunctions() const
{
    return queryFunctions(VirtualFunctions
                          | NotRemovedFromJava);
}

inline MetaJavaFunctionList MetaJavaClass::allFinalFunctions() const
{
    return queryFunctions(FinalInJavaFunctions
                          | FinalInCppFunctions
                          | NotRemovedFromJava);
}

inline MetaJavaFunctionList MetaJavaClass::cppInconsistentFunctions() const
{
    return queryFunctions(Inconsistent
                          | NormalFunctions
                          | Visible
                          | NotRemovedFromJava);
}

inline MetaJavaFunctionList MetaJavaClass::cppSignalFunctions() const
{
    return queryFunctions(Signals
                          | Visible
                          | NotRemovedFromJava);
}

#endif // METAJAVA_H
