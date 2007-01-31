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

#include "metajavabuilder.h"
#include "reporthandler.h"

#include "ast.h"
#include "binder.h"
#include "control.h"
#include "default_visitor.h"
#include "dumptree.h"
#include "lexer.h"
#include "parser.h"
#include "tokens.h"

#include <QtCore/QDebug>
#include <QtCore/QFile>
#include <QtCore/QFileInfo>
#include <QtCore/QTextCodec>
#include <QtCore/QTextStream>
#include <QtCore/QVariant>

static QString strip_template_args(const QString &name)
{
    int pos = name.indexOf('<');
    return pos < 0 ? name : name.left(pos);
}

static QString strip_preprocessor_lines(const QString &name)
{
    QStringList lst = name.split("\n");
    QString s;
    for (int i=0; i<lst.size(); ++i) {
        if (!lst.at(i).startsWith('#'))
            s += lst.at(i);
    }
    return s.trimmed();
}

static QHash<QString, QString> *operator_names;
QString rename_operator(const QString &oper)
{
    QString op = oper.trimmed();
    if (!operator_names) {
        operator_names = new QHash<QString, QString>;

        operator_names->insert("+", "add");
        operator_names->insert("-", "subtract");
        operator_names->insert("*", "multiply");
        operator_names->insert("/", "divide");
        operator_names->insert("%", "modulo");
        operator_names->insert("&", "and");
        operator_names->insert("|", "or");
        operator_names->insert("^", "xor");
        operator_names->insert("~", "negate");
        operator_names->insert("<<", "shift_left");
        operator_names->insert(">>", "shift_right");

        // assigments
        operator_names->insert("=", "assign");
        operator_names->insert("+=", "add_assign");
        operator_names->insert("-=", "subtract_assign");
        operator_names->insert("*=", "multiply_assign");
        operator_names->insert("/=", "divide_assign");
        operator_names->insert("%=", "modulo_assign");
        operator_names->insert("&=", "and_assign");
        operator_names->insert("|=", "or_assign");
        operator_names->insert("^=", "xor_assign");
        operator_names->insert("<<=", "shift_left_assign");
        operator_names->insert(">>=", "shift_right_assign");

        // Logical
        operator_names->insert("&&", "logical_and");
        operator_names->insert("||", "logical_or");
        operator_names->insert("!", "not");

        // incr/decr
        operator_names->insert("++", "increment");
        operator_names->insert("--", "decrement");

        // compare
        operator_names->insert("<", "less");
        operator_names->insert(">", "greater");
        operator_names->insert("<=", "less_or_equal");
        operator_names->insert(">=", "greater_or_equal");
        operator_names->insert("!=", "not_equal");
        operator_names->insert("==", "equal");

        // other
        operator_names->insert("[]", "subscript");
        operator_names->insert("->", "pointer");
    }

    if (!operator_names->contains(op)) {
        TypeDatabase *tb = TypeDatabase::instance();

        TypeParser::Info typeInfo = TypeParser::parse(op);
        QString cast_to_name = typeInfo.qualified_name.join("::");
        TypeEntry *te = tb->findType(cast_to_name);
        if ((te && te->codeGeneration() == TypeEntry::GenerateNothing)
            || tb->isClassRejected(cast_to_name)) {
            return QString();
        } else if (te) {
            return "operator_cast_" + typeInfo.qualified_name.join("_");
        } else {
            ReportHandler::warning(QString("unknown operator '%1'").arg(op));
            return "operator " + op;
        }
    }

    return "operator_" + operator_names->value(op);
}

MetaJavaBuilder::MetaJavaBuilder()
    : m_current_class(0)
{
}

void MetaJavaBuilder::checkFunctionModifications()
{
    TypeDatabase *types = TypeDatabase::instance();
    TypeEntryHash entryHash = types->entries();
    QList<TypeEntry *> entries = entryHash.values();
    foreach (TypeEntry *entry, entries) {
        if (entry == 0)
            continue;
        if (!entry->isComplex() || entry->codeGeneration() == TypeEntry::GenerateNothing)
            continue;

        ComplexTypeEntry *centry = static_cast<ComplexTypeEntry *>(entry);
        FunctionModificationList modifications = centry->functionModifications();

        foreach (FunctionModification modification, modifications) {
            QString signature = modification.signature;

            QString name = signature.trimmed();
            name = name.mid(0, signature.indexOf("("));

            MetaJavaClass *clazz = m_java_classes.findClass(centry->qualifiedCppName());
            if (clazz == 0)
                continue;

            MetaJavaFunctionList functions = clazz->functions();
            bool found = false;
            QStringList possibleSignatures;
            foreach (MetaJavaFunction *function, functions) {
                if (function->minimalSignature() == signature && function->implementingClass() == clazz) {
                    found = true;
                    break;
                }

                if (function->originalName() == name)
                    possibleSignatures.append(function->minimalSignature() + " in " + function->implementingClass()->name());
            }

            if (!found) {
                QString warning
                    = QString("signature '%1' for function modification in '%2' not found. Possible candidates: %3")
                        .arg(signature)
                        .arg(clazz->qualifiedCppName())
                        .arg(possibleSignatures.join(", "));

                ReportHandler::warning(warning);
            }
        }
    }
}

MetaJavaClass *MetaJavaBuilder::argumentToClass(ArgumentModelItem argument)
{
    MetaJavaClass *returned = 0;
    bool ok = false;
    MetaJavaType *type = translateType(argument->type(), &ok);
    if (ok && type != 0 && type->typeEntry() != 0 && type->typeEntry()->isComplex()) {
        const TypeEntry *entry = type->typeEntry();
        returned = m_java_classes.findClass(entry->name());
    }
    delete type;
    return returned;
}

/**
 * Checks the argument of a hash function and flags the type if it is a complex type
 */
void MetaJavaBuilder::registerHashFunction(FunctionModelItem function_item)
{
    ArgumentList arguments = function_item->arguments();
    if (arguments.size() == 1) {
        if (MetaJavaClass *cls = argumentToClass(arguments.at(0)))
            cls->setHasHashFunction(true);
    }
}

/**
 * Checks the argument of an equals operator and flags the type if it is a complex type
 */
void MetaJavaBuilder::registerEqualsOperator(FunctionModelItem function_item)
{
    ArgumentList arguments = function_item->arguments();
    if (arguments.size() == 2) {
        MetaJavaClass *class_one = argumentToClass(arguments.at(0));
        MetaJavaClass *class_two = argumentToClass(arguments.at(1));
        if (class_one != 0 && class_one == class_two)
            class_one->setHasEqualsOperator(true);
    }
}

