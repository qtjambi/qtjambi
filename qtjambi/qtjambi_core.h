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

#ifndef QTJAMBI_CORE_H
#define QTJAMBI_CORE_H

#ifdef QT3_SUPPORT
#  error Qt must be configured without Qt 3 support in order to build Qt Jambi
#endif

#include "qtjambi_global.h"

#include "qtjambi_cache.h"
#include "qtjambilink.h"

#include <QtCore/QObject>
#include <QtCore/QString>
#include <QtCore/QMetaType>

#include <QtCore/QPair>
#include <QtCore/QVariant>
#include <QtCore/QEvent>
#include <QtCore/QModelIndex>
#include <QtCore/QVarLengthArray>

#ifdef QT_NO_DEBUG
#  define QTJAMBI_EXCEPTION_CHECK(env)
#  define QTJAMBI_DEBUG_TRACE(location)
#else
#  define QTJAMBI_EXCEPTION_CHECK(env) \
      if (env->ExceptionCheck()) { \
          printf("QtJambi: exception pending at %s, %d\n", __FILE__, __LINE__); \
          env->ExceptionDescribe(); \
      }
#  define QTJAMBI_DEBUG_TRACE(location) qtjambi_debug_trace(location, __FILE__, __LINE__);
#endif


class QVariant;
class QRect;
class QtJambiFunctionTable;

struct QtJambiSignalInfo
{
    jobject object;
    jmethodID methodId;
};

#ifdef JOBJECT_REFCOUNT
#  include <QtCore/QReadWriteLock>
#  include <QtCore/QWriteLocker>
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

        fprintf(stderr, qPrintable(s));
    }

#  define REF_JOBJECT jobjectRefCount(true)
#  define DEREF_JOBJECT jobjectRefCount(false)
#else
#  define REF_JOBJECT // noop
#  define DEREF_JOBJECT // noop
#endif // JOBJECT_REFCOUNT

QTJAMBI_EXPORT JNIEnv *qtjambi_current_environment();

struct QTJAMBI_EXPORT JObjectWrapper
{
    JObjectWrapper() : object(0)
    {
    }

    JObjectWrapper(const JObjectWrapper &wrapper)
    {
        operator=(wrapper);
    }

    JObjectWrapper(JNIEnv *env, jobject obj)
    {
        Q_ASSERT(env != 0);
        if (obj != 0)
            initialize(env, obj);
        else
            object = 0;
        REF_JOBJECT;
    }

    ~JObjectWrapper();


    void operator=(const JObjectWrapper &wrapper) {
        if (wrapper.object != 0) {
            initialize(qtjambi_current_environment(), wrapper.object);
        } else {
            object = 0;
        }
        REF_JOBJECT;
    }

    bool operator==(const JObjectWrapper &other) const;

    void initialize(JNIEnv *env, jobject obj);

    jobject object;
};
Q_DECLARE_METATYPE(JObjectWrapper)

inline void *qtjambi_from_jlong(jlong ptr)
{
    if (ptr != 0) {
        QtJambiLink *link = reinterpret_cast<QtJambiLink *>(ptr);
        return link->pointer();
    } else {
        return 0;
    }
}

typedef bool (*QtJambiPolymorphicHandler)(const void *object, char **class_name, char **package);

QTJAMBI_EXPORT void qtjambi_register_polymorphic_id(const char *lookup, QtJambiPolymorphicHandler handler);
QTJAMBI_EXPORT void qtjambi_resolve_polymorphic_id(const char *lookup, const void *object,
                                                   char **class_name, char **package);

QTJAMBI_EXPORT bool qtjambi_initialize_vm();
QTJAMBI_EXPORT bool qtjambi_destroy_vm();
extern "C" QTJAMBI_EXPORT void qtjambi_set_vm_location_override(const QString &location);

// To avoid binary incompatibilities, we keep the qtjambi_exception_check() function but
// override it with a macro.
QTJAMBI_EXPORT bool qtjambi_exception_check(JNIEnv *env, char *fileName, int lineNumber);
QTJAMBI_EXPORT bool qtjambi_exception_check(JNIEnv *env);
#define qtjambi_exception_check(ENV) qtjambi_exception_check(ENV, __FILE__, __LINE__)

