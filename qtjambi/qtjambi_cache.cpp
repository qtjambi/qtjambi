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

#include "qtjambi_cache.h"
#include <qtjambifunctiontable.h>
#include <qtjambitypemanager.h>
#include <qtjambilink.h>

#include <QtCore/QHash>
#include <QtCore/QReadWriteLock>
#include <QtCore/QByteArray>

#include <QtCore/QDebug>

QList<StaticCache *> StaticCache::m_caches;

// #define QTJAMBI_NOCACHE
// #define QTJAMBI_COUNTCACHEMISSES(T) cacheMisses(T)

#ifdef QTJAMBI_COUNTCACHEMISSES
static void cacheMisses(const char *s)
{
    static int count = 0;

    qDebug("Total # of cache misses: %d : '%s'", ++count, s);
}
#else
# define QTJAMBI_COUNTCACHEMISSES(T)
#endif

Q_GLOBAL_STATIC(QReadWriteLock, gStaticLock);

uint qHash(const char *p)
{
    uint h = 0;
    uint g;

    while (*p != 0) {
        h = (h << 4) + *p++;
        if ((g = (h & 0xf0000000)) != 0)
            h ^= g >> 23;
        h &= ~g;
    }
    return h;
}
#define FUNC ^


typedef QHash<QString, QString> NameHash;
Q_GLOBAL_STATIC(QReadWriteLock, gJavaNameHashLock);
Q_GLOBAL_STATIC(NameHash, gJavaNameHash);
void registerQtToJava(const QString &qt_name, const QString &java_name)
{
    QWriteLocker locker(gJavaNameHashLock());
    gJavaNameHash()->insert(qt_name, java_name);
}
QString getJavaName(const QString &qt_name)
{
    QReadLocker locker(gJavaNameHashLock());
    return gJavaNameHash()->value(qt_name, QString());
}

Q_GLOBAL_STATIC(QReadWriteLock, gQtNameHashLock);
Q_GLOBAL_STATIC(NameHash, gQtNameHash);
void registerJavaToQt(const QString &java_name, const QString &qt_name)
{
    QWriteLocker locker(gQtNameHashLock());
    gQtNameHash()->insert(java_name, qt_name);
}

QString getQtName(const QString &java_name)
{
    QReadLocker locker(gQtNameHashLock());
    return gQtNameHash()->value(java_name, QString());

}

/*******************************************************************************
 * Class Cache
 */
struct class_id
{
    const char *className;
    const char *package;
    JNIEnv *env;
};

typedef QHash<class_id, jclass> ClassIdHash ;
Q_GLOBAL_STATIC(ClassIdHash, gClassHash);

inline bool operator==(const class_id &id1, const class_id &id2)
{
    return (!strcmp(id1.className, id2.className)
            && !strcmp(id1.package, id2.package)
            && (id1.env == id2.env));
}
uint qHash(const class_id &id) { return qHash(id.className) FUNC qHash(id.package) FUNC qHash(id.env); }

jclass resolveClass(JNIEnv *env, const char *className, const char *package)
{
    jclass returned = 0;
#ifndef QTJAMBI_NOCACHE
    class_id key = { className, package, env };

    {
        QReadLocker locker(gStaticLock());
        returned = gClassHash()->value(key, 0);
    }

    if (returned == 0) {
        QWriteLocker locker(gStaticLock());

        QTJAMBI_COUNTCACHEMISSES(className);

#endif // QTJAMBI_NOCACHE

        QByteArray ba(package);
        ba += className;

        returned = env->FindClass(ba.constData());

#ifndef QTJAMBI_NOCACHE
        if (returned != 0 && !gClassHash()->contains(key)) {
            char *tmp = new char[strlen(className) + 1];
            qstrcpy(tmp, className);
            key.className = tmp;

            tmp = new char[strlen(package) + 1];
            qstrcpy(tmp, package);
            key.package = tmp;

            gClassHash()->insert(key, (jclass) env->NewGlobalRef(returned));
        }
    }
#endif // QTJAMBI_NOCACHE

    return returned;
}