bool MetaJavaBuilder::build()
{
    Q_ASSERT(!m_file_name.isEmpty());

    QFile file(m_file_name);

    if (!file.open(QFile::ReadOnly))
        return false;

    QTextStream stream(&file);
    stream.setCodec(QTextCodec::codecForName("UTF-8"));
    QByteArray contents = stream.readAll().toUtf8();
    file.close();

    Control control;
    Parser p(&control);
    pool __pool;

    TranslationUnitAST *ast = p.parse(contents, contents.size(), &__pool);

    CodeModel model;
    Binder binder(&model, p.location());
    m_dom = binder.run(ast);

    QHash<QString, ClassModelItem> typeMap = m_dom->classMap();

    // fix up QObject's in the type system..
    TypeDatabase *types = TypeDatabase::instance();
    foreach (ClassModelItem item, typeMap.values()) {
        QString qualified_name = item->qualifiedName().join("::");
        TypeEntry *entry = types->findType(qualified_name);
        if (entry) {
            if (isQObject(qualified_name) && entry->isComplex()) {
                ((ComplexTypeEntry *) entry)->setQObject(true);
            }
        }
    }

    // Start the generation...
    foreach (ClassModelItem item, typeMap.values()) {
        MetaJavaClass *cls = traverseClass(item);
        if (!cls)
            continue;

	    cls->setOriginalAttributes(cls->attributes());
        if (cls->typeEntry()->isContainer()) {
            m_templates << cls;
        } else {
            m_java_classes << cls;
            if (cls->typeEntry()->designatedInterface()) {
                MetaJavaClass *interface = cls->extractInterface();
                m_java_classes << interface;
                ReportHandler::debugSparse(QString(" -> interface '%1'").arg(interface->name()));
            }
        }
    }

    foreach (MetaJavaClass *cls, m_java_classes) {
        if (!cls->isInterface() && !cls->isNamespace()) {
            setupInheritance(cls);
        }
    }

    QHash<QString, NamespaceModelItem> namespaceMap = m_dom->namespaceMap();
    foreach (NamespaceModelItem item, namespaceMap.values()) {
        MetaJavaClass *java_class = traverseNamespace(item);
        if (java_class)
            m_java_classes << java_class;
    }

    foreach (MetaJavaClass *cls, m_java_classes) {
        cls->fixFunctions();

        if (cls->typeEntry() == 0) {
            ReportHandler::warning(QString("class '%1' does not have an entry in the type system")
                                   .arg(cls->name()));
        } else {
            if (!cls->hasConstructors() && !cls->isFinal() && !cls->isInterface() && !cls->isNamespace())
                cls->addDefaultConstructor();
        }

        if (cls->isAbstract() && !cls->isInterface()) {
            cls->typeEntry()->setLookupName(cls->typeEntry()->javaName() + "$ConcreteWrapper");
        }
    }

    foreach (TypeEntry *entry, m_used_types) {
        if (entry->isPrimitive())
            continue;

        QString name = entry->qualifiedJavaName();

        if (!entry->codeGeneration() == TypeEntry::GenerateNothing
            && (entry->isValue() || entry->isObject())
            && !m_java_classes.findClass(name))
            ReportHandler::warning(QString("type '%1' is specified in typesystem, but not declared")
                                   .arg(name));

        if (entry->isEnum()) {
            QString pkg = entry->javaPackage();
            QString name = (pkg.isEmpty() ? QString() : pkg + ".")
                           + ((EnumTypeEntry *) entry)->javaQualifier();
            MetaJavaClass *cls = m_java_classes.findClass(name);

            if (!cls) {
                ReportHandler::warning(QString("namespace '%1' for enum '%2' is not declared")
                                       .arg(name).arg(entry->javaName()));
            } else {
                MetaJavaEnum *e = cls->findEnum(entry->javaName());
                if (!e)
                    ReportHandler::warning(QString("enum '%1' is specified in typesystem, "
                                                   "but not declared")
                                           .arg(entry->qualifiedCppName()));
            }
        }
    }

    {
        FunctionList hash_functions = m_dom->findFunctions("qHash");
        foreach (FunctionModelItem item, hash_functions) {
            registerHashFunction(item);
        }
    }

    {
        FunctionList equal_functions = m_dom->findFunctions("operator==");
        foreach (FunctionModelItem item, equal_functions) {
            registerEqualsOperator(item);
        }
    }

    figureOutEnumValues();
    figureOutDefaultEnumArguments();
    checkFunctionModifications();

    dumpLog();

    return true;
}


MetaJavaClass *MetaJavaBuilder::traverseNamespace(NamespaceModelItem namespace_item)
{
    NamespaceTypeEntry *type = TypeDatabase::instance()->findNamespaceType(namespace_item->name());

    QString namespace_name = namespace_item->name();

    if (TypeDatabase::instance()->isClassRejected(namespace_name)) {
        m_rejected_classes.insert(namespace_name, GenerationDisabled);
        return 0;
    }

    if (!type) {
        ReportHandler::warning(QString("namespace '%1' does not have a type entry")
                               .arg(namespace_item->name()));
        return 0;
    }

    MetaJavaClass *java_class = new MetaJavaClass;
    java_class->setTypeEntry(type);

    *java_class += MetaJavaAttributes::Public;

    m_current_class = java_class;

    ReportHandler::debugSparse(QString("namespace '%1.%2'")
                               .arg(java_class->package())
                               .arg(namespace_item->name()));

    traverseEnums(model_dynamic_cast<ScopeModelItem>(namespace_item), java_class);
    // traverseFunctions(model_dynamic_cast<ScopeModelItem>(namespace_item), java_class);

    m_current_class = 0;

    return java_class;
}

struct Operator
{
    enum Type { Plus, ShiftLeft, None };

    Operator() : type(None) { }

    int calculate(int x) {
        switch (type) {
        case Plus: return x + value;
        case ShiftLeft: return x << value;
        case None: return x;
        }
        return x;
    }

    Type type;
    int value;
};



Operator findOperator(QString *s) {
    const char *names[] = {
        "+",
        "<<"
    };

    for (int i=0; i<Operator::None; ++i) {
        QString name = QLatin1String(names[i]);
        QString str = *s;
        int splitPoint = str.indexOf(name);
        if (splitPoint > 0) {
            bool ok;
            QString right = str.mid(splitPoint + name.length());
            Operator op;
            op.value = right.toInt(&ok);
            if (ok) {
                op.type = Operator::Type(i);
                *s = str.left(splitPoint).trimmed();
                return op;
            }
        }
    }
    return Operator();
}

int MetaJavaBuilder::figureOutEnumValue(const QString &stringValue,
                                        int oldValuevalue,
                                        MetaJavaEnum *java_enum,
                                        MetaJavaFunction *java_function)
{
    if (stringValue.isEmpty())
        return oldValuevalue;

    QStringList stringValues = stringValue.split("|");

    int returnValue = 0;

    bool matched = false;

    for (int i=0; i<stringValues.size(); ++i) {
        QString s = strip_preprocessor_lines(stringValues.at(i));

        bool ok;
        int v;

        Operator op = findOperator(&s);

        if (s.length() > 0 && s.at(0) == QLatin1Char('0'))
            v = s.toUInt(&ok, 0);
        else
            v = s.toInt(&ok);

        if (ok) {
            matched = true;

        } else if (m_enum_values.contains(s)) {
            v = m_enum_values[s]->value();
            matched = true;

        } else {
            MetaJavaEnumValue *ev = 0;

            if (java_enum && (ev = java_enum->values().find(s))) {
                v = ev->value();
                matched = true;

            } else if (java_enum && (ev = java_enum->enclosingClass()->findEnumValue(s, java_enum))) {
                v = ev->value();
                matched = true;

            } else {            
                ReportHandler::warning("unhandled enum value: " + s + " in "
                                       + java_enum->enclosingClass()->name() + "::"
                                       + java_enum->name());
            }
        }

        if (matched)
            returnValue |= op.calculate(v);
    }

    if (!matched) {
        QString warn = QString("unmatched enum %1").arg(stringValue);

        if (java_function != 0) {
            warn += QString(" when parsing default value of '%1' in class '%2'")
                .arg(java_function->name())
                .arg(java_function->implementingClass()->name());
        }

        ReportHandler::warning(warn);
        returnValue = oldValuevalue;
    }

    return returnValue;
}

