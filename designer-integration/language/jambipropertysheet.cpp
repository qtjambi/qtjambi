#include "jambipropertysheet.h"
#include "jambilanguageplugin.h"

#include "qtjambi_core.h"

#include <QtDesigner/private/qdesigner_utils_p.h>

using namespace qdesigner_internal;


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
    qtjambi_exception_check(env);
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

