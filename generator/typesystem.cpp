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

#include "typesystem.h"

#include "customtypes.h"

#include <reporthandler.h>

#include <QtXml>
#include <QHash>
#include <QStack>

struct StackElement
{
    enum ElementType {
        None = 0x0,

        // Type tags (0x1, ... , 0xff)
        ObjectTypeEntry      = 0x1,
        ValueTypeEntry       = 0x2,
        InterfaceTypeEntry   = 0x3,
        NamespaceTypeEntry   = 0x4,
        ComplexTypeEntryMask = 0xf,

        // Non-complex type tags (0x10, 0x20, ... , 0xf0)
        PrimitiveTypeEntry   = 0x10,
        EnumTypeEntry        = 0x20,
        TypeEntryMask        = 0xff,

        // Simple tags (0x100, 0x200, ... , 0xf00)
        ExtraIncludes           = 0x100,
        Include                 = 0x200,
        ModifyFunction          = 0x300,
        ModifyField             = 0x400,
        Root                    = 0x500,
        CustomMetaConstructor   = 0x600,
        CustomMetaDestructor    = 0x700,
        ArgumentMap             = 0x800,
        SuppressedWarning       = 0x900,
        Rejection               = 0xa00,
        LoadTypesystem          = 0xb00,
        SimpleMask              = 0xf00,

        // Code snip tags (0x1000, 0x2000, ... , 0xf000)
        InjectCode =           0x1000,
        InjectCodeInFunction = 0x2000,
        CodeSnipMask =         0xf000,

        // Function modifier tags (0x10000, 0x20000, ... , 0xf0000)
        Access                   = 0x010000,
        Removal                  = 0x020000,
        Rename                   = 0x040000,
        DisableGC                = 0x080000,
        ReplaceDefaultExpression = 0x100000,
        FunctionModifiers        = 0xff0000
    };

    StackElement() : entry(0), type(None) { }

    TypeEntry *entry;
    ElementType type;
};

class Handler : public QXmlDefaultHandler
{
public:
    Handler(TypeDatabase *database, bool generate)
        : m_database(database), m_generate(generate ? TypeEntry::GenerateAll : TypeEntry::GenerateForSubclass)
    {
    }

    bool startElement(const QString &namespaceURI, const QString &localName,
                      const QString &qName, const QXmlAttributes &atts);
    bool endElement(const QString &namespaceURI, const QString &localName, const QString &qName);

    QString errorString() const { return m_error; }
    bool error(const QXmlParseException &exception);
    bool fatalError(const QXmlParseException &exception);
    bool warning(const QXmlParseException &exception);

    bool characters(const QString &ch);

private:
    void fetchAttributeValues(const QString &name, const QXmlAttributes &atts,
                              QHash<QString, QString> *acceptedAttributes);

    TypeDatabase *m_database;
    QStack<StackElement> m_stack;
    QString m_defaultPackage;
    QString m_defaultSuperclass;
    QString m_error;
    TypeEntry::CodeGeneration m_generate;

    CodeSnipList m_code_snips;
    QString m_current_data;
    FunctionModificationList m_function_mods;
    FieldModificationList m_field_mods;
};

bool Handler::error(const QXmlParseException &e)
{
    qWarning("Error: line=%d, column=%d, message=%s\n",
             e.lineNumber(), e.columnNumber(), qPrintable(e.message()));
    return false;
}

bool Handler::fatalError(const QXmlParseException &e)
{
    qWarning("Fatal error: line=%d, column=%d, message=%s\n",
             e.lineNumber(), e.columnNumber(), qPrintable(e.message()));

    return false;
}

bool Handler::warning(const QXmlParseException &e)
{
    qWarning("Warning: line=%d, column=%d, message=%s\n",
             e.lineNumber(), e.columnNumber(), qPrintable(e.message()));

    return false;
}

void Handler::fetchAttributeValues(const QString &name, const QXmlAttributes &atts,
                                   QHash<QString, QString> *acceptedAttributes)
{
    Q_ASSERT(acceptedAttributes != 0);

    for (int i=0; i<atts.length(); ++i) {
        QString key = atts.localName(i).toLower();
        QString val = atts.value(i);

        if (!acceptedAttributes->contains(key)) {
            ReportHandler::warning(QString("Unknown attribute for '%1': '%2'").arg(name).arg(key));
        } else {
            (*acceptedAttributes)[key] = val;
        }
    }
}

