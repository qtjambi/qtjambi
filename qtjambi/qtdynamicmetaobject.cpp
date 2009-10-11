/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "qtdynamicmetaobject.h"
#include "qtjambi_core.h"
#include "qtjambitypemanager_p.h"

#include <QtCore/QHash>
#include <QtCore/QVarLengthArray>
#include <QtCore/QMetaEnum>

class QtDynamicMetaObjectPrivate
{
    QtDynamicMetaObject *q_ptr;
    Q_DECLARE_PUBLIC(QtDynamicMetaObject);

public:
    QtDynamicMetaObjectPrivate(QtDynamicMetaObject *q, JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object);
    ~QtDynamicMetaObjectPrivate();

    void initialize(JNIEnv *jni_env, jclass java_class, const QMetaObject *original_meta_object);
    void invokeMethod(JNIEnv *env, jobject object, jobject method_object, void **_a, const QString &signature = QString()) const;

    int m_method_count;
    int m_signal_count;
    int m_property_count;

    jobjectArray m_methods;
    jobjectArray m_signals;

    jobjectArray m_property_readers;
    jobjectArray m_property_writers;
    jobjectArray m_property_resetters;
    jobjectArray m_property_designables;

    QString *m_original_signatures;
};

QtDynamicMetaObjectPrivate::QtDynamicMetaObjectPrivate(QtDynamicMetaObject *q, JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object)
    : q_ptr(q), m_method_count(-1), m_signal_count(0), m_property_count(0), m_methods(0), m_signals(0),
      m_property_readers(0), m_property_writers(0), m_property_resetters(0), m_property_designables(0),
      m_original_signatures(0)
{
    Q_ASSERT(env != 0);
    Q_ASSERT(java_class != 0);

    initialize(env, java_class, original_meta_object);
}

QtDynamicMetaObjectPrivate::~QtDynamicMetaObjectPrivate()
{
    JNIEnv *env = qtjambi_current_environment();
    if (env != 0) {
        if (m_methods != 0) env->DeleteGlobalRef(m_methods);
        if (m_signals != 0) env->DeleteGlobalRef(m_signals);
        if (m_property_readers != 0) env->DeleteGlobalRef(m_property_readers);
        if (m_property_writers != 0) env->DeleteGlobalRef(m_property_writers);
        if (m_property_resetters != 0) env->DeleteGlobalRef(m_property_resetters);
        if (m_property_designables != 0) env->DeleteGlobalRef(m_property_designables);
    }

    delete[] m_original_signatures;
}