void MetaJavaBuilder::figureOutEnumValuesForClass(MetaJavaClass *java_class,
                                                  QSet<MetaJavaClass *> *classes)
{
    MetaJavaClass *base = java_class->baseClass();

    if (base != 0 && !classes->contains(base))
        figureOutEnumValuesForClass(base, classes);

    if (classes->contains(java_class))
        return;

    MetaJavaEnumList enums = java_class->enums();
    foreach (MetaJavaEnum *e, enums) {
        if (!e)
            ReportHandler::warning("bad enum in class " + java_class->name());
        MetaJavaEnumValueList lst = e->values();
        int value = 0;
        for (int i=0; i<lst.size(); ++i) {
            value = figureOutEnumValue(lst.at(i)->stringValue(), value, e);
            lst.at(i)->setValue(value);
            value++;
        }

        // Check for duplicate values...
        EnumTypeEntry *ete = e->typeEntry();
        if (!ete->forceInteger()) {
            QHash<int, MetaJavaEnumValue *> entries;
            foreach (MetaJavaEnumValue *v, lst) {

                bool vRejected = ete->isEnumValueRejected(v->name());

                MetaJavaEnumValue *current = entries.value(v->value());
                if (current) {
                    bool currentRejected = ete->isEnumValueRejected(current->name());
                    if (!currentRejected && !vRejected) {
                        ReportHandler::warning(
                            QString("duplicate enum values: %1::%2, %3 and %4 are %5")
                            .arg(java_class->name())
                            .arg(e->name())
                            .arg(v->name())
                            .arg(entries[v->value()]->name())
                            .arg(v->value()));
                        continue;
                    }
                }

                if (!vRejected)
                    entries[v->value()] = v;
            }

            // Entries now contain all the original entries, no
            // rejected ones... Use this to generate the enumValueRedirection table.
            foreach (MetaJavaEnumValue *reject, lst) {
                if (!ete->isEnumValueRejected(reject->name()))
                    continue;

                MetaJavaEnumValue *used = entries.value(reject->value());
                if (!used) {
                    ReportHandler::warning(
                        QString::fromLatin1("Rejected enum has no alternative...: %1::%2\n")
                        .arg(java_class->name())
                        .arg(reject->name()));
                    continue;
                }
                ete->addEnumValueRedirection(reject->name(), used->name());
            }

        }
    }



    *classes += java_class;
}


void MetaJavaBuilder::figureOutEnumValues()
{
    // Keep a set of classes that we already traversed. We use this to
    // enforce that we traverse base classes prior to subclasses.
    QSet<MetaJavaClass *> classes;
    foreach (MetaJavaClass *c, m_java_classes) {
        figureOutEnumValuesForClass(c, &classes);
    }
}

void MetaJavaBuilder::figureOutDefaultEnumArguments()
{
    foreach (MetaJavaClass *java_class, m_java_classes) {
        foreach (MetaJavaFunction *java_function, java_class->functions()) {
            foreach (MetaJavaArgument *arg, java_function->arguments()) {

                QString expr = arg->defaultValueExpression();
                if (expr.isEmpty())
                    continue;

                if (!java_function->replacedDefaultExpression(java_function->implementingClass(),
                    arg->argumentIndex()+1).isEmpty()) {
                    continue;
                }

                QString new_expr = expr;
                if (arg->type()->isEnum()) {
                    QStringList lst = expr.split(QLatin1String("::"));
                    if (lst.size() == 1) {
                        MetaJavaEnum *e = java_class->findEnumForValue(expr);
                        new_expr = QString("%1.%2")
                                   .arg(e->typeEntry()->qualifiedJavaName())
                                   .arg(expr);
                    } else if (lst.size() == 2) {
                        MetaJavaClass *cl = m_java_classes.findClass(lst.at(0));
                        if (!cl) {
                            ReportHandler::warning("missing required class for enums: " + lst.at(0));
                            continue;
                        }
                        new_expr = QString("%1.%2.%3")
                                   .arg(cl->typeEntry()->qualifiedJavaName())
                                   .arg(arg->type()->name())
                                   .arg(lst.at(1));
                    } else {
                        ReportHandler::warning("bad default value passed to enum " + expr);
                    }

                } else if(arg->type()->isFlags()) {
                    const FlagsTypeEntry *flagsEntry =
                        static_cast<const FlagsTypeEntry *>(arg->type()->typeEntry());
                    EnumTypeEntry *enumEntry = flagsEntry->originator();
                    MetaJavaEnum *java_enum = m_java_classes.findEnum(enumEntry);
                    if (!java_enum) {
                        ReportHandler::warning("unknown required enum " + enumEntry->qualifiedCppName());
                        continue;
                    }

                    int value = figureOutEnumValue(expr, 0, java_enum, java_function);
                    new_expr = QString::number(value);

                } else if (arg->type()->isPrimitive()) {
                    MetaJavaEnumValue *value = 0;
                    if (expr.contains("::"))
                        value = m_java_classes.findEnumValue(expr);
                    if (!value)
                        value = java_class->findEnumValue(expr, 0);

                    if (value) {
                        new_expr = QString::number(value->value());
                    } else if (expr.contains(QLatin1Char('+'))) {
                        new_expr = QString::number(figureOutEnumValue(expr, 0, 0));

                    }



                }

                arg->setDefaultValueExpression(new_expr);
            }
        }
    }
}


