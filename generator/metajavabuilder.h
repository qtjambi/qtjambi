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

    QString fileName() const { return m_file_name; }
    void setFileName(const QString &fileName) { m_file_name = fileName; }

    void dumpLog();

    bool build();

    MetaJavaClass *traverseClass(ClassModelItem item);
    bool setupInheritance(MetaJavaClass *java_class);
    MetaJavaClass *traverseNamespace(NamespaceModelItem item);
    MetaJavaEnum *traverseEnum(EnumModelItem item);
    void traverseEnums(ScopeModelItem item, MetaJavaClass *parent);
    void traverseFunctions(ScopeModelItem item, MetaJavaClass *parent);
    void traverseFields(ScopeModelItem item, MetaJavaClass *parent);
    MetaJavaFunction *traverseFunction(FunctionModelItem function);
    MetaJavaField *traverseField(VariableModelItem field, const MetaJavaClass *cls);

    QString translateDefaultValue(ArgumentModelItem item, MetaJavaType *type,
                                               MetaJavaFunction *fnc, MetaJavaClass *,
                                               int argument_index);    
    MetaJavaType *translateType(const TypeInfo &type, bool *ok);

    void decideUsagePattern(MetaJavaType *type);

    bool inheritTemplate(MetaJavaClass *subclass,
                         const MetaJavaClass *template_class,
                         const TypeParser::Info &info);
    MetaJavaType *inheritTemplateType(const QList<TypeEntry *> &template_types, MetaJavaType *java_type);

    bool isQObject(const QString &qualified_name);
    bool isEnum(const QStringList &qualified_name);

protected:
    QString m_file_name;

    MetaJavaClassList m_java_classes;
    MetaJavaClassList m_templates;
    FileModelItem m_dom;

    QList<TypeEntry *> m_template_args;
    QSet<TypeEntry *> m_used_types;

    QMap<QString, RejectReason> m_rejected_classes;
    QMap<QString, RejectReason> m_rejected_enums;
    QMap<QString, RejectReason> m_rejected_functions;
    QMap<QString, RejectReason> m_rejected_fields;

    MetaJavaClass *m_current_class;
};

#endif // METAJAVABUILDER_H