QTJAMBI_EXPORT jclass qtjambi_find_class(JNIEnv *env, const char *qualifiedName);

QTJAMBI_EXPORT QVariant qtjambi_to_qvariant(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT jobject qtjambi_from_qvariant(JNIEnv *env, const QVariant &qt_variant);

QTJAMBI_EXPORT void *qtjambi_to_object(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT QObject *qtjambi_to_qobject(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT int qtjambi_to_enum(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT QString qtjambi_to_qstring(JNIEnv *env, jstring java_string);

QTJAMBI_EXPORT JObjectWrapper qtjambi_to_jobjectwrapper(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT jobject qtjambi_from_jobjectwrapper(JNIEnv *env, const JObjectWrapper &wrapper);

//QTJAMBI_EXPORT jobject qtjambi_from_qstyleoption(JNIEnv *env, const QStyleOption *so);

QTJAMBI_EXPORT void qtjambi_register_callbacks();

QTJAMBI_EXPORT void *qtjambi_to_interface(JNIEnv *env,
                                           QtJambiLink *link,
                                           const char *interface_name,
                                           const char *package_name,
                                           const char *function_name);

void qtjambi_connect_notify(JNIEnv *env, QObject *qobject, const QString &signal_name);

void qtjambi_disconnect_notify(JNIEnv *env, QObject *qobject, const QString &signal_name);

QTJAMBI_EXPORT void qtjambi_end_paint(JNIEnv *env, jobject widget);


inline void *qtjambi_to_interface(JNIEnv *env,
                                  jobject java_object,
                                  const char *interface_name,
                                  const char *package_name,
                                  const char *function_name)
{
    return qtjambi_to_interface(
        env,
        QtJambiLink::findLink(env, java_object),
        interface_name,
        package_name,
        function_name
    );
}

template <typename T>
inline jobjectArray qtjambi_from_array(JNIEnv *env, T *array,
                                       int size, char *className, char *packageName)
{
    if (array == 0)
        return 0;

    jclass clazz = resolveClass(env, className, packageName);
    QTJAMBI_EXCEPTION_CHECK(env);
    if (clazz == 0)
        return 0;

    jobjectArray returned = env->NewObjectArray(size, clazz, 0);
    if (returned != 0) {
        for (int i=0; i<size; ++i) {
            jobject java_object = qtjambi_from_object(env, array + i, className, packageName, true);
            env->SetObjectArrayElement(returned, i, java_object);
        }
    }

    return returned;
}

template <typename T>
inline jobjectArray qtjambi_from_interface_array(JNIEnv *env, T *array,
                                                 int size, char *interfaceName, char *className,
                                                 char *packageName)
{
    if (array == 0)
        return 0;

    jclass clazz = resolveClass(env, interfaceName, packageName);
    QTJAMBI_EXCEPTION_CHECK(env);
    if (clazz == 0)
        return 0;

    jobjectArray returned = env->NewObjectArray(size, clazz, 0);
    if (returned != 0) {
        for (int i=0; i<size; ++i) {
            jobject java_object = qtjambi_from_object(env, array[i], className, packageName, true);
            env->SetObjectArrayElement(returned, i, java_object);
        }
    }

    return returned;
}

QTJAMBI_EXPORT
jobject qtjambi_from_object(JNIEnv *env, const void *qt_object, char *className,
                            char *packageName, const char *lookupName, bool makeCopyOfValueTypes);

QTJAMBI_EXPORT
jobject qtjambi_from_object(JNIEnv *env, const void *qt_object, const char *className,
                            const char *packageName, bool makeCopyOfValueTypes);

QTJAMBI_EXPORT
jobject qtjambi_from_qobject(JNIEnv *env, QObject *qt_object, const char *className, const char *packageName);


// Convenience overloads so you won't have to remember / write the
// package string for QObjects and QWidgets
QTJAMBI_EXPORT jobject qtjambi_from_QObject(JNIEnv *env, QObject *qt_object);
QTJAMBI_EXPORT jobject qtjambi_from_QWidget(JNIEnv *env, QWidget *widget);

QTJAMBI_EXPORT jobject qtjambi_from_enum(JNIEnv *env, int qt_enum, const char *className);

QTJAMBI_EXPORT jobject qtjambi_from_flags(JNIEnv *env, int qt_flags, const char *className);

// QtEnumerator<T> -> int
QTJAMBI_EXPORT int qtjambi_to_enumerator(JNIEnv *env, jobject value);

QTJAMBI_EXPORT
jstring qtjambi_from_qstring(JNIEnv *env, const QString &s);

inline jstring qtjambi_from_qstring(JNIEnv *env, const QStringRef &s)
{
    return qtjambi_from_qstring(env, s.toString());
}

QTJAMBI_EXPORT
void qtjambi_invalidate_object(JNIEnv *env, jobject java_object, bool checkJavaOwnership = true);

QTJAMBI_EXPORT
void qtjambi_invalidate_collection(JNIEnv *env, jobject java_collection, bool checkJavaOwnership = true);

QTJAMBI_EXPORT
void qtjambi_invalidate_array(JNIEnv *env, jobjectArray java_array, bool checkJavaOwnership = true);

QTJAMBI_EXPORT
QtJambiLink *qtjambi_construct_qobject(JNIEnv *env, jobject java_object, QObject *qobject);

QTJAMBI_EXPORT
QtJambiLink *qtjambi_construct_object(JNIEnv *env, jobject java_object, void *object,
                                      int metaType = QMetaType::Void, const QString &java_name = QString(),
                                      bool created_by_java = false);

QTJAMBI_EXPORT
QtJambiLink *qtjambi_construct_object(JNIEnv *env, jobject java_object, void *,
                                    const char *className);

QTJAMBI_EXPORT
jobject qtjambi_to_tablearea(JNIEnv *env, int row, int column, int rowCount, int columnCount);

QTJAMBI_EXPORT
void qtjambi_from_tablearea(JNIEnv *env, jobject tableArea, int *row, int *column, int *rowCount, int *columnCount);

QTJAMBI_EXPORT
jobject qtjambi_to_cellatindex(JNIEnv *env, int row, int column, int rowCount, int columnCount, bool isSelected);

QTJAMBI_EXPORT
void qtjambi_from_cellatindex(JNIEnv *env, jobject tableArea, int *row, int *column, int *rowCount, int *columnCount, bool *isSelected);

QTJAMBI_EXPORT
jobject qtjambi_to_resolvedentity(JNIEnv *env, bool success, void *inputSource);

QTJAMBI_EXPORT
bool qtjambi_from_resolvedentity(JNIEnv *env, void *&inputSource, jobject resolvedEntity);

QTJAMBI_EXPORT
void *qtjambi_to_cpointer(JNIEnv *env, jobject java_object, int indirections);

QTJAMBI_EXPORT
jobject qtjambi_from_cpointer(JNIEnv *env, const void *qt_pointer, int type_id, int indirections);

QTJAMBI_EXPORT
jobject qtjambi_array_to_nativepointer(JNIEnv *env, jobjectArray array, int elementSize);

QTJAMBI_EXPORT QThread *qtjambi_to_thread(JNIEnv *env, jobject thread);
QTJAMBI_EXPORT jobject qtjambi_from_thread(JNIEnv *env, QThread *thread);
bool qtjambi_adopt_current_thread(void **args);

QTJAMBI_EXPORT QModelIndex qtjambi_to_QModelIndex(JNIEnv *env, jobject index);
QTJAMBI_EXPORT jobject qtjambi_from_QModelIndex(JNIEnv *env, const QModelIndex &index);

bool qtjambi_release_threads(JNIEnv *env);

QTJAMBI_EXPORT
QtJambiFunctionTable *qtjambi_setup_vtable(JNIEnv *env,
                                         jobject object,
                                         int inconsistentCount,
                                         const char **inconsistentNames,
                                         const char **inconsistentSignatures,
                                         int methodCount,
                                         const char **methodNames,
                                         const char **methodSignatures);

QTJAMBI_EXPORT QString qtjambi_class_name(JNIEnv *env, jclass java_class);
QTJAMBI_EXPORT QString qtjambi_object_class_name(JNIEnv *env, jobject java_object);

QTJAMBI_EXPORT void qtjambi_metacall(JNIEnv *env, QEvent *event);

QTJAMBI_EXPORT bool qtjambi_is_created_by_java(QObject *qobject);

QTJAMBI_EXPORT void qtjambi_debug_trace(const char *location, const char *file, int line);

// Boxing functions
inline jobject qtjambi_from_int(JNIEnv *env, int int_value) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveInteger();
    return env->NewObject(sc->Integer.class_ref, sc->Integer.constructor, int_value);
}


inline int qtjambi_to_int(JNIEnv *env, jobject int_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveInteger();
    return env->CallIntMethod(int_object, sc->Integer.intValue);
}


inline jobject qtjambi_from_double(JNIEnv *env, double double_value) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveDouble();
    return env->NewObject(sc->Double.class_ref, sc->Double.constructor, double_value);
}


inline double qtjambi_to_double(JNIEnv *env, jobject double_object) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveDouble();
    return env->CallDoubleMethod(double_object, sc->Double.doubleValue);
}

inline jobject qtjambi_from_boolean(JNIEnv *env, bool bool_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveBoolean();
    return env->NewObject(sc->Boolean.class_ref, sc->Boolean.constructor, bool_value);
}

inline bool qtjambi_to_boolean(JNIEnv *env, jobject bool_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveBoolean();
    return env->CallBooleanMethod(bool_object, sc->Boolean.booleanValue);
}

inline jlong qtjambi_to_long(JNIEnv *env, jobject long_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveLong();
    return env->CallLongMethod(long_object, sc->Long.longValue);
}

inline jobject qtjambi_from_long(JNIEnv *env, qint64 long_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveLong();
    return env->NewObject(sc->Long.class_ref, sc->Long.constructor, long_value);
}

inline jobject qtjambi_from_short(JNIEnv *env, short short_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveShort();
    return env->NewObject(sc->Short.class_ref, sc->Short.constructor, short_value);
}

inline jobject qtjambi_from_float(JNIEnv *env, float float_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveFloat();
    return env->NewObject(sc->Float.class_ref, sc->Float.constructor, float_value);
}

inline jobject qtjambi_from_byte(JNIEnv *env, char byte_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveByte();
    return env->NewObject(sc->Byte.class_ref, sc->Byte.constructor, byte_value);
}


inline jchar qtjambi_to_char(JNIEnv *env, jobject char_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveCharacter();
    return env->CallCharMethod(char_object, sc->Character.charValue);
}

inline jobject qtjambi_from_char(JNIEnv *env, jchar char_value)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveCharacter();
    return env->NewObject(sc->Character.class_ref, sc->Character.constructor, char_value);
}

inline float qtjambi_to_float(JNIEnv *env, jobject float_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveFloat();
    return env->CallFloatMethod(float_object, sc->Float.floatValue);
}

inline short qtjambi_to_short(JNIEnv *env, jobject short_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveShort();
    return env->CallShortMethod(short_object, sc->Short.shortValue);
}

inline jchar qtjambi_to_jchar(JNIEnv *env, jobject char_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveCharacter();
    return env->CallCharMethod(char_object, sc->Character.charValue);
}

inline jbyte qtjambi_to_byte(JNIEnv *env, jobject byte_object)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveByte();
    return env->CallByteMethod(byte_object, sc->Byte.byteValue);
}

