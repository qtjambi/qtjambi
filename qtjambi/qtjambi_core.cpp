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
#include "qtjambi_cache.h"
#include "qtjambifunctiontable.h"
#include "qtjambilink.h"
#include "qtjambitypemanager.h"
#include "qnativepointer.h"

#include <qglobal.h>

#include <QtCore/QDebug>
#include <QtCore/QEvent>
#include <QtCore/QThread>
#include <QtCore/QVariant>

#include <QtCore/QAbstractItemModel>

#include <QtCore/QMetaObject>
#include <QtCore/QMetaMethod>

// C-style wrapper for qInstallMsgHandler so the launcher launcher can look it up dynamically
// without bothering with knowing the name mangling
extern "C" QTJAMBI_EXPORT QtMsgHandler wrap_qInstallMsgHandler(QtMsgHandler handler)
{
    return qInstallMsgHandler(handler);
}


// accessor for protected function receiver
class Qaffeine: public QObject
{
public:
    inline int receivers(const char *signal) { return QObject::receivers(signal); }
};

// #define JOBJECT_REFCOUNT

JavaVM *qtjambi_vm = 0;

/*!
 * This function is called by the Virtual Machine when it loads the
 * library. We need this to get a hold of the global VM pointer
 */
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *)
{
    qtjambi_vm = vm;
    return JNI_VERSION_1_4;
}


typedef QHash<QThread *, jobject> ThreadTable;

Q_GLOBAL_STATIC(ThreadTable, qtjambi_thread_table);
Q_GLOBAL_STATIC(QReadWriteLock, qtjambi_thread_table_lock);

#ifdef JOBJECT_REFCOUNT
#  include <QtCore/QReadWriteLock>
#  include <QtCore/QWriteLocker>
#  include <QtCore/QDebug>
    Q_GLOBAL_STATIC(QReadWriteLock, gRefCountLock);

    static void jobjectRefCount(bool create)
    {
        QWriteLocker locker(gRefCountLock());

        static int refs = 0;
        QString s;
        if (!create) {
            s = QString("Deleting jobject reference: %1 references left").arg(--refs);
        } else {
            s = QString("Creating jobject reference: %1 references now").arg(++refs);
        }

        Q_ASSERT(refs >= 0);

        qDebug() << s;
    }

#  define REF_JOBJECT jobjectRefCount(true)
#  define DEREF_JOBJECT jobjectRefCount(false)
#else
#  define REF_JOBJECT // noop
#  define DEREF_JOBJECT // noop
#endif // JOBJECT_REFCOUNT

struct JObjectWrapper
{
    JObjectWrapper() : environment(0), object(0)
    {
        Q_ASSERT(false);
    }

    JObjectWrapper(const JObjectWrapper &wrapper)
    {
        environment = wrapper.environment;
        Q_ASSERT(environment);

        object = environment->NewGlobalRef(wrapper.object);
        Q_ASSERT(object);

        REF_JOBJECT;
    }

    JObjectWrapper(JNIEnv *env, jobject obj) : environment(env)
    {
        Q_ASSERT(environment);
        Q_ASSERT(obj);
        object = environment->NewGlobalRef(obj);

        REF_JOBJECT;
    }

    ~JObjectWrapper()
    {
        Q_ASSERT(environment);
        Q_ASSERT(object);

        DEREF_JOBJECT;

        environment->DeleteGlobalRef(object);
    }

    JNIEnv *environment;
    jobject object;
};


Q_DECLARE_METATYPE(JObjectWrapper)
static void jobject_delete(JObjectWrapper *wrapper)
{
    Q_ASSERT(wrapper);

    delete wrapper;
}

static void *jobject_create(JObjectWrapper *wrapper)
{
    if (wrapper != 0)
        return new JObjectWrapper(wrapper->environment, wrapper->object);
    else
        return 0;
}

