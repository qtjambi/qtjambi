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

#include "qtjambilink.h"

#include "qtjambi_cache.h"
#include "qtjambi_core.h"
#include "qtjambitypemanager.h"
#include "qtjambidestructorevent.h"

#include <QDebug>
#include <QHash>
#include <QReadWriteLock>
#include <QThread>
#include <QWriteLocker>
#include <QCoreApplication>

// #define DEBUG_REFCOUNTING

typedef QHash<const void *, QtJambiLink *> LinkHash;
Q_GLOBAL_STATIC(QReadWriteLock, gStaticUserDataIdLock);
Q_GLOBAL_STATIC(QReadWriteLock, gUserObjectCacheLock);
Q_GLOBAL_STATIC(LinkHash, gUserObjectCache);

int qtjambi_user_data_id = -1;


static int user_data_id() 
{
  {
    QReadLocker read_lock(gStaticUserDataIdLock());
    if (qtjambi_user_data_id >= 0)
      return qtjambi_user_data_id;
  }

  {
    QWriteLocker lock(gStaticUserDataIdLock());
    if (qtjambi_user_data_id == -1)
      qtjambi_user_data_id = QObject::registerUserData();
    return qtjambi_user_data_id;
  }
  
}

inline static void deleteWeakObject(JNIEnv *env, jobject object)
{
#ifdef Q_CC_MINGW
    env->DeleteWeakGlobalRef( (jweak) object);
#else
    env->DeleteWeakGlobalRef(object);
#endif
}

void QtJambiLink::registerSubObject(void *ptr) {
    QWriteLocker locker(gUserObjectCacheLock());
    Q_ASSERT(gUserObjectCache());
    gUserObjectCache()->insert(ptr, this);
}

void QtJambiLink::unregisterSubObject(void *ptr) {
    QWriteLocker locker(gUserObjectCacheLock());

    int i = gUserObjectCache() ? gUserObjectCache()->remove(ptr) : 1;
    Q_ASSERT(i == 1);
    Q_UNUSED(i);
}

QtJambiLink *QtJambiLink::createLinkForQObject(JNIEnv *env, jobject java, QObject *object)
{
    Q_ASSERT(env);
    Q_ASSERT(java);
    Q_ASSERT(object);

    // Initialize the link
    QtJambiLink *link = new QtJambiLink(env->NewWeakGlobalRef(java));
    link->m_is_qobject = true;
    link->m_global_ref = false;
    link->m_pointer = object;

    // Fetch the user data id
    object->setUserData(user_data_id(), new QtJambiLinkUserData(link));

    // Set the native__id field of the java object
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiObject();
    env->SetLongField(link->m_java_object, sc->QtJambiObject.native_id, reinterpret_cast<jlong>(link));

    link->setCppOwnership(env, link->m_java_object);

    return link;
}


QtJambiLink *QtJambiLink::createWrapperForQObject(JNIEnv *env, QObject *object, const char *class_name,
                                                const char *package_name)
{
    Q_ASSERT(!object->userData(user_data_id()));

    jclass object_class = resolveClass(env, class_name, package_name);
    if (object_class == 0) {
        qWarning("createWrapperForQObject(), failed to resolve class %s.%s\n",
                 package_name, class_name);
        return 0;
    }

    jmethodID constructorId = resolveMethod(env, "<init>", "(Lcom/trolltech/qt/QtJambiObject$QPrivateConstructor;)V",
        class_name, package_name, false);
    Q_ASSERT(constructorId);

    jobject java_object = env->NewObject(object_class, constructorId, 0);
    return createLinkForQObject(env, java_object, object);
}


QtJambiLink *QtJambiLink::createLinkForObject(JNIEnv *env, jobject java, void *ptr, const QString &java_name,
                                              bool enter_in_cache)
{
    Q_ASSERT(env);
    Q_ASSERT(java);
    Q_ASSERT(ptr);

    // Initialize the link
    QtJambiLink *link = new QtJambiLink(env->NewWeakGlobalRef(java));
    link->m_is_qobject = false;
    link->m_global_ref = false;
    link->m_pointer = ptr;
    link->m_destructor_function = java_name.isEmpty() ? 0 : destructor(java_name);

    // If the object is created by Java, then we have control over its destructor, which means
    // we can cache the pointer. Otherwise, we do not have any control over when the memory
    // becomes free, so we cannot cache the pointer.
    if (enter_in_cache) {
        QWriteLocker locker(gUserObjectCacheLock());
        Q_ASSERT(gUserObjectCache());
        gUserObjectCache()->insert(ptr, link);
        link->m_in_cache = true;
    }

    // Set the native__id field of the java object
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiObject();
    env->SetLongField(link->m_java_object, sc->QtJambiObject.native_id, reinterpret_cast<jlong>(link));

    return link;
}

