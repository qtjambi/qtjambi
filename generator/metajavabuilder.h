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

#ifndef METAJAVABUILDER_H
#define METAJAVABUILDER_H

#include "codemodel.h"
#include "metajava.h"
#include "typesystem.h"
#include "typeparser.h"

#include <QtCore/QSet>

class MetaJavaBuilder
{
public:
    enum RejectReason {
        NotInTypeSystem,
        GenerationDisabled,
        RedefinedToNotClass,
        UnmatchedArgumentType,
        UnmatchedReturnType,
        NoReason
    };

    MetaJavaBuilder();

    MetaJavaClassList classes() const { return m_java_classes; }

    FileModelItem model() const { return m_dom; }
    void setModel(FileModelItem item) { m_dom = item; }


    ScopeModelItem popScope() { return m_scopes.takeLast(); }
    void pushScope(ScopeModelItem item) { m_scopes << item; }
    ScopeModelItem currentScope() const { return m_scopes.last(); }

    QString fileName() const { return m_file_name; }
    void setFileName(const QString &fileName) { m_file_name = fileName; }

    void dumpLog();

    bool build();

    void figureOutEnumValuesForClass(MetaJavaClass *java_class, QSet<MetaJavaClass *> *classes);
    int figureOutEnumValue(const QString &name, int value, MetaJavaEnum *java_enum, MetaJavaFunction *java_function = 0);
    void figureOutEnumValues();
    void figureOutDefaultEnumArguments();

    void addMetaJavaClass(MetaJavaClass *cls);
    MetaJavaClass *traverseClass(ClassModelItem item);
    bool setupInheritance(MetaJavaClass *java_class);
    MetaJavaClass *traverseNamespace(NamespaceModelItem item);
    MetaJavaEnum *traverseEnum(EnumModelItem item, MetaJavaClass *enclosing);
    void traverseEnums(ScopeModelItem item, MetaJavaClass *parent);
    void traverseFunctions(ScopeModelItem item, MetaJavaClass *parent);
    void traverseFields(ScopeModelItem item, MetaJavaClass *parent);
    void traverseStreamOperator(FunctionModelItem function_item);
    void traverseCompareOperator(FunctionModelItem item);
    MetaJavaFunction *traverseFunction(FunctionModelItem function);
    MetaJavaField *traverseField(VariableModelItem field, const MetaJavaClass *cls);
    void checkFunctionModifications();
    void registerHashFunction(FunctionModelItem function_item);

    void parseQ_Property(MetaJavaClass *java_class, const QStringList &declarations);
    void setupEquals(MetaJavaClass *java_class);
    void setupComparable(MetaJavaClass *java_class);
    void setupFunctionDefaults(MetaJavaFunction *java_function, MetaJavaClass *java_class);

    QString translateDefaultValue(ArgumentModelItem item, MetaJavaType *type,
                                               MetaJavaFunction *fnc, MetaJavaClass *,
                                               int argument_index);
    MetaJavaType *translateType(const TypeInfo &type, bool *ok);

    void decideUsagePattern(MetaJavaType *type);

    bool inheritTemplate(MetaJavaClass *subclass,
                         const MetaJavaClass *template_class,
                         const TypeParser::Info &info);
    MetaJavaType *inheritTemplateType(const QList<MetaJavaType *> &template_types, MetaJavaType *java_type);

    bool isQObject(const QString &qualified_name);
    bool isEnum(const QStringList &qualified_name);

protected:
    MetaJavaClass *argumentToClass(ArgumentModelItem);

    QString m_file_name;

    MetaJavaClassList m_java_classes;
    MetaJavaClassList m_templates;
    FileModelItem m_dom;

    QList<TypeEntry *> m_template_args;
    QSet<const TypeEntry *> m_used_types;

    QMap<QString, RejectReason> m_rejected_classes;
    QMap<QString, RejectReason> m_rejected_enums;
    QMap<QString, RejectReason> m_rejected_functions;
    QMap<QString, RejectReason> m_rejected_fields;

    QList<MetaJavaEnum *> m_enums;

    QList<QPair<MetaJavaArgument *, MetaJavaFunction *> > m_enum_default_arguments;

    QHash<QString, MetaJavaEnumValue *> m_enum_values;

    MetaJavaClass *m_current_class;
    QList<ScopeModelItem> m_scopes;
    QString m_namespace_prefix;

    QSet<MetaJavaClass *> m_setup_inheritance_done;
};

#endif // METAJAVABUILDER_H