static inline int javaObjectVariant()
{
    int type = QMetaType::type("JObjectWrapper");
    if (type == QVariant::Invalid) {
        type = QMetaType::registerType("JObjectWrapper",
                                reinterpret_cast<QMetaType::Destructor>(jobject_delete),
                                reinterpret_cast<QMetaType::Constructor>(jobject_create));
    }
    return type;
}


void qtjambi_exception_check(JNIEnv *env)
{
    if (env->ExceptionCheck()) {
        qDebug("Exception pending in native code");
        env->ExceptionDescribe();
        env->ExceptionCheck();
    }
}


/*!
 * Fetches the current environment based on the global Virtual
 * Machine. This function will fail if the current thread is not
 * attached, but that should never happen.
 */
JNIEnv *qtjambi_current_environment()
{
    JNIEnv *env;
    Q_ASSERT(qtjambi_vm);
    int result = qtjambi_vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4);
    if (result == JNI_EDETACHED) {
        qWarning("qtjambi_current_environment(): current thread is not attached\n");
        return 0;
    }
    Q_ASSERT(result == JNI_OK);
    return env;
}

QString qtjambi_class_name(JNIEnv *env, jclass java_class)
{
    Q_ASSERT(java_class);
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveClass();
    jstring name = (jstring) env->CallObjectMethod(java_class, sc->Class.getName);
    return QtJambiTypeManager::jstringToQString(env, name);
}

jobject qtjambi_from_qvariant(JNIEnv *env, const QVariant &qt_variant)
{
    int type = javaObjectVariant();

    StaticCache *sc = StaticCache::instance(env);

    switch (qt_variant.userType()) {
    case QVariant::Invalid: return 0;
    case QVariant::Int:
        sc->resolveInteger();
        return env->NewObject(sc->Integer.class_ref, sc->Integer.constructor, qt_variant.toInt());
    case QVariant::Double:
        sc->resolveDouble();
        return env->NewObject(sc->Double.class_ref, sc->Double.constructor, qt_variant.toDouble());
    case QVariant::String:
        sc->resolveString();
        return qtjambi_from_qstring(env, qt_variant.toString());
    case QVariant::LongLong:
    case QVariant::ULongLong:
        sc->resolveLong();
        return env->NewObject(sc->Long.class_ref, sc->Long.constructor, qt_variant.toLongLong());
    }

    // generic java object
    if (qt_variant.userType() == type) {
        JObjectWrapper wrapper = qVariantValue<JObjectWrapper>(qt_variant);

        if (wrapper.object) {
            jclass cls = env->GetObjectClass(wrapper.object);
            QString classname = qtjambi_class_name(env, cls);
        }

        return env->NewLocalRef(wrapper.object);
    } else {
        QString qtType = QLatin1String(qt_variant.typeName());

        QtJambiTypeManager manager(env);
        QString javaType = manager.getExternalTypeName(qtType, QtJambiTypeManager::ArgumentType);
        void *copy = 0;
        bool ok = manager.convertInternalToExternal(qt_variant.constData(), &copy,
            qtType, javaType, QtJambiTypeManager::ReturnType);

        jobject java_object = 0;
        if (ok) {
            java_object = (reinterpret_cast<jvalue *>(copy))->l;
            manager.destroyExternal(copy, QtJambiTypeManager::ReturnType);
        }

        return java_object;
    }
}