MetaJavaEnum *MetaJavaBuilder::traverseEnum(EnumModelItem enum_item, MetaJavaClass *enclosing)
{
    // Skipping private enums.
    if (enum_item->accessPolicy() == CodeModel::Private) {
        return 0;
    }

    QString qualified_name = enum_item->qualifiedName().join("::");
    TypeEntry *type_entry = TypeDatabase::instance()->findType(qualified_name);

    Q_ASSERT(m_current_class != 0);
    QString enum_name = enum_item->name();
    QString class_name = m_current_class->typeEntry()->qualifiedCppName();
    if (m_current_class && TypeDatabase::instance()->isEnumRejected(class_name, enum_name)) {
        m_rejected_enums.insert(qualified_name, GenerationDisabled);
        return 0;
    }

    if (!type_entry || !type_entry->isEnum()) {
        ReportHandler::warning(QString("enum '%1::%2' does not have a type entry or is not an enum")
                               .arg(m_current_class->name())
                               .arg(enum_item->name()));
        m_rejected_enums.insert(qualified_name, NotInTypeSystem);
        return 0;
    }

    MetaJavaEnum *java_enum = new MetaJavaEnum;

    java_enum->setTypeEntry((EnumTypeEntry *) type_entry);
    switch (enum_item->accessPolicy()) {
    case CodeModel::Public: *java_enum += MetaJavaAttributes::Public; break;
    case CodeModel::Protected: *java_enum += MetaJavaAttributes::Protected; break;
//     case CodeModel::Private: *java_enum += MetaJavaAttributes::Private; break;
    default: break;
    }

    ReportHandler::debugMedium(QString(" - traversing enum %1").arg(java_enum->fullName()));

    foreach (EnumeratorModelItem value, enum_item->enumerators()) {

        MetaJavaEnumValue *java_enum_value = new MetaJavaEnumValue;
        java_enum_value->setName(value->name());
        // Deciding the enum value...

        java_enum_value->setStringValue(strip_preprocessor_lines(value->value()));
        java_enum->addEnumValue(java_enum_value);

        ReportHandler::debugFull("   - " + java_enum_value->name() + " = "
                                 + java_enum_value->value());

        // Add into global register...
        QString key = enclosing->name() + "::" + java_enum_value->name();
        m_enum_values[key] = java_enum_value;
    }

    m_enums << java_enum;

    return java_enum;
}

MetaJavaClass *MetaJavaBuilder::traverseClass(ClassModelItem class_item)
{
    QString class_name = strip_template_args(class_item->name());

    QString full_class_name = class_name;
    // we have inner an class
    if (m_current_class) {
        full_class_name = strip_template_args(m_current_class->typeEntry()->qualifiedCppName())
                          + "::" + full_class_name;
    }

    ComplexTypeEntry *type = TypeDatabase::instance()->findComplexType(full_class_name);
    RejectReason reason = NoReason;

    if (TypeDatabase::instance()->isClassRejected(full_class_name)) {
        reason = GenerationDisabled;
    } else if (!type) {
        TypeEntry *te = TypeDatabase::instance()->findType(full_class_name);
        if (te && !te->isComplex())
            reason = RedefinedToNotClass;
        else
            reason = NotInTypeSystem;
    } else if (type->codeGeneration() == TypeEntry::GenerateNothing) {
        reason = GenerationDisabled;
    }

    if (reason != NoReason) {
        m_rejected_classes.insert(full_class_name, reason);
        return false;
    }

    if (type->isObject()) {
        ((ObjectTypeEntry *)type)->setQObject(isQObject(full_class_name));
    }

    MetaJavaClass *java_class = new MetaJavaClass;
    java_class->setTypeEntry(type);
    java_class->setBaseClassNames(class_item->baseClasses());
    *java_class += MetaJavaAttributes::Public;

    MetaJavaClass *old_current_class = m_current_class;
    m_current_class = java_class;

    if (type->isContainer()) {
        ReportHandler::debugSparse(QString("container: '%1'").arg(full_class_name));
    } else {
        ReportHandler::debugSparse(QString("class: '%1'").arg(java_class->fullName()));
    }


    TemplateParameterList template_parameters = class_item->templateParameters();
    m_template_args.clear();
    for (int i=0; i<template_parameters.size(); ++i) {
        const TemplateParameterModelItem &param = template_parameters.at(i);
        TemplateArgumentEntry *param_type = new TemplateArgumentEntry(param->name());
        param_type->setOrdinal(i);
        m_template_args.append(param_type);
    }

    traverseFunctions(model_dynamic_cast<ScopeModelItem>(class_item), java_class);
    traverseEnums(model_dynamic_cast<ScopeModelItem>(class_item), java_class);
    traverseFields(model_dynamic_cast<ScopeModelItem>(class_item), java_class);

    // Inner classes
    {
        QList<ClassModelItem> inner_classes = class_item->classMap().values();
        foreach (const ClassModelItem &ci, inner_classes) {
            MetaJavaClass *cl = traverseClass(ci);
            if (cl) {
                cl->setEnclosingClass(java_class);
                m_java_classes << cl;
            }
        }

    }

    m_template_args.clear();

    m_current_class = old_current_class;

    // Set the default include file name
    if (!type->include().isValid()) {
        QFileInfo info(class_item->fileName());
        type->setInclude(Include(Include::IncludePath, info.fileName()));
    }

    return java_class;
}

MetaJavaField *MetaJavaBuilder::traverseField(VariableModelItem field, const MetaJavaClass *cls)
{
    QString field_name = field->name();
    QString class_name = m_current_class->typeEntry()->qualifiedCppName();

    // Ignore friend decl.
    if (field->isFriend())
        return 0;

    if (field->accessPolicy() == CodeModel::Private)
        return 0;

    if (TypeDatabase::instance()->isFieldRejected(class_name, field_name)) {
        m_rejected_fields.insert(class_name + "::" + field_name, GenerationDisabled);
        return 0;
    }


    MetaJavaField *java_field = new MetaJavaField;
    java_field->setName(field_name);
    java_field->setEnclosingClass(cls);

    bool ok;
    TypeInfo field_type = field->type();
    MetaJavaType *java_type = translateType(field_type, &ok);

    if (!java_type || !ok) {
        ReportHandler::warning(QString("skipping field '%1::%2' with unmatched type '%3'")
                               .arg(m_current_class->name())
                               .arg(field_name)
                               .arg(TypeInfo::resolveType(field_type, model()->toItem()).qualifiedName().join("::")));
        delete java_field;
        return 0;
    }

    java_field->setType(java_type);

    uint attr = 0;
    if (field->isStatic())
        attr |= MetaJavaAttributes::Static;

    CodeModel::AccessPolicy policy = field->accessPolicy();
    if (policy == CodeModel::Public)
        attr |= MetaJavaAttributes::Public;
    else if (policy == CodeModel::Protected)
        attr |= MetaJavaAttributes::Protected;
    else
        attr |= MetaJavaAttributes::Private;
    java_field->setAttributes(attr);

    return java_field;
}

void MetaJavaBuilder::traverseFields(ScopeModelItem scope_item, MetaJavaClass *java_class)
{
    foreach (VariableModelItem field, scope_item->variables()) {
        MetaJavaField *java_field = traverseField(field, java_class);

        if (java_field) {
            java_field->setOriginalAttributes(java_field->attributes());
            java_class->addField(java_field);
        }
    }
}

