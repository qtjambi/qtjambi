#include "jnilayer.h"

#include "qtjambi_core.h"


jclass class_PropertySheet;
jclass class_NamedIntSet;

jmethodID method_createPropertySheet;
jmethodID method_count;
jmethodID method_hasReset;
jmethodID method_indexOf;
jmethodID method_isAttribute;
jmethodID method_isChanged;
jmethodID method_isVisible;
jmethodID method_property;
jmethodID method_propertyGroup;
jmethodID method_propertyName;
jmethodID method_reset;
jmethodID method_setAttribute;
jmethodID method_setChanged;
jmethodID method_setProperty;
jmethodID method_setPropertyGroup;
jmethodID method_setVisible;

jfieldID field_value;
jfieldID field_names;
jfieldID field_isEnum;

struct ClassData {
    jclass *cl;
    const char *name;
};

struct MethodData {
    jclass *cl;
    jmethodID *id;
    const char *name;
    const char *signature;
};

struct FieldData {
    jclass *cl;
    jfieldID *id;
    const char *name;
    const char *signature;
};

static ClassData classes[] = {
    { &class_PropertySheet, "com/trolltech/tools/designer/PropertySheet" },
    { &class_NamedIntSet, "com/trolltech/tools/designer/NamedIntSet" },
    { 0, 0 }
};

static MethodData member_functions[] = {
    { &class_PropertySheet, &method_count, "count", "()I" },
    { &class_PropertySheet, &method_hasReset, "hasReset", "(I)Z" },
    { &class_PropertySheet, &method_indexOf, "indexOf", "(Ljava/lang/String;)I" },
    { &class_PropertySheet, &method_isAttribute, "isAttribute", "(I)Z" },
    { &class_PropertySheet, &method_isChanged, "isChanged", "(I)Z" },
    { &class_PropertySheet, &method_isVisible, "isVisible", "(I)Z" },
    { &class_PropertySheet, &method_property, "property", "(I)Ljava/lang/Object;" },
    { &class_PropertySheet, &method_propertyGroup, "propertyGroup", "(I)Ljava/lang/String;" },
    { &class_PropertySheet, &method_propertyName, "propertyName", "(I)Ljava/lang/String;" },
    { &class_PropertySheet, &method_reset, "reset", "(I)Z" },
    { &class_PropertySheet, &method_setAttribute, "setAttribute", "(IZ)V" },
    { &class_PropertySheet, &method_setChanged, "setChanged", "(IZ)V" },
    { &class_PropertySheet, &method_setProperty, "setProperty", "(ILjava/lang/Object;)V" },
    { &class_PropertySheet, &method_setPropertyGroup, "setPropertyGroup", "(ILjava/lang/String;)V" },
    { &class_PropertySheet, &method_setVisible, "setVisible", "(IZ)V" },
    { 0, 0, 0, 0 }
};

static MethodData static_functions[] = {
    { &class_PropertySheet,
      &method_createPropertySheet,
      "createPropertySheet",
      "(Lcom/trolltech/qt/core/QObject;)Lcom/trolltech/tools/designer/PropertySheet;"
    },
    { 0, 0, 0, 0 }
};

static FieldData member_fields[] = {
    { &class_NamedIntSet, &field_value,  "value",  "I" },
    { &class_NamedIntSet, &field_names,  "names",  "Ljava/util/Map;" },
    { &class_NamedIntSet, &field_isEnum, "isEnum", "Z" },
    { 0, 0, 0, 0 }
};


void resolve(JNIEnv *env) {
    if (class_PropertySheet)
        return;

    // Resolve Classes...
    for (int i=0; classes[i].cl; ++i) {
        *classes[i].cl = (jclass) env->NewGlobalRef(env->FindClass(classes[i].name));
        Q_ASSERT_X(*classes[i].cl, "Failed to resolve class", classes[i].name);
    }


    // Resolve member functions
    for (int i=0; member_functions[i].cl; ++i) {
        *member_functions[i].id = env->GetMethodID(*member_functions[i].cl,
                                                   member_functions[i].name,
                                                   member_functions[i].signature);
        Q_ASSERT_X(*member_functions[i].id,
                   member_functions[i].name,
                   member_functions[i].signature);
    }


    // Resolve static functions
    for (int i=0; static_functions[i].cl; ++i) {
        *static_functions[i].id = env->GetStaticMethodID(*static_functions[i].cl,
                                                         static_functions[i].name,
                                                         static_functions[i].signature);
        Q_ASSERT_X(*static_functions[i].id,
                   static_functions[i].name,
                   static_functions[i].signature);
    }


    // Resovle fields
    for (int i=0; member_fields[i].cl; ++i) {
        *member_fields[i].id = env->GetFieldID(*member_fields[i].cl,
                                               member_fields[i].name,
                                               member_fields[i].signature);
        Q_ASSERT_X(*member_fields[i].id,
                   member_fields[i].name,
                   member_fields[i].signature);
    }
}

QMap<QString, QVariant> jmap_to_qmap(JNIEnv *env, jobject jmap) {
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveMap();
    jobjectArray entrySet = qtjambi_map_entryset_array(env, jmap);
    int size = env->CallIntMethod(jmap, sc->Map.size);
    QMap<QString, QVariant> qmap;
    for (int i=0; i<size; ++i) {
        QPair<jobject, jobject> entry = qtjambi_entryset_array_get(env, entrySet, i);
        QString name = qtjambi_to_qstring(env, (jstring) entry.first);
        int value = env->CallIntMethod(entry.second, sc->Integer.intValue);
        qmap.insert(name, value);
    }
    return qmap;
}