QVariant qtjambi_to_qvariant(JNIEnv *env, jobject java_object)
{
    if (java_object == 0)
        return QVariant();

    jclass object_class = env->GetObjectClass(java_object);
    if (object_class == 0)
        return QVariant();

    // Test some quick ones first...
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveString();
    sc->resolveInteger();
    sc->resolveDouble();
    sc->resolveLong();
    if (env->IsSameObject(sc->String.class_ref, object_class)) {
        return qtjambi_to_qstring(env, static_cast<jstring>(java_object));
    } else if (env->IsSameObject(sc->Integer.class_ref, object_class)) {
        return (qint32) env->CallIntMethod(java_object, sc->Integer.intValue);
    } else if (env->IsSameObject(sc->Double.class_ref, object_class)) {
        return env->CallDoubleMethod(java_object, sc->Double.doubleValue);
    } else if (env->IsSameObject(sc->Long.class_ref, object_class)) {
        return env->CallLongMethod(java_object, sc->Long.longValue);
    }

    // Do the slightly slower fallback...
    QString fullName = qtjambi_class_name(env, object_class).replace(".", "/");

    QtJambiTypeManager manager(env);
    QString className = manager.getInternalTypeName(fullName, QtJambiTypeManager::ArgumentType);
    QByteArray l1className = className.toLatin1();
    int type = !className.isEmpty()
        ? QVariant::nameToType(l1className.constData())
        : QVariant::Invalid;
    if (type == QVariant::UserType)
        type = QMetaType::type(l1className.constData());

    JObjectWrapper wrapper(env, java_object);
    void *copy = 0;
    bool destroyCopy = false;
    if (type != QVariant::Invalid) {
        jvalue val;
        val.l = java_object;
        bool ok = manager.convertExternalToInternal(&val, &copy, fullName, className,
                                                    QtJambiTypeManager::ArgumentType);
        if (!ok)
            type = QVariant::Invalid;
        else
            destroyCopy = true;
    }


    if (type == QVariant::Invalid) {
        type = javaObjectVariant();
        copy = &wrapper;
    }

    QVariant returned = QVariant(type, copy);
    if (destroyCopy)
        manager.destroyInternal(copy, QtJambiTypeManager::ArgumentType);

//     qDebug() << fullName << className << returned.type() << returned.typeName();

    return returned;
}

void *qtjambi_to_object(JNIEnv *env, jobject java_object)
{
    if (QtJambiLink *link = QtJambiLink::findLink(env, java_object))
        return link->object();
    else
        return 0;
}

void qtjambi_connect_notify(JNIEnv *env, QtJambiLink *link, const char *signal)
{
    Q_ASSERT(signal != 0);

    QString latin1 = QLatin1String(signal + 1);
    latin1 = latin1.mid(0, latin1.indexOf("("));
    jstring jstr = qtjambi_from_qstring(env, latin1);

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQObject();
    env->CallVoidMethod(link->javaObject(env), sc->QObject.javaConnectNotify, jstr, ((Qaffeine *)link->qobject())->receivers(signal));
}

void qtjambi_disconnect_notify(JNIEnv *env, QtJambiLink *link, const char *signal)
{
    jstring jstr;
    int receivers = 0;
    if (signal != 0) {
        QString latin1 = QLatin1String(signal + 1);
        latin1 = latin1.mid(0, latin1.indexOf("("));
        jstr = qtjambi_from_qstring(env, latin1);
        receivers = ((Qaffeine *)link->qobject())->receivers(signal);
    } else {
        jstr = 0;
    }

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQObject();
    env->CallVoidMethod(link->javaObject(env), sc->QObject.javaDisconnectNotify, jstr, receivers);
}


QObject *qtjambi_to_qobject(JNIEnv *env, jobject java_object)
{
    if (java_object == 0)
        return 0;

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtObject();

    jlong id = env->GetLongField(java_object, sc->QtObject.native_id);
    return id == 0
               ? 0
               : (reinterpret_cast<QtJambiLink *>(id))->qobject();
}

int qtjambi_to_enum(JNIEnv *env, jobject java_object)
{
    int returned = 0;
    jclass clazz = env->GetObjectClass(java_object);
    if (clazz != 0) {
        jmethodID methodId = resolveMethod(env, "value", "()I", clazz);
        if (methodId == 0) {
            env->ExceptionClear();
//             qWarning("qtjambi_to_enum, method 'ordinal()' was not found");
            methodId = resolveMethod(env, "ordinal", "()I", clazz);
        }

        if (methodId != 0)
            returned = env->CallIntMethod(java_object, methodId);
    }

    return returned;
}