void MetaJavaBuilder::traverseFunctions(ScopeModelItem scope_item, MetaJavaClass *java_class)
{
    foreach (FunctionModelItem function, scope_item->functions()) {
        MetaJavaFunction *java_function = traverseFunction(function);

        if (java_function) {

            // Set the default value of the declaring class. This may be changed
            // in fixFunctions later on
            java_function->setDeclaringClass(java_class);

            // Some of the queries below depend on the implementing class being set
            // to function properly. Such as function modifications
            java_function->setImplementingClass(java_class);

            java_function->setOriginalAttributes(java_function->attributes());

            if ((java_function->isConstructor() || java_function->isDestructor())
                && (java_function->isPrivate() || java_function->isInvalid())
                && !java_class->hasNonPrivateConstructor()) {
                *java_class += MetaJavaAttributes::Final;
            } else if (java_function->isConstructor() && !java_function->isPrivate()) {
                *java_class -= MetaJavaAttributes::Final;
                java_class->setHasNonPrivateConstructor(true);
            }

            // Classes with virtual destructors should always have a shell class
            // (since we aren't registering the destructors, we need this extra check)
            if (java_function->isDestructor() && !java_function->isFinal())
                java_class->setForceShellClass(true);

            if (java_function->isSignal() && !java_class->isQObject()) {
                QString warn = QString("signal '%1' in non-QObject class '%2'")
                    .arg(java_function->name()).arg(java_class->name());
                ReportHandler::warning(warn);
            }

            if (java_function->isSignal() && java_class->hasSignal(java_function)) {
                QString warn = QString("signal '%1' in class '%2' is overloaded.")
                    .arg(java_function->name()).arg(java_class->name());
                ReportHandler::warning(warn);
            }

            if (!java_function->isDestructor()
                && !java_function->isInvalid()
                && (!java_function->isConstructor() || !java_function->isPrivate())) {

                if (java_class->typeEntry()->designatedInterface() && !java_function->isPublic()
                    && !java_function->isPrivate()) {
                    QString warn = QString("non-public function '%1' in interface '%2'")
                        .arg(java_function->name()).arg(java_class->name());
                    ReportHandler::warning(warn);

                    java_function->setVisibility(MetaJavaClass::Public);
                }

                if (!java_function->isFinalInJava()
                    && java_function->isRemovedFrom(java_class, TypeSystem::JavaCode)) {
                    *java_function += MetaJavaAttributes::FinalInCpp;
                }

                if (java_function->name() == "operator_equal")
                    java_class->setHasEqualsOperator(true);

                java_class->addFunction(java_function);
            } else if (java_function->isDestructor() && !java_function->isPublic()) {
                java_class->setHasPublicDestructor(false);
            }
        }
    }
}

bool MetaJavaBuilder::setupInheritance(MetaJavaClass *java_class)
{
    Q_ASSERT(!java_class->isInterface());

    if (m_setup_inheritance_done.contains(java_class))
        return true;
    m_setup_inheritance_done.insert(java_class);

    QStringList base_classes = java_class->baseClassNames();

    TypeDatabase *types = TypeDatabase::instance();

    // we only support our own containers and ONLY if there is only one baseclass
    if (base_classes.size() == 1 && base_classes.first().count('<') == 1) {
        QString complete_name = base_classes.first();
        TypeParser::Info info = TypeParser::parse(complete_name);
        QString base_name = info.qualified_name.join("::");
        ContainerTypeEntry *cte = types->findContainerType(base_name);

        if (cte) {
            MetaJavaClass *templ = 0;
            foreach (MetaJavaClass *c, m_templates) {
                if (c->typeEntry()->name() == base_name) {
                    templ = c;
                    break;
                }
            }
            if (templ) {
                inheritTemplate(java_class, templ, info);
                return true;
            }

            ReportHandler::warning(QString("template baseclass '%1' of '%2' is not known")
                                   .arg(base_name)
                                   .arg(java_class->name()));
            return false;
        }
    }

    int primary = -1;
    int primaries = 0;
    for (int i=0; i<base_classes.size(); ++i) {

        if (types->isClassRejected(base_classes.at(i)))
            continue;

        TypeEntry *base_class_entry = types->findType(base_classes.at(i));
        if (!base_class_entry) {
            ReportHandler::warning(QString("class '%1' inherits from unknown base class '%2'")
                                   .arg(java_class->name()).arg(base_classes.at(i)));
        }

        // true for primary base class
        else if (!base_class_entry->designatedInterface()) {
            if (primaries > 0) {
                ReportHandler::warning(QString("class '%1' has multiple primary base classes"
                                               " '%2' and '%3'")
                                       .arg(java_class->name())
                                       .arg(base_classes.at(primary))
                                       .arg(base_class_entry->name()));
                return false;
            }
            primaries++;
            primary = i;
        }
    }

    if (primary >= 0) {
        MetaJavaClass *base_class = m_java_classes.findClass(base_classes.at(primary));
        if (!base_class) {
            ReportHandler::warning(QString("unknown baseclass for '%1': '%2'")
                                   .arg(java_class->name())
                                   .arg(base_classes.at(primary)));
            return false;
        }
        java_class->setBaseClass(base_class);
    }

    for (int i=0; i<base_classes.size(); ++i) {
        if (types->isClassRejected(base_classes.at(i)))
            continue;

        if (i != primary) {
            MetaJavaClass *base_class = m_java_classes.findClass(base_classes.at(i));
            if (base_class == 0) {
                ReportHandler::warning(QString("class not found for setup inheritance '%1'").arg(base_class->name()));
                return false;
            }

            setupInheritance(base_class);

            QString interface_name = InterfaceTypeEntry::interfaceName(base_classes.at(i));
            MetaJavaClass *iface = m_java_classes.findClass(interface_name);
            if (!iface) {
                ReportHandler::warning(QString("unknown interface for '%1': '%2'")
                                       .arg(java_class->name())
                                       .arg(interface_name));
                return false;
            }
            java_class->addInterface(iface);

            MetaJavaClassList interfaces = iface->interfaces();
            foreach (MetaJavaClass *iface, interfaces)
                java_class->addInterface(iface);
        }
    }

    return true;
}

void MetaJavaBuilder::traverseEnums(ScopeModelItem scope_item, MetaJavaClass *java_class)
{
    EnumList enums = scope_item->enums();
    foreach (EnumModelItem enum_item, enums) {
        MetaJavaEnum *java_enum = traverseEnum(enum_item, java_class);
        if (java_enum) {
            java_enum->setOriginalAttributes(java_enum->attributes());
            java_class->addEnum(java_enum);
            java_enum->setEnclosingClass(java_class);
        }
    }
}