bool Handler::endElement(const QString &, const QString &, const QString &)
{
    if (m_stack.isEmpty())
        return true;

    StackElement element = m_stack.top();

    switch (element.type) {
    case StackElement::ObjectTypeEntry:
    case StackElement::ValueTypeEntry:
        {
            ComplexTypeEntry *centry = static_cast<ComplexTypeEntry *>(element.entry);
            centry->setFunctionModifications(m_function_mods);
            centry->setFieldModifications(m_field_mods);
            centry->setCodeSnips(m_code_snips);
            if (centry->designatedInterface()) {
                centry->designatedInterface()->setCodeSnips(m_code_snips);
                centry->designatedInterface()->setFunctionModifications(m_function_mods);
            }
            m_code_snips = CodeSnipList();
            m_function_mods = FunctionModificationList();
            m_field_mods = FieldModificationList();
        }
        break;
    case StackElement::CustomMetaConstructor:
        {
            CustomFunction func = element.entry->customConstructor();
            func.code = m_current_data;
            element.entry->setCustomConstructor(func);
        }
        break ;
    case StackElement::CustomMetaDestructor:
        {
            CustomFunction func = element.entry->customDestructor();
            func.code = m_current_data;
            element.entry->setCustomDestructor(func);
        }
        break ;
    default:
        break;
    }

    m_stack.pop();

    if (!m_stack.isEmpty()) {
        StackElement &parent = m_stack.top();

        if (element.type & StackElement::CodeSnipMask) {
            switch (parent.type) {
            case StackElement::ModifyFunction:
                m_function_mods.last().snips.last().code = m_current_data;
                break ;
            case StackElement::ObjectTypeEntry:
            case StackElement::ValueTypeEntry:
                m_code_snips.last().code = m_current_data;
                break ;
            default:
                Q_ASSERT(false);
            };
        }
    }

    return true;
}

bool Handler::characters(const QString &ch)
{
    if (!m_stack.isEmpty()) {
        StackElement &element = m_stack.top();
        if (element.type & StackElement::CodeSnipMask
            || element.type & StackElement::CustomMetaConstructor
            || element.type & StackElement::CustomMetaDestructor)
            m_current_data += ch;
    }
    return true;
}