void *qtjambi_to_interface(JNIEnv *env,
                          QtJambiLink *link,
                          const char *interface_name,
                          const char *package_name,
                          const char *function_name)
{
    if (!link)
        return 0;
    jobject object = link->javaObject(env);
    jmethodID cast_id = resolveMethod(env, function_name, "(J)J", interface_name, package_name);
    jlong ret = env->CallLongMethod(object, cast_id, (jlong) link->pointer());
    QTJAMBI_EXCEPTION_CHECK(env);
    return reinterpret_cast<void *>(ret);
}


jobject qtjambi_from_object(JNIEnv *env, const QRect &rect, const char *className, const char *packageName)
{
    return qtjambi_from_object(env, &rect, className, packageName);
}

jobject qtjambi_from_object(JNIEnv *env, const QEvent *qt_object, const char *className, const char *packageName)
{
    if (qt_object != 0) {
        switch (qt_object->type()) {
        case QEvent::AccessibilityDescription:
            className = "QAccessibleEvent";
            packageName = "com/trolltech/qt/gui/";
            break;
        case QEvent::AccessibilityHelp: className = "QAccessibleEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ActionAdded: className = "QActionEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ActionChanged: className = "QActionEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ActionRemoved: className = "QActionEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ChildAdded: className = "QChildEvent"; packageName = "com/trolltech/qt/core/"; break;
        case QEvent::ChildPolished: className = "QChildEvent"; packageName = "com/trolltech/qt/core/"; break;
        case QEvent::ChildRemoved: className = "QChildEvent"; packageName = "com/trolltech/qt/core/"; break;
        case QEvent::Clipboard: className = "QClipboardEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Close: className = "QCloseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ContextMenu: className = "QContextMenuEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::DragEnter: className = "QDragEnterEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::DragLeave: className = "QDragLeaveEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::DragMove: className = "QDragMoveEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Drop: className = "QDropEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::FileOpen: className = "QFileOpenEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::FocusIn: className = "QFocusEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::FocusOut: className = "QFocusEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Hide: className = "QHideEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::HoverEnter: className = "QHoverEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::HoverLeave: className = "QHoverEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::HoverMove: className = "QHoverEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::IconDrag: className = "QIconDragEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::InputMethod: className = "QInputMethodEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::KeyPress: className = "QKeyEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::KeyRelease: className = "QKeyEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::MouseButtonDblClick: className = "QMouseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::MouseButtonPress: className = "QMouseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::MouseButtonRelease: className = "QMouseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::MouseMove: className = "QMouseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Move: className = "QMoveEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Paint: className = "QPaintEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Resize: className = "QResizeEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Shortcut: className = "QShortcutEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::ShortcutOverride: className = "QKeyEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Show: className = "QShowEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::StatusTip: className = "QStatusTipEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::TabletMove: className = "QTabletMoveEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::TabletPress: className = "QTabletPressEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::TabletRelease: className = "QTabletReleaseEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Timer: className = "QTimerEvent"; packageName = "com/trolltech/qt/core/"; break;
        case QEvent::ToolTip: className = "QHelpEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::WhatsThis: className = "QHelpEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::Wheel: className = "QWheelEvent"; packageName = "com/trolltech/qt/gui/"; break;
        case QEvent::WindowStateChange:
            className = "QWindowStateChangeEvent";
            packageName = "com/trolltech/qt/gui/";
            break;
        default:
            break;
        }
    }
    return qtjambi_from_object(env, reinterpret_cast<const void *>(qt_object), className, packageName);
}

