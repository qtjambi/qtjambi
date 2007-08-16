#include "qdynamicmetaobject.h"
#include "qtjambi_core.h"
#include "qtjambitypemanager.h"

#include <QtCore/QHash>


QDynamicMetaObject::QDynamicMetaObject(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object, jobject object) 
    : m_method_count(-1), m_signal_count(0), m_property_count(0), m_methods(0), m_signals(0), m_property_readers(0), m_property_writers(0), m_property_resetters(0)
{
    Q_ASSERT(env != 0);
    Q_ASSERT(java_class != 0);

    initialize(env, java_class, original_meta_object, object);
}

QDynamicMetaObject::~QDynamicMetaObject() 
{
    JNIEnv *env = qtjambi_current_environment();
    if (env != 0) {
        if (m_methods != 0) env->DeleteGlobalRef(m_methods);
        if (m_signals != 0) env->DeleteGlobalRef(m_signals);
    }
}

void QDynamicMetaObject::initialize(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object, jobject object)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiInternal();

    env->PushLocalFrame(100);

    jobject meta_data_struct = env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.buildMetaData, java_class, object);
    qtjambi_exception_check(env);
    Q_ASSERT(meta_data_struct);

    sc->resolveMetaData();
    jintArray meta_data = (jintArray) env->GetObjectField(meta_data_struct, sc->MetaData.metaData);
    Q_ASSERT(meta_data);

    jbyteArray string_data = (jbyteArray) env->GetObjectField(meta_data_struct, sc->MetaData.stringData);
    Q_ASSERT(string_data);

    d.superdata = qtjambi_metaobject_for_class(env, env->GetSuperclass(java_class), original_meta_object, object);

    int string_data_len = env->GetArrayLength(string_data);
    d.stringdata = new char[string_data_len];

    int meta_data_len = env->GetArrayLength(meta_data);
    d.data = new uint[meta_data_len];
    d.extradata = 0;

    env->GetByteArrayRegion(string_data, 0, string_data_len, (jbyte *) d.stringdata);
    env->GetIntArrayRegion(meta_data, 0, meta_data_len, (jint *) d.data);

    m_methods = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.slotsArray);
    m_signals = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.signalsArray);
    m_property_readers = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyReadersArray);
    m_property_writers = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyWritersArray);
    m_property_resetters = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyResettersArray);

    if (m_methods != 0) {
        m_methods = (jobjectArray) env->NewGlobalRef(m_methods);    
        m_method_count = env->GetArrayLength(m_methods);
    }

    if (m_signals != 0) {
        m_signals = (jobjectArray) env->NewGlobalRef(m_signals);    
        m_signal_count = env->GetArrayLength(m_signals);
    }

    if (m_property_readers != 0) {
        m_property_readers = (jobjectArray) env->NewGlobalRef(m_property_readers);
        m_property_count = env->GetArrayLength(m_property_readers);
    }

    if (m_property_writers != 0) {
        m_property_writers = (jobjectArray) env->NewGlobalRef(m_property_writers);
        Q_ASSERT(m_property_count == env->GetArrayLength(m_property_writers));
    }

    if (m_property_resetters != 0) {
        m_property_resetters = (jobjectArray) env->NewGlobalRef(m_property_resetters);
        Q_ASSERT(m_property_count == env->GetArrayLength(m_property_resetters));
    }

    env->PopLocalFrame(0);
}