/*******************************************************************************
 * Field Cache
 */
struct field_id
{
    const char *fieldName;
    const char *className;
    const char *package;
    bool isStatic;
    JNIEnv *env;
};

typedef QHash<field_id, jfieldID> FieldIdHash ;
Q_GLOBAL_STATIC(FieldIdHash, gFieldHash);

inline bool operator==(const field_id &id1, const field_id &id2)
{
    return (!strcmp(id1.fieldName, id2.fieldName)
            && !strcmp(id1.className, id2.className)
            && !strcmp(id1.package, id2.package)
            && (id1.env == id2.env)
            && (id1.isStatic == id2.isStatic));
}
uint qHash(const field_id &id)
{
    return qHash(id.fieldName) FUNC qHash(id.className) FUNC qHash(id.package)
           FUNC qHash(id.env) FUNC int(id.isStatic);
}

jfieldID resolveField(JNIEnv *env, const char *fieldName, const char *signature,
                      const char *className, const char *package, bool isStatic)
{
    jfieldID returned = 0;
#ifndef QTJAMBI_NOCACHE
    field_id key = { fieldName, className, package, isStatic, env };

    {
        QReadLocker locker(gStaticLock());
        returned = gFieldHash()->value(key, 0);
    }

    if (returned == 0) {
#endif // QTJAMBI_NOCACHE
        jclass clazz = resolveClass(env, className, package);
#ifndef QTJAMBI_NOCACHE

        {
            QWriteLocker locker(gStaticLock());

            QTJAMBI_COUNTCACHEMISSES(fieldName);

#endif // QTJAMBI_NOCACHE
            if (!isStatic)
                returned = env->GetFieldID(clazz, fieldName, signature);
            else
                returned = env->GetStaticFieldID(clazz, fieldName, signature);
#ifndef QTJAMBI_NOCACHE

            if (returned != 0 && !gFieldHash()->contains(key)) {
                char *tmp = new char[strlen(fieldName) + 1];
                qstrcpy(tmp, fieldName);
                key.fieldName = tmp;

                tmp = new char[strlen(className) + 1];
                qstrcpy(tmp, className);
                key.className = tmp;

                tmp = new char[strlen(package) + 1];
                qstrcpy(tmp, package);
                key.package = tmp;

                gFieldHash()->insert(key, returned);
            }
        }
    }
#endif // QTJAMBI_NOCACHE
    return returned;
}

jfieldID resolveField(JNIEnv *env, const char *fieldName, const char *signature,
                      jclass clazz, bool isStatic)
{
    QString qualifiedName = QtJambiLink::nameForClass(env, clazz).replace(QLatin1Char('.'), QLatin1Char('/'));
    QByteArray className = QtJambiTypeManager::className(qualifiedName).toUtf8();
    QByteArray package = QtJambiTypeManager::package(qualifiedName).toUtf8();

    return resolveField(env, fieldName, signature, className.constData(), package.constData(), isStatic);
}

/*******************************************************************************
 * Method Cache
 */
struct method_id
{
    const char *methodName;
    const char *signature;
    const char *className;
    const char *package;
    bool isStatic;
    JNIEnv *env;
};

typedef QHash<method_id, jmethodID> MethodIdHash ;
Q_GLOBAL_STATIC(MethodIdHash, gMethodHash);

inline bool operator==(const method_id &id1, const method_id &id2)
{
    return (!strcmp(id1.methodName, id2.methodName)
            && !strcmp(id1.signature, id2.signature)
            && !strcmp(id1.className, id2.className)
            && !strcmp(id1.package, id2.package)
            && (id1.isStatic == id2.isStatic)
            && (id1.env == id2.env));
}
uint qHash(const method_id &id)
{
    return qHash(id.methodName)
           FUNC qHash(id.signature)
           FUNC qHash(id.className)
           FUNC qHash(id.package)
           FUNC qHash(id.env)
           FUNC int(id.isStatic);
}