void QtDynamicMetaObjectPrivate::initialize(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object)
{
    Q_Q(QtDynamicMetaObject);

    StaticCache *sc = StaticCache::instance();
    sc->resolveMetaObjectTools();

    env->PushLocalFrame(100);

    jobject meta_data_struct = env->CallStaticObjectMethod(sc->MetaObjectTools.class_ref, sc->MetaObjectTools.buildMetaData, java_class);
    qtjambi_exception_check(env);
    if (meta_data_struct == 0)
        return;

    sc->resolveMetaData();
    jintArray meta_data = (jintArray) env->GetObjectField(meta_data_struct, sc->MetaData.metaData);
    Q_ASSERT(meta_data);

    jbyteArray string_data = (jbyteArray) env->GetObjectField(meta_data_struct, sc->MetaData.stringData);
    Q_ASSERT(string_data);

    q->d.superdata = qtjambi_metaobject_for_class(env, env->GetSuperclass(java_class), original_meta_object);

    int string_data_len = env->GetArrayLength(string_data);
    q->d.stringdata = new char[string_data_len];

    int meta_data_len = env->GetArrayLength(meta_data);
    q->d.data = new uint[meta_data_len];
    q->d.extradata = 0;

    env->GetByteArrayRegion(string_data, 0, string_data_len, (jbyte *) q->d.stringdata);
    env->GetIntArrayRegion(meta_data, 0, meta_data_len, (jint *) q->d.data);

    m_methods = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.slotsArray);
    m_signals = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.signalsArray);
    m_property_readers = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyReadersArray);
    m_property_writers = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyWritersArray);
    m_property_resetters = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyResettersArray);
    m_property_designables = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.propertyDesignablesArray);
    jobjectArray extra_data = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.extraDataArray);

    if (m_methods != 0) {
        m_methods = (jobjectArray) env->NewGlobalRef(m_methods);
        m_method_count = env->GetArrayLength(m_methods);
    }

    if (m_signals != 0) {
        m_signals = (jobjectArray) env->NewGlobalRef(m_signals);
        m_signal_count = env->GetArrayLength(m_signals);
    }

    if (m_method_count + m_signal_count > 0) {
        m_original_signatures = new QString[m_method_count + m_signal_count];
        jobjectArray original_signatures = (jobjectArray) env->GetObjectField(meta_data_struct, sc->MetaData.originalSignatures);
        for (int i=0; i<m_method_count + m_signal_count; ++i)
            m_original_signatures[i] = qtjambi_to_qstring(env, (jstring) env->GetObjectArrayElement(original_signatures, i));
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

    if (m_property_designables != 0) {
        m_property_designables = (jobjectArray) env->NewGlobalRef(m_property_designables);
        Q_ASSERT(m_property_count == env->GetArrayLength(m_property_designables));
    }

    int extra_data_count = extra_data != 0 ? env->GetArrayLength(extra_data) : 0;
    if (extra_data_count > 0) {
        const QMetaObject **ptr = new const QMetaObject *[extra_data_count];
        q->d.extradata = ptr;
        Q_ASSERT(q->d.extradata != 0);

        for (int i=0; i<extra_data_count; ++i) {
            ptr[i] = qtjambi_metaobject_for_class(env, reinterpret_cast<jclass>(env->GetObjectArrayElement(extra_data, i)), 0);
        }
    }


    env->PopLocalFrame(0);
}

void QtDynamicMetaObjectPrivate::invokeMethod(JNIEnv *env, jobject object, jobject method_object, void **_a, const QString &_signature) const
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveMetaObjectTools();

    jobject method_signature = env->CallStaticObjectMethod(sc->MetaObjectTools.class_ref, sc->MetaObjectTools.methodSignature2, method_object, true);
    Q_ASSERT(method_signature != 0);

    // If no signature is specified, we look it up
    QString signature(_signature);
    if (signature.isEmpty())
        signature = qtjambi_to_qstring(env, reinterpret_cast<jstring>(method_signature));
    Q_ASSERT(!signature.isEmpty());

    QtJambiTypeManager manager(env, true, QtJambiTypeManager::DynamicMetaObjectMode);

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
                qWarning("QtDynamicMetaObject::invokeMethod: Unrecognized JNI type '%c'", jni_type.at(0).toLatin1());
                break;
            };
        }

        manager.convertExternalToInternal(converted_arguments.at(0), _a, type_list.at(0),
            manager.getInternalTypeName(type_list.at(0), QtJambiTypeManager::ReturnType), QtJambiTypeManager::ReturnType);

        manager.destroyConstructedExternal(converted_arguments);
    } else {
        qWarning("QtDynamicMetaObject::invokeMethod: Failed to convert arguments");
    }
}

QtDynamicMetaObject::QtDynamicMetaObject(JNIEnv *jni_env, jclass java_class, const QMetaObject *original_meta_object)
    : d_ptr(new QtDynamicMetaObjectPrivate(this, jni_env, java_class, original_meta_object)) { }

QtDynamicMetaObject::~QtDynamicMetaObject()
{
    delete d_ptr;
}


int QtDynamicMetaObject::originalSignalOrSlotSignature(JNIEnv *env, int _id, QString *signature) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();

    bool is_dynamic = qtjambi_metaobject_is_dynamic(super_class);
    if (is_dynamic) {
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->originalSignalOrSlotSignature(env, _id, signature);
    } else {
        if (_id < super_class->methodCount()) {
            QString qt_signature = QLatin1String(super_class->className()) + QLatin1String("::") + QString::fromLatin1(super_class->method(_id).signature());
            *signature = getJavaName(qt_signature.toLatin1());
        }
        _id -= super_class->methodCount();
    }
    if (_id < 0) return _id;

    if (_id < d->m_signal_count + d->m_method_count)
        *signature = d->m_original_signatures[_id];

    return _id - d->m_method_count - d->m_signal_count;
}