// Container helpers...
inline jobject qtjambi_pair_new(JNIEnv *env, jobject first, jobject second) {
    StaticCache *sc = StaticCache::instance();
    sc->resolvePair();
    return env->NewObject(sc->Pair.class_ref, sc->Pair.constructor, first, second);
}


inline jobject qtjambi_pair_get(JNIEnv *env, jobject pair, int pos) {
    Q_ASSERT(pos == 0 || pos == 1);
    StaticCache *sc = StaticCache::instance();
    sc->resolvePair();
    if (pos == 0)
        return env->GetObjectField(pair, sc->Pair.first);
    else
        return env->GetObjectField(pair, sc->Pair.second);
}

inline jobject qtjambi_hashset_new(JNIEnv *env) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveHashSet();
    return env->NewObject(sc->HashSet.class_ref, sc->HashSet.constructor);
}

inline jobject qtjambi_hashmap_new(JNIEnv *env, int size) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveHashMap();
    return env->NewObject(sc->HashMap.class_ref, sc->HashMap.constructor, size);
}

inline jobject qtjambi_treemap_new(JNIEnv *env, int size) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveTreeMap();
    return env->NewObject(sc->TreeMap.class_ref, sc->TreeMap.constructor, size);
}

inline void qtjambi_map_put(JNIEnv *env, jobject map, jobject key, jobject val)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveMap();
    env->CallObjectMethod(map, sc->Map.put, key, val);
}

