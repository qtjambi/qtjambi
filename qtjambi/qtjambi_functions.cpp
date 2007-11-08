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

#include "qtjambi_core.h"
#include "qtjambivariant_p.h"
#include "qtjambitypemanager_p.h"

#include <QtCore/QCoreApplication>
#include <QtCore/QVarLengthArray>
#include <QtCore/QPointer>
#include <QtCore/QStringList>
#include <QtCore/QMetaObject>
#include <QtCore/QMetaProperty>

#ifdef QTJAMBI_SANITY_CHECK
#include <QtCore/QObject>
#include <QtCore/private/qobject_p.h>
#endif

static QtMsgHandler qt_message_handler;
static bool qt_message_handler_installed;
static void qtjambi_messagehandler_proxy(QtMsgType type, const char *message);


class QThreadData;

class QObjectPrivateAccessor : public QObjectData
{
public:
    virtual ~QObjectPrivateAccessor() { }
    QList<QObject *> unused;
    QThreadData *thread;
    QObject *currentSender;

    int currentSenderSignalIdStart;
    int currentSenderSignalIdEnd;
    QList< QPointer<QObject> > eventFilters;
    struct ExtraData
    {
#ifndef QT_NO_USERDATA
        QVector<QObjectUserData *> userData;
#endif
        QList<QByteArray> propertyNames;
        QList<QVariant> propertyValues;
    };
    ExtraData *extraData;
    mutable int connectedSignals;
    QString objectName;

};

class QObjectAccessor {
public:
    virtual ~QObjectAccessor() { }
    QObjectPrivateAccessor *d_ptr;
};

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryInitializer_initialize(JNIEnv *, jclass))
{
    QInternal::callFunction(QInternal::SetCurrentThreadToMainThread, 0);
    qtjambi_register_callbacks();

    if (QCoreApplication::instance())
        QtJambiVariant::registerHandler();
}


extern "C" JNIEXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QThreadManager_releaseNativeResources(JNIEnv *env, jclass))
{
    return qtjambi_release_threads(env);
}


extern "C" JNIEXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_nativeSwapQObjectSender)
(JNIEnv *env, jclass, jlong r, jlong s, jboolean return_previous_sender)
{
    Q_UNUSED(env)
    QObject *the_receiver = reinterpret_cast<QObject *>(qtjambi_from_jlong(r));
    QObject *the_sender = reinterpret_cast<QObject *>(qtjambi_from_jlong(s));
    if (the_receiver == 0)
        return 0;

    QObjectPrivateAccessor *d = (reinterpret_cast<QObjectAccessor *>(the_receiver))->d_ptr;
    if (d == 0)
        return 0;

#ifdef QTJAMBI_SANITY_CHECK
    Q_ASSERT(sizeof(QObjectPrivateAccessor) == sizeof(QObjectPrivate));
    Q_ASSERT(d->currentSender == ((QObjectPrivate *) d)->currentSender);
#endif

    QObject *prev = d->currentSender;
    d->currentSender = the_sender;

    return return_previous_sender ? long(prev) : 0L;
}


extern "C" JNIEXPORT jobject JNICALL

QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_sender(JNIEnv *env, jclass, jobject obj))
{
    QObject *qobject = qtjambi_to_qobject(env, obj);
    QObjectPrivateAccessor *d = (reinterpret_cast<QObjectAccessor *>(qobject))->d_ptr;

#ifdef QTJAMBI_SANITY_CHECK
    Q_ASSERT(sizeof(QObjectPrivateAccessor) == sizeof(QObjectPrivate));
    Q_ASSERT(d->currentSender == ((QObjectPrivate *) d)->currentSender);
#endif

    return qtjambi_from_qobject(env, d->currentSender, "QObject", "com.trolltech.qt.core");
}


extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_createExtendedEnum(JNIEnv *env, jclass, jint value, jint ordinal, jclass enumClass, jstring name))
{
    jmethodID methodId = env->GetMethodID(enumClass, "<init>", "(Ljava/lang/String;II)V");
    jobject object = env->NewObject(enumClass, methodId, name, ordinal, value);
    return object;
}

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_setField)
(JNIEnv *env,
 jclass,
 jobject _this,
 jobject field,
 jobject newValue)
{
    jfieldID fieldId = env->FromReflectedField(field);
    Q_ASSERT(fieldId != 0);

    env->SetObjectField(_this, fieldId, newValue);
}

extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_fetchSignal)
(JNIEnv *env,
 jclass,
 jobject java_object,
 jobject field)
{
    jfieldID fieldId = env->FromReflectedField(field);
    if (fieldId == 0)
        return 0;

    jobject signal = env->GetObjectField(java_object, fieldId);
    return signal;
}