QtJambiLink *QtJambiLink::findLinkForUserObject(const void *ptr)
{
    if (ptr == 0)
        return 0;

    QReadLocker locker(gUserObjectCacheLock());
    Q_ASSERT(gUserObjectCache());
    return gUserObjectCache()->value(ptr, 0);
}

QtJambiLink *QtJambiLink::findLinkForQObject(QObject *o)
{
    if (o == 0)
        return 0;

    QtJambiLinkUserData *p = static_cast<QtJambiLinkUserData *>(o->userData(user_data_id()));
    return p == 0 ? 0 : p->link();
}


QtJambiLink *QtJambiLink::findLink(JNIEnv *env, jobject java)
{
    if (java == 0)
        return 0;

    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiObject();
    return reinterpret_cast<QtJambiLink *>(env->GetLongField(java, sc->QtJambiObject.native_id));
}


void QtJambiLink::releaseJavaObject(JNIEnv *env)
{
    if (!m_java_object)
        return;

    aboutToMakeObjectInvalid(env);

    if (isGlobalReference()) {
        env->DeleteGlobalRef(m_java_object);
    } else {
        // Check if garbage collector has removed the object
        jobject localRef = env->NewLocalRef(m_java_object);
        if (!env->IsSameObject(localRef, 0)) {
            deleteWeakObject(env, m_java_object);
            env->DeleteLocalRef(localRef);
        }
    }

    m_java_object = 0;
}

#ifdef DEBUG_REFCOUNTING
typedef QHash<int, int> RefCountingHash;
Q_GLOBAL_STATIC(QReadWriteLock, gRefCountStaticLock);
Q_GLOBAL_STATIC(RefCountingHash, gRefCountHash);
#endif

QtJambiLink::~QtJambiLink()
{
    JNIEnv *env = qtjambi_current_environment();
    cleanUpAll(env);
}


void QtJambiLink::aboutToMakeObjectInvalid(JNIEnv *env)
{
    if (env != 0 && m_pointer != 0 && m_java_object != 0 && !m_object_invalid) {
        StaticCache *sc = StaticCache::instance(env);
        sc->resolveQtJambiObject();
        env->CallVoidMethod(m_java_object, sc->QtJambiObject.disposed);
        qtjambi_exception_check(env);
        env->SetLongField(m_java_object, sc->QtJambiObject.native_id, 0);
        QTJAMBI_EXCEPTION_CHECK(env);
        m_object_invalid = true;
    }
}

void QtJambiLink::setGlobalRef(JNIEnv *env, bool global)
{
    if (global == m_global_ref)
        return;

    Q_ASSERT_X(m_java_object, "QtJambiLink::setGlobalRef()", "Java object required");

    // Delete the global reference and make it a weak one
    if (!global) {
        jobject localRef = env->NewWeakGlobalRef(m_java_object);
        env->DeleteGlobalRef(m_java_object);

        m_global_ref = false;
        m_java_object = localRef;

    // Delete the weak ref and replace it with a global ref
    } else {
        jobject globalRef = env->NewGlobalRef(m_java_object);
        env->DeleteWeakGlobalRef(m_java_object);

        m_global_ref = true;
        m_java_object = globalRef;
    }
}

