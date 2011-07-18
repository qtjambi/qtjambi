
#ifndef HANDLER_H_
#define HANDLER_H_

#include <qxml.h>
#include "typeentry.h"
#include "stackelement.h"

class TypeDatabase;

class Handler : public QXmlDefaultHandler {
    public:
        Handler(TypeDatabase *database, bool generate)
                : m_database(database), m_generate(generate ? TypeEntry::GenerateAll : TypeEntry::GenerateForSubclass) {
            current = 0;

            tagNames["rejection"]                   = StackElement::Rejection;
            tagNames["primitive-type"]              = StackElement::PrimitiveTypeEntry;
            tagNames["object-type"]                 = StackElement::ObjectTypeEntry;
            tagNames["value-type"]                  = StackElement::ValueTypeEntry;
            tagNames["interface-type"]              = StackElement::InterfaceTypeEntry;
            tagNames["namespace-type"]              = StackElement::NamespaceTypeEntry;
            tagNames["enum-type"]                   = StackElement::EnumTypeEntry;
            tagNames["extra-includes"]              = StackElement::ExtraIncludes;
            tagNames["include"]                     = StackElement::Include;
            tagNames["inject-code"]                 = StackElement::InjectCode;
            tagNames["modify-function"]             = StackElement::ModifyFunction;
            tagNames["modify-field"]                = StackElement::ModifyField;
            tagNames["access"]                      = StackElement::Access;
            tagNames["remove"]                      = StackElement::Removal;
            tagNames["rename"]                      = StackElement::Rename;
            tagNames["typesystem"]                  = StackElement::Root;
            tagNames["custom-constructor"]          = StackElement::CustomMetaConstructor;
            tagNames["custom-destructor"]           = StackElement::CustomMetaDestructor;
            tagNames["argument-map"]                = StackElement::ArgumentMap;
            tagNames["suppress-warning"]            = StackElement::SuppressedWarning;
            tagNames["load-typesystem"]             = StackElement::LoadTypesystem;
            tagNames["define-ownership"]            = StackElement::DefineOwnership;
            tagNames["replace-default-expression"]  = StackElement::ReplaceDefaultExpression;
            tagNames["reject-enum-value"]           = StackElement::RejectEnumValue;
            tagNames["replace-type"]                = StackElement::ReplaceType;
            tagNames["conversion-rule"]             = StackElement::ConversionRule;
            tagNames["modify-argument"]             = StackElement::ModifyArgument;
            tagNames["remove-argument"]             = StackElement::RemoveArgument;
            tagNames["remove-default-expression"]   = StackElement::RemoveDefaultExpression;
            tagNames["template"]                    = StackElement::Template;
            tagNames["insert-template"]             = StackElement::TemplateInstanceEnum;
            tagNames["replace"]                     = StackElement::Replace;
            tagNames["no-null-pointer"]             = StackElement::NoNullPointers;
            tagNames["reference-count"]             = StackElement::ReferenceCount;
        }

        bool startElement(const QString &namespaceURI, const QString &localName,
                          const QString &qName, const QXmlAttributes &atts);
        bool endElement(const QString &namespaceURI, const QString &localName, const QString &qName);

        QString errorString() const {
            return m_error;
        }
        bool error(const QXmlParseException &exception);
        bool fatalError(const QXmlParseException &exception);
        bool warning(const QXmlParseException &exception);

        bool characters(const QString &ch);

        void setImportInputDirectory(const QString &importInputDirectory) {
            m_importInputDirectory = importInputDirectory;
        }

    private:
        void fetchAttributeValues(const QString &name, const QXmlAttributes &atts,
                                  QHash<QString, QString> *acceptedAttributes);

        bool importFileElement(const QXmlAttributes &atts);
        bool convertBoolean(const QString &, const QString &, bool);

        TypeDatabase *m_database;
        StackElement* current;
        QString m_defaultPackage;
        QString m_defaultSuperclass;
        QString m_error;
        QString m_importInputDirectory;
        TypeEntry::CodeGeneration m_generate;

        /**
         * Returns attributes corresponding element type
         * StackElement::ElementType.
         */
        QHash<QString, QString> setStackElementAttributes(StackElement::ElementType type);

        CodeSnipList m_code_snips;
        FunctionModificationList m_function_mods;
        FieldModificationList m_field_mods;

        QHash<QString, StackElement::ElementType> tagNames;
};

#endif
