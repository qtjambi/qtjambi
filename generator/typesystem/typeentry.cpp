
#include "typeentry.h"
#include "typedatabase.h"

QString PrimitiveTypeEntry::javaObjectName() const {
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
    Q_ASSERT(table.contains(targetLangName()));
    return table[targetLangName()];
}

QString EnumTypeEntry::jniName() const {
    return "jint";
}

QString FlagsTypeEntry::jniName() const {
    return "jint";
}

void EnumTypeEntry::addEnumValueRedirection(const QString &rejected, const QString &usedValue) {
    m_enum_redirections << EnumValueRedirection(rejected, usedValue);
}

QString EnumTypeEntry::enumValueRedirection(const QString &value) const {
    for (int i = 0; i < m_enum_redirections.size(); ++i)
        if (m_enum_redirections.at(i).rejected == value)
            return m_enum_redirections.at(i).used;
    return QString();
}

QString FlagsTypeEntry::qualifiedTargetLangName() const {
    return javaPackage() + "." + m_enum->javaQualifier() + "." + targetLangName();
}

QString EnumTypeEntry::javaQualifier() const {
    TypeEntry *te = TypeDatabase::instance()->findType(m_qualifier);
    if (te != 0)
        return te->targetLangName();
    else
        return m_qualifier;
}

QString ContainerTypeEntry::javaPackage() const {
    if (m_type == PairContainer)
        return "com.trolltech.qt";
    return "java.util";
}

QString ContainerTypeEntry::targetLangName() const {

    switch (m_type) {
        case StringListContainer: return "List";
        case ListContainer: return "List";
        case LinkedListContainer: return "LinkedList";
        case VectorContainer: return "List";
        case StackContainer: return "Stack";
        case QueueContainer: return "Queue";
        case SetContainer: return "Set";
        case MapContainer: return "SortedMap";
        case MultiMapContainer: return "SortedMap";
        case HashContainer: return "HashMap";
            //     case MultiHashCollectio: return "MultiHash";
        case PairContainer: return "QPair";
        default:
            qWarning("bad type... %d", m_type);
            break;
    }
    return QString();
}

QString ContainerTypeEntry::qualifiedCppName() const {
    if (m_type == StringListContainer)
        return "QStringList";
    return ComplexTypeEntry::qualifiedCppName();
}

FunctionModificationList ComplexTypeEntry::functionModifications(const QString &signature) const {
    FunctionModificationList lst;
    for (int i = 0; i < m_function_mods.count(); ++i) {
        FunctionModification mod = m_function_mods.at(i);
        if (mod.signature == signature) {
            lst << mod;
        }
    }

    return lst;
}

FieldModification ComplexTypeEntry::fieldModification(const QString &name) const {
    for (int i = 0; i < m_field_mods.size(); ++i)
        if (m_field_mods.at(i).name == name)
            return m_field_mods.at(i);
    FieldModification mod;
    mod.name = name;
    mod.modifiers = FieldModification::Readable | FieldModification::Writable;
    return mod;
}
