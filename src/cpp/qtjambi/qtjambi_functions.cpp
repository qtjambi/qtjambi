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

#include "qtjambi_core.h"
#include "qtjambivariant_p.h"
#include "qtjambitypemanager_p.h"

#include <QtCore/QCoreApplication>
#include <QtCore/QVarLengthArray>
#include <QtCore/QPointer>
#include <QtCore/QStringList>
#include <QtCore/QMetaObject>
#include <QtCore/QMetaProperty>
#include <QtCore/QAbstractFileEngine>
#include <QtCore/QAbstractFileEngineHandler>

#ifdef QTJAMBI_SANITY_CHECK
#include <QtCore/QObject>
#include <QtCore/private/qobject_p.h>
#endif

#ifdef Q_OS_DARWIN
#include <pthread.h>
#endif

static QtMsgHandler qt_message_handler;
static bool qt_message_handler_installed;
static void qtjambi_messagehandler_proxy(QtMsgType type, const char *message);


class QThreadData;

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryInitializer_initialize)
(JNIEnv *, jclass)
{
    qtjambi_register_callbacks();

    if (QCoreApplication::instance())
        QtJambiVariant::registerHandler();
}


extern "C" Q_DECL_EXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QThreadManager_releaseNativeResources)
(JNIEnv *env, jclass)
{
    return qtjambi_release_threads(env);
}


extern "C" Q_DECL_EXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_setQObjectSender)
(JNIEnv *, jclass, jlong r, jlong s)
{
    QObject *the_receiver = reinterpret_cast<QObject *>(qtjambi_from_jlong(r));
    QObject *the_sender = reinterpret_cast<QObject *>(qtjambi_from_jlong(s));
    if (the_receiver == 0)
        return 0;

    int id = -1;
    void *args[] = {
        the_receiver,
        the_sender,
        &id,            // the signal id, unknown right now...
        0,              // return value for old sender...
        0               // return value new sender
    };

    if (!QInternal::callFunction(QInternal::SetQObjectSender, args)) {
        qWarning("QtJambiInternal::setQObjectSender: internal function call failed...");
    }

    void **keep = new void*[2];
    keep[0] = args[3];
    keep[1] = args[4];

    return (jlong) keep;
}


extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_resetQObjectSender)
(JNIEnv *, jclass, jlong r, jlong keep)
{
    QObject *receiver = reinterpret_cast<QObject *>(qtjambi_from_jlong(r));
    void **senders = (void **) keep;

    void *args[] = {
        receiver,
        senders[0],
        senders[1]
    };

    if (!QInternal::callFunction(QInternal::ResetQObjectSender, args))
        qWarning("QtJambiInternal::resetQObjectSender: internal function call failed...");

    delete [] senders;
}


extern "C" Q_DECL_EXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_sender)
(JNIEnv *env, jclass, jobject obj)
{
    QObject *qobject = qtjambi_to_qobject(env, obj);

    void *args[] = {
        qobject,
        0
    };

    if (!QInternal::callFunction(QInternal::GetQObjectSender, args)) {
        qWarning("QtJambiInternal::sender: internal function call failed...");
        return 0;
    }

    return qtjambi_from_qobject(env, (QObject *) args[1], "QObject", "com.trolltech.qt.core");
}


extern "C" Q_DECL_EXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_createExtendedEnum)
(JNIEnv *env, jclass, jint value, jint ordinal, jclass enumClass, jstring name)
{
    jmethodID methodId = env->GetMethodID(enumClass, "<init>", "(Ljava/lang/String;II)V");
    jobject object = env->NewObject(enumClass, methodId, name, ordinal, value);
    return object;
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_setField)
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

extern "C" Q_DECL_EXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_fetchSignal)
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
extern "C" Q_DECL_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_cppDisconnect)
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

extern "C" Q_DECL_EXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_resolveSlot)
(JNIEnv *env,
 jclass,
 jobject method)
{
    Q_ASSERT(method);
    return reinterpret_cast<jlong>(env->FromReflectedMethod(method));
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_invokeSlot)
(JNIEnv *env,
 jclass,
 jobject receiver,
 jlong m,
 jbyte returnType,
 jobjectArray args,
 jintArray cnvTypes)
{
    Q_ASSERT(receiver != 0);
    Q_ASSERT(m != 0);

    jmethodID methodId = reinterpret_cast<jmethodID>(m);
    qtjambi_invoke_method(env, receiver, methodId, returnType, qtjambi_from_jobjectArray(env, args, cnvTypes));
}

extern "C" Q_DECL_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_setFieldNative)
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