MetaJavaFunction *MetaJavaBuilder::traverseFunction(FunctionModelItem function_item)
{
    QString function_name = function_item->name();
    QString class_name = m_current_class->typeEntry()->qualifiedCppName();

    if (TypeDatabase::instance()->isFunctionRejected(class_name, function_name)) {
        m_rejected_functions.insert(class_name + "::" + function_name, GenerationDisabled);
        return 0;
    }


    Q_ASSERT(function_item->functionType() == CodeModel::Normal
             || function_item->functionType() == CodeModel::Signal
             || function_item->functionType() == CodeModel::Slot);

    if (function_item->isFriend())
        return 0;


    QString cast_type;

    if (function_name.startsWith("operator")) {
        function_name = rename_operator(function_name.mid(8));
        if (function_name.isEmpty()) {
            m_rejected_functions.insert(class_name + "::" + function_name,
                                        GenerationDisabled);
            return 0;
        }
        if (function_name.contains("_cast_"))
            cast_type = function_name.mid(14).trimmed();
    }

    MetaJavaFunction *java_function = new MetaJavaFunction;
    java_function->setConstant(function_item->isConstant());

    ReportHandler::debugMedium(QString(" - %2()").arg(function_name));

    java_function->setName(function_name);
    java_function->setOriginalName(function_item->name());

    if (function_item->isAbstract())
        *java_function += MetaJavaAttributes::Abstract;

    if (!java_function->isAbstract())
        *java_function += MetaJavaAttributes::Native;

    if (!function_item->isVirtual())
        *java_function += MetaJavaAttributes::Final;

    if (function_item->isStatic()) {
        *java_function += MetaJavaAttributes::Static;
        *java_function += MetaJavaAttributes::Final;
    }

    // Access rights
    if (function_item->accessPolicy() == CodeModel::Public)
        *java_function += MetaJavaAttributes::Public;
    else if (function_item->accessPolicy() == CodeModel::Private)
        *java_function += MetaJavaAttributes::Private;
    else
        *java_function += MetaJavaAttributes::Protected;


    QString stripped_class_name = class_name;
    int cc_pos = stripped_class_name.lastIndexOf("::");
    if (cc_pos > 0)
        stripped_class_name = stripped_class_name.mid(cc_pos + 2);

    TypeInfo function_type = function_item->type();
    if (function_name.startsWith('~')) {
        java_function->setFunctionType(MetaJavaFunction::DestructorFunction);
        java_function->setInvalid(true);
    } else if (strip_template_args(function_name) == stripped_class_name) {
        java_function->setFunctionType(MetaJavaFunction::ConstructorFunction);
        java_function->setName(m_current_class->name());
    } else {
        bool ok;
        MetaJavaType *type = 0;

        if (!cast_type.isEmpty()) {
            TypeInfo info;
            info.setQualifiedName(QStringList(cast_type));
            type = translateType(info, &ok);
        } else {
            type = translateType(function_type, &ok);
        }

        if (!ok) {
            ReportHandler::warning(QString("skipping function '%1::%2', unmatched return type '%3'")
                                   .arg(class_name)
                                   .arg(function_item->name())
                                   .arg(function_item->type().toString()));
            m_rejected_functions[class_name + "::" + function_name] =
                UnmatchedReturnType;
            java_function->setInvalid(true);
            return java_function;
        }
        java_function->setType(type);

        if (function_item->functionType() == CodeModel::Signal)
            java_function->setFunctionType(MetaJavaFunction::SignalFunction);
        else if (function_item->functionType() == CodeModel::Slot)
            java_function->setFunctionType(MetaJavaFunction::SlotFunction);
    }

    ArgumentList arguments = function_item->arguments();
    MetaJavaArgumentList java_arguments;

    int first_default_argument = 0;
    for (int i=0; i<arguments.size(); ++i) {
        ArgumentModelItem arg = arguments.at(i);

        bool ok;
        MetaJavaType *java_type = translateType(arg->type(), &ok);
        if (!java_type || !ok) {
            ReportHandler::warning(QString("skipping function '%1::%2', "
                                           "unmatched parameter type '%3'")
                                   .arg(class_name)
                                   .arg(function_item->name())
                                   .arg(arg->type().toString()));
            m_rejected_functions[class_name + "::" + function_name] =
                UnmatchedArgumentType;
            java_function->setInvalid(true);
            return java_function;
        }
        MetaJavaArgument *java_argument = new MetaJavaArgument;
        java_argument->setType(java_type);
        java_argument->setName(arg->name());
        java_argument->setArgumentIndex(i);
        java_arguments << java_argument;
    }

    java_function->setArguments(java_arguments);

    // Find the correct default values
    for (int i=0; i<arguments.size(); ++i) {
        ArgumentModelItem arg = arguments.at(i);
        MetaJavaArgument *java_arg = java_arguments.at(i);
        if (arg->defaultValue()) {
            QString expr = arg->defaultValueExpression();
            if (!expr.isEmpty())
                java_arg->setOriginalDefaultValueExpression(expr);

            expr = translateDefaultValue(arg, java_arg->type(), java_function, m_current_class, i);
            if (expr.isEmpty()) {
                first_default_argument = i;
            } else {
                java_arg->setDefaultValueExpression(expr);
            }

            if (java_arg->type()->isEnum() || java_arg->type()->isFlags()) {
                m_enum_default_arguments
                    << QPair<MetaJavaArgument *, MetaJavaFunction *>(java_arg, java_function);
            }

        }
    }

    // If we where not able to translate the default argument make it
    // reset all default arguments before this one too.
    for (int i=0; i<first_default_argument; ++i)
        java_arguments[i]->setDefaultValueExpression(QString());

    if (ReportHandler::debugLevel() == ReportHandler::FullDebug)
        foreach(MetaJavaArgument *arg, java_arguments)
            ReportHandler::debugFull("   - " + arg->toString());

    return java_function;
}


MetaJavaType *MetaJavaBuilder::translateType(const TypeInfo &_typei, bool *ok)
{
    Q_ASSERT(ok);
    *ok = true;

    TypeInfo typei = TypeInfo::resolveType(_typei, model()->toItem());
    if (typei.isFunctionPointer()) {
        *ok = false;
        return 0;
    }
    TypeParser::Info typeInfo = TypeParser::parse(typei.toString());
    if (typeInfo.is_busted) {
        *ok = false;
        return 0;
    }

    bool array_of_unspecified_size = false;
    if (typeInfo.arrays.size() > 0) {
        array_of_unspecified_size = true;
        for (int i=0; i<typeInfo.arrays.size(); ++i)
            array_of_unspecified_size = array_of_unspecified_size && typeInfo.arrays.at(i).isEmpty();

        if (!array_of_unspecified_size) {
            TypeInfo newInfo;
            //newInfo.setArguments(typei.arguments());
            newInfo.setIndirections(typei.indirections());
            newInfo.setConstant(typei.isConstant());
            newInfo.setFunctionPointer(typei.isFunctionPointer());
            newInfo.setQualifiedName(typei.qualifiedName());
            newInfo.setReference(typei.isReference());
            newInfo.setVolatile(typei.isVolatile());

            MetaJavaType *elementType = translateType(newInfo, ok);
            if (!ok)
                return 0;

            for (int i=typeInfo.arrays.size()-1; i>=0; --i) {
                QString s = typeInfo.arrays.at(i);
                bool ok;


                int elems = s.toInt(&ok);
                if (!ok)
                    return 0;

                MetaJavaType *arrayType = new MetaJavaType;
                arrayType->setArrayElementCount(elems);
                arrayType->setArrayElementType(elementType);
                arrayType->setTypeEntry(new ArrayTypeEntry(elementType->typeEntry()));
                decideUsagePattern(arrayType);

                elementType = arrayType;
            }

            return elementType;
        }  else {
            typeInfo.indirections += typeInfo.arrays.size();
        }
    }

    QStringList qualifier_list = typeInfo.qualified_name;

    if (qualifier_list.isEmpty()) {
        ReportHandler::warning(QString("horribly broken type '%1'").arg(_typei.toString()));
        *ok = false;
        return 0;
    }

    QString qualified_name = qualifier_list.join("::");
    QString name = qualifier_list.takeLast();

    if (name == "void" && typeInfo.indirections == 0) {
        return 0;
    }

    if (qualified_name == "QFlags")
        qualified_name = typeInfo.toString();

    TypeEntry *type = TypeDatabase::instance()->findType(qualified_name);

    if (!type) {
        type = TypeDatabase::instance()->findContainerType(name);

        if (!type) {
            foreach (TypeEntry *te, m_template_args) {
                if (te->name() == qualified_name)
                    type = te;
            }

            if (!type) {
                *ok = false;
                return 0;
            }
        }
    }

    // Used to for diagnostics later...
    m_used_types << type;

    // These are only implicit and should not appear in code...
    Q_ASSERT(!type->isInterface());

    MetaJavaType *java_type = new MetaJavaType;
    java_type->setTypeEntry(type);
    java_type->setIndirections(typeInfo.indirections);
    java_type->setReference(typeInfo.is_reference);
    java_type->setConstant(typeInfo.is_constant);
    java_type->setOriginalTypeDescription(_typei.toString());
    decideUsagePattern(java_type);

    if (java_type->isContainer()) {
        ContainerTypeEntry::Type container_type =
            static_cast<const ContainerTypeEntry *>(type)->type();

        if (container_type == ContainerTypeEntry::StringListContainer) {
            TypeInfo info;
            info.setQualifiedName(QStringList() << "QString");
            MetaJavaType *targ_type = translateType(info, ok);

            Q_ASSERT(*ok);
            Q_ASSERT(targ_type);

            java_type->addInstantiation(targ_type);
            java_type->setInstantiationInCpp(false);

        } else {
            foreach (const TypeParser::Info &ta, typeInfo.template_instantiations) {
                TypeInfo info;
                info.setConstant(ta.is_constant);
                info.setReference(ta.is_reference);
                info.setIndirections(ta.indirections);
                info.setFunctionPointer(false);
                info.setQualifiedName(ta.instantiationName().split("::"));

                MetaJavaType *targ_type = translateType(info, ok);
                if (!(*ok)) {
                    delete java_type;
                    return 0;
                }
                java_type->addInstantiation(targ_type);
            }
        }

        if (container_type == ContainerTypeEntry::ListContainer
            || container_type == ContainerTypeEntry::VectorContainer
            || container_type == ContainerTypeEntry::StringListContainer) {
            Q_ASSERT(java_type->instantiations().size() == 1);
        }
    }

    return java_type;
}