bool Handler::startElement(const QString &, const QString &n,
                           const QString &, const QXmlAttributes &atts)
{
    QString tagName = n.toLower();
    StackElement element;
    m_current_data = QString();

    static QHash<QString, StackElement::ElementType> tagNames;
    if (tagNames.isEmpty()) {
        tagNames["rejection"] = StackElement::Rejection;
        tagNames["primitive-type"] = StackElement::PrimitiveTypeEntry;
        tagNames["object-type"] = StackElement::ObjectTypeEntry;
        tagNames["value-type"] = StackElement::ValueTypeEntry;
        tagNames["interface-type"] = StackElement::InterfaceTypeEntry;
        tagNames["namespace-type"] = StackElement::NamespaceTypeEntry;
        tagNames["enum-type"] = StackElement::EnumTypeEntry;
        tagNames["extra-includes"] = StackElement::ExtraIncludes;
        tagNames["include"] = StackElement::Include;
        tagNames["inject-code"] = StackElement::InjectCode;
        tagNames["modify-function"] = StackElement::ModifyFunction;
        tagNames["modify-field"] = StackElement::ModifyField;
        tagNames["access"] = StackElement::Access;
        tagNames["remove"] = StackElement::Removal;
        tagNames["rename"] = StackElement::Rename;
        tagNames["typesystem"] = StackElement::Root;
        tagNames["custom-constructor"] = StackElement::CustomMetaConstructor;
        tagNames["custom-destructor"] = StackElement::CustomMetaDestructor;
        tagNames["argument-map"] = StackElement::ArgumentMap;
        tagNames["suppress-warning"] = StackElement::SuppressedWarning;
        tagNames["load-typesystem"] = StackElement::LoadTypesystem;
        tagNames["disable-gc"] = StackElement::DisableGC;
        tagNames["replace-default-expression"] = StackElement::ReplaceDefaultExpression;
    }

    if (!tagNames.contains(tagName)) {
        m_error = QString("Unknown tag name: '%1'").arg(tagName);
        return false;
    }

    element.type = tagNames[tagName];
    if (element.type & StackElement::TypeEntryMask) {
        if (!m_stack.isEmpty()) {
            m_error = "Nested types not supported";
            return false;
        }

        QHash<QString, QString> attributes;
        attributes["name"] = QString();

        switch (element.type) {
        case StackElement::PrimitiveTypeEntry:
            attributes["java-name"] = QString();
            attributes["jni-name"] = QString();
            attributes["preferred-conversion"] = "yes";
            break ;
        case StackElement::EnumTypeEntry:
            attributes["flags"] = "no";
            break;
        
        case StackElement::InterfaceTypeEntry:
        case StackElement::ObjectTypeEntry:
            attributes["memory-managed"] = "no"; 
            // fall through
        case StackElement::ValueTypeEntry:
            attributes["default-superclass"] = m_defaultSuperclass;
            // fall through
        case StackElement::NamespaceTypeEntry:
            attributes["package"] = m_defaultPackage;                        
            break;
        default:
            ; // nada
        };

        fetchAttributeValues(tagName, atts, &attributes);

        QString name = attributes["name"];

        if (name.isEmpty()) {
            m_error = "no 'name' attribute specified";
            return false;
        }

        TypeEntry *tmp = m_database->findType(name);
        if (tmp != 0) {
            ReportHandler::warning(QString("Duplicate type entry: '%1'").arg(name));
        }

        switch (element.type) {
        case StackElement::PrimitiveTypeEntry:
            {
                QString java_name = attributes["java-name"];
                QString jni_name = attributes["jni-name"];
                QString preferred_conversion = attributes["preferred-conversion"].toLower();

                if (java_name.isEmpty())
                    java_name = name;
                if (jni_name.isEmpty())
                    jni_name = name;

                PrimitiveTypeEntry *type = new PrimitiveTypeEntry(name);
                type->setCodeGeneration(m_generate);
                type->setJavaName(java_name);
                type->setJniName(jni_name);

                if (preferred_conversion == "yes") {
                    type->setPreferredConversion(true);
                } else if (preferred_conversion == "no") {
                    type->setPreferredConversion(false);
                } else {
                    QString debug = QString("Preferred conversion value '%1' not valid for '%2'. "
                                            "Will default to 'yes'.")
                                    .arg(preferred_conversion).arg(name);
                    ReportHandler::warning(debug);
                }

                element.entry = type;
            }
            break ;
        case StackElement::EnumTypeEntry:
            element.entry = new EnumTypeEntry(name);
            element.entry->setCodeGeneration(m_generate);
            ((EnumTypeEntry *) element.entry)->setJavaPackage(attributes["package"]);

            // put in the flags parallel...
            if (!attributes["flags"].isEmpty()) {
                FlagsTypeEntry *ftype = new FlagsTypeEntry("QFlags<" + name + ">");
                ftype->setOriginalName(attributes["flags"]);
                ftype->setCodeGeneration(m_generate);
                m_database->addType(ftype);
            }
            break ;
        case StackElement::InterfaceTypeEntry:
            {
                ObjectTypeEntry *otype = new ObjectTypeEntry(name);
                otype->setCodeGeneration(m_generate);
                InterfaceTypeEntry *itype =
                    new InterfaceTypeEntry(InterfaceTypeEntry::interfaceName(name));
                itype->setCodeGeneration(m_generate);
                otype->setDesignatedInterface(itype);
                itype->setOrigin(otype);
                element.entry = otype;
            }
            // fall through
        case StackElement::NamespaceTypeEntry:
            if (element.entry == 0) {
                element.entry = new NamespaceTypeEntry(name);
                element.entry->setCodeGeneration(m_generate);
            }
            // fall through
        case StackElement::ObjectTypeEntry:
            if (element.entry == 0) {
                element.entry = new ObjectTypeEntry(name);
                element.entry->setCodeGeneration(m_generate);
                if (attributes["memory-managed"] == "yes")
                    static_cast<ObjectTypeEntry *>(element.entry)->setMemoryManaged(true);
            }
            // fall through
        case StackElement::ValueTypeEntry:
            {
                if (element.entry == 0) {
                    element.entry = new ValueTypeEntry(name);
                    element.entry->setCodeGeneration(m_generate);
                }

                ComplexTypeEntry *ctype = static_cast<ComplexTypeEntry *>(element.entry);
                ctype->setJavaPackage(attributes["package"]);
                ctype->setDefaultSuperclass(attributes["default-superclass"]);

                ctype->setInclude(Include(Include::IncludePath, ctype->name()));
                ctype = ctype->designatedInterface();
                if (ctype != 0)
                    ctype->setJavaPackage(attributes["package"]);
            }
            break ;
        default:
            Q_ASSERT(false);
        };

        Q_ASSERT(element.entry);
        m_database->addType(element.entry);
    } else if (element.type != StackElement::None) {
        bool topLevel = element.type == StackElement::Root
      || element.type == StackElement::SuppressedWarning
      || element.type == StackElement::Rejection
      || element.type == StackElement::LoadTypesystem;

        if (!topLevel && m_stack.isEmpty()) {
            m_error = QString("Tag requires parent: '%1'").arg(tagName);
            return false;
        }

        StackElement topElement = topLevel ? StackElement() : m_stack.top();
        element.entry = topElement.entry;

        QHash<QString, QString> attributes;
        switch (element.type) {
        case StackElement::Root:
            attributes["package"] = QString();
            attributes["default-superclass"] = QString();
            break ;
        case StackElement::LoadTypesystem:
            attributes["name"] = QString();
            attributes["generate"] = "yes";
            break;
        case StackElement::DisableGC:
            attributes["argument"] = QString();
            break;
        case StackElement::SuppressedWarning:
            attributes["text"] = QString();
            break ;
        case StackElement::ReplaceDefaultExpression:
            attributes["index"] = QString();
            attributes["with"] = QString();
            break ;
        case StackElement::ModifyFunction:
            attributes["signature"] = QString();
            attributes["class"] = "java";
            break ;
        case StackElement::ModifyField:
            attributes["name"] = QString();
            attributes["write"] = "true";
            attributes["read"] = "true";
            break ;
        case StackElement::Access:
            attributes["modifier"] = QString();
            break;
        case StackElement::Include:
            attributes["file-name"] = QString();
            attributes["location"] = QString();
            break ;
        case StackElement::CustomMetaConstructor:
            attributes["name"] = topElement.entry->name().toLower() + "_create";
            attributes["param-name"] = "copy";
            break ;
        case StackElement::CustomMetaDestructor:
            attributes["name"] = topElement.entry->name().toLower() + "_delete";
            attributes["param-name"] = "copy";
            break ;
        case StackElement::InjectCode:
            if (topElement.type == StackElement::ModifyFunction) {
                FunctionModification mod = m_function_mods.last();

                switch (mod.language) {
                case CodeSnip::JavaCode: attributes["class"] = "java"; break;
                case CodeSnip::NativeCode: attributes["class"] = "native"; break;
                case CodeSnip::ShellCode: attributes["class"] = "shell"; break;
                case CodeSnip::ShellDeclaration: attributes["class"] = "shell-declaration"; break;
                };
            } else {
                attributes["class"] = "java";
            }
            attributes["position"] = "beginning";
            break ;
        case StackElement::ArgumentMap:
            attributes["position"] = "1";
            attributes["meta-name"] = QString();
            break ;
        case StackElement::Rename:
            attributes["to"] = QString();
            break ;
        case StackElement::Rejection:
            attributes["class"] = "*";
            attributes["function-name"] = "*";
            attributes["field-name"] = "*";
            break;
        case StackElement::Removal:
            attributes["exclusive"] = "no";
        default:
            ; // nada
        };

        if (attributes.count() > 0)
            fetchAttributeValues(tagName, atts, &attributes);

        switch (element.type) {
        case StackElement::Root:
            m_defaultPackage = attributes["package"];
            m_defaultSuperclass = attributes["default-superclass"];
            element.type = StackElement::None; // don't push on stack
            break ;
        case StackElement::LoadTypesystem:
            {
                QString name = attributes["name"];
                if (name.isEmpty()) {
                    m_error = "No typesystem name specified";
                    return false;
                }

                QString str_generate = attributes["generate"].toLower();

                bool generate;
                if (str_generate == "no") {
                    generate = false;
                } else if (str_generate == "yes") {
                    generate = true;
                } else {
                    m_error = "Use 'yes' or 'no' to indicate whether a typesystem should generate code";
                    return false;
                }

                if (!m_database->parseFile(name, generate)) {
                    m_error = QString("Failed to parse: '%1'").arg(name);
                    return false;
                }
            }
            break;
        case StackElement::DisableGC: 
            {
                QString argument = attributes["argument"];

                if (topElement.type != StackElement::ModifyFunction) {
                    m_error = "disable-gc requires modify-function as parent";
                    return false;
                }

                CodeSnip::Language lang = m_function_mods.last().language;
                if (lang != CodeSnip::JavaCode && lang != CodeSnip::ShellCode) {
                    m_error = "You can only disable garbage collection in java and shell code";
                    return false;
                }

                if (argument.isEmpty() && lang == CodeSnip::JavaCode) {
                    m_error = "You need to specify an argument index or 'this' "
                              "for disable gc in java function modifications";
                    return false;
                } else if (argument.toLower() == "this") {
                    argument = "0";
                } 

                if (!argument.isEmpty() && lang == CodeSnip::ShellCode) {
                    ReportHandler::warning("Argument attribute ignored when disabling GC for "
                                           "shell code");
                } else if (argument.isEmpty()) {
                    argument = "-1";
                }

                m_function_mods.last().disable_gc_argument_indexes[argument.toInt()] = true;
            }
            break;
        case StackElement::SuppressedWarning:
            if (attributes["text"].isEmpty())
                ReportHandler::warning("Suppressed warning with no text specified");
            else
                m_database->addSuppressedWarning(attributes["text"]);
            break ;
        case StackElement::ArgumentMap:
            {
                if (!(topElement.type & StackElement::CodeSnipMask)) {
                    m_error = "Argument maps requires code injection as parent";
                    return false;
                }

                bool ok;
                int pos = attributes["position"].toInt(&ok);
                if (!ok) {
                    m_error = QString("Can't convert position '%1' to integer")
                              .arg(attributes["position"]);
                    return false;
                }

                if (pos <= 0) {
                    m_error = QString("Argument position %1 must be a positive number").arg(pos);
                    return false;
                }

                QString meta_name = attributes["meta-name"];
                if (meta_name.isEmpty()) {
                    ReportHandler::warning("Empty meta name in argument map");
                }

                if (topElement.type == StackElement::InjectCodeInFunction) {
                    m_function_mods.last().snips.last().argumentMap[pos] = meta_name;
                } else {
                    ReportHandler::warning("Argument maps are only useful for injection of code "
                                           "into functions.");
                }
            }
            break ;
        case StackElement::Rename:
        case StackElement::Removal:
        case StackElement::Access:
            {
                if (topElement.type != StackElement::ModifyFunction) {
                    m_error = "Function modification parent required";
                    return false;
                }

                if (m_function_mods.last().language != CodeSnip::JavaCode) {
                    m_error = "Function renaming, removal, and access modification only supported for java "
                              "functions";
                    return false;
                }

                QString modifier;
                if (element.type == StackElement::Removal) {
                    if (attributes["exclusive"].toLower() == "yes")
                        modifier = "exclusive-remove";
                    else
                        modifier = "remove";
                } else if (element.type == StackElement::Rename) {
                    modifier = "rename";
                    QString renamed_to = attributes["to"];
                    if (renamed_to.isEmpty()) {
                        m_error = "Rename modifier requires 'renamed-to' attribute";
                        return false;
                    }
                    m_function_mods.last().setRenamedTo(renamed_to);
                } else {
                    modifier = attributes["modifier"].toLower();
                }

                if (modifier.isEmpty()) {
                    m_error = "No access modification specified";
                    return false;
                }

                static QHash<QString, FunctionModification::Modifiers> modifierNames;
                if (modifierNames.isEmpty()) {
                    modifierNames["private"] = FunctionModification::Private;
                    modifierNames["public"] = FunctionModification::Public;
                    modifierNames["protected"] = FunctionModification::Protected;
                    modifierNames["friendly"] = FunctionModification::Friendly;
                    modifierNames["remove"] = FunctionModification::Remove;
                    modifierNames["exclusive-remove"] = FunctionModification::Modifiers(FunctionModification::Remove | FunctionModification::Exclusive);
                    modifierNames["rename"] = FunctionModification::Rename;
                }

                if (!modifierNames.contains(modifier)) {
                    m_error = QString("Unknown access modifier: '%1'").arg(modifier);
                    return false;
                }

                FunctionModification::Modifiers mod = modifierNames[modifier];
                m_function_mods.last().modifiers |= mod;
            }
            break ;

        case StackElement::ModifyField:
            {
                QString name = attributes["name"];
                if (name.isEmpty())
                    break;
                FieldModification fm;
                fm.name = name;
                fm.modifiers = 0;

                QString read = attributes["read"];
                QString write = attributes["write"];

                if (read == "true") fm.modifiers |= FieldModification::Readable;
                if (write == "true") fm.modifiers |= FieldModification::Writable;

                m_field_mods << fm;
            }
            break;
        case StackElement::ModifyFunction:
            {
                if (!(topElement.type & StackElement::ComplexTypeEntryMask)) {
                    m_error = "Modify function requires type as parent";
                    return false;
                }
                QString signature = attributes["signature"];

                signature = QMetaObject::normalizedSignature(signature.toLocal8Bit().constData());
                if (signature.isEmpty()) {
                    m_error = "No signature for modified function";
                    return false;
                }

                static QHash<QString, CodeSnip::Language> languageNames;
                if (languageNames.isEmpty()) {
                    languageNames["java"] = CodeSnip::JavaCode;
                    languageNames["native"] = CodeSnip::NativeCode;
                    languageNames["shell"] = CodeSnip::ShellCode;
                    languageNames["shell-declaration"] = CodeSnip::ShellDeclaration;
                }

                QString className = attributes["class"].toLower();
                if (!languageNames.contains(className)) {
                    m_error = QString("Invalid class specifier: '%1'").arg(className);
                    return false;
                }

                FunctionModification mod;
                mod.language = languageNames.value(className);
                mod.signature = signature;

                QString debug = QString("Adding function modification for '%1' in language '%2' == %3")
                                .arg(signature).arg(className).arg(int(mod.language));
                ReportHandler::debugMedium(debug);

                m_function_mods << mod;
            }
            break ;
        case StackElement::ReplaceDefaultExpression:
            if (!(topElement.type & StackElement::ModifyFunction)) {
                m_error = "Replace default expression only allowed as child of modify function";
                return false;
            }

            if (attributes["index"].isEmpty()) {
                m_error = "No index specified for replace default expression";
                return false;
            }

            m_function_mods.last().modifiers |= FunctionModification::ReplaceExpression;
            m_function_mods.last().renamed_default_expressions[attributes["index"].toUInt()] = attributes["with"];
            break ;
        case StackElement::CustomMetaConstructor:
        case StackElement::CustomMetaDestructor:

            {
                CustomFunction func(attributes["name"]);
                func.param_name = attributes["param-name"];
                if (element.type == StackElement::CustomMetaConstructor)
                    element.entry->setCustomConstructor(func);
                else
                    element.entry->setCustomDestructor(func);
            }
            break ;
        case StackElement::InjectCode:
            {
                if ((topElement.type & StackElement::ComplexTypeEntryMask) == 0 &&
                    (topElement.type & StackElement::ModifyFunction) == 0) {
                    m_error = "Modify function requires complex type entry or function modification "
                              "as parent";
                    return false;
                }

                static QHash<QString, CodeSnip::Language> languageNames;
                if (languageNames.isEmpty()) {
                    languageNames["java"] = CodeSnip::JavaCode;
                    languageNames["native"] = CodeSnip::NativeCode;
                    languageNames["shell"] = CodeSnip::ShellCode;
                    languageNames["shell-declaration"] = CodeSnip::ShellDeclaration;
                }

                QString className = attributes["class"].toLower();
                if (!languageNames.contains(className)) {
                    m_error = QString("Invalid class specifier: '%1'").arg(className);
                    return false;
                }

                static QHash<QString, CodeSnip::Position> positionNames;
                if (positionNames.isEmpty()) {
                    positionNames["beginning"] = CodeSnip::Beginning;
                    positionNames["end"] = CodeSnip::End;
                }

                QString position = attributes["position"].toLower();
                if (!positionNames.contains(position)) {
                    m_error = QString("Invalid position: '%1'").arg(position);
                    return false;
                }

                CodeSnip snip;
                snip.language = languageNames[className];
                snip.position = positionNames[position];

                if (topElement.type & StackElement::ModifyFunction) {
                    FunctionModification mod = m_function_mods.last();
                    if (mod.language != CodeSnip::ShellCode) {
                        m_error = "Code injection in functions only supported for shell functions";
                        return false;
                    }

                    if (mod.language != snip.language) {
                        ReportHandler::warning("Code injected in function modification has "
                                               "different class than parent while modifying "
                                               "'%1'.");
                    }

                    m_function_mods.last().snips << snip;
                    element.type = StackElement::InjectCodeInFunction;
                } else {
                    m_code_snips << snip;
                }
            }
            break ;
        case StackElement::Include:
            {
                QString location = attributes["location"].toLower();

                static QHash<QString, Include::IncludeType> locationNames;
                if (locationNames.isEmpty()) {
                    locationNames["global"] = Include::IncludePath;
                    locationNames["local"] = Include::LocalPath;
                    locationNames["java"] = Include::JavaImport;
                }

                if (!locationNames.contains(location)) {
                    m_error = QString("Location not recognized: '%1'").arg(location);
                    return false;
                }

                Include::IncludeType loc = locationNames[location];
                Include inc(loc, attributes["file-name"]);

                ComplexTypeEntry *ctype = static_cast<ComplexTypeEntry *>(element.entry);
                if (topElement.type & StackElement::ComplexTypeEntryMask) {
                    ctype->setInclude(inc);
                } else if (topElement.type == StackElement::ExtraIncludes) {
                    ctype->addExtraInclude(inc);
                } else {
                    m_error = "Only supported parents are complex types and extra-includes";
                    return false;
                }

                inc = ctype->include();
                IncludeList lst = ctype->extraIncludes();
                ctype = ctype->designatedInterface();
                if (ctype != 0) {
                    ctype->setExtraIncludes(lst);
                    ctype->setInclude(inc);
                }
            }
            break ;
        case StackElement::Rejection:
            {
                QString cls = attributes["class"];
                QString function = attributes["function-name"];
                QString field = attributes["field-name"];
                if (cls == "*" && function == "*" && field == "*") {
                    m_error = "bad reject entry, neither 'class', 'function-name' nor "
                              "'field' specified";
                    return false;
                }
                m_database->addRejection(cls, function, field);
            }
            break;
        default:
            ; // nada
        };
    }

    if (element.type != StackElement::None)
        m_stack.push(element);

    return true;
}

