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

#include "qtjambilink.h"

#include "qtjambi_cache.h"
#include "qtjambi_core.h"
#include "qtjambitypemanager_p.h"
#include "qtjambidestructorevent_p.h"

#include <QDebug>
#include <QHash>
#include <QReadWriteLock>
#include <QThread>
#include <QWriteLocker>
#include <QCoreApplication>

#if defined(QTJAMBI_DEBUG_TOOLS)
#  include "qtjambidebugtools_p.h"
#  include <QStringList>
#endif

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

int QtJambiLinkUserData::id()
{
    return user_data_id();
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
    gUserObjectCache()->remove(ptr);
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

#if defined(QTJAMBI_DEBUG_TOOLS)
    link->m_className = QString::fromLatin1(object->metaObject()->className());
    qtjambi_increase_linkConstructedCount(link->m_className);
#endif

    // Fetch the user data id
    object->setUserData(user_data_id(), new QtJambiLinkUserData(link));

    // Set the native__id field of the java object
    StaticCache *sc = StaticCache::instance();
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
    QtJambiLink *link = createLinkForQObject(env, java_object, object);
    link->setMetaObject(object->metaObject());
    return link;
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

#if defined(QTJAMBI_DEBUG_TOOLS)
    link->m_className = java_name.indexOf("/") >= 0 ? java_name.split("/").last() : java_name;
    qtjambi_increase_linkConstructedCount(link->m_className);
#endif

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
    StaticCache *sc = StaticCache::instance();
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

    StaticCache *sc = StaticCache::instance();
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

// If the object is to be deleted in the main thread, then the gc thread will post an event
// to the main thread which will delete the link, and then proceed to alter the link object.
// If the event is delivered before the process of cleaning up the link in the gc thread is
// done, we will get crashes. Therefore, the lock guarantees that only one thread is cleaning
// up the link at any given time.
Q_GLOBAL_STATIC(QReadWriteLock, g_deleteLinkLock);

QtJambiLink::~QtJambiLink()
{
    if (deleteInMainThread())
        g_deleteLinkLock()->lockForWrite();



    JNIEnv *env = qtjambi_current_environment();
    cleanUpAll(env);

#if defined(QTJAMBI_DEBUG_TOOLS)
    qtjambi_increase_linkDestroyedCount(m_className);
#endif

    if (deleteInMainThread())
        g_deleteLinkLock()->unlock();
}


void QtJambiLink::aboutToMakeObjectInvalid(JNIEnv *env)
{
    if (env != 0 && m_pointer != 0 && m_java_object != 0 && !m_object_invalid) {
        StaticCache *sc = StaticCache::instance();
        sc->resolveQtJambiObject();
        env->CallVoidMethod(m_java_object, sc->QtJambiObject.disposed);
        qtjambi_exception_check(env);

#if defined(QTJAMBI_DEBUG_TOOLS)
        qtjambi_increase_objectInvalidatedCount(m_className);
#endif

        env->SetLongField(m_java_object, sc->QtJambiObject.native_id, 0);
        if (m_in_cache)
            removeFromCache(env);
        QTJAMBI_EXCEPTION_CHECK(env);
        m_object_invalid = true;
    }
}

void QtJambiLink::setMetaObject(const QMetaObject *mo) const
{
    Q_ASSERT(isQObject());
    if (!isQObject())
        return;

    QObject *o = qobject();
    QtJambiLinkUserData *d = static_cast<QtJambiLinkUserData *>(o->userData(QtJambiLinkUserData::id()));
    if (d != 0)
        d->setMetaObject(mo);
    else
        qWarning("setMetaObject: No jambi user data in QObject, line %d in file '%s'", __LINE__, __FILE__);
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

    //             StaticCache *sc = StaticCache::instance();
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
        if (m_ownership == JavaOwnership && deleteInMainThread() && (QCoreApplication::instance() == 0 || QCoreApplication::instance()->thread() != QThread::currentThread())) {

            if (QCoreApplication::instance()) {
                QCoreApplication::postEvent(QCoreApplication::instance(), new QtJambiDestructorEvent(this, m_pointer, m_meta_type, m_ownership, m_destructor_function));
            }

        } else if (m_ownership == JavaOwnership && m_pointer != 0 && m_meta_type != QMetaType::Void && (QCoreApplication::instance() != 0
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

    if (m_wrapper) {
        delete m_wrapper;
        m_wrapper = 0;
    }
}

void QtJambiLink::javaObjectFinalized(JNIEnv *env)
{
    if (deleteInMainThread())
        g_deleteLinkLock()->lockForWrite();

    cleanUpAll(env);
    setAsFinalized();

    // This is required for the user data destructor
    // can know it's time to delete the link object.
    m_java_link_removed = true;

    // Link deletion policy:
    //
    // 1. QObjects will be deleteLater'd, and they always have a link, so we must keep the link
    // 2. GUI objects with Java ownership will be deleted by event, the event will kill the link,
    //    so we must keep the link.
    // 3. For all other objects:
    //    A. If Java has ownership of the native object, it has been deleted, so we kill the link
    //    B. The last possible scenario is either C++ owns the Java object (it wouldn't have been finalized)
    //       or it's not created by Java. Since the Java object has been finalized, we know the ownership
    //       is split, and the object is not created by Java, and we can safely kill the link because
    //       the native object cannot have a link back.

    // Collect info before opening the lock, as the link may be deleted at any point after the
    // lock is released, so we can't call any functions on it.
    bool javaOwnership = ownership() == JavaOwnership;
    bool isQObject = this->isQObject();
    bool deleteInMainThread = this->deleteInMainThread();
    bool qobjectDeleted = this->qobjectDeleted();

    if (deleteInMainThread)
        g_deleteLinkLock()->unlock();

    // **** Link may be deleted at this point (QObjects and GUI objects that are Java owned)

    if ((!isQObject || qobjectDeleted) && (!deleteInMainThread || !javaOwnership))
        delete this;
}

void QtJambiLink::javaObjectDisposed(JNIEnv *env)
{
    if (deleteInMainThread())
        g_deleteLinkLock()->lockForWrite();

    if (m_pointer) {
        setJavaOwnership(env, m_java_object);
        deleteNativeObject(env);

#if defined(QTJAMBI_DEBUG_TOOLS)
        qtjambi_increase_disposeCalledCount(m_className);
#endif

    }

    // Link has been severed from Java object. We can delete
    // it as soon as the C++ object severs its link.
    m_java_link_removed = true;

    // Link deletion policy:
    //
    // NOTE that dispose() forces Java ownership of the C++ object, so we
    // only need to consider this case.
    //
    // 1. QObjects that are in the wrong thread will be deleteLater'd, we must keep the link
    // 2. QObjects that are the right thread have been deleted, we can kill the link [qobjectDeleted is true in this case]
    // 3. GUI objects that are not in the GUI thread will be deleted later, we keep the link
    // 4. All other objects can kill the link now

    // Collect data about object before opening lock, as the link
    // can be deleted at any point once the lock is open
    bool isQObject = this->isQObject();
    bool qobjectDeleted = this->qobjectDeleted();
    bool deleteInMainThread = this->deleteInMainThread();
    bool isGUIThread = QCoreApplication::instance() == 0 || QCoreApplication::instance()->thread() == QThread::currentThread();

    if (deleteInMainThread)
        g_deleteLinkLock()->unlock();

    // **** Link may be deleted at this point (QObjects and GUI objects that are Java owned)
    
    if ((!isQObject || qobjectDeleted) && (!deleteInMainThread || isGUIThread))
        delete this;
}

void QtJambiLink::nativeShellObjectDestroyed(JNIEnv *env)
{
    resetObject(env);

    // Link deletion policy:
    //
    // Either the object has Java ownership or C++ ownership [we can only come here if the object was created by Java]
    // We also know that this function is *never* called for QObjects, as they are memory managed differently.
    // 1. If it has Java ownership, it is currently being deleted, we should keep the link because we are inside one of
    //    the other deletion entry points, and the link will be deleted a little bit later.
    // 2. If it has C++ ownership, the link should be deleted, because the Java object will now have its link removed.

    if (ownership() != JavaOwnership)
        delete this;
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
    releaseJavaObject(env);
    aboutToMakeObjectInvalid(env);

    m_pointer = 0;
}

void QtJambiLink::javaObjectInvalidated(JNIEnv *env)
{
    releaseJavaObject(env);

    // Link deletion policy
    //
    // We can only get to this point if the object is not Java owned, so that reduces the cases we need to look at.
    // 1. Either the native object has a link back (it's a QObject which has not been deleted,
    //    or a polymorphic object which has been created by Java.) We should keep the link in this case.
    // 2. or it doesn't (not created by Java and not QObject, or not polymorphic.) We should kill the link in this case.
    //    because there's no way of getting back to it.

    m_java_link_removed = true;
    if (qobjectDeleted() || (!isQObject() && !createdByJava()))
        delete this;
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

void QtJambiLink::removeFromCache(JNIEnv *)
{
    QWriteLocker locker(gUserObjectCacheLock());
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
        // This typically happens when a QObject is destroyed after the vm shuts down,
        // in which case there is no way for us to properly clean up...
        if (!env)
            return;
        m_link->setAsQObjectDeleted();

        m_link->resetObject(env);

#if defined(QTJAMBI_DEBUG_TOOLS)
        qtjambi_increase_userDataDestroyedCount(m_link->m_className);
#endif


        // Link deletion policy
        //
        // 1. If there's Java ownership, java link removed: We are being deleteLater'd. Kill the link.
        // 2. Java ownership, java link not removed: We are currently being deleted. Keep the link. (dispose() basically)
        // 3. Cpp/Split ownership: Java object has been invalidated, so there's no link back. We can kill the link.

        if (m_link->ownership() != QtJambiLink::JavaOwnership || m_link->javaLinkRemoved())
            delete m_link;
    }
}
