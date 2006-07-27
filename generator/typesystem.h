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

#ifndef TYPESYSTEM_H
#define TYPESYSTEM_H

#include <QtCore/QHash>
#include <QtCore/QString>
#include <QtCore/QStringList>
#include <QtCore/QMap>
#include <QDebug>

class MetaJavaType;
class QTextStream;

struct Include
{
    enum IncludeType {
        IncludePath,
        LocalPath,
        JavaImport
    };

    Include() : type(IncludePath) { }
    Include(IncludeType t, const QString &nam) : type(t), name(nam) { };

    IncludeType type;
    QString name;

    QString toString() const;
};
typedef QList<Include> IncludeList;

struct CustomFunction
{
    CustomFunction(const QString &n = QString()) : name(n) { }

    QString name;
    QString param_name;
    QString code;
};

typedef QMap<int, QString> ArgumentMap;
struct CodeSnip
{
    enum Language {
        JavaCode,
        NativeCode,
        ShellCode,
        ShellDeclaration
    };

    enum Position {
        Beginning,
        End
    };

    CodeSnip() : language(JavaCode) { }
    CodeSnip(Language lang, const QString &c) : language(lang), code(c) { }

    // Very simple, easy to make code ugly if you try
    QString formattedCode(const QString &_defaultIndent);

    Language language;
    QString code;
    Position position;
    ArgumentMap argumentMap;
};
typedef QList<CodeSnip> CodeSnipList;

struct FunctionModification
{
    enum Modifiers {
        Private =               0x0001,
        Protected =             0x0002,
        Public =                0x0003,
        Friendly =              0x0004,
        AccessModifierMask =    0x0007,

        Remove =                0x0010,
        CodeInjection =         0x0020,
        Rename =                0x0040,
        Exclusive =             0x0080,
        ReplaceExpression =     0x0100
    };

    FunctionModification() : modifiers(0) { }

    bool isAccessModifier() const { return modifiers & AccessModifierMask; }
    Modifiers accessModifier() const { return Modifiers(modifiers & AccessModifierMask); }
    bool isPrivate() const { return accessModifier() == Private; }
    bool isExclusive() const { return modifiers & Exclusive; }
    bool isProtected() const { return accessModifier() == Protected; }
    bool isPublic() const { return accessModifier() == Public; }
    bool isFriendly() const { return accessModifier() == Friendly; }
    QString accessModifierString() const;

    bool isCodeInjection() const { return modifiers & CodeInjection; }
    bool isRenameModifier() const { return modifiers & Rename; }
    bool isRemoveModifier() const { return modifiers & Remove; }
    bool isDisableGCModifier() const { return disable_gc_argument_indexes.count() > 0; }
    bool isReplaceExpression() const { return modifiers & ReplaceExpression; }

    void setRenamedTo(const QString &name) { renamedToName = name; }
    QString renamedTo() const { return renamedToName; }

    QString signature;
    QString renamedToName;
    uint modifiers;
    CodeSnipList snips;
    CodeSnip::Language language;
    QHash<int, bool> disable_gc_argument_indexes;
    QHash<int, QString> renamed_default_expressions;

};
typedef QList<FunctionModification> FunctionModificationList;

struct FieldModification
{
    enum Modifiers {
        Readable        = 0x0001,
        Writable        = 0x0002
    };

    bool isReadable() const { return modifiers & Readable; }
    bool isWritable() const { return modifiers & Writable; }

    QString name;
    uint modifiers;
};
typedef QList<FieldModification> FieldModificationList;

class InterfaceTypeEntry;
class ObjectTypeEntry;

class TypeEntry
{
public:
    enum Type {
        PrimitiveType,
        VoidType,
        FlagsType,
        EnumType,
        TemplateArgumentType,
        ThreadType,
        BasicValueType,
        StringType,
        ContainerType,
        InterfaceType,
        ObjectType,
        NamespaceType,
        VariantType,
        CharType,
        ArrayType,
        CustomType
    };

    enum CodeGeneration {
        GenerateJava            = 0x0001,
        GenerateCpp             = 0x0002,
        GenerateForSubclass     = 0x0004,

        GenerateNothing         = 0,
        GenerateAll             = 0xffff
    };