TypeDatabase *TypeDatabase::instance()
{
    static TypeDatabase *db = new TypeDatabase();
    return db;
}

TypeDatabase::TypeDatabase() : m_suppressWarnings(true)
{
    addType(new StringTypeEntry("QString"));

    StringTypeEntry *e = new StringTypeEntry("QLatin1String");
    e->setPreferredConversion(false);
    addType(e);

    addType(new CharTypeEntry("QChar"));

    CharTypeEntry *c = new CharTypeEntry("QLatin1Char");
    c->setPreferredConversion(false);
    addType(c);

    {
        VariantTypeEntry *qvariant = new VariantTypeEntry("QVariant");
        qvariant->setCodeGeneration(TypeEntry::GenerateNothing);
        addType(qvariant);
    }

    addType(new ThreadTypeEntry());
    addType(new VoidTypeEntry());

    // Predefined containers...
    addType(new ContainerTypeEntry("QList", ContainerTypeEntry::ListContainer));
    addType(new ContainerTypeEntry("QStringList", ContainerTypeEntry::StringListContainer));
    addType(new ContainerTypeEntry("QLinkedList", ContainerTypeEntry::LinkedListContainer));
    addType(new ContainerTypeEntry("QVector", ContainerTypeEntry::VectorContainer));
    addType(new ContainerTypeEntry("QStack", ContainerTypeEntry::StackContainer));
    addType(new ContainerTypeEntry("QSet", ContainerTypeEntry::SetContainer));
    addType(new ContainerTypeEntry("QMap", ContainerTypeEntry::MapContainer));
    addType(new ContainerTypeEntry("QHash", ContainerTypeEntry::HashContainer));
    addType(new ContainerTypeEntry("QPair", ContainerTypeEntry::PairContainer));
    addType(new ContainerTypeEntry("QQueue", ContainerTypeEntry::QueueContainer));

    // Custom types...
    addType(new QModelIndexTypeEntry());
}