jobject qtjambi_from_object(JNIEnv *env, const void *qt_object, const char *className, const char *packageName)
{
    jobject returned = 0;
    if (qt_object == 0)
        return 0;

    jclass clazz = resolveClass(env, className, packageName);
    if (clazz != 0)
        returned = env->AllocObject(clazz);
    if (returned == 0)
        return 0;

    int metaType = QMetaType::type(className);

    // If it's not a value type, we just link to the pointer directly.
    void *copy = 0;
    QString java_name;
    if (metaType == QMetaType::Void) {
        // If the object is constructed in Java, then we can look it up
        QtJambiLink *link = QtJambiLink::findLinkForUserObject(qt_object);
        if (link != 0)
            return link->javaObject(env);

        // Otherwise we have to create it
        copy = const_cast<void *>(qt_object);
        java_name = QLatin1String(packageName) + QLatin1String(className);
    } else {
        copy = QMetaType::construct(metaType, qt_object);
        if (copy == 0)
            return 0;
    }
   
    if (!qtjambi_construct_object(env, returned, copy, metaType, java_name, false)) {
        if (metaType != QMetaType::Void && copy != 0)
            QMetaType::destroy(metaType, copy);

        returned = 0;
    }

    return returned;
}

static void qtjambi_setup_connections(JNIEnv *env, QtJambiLink *link)
{
    const QObject *qobject = link->qobject();
    const QMetaObject *mo = qobject->metaObject();
    for (int i=0; i<mo->methodCount(); ++i) {
        QMetaMethod m = mo->method(i);
        if (m.methodType() == QMetaMethod::Signal) {
            const char *signature = m.signature();
            QByteArray ba = QByteArray(signature);
            ba = QByteArray("2") + ba;

            // There will always be one connection
            if (((Qaffeine *)qobject)->receivers(ba.constData()) > 0)
                qtjambi_connect_notify(env, link, ba.constData());
        }
    }
}

jobject qtjambi_from_qobject(JNIEnv *env, QObject *qt_object, const char *className, const char *packageName)
{
    if (qt_object == 0)
        return 0;

    QtJambiLink *link = QtJambiLink::findLinkForQObject(qt_object);
    if (!link) {
        QString javaName = getJavaName(QLatin1String(qt_object->metaObject()->className()));

        QByteArray javaClassName;
        QByteArray javaPackageName;
        if (javaName.length() > 0) {
            javaClassName = QtJambiTypeManager::className(javaName).toLatin1();
            javaPackageName = QtJambiTypeManager::package(javaName).toLatin1();

            className = javaClassName.constData();
            packageName = javaPackageName.constData();
        }

        link = QtJambiLink::createWrapperForQObject(env, qt_object, className, packageName);
        if (!link->createdByJava())
            qtjambi_setup_connections(env, link);
    }
    Q_ASSERT(link);


    return link->javaObject(env);
}

jobject qtjambi_from_enum(JNIEnv *env, int qt_enum, const char *className)
{
    jclass cl = env->FindClass(className);
    Q_ASSERT(cl);

    jmethodID method = env->GetStaticMethodID(cl, "resolve_internal", "(I)Ljava/lang/Object;");
    Q_ASSERT(method);

    return env->CallStaticObjectMethod(cl, method, qt_enum);
}

jobject qtjambi_from_flags(JNIEnv *env, int qt_flags, const char *className)
{
    jclass cl = env->FindClass(className);
    Q_ASSERT(cl);

    jmethodID method = env->GetMethodID(cl, "<init>", "(I)V");
    Q_ASSERT(method);

    return env->NewObject(cl, method, qt_flags);
}

int qtjambi_to_enumerator(JNIEnv *env, jobject value)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtEnumerator();
    return env->CallIntMethod(value, sc->QtEnumerator.value);
}


QtJambiLink *qtjambi_construct_qobject(JNIEnv *env, jobject java_object, QObject *qobject,
                              bool memory_managed)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveThread();
    jobject java_thread = env->CallStaticObjectMethod(sc->Thread.class_ref,
                                                      sc->Thread.currentThread);
    Q_ASSERT(java_thread);

    QThread *qt_thread = qobject->thread();
    Q_ASSERT(qt_thread == QThread::currentThread());
    Q_ASSERT(qt_thread);

    {
        QWriteLocker lock(qtjambi_thread_table_lock());
        qtjambi_thread_table()->insert(qt_thread, env->NewWeakGlobalRef(java_thread));
//         printf("inserting: %p for %s [%s] [%p]\n", qt_thread, qPrintable(qobject->objectName()), qobject->metaObject()->className(), qobject);
    }

    return QtJambiLink::createLinkForQObject(env, java_object, qobject, memory_managed);
}