    TypeEntry(const QString &name, Type t)
        : m_name(name),
          m_type(t),
          m_code_generation(GenerateAll),
          m_preferred_conversion(true)
    {
    };

    virtual ~TypeEntry() { }

    Type type() const { return m_type; }
    bool isPrimitive() const { return m_type == PrimitiveType; }
    bool isEnum() const { return m_type == EnumType; }
    bool isFlags() const { return m_type == FlagsType; }
    bool isInterface() const { return m_type == InterfaceType; }
    bool isObject() const { return m_type == ObjectType; }
    bool isString() const { return m_type == StringType; }
    bool isChar() const { return m_type == CharType; }
    bool isNamespace() const { return m_type == NamespaceType; }
    bool isContainer() const { return m_type == ContainerType; }
    bool isVariant() const { return m_type == VariantType; }
    bool isArray() const { return m_type == ArrayType; }
    bool isTemplateArgument() const { return m_type == TemplateArgumentType; }
    bool isVoid() const { return m_type == VoidType; }
    bool isThread() const { return m_type == ThreadType; }
    bool isCustom() const { return m_type == CustomType; }
    bool isBasicValue() const { return m_type == BasicValueType; }

    virtual bool preferredConversion() const { return m_preferred_conversion; }
    virtual void setPreferredConversion(bool b) { m_preferred_conversion = b; }

    // The type's name in C++, fully qualified
    QString name() const { return m_name; }

    uint codeGeneration() const { return m_code_generation; }
    void setCodeGeneration(uint cg) { m_code_generation = cg; }

    virtual QString qualifiedCppName() const { return m_name; }

    // Its type's name in JNI
    virtual QString jniName() const { return m_name; }

    // The type's name in Java
    virtual QString javaName() const { return m_name; }

    // The package
    virtual QString javaPackage() const { return QString(); }

    virtual QString qualifiedJavaName() const {
        QString pkg = javaPackage();
        if (pkg.isEmpty()) return javaName();
        return pkg + '.' + javaName();
    }

    virtual InterfaceTypeEntry *designatedInterface() const { return 0; }


    void setCustomConstructor(const CustomFunction &func) { m_customConstructor = func; }
    CustomFunction customConstructor() const { return m_customConstructor; }

    void setCustomDestructor(const CustomFunction &func) { m_customDestructor = func; }
    CustomFunction customDestructor() const { return m_customDestructor; }

    virtual bool isValue() const { return false; }
    virtual bool isComplex() const { return false; }

    virtual bool isNativeIdBased() const { return false; }

private:
    QString m_name;
    Type m_type;
    uint m_code_generation;
    CustomFunction m_customConstructor;
    CustomFunction m_customDestructor;
    bool m_preferred_conversion;
};
typedef QHash<QString, TypeEntry *> TypeEntryHash;

class ThreadTypeEntry : public TypeEntry
{
public:
    ThreadTypeEntry() : TypeEntry("QThread", ThreadType) { setCodeGeneration(GenerateNothing); }

    QString jniName() const { return "jobject"; }
    QString javaName() const { return "Thread"; }
    QString javaPackage() const { return "java.lang"; }
};

class VoidTypeEntry : public TypeEntry
{
public:
    VoidTypeEntry() : TypeEntry("void", VoidType) { }
};

class TemplateArgumentEntry : public TypeEntry
{
public:
    TemplateArgumentEntry(const QString &name)
        : TypeEntry(name, TemplateArgumentType), m_ordinal(0)
    {
    }

    int ordinal() const { return m_ordinal; }
    void setOrdinal(int o) { m_ordinal = o; }

private:
    int m_ordinal;
};


class FlagsTypeEntry : public TypeEntry
{
public:
    FlagsTypeEntry(const QString &name) : TypeEntry(name, FlagsType) { }

    QString javaName() const { return "int"; }
    QString jniName() const { return "jint"; }
    virtual bool preferredConversion() const { return false; }

    QString originalName() const { return m_original_name; }
    void setOriginalName(const QString &s) { m_original_name = s; }

private:
    QString m_original_name;
};

class ArrayTypeEntry : public TypeEntry
{
public:
    ArrayTypeEntry(const TypeEntry *nested_type) : TypeEntry("Array", ArrayType), m_nested_type(nested_type)
    {
        Q_ASSERT(m_nested_type);
    }