void MetaJavaBuilder::decideUsagePattern(MetaJavaType *java_type)
{
    const TypeEntry *type = java_type->typeEntry();

    if (type->isPrimitive() && java_type->actualIndirections() == 0) {
        java_type->setTypeUsagePattern(MetaJavaType::PrimitivePattern);

    } else if (type->isVoid()) {
        java_type->setTypeUsagePattern(MetaJavaType::NativePointerPattern);

    } else if (type->isString()
               && java_type->indirections() == 0
               && java_type->isConstant() == java_type->isReference()) {
        java_type->setTypeUsagePattern(MetaJavaType::StringPattern);

    } else if (type->isChar()
        && java_type->indirections() == 0
        && java_type->isConstant() == java_type->isReference()) {
        java_type->setTypeUsagePattern(MetaJavaType::CharPattern);

    } else if (type->isVariant()
        && java_type->indirections() == 0
        && java_type->isConstant() == java_type->isReference()) {
        java_type->setTypeUsagePattern(MetaJavaType::VariantPattern);

    } else if (type->isEnum() && java_type->actualIndirections() == 0) {
        java_type->setTypeUsagePattern(MetaJavaType::EnumPattern);

    } else if (type->isObject()
                && java_type->indirections() == 0
                && java_type->isReference()) {
        if (((ComplexTypeEntry *) type)->isQObject())
            java_type->setTypeUsagePattern(MetaJavaType::QObjectPattern);
        else
            java_type->setTypeUsagePattern(MetaJavaType::ObjectPattern);

    } else if (type->isObject()
               && java_type->indirections() == 1) {
        if (((ComplexTypeEntry *) type)->isQObject())
            java_type->setTypeUsagePattern(MetaJavaType::QObjectPattern);
        else
            java_type->setTypeUsagePattern(MetaJavaType::ObjectPattern);

        // const-references to pointers can be passed as pointers
        if (java_type->isReference() && java_type->isConstant()) {
            java_type->setReference(false);
            java_type->setConstant(false);
        }

    } else if (type->isContainer()) {
        java_type->setTypeUsagePattern(MetaJavaType::ContainerPattern);

    } else if (type->isTemplateArgument()) {

    } else if (type->isFlags()
               && java_type->indirections() == 0
               && (java_type->isConstant() == java_type->isReference())) {
        java_type->setTypeUsagePattern(MetaJavaType::FlagsPattern);

    } else if (type->isArray()) {
        java_type->setTypeUsagePattern(MetaJavaType::ArrayPattern);

    } else if (type->isThread()) {
        Q_ASSERT(java_type->indirections() == 1);
        java_type->setTypeUsagePattern(MetaJavaType::ThreadPattern);

    } else if (type->isValue()
               && java_type->indirections() == 0
               && (java_type->isConstant() == java_type->isReference()
                   || !java_type->isReference())) {
        java_type->setTypeUsagePattern(MetaJavaType::ValuePattern);

    } else {
        java_type->setTypeUsagePattern(MetaJavaType::NativePointerPattern);
        ReportHandler::debugFull(QString("native pointer pattern for '%1'")
                                 .arg(java_type->cppSignature()));
    }
}

QString MetaJavaBuilder::translateDefaultValue(ArgumentModelItem item, MetaJavaType *type,
                                               MetaJavaFunction *fnc, MetaJavaClass *implementing_class,
                                               int argument_index)
{
    QString function_name = fnc->name();
    QString class_name = implementing_class->name();

    QString replaced_expression = fnc->replacedDefaultExpression(implementing_class, argument_index + 1);
    if (fnc->removedDefaultExpression(implementing_class, argument_index +1))
        return "";
    if (!replaced_expression.isEmpty())
        return replaced_expression;

    QString expr = item->defaultValueExpression();
    if (type->isPrimitive()) {
        if (type->name() == "boolean") {
            if (expr == "false" || expr=="true") {
                return expr;
            } else {
                bool ok = false;
                int number = expr.toInt(&ok);
                if (ok && number)
                    return "true";
                else
                    return "false";
            }
        } else if (expr == "ULONG_MAX") {
            return "Long.MAX_VALUE";
        } else if (expr == "QVariant::Invalid") {
            return QString::number(QVariant::Invalid);
        } else {
            // This can be an enum or flag so I need to delay the
            // translation untill all namespaces are completly
            // processed. This is done in figureOutEnumValues()
            return expr;
        }
    } else if (type != 0 && (type->isFlags() || type->isEnum())) {
        // Same as with enum explanation above...
        return expr;

    } else {

        // constructor or functioncall can be a bit tricky...
        if (expr == "QVariant()" || expr == "QModelIndex()") {
            return "null";
        } else if (expr == "QString()") {
            return "\"\"";
        } else if (expr.endsWith(")") && expr.contains("::")) {
            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(expr.indexOf("::")));
            if (typeEntry)
                return typeEntry->qualifiedJavaName() + "." + expr.right(expr.length() - expr.indexOf("::") - 2);
        } else if (expr.endsWith(")") && type->isValue()) {
            int pos = expr.indexOf("(");

            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(pos));
            if (typeEntry)
                return "new " + typeEntry->qualifiedJavaName() + expr.right(expr.length() - pos);
            else
                return expr;
        } else if (expr == "0") {
            return "null";
        } else if (type->isObject() || type->isValue() || expr.contains("::")) { // like Qt::black passed to a QColor
            TypeEntry *typeEntry = TypeDatabase::instance()->findType(expr.left(expr.indexOf("::")));

            expr = expr.right(expr.length() - expr.indexOf("::") - 2);
            if (typeEntry) {
                return "new " + type->typeEntry()->qualifiedJavaName() +
                       "(" + typeEntry->qualifiedJavaName() + "." + expr + ")";
            }
        }
    }

    QString warn = QString("unsupported default value '%3' of argument in function '%1', class '%2'")
        .arg(function_name).arg(class_name).arg(item->defaultValueExpression());
    ReportHandler::warning(warn);

    return QString();
}