void QtJambiLink::deleteNativeObject(JNIEnv *env)
{
#ifdef DEBUG_REFCOUNTING
    {
        QWriteLocker locker(gRefCountStaticLock());
        int currentCount = gRefCountHash()->value(m_meta_type, 0) - 1;
        gRefCountHash()->insert(m_meta_type, currentCount);
        qDebug("Deleting '%s' [count after: %d]", QMetaType::typeName(m_meta_type), currentCount);
    }
#endif
    Q_ASSERT(m_pointer);

    aboutToMakeObjectInvalid(env);

    if (m_java_object && isGlobalReference()) {
        env->DeleteGlobalRef(m_java_object);
        m_java_object = 0;
    }

    if (isQObject() && m_ownership == JavaOwnership) {

        QObject *qobj = qobject();
        QThread *objectThread = qobj->thread();

        // Explicit dispose from current thread, delete object
        if (QThread::currentThread() == objectThread) {
//             printf(" - straight up delete %s [%s]\n",
//                    qPrintable(qobj->objectName()),
//                    qobj->metaObject()->className());
            delete qobj;

        // We're in the main thread and we'll have an event loop
        // running, so its safe to call delete later.
        } else if (QCoreApplication::instance() &&
                   QCoreApplication::instance()->thread() == objectThread) {
//             printf(" - deleteLater in main thread %s [%s]\n",
//                    qPrintable(qobj->objectName()),
//                    qobj->metaObject()->className());
            qobj->deleteLater();

        // If the QObject is in a non-main thread, check if that
        // thread is a QThread, in which case it will run an eventloop
        // and thus, do cleanup, hence deleteLater() is safe;
        // Otherwise issue a warning.
        } else {
            jobject t = env->NewLocalRef(qtjambi_from_thread(env, objectThread));
            if (t) {
                QTJAMBI_EXCEPTION_CHECK(env);
//                printf("name %s, %s, %p", qPrintable(objectThread->objectName()), objectThread->metaObject()->className(), t);;
                jclass cl = env->GetObjectClass(t);
//                qDebug() << ".. au ..";

                if (qtjambi_class_name(env, cl) == QLatin1String("com.trolltech.qt.QThread")) {
    //                 printf(" - delete later in QThread=%p %s [%s]\n",
    //                        objectThread,
    //                        qPrintable(qobj->objectName()),
    //                        qobj->metaObject()->className());
                    qobj->deleteLater();
                } else if (QCoreApplication::instance()) {
                    qWarning("QObjects can only be implicitly garbage collected when owned"
                            " by a QThread, native resource ('%s' [%s]) is leaked",
                            qPrintable(qobj->objectName()),
                            qobj->metaObject()->className());
                } 

    //             StaticCache *sc = StaticCache::instance(env);
    //             sc->resolveQThread();
    //             if (env->IsSameObject(cl, sc->QThread.class_ref)) {
    //                 qobj->deleteLater();

    //             } else {
    //                 // ## Resolve QThreadAffinityException...
    //                 // Message: "QObjects can only be implicitly garbage collected when owned by a QThread".
    //                 qWarning("something really bad happened...");
    //             }
            } else {
                delete qobj;
            }
            env->DeleteLocalRef(t);
        } 
        m_pointer = 0;

    } else {
	if (deleteInMainThread() && (QCoreApplication::instance() == 0 || QCoreApplication::instance()->thread() != QThread::currentThread())) {
	    if (QCoreApplication::instance()) {
                QCoreApplication::postEvent(QCoreApplication::instance(), new QtJambiDestructorEvent(m_pointer, m_meta_type, m_ownership, m_destructor_function));
	    }
        } else if (m_pointer != 0 && m_meta_type != QMetaType::Void && (QCoreApplication::instance() != 0 
                   || (m_meta_type < QMetaType::FirstGuiType || m_meta_type > QMetaType::LastGuiType))) {
	    QMetaType::destroy(m_meta_type, m_pointer);
        } else if (m_ownership == JavaOwnership && m_destructor_function) {
            m_destructor_function(m_pointer);
        }
        m_pointer = 0;



    }
}

void QtJambiLink::cleanUpAll(JNIEnv *env)
{
    if (m_java_object != 0)
        releaseJavaObject(env);

    if (m_pointer)
        deleteNativeObject(env);
}

void QtJambiLink::javaObjectFinalized(JNIEnv *env)
{
    cleanUpAll(env);
    setAsFinalized();
    if (readyForDelete())
        delete this;
}

void QtJambiLink::javaObjectDisposed(JNIEnv *env)
{
    if (m_pointer) {
        setJavaOwnership(env, m_java_object);
        deleteNativeObject(env);
    }
}


void QtJambiLink::setMetaType(int metaType)
{
    m_meta_type = metaType;

#ifdef DEBUG_REFCOUNTING
    {
        QWriteLocker locker(gRefCountStaticLock());
        int currentCount = gRefCountHash()->value(m_meta_type, 0) + 1;
        gRefCountHash()->insert(m_meta_type, currentCount);
        qDebug("Creating '%s' [count after: %d]", QMetaType::typeName(m_meta_type), currentCount);
    }
#endif
}


void QtJambiLink::resetObject(JNIEnv *env) {
    aboutToMakeObjectInvalid(env);

    if (m_in_cache)
        removeFromCache(env);
    m_pointer = 0;

    if (m_wrapper) {
        delete m_wrapper;
        m_wrapper = 0;
    }
}


/*******************************************************************************
 * Convenience stuff...
 */