inline int qtjambi_map_size(JNIEnv *env, jobject map)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveMap();
    return env->CallIntMethod(map, sc->Map.size);
}

inline jobjectArray qtjambi_map_entryset_array(JNIEnv *env, jobject map)
{
    StaticCache *sc = StaticCache::instance();
    sc->resolveMap();
    jobject set = env->CallObjectMethod(map, sc->Map.entrySet);
    sc->resolveCollection();
    return (jobjectArray) env->CallObjectMethod(set, sc->Collection.toArray);
}

inline QPair<jobject, jobject> qtjambi_entryset_array_get(JNIEnv *env, jobjectArray array, int idx)
{
    jobject entry = env->GetObjectArrayElement(array, idx);

    StaticCache *sc = StaticCache::instance();
    sc->resolveMapEntry();
    jobject key = env->CallObjectMethod(entry, sc->MapEntry.getKey);
    jobject value = env->CallObjectMethod(entry, sc->MapEntry.getValue);

    return QPair<jobject, jobject>(key, value);
}

inline jobject qtjambi_arraylist_new(JNIEnv *env, int size) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveArrayList();
    return env->NewObject(sc->ArrayList.class_ref, sc->ArrayList.constructor, size);
}

inline jobject qtjambi_linkedlist_new(JNIEnv *env) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveLinkedList();
    return env->NewObject(sc->LinkedList.class_ref, sc->LinkedList.constructor);
}