QtJambiLink *qtjambi_construct_object(JNIEnv *env, jobject java_object, void *object,
                             int metaType, const QString &java_name, bool created_by_java)
{
    QtJambiLink *link = QtJambiLink::createLinkForObject(env, java_object, object, java_name, created_by_java);
    link->setMetaType(metaType);
    return link;
}

QtJambiLink *qtjambi_construct_object(JNIEnv *env, jobject java_object, void *object,
                                      const char *className)
{
    int metaType = QMetaType::type(className);
    if (metaType != QMetaType::Void)
        return qtjambi_construct_object(env, java_object, object, metaType);
    else
        return 0;
}

void *qtjambi_to_cpointer(JNIEnv *env, jobject java_object, int indirections)
{
    if (java_object == 0)
        return 0;
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveNativePointer();
    int object_indirections = env->GetIntField(java_object, sc->NativePointer.indirections);
    if (object_indirections != indirections) {
        jclass exception_class = resolveClass(env, "IllegalArgumentException", "java/lang/");
        Q_ASSERT(exception_class);
        env->ThrowNew(exception_class, "Illegal number of indirections");
        return 0;
    }
    return reinterpret_cast<void *>(env->GetLongField(java_object, sc->NativePointer.ptr));
}

jobject qtjambi_from_cpointer(JNIEnv *env, const void *qt_object, int type, int indirections)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveNativePointer();
    return env->CallStaticObjectMethod(sc->NativePointer.class_ref,
                                       sc->NativePointer.fromNative,
                                       reinterpret_cast<jlong>(qt_object), type, indirections);
}


jstring qtjambi_from_qstring(JNIEnv *env, const QString &s)
{
    QTJAMBI_EXCEPTION_CHECK(env);
    jstring str = env->NewString(reinterpret_cast<const jchar *>(s.constData()), s.length());
    Q_ASSERT(str != 0);

    return str;
}

QString qtjambi_to_qstring(JNIEnv *env, jstring java_string)
{
    if (java_string == 0) {
        StaticCache *sc = StaticCache::instance(env);
        sc->resolveNullPointerException();
        env->ThrowNew(sc->NullPointerException.class_ref, "String is null pointer");
        return QLatin1String("");
    }

    QString result;
    int length = env->GetStringLength(java_string);
    result.resize(length);
    env->GetStringRegion(java_string, 0, length, reinterpret_cast<ushort*>(result.data()));
    return result;
}