#include <QDebug>
extern "C" JNIEXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_cppDisconnect)
(JNIEnv *env,
 jclass,
 jobject java_sender,
 jstring java_signal_name,
 jobject java_receiver,
 jstring java_slot_signature)
{
    Q_ASSERT(java_signal_name);
    Q_ASSERT(java_sender);

    QObject *sender = qtjambi_to_qobject(env, java_sender);
    if (sender == 0) // Sender object deleted or about to be deleted
        return false;

    QObject *receiver = qtjambi_to_qobject(env, java_receiver);
    QByteArray signal_name = getQtName(qtjambi_to_qstring(env, java_signal_name)).toLatin1();
    if (signal_name.isEmpty())
        return false;
    int paren_pos = signal_name.indexOf('(');
    signal_name = QByteArray::number(QSIGNAL_CODE)
                  + signal_name.mid(signal_name.lastIndexOf("::", paren_pos) + 2);
    QByteArray ba_slot_signature;
    const char *slot_signature = 0;
    if (java_slot_signature != 0) {
        ba_slot_signature = getQtName(qtjambi_to_qstring(env, java_slot_signature)).toLatin1();
        if (ba_slot_signature.isEmpty())
            return false;
        paren_pos = ba_slot_signature.indexOf('(');
        ba_slot_signature = QByteArray::number(QSLOT_CODE)
                            + ba_slot_signature.mid(ba_slot_signature.lastIndexOf("::", paren_pos) + 2);
        slot_signature = ba_slot_signature.constData();
    }
    return QObject::disconnect(sender, signal_name.constData(), receiver, slot_signature);
}

extern "C" JNIEXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_resolveSlot)
(JNIEnv *env,
 jclass,
 jobject method)
{
    Q_ASSERT(method);
    return reinterpret_cast<jlong>(env->FromReflectedMethod(method));
}

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_invokeSlot)
(JNIEnv *env,
 jclass,
 jobject receiver,
 jlong m,
 jbyte returnType,
 jobjectArray args,
 jintArray _cnvTypes)
{
    Q_ASSERT(receiver != 0);
    Q_ASSERT(m != 0);

    int len = env->GetArrayLength(_cnvTypes);
    jint *cnvTypes = env->GetIntArrayElements(_cnvTypes, 0);
    QVarLengthArray<jvalue> argsArray(len);
    for (int i=0; i<len; ++i) {
        jobject arg_object = env->GetObjectArrayElement(args, i);
        switch (cnvTypes[i]) {
        case 'L': argsArray[i].l = arg_object; break ;
        case 'Z': argsArray[i].z = qtjambi_to_boolean(env, arg_object); break ;
        case 'J': argsArray[i].j = qtjambi_to_long(env, arg_object); break ;
        case 'I': argsArray[i].i = qtjambi_to_int(env, arg_object); break ;
        case 'F': argsArray[i].f = qtjambi_to_float(env, arg_object); break ;
        case 'D': argsArray[i].d = qtjambi_to_double(env, arg_object); break ;
        case 'S': argsArray[i].s = qtjambi_to_short(env, arg_object); break ;
        case 'B': argsArray[i].b = qtjambi_to_byte(env, arg_object); break ;
        case 'C': argsArray[i].c = qtjambi_to_jchar(env, arg_object); break ;
        default:
            Q_ASSERT_X(false, "invokeSlot", "Error in conversion array");
        }
    }
    env->ReleaseIntArrayElements(_cnvTypes, cnvTypes, JNI_ABORT);

    jmethodID methodId = reinterpret_cast<jmethodID>(m);
    switch (returnType)
    {
    case 'L': env->CallObjectMethodA(receiver, methodId, argsArray.data()); break ;
    case 'V': env->CallVoidMethodA(receiver, methodId, argsArray.data()); break ;
    case 'I': env->CallIntMethodA(receiver, methodId, argsArray.data()); break ;
    case 'J': env->CallLongMethodA(receiver, methodId, argsArray.data()); break ;
    case 'S': env->CallShortMethodA(receiver, methodId, argsArray.data()); break ;
    case 'Z': env->CallBooleanMethodA(receiver, methodId, argsArray.data()); break ;
    case 'F': env->CallFloatMethodA(receiver, methodId, argsArray.data()); break ;
    case 'D': env->CallDoubleMethodA(receiver, methodId, argsArray.data()); break ;
    case 'B': env->CallByteMethodA(receiver, methodId, argsArray.data()); break ;
    case 'C': env->CallCharMethodA(receiver, methodId, argsArray.data()); break ;
    default:
        Q_ASSERT_X(false, "invokeSlot", "Invalid return type parameter");
    };
}

extern "C" JNIEXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_setFieldNative)
(JNIEnv *env,
 jclass,
 jobject owner,
 jobject field,
 jobject newValue)
{
    if (owner == 0 || field == 0) return false;

    jfieldID id = env->FromReflectedField(field);
    if (id == 0) return false;

    env->SetObjectField(owner, id, newValue);
    return true;
}

extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_fetchFieldNative)
(JNIEnv *env,
 jclass,
 jobject owner,
 jobject field)
{
    if (owner == 0 || field == 0) return 0;

    jfieldID id = env->FromReflectedField(field);
    if (id == 0) return 0;

    return env->GetObjectField(owner, id);
}

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_emitNativeSignal)
(JNIEnv *env,
 jclass,
 jobject owner,
 jobject signalSignature,
 jobject signalCppSignature,
 jobject a)
{
    QObject *o = qtjambi_to_qobject(env, owner);
    if (o != 0) {
        const QMetaObject *mo = o->metaObject();

        QString signal_cpp_signature = qtjambi_to_qstring(env, reinterpret_cast<jstring>(signalCppSignature));
        int mox = mo->indexOfSignal(signal_cpp_signature.toLatin1().constData());
        
        QtJambiTypeManager manager(env);
        QString signal_signature = qtjambi_to_qstring(env, reinterpret_cast<jstring>(signalSignature));
        QVector<QString> type_list = manager.parseSignature(signal_signature);

        jobjectArray args = reinterpret_cast<jobjectArray>(a);
        QVector<void *> input_arguments(type_list.size() - 1, 0);        
        for (int i=0;i<type_list.size()-1;++i) {
            jvalue *jv = new jvalue; 
            jv->l = env->GetObjectArrayElement(args, i);     
            input_arguments[i] = jv;
        }
        
        QVector<void *> converted_arguments = manager.initExternalToInternal(input_arguments, type_list);
        if (converted_arguments.size() > 0) {
            void **_a = converted_arguments.data();
            QMetaObject::activate(o, mox, _a);
            manager.destroyConstructedInternal(converted_arguments);
        }
    }
}

void qtjambi_shutdown();

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryShutdown_run_1helper)
(JNIEnv *,
 jobject)
{
    qtjambi_shutdown();
}

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QMessageHandler_installMessageHandlerProxy)
(JNIEnv *, jclass)
{
    if (!qt_message_handler_installed) {
        qt_message_handler = qInstallMsgHandler(qtjambi_messagehandler_proxy);
        qt_message_handler_installed = true;
    }
}

extern "C" JNIEXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QMessageHandler_removeMessageHandlerProxy)
(JNIEnv *, jclass)
{
    if (qt_message_handler_installed) {
        qInstallMsgHandler(0);
        qt_message_handler_installed = false;
    }
}

extern "C" JNIEXPORT jstring JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_internalTypeName)
(JNIEnv *env, jclass, jstring s, jint varContext)
{
    QString signature = qtjambi_to_qstring(env, s);
    
    int prefix_end = signature.indexOf("(");
    QString prefix;
    if (prefix_end >= 0) {
        prefix = signature.mid(0, prefix_end+1);
        signature = signature.mid(prefix_end+1);
    }
    
    int postfix_start = signature.lastIndexOf(")");
    QString postfix;
    if (postfix_start >= 0) {
        postfix = signature.mid(postfix_start);
        signature = signature.mid(0, postfix_start);
    }

    QtJambiTypeManager manager(env);

    QStringList allArgs = signature.split(",");
    for (int i=0; i<allArgs.size(); ++i) {
        if (!allArgs.at(i).isEmpty()) {
            allArgs[i] = manager.getInternalTypeName(QString(allArgs.at(i)).replace('.', '/'), QtJambiTypeManager::VariableContext(varContext));
            if (allArgs[i].isEmpty()) // Can't convert type name, in which case we just return emptiness
                return qtjambi_from_qstring(env, "");
        }
    }

    return qtjambi_from_qstring(env, prefix + allArgs.join(",") + postfix);
}

extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambiInternal_properties)
(JNIEnv *env,
 jclass,
 jlong nativeId)
{
    if (nativeId == 0)
        return 0;

    QObject *_this = reinterpret_cast<QObject *>(qtjambi_from_jlong(nativeId));
    Q_ASSERT(_this != 0);

    const QMetaObject *metaObject = _this->metaObject();
    Q_ASSERT(metaObject != 0);

    int count = metaObject->propertyCount();
    jobject propertyList = qtjambi_arraylist_new(env, count);
    Q_ASSERT(propertyList != 0);

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtProperty();

    for (int i=0; i<count; ++i) {
        QMetaProperty property = metaObject->property(i);

        jobject javaProperty = env->NewObject(sc->QtProperty.class_ref, sc->QtProperty.constructor, 
                                              property.isWritable(), property.isDesignable(_this), property.isResettable(),
                                              qtjambi_from_qstring(env, property.name()));
        Q_ASSERT(javaProperty != 0);

        qtjambi_collection_add(env, propertyList, javaProperty);
    }

    return propertyList;
}

void qtjambi_messagehandler_proxy(QtMsgType type, const char *message)
{
    JNIEnv *env = qtjambi_current_environment();
    jclass cls = env->FindClass("com/trolltech/qt/core/QMessageHandler");
    QTJAMBI_EXCEPTION_CHECK(env);

    jmethodID id = env->GetStaticMethodID(cls, "process", "(ILjava/lang/String;)Z");
    QTJAMBI_EXCEPTION_CHECK(env);

    jstring str = qtjambi_from_qstring(env, QString::fromLocal8Bit(message));

    jboolean eaten = env->CallStaticBooleanMethod(cls, id, (jint) type, str);
    qtjambi_exception_check(env);

    if (!eaten && qt_message_handler)
        qt_message_handler(type, message);
}