extern "C" Q_DECL_EXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_fetchFieldNative)
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

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_MetaObjectTools_emitNativeSignal)
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
        if (mox < 0)
            return;

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

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryShutdown_shutdown_1mark)
(JNIEnv *,
 jobject)
{
    qtjambi_vm_shutdown_set(1); // Mark the global state
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryShutdown_unregister_1helper)
(JNIEnv *env,
 jobject)
{
    qtjambi_unregister_callbacks();
    qtjambi_cache_prune(env);
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtJambi_1LibraryShutdown_shutdown_1helper)
(JNIEnv *,
 jobject)
{
    qtjambi_shutdown();	// NULL the JavaVM handle
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QMessageHandler_installMessageHandlerProxy)
(JNIEnv *, jclass)
{
    if (!qt_message_handler_installed) {
        // returns old handler, which we save
        qt_message_handler = qInstallMsgHandler(qtjambi_messagehandler_proxy);
        qt_message_handler_installed = true;
    }
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QMessageHandler_removeMessageHandlerProxy)
(JNIEnv *, jclass)
{
    if (qt_message_handler_installed) {
        // restore original handler
        qInstallMsgHandler(qt_message_handler);
        qt_message_handler = 0;
        qt_message_handler_installed = false;
    }
}

extern "C" Q_DECL_EXPORT jstring JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_MetaObjectTools_internalTypeName)
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

extern "C" Q_DECL_EXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiInternal_properties)
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

    StaticCache *sc = StaticCache::instance();
    sc->resolveQtProperty();

    for (int i=0; i<count; ++i) {
        QMetaProperty property = metaObject->property(i);

        jobject javaProperty = env->NewObject(sc->QtProperty.class_ref, sc->QtProperty.constructor,
                                              property.isWritable(), property.isDesignable(_this), property.isResettable(),
                                              property.isUser(), qtjambi_from_qstring(env, property.name()));
        Q_ASSERT(javaProperty != 0);

        qtjambi_collection_add(env, propertyList, javaProperty);
    }

    return propertyList;
}

class QClassPathFileEngineHandler: public QAbstractFileEngineHandler
{
public:
    QAbstractFileEngine *create(const QString &fileName) const
    {
        QAbstractFileEngine *rv;
        if (fileName.startsWith("classpath:"))
            rv = newClassPathFileEngine(fileName);
        else
            rv = 0;
        return rv;
    }

private:
    QAbstractFileEngine *newClassPathFileEngine(const QString &fileName) const
    {
        JNIEnv *env = qtjambi_current_environment();
        env->PushLocalFrame(100);

        StaticCache *sc = StaticCache::instance();
        sc->resolveQClassPathEngine();

        jstring javaFileName = qtjambi_from_qstring(env, fileName);
        jobject javaFileEngine = env->NewObject(sc->QClassPathEngine.class_ref, sc->QClassPathEngine.constructor, javaFileName);
        QTJAMBI_EXCEPTION_CHECK(env);
        QAbstractFileEngine *fileEngine = reinterpret_cast<QAbstractFileEngine *>(qtjambi_to_object(env, javaFileEngine));
        QtJambiLink *link = 0;
        if (javaFileEngine != 0) {
            link = QtJambiLink::findLink(env, javaFileEngine);
            Q_ASSERT(link != 0);

            link->setCppOwnership(env, javaFileEngine);
        }
        env->PopLocalFrame(0);

        return fileEngine;
    }
};

static QClassPathFileEngineHandler *qtjambiQClassPathFileEngineHandler;

/* This is synchronized with initialize() by the caller in Java linkage */
extern "C" QTJAMBI_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_fileengine_QClassPathFileEngineHandler_uninitialize)
(JNIEnv *,
 jclass)
{
    if(qtjambiQClassPathFileEngineHandler) {
        delete qtjambiQClassPathFileEngineHandler;
        qtjambiQClassPathFileEngineHandler = 0;
        return true;
    }
    return false;
}

/* This is synchronized with uninitialize() by the caller in Java linkage */
extern "C" QTJAMBI_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_fileengine_QClassPathFileEngineHandler_initialize)
(JNIEnv *,
 jclass)
{
    if(qtjambiQClassPathFileEngineHandler == 0) {
        qtjambiQClassPathFileEngineHandler = new QClassPathFileEngineHandler;
        return true;
    }
    return false;
}

extern "C" Q_DECL_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiDebugTools_hasDebugTools)
(JNIEnv *,
 jclass)
{
#if defined(QTJAMBI_DEBUG_TOOLS)
    return true;
#else
    return false;
#endif
}

extern "C" Q_DECL_EXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_getObjectCacheMode)
(JNIEnv *,
 jclass)
{
    return qtjambi_object_cache_mode_get();
}

extern "C" Q_DECL_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_setObjectCacheMode)
(JNIEnv *,
 jclass,
 jint object_cache_mode)
{
    qtjambi_object_cache_mode_set(object_cache_mode);
}

extern "C" Q_DECL_EXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_objectCacheOperation)
(JNIEnv *,
 jclass,
 jint op)
{
    if(op == 1)
        return qtjambi_object_cache_operation_flush();
    else if(op == 2)
        return qtjambi_object_cache_operation_count();
    return -1;
}