QtJambiFunctionTable *qtjambi_setup_vtable(JNIEnv *env,
                                         jobject object,
                                         int inconsistentCount,
                                         const char **inconsistentNames,
                                         const char **inconsistentSignatures,
                                         int count,
                                         const char **names,
                                         const char **signatures)
{
    QTJAMBI_EXCEPTION_CHECK(env);
    jclass object_class = env->GetObjectClass(object);

//     printf("vtable for: %s\n", qPrintable(qtjambi_class_name(env, object_class)));

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveClass();
    sc->resolveObject();
    sc->resolveMethod();
    sc->resolveQtJambiUtils();

    QTJAMBI_EXCEPTION_CHECK(env);
    jstring jclass_name = (jstring) env->CallObjectMethod(object_class, sc->Class.getName);
    QString qclass_name = qtjambi_to_qstring(env, jclass_name);

    QtJambiFunctionTable *table = findFunctionTable(qclass_name);

    if (table) {
        table->ref();
        return table;
    } else {
        table = new QtJambiFunctionTable(qclass_name, count);
    }

    for (int i=0; i<count; ++i) {
        QTJAMBI_EXCEPTION_CHECK(env);
        jmethodID method_id = env->GetMethodID(object_class, names[i], signatures[i]);
        Q_ASSERT_X(method_id, "vtable setup failed",
                   (qclass_name + "::" + names[i] + signatures[i]).toLatin1());

        QTJAMBI_EXCEPTION_CHECK(env);
        jobject method_object = env->ToReflectedMethod(object_class, method_id, true);
        Q_ASSERT(method_object);

        QTJAMBI_EXCEPTION_CHECK(env);
        if (env->CallStaticBooleanMethod(sc->QtJambiUtils.class_ref,
                                         sc->QtJambiUtils.isImplementedInJava,
                                         method_object)) {
            table->setMethod(i, method_id);
//             printf("Implemented: %s::%s\n",
//                    qPrintable(qtjambi_class_name(env, object_class)),
//                    names[i]);
        }
    }

    QTJAMBI_EXCEPTION_CHECK(env);
    storeFunctionTable(qclass_name, table);

    for (int i=0; i<inconsistentCount; ++i) {
        QTJAMBI_EXCEPTION_CHECK(env);
        jmethodID method_id = env->GetMethodID(object_class, inconsistentNames[i],
                                               inconsistentSignatures[i]);
        Q_ASSERT_X(method_id, "vtable setup failed for inconsistent function",
                   (qclass_name + "::" + names[i] + signatures[i]).toLatin1());

        QTJAMBI_EXCEPTION_CHECK(env);
        jobject method_object = env->ToReflectedMethod(object_class, method_id, true);
        Q_ASSERT(method_object);

        QTJAMBI_EXCEPTION_CHECK(env);
        if (env->CallStaticBooleanMethod(sc->QtJambiUtils.class_ref,
                                         sc->QtJambiUtils.isImplementedInJava,
                                         method_object)) {
            QString errorMessage = QString("Function '%1' in class '%2'")
                                   .arg(inconsistentNames[i]).arg(qclass_name);
            QtJambiLink::throwQtException(env, errorMessage,
                                         QLatin1String("QNonVirtualOverridingException"));
            return 0;
        }
    }

    QTJAMBI_EXCEPTION_CHECK(env);
    return table;
}

void qtjambi_setup_signals(JNIEnv *env, jobject java_object, QtJambiSignalInfo *signal_infos, int count,
                          const char **names, const int *argument_counts)
{
    if (count == 0)
        return ;

    jclass object_class = env->GetObjectClass(java_object);
    for (int i=0; i<count; ++i) {
        QByteArray signal_class("Lcom/trolltech/qt/core/QObject$Signal");
        signal_class += QByteArray::number(argument_counts[i]) + ";";

        jfieldID fieldId = env->GetFieldID(object_class, names[i], signal_class.constData());
        Q_ASSERT(fieldId);

        jobject signal_object = env->GetObjectField(java_object, fieldId);
        Q_ASSERT(signal_object);
        signal_infos[i].object = env->NewWeakGlobalRef(signal_object);

        jclass signal_class_object = env->GetObjectClass(signal_object);
        Q_ASSERT(signal_class_object);

        QByteArray signature("(");
        for (int j=0; j<argument_counts[i]; ++j)
            signature += "Ljava/lang/Object;";
        signature += ")V";
        signal_infos[i].methodId = env->GetMethodID(signal_class_object, "emit", signature.constData());
        Q_ASSERT(signal_infos[i].methodId);
    }
}

jobject qtjambi_array_to_nativepointer(JNIEnv *env, jobjectArray array, int elementSize)
{
    int len = env->GetArrayLength(array);
    if (len == 0)
        return 0;

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveNativePointer();
    jobject nativePointer = env->NewObject(sc->NativePointer.class_ref, sc->NativePointer.constructor,
                                           jint(ByteType), elementSize * len, 1);

    char *buf = reinterpret_cast<char *>(qtjambi_to_cpointer(env, nativePointer, 1));
    for (int i=0; i<len; ++i) {
        jobject java_object = env->GetObjectArrayElement(array, i);

        void *ptr = 0;
        if (java_object != 0) {
            QtJambiLink *link = QtJambiLink::findLink(env, java_object);
            if (link != 0)
                ptr = link->pointer();
        }

        if (ptr != 0)
            memcpy(buf + i * elementSize, ptr, elementSize);
    }

    return nativePointer;
}


