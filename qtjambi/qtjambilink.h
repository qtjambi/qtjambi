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

#ifndef QTJAMBISHELL_H
#define QTJAMBISHELL_H

#include "qtjambi_global.h"

#include <QObject>
#include <QString>
#include <QList>
#include <QHash>
#include <QVector>
#include <QMetaType>
#include <QEvent>

class QtJambiLink;

#define PACKAGEPATH "com/trolltech/"
#define EXCEPTIONPATHN PACKAGEPATH"exceptions/"
#define EXCEPTIONPATH(CLASS) EXCEPTIONPATHN#CLASS
#define KERNELPATHN PACKAGEPATH"kernel/"
#define KERNELPATH(CLASS) KERNELPATHN#CLASS
#define QTPATHN PACKAGEPATH"qt/"
#define QTPATH(CLASS) QTPATHN#CLASS

struct QtJambiLinkUserData : public QObjectUserData
{
    QtJambiLinkUserData(QtJambiLink *link) : m_link(link) { }
    virtual ~QtJambiLinkUserData();

    inline QtJambiLink *link() { return m_link; }

private:
    QtJambiLink *m_link;
};

/*
  A QtJambiLink is owned by Java, it will be deleted when the
  java object is finalized.
 */
class QTJAMBI_EXPORT QtJambiLink
{
    inline QtJambiLink(jobject jobj)
        : m_java_object(jobj),
          m_meta_type(QMetaType::Void),
          m_wrapper(0),
          m_has_been_finalized(false),
          m_qobject_deleted(false),
          m_created_by_java(false),
          m_object_invalid(false),
          m_in_cache(false),
          m_connected_to_java(false),
          m_delete_in_main_thread(false),
          m_destructor_function(0),
          m_ownership(SplitOwnership) // Default to split, because it's safest
    {
    };

public:
    enum Ownership {
        JavaOwnership, // Weak ref to java object, deleteNativeObject deletes c++ object
        CppOwnership,  // Strong ref to java object until c++ object is deleted, deleteNativeObject does *not* delete c++ obj.
        SplitOwnership // Weak ref to java object, deleteNativeObject does *not* delete c++ object
    };

    ~QtJambiLink();

    /* Returns the pointer value, wether its a QObject or plain object */
    inline void *pointer() const { return m_pointer; }

    /* Returns the pointer value as an object, will assert if pointer
       is a QObject */
    inline void *object() const { Q_ASSERT(!isQObject()); return m_pointer; }
    void resetObject(JNIEnv *env);

    /* Returns the pointer value for the signal wrapper, will assert if pointer is not a QObject */
    inline QObject *signalWrapper() const { Q_ASSERT(isQObject()); return m_wrapper; }
    inline void setSignalWrapper(QObject *ptr) { m_wrapper = ptr; }

    inline jobject javaObject(JNIEnv *env) const;

    /* Returns the pointer value as a QObject, will assert if pointer
       is not a QObject */
    inline QObject *qobject() const { Q_ASSERT(isQObject()); return reinterpret_cast<QObject *>(m_pointer); }

    inline int metaType() const { return m_meta_type; }
    void setMetaType(int metaType);

    inline bool isCached() const { return m_in_cache; }

    /* Returns true if this link holds a global reference to the java
       object, meaning that the java object will not be
       finalized. This is for widgets mostly. */
    inline bool isGlobalReference() const { return m_global_ref; }

    /* Returns true if the link has ownership over the data. */
    inline bool hasOwnership() const { return !isQObject() || !isGlobalReference(); }

    inline bool isQObject() const { return m_is_qobject; }

    /* Deletes any global references to the java object so that it can
       be finalized by the virtual machine */
    void releaseJavaObject(JNIEnv *env);

    /* Deletes the native object */
    void deleteNativeObject(JNIEnv *env);

    /* Triggered by native jni functions when a java object has been
       finalized. */
    void javaObjectFinalized(JNIEnv *env);