int QtDynamicMetaObject::invokeSignalOrSlot(JNIEnv *env, jobject object, int _id, void **_a) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->invokeSignalOrSlot(env, object, _id, _a);
    if (_id < 0) return _id;

    // Emit the correct signal
    if (_id < d->m_signal_count) {
        jobject signal_field = env->GetObjectArrayElement(d->m_signals, _id);
        Q_ASSERT(signal_field);

        jfieldID field_id = env->FromReflectedField(signal_field);
        Q_ASSERT(field_id);

        jobject signal_object = env->GetObjectField(object, field_id);
        Q_ASSERT(signal_object);

        StaticCache *sc = StaticCache::instance();
        sc->resolveQtJambiInternal();

        jobject signal_emit_method = env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.findEmitMethod, signal_object);
        qtjambi_exception_check(env);
        Q_ASSERT(signal_emit_method);

        jstring j_signal_parameters = static_cast<jstring>(env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref,
                                                                                       sc->QtJambiInternal.signalParameters,
                                                                                       signal_object));
        qtjambi_exception_check(env);
        Q_ASSERT(j_signal_parameters);

        // Because of type erasure, we need to find the compile time signature of the emit method
        QString signal_parameters = "void emit(" + qtjambi_to_qstring(env, j_signal_parameters) + ")";
        d->invokeMethod(env, signal_object, signal_emit_method, _a, signal_parameters);
    } else if (_id < d->m_signal_count + d->m_method_count) { // Call the correct method
        jobject method_object = env->GetObjectArrayElement(d->m_methods, _id - d->m_signal_count);
        Q_ASSERT(method_object != 0);

        d->invokeMethod(env, object, method_object, _a);
    }

    return _id - d->m_method_count - d->m_signal_count;
}

int QtDynamicMetaObject::readProperty(JNIEnv *env, jobject object, int _id, void **_a) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->readProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    if (_id < d->m_property_count) {
        jobject method_object = env->GetObjectArrayElement(d->m_property_readers, _id);
        Q_ASSERT(method_object != 0);

        d->invokeMethod(env, object, method_object, _a);
    }

    return _id - d->m_property_count;
}

int QtDynamicMetaObject::writeProperty(JNIEnv *env, jobject object, int _id, void **_a) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->writeProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    if (_id < d->m_property_count) {
        jobject method_object = env->GetObjectArrayElement(d->m_property_writers, _id);
        if (method_object != 0) {
            // invokeMethod expects a place holder for return value, but write property meta calls
            // do not since all property writers return void by convention.
            void *a[2] = { 0, _a[0] };
            d->invokeMethod(env, object, method_object, a);
        }
    }

    return _id - d->m_property_count;
}

int QtDynamicMetaObject::resetProperty(JNIEnv *env, jobject object, int _id, void **_a) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->resetProperty(env, object, _id, _a);
    if (_id < 0) return _id;

    if (_id < d->m_property_count) {
        jobject method_object = env->GetObjectArrayElement(d->m_property_resetters, _id);
        if (method_object != 0)
            d->invokeMethod(env, object, method_object, _a);
    }

    return _id - d->m_property_count;
}

int QtDynamicMetaObject::queryPropertyDesignable(JNIEnv *env, jobject object, int _id, void **_a) const
{
    Q_D(const QtDynamicMetaObject);

    const QMetaObject *super_class = superClass();
    if (qtjambi_metaobject_is_dynamic(super_class))
        _id = static_cast<const QtDynamicMetaObject *>(super_class)->queryPropertyDesignable(env, object, _id, _a);
    if (_id < 0) return _id;

    if (_id < d->m_property_count) {
        jobject method_object = env->GetObjectArrayElement(d->m_property_designables, _id);
        if (method_object != 0)
            d->invokeMethod(env, object, method_object, _a);
    }

    return _id - d->m_property_count;
}