jmethodID resolveMethod(JNIEnv *env, const char *methodName, const char *signature, const char *className,
                        const char *package, bool isStatic)
{
    jmethodID returned = 0;
#ifndef QTJAMBI_NOCACHE
    method_id key = { methodName, signature, className, package, isStatic, env };

    {
        QReadLocker locker(gStaticLock());
        returned = gMethodHash()->value(key, 0);
    }

    if (returned == 0) {
#endif
        jclass clazz = resolveClass(env, className, package);
#ifndef QTJAMBI_NOCACHE

        if (clazz != 0) {
            QWriteLocker locker(gStaticLock());

            QTJAMBI_COUNTCACHEMISSES(methodName);

#endif // QTJAMBI_NOCACHE
            if (!isStatic)
                returned = env->GetMethodID(clazz, methodName, signature);
            else
                returned = env->GetStaticMethodID(clazz, methodName, signature);
#ifndef QTJAMBI_NOCACHE

            if (returned != 0 && !gMethodHash()->contains(key)) {
                char *tmp = new char[strlen(methodName) + 1];
                qstrcpy(tmp, methodName);
                key.methodName = tmp;

                tmp = new char[strlen(signature) + 1];
                qstrcpy(tmp, signature);
                key.signature = tmp;

                tmp = new char[strlen(className) + 1];
                qstrcpy(tmp, className);
                key.className = tmp;

                tmp = new char[strlen(package) + 1];
                qstrcpy(tmp, package);
                key.package = tmp;

                gMethodHash()->insert(key, returned);
            }
        }
    }
#endif // QTJAMBI_NOCACHE

    return returned;
}

jmethodID resolveMethod(JNIEnv *env, const char *methodName, const char *signature, jclass clazz,
                        bool isStatic)
{
    QString qualifiedName = QtJambiLink::nameForClass(env, clazz).replace(QLatin1Char('.'), QLatin1Char('/'));
    QByteArray className = QtJambiTypeManager::className(qualifiedName).toUtf8();
    QByteArray package = QtJambiTypeManager::package(qualifiedName).toUtf8();

    return resolveMethod(env, methodName, signature, className, package, isStatic);
}

/*******************************************************************************
 * Closest superclass in Qt Cache
 */

struct closestsuperclass_id
{
    const char *className;
    const char *package;
};

inline bool operator==(const closestsuperclass_id &id1, const closestsuperclass_id &id2)
{
    return (!strcmp(id1.className, id2.className)
            && !strcmp(id1.package, id2.package));
}
uint qHash(const closestsuperclass_id &id) { return qHash(id.className) FUNC qHash(id.package); }


jclass resolveClosestQtSuperclass(JNIEnv *env, jclass clazz)
{
    QString qualifiedName = QtJambiLink::nameForClass(env, clazz).replace(QLatin1Char('.'), QLatin1Char('/'));
    QByteArray className = QtJambiTypeManager::className(qualifiedName).toUtf8();
    QByteArray package = QtJambiTypeManager::package(qualifiedName).toUtf8();

    return resolveClosestQtSuperclass(env, className.constData(), package.constData());
}