    /* Called by the native jni fucntion when the java object has been
       disposed */
    void javaObjectDisposed(JNIEnv *env);

    void registerSubObject(void *);
    void unregisterSubObject(void *);

    inline bool hasBeenFinalized() const { return m_has_been_finalized; }
    inline bool readyForDelete() const { return !isQObject() || (hasBeenFinalized() && qobjectDeleted()); }
    inline bool qobjectDeleted() const { return m_qobject_deleted; }
    inline PtrDestructorFunction destructorFunction() const { return m_destructor_function; }
    inline bool connectedToJava() const { return m_connected_to_java; }
    inline bool deleteInMainThread() const { return m_delete_in_main_thread; }
    inline void setAsQObjectDeleted() { m_qobject_deleted = true; }
    inline void setAsFinalized() { m_has_been_finalized = true; }
    inline void setDestructorFunction(PtrDestructorFunction dfnc) { m_destructor_function = dfnc; }
    inline void setConnectedToJava(bool c) { m_connected_to_java = c; }
    inline void setDeleteInMainThread(bool c) { m_delete_in_main_thread = c; }

    inline bool createdByJava() const { return m_created_by_java; }
    inline void setCreatedByJava(bool cbj) { m_created_by_java = cbj; }

    int indexQtSignal(const QByteArray &signal) const;
    int indexQtSlot(const QByteArray &slot) const;

    void disableGarbageCollection(JNIEnv *env, jobject java);

    void setCppOwnership(JNIEnv *env, jobject java);
    void setJavaOwnership(JNIEnv *env, jobject java);
    void setSplitOwnership(JNIEnv *env, jobject java);
    void setDefaultOwnership(JNIEnv *env, jobject java);

    Ownership ownership() const { return Ownership(m_ownership); }

    void setGlobalRef(JNIEnv *env, bool global);

    static QtJambiLink *createLinkForObject(JNIEnv *env, jobject java, void *ptr, const QString &java_name,
        bool enter_in_cache);
    static QtJambiLink *createLinkForQObject(JNIEnv *env, jobject java, QObject *object);
    static QtJambiLink *createWrapperForQObject(JNIEnv *env, QObject *o, const char *class_name,
        const char *package_name);

    static QtJambiLink *findLink(JNIEnv *env, jobject java);
    static inline QtJambiLink *findQObjectLink(JNIEnv *env, jobject java);

    static QtJambiLink *findLinkForQObject(QObject *qobject);
    static QtJambiLink *findLinkForUserObject(const void *ptr);

    static jmethodID findMethod(JNIEnv *env, jobject java, const QString &method);

    static QString nameForClass(JNIEnv *env, jclass clazz);
    static bool stripQtPackageName(QString *className);
    static bool throwQtException(JNIEnv *env, const QString &extra, const QString &name);

private:
    void setNativeId();
    void cleanUpAll(JNIEnv *env);
    void removeFromCache(JNIEnv *env);
    void aboutToMakeObjectInvalid(JNIEnv *env);

    jobject m_java_object;
    void *m_pointer;
    int m_meta_type;

    QObject *m_wrapper;

    uint m_global_ref : 1;
    uint m_is_qobject : 1;
    uint m_has_been_finalized : 1;
    uint m_qobject_deleted : 1;
    uint m_created_by_java : 1;
    uint m_object_invalid : 1;
    uint m_in_cache : 1;
    uint m_connected_to_java : 1;
    uint m_delete_in_main_thread : 1;
    uint m_reserved1 : 23;

    PtrDestructorFunction m_destructor_function;

    uint m_ownership : 2;
    uint m_reserved2 : 30;
};

inline jobject QtJambiLink::javaObject(JNIEnv *env) const
{
    if (m_global_ref)
        return m_java_object;
    else
        return env->NewLocalRef(m_java_object);
}

inline QtJambiLink *QtJambiLink::findQObjectLink(JNIEnv *env, jobject java)
{
    QtJambiLink *link = findLink(env, java);
    return link && link->isQObject() ? link : 0;
}
  
#endif
