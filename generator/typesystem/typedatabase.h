
#ifndef TYPEDATABASE_H_
#define TYPEDATABASE_H_

#include <QList>
#include <qstringlist.h>

#include "typeentry.h"

class TypeDatabase {
    public:
        TypeDatabase();

        static TypeDatabase *instance();

        QList<Include> extraIncludes(const QString &className);

        ComplexTypeEntry *findComplexType(const QString &name);
        PrimitiveTypeEntry *findPrimitiveType(const QString &name);
        ObjectTypeEntry *findObjectType(const QString &name);
        NamespaceTypeEntry *findNamespaceType(const QString &name);

        ContainerTypeEntry *findContainerType(const QString &name);

        TypeEntry *findType(const QString &name) const;

        QList<TypeEntry *> findTypes(const QString &name) const {
            return m_entries.value(name);
        }

        TypeEntryHash allEntries() {
            return m_entries;
        }

        SingleTypeEntryHash entries();

        PrimitiveTypeEntry *findTargetLangPrimitiveType(const QString &java_name);

        void addRejection(const QString &class_name, const QString &function_name,
                          const QString &field_name, const QString &enum_name);
        bool isClassRejected(const QString &class_name);
        bool isFunctionRejected(const QString &class_name, const QString &function_name);
        bool isFieldRejected(const QString &class_name, const QString &field_name);
        bool isEnumRejected(const QString &class_name, const QString &enum_name);

        void addType(TypeEntry *e) {
            m_entries[e->qualifiedCppName()].append(e);
        }

        SingleTypeEntryHash flagsEntries() const {
            return m_flags_entries;
        }

        FlagsTypeEntry *findFlagsType(const QString &name) const;

        void addFlagsType(FlagsTypeEntry *fte) {
            m_flags_entries[fte->originalName()] = fte;
        }

        TemplateEntry *findTemplate(const QString &name) {
            return m_templates[name];
        }
        void addTemplate(TemplateEntry *t) {
            m_templates[t->name()] = t;
        }

        void setIncludeEclipseWarnings(bool on) {
            m_includeEclipseWarnings = on;
        }
        bool includeEclipseWarnings() const {
            return m_includeEclipseWarnings;
        }

        void setSuppressWarnings(bool on) {
            m_suppressWarnings = on;
        }
        void addSuppressedWarning(const QString &s) {
            m_suppressedWarnings.append(s);
        }

        bool isSuppressedWarning(const QString &s);

        void setRebuildClasses(const QStringList &cls) {
            m_rebuild_classes = cls;
        }

        static QString globalNamespaceClassName(const TypeEntry *te);
        QString filename() const {
            return "typesystem.txt";
        }

        bool parseFile(const QString &filename, bool generate = true);

    private:
    uint m_suppressWarnings :
        1;
    uint m_includeEclipseWarnings :
        1;
    uint m_reserved :
        30;

        TypeEntryHash m_entries;
        SingleTypeEntryHash m_flags_entries;
        TemplateEntryHash m_templates;
        QStringList m_suppressedWarnings;

        QList<TypeRejection> m_rejections;
        QStringList m_rebuild_classes;
};

#endif