#if defined(QTJAMBI_DEBUG_TOOLS)
static qint64 qtjambi_debug_marker_id;
Q_GLOBAL_STATIC(QMutex, gStaticQtJambiRuntimeDebugMarker);
Q_GLOBAL_STATIC(QList<qint64>, gStaticQtJambiRuntimeDebugMarkerList);

static jboolean qtjambi_debug_marker_push(qint64 id) {
    gStaticQtJambiRuntimeDebugMarker()->lock();
    gStaticQtJambiRuntimeDebugMarkerList()->append((qint64)id);
    qtjambi_debug_marker_id = id;
    gStaticQtJambiRuntimeDebugMarker()->unlock();
    return true;
}
static qint64 qtjambi_debug_marker_pop() {
    qint64 rv;
    gStaticQtJambiRuntimeDebugMarker()->lock();
    if(gStaticQtJambiRuntimeDebugMarkerList()->isEmpty() == false) {
        gStaticQtJambiRuntimeDebugMarkerList()->removeLast();
        if(gStaticQtJambiRuntimeDebugMarkerList()->isEmpty() == false)
            qtjambi_debug_marker_id = gStaticQtJambiRuntimeDebugMarkerList()->last();
        else
            qtjambi_debug_marker_id = 0;
    }
    rv = qtjambi_debug_marker_id;
    gStaticQtJambiRuntimeDebugMarker()->unlock();
    return rv;
}
static qint64 qtjambi_debug_marker_get() {
    qint64 rv;
    gStaticQtJambiRuntimeDebugMarker()->lock();
    rv = qtjambi_debug_marker_id;
    gStaticQtJambiRuntimeDebugMarker()->unlock();
    return rv;
}
#endif

extern "C" Q_DECL_EXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_pushDebugMarker)
(JNIEnv *,
 jclass,
 jlong id)
 {
#if defined(QTJAMBI_DEBUG_TOOLS)
    return qtjambi_debug_marker_push((qint64)id);
#else
    Q_UNUSED(id);
    return JNI_FALSE;
#endif
}

extern "C" Q_DECL_EXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_popDebugMarker)
(JNIEnv *,
 jclass)
 {
#if defined(QTJAMBI_DEBUG_TOOLS)
    return (jlong)qtjambi_debug_marker_pop();
#else
    return -1;
#endif
}

extern "C" Q_DECL_EXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiRuntime_getDebugMarker)
(JNIEnv *,
 jclass)
 {
#if defined(QTJAMBI_DEBUG_TOOLS)
    return (jlong)qtjambi_debug_marker_get();
#else
    return -1;
#endif
}

static jboolean qtjambi_messagehandler_proxy_cached(JNIEnv *env, jclass cls, jmethodID mid, QtMsgType type, const char *message)
{
    jstring str = qtjambi_from_qstring(env, QString::fromLocal8Bit(message));

    jboolean eaten = env->CallStaticBooleanMethod(cls, mid, (jint) type, str);
    qtjambi_exception_check(env);

    if (eaten == JNI_FALSE && qt_message_handler)
        qt_message_handler(type, message);

    return eaten;
}

// FIXME: Cache the cls/mid, try to look them up when we install the handler, if found use
//  an implementation of this method that uses cached values.  If not use this version.
void qtjambi_messagehandler_proxy(QtMsgType type, const char *message)
{
    JNIEnv *env = qtjambi_current_environment();
    if(env) {
        QTJAMBI_EXCEPTION_CHECK_CLEAR(env);	// Can't have pending before FindClass()
        // FIXME: Cache ?  Ensure we clear exceptions at least ?
        jclass cls = env->FindClass("com/trolltech/qt/core/QMessageHandler");
        QTJAMBI_EXCEPTION_CHECK(env);
        if(cls) {
            jmethodID mid = env->GetStaticMethodID(cls, "process", "(ILjava/lang/String;)Z");
            QTJAMBI_EXCEPTION_CHECK(env);

            if(mid) {
                qtjambi_messagehandler_proxy_cached(env, cls, mid, type, message);
                env->DeleteLocalRef(cls);
                return;       // need to return here otherwise qt_message_handler is called twice
            }
            env->DeleteLocalRef(cls);
        }
    }

    if (qt_message_handler)
        qt_message_handler(type, message);
}


extern "C" QTJAMBI_EXPORT void JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_HelperFunctions_setAsMainThread)
    (JNIEnv *, jclass)
{
#ifdef Q_OS_DARWIN
    if (!pthread_main_np()) {
        qWarning("\n\n\nWARNING!!\n\n\n"
                 "Qt Jambi does not appear to be running on the main thread and will "
                 "most likely be unstable and crash. "
                 "Please make sure to launch your 'java' command with the "
                 "'-XstartOnFirstThread' command line option. For instance:\n\n"
                 "> java -XstartOnFirstThread com.trolltech.examples.AnalogClock\n\n");
    }
#endif

    QInternal::callFunction(QInternal::SetCurrentThreadToMainThread, 0);
}