    void setNestedTypeEntry(TypeEntry *nested) { m_nested_type = nested; }
    const TypeEntry *nestedTypeEntry() const { return m_nested_type; }

    QString javaName() const { return m_nested_type->javaName() + "[]"; }
    QString jniName() const
    {
        if (m_nested_type->isPrimitive())
            return m_nested_type->jniName() + "Array";
        else
            return "jobjectArray";
    }

private:
    const TypeEntry *m_nested_type;
};


class PrimitiveTypeEntry : public TypeEntry
{
public:
    PrimitiveTypeEntry(const QString &name)
        : TypeEntry(name, PrimitiveType), m_preferred_conversion(true)
    {
    }

    QString javaName() const { return m_java_name; }
    void setJavaName(const QString &javaName) { m_java_name  = javaName; }

    QString jniName() const { return m_jni_name; }
    void setJniName(const QString &jniName) { m_jni_name = jniName; }

    QString javaObjectFullName() const { return javaObjectPackage() + "." + javaObjectName(); }
    QString javaObjectName() const;
    QString javaObjectPackage() const { return "java.lang"; }

    virtual bool preferredConversion() const { return m_preferred_conversion; }
    virtual void setPreferredConversion(bool b) { m_preferred_conversion = b; }

private:
    QString m_java_name;
    QString m_jni_name;
    bool m_preferred_conversion;
};



class EnumTypeEntry : public TypeEntry
{
public:
    EnumTypeEntry(const QString &name)
        : TypeEntry(name, EnumType), m_qualified_cpp_name(name)
    {
        QStringList splitted = name.split("::");
        Q_ASSERT(splitted.size() == 2);
        m_qualifier = splitted.at(0);
        m_java_name = splitted.at(1);
    }

    QString javaPackage() const { return m_package_name; }
    void setJavaPackage(const QString &package) { m_package_name = package; }

    QString javaName() const { return m_java_name; }
    QString javaQualifier() const { return m_qualifier; }
    QString qualifiedJavaName() const {
        QString pkg = javaPackage();
        if (pkg.isEmpty()) return javaQualifier() + '.' + javaName();
        return pkg + '.' + javaQualifier() + '.' + javaName();
    }

    QString qualifiedCppName() const { return m_qualified_cpp_name; }

    virtual bool preferredConversion() const { return false; }

private:
    QString m_qualified_cpp_name;
    QString m_package_name;
    QString m_qualifier;
    QString m_java_name;
};


class ComplexTypeEntry : public TypeEntry
{
public:
    ComplexTypeEntry(const QString &name, Type t)
        : TypeEntry(QString(name).replace("::", "_"), t),
          m_qualified_cpp_name(name),
          m_qobject(false)
    {
        Include inc;
        inc.name = "QVariant";
        inc.type = Include::IncludePath;

        addExtraInclude(inc);
    }

    bool isComplex() const { return true; }

    IncludeList extraIncludes() const { return m_extra_includes; }
    void setExtraIncludes(const IncludeList &includes) { m_extra_includes = includes; }
    void addExtraInclude(const Include &include) { m_extra_includes << include; }

    Include include() const { return m_include; }
    void setInclude(const Include &inc) { m_include = inc; }

    CodeSnipList codeSnips() const { return m_code_snips; }
    void setCodeSnips(const CodeSnipList &codeSnips) { m_code_snips = codeSnips; }
    void addCodeSnip(const CodeSnip &codeSnip) { m_code_snips << codeSnip; }

    FunctionModificationList functionModifications() const { return m_function_mods; }
    void setFunctionModifications(const FunctionModificationList &functionModifications) {
        m_function_mods = functionModifications;
    }
    void addFunctionModification(const FunctionModification &functionModification) {
        m_function_mods << functionModification;
    }
    FunctionModificationList functionModifications(const QString &signature) const;

    FieldModification fieldModification(const QString &name) const;
    void setFieldModifications(const FieldModificationList &mods) { m_field_mods = mods; }

    QString javaPackage() const { return m_package; }
    void setJavaPackage(const QString &package) { m_package = package; }

    bool isQObject() const { return m_qobject; }
    void setQObject(bool qobject) { m_qobject = qobject; }

    QString defaultSuperclass() const { return m_default_superclass; }
    void setDefaultSuperclass(const QString &sc) { m_default_superclass = sc; }