bool TypeDatabase::parseFile(const QString &filename, bool generate)
{
    QFile file(filename);
    Q_ASSERT(file.exists());
    QXmlInputSource source(&file);

    QXmlSimpleReader reader;
    Handler handler(this, generate);

    reader.setContentHandler(&handler);
    reader.setErrorHandler(&handler);

    return reader.parse(&source, false);
}

QString PrimitiveTypeEntry::javaObjectName() const
{
    static QHash<QString, QString> table;
    if (table.isEmpty()) {
        table["boolean"] = "Boolean";
        table["byte"] = "Byte";
        table["char"] = "Character";
        table["short"] = "Short";
        table["int"] = "Integer";
        table["long"] = "Long";
        table["float"] = "Float";
        table["double"] = "Double";
    }
    Q_ASSERT(table.contains(javaName()));
    return table[javaName()];
}

ContainerTypeEntry *TypeDatabase::findContainerType(const QString &name)
{
    QString template_name = name;

    int pos = name.indexOf('<');
    if (pos > 0)
        template_name = name.left(pos);

    TypeEntry *type_entry = findType(template_name);
    if (type_entry && type_entry->isContainer())
        return static_cast<ContainerTypeEntry *>(type_entry);
    return 0;
}

PrimitiveTypeEntry *TypeDatabase::findJavaPrimitiveType(const QString &java_name)
{
    foreach (TypeEntry *e, m_entries.values()) {
        if (e && e->isPrimitive()) {
            PrimitiveTypeEntry *pe = static_cast<PrimitiveTypeEntry *>(e);
            if (pe->javaName() == java_name && pe->preferredConversion())
                return pe;
        }
    }

    return 0;
}

