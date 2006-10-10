/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "jambilanguageplugin.h"

#include "qtjambi_core.h"

#include "javanametable.h"

#include <QtDesigner/private/qdesigner_utils_p.h>

#include <QtDebug>
#include <QtPlugin>


#include <QtGui/QMessageBox>


static jclass class_PropertySheet;
static jclass class_NamedIntSet;

static jmethodID method_createPropertySheet;
static jmethodID method_count;
static jmethodID method_hasReset;
static jmethodID method_indexOf;
static jmethodID method_isAttribute;
static jmethodID method_isChanged;
static jmethodID method_isVisible;
static jmethodID method_property;
static jmethodID method_propertyGroup;
static jmethodID method_propertyName;
static jmethodID method_reset;
static jmethodID method_setAttribute;
static jmethodID method_setChanged;
static jmethodID method_setProperty;
static jmethodID method_setPropertyGroup;
static jmethodID method_setVisible;

static jfieldID field_value;
static jfieldID field_names;
static jfieldID field_isEnum;

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


static void resolve(JNIEnv *env) {
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

static QMap<QString, QVariant> jmap_to_qmap(JNIEnv *env, jobject jmap) {
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

using namespace qdesigner_internal;

JambiLanguagePlugin::JambiLanguagePlugin():
    m_core(0)
{
}

JambiLanguagePlugin::~JambiLanguagePlugin()
{
}

bool JambiLanguagePlugin::isInitialized() const
{
    return m_core != 0;
}

void JambiLanguagePlugin::initialize(QDesignerFormEditorInterface *core)
{
    if (m_core)
        return;

    m_core = core;

    QExtensionManager *mgr = m_core->extensionManager();
    Q_ASSERT (mgr != 0);

    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerLanguageExtension));
    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerPropertySheetExtension));

    qtjambi_initialize_vm();
}

QAction *JambiLanguagePlugin::action() const
{
    return 0;
}

QDesignerFormEditorInterface *JambiLanguagePlugin::core() const
{
    return m_core;
}

JambiLanguage::JambiLanguage(QObject *parent)
    : QObject(parent)
{
    m_name_table = JavaNameTable::instance();
}

JambiLanguage::~JambiLanguage()
{
}

QDialog *JambiLanguage::createFormWindowSettingsDialog(QDesignerFormWindowInterface *, QWidget *)
{
    return 0;
}

QString JambiLanguage::classNameOf(QObject *object) const
{
    QtJambiLink *link = QtJambiLink::findLinkForQObject(object);
    if (link && link->createdByJava()) {
        JNIEnv *env = qtjambi_current_environment();
        jobject jobj = link->javaObject(env);
        QString name = qtjambi_class_name(env, env->GetObjectClass(jobj));
        return name;
    }
    return object->metaObject()->className();
}

QString JambiLanguage::enumerator(const QString &name) const
{
    QString javaName = m_name_table->javaEnum(name);
    return javaName.isEmpty() ? name : javaName;
}

QString JambiLanguage::neutralEnumerator(const QString &name) const
{
    QString cppName = m_name_table->cppEnum(name);
    return cppName.isEmpty() ? name : cppName;
}


JambiPropertySheet::JambiPropertySheet(JambiLanguagePlugin *plugin, QObject *object, QObject *parent):
    QDesignerPropertySheet(object, parent),
    m_jambi(plugin)
{
    QExtensionManager *mgr = m_jambi->core()->extensionManager();
    m_language = qt_extension<QDesignerLanguageExtension*> (mgr, m_jambi->core());

    JNIEnv *env = qtjambi_current_environment();

    resolve(env);

    Q_ASSERT(qtjambi_is_created_by_java(object));
    jobject java_object = QtJambiLink::findLinkForQObject(object)->javaObject(env);
    m_property_sheet = env->CallStaticObjectMethod(class_PropertySheet, method_createPropertySheet, java_object);
    Q_ASSERT(m_property_sheet);

    m_property_sheet = env->NewGlobalRef(m_property_sheet);
}


QString JambiPropertySheet::callStringMethod_int(jmethodID mid, int i) const {
    JNIEnv *env = qtjambi_current_environment();
    jstring jstr = (jstring) env->CallObjectMethod(m_property_sheet, mid, i);
    QString qstr = qtjambi_to_qstring(env, jstr);
    env->DeleteLocalRef(jstr);
    return qstr;
}

bool JambiPropertySheet::callBoolMethod(jmethodID mid, int i) const {
    JNIEnv *env = qtjambi_current_environment();
    return env->CallBooleanMethod(m_property_sheet, mid, i);
}

void JambiPropertySheet::call_int_bool(jmethodID mid, int i, bool b) const {
    JNIEnv *env = qtjambi_current_environment();
    env->CallVoidMethod(m_property_sheet, mid, i, b);
}