typedef QHash<closestsuperclass_id, jclass> ClassHash;
Q_GLOBAL_STATIC(ClassHash, gQtSuperclassHash);
jclass resolveClosestQtSuperclass(JNIEnv *env, const char *className, const char *package)
{
    closestsuperclass_id key = { className, package };

    jclass returned = 0;
    {
        QReadLocker locker(gStaticLock());
        returned = gQtSuperclassHash()->value(key, 0);
    }

    if (returned == 0) {
        jclass clazz = resolveClass(env, className, package);

        // Check if key is a Qt class
        if (clazz != 0) {
            jmethodID methodId = resolveMethod(env, "getName", "()Ljava/lang/String;",
                "Class", "java/lang/", false);

            if (methodId != 0) {
                jstring className = (jstring) env->CallObjectMethod(clazz, methodId);
                if (QtJambiTypeManager::jstringToQString(env, className).startsWith("com.trolltech."))
                    returned = clazz;
            }
        }

        // If not, try the superclass recursively
        if (returned == 0 && clazz != 0) {
            jclass superKey = env->GetSuperclass(clazz);
            if (superKey != 0) {
                returned = resolveClosestQtSuperclass(env, superKey);
            }
        }

        if (returned != 0) {
            QWriteLocker locker(gStaticLock());

            if (!gQtSuperclassHash()->contains(key)) {
                QTJAMBI_COUNTCACHEMISSES(className);

                char *tmp = new char[strlen(className) + 1];
                qstrcpy(tmp, className);
                key.className = tmp;

                tmp = new char[strlen(package) + 1];
                qstrcpy(tmp, package);
                key.package = tmp;

                gQtSuperclassHash()->insert(key, (jclass) env->NewGlobalRef(returned));
            }
        }
    }

    return returned;
}

/*******************************************************************************
 * Function Table Cache
 */
QHash<QString, QtJambiFunctionTable *> functionTableCache;

QtJambiFunctionTable *findFunctionTable(const QString &className)
{
    QReadLocker locker(gStaticLock());
    QtJambiFunctionTable *table = functionTableCache.value(className);
    return table;
}

void storeFunctionTable(const QString &className, QtJambiFunctionTable *table)
{
    QWriteLocker locker(gStaticLock());
    functionTableCache.insert(className, table);
}

void removeFunctionTable(QtJambiFunctionTable *table)
{
    QWriteLocker locker(gStaticLock());
    functionTableCache.remove(table->className());
}


#define ref_class(x) (jclass) env->NewGlobalRef((jobject) x);

#define IMPLEMENT_RESOLVE_DEFAULT_FUNCTION(structName, qualifiedClassName, constructorSignature) \
    void StaticCache::resolve##structName##_internal() {                                        \
        Q_ASSERT(!structName.class_ref);                                                      \
        structName.class_ref = ref_class(env->FindClass(qualifiedClassName));                 \
        Q_ASSERT(structName.class_ref);                                                       \
        structName.constructor =                                                              \
            env->GetMethodID(structName.class_ref, "<init>", constructorSignature);           \
        Q_ASSERT(structName.constructor);                                                     \
}


IMPLEMENT_RESOLVE_DEFAULT_FUNCTION(HashSet, "java/util/HashSet", "()V");


void StaticCache::resolveArrayList_internal()
{
    Q_ASSERT(!ArrayList.class_ref);

    ArrayList.class_ref = ref_class(env->FindClass("java/util/ArrayList"));
    Q_ASSERT(ArrayList.class_ref);

    ArrayList.constructor = env->GetMethodID(ArrayList.class_ref, "<init>", "(I)V");
    Q_ASSERT(ArrayList.constructor);
}

void StaticCache::resolveStack_internal()
{
    Q_ASSERT(!Stack.class_ref);

    Stack.class_ref = ref_class(env->FindClass("java/util/Stack"));
    Q_ASSERT(Stack.class_ref);

    Stack.constructor = env->GetMethodID(Stack.class_ref, "<init>", "()V");
    Q_ASSERT(Stack.constructor);
}

void StaticCache::resolveLinkedList_internal()
{
    Q_ASSERT(!LinkedList.class_ref);

    LinkedList.class_ref = ref_class(env->FindClass("java/util/LinkedList"));
    Q_ASSERT(LinkedList.class_ref);

    LinkedList.constructor = env->GetMethodID(LinkedList.class_ref, "<init>", "()V");
    Q_ASSERT(LinkedList.constructor);
}