QThread *qtjambi_to_thread(JNIEnv *env, jobject thread)
{
    QReadLocker locker(qtjambi_thread_table_lock());
    ThreadTable *table = qtjambi_thread_table();
    for (ThreadTable::const_iterator it = table->begin(); it != table->end(); ++it) {
        if (env->IsSameObject(it.value(), thread))
            return it.key();
    }
    return 0;
}

jobject qtjambi_from_thread(JNIEnv *, QThread *thread)
{
    QReadLocker locker(qtjambi_thread_table_lock());
    ThreadTable *table = qtjambi_thread_table();
    jobject java_thread = table->value(thread, 0);
    return java_thread;
}

bool qtjambi_release_threads(JNIEnv *env)
{
    QWriteLocker locker(qtjambi_thread_table_lock());
    int releaseCount = 0;
    ThreadTable *table = qtjambi_thread_table();
    for (ThreadTable::iterator it = table->begin(); it != table->end(); ) {
        jobject java_thread = it.value();
        Q_ASSERT(java_thread);

        if (env->IsSameObject(java_thread, 0)) {
            ++releaseCount;
            QThread *thread = it.key();
            it = table->erase(it);
            delete thread;
        } else {
            ++it;
        }
    }
    return releaseCount > 0;
}

void qtjambi_metacall(JNIEnv *env, QEvent *event)
{
    // The hardcoded value for metatype id's
    Q_ASSERT(event->type() == 512);

    if (!env)
        env = qtjambi_current_environment();

    QtJambiLink *link = QtJambiLink::findLinkForUserObject(event);
    Q_ASSERT(link);

    jobject jEvent = link->javaObject(env);
    Q_ASSERT(jEvent);

    jclass cls = env->GetObjectClass(jEvent);
    Q_ASSERT(cls);

    jmethodID id = env->GetMethodID(cls, "execute", "()V");
    Q_ASSERT(id);

    env->CallVoidMethod(jEvent, id);

    // Clear print out and clear any exceptions that occured during
    // the metacall...
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
}



struct QModelIndexAccessor {
    int row;
    int col;
    void *ptr;
    QAbstractItemModel *model;
};

QModelIndex qtjambi_to_QModelIndex(JNIEnv *env, jobject index)
{
    if (!index)
        return QModelIndex();

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQModelIndex();

    QModelIndexAccessor mia = {
        env->GetIntField(index, sc->QModelIndex.field_row),
        env->GetIntField(index, sc->QModelIndex.field_column),
        (void *) env->GetLongField(index, sc->QModelIndex.field_internalId),
        (QAbstractItemModel *)
        qtjambi_to_qobject(env, env->GetObjectField(index, sc->QModelIndex.field_model))
    };
    QTJAMBI_EXCEPTION_CHECK(env);
    return *(QModelIndex *) &mia;

}

jobject qtjambi_from_QModelIndex(JNIEnv *env, const QModelIndex &index)
{
    if (!index.isValid())
        return 0;
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQModelIndex();

    jobject retVal = env->NewObject(sc->QModelIndex.class_ref, sc->QModelIndex.constructor,
                                    index.row(),
                                    index.column(),
                                    index.internalId(),
                                    qtjambi_from_qobject(env,
                                                        const_cast<QAbstractItemModel *>(index.model()),
                                                        "QAbstractItemModel$ConcreteWrapper",
                                                        "com/trolltech/qt/core/"));
    QTJAMBI_EXCEPTION_CHECK(env);
    return retVal;
}

bool qtjambi_is_created_by_java(QObject *qobject)
{
    extern int qtjambi_user_data_id;
    QtJambiLinkUserData *userData = static_cast<QtJambiLinkUserData *>(qobject->userData(qtjambi_user_data_id));

    Q_ASSERT(!userData || userData->link());

    return userData && userData->link()->createdByJava();
}