void QDynamicMetaObject::invokeMethod(JNIEnv *env, jobject object, jobject method_object, void **_a) const
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiInternal();

    jobject method_signature = env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.methodSignature, method_object);
    Q_ASSERT(method_signature != 0);

    QString signature = qtjambi_to_qstring(env, reinterpret_cast<jstring>(method_signature));
    Q_ASSERT(!signature.isEmpty());

    QtJambiTypeManager manager(env);

    QVector<QString> type_list = manager.parseSignature(signature);
    QVector<void *> input_arguments(type_list.size() - 1, 0);
    for (int i=1; i<type_list.size(); ++i)
        input_arguments[i - 1] = _a[i];

    QVector<void *> converted_arguments = manager.initInternalToExternal(input_arguments, type_list);
    if (converted_arguments.size() > 0) {
        QVector<jvalue> jvArgs(converted_arguments.size() - 1);
        jvalue **data = reinterpret_cast<jvalue **>(converted_arguments.data());
        for (int i=1; i<converted_arguments.count(); ++i) {
            memcpy(&jvArgs[i - 1], data[i], sizeof(jvalue));
        }

        jvalue *args = jvArgs.data();
        jvalue *returned = reinterpret_cast<jvalue *>(converted_arguments[0]);

        jvalue dummy;
        if (returned == 0) {
            dummy.j = 0;
            returned = &dummy; 
        }

        jmethodID id = env->FromReflectedMethod(method_object);
        Q_ASSERT(id != 0);

        QString jni_type = QtJambiTypeManager::mangle(type_list.at(0));
        if (!jni_type.isEmpty()) {
            switch (jni_type.at(0).toLatin1()) {
            case 'V': returned->j = 0; env->CallVoidMethodA(object, id, args); break;
            case 'I': returned->i = env->CallIntMethodA(object, id, args); break;
            case 'J': returned->j = env->CallLongMethodA(object, id, args); break;
            case 'Z': returned->z = env->CallBooleanMethodA(object, id, args); break;
            case 'S': returned->s = env->CallShortMethodA(object, id, args); break;
            case 'B': returned->b = env->CallByteMethodA(object, id, args); break;
            case 'F': returned->f = env->CallFloatMethodA(object, id, args); break;
            case 'D': returned->d = env->CallDoubleMethodA(object, id, args); break;
            case 'C': returned->c = env->CallCharMethodA(object, id, args); break;
            case 'L': returned->l = env->CallObjectMethodA(object, id, args); break;
            default:
                qWarning("QDynamicMetaObject::invokeMethod: Unrecognized JNI type '%c'", jni_type.at(0).toLatin1());
                break;
            };
        }

        manager.convertExternalToInternal(converted_arguments.at(0), _a, type_list.at(0),
            manager.getInternalTypeName(type_list.at(0), QtJambiTypeManager::ReturnType), QtJambiTypeManager::ReturnType);

        manager.destroyConstructedExternal(converted_arguments);
    } else {
        qWarning("QDynamicMetaObject::invokeMethod: Failed to convert arguments");
    }
}

int QDynamicMetaObject::invokeSignalOrSlot(JNIEnv *env, jobject object, int _id, void **_a) const
{
    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QDynamicMetaObject *>(super_class)->invokeSignalOrSlot(env, object, _id, _a);
    if (_id < 0) return _id;

    // Emit the correct signal
    if (_id < m_signal_count) {
        jobject signal_field = env->GetObjectArrayElement(m_signals, _id);
        Q_ASSERT(signal_field);

        jfieldID field_id = env->FromReflectedField(signal_field);
        Q_ASSERT(field_id);

        jobject signal_object = env->GetObjectField(object, field_id);
        Q_ASSERT(signal_object);

        StaticCache *sc = StaticCache::instance(env);
        sc->resolveQtJambiInternal();

        jobject signal_emit_method = env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.findEmitMethod, signal_object);
        qtjambi_exception_check(env);
        Q_ASSERT(signal_emit_method);

        invokeMethod(env, signal_object, signal_emit_method, _a);
    } else if (_id < m_signal_count + m_method_count) { // Call the correct method
        jobject method_object = env->GetObjectArrayElement(m_methods, _id - m_signal_count);
        Q_ASSERT(method_object != 0);

        invokeMethod(env, object, method_object, _a);
    } 

    return _id - m_method_count - m_signal_count;
}

int QDynamicMetaObject::readProperty(JNIEnv *env, jobject object, int _id, void **_a) const 
{
    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QDynamicMetaObject *>(super_class)->readProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    jobject method_object = env->GetObjectArrayElement(m_property_readers, _id);
    Q_ASSERT(method_object != 0);

    invokeMethod(env, object, method_object, _a);

    return _id - m_property_count;
}

int QDynamicMetaObject::writeProperty(JNIEnv *env, jobject object, int _id, void **_a) const
{
    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QDynamicMetaObject *>(super_class)->writeProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    jobject method_object = env->GetObjectArrayElement(m_property_writers, _id);
    if (method_object != 0)
        invokeMethod(env, object, method_object, _a);

    return _id - m_property_count;
}

int QDynamicMetaObject::resetProperty(JNIEnv *env, jobject object, int _id, void **_a) const
{
    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QDynamicMetaObject *>(super_class)->resetProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    jobject method_object = env->GetObjectArrayElement(m_property_resetters, _id);
    if (method_object != 0)
        invokeMethod(env, object, method_object, _a);

    return _id - m_property_count;
}