jmethodID QtJambiLink::findMethod(JNIEnv *env, jobject javaRef, const QString &method)
{
    Q_ASSERT(javaRef != 0);
    Q_ASSERT(env != 0);

    QString name;
    QString signature = QtJambiTypeManager::toJNISignature(method, &name);

    jclass clazz = env->GetObjectClass(javaRef);
    jmethodID id = 0;
    if (clazz != 0)
        id = resolveMethod(env, name.toLatin1(), signature.toLatin1(), clazz);

    if (id == 0) {
        qWarning("QtJambiLink::findMethod(), '%s' was not found (%s - %s)",
                 qPrintable(method),
                 qPrintable(name), qPrintable(signature));
    }

    return id;
}

void QtJambiLink::removeFromCache(JNIEnv *env)
{
    QWriteLocker locker(gUserObjectCacheLock());

    releaseJavaObject(env);

    if (m_pointer != 0 && gUserObjectCache() && gUserObjectCache()->contains(m_pointer)) {
        int count = gUserObjectCache()->remove(m_pointer);
        Q_ASSERT(count == 1);
        Q_UNUSED(count);
        m_in_cache = false;
    }
}

bool QtJambiLink::throwQtException(JNIEnv *env, const QString &extra, const QString &name)
{
    bool success = false;
    jclass cls = resolveClass(env, name.toUtf8().constData(), "com/trolltech/qt/");
    QTJAMBI_EXCEPTION_CHECK(env);
    success = (env->ThrowNew(cls, extra.toUtf8()) == 0);
    return success;
}

QString QtJambiLink::nameForClass(JNIEnv *env, jclass clazz)
{
    QString returned;

    jmethodID methodId = resolveMethod(env, "getName", "()Ljava/lang/String;", "Class", "java/lang/");
    if (methodId != 0) {
        returned = qtjambi_to_qstring(env, reinterpret_cast<jstring>(env->CallObjectMethod(clazz, methodId)));
    }

    return returned;
}

bool QtJambiLink::stripQtPackageName(QString *className)
{
    bool altered = false;
    if (className->startsWith(QLatin1String(PACKAGEPATH))) {
        int idx = className->lastIndexOf("/");
        if (idx != -1) {
            (*className) = className->right(className->length() - idx - 1);
            altered = true;
        }
    }

    return altered;
}


int QtJambiLink::indexQtSignal(const QByteArray &signal) const
{
    Q_ASSERT(qobject() != 0);

    const QMetaObject *mo = qobject()->metaObject();
    QByteArray normalized = QMetaObject::normalizedSignature(signal.data());

    return mo->indexOfSignal(normalized.data() + 1);
}

int QtJambiLink::indexQtSlot(const QByteArray &slot) const
{
    Q_ASSERT(qobject() != 0);

    const QMetaObject *mo = qobject()->metaObject();
    QByteArray normalized = QMetaObject::normalizedSignature(slot.data());

    return mo->indexOfSlot(normalized.data() + 1);
}


void QtJambiLink::disableGarbageCollection(JNIEnv *env, jobject obj)
{
    setCppOwnership(env, obj);
}

void QtJambiLink::setCppOwnership(JNIEnv *env, jobject obj)
{
    if (!isGlobalReference()) {
        jobject global_ref = env->NewGlobalRef(obj);

        if (m_java_object)
            deleteWeakObject(env, m_java_object);

        m_java_object = global_ref;
        m_global_ref = true;
    }
    m_ownership = CppOwnership;
}

void QtJambiLink::setDefaultOwnership(JNIEnv *env, jobject obj) 
{
    if (createdByJava())
        setJavaOwnership(env, obj);
    else
        setSplitOwnership(env, obj);
}

void QtJambiLink::setJavaOwnership(JNIEnv *env, jobject obj)
{
    if (isGlobalReference()) {
        jobject weak_ref = env->NewWeakGlobalRef(obj);
        if (m_java_object)
            env->DeleteGlobalRef(m_java_object),
        m_java_object = weak_ref;
        m_global_ref = false;
    }
    m_ownership = JavaOwnership;
}

void QtJambiLink::setSplitOwnership(JNIEnv *env, jobject obj)
{
    if (isGlobalReference()) {
        jobject weak_ref = env->NewWeakGlobalRef(obj);
        if (m_java_object)
            env->DeleteGlobalRef(m_java_object),
        m_java_object = weak_ref;
        m_global_ref = false;
    }
    m_ownership = SplitOwnership;
}

QtJambiLinkUserData::~QtJambiLinkUserData()
{
    if (m_link) {
        JNIEnv *env = qtjambi_current_environment();
        m_link->releaseJavaObject(env);
        m_link->setAsQObjectDeleted();
        m_link->resetObject(env);

	if (m_link->readyForDelete())
	    delete m_link;
    }
}