    virtual QString qualifiedCppName() const { return m_qualified_cpp_name; }

private:
    IncludeList m_extra_includes;
    Include m_include;
    FunctionModificationList m_function_mods;
    FieldModificationList m_field_mods;
    CodeSnipList m_code_snips;
    QString m_package;
    QString m_default_superclass;
    QString m_qualified_cpp_name;
    uint m_qobject : 1;
};


class ContainerTypeEntry : public ComplexTypeEntry
{
public:
    enum Type {
        NoContainer,
        ListContainer,
        StringListContainer,
        LinkedListContainer,
        VectorContainer,
        StackContainer,
        QueueContainer,
        SetContainer,
        MapContainer,
        MultiMapContainer,
        HashContainer,
        MultiHashContainer,
        PairContainer,
    };

    ContainerTypeEntry(const QString &name, Type type)
        : ComplexTypeEntry(name, ContainerType)
    {
        m_type = type;
        setCodeGeneration(GenerateForSubclass);
    }

    Type type() const { return m_type; }
    QString javaName() const;
    QString javaPackage() const;
    QString qualifiedCppName() const;

private:
    Type m_type;
};


class NamespaceTypeEntry : public ComplexTypeEntry
{
public:
    NamespaceTypeEntry(const QString &name) : ComplexTypeEntry(name, NamespaceType) { }
};


class ValueTypeEntry : public ComplexTypeEntry
{
public:
    ValueTypeEntry(const QString &name) : ComplexTypeEntry(name, BasicValueType) { }

    bool isValue() const { return true; }

    virtual bool isNativeIdBased() const { return true; }

protected:
    ValueTypeEntry(const QString &name, Type t) : ComplexTypeEntry(name, t) { }
};


class StringTypeEntry : public ValueTypeEntry
{
public:
    StringTypeEntry(const QString &name)
        : ValueTypeEntry(name, StringType)
    {
        setCodeGeneration(GenerateNothing);
    }

    QString jniName() const { return "jobject"; }
    QString javaName() const { return "String"; }
    QString javaPackage() const { return "java.lang"; }

    virtual bool isNativeIdBased() const { return false; }
};

class CharTypeEntry : public ValueTypeEntry
{
public:
    CharTypeEntry(const QString &name) : ValueTypeEntry(name, CharType)
    {
        setCodeGeneration(GenerateNothing);
    }

    QString jniName() const { return "jchar"; }
    QString javaName() const { return "char"; }
    QString javaPackage() const { return ""; }

    virtual bool isNativeIdBased() const { return false; }
};

class VariantTypeEntry: public ValueTypeEntry
{
public:
    VariantTypeEntry(const QString &name) : ValueTypeEntry(name, VariantType) { }

    QString jniName() const { return "jobject"; }
    QString javaName() const { return "Object"; }
    QString javaPackage() const { return "java.lang"; }

    virtual bool isNativeIdBased() const { return false; }
};


class InterfaceTypeEntry : public ComplexTypeEntry
{
public:
    InterfaceTypeEntry(const QString &name)
        : ComplexTypeEntry(name, InterfaceType)
    {
    }

    static QString interfaceName(const QString &name) {
        return name + "Interface";
    }

    ObjectTypeEntry *origin() const { return m_origin; }
    void setOrigin(ObjectTypeEntry *origin) { m_origin = origin; }

    virtual bool isNativeIdBased() const { return true; }
    virtual QString qualifiedCppName() const { 
        return name().left(name().length() - interfaceName("").length());
    }

private:
    ObjectTypeEntry *m_origin;
};


class ObjectTypeEntry : public ComplexTypeEntry
{
public:
    ObjectTypeEntry(const QString &name)
        : ComplexTypeEntry(name, ObjectType), m_interface(0), m_memory_managed(false)
    {
    }

    InterfaceTypeEntry *designatedInterface() const { return m_interface; }
    void setDesignatedInterface(InterfaceTypeEntry *entry) { m_interface = entry; }

    bool isMemoryManaged() const { return m_memory_managed; }
    void setMemoryManaged(bool mm) { m_memory_managed = mm; }

    virtual bool isNativeIdBased() const { return true; }

private:
    InterfaceTypeEntry *m_interface;
    uint m_memory_managed : 1;
};