IncludeList TypeDatabase::extraIncludes(const QString &className)
{
    ComplexTypeEntry *typeEntry = findComplexType(className);
    if (typeEntry != 0)
        return typeEntry->extraIncludes();
    else
        return IncludeList();
}



QString Include::toString() const
{
    if (type == IncludePath)
        return "#include <" + name + '>';
    else if (type == LocalPath)
        return "#include \"" + name + "\"";
    else
        return "import " + name + ";";
}

QString FunctionModification::accessModifierString() const
{
    if (isPrivate()) return "private";
    if (isProtected()) return "protected";
    if (isPublic()) return "public";
    if (isFriendly()) return "friendly";
    return QString();
}

FunctionModificationList ComplexTypeEntry::functionModifications(const QString &signature) const
{
    FunctionModificationList lst;

    for (int i=0; i<m_function_mods.count(); ++i) {
        FunctionModification mod = m_function_mods.at(i);
        if (mod.signature == signature)
            lst << mod;
    }

    return lst;
}

FieldModification ComplexTypeEntry::fieldModification(const QString &name) const
{
    for (int i=0; i<m_field_mods.size(); ++i)
        if (m_field_mods.at(i).name == name)
            return m_field_mods.at(i);
    FieldModification mod;
    mod.name = name;
    mod.modifiers = FieldModification::Readable | FieldModification::Writable;
    return mod;
}