JambiPropertySheet::~JambiPropertySheet()
{
    qtjambi_current_environment()->DeleteGlobalRef(m_property_sheet);
}

int JambiPropertySheet::count() const
{
    JNIEnv *env = qtjambi_current_environment();
    int i = env->CallIntMethod(m_property_sheet, method_count);
    return i;
}

bool JambiPropertySheet::hasReset(int index) const
{
    return callBoolMethod(method_hasReset, index);
}

int JambiPropertySheet::indexOf(const QString & name) const
{
    JNIEnv *env = qtjambi_current_environment();
    jstring jname = qtjambi_from_qstring(env, name);
    int index = env->CallIntMethod(m_property_sheet, method_indexOf, jname);
    env->DeleteLocalRef(jname);
    return index;
}

bool JambiPropertySheet::isAttribute(int index) const
{
    return callBoolMethod(method_isAttribute, index);
}

bool JambiPropertySheet::isChanged(int index) const
{
    return callBoolMethod(method_isChanged, index);
}

bool JambiPropertySheet::isVisible(int index) const
{
    return callBoolMethod(method_isVisible, index);
}

QVariant JambiPropertySheet::property(int index) const
{
    JNIEnv *env = qtjambi_current_environment();
    jobject jobj = env->CallObjectMethod(m_property_sheet, method_property, index);

    QTJAMBI_EXCEPTION_CHECK(env);

    if (!jobj)
        return "N/A (QtJambi)";

    QVariant variant;

    if (env->IsSameObject(env->GetObjectClass(jobj), class_NamedIntSet)) {
        if (env->GetBooleanField(jobj, field_isEnum)) {
            EnumType et;
            et.value = env->GetIntField(jobj, field_value);
            jobject jmap = env->GetObjectField(jobj, field_names);
            et.items = jmap_to_qmap(env, jmap);
            qVariantSetValue(variant, et);
        } else {
            FlagType ft;
            ft.value = env->GetIntField(jobj, field_value);
            ft.items = jmap_to_qmap(env, env->GetObjectField(jobj, field_names));
            qVariantSetValue(variant, ft);
        }
    } else {
         variant = qtjambi_to_qvariant(env, jobj);
         QTJAMBI_EXCEPTION_CHECK(env);
    }

    QTJAMBI_EXCEPTION_CHECK(env);
    env->DeleteLocalRef(jobj);
    QTJAMBI_EXCEPTION_CHECK(env);

    return variant;
}

QString JambiPropertySheet::propertyGroup(int index) const
{
    return callStringMethod_int(method_propertyGroup, index);
}

QString JambiPropertySheet::propertyName(int index) const
{
    return callStringMethod_int(method_propertyName, index);
}

bool JambiPropertySheet::reset(int index)
{
    return callBoolMethod(method_reset, index);
}

void JambiPropertySheet::setAttribute(int index, bool attribute)
{
    call_int_bool(method_setAttribute, index, attribute);
}

void JambiPropertySheet::setChanged(int index, bool changed)
{
    call_int_bool(method_setChanged, index, changed);
}

void JambiPropertySheet::setProperty(int index, const QVariant &value)
{
    JNIEnv *env = qtjambi_current_environment();
    jobject jobj = qtjambi_from_qvariant(env, value);
    env->CallVoidMethod(m_property_sheet, method_setProperty, index, jobj);
    env->DeleteLocalRef(jobj);
}

void JambiPropertySheet::setPropertyGroup(int index, const QString &group)
{
    JNIEnv *env = qtjambi_current_environment();
    jstring jstr = qtjambi_from_qstring(env, group);
    env->CallVoidMethod(m_property_sheet, method_setProperty, index, jstr);
    env->DeleteLocalRef(jstr);
}

void JambiPropertySheet::setVisible(int index, bool visible)
{
    call_int_bool(method_setVisible, index, visible);
}


JambiExtensionFactory::JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent):
    QExtensionFactory(parent),
    m_jambi(plugin)
{
    printf("JambiExtensionFactory()\n");
}


JambiExtensionFactory::~JambiExtensionFactory()
{
}


QObject *JambiExtensionFactory::createExtension(QObject *object, const QString &iid, QObject *parent) const
{

    if (iid == Q_TYPEID(QDesignerLanguageExtension) && qobject_cast<QDesignerFormEditorInterface*> (object))
        return new JambiLanguage(parent);

    else if (iid == Q_TYPEID(QDesignerPropertySheetExtension)
             && qtjambi_is_created_by_java(object)) {
        return new JambiPropertySheet(m_jambi, object, parent);
    }

    return 0;
}

Q_EXPORT_PLUGIN2(JambiLanguagePlugin, JambiLanguagePlugin)