class CustomTypeEntry : public ComplexTypeEntry
{
public:
    CustomTypeEntry(const QString &name) : ComplexTypeEntry(name, CustomType) { }

    virtual void generateCppJavaToQt(QTextStream &s,
                                     const MetaJavaType *java_type,
                                     const QString &env_name,
                                     const QString &qt_name,
                                     const QString &java_name) const = 0;

    virtual void generateCppQtToJava(QTextStream &s,
                                     const MetaJavaType *java_type,
                                     const QString &env_name,
                                     const QString &qt_name,
                                     const QString &java_name) const = 0;
};

struct TypeRejection
{
    QString class_name;
    QString function_name;
    QString field_name;
};

class TypeDatabase
{
public:
    TypeDatabase();

    static TypeDatabase *instance();

    QList<Include> extraIncludes(const QString &className);

    inline PrimitiveTypeEntry *findPrimitiveType(const QString &name);
    inline ComplexTypeEntry *findComplexType(const QString &name);
    inline ObjectTypeEntry *findObjectType(const QString &name);
    inline NamespaceTypeEntry *findNamespaceType(const QString &name);
    ContainerTypeEntry *findContainerType(const QString &name);

    TypeEntry *findType(const QString &name) { return m_entries[name]; }
    TypeEntryHash entries() { return m_entries; }

    PrimitiveTypeEntry *findJavaPrimitiveType(const QString &java_name);

    void addRejection(const QString &class_name, const QString &function_name,
                      const QString &field_name);
    bool isClassRejected(const QString &class_name);
    bool isFunctionRejected(const QString &class_name, const QString &function_name);
    bool isFieldRejected(const QString &class_name, const QString &field_name);

    void addType(TypeEntry *e) { m_entries[e->qualifiedCppName()] = e; }

    void setSuppressWarnings(bool on) { m_suppressWarnings = on; }
    void addSuppressedWarning(const QString &s)
    {
        m_suppressedWarnings.append(s);
    }

    bool isSuppressedWarning(const QString &s)
    {
        foreach (const QString &_warning, m_suppressedWarnings) {
            QString warning(QString(_warning).replace("\\*", "&place_holder_for_asterisk;"));

            QStringList segs = warning.split("*", QString::SkipEmptyParts);
            if (segs.size() == 0)
                continue ;

            int i = 0;
            int pos = s.indexOf(QString(segs.at(i++)).replace("&place_holder_for_asterisk;", "*"));
            //qDebug() << "s == " << s << ", warning == " << segs;
            while (pos != -1) {
                if (i == segs.size())
                    return true;
                pos = s.indexOf(QString(segs.at(i++)).replace("&place_holder_for_asterisk;", "*"), pos);
            }
        }

        return false;
    }

    void setRebuildClasses(const QStringList &cls) { m_rebuild_classes = cls; }

    QString filename() const { return "typesystem.txt"; }

    bool parseFile(const QString &filename, bool generate = true);

private:
    bool m_suppressWarnings;
    TypeEntryHash m_entries;
    QStringList m_suppressedWarnings;

    QList<TypeRejection> m_rejections;
    QStringList m_rebuild_classes;
};

inline PrimitiveTypeEntry *TypeDatabase::findPrimitiveType(const QString &name)
{
    TypeEntry *entry = findType(name);
    if (entry != 0 && entry->isPrimitive())
        return static_cast<PrimitiveTypeEntry *>(entry);
    else
        return 0;
}

inline ComplexTypeEntry *TypeDatabase::findComplexType(const QString &name)
{
    TypeEntry *entry = findType(name);
    if (entry != 0 && entry->isComplex())
        return static_cast<ComplexTypeEntry *>(entry);
    else
        return 0;
}

inline ObjectTypeEntry *TypeDatabase::findObjectType(const QString &name)
{
    TypeEntry *entry = findType(name);
    if (entry != 0 && entry->isObject())
        return static_cast<ObjectTypeEntry *>(entry);
    else
        return 0;
}

inline NamespaceTypeEntry *TypeDatabase::findNamespaceType(const QString &name)
{
    TypeEntry *entry = findType(name);
    if (entry != 0 && entry->isNamespace())
        return static_cast<NamespaceTypeEntry *>(entry);
    else
        return 0;
}

QString fixCppTypeName(const QString &name);

#endif // TYPESYSTEM_H