QString ContainerTypeEntry::javaPackage() const
{
    if (m_type == PairContainer)
        return "com.trolltech.qt";
    return "java.util";
}

QString ContainerTypeEntry::javaName() const
{

    switch (m_type) {
    case StringListContainer: return "List";
    case ListContainer: return "List";
    case LinkedListContainer: return "LinkedList";
    case VectorContainer: return "List";
    case StackContainer: return "Stack";
    case QueueContainer: return "Queue";
    case SetContainer: return "Set";
    case MapContainer: return "SortedMap";
        //     case MultiMapContainer: return "MultiMap";
    case HashContainer: return "HashMap";
        //     case MultiHashCollectio: return "MultiHash";
    case PairContainer: return "QPair";
    default:
        qWarning("bad type... %d", m_type);
        break;
    }
    return QString();
}

QString ContainerTypeEntry::qualifiedCppName() const
{
    if (m_type == StringListContainer)
        return "QStringList";
    return ComplexTypeEntry::qualifiedCppName();
}

void TypeDatabase::addRejection(const QString &class_name, const QString &function_name,
                                const QString &field_name)
{
    TypeRejection r;
    r.class_name = class_name;
    r.function_name = function_name;
    r.field_name = field_name;

    m_rejections << r;
}

bool TypeDatabase::isClassRejected(const QString &class_name)
{
    if (!m_rebuild_classes.isEmpty())
        return !m_rebuild_classes.contains(class_name);

    foreach (const TypeRejection &r, m_rejections)
        if (r.class_name == class_name && r.function_name == "*" && r.field_name == "*") {
            return true;
        }
    return false;
}