void StaticCache::resolveMap_internal()
{
    Q_ASSERT(!Map.class_ref);

    Map.class_ref = ref_class(env->FindClass("java/util/Map"));
    Q_ASSERT(Map.class_ref);

    Map.put = env->GetMethodID(Map.class_ref, "put",
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    Q_ASSERT(Map.put);

    Map.size = env->GetMethodID(Map.class_ref, "size", "()I");
    Q_ASSERT(Map.size);

    Map.entrySet = env->GetMethodID(Map.class_ref, "entrySet", "()Ljava/util/Set;");
    Q_ASSERT(Map.entrySet);
}

void StaticCache::resolveMapEntry_internal()
{
    Q_ASSERT(!MapEntry.class_ref);

    MapEntry.class_ref = ref_class(env->FindClass("java/util/Map$Entry"));
    Q_ASSERT(MapEntry.class_ref);

    MapEntry.getKey = env->GetMethodID(MapEntry.class_ref, "getKey", "()Ljava/lang/Object;");
    Q_ASSERT(MapEntry.getKey);

    MapEntry.getValue = env->GetMethodID(MapEntry.class_ref, "getValue", "()Ljava/lang/Object;");
    Q_ASSERT(MapEntry.getValue);
}

void StaticCache::resolveHashMap_internal()
{
    Q_ASSERT(!HashMap.class_ref);

    HashMap.class_ref = ref_class(env->FindClass("java/util/HashMap"));
    Q_ASSERT(HashMap.class_ref);

    HashMap.constructor = env->GetMethodID(HashMap.class_ref, "<init>", "(I)V");
    Q_ASSERT(HashMap.constructor);
}

void StaticCache::resolveTreeMap_internal()
{
    Q_ASSERT(!TreeMap.class_ref);

    TreeMap.class_ref = ref_class(env->FindClass("java/util/TreeMap"));
    Q_ASSERT(TreeMap.class_ref);

    TreeMap.constructor = env->GetMethodID(TreeMap.class_ref, "<init>", "()V");
    Q_ASSERT(TreeMap.constructor);
}

void StaticCache::resolveNullPointerException_internal()
{
    Q_ASSERT(!NullPointerException.class_ref);

    NullPointerException.class_ref = ref_class(env->FindClass("java/lang/NullPointerException"));
    Q_ASSERT(NullPointerException.class_ref);
}

void StaticCache::resolveCollection_internal()
{
    Q_ASSERT(!Collection.class_ref);

    Collection.class_ref = ref_class(env->FindClass("java/util/Collection"));
    Q_ASSERT(Collection.class_ref);

    Collection.add = env->GetMethodID(Collection.class_ref, "add", "(Ljava/lang/Object;)Z");
    Collection.size = env->GetMethodID(Collection.class_ref, "size", "()I");
    Collection.toArray = env->GetMethodID(Collection.class_ref, "toArray", "()[Ljava/lang/Object;");

    Q_ASSERT(Collection.add);
    Q_ASSERT(Collection.size);
    Q_ASSERT(Collection.toArray);
}


void StaticCache::resolvePair_internal()
{
    Q_ASSERT(!Pair.class_ref);

    Pair.class_ref = ref_class(env->FindClass("com/trolltech/qt/QPair"));
    Q_ASSERT(Pair.class_ref);

    Pair.constructor = env->GetMethodID(Pair.class_ref, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    Pair.first = env->GetFieldID(Pair.class_ref, "first", "Ljava/lang/Object;");
    Pair.second = env->GetFieldID(Pair.class_ref, "second", "Ljava/lang/Object;");

    Q_ASSERT(Pair.constructor);
    Q_ASSERT(Pair.first);
    Q_ASSERT(Pair.second);
}


void StaticCache::resolveInteger_internal()
{
    Q_ASSERT(!Integer.class_ref);

    Integer.class_ref = ref_class(env->FindClass("java/lang/Integer"));
    Q_ASSERT(Integer.class_ref);

    Integer.constructor = env->GetMethodID(Integer.class_ref, "<init>", "(I)V");
    Integer.intValue = env->GetMethodID(Integer.class_ref, "intValue", "()I");

    Q_ASSERT(Integer.constructor);
    Q_ASSERT(Integer.intValue);
}


void StaticCache::resolveDouble_internal()
{
    Q_ASSERT(!Double.class_ref);

    Double.class_ref = ref_class(env->FindClass("java/lang/Double"));
    Q_ASSERT(Double.class_ref);

    Double.constructor = env->GetMethodID(Double.class_ref, "<init>", "(D)V");
    Double.doubleValue = env->GetMethodID(Double.class_ref, "doubleValue", "()D");

    Q_ASSERT(Double.constructor);
    Q_ASSERT(Double.doubleValue);
}


void StaticCache::resolveMethod_internal()
{
    Q_ASSERT(!Method.class_ref);

    Method.class_ref = ref_class(env->FindClass("java/lang/reflect/Method"));
    Q_ASSERT(Method.class_ref);

    Method.getDeclaringClass = env->GetMethodID(Method.class_ref, "getDeclaringClass",
                                                "()Ljava/lang/Class;");
    Method.getModifiers = env->GetMethodID(Method.class_ref, "getModifiers", "()I");
    Method.getName = env->GetMethodID(Method.class_ref, "getName", "()Ljava/lang/String;");

    Q_ASSERT(Method.getModifiers);
    Q_ASSERT(Method.getDeclaringClass);
    Q_ASSERT(Method.getName);
}


void StaticCache::resolveModifier_internal()
{
    Q_ASSERT(!Modifier.class_ref);

    Modifier.class_ref = ref_class(env->FindClass("java/lang/reflect/Modifier"));
    Q_ASSERT(Modifier.class_ref);

    Modifier.isNative = env->GetStaticMethodID(Modifier.class_ref, "isNative", "(I)Z");

    Q_ASSERT(Modifier.isNative);
}

void StaticCache::resolveObject_internal()
{
    Q_ASSERT(!Object.class_ref);

    Object.class_ref = ref_class(env->FindClass("java/lang/Object"));
    Q_ASSERT(Object.class_ref);

    Object.equals = env->GetMethodID(Object.class_ref, "equals", "(Ljava/lang/Object;)Z");
    Object.toString = env->GetMethodID(Object.class_ref, "toString", "()Ljava/lang/String;");
    Q_ASSERT(Object.equals);
    Q_ASSERT(Object.toString);
}


void StaticCache::resolveNativePointer_internal()
{
    Q_ASSERT(!NativePointer.class_ref);

    NativePointer.class_ref = ref_class(env->FindClass("com/trolltech/qt/QNativePointer"));
    Q_ASSERT(NativePointer.class_ref);

    NativePointer.fromNative = env->GetStaticMethodID(NativePointer.class_ref,
                                                      "fromNative",
                                                      "(JII)Lcom/trolltech/qt/QNativePointer;");
    NativePointer.constructor = env->GetMethodID(NativePointer.class_ref, "<init>", "(III)V");
    NativePointer.indirections = env->GetFieldID(NativePointer.class_ref, "m_indirections", "I");
    NativePointer.ptr = env->GetFieldID(NativePointer.class_ref, "m_ptr", "J");

    Q_ASSERT(NativePointer.fromNative);
    Q_ASSERT(NativePointer.indirections);
    Q_ASSERT(NativePointer.ptr);
}


void StaticCache::resolveQtObject_internal()
{
    Q_ASSERT(!QtObject.class_ref);

    QtObject.class_ref = ref_class(env->FindClass("com/trolltech/qt/QtObject"));
    Q_ASSERT(QtObject.class_ref);

    QtObject.native_id = env->GetFieldID(QtObject.class_ref, "native__id", "J");
    Q_ASSERT(QtObject.native_id);

    QtObject.disposed = env->GetMethodID(QtObject.class_ref, "disposed", "()V");
}

void StaticCache::resolveBoolean_internal()
{
    Q_ASSERT(!Boolean.class_ref);

    Boolean.class_ref = ref_class(env->FindClass("java/lang/Boolean"));
    Q_ASSERT(Boolean.class_ref);

    Boolean.constructor = env->GetMethodID(Boolean.class_ref, "<init>", "(Z)V");
    Q_ASSERT(Boolean.constructor);

    Boolean.booleanValue = env->GetMethodID(Boolean.class_ref, "booleanValue", "()Z");
    Q_ASSERT(Boolean.booleanValue);

    Boolean.field_FALSE = env->GetStaticFieldID(Boolean.class_ref, "FALSE", "Ljava/lang/Boolean;");
    Q_ASSERT(Boolean.field_FALSE);

    Boolean.field_TRUE = env->GetStaticFieldID(Boolean.class_ref, "TRUE", "Ljava/lang/Boolean;");
    Q_ASSERT(Boolean.field_TRUE);
}

void StaticCache::resolveLong_internal()
{
    Q_ASSERT(!Long.class_ref);

    Long.class_ref = ref_class(env->FindClass("java/lang/Long"));
    Q_ASSERT(Long.class_ref);

    Long.longValue = env->GetMethodID(Long.class_ref, "longValue", "()J");
    Q_ASSERT(Long.longValue);

    Long.constructor = env->GetMethodID(Long.class_ref, "<init>", "(J)V");
    Q_ASSERT(Long.constructor);
}

void StaticCache::resolveFloat_internal()
{
    Q_ASSERT(!Float.class_ref);

    Float.class_ref = ref_class(env->FindClass("java/lang/Float"));
    Q_ASSERT(Float.class_ref);

    Float.floatValue = env->GetMethodID(Float.class_ref, "floatValue", "()F");
    Q_ASSERT(Float.floatValue);
}

void StaticCache::resolveShort_internal()
{
    Q_ASSERT(!Short.class_ref);

    Short.class_ref = ref_class(env->FindClass("java/lang/Short"));
    Q_ASSERT(Short.class_ref);

    Short.shortValue = env->GetMethodID(Short.class_ref, "shortValue", "()S");
    Q_ASSERT(Short.shortValue);
}

void StaticCache::resolveByte_internal()
{
    Q_ASSERT(!Byte.class_ref);

    Byte.class_ref = ref_class(env->FindClass("java/lang/Byte"));
    Q_ASSERT(Byte.class_ref);

    Byte.byteValue = env->GetMethodID(Byte.class_ref, "byteValue", "()B");
    Q_ASSERT(Byte.byteValue);
}

void StaticCache::resolveCharacter_internal()
{
    Q_ASSERT(!Character.class_ref);

    Character.class_ref = ref_class(env->FindClass("java/lang/Character"));
    Q_ASSERT(Character.class_ref);

    Character.charValue = env->GetMethodID(Character.class_ref, "charValue", "()C");
    Q_ASSERT(Character.charValue);
}

void StaticCache::resolveClass_internal()
{
    Q_ASSERT(!Class.class_ref);

    Class.class_ref = ref_class(env->FindClass("java/lang/Class"));
    Q_ASSERT(Class.class_ref);

    Class.getName = env->GetMethodID(Class.class_ref, "getName", "()Ljava/lang/String;");

    Q_ASSERT(Class.getName);

    Class.getDeclaredMethods = env->GetMethodID(Class.class_ref, "getDeclaredMethods",
        "()[Ljava/lang/reflect/Method;");
    Q_ASSERT(Class.getDeclaredMethods);
}


void StaticCache::resolveSystem_internal()
{
    Q_ASSERT(!System.class_ref);

    System.class_ref = ref_class(env->FindClass("java/lang/System"));
    Q_ASSERT(System.class_ref);

    System.gc = env->GetStaticMethodID(System.class_ref, "gc", "()V");

    Q_ASSERT(System.gc);
}


void StaticCache::resolveQtJambiUtils_internal()
{
    Q_ASSERT(!QtJambiUtils.class_ref);

    QtJambiUtils.class_ref = ref_class(env->FindClass("com/trolltech/qt/QtJambiUtils"));
    Q_ASSERT(QtJambiUtils.class_ref);

    QtJambiUtils.isImplementedInJava =
        env->GetStaticMethodID(QtJambiUtils.class_ref, "isImplementedInJava",
                               "(Ljava/lang/reflect/Method;)Z");

    Q_ASSERT(QtJambiUtils.isImplementedInJava);
}


void StaticCache::resolveQObject_internal()
{
    Q_ASSERT(!QObject.class_ref);

    QObject.class_ref = ref_class(env->FindClass("com/trolltech/qt/core/QObject"));
    Q_ASSERT(QObject.class_ref);

    QObject.javaConnectNotify = env->GetMethodID(QObject.class_ref, "__qt_javaConnectNotify",
        "(Ljava/lang/String;I)V");
    Q_ASSERT(QObject.javaConnectNotify);
    QObject.javaDisconnectNotify = env->GetMethodID(QObject.class_ref, "__qt_javaDisconnectNotify",
        "(Ljava/lang/String;I)V");
    Q_ASSERT(QObject.javaDisconnectNotify);
}

void StaticCache::resolveAbstractSignal_internal()
{
    Q_ASSERT(!AbstractSignal.class_ref);

    AbstractSignal.class_ref = ref_class(env->FindClass("com/trolltech/qt/core/QObject$AbstractSignal"));
    Q_ASSERT(AbstractSignal.class_ref);

    AbstractSignal.m_in_cpp_emission = env->GetFieldID(AbstractSignal.class_ref, "m_in_cpp_emission", "Z");
}

void StaticCache::resolveString_internal()
{
    Q_ASSERT(!String.class_ref);

    String.class_ref = ref_class(env->FindClass("java/lang/String"));
    Q_ASSERT(String.class_ref);
}

void StaticCache::resolveThread_internal()
{
    Q_ASSERT(!Thread.class_ref);

    Thread.class_ref = ref_class(env->FindClass("java/lang/Thread"));
    Q_ASSERT(Thread.class_ref);

    Thread.currentThread = env->GetStaticMethodID(Thread.class_ref, "currentThread", "()Ljava/lang/Thread;");
    Q_ASSERT(Thread.currentThread);
}

void StaticCache::resolveQModelIndex_internal()
{
    Q_ASSERT(!QModelIndex.class_ref);

    QModelIndex.class_ref = ref_class(env->FindClass("com/trolltech/qt/core/QModelIndex"));
    Q_ASSERT(QModelIndex.class_ref);

    QModelIndex.constructor = env->GetMethodID(QModelIndex.class_ref,
                                               "<init>",
                                               "(IIJLcom/trolltech/qt/core/QAbstractItemModel;)V");
    Q_ASSERT(QModelIndex.constructor);

    QModelIndex.field_row = env->GetFieldID(QModelIndex.class_ref, "row", "I");
    QModelIndex.field_column = env->GetFieldID(QModelIndex.class_ref, "column", "I");
    QModelIndex.field_internalId = env->GetFieldID(QModelIndex.class_ref, "internalId", "J");
    QModelIndex.field_model = env->GetFieldID(QModelIndex.class_ref, "model",
                                              "Lcom/trolltech/qt/core/QAbstractItemModel;");
    Q_ASSERT(QModelIndex.field_row);
    Q_ASSERT(QModelIndex.field_column);
    Q_ASSERT(QModelIndex.field_internalId);
    Q_ASSERT(QModelIndex.field_model);
}

void StaticCache::resolveQtEnumerator_internal()
{
    Q_ASSERT(!QtEnumerator.class_ref);

    QtEnumerator.class_ref = ref_class(env->FindClass("com/trolltech/qt/QtEnumerator"));
    Q_ASSERT(QtEnumerator.class_ref);

    QtEnumerator.value = env->GetMethodID(QtEnumerator.class_ref, "value", "()I");
    Q_ASSERT(QtEnumerator.value);

}