inline jobject qtjambi_stack_new(JNIEnv *env) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveStack();
    return env->NewObject(sc->Stack.class_ref, sc->Stack.constructor);
}

inline void qtjambi_collection_add(JNIEnv *env, jobject list, jobject obj) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveCollection();
    env->CallObjectMethod(list, sc->Collection.add, obj);
}

inline jobjectArray qtjambi_collection_toArray(JNIEnv *env, jobject col) {
    StaticCache *sc = StaticCache::instance();
    sc->resolveCollection();
    return (jobjectArray) env->CallObjectMethod(col, sc->Collection.toArray);
}

QTJAMBI_EXPORT
void qtjambi_resolve_signals(JNIEnv *env,
                             jobject java_object,
                             QtJambiSignalInfo *infos,
                             int count,
                             char **names,
                             int *argument_counts);

QTJAMBI_EXPORT
bool qtjambi_connect_cpp_to_java(JNIEnv *,
                                 const QString &java_signal_name,
                                 QObject *sender,
                                 QObject *wrapper,
                                 const QString &java_class_name,
                                 const QString &signal_wrapper_prefix);

// ### QtJambiSignalInfo has to be passed as a copy, or we will crash whenever the
// slot deletes its sender.
QTJAMBI_EXPORT void qtjambi_call_java_signal(JNIEnv *env, QtJambiSignalInfo signal_info, jvalue *args);


QTJAMBI_EXPORT const QMetaObject *qtjambi_metaobject_for_class(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object);

QTJAMBI_EXPORT bool qtjambi_metaobject_is_dynamic(const QMetaObject *meta_object);

QTJAMBI_EXPORT QString qtjambi_enum_name_for_flags_name(JNIEnv *env, const QString &qualified_name);

QTJAMBI_EXPORT void qtjambi_register_variant_handler();

QTJAMBI_EXPORT jobject qtjambi_invoke_method(JNIEnv *env, jobject receiver, jmethodID methodId, jbyte returnType, QVarLengthArray<jvalue> argsArray);

QTJAMBI_EXPORT QVarLengthArray<jvalue> qtjambi_from_jobjectArray(JNIEnv *env, jobjectArray args, jintArray _cnvTypes, bool globalRefs = false);



#endif // QTJAMBI_CORE_H