bool MetaJavaBuilder::isQObject(const QString &qualified_name)
{
    if (qualified_name == "QObject")
        return true;

    ClassModelItem class_item = m_dom->findClass(qualified_name);
    bool isqobject = class_item && class_item->extendsClass("QObject");

    if (class_item && !isqobject) {
        QStringList baseClasses = class_item->baseClasses();
        for (int i=0; i<baseClasses.count(); ++i) {
            isqobject = isQObject(baseClasses.at(i));
            if (isqobject)
                break;
        }
    }

    return isqobject;
}


bool MetaJavaBuilder::isEnum(const QStringList &qualified_name)
{
    CodeModelItem item = m_dom->model()->findItem(qualified_name, m_dom->toItem());
    return item && item->kind() == _EnumModelItem::__node_kind;
}

MetaJavaType *MetaJavaBuilder::inheritTemplateType(const QList<MetaJavaType *> &template_types,
                                                   MetaJavaType *java_type)
{

    if (!java_type || (!java_type->typeEntry()->isTemplateArgument() && !java_type->hasInstantiations()))
        return java_type;

    MetaJavaType *returned = java_type->copy();

    if (returned->typeEntry()->isTemplateArgument()) {
        const TemplateArgumentEntry *tae = static_cast<const TemplateArgumentEntry *>(returned->typeEntry());

        MetaJavaType *t = returned->copy();

        t->setTypeEntry(template_types.at(tae->ordinal())->typeEntry());
        t->setIndirections(template_types.at(tae->ordinal())->indirections() + t->indirections()
                           ? 1
                           : 0);
        decideUsagePattern(t);

        delete returned;
        returned = inheritTemplateType(template_types, t);
    }

    if (returned->hasInstantiations()) {
        QList<MetaJavaType *> instantiations = returned->instantiations();
        for (int i=0; i<instantiations.count(); ++i)
            instantiations[i] = inheritTemplateType(template_types, instantiations.at(i));
        returned->setInstantiations(instantiations);
    }

    return returned;
}

bool MetaJavaBuilder::inheritTemplate(MetaJavaClass *subclass,
                                      const MetaJavaClass *template_class,
                                      const TypeParser::Info &info)
{
    QList<TypeParser::Info> targs = info.template_instantiations;

    QList<MetaJavaType *> template_types;
    foreach (const TypeParser::Info &i, targs) {
        TypeEntry *t = TypeDatabase::instance()->findType(i.qualified_name.join("::"));

        if (t != 0) {
            MetaJavaType *temporary_type = new MetaJavaType;
            temporary_type->setTypeEntry(t);
            temporary_type->setConstant(i.is_constant);
            temporary_type->setReference(i.is_reference);
            temporary_type->setIndirections(i.indirections);
            template_types << temporary_type;
        } else {
            ReportHandler::warning(QString("unknown type used as template argument: %1 in %2")
                                   .arg(i.toString())
                                   .arg(info.toString()));
            return false;
        }
    }

    MetaJavaFunctionList funcs = subclass->functions();
    foreach (const MetaJavaFunction *function, template_class->functions()) {
        MetaJavaFunction *f = function->copy();
        f->setArguments(MetaJavaArgumentList());

        MetaJavaType *ftype = function->type();
        f->setType(inheritTemplateType(template_types, ftype));

        foreach (MetaJavaArgument *argument, function->arguments()) {
            MetaJavaType *atype = argument->type();

            MetaJavaArgument *arg = argument->copy();
            arg->setType(inheritTemplateType(template_types, atype));
            f->addArgument(arg);
        }

        // There is no base class in java to inherit from here, so the
        // template instantiation is the class that implements the function..
        f->setImplementingClass(subclass);

        if (f->isConstructor()) {
            delete f;
            continue;
        }

        // if the instantiation has a function named the same as an existing
        // function we have shadowing so we need to skip it.
        bool found = false;
        for (int i=0; i<funcs.size(); ++i) {
            if (funcs.at(i)->name() == f->name()) {
                found = true;
                continue;
            }
        }
        if (found) {
            delete f;
            continue;
        }

        subclass->addFunction(f);
    }

    // Clean up
    foreach (MetaJavaType *type, template_types) {
        delete type;
    }

    return true;
}


static void write_reject_log_file(const QString &name,
                                  const QMap<QString, MetaJavaBuilder::RejectReason> &rejects)
{
    QFile f(name);
    if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        ReportHandler::warning(QString("failed to write log file: '%1'")
                               .arg(f.fileName()));
        return;
    }

    QTextStream s(&f);


    for (int reason=0; reason<MetaJavaBuilder::NoReason; ++reason) {
        s << QString(72, '*') << endl;
        switch (reason) {
        case MetaJavaBuilder::NotInTypeSystem:
            s << "Not in type system";
            break;
        case MetaJavaBuilder::GenerationDisabled:
            s << "Generation disabled by type system";
            break;
        case MetaJavaBuilder::RedefinedToNotClass:
            s << "Type redefined to not be a class";
            break;

        case MetaJavaBuilder::UnmatchedReturnType:
            s << "Unmatched return type";
            break;

        case MetaJavaBuilder::UnmatchedArgumentType:
            s << "Unmatched argument type";
            break;

        default:
            s << "unknown reason";
            break;
        }

        s << endl;

        for (QMap<QString, MetaJavaBuilder::RejectReason>::const_iterator it = rejects.constBegin();
             it != rejects.constEnd(); ++it) {
            if (it.value() != reason)
                continue;
            s << " - " << it.key() << endl;
        }

        s << QString(72, '*') << endl << endl;
    }

}


void MetaJavaBuilder::dumpLog()
{
    write_reject_log_file("mjb_rejected_classes.log", m_rejected_classes);
    write_reject_log_file("mjb_rejected_enums.log", m_rejected_enums);
    write_reject_log_file("mjb_rejected_functions.log", m_rejected_functions);
    write_reject_log_file("mjb_rejected_fields.log", m_rejected_fields);
}