bool TypeDatabase::isFunctionRejected(const QString &class_name, const QString &function_name)
{
    foreach (const TypeRejection &r, m_rejections)
        if (r.function_name == function_name &&
            (r.class_name == class_name || r.class_name == "*"))
            return true;
    return false;
}


bool TypeDatabase::isFieldRejected(const QString &class_name, const QString &field_name)
{
    foreach (const TypeRejection &r, m_rejections)
        if (r.field_name == field_name &&
            (r.class_name == class_name || r.class_name == "*"))
            return true;
    return false;
}

/*!
 * The Visual Studio 2002 compiler doesn't support these symbols,
 * which our typedefs unforntuatly expand to.
 */
QString fixCppTypeName(const QString &name)
{
    if (name == "long long") return "qint64";
    else if (name == "unsigned long long") return "quint64";
    return name;
}

QString CodeSnip::formattedCode(const QString &_defaultIndent)
{
    QString returned;

    QStringList lst = code.split("\n");

    QString defaultIndent(_defaultIndent);
    QString indent = defaultIndent;
    foreach (QString s, lst) {
        if (s.trimmed().isEmpty())
            continue ;

        if (s.trimmed().endsWith("}"))
            indent.chop(4);

        returned += indent + s.trimmed();

        indent = defaultIndent;
        if (!returned.endsWith(";") && !returned.endsWith("}"))
            indent += "    ";

        if (returned.endsWith("{"))
            defaultIndent += "    ";
        else if (returned.endsWith("}"))
            defaultIndent.chop(4);

        returned += "\n";
    }

    return returned;
}

