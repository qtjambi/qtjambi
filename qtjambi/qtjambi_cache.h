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

#ifndef QTJAMBI_CACHE_H
#define QTJAMBI_CACHE_H

#include "qtjambi_global.h"

#include <QtCore/QList>
#include <QtCore/QMutex>

class QtJambiFunctionTable;
class QString;

enum DeletionPolicy {
    DeletionPolicyNormal,
    DeletionPolicyDeleteInMainThread
};

QTJAMBI_EXPORT void registerQtToJava(const QString &qt_name, const QString &java_name);
QTJAMBI_EXPORT void registerJavaToQt(const QString &java_name, const QString &qt_name);
QTJAMBI_EXPORT void registerDestructor(const QString &java_name, PtrDestructorFunction destructor);
QTJAMBI_EXPORT void registerJavaSignature(const QString &qt_name, const QString &java_signature);
QTJAMBI_EXPORT void registerDeletionPolicy(const QString &java_name, DeletionPolicy policy);

QTJAMBI_EXPORT QString getQtName(const QString &java_name);
QTJAMBI_EXPORT QString getJavaName(const QString &qt_name);
QTJAMBI_EXPORT QString getJavaSignature(const QString &qt_name);
PtrDestructorFunction destructor(const QString &java_name);
DeletionPolicy deletionPolicy(const QString &java_name);

QTJAMBI_EXPORT jclass resolveClass(JNIEnv *env, const char *className, const char *package);
QTJAMBI_EXPORT jfieldID resolveField(JNIEnv *env, const char *fieldName, const char *signature, jclass clazz,
                      bool isStatic = false);

QTJAMBI_EXPORT jfieldID resolveField(JNIEnv *env, const char *fieldName, const char *signature,
                      const char *className, const char *package, bool isStatic = false);
QTJAMBI_EXPORT jmethodID resolveMethod(JNIEnv *env, const char *methodName, const char *signature, jclass clazz,
                        bool isStatic = false);
QTJAMBI_EXPORT jmethodID resolveMethod(JNIEnv *env, const char *methodName, const char *signature,
                        const char *className, const char *package, bool isStatic = false);

QtJambiFunctionTable *findFunctionTable(const QString &className);
void storeFunctionTable(const QString &className, QtJambiFunctionTable *table);
void removeFunctionTable(QtJambiFunctionTable *table);

jclass resolveClosestQtSuperclass(JNIEnv *env, jclass clazz);
jclass resolveClosestQtSuperclass(JNIEnv *env, const char *className, const char *package);


class StaticCachePrivate {
public:
    StaticCachePrivate() : lock(QMutex::Recursive) {}
    virtual ~StaticCachePrivate() { }

    QMutex lock;
};


#define DECLARE_RESOLVE_FUNCTIONS(type_name) \
public: \
    inline void resolve##type_name() { \
        d->lock.lock(); \
        if (type_name.class_ref) { \
            d->lock.unlock(); \
            return; \
        } \
        resolve##type_name##_internal(); \
        d->lock.unlock(); \
    } \
private: \
    void resolve##type_name##_internal()


struct QTJAMBI_EXPORT StaticCache
{
    struct {
        jclass class_ref;
        jmethodID constructor;
    } HashMap;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } HashSet;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } TreeMap;

    struct {
        jclass class_ref;
        jmethodID getKey;
        jmethodID getValue;
    } MapEntry;

    struct {
        jclass class_ref;
        jmethodID put;
        jmethodID size;
        jmethodID entrySet;
    } Map;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } ArrayList;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } LinkedList;

    struct {
        jclass class_ref;
        jmethodID add;
        jmethodID size;
        jmethodID toArray;
        jmethodID clear;
    } Collection;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } Stack;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID first;
        jfieldID second;
    } Pair;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID intValue;
    } Integer;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID doubleValue;
    } Double;

    struct {
        jclass class_ref;
        jmethodID getModifiers;
        jmethodID getDeclaringClass;
        jmethodID getName;
    } Method;

    struct {
        jclass class_ref;
        jmethodID isNative;
    } Modifier;

    struct {
        jclass class_ref;
        jmethodID fromNative;
        jmethodID constructor;
        jfieldID indirections;
        jfieldID ptr;
    } NativePointer;

    struct {
        jclass class_ref;
        jfieldID native_id;
        jmethodID disposed;
    } QtJambiObject;

    struct {
        jclass class_ref;
        jmethodID disconnect;
    } QSignalEmitter;

    struct {
        jclass class_ref;
        jmethodID getName;
        jmethodID getDeclaredMethods;
        jmethodID isEnum;
        jmethodID getEnumConstants;
    } Class;

    struct {
        jclass class_ref;
        jmethodID gc;
        jmethodID getProperty;
    } System;

    struct {
        jclass class_ref;
        jmethodID equals;
        jmethodID hashCode;
        jmethodID toString;
    } Object;

    struct {
        jclass class_ref;
        jmethodID newInstance;
        jmethodID addURL;
    } URLClassLoader;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } URL;

    struct {
        jclass class_ref;
        jmethodID loadClass;
    } ClassLoader;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID booleanValue;
        jfieldID field_FALSE;
        jfieldID field_TRUE;
    } Boolean;

    struct {
        jclass class_ref;
        jmethodID longValue;
        jmethodID constructor;
    } Long;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID floatValue;
    } Float;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID shortValue;
    } Short;

    struct {
        jclass class_ref;
        jmethodID charValue;
        jmethodID constructor;
    } Character;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jmethodID byteValue;
    } Byte;

    struct {
        jclass class_ref;
    } NullPointerException;

    struct {
        jclass class_ref;
    } String;

    struct {
        jclass class_ref;
        jfieldID inCppEmission;
        jmethodID connect;
        jmethodID connectSignalMethod;
        jmethodID removeConnection;
        jfieldID inJavaEmission;
    } AbstractSignal;

    struct {
        jclass class_ref;
    } QObject;

    struct {
        jclass class_ref;
        jmethodID findEmitMethod;
        jmethodID findGeneratedSuperclass;
        jmethodID isImplementedInJava;
        jmethodID lookupSignal;
        jmethodID lookupSlot;
        jmethodID writeSerializableJavaObject;
        jmethodID readSerializableJavaObject;
        jmethodID buildMetaData;
        jmethodID isGeneratedClass;
        jmethodID methodSignature;
        jmethodID methodSignature2;
        jmethodID signalParameters;
        jmethodID getEnumForQFlags;
        jmethodID signalMatchesSlot;
    } QtJambiInternal;

    struct {
        jclass class_ref;
        jfieldID metaData;
        jfieldID stringData;
        jfieldID signalsArray;
        jfieldID slotsArray;
        jfieldID propertyReadersArray;
        jfieldID propertyWritersArray;
        jfieldID propertyResettersArray;
        jfieldID propertyDesignablesArray;
        jfieldID extraDataArray;
        jfieldID originalSignatures;
    } MetaData;

    struct {
        jclass class_ref;
        jmethodID endPaint;
    } QtJambiGuiInternal;

    struct {
        jclass class_ref;
        jmethodID currentThread;
        jmethodID getContextClassLoader;
        jmethodID setContextClassLoader;
    } Thread;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID field_row;
        jfieldID field_column;
        jfieldID field_internalId;
        jfieldID field_model;
    } QModelIndex;

    struct {
        jclass class_ref;
        jmethodID value;
    } QtEnumerator;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID string;
        jfieldID position;
    } ValidationData;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID row;
        jfieldID column;
        jfieldID rowCount;
        jfieldID columnCount;
    } QTableArea;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID isSelected;
    } CellAtIndex;

    struct {
        jclass class_ref;
        jmethodID ordinal;
    } Enum;

    struct {
        jclass class_ref;
    } Qt;

    struct {
        jclass class_ref;
    } QFlags;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } QtProperty;

    struct {
        jclass class_ref;
        jmethodID map;
    } QtConcurrent_MapFunctor;

    struct {
        jclass class_ref;
        jmethodID map;
    } QtConcurrent_MappedFunctor;

    struct {
        jclass class_ref;
        jmethodID reduce;
        jmethodID defaultResult;
    } QtConcurrent_ReducedFunctor;

    struct {
        jclass class_ref;
        jmethodID filter;
    } QtConcurrent_FilteredFunctor;

    struct {
        jclass class_ref;
        jmethodID constructor;
    } QClassPathEngine;

    struct {
        jclass class_ref;
        jmethodID createWidget;
        jmethodID valuePropertyName;
    } QItemEditorCreatorBase;

    struct {
        jclass class_ref;
        jmethodID constructor;
        jfieldID error;
        jfieldID inputSource;
    } ResolvedEntity;

    DECLARE_RESOLVE_FUNCTIONS(AbstractSignal);
    DECLARE_RESOLVE_FUNCTIONS(ArrayList);
    DECLARE_RESOLVE_FUNCTIONS(Boolean);
    DECLARE_RESOLVE_FUNCTIONS(Byte);
    DECLARE_RESOLVE_FUNCTIONS(Character);
    DECLARE_RESOLVE_FUNCTIONS(Class);
    DECLARE_RESOLVE_FUNCTIONS(ClassLoader);
    DECLARE_RESOLVE_FUNCTIONS(Collection);
    DECLARE_RESOLVE_FUNCTIONS(Double);
    DECLARE_RESOLVE_FUNCTIONS(Float);
    DECLARE_RESOLVE_FUNCTIONS(HashMap);
    DECLARE_RESOLVE_FUNCTIONS(HashSet);
    DECLARE_RESOLVE_FUNCTIONS(Integer);
    DECLARE_RESOLVE_FUNCTIONS(LinkedList);
    DECLARE_RESOLVE_FUNCTIONS(Long);
    DECLARE_RESOLVE_FUNCTIONS(Map);
    DECLARE_RESOLVE_FUNCTIONS(MapEntry);
    DECLARE_RESOLVE_FUNCTIONS(Method);
    DECLARE_RESOLVE_FUNCTIONS(Modifier);
    DECLARE_RESOLVE_FUNCTIONS(NativePointer);
    DECLARE_RESOLVE_FUNCTIONS(NullPointerException);
    DECLARE_RESOLVE_FUNCTIONS(Object);
    DECLARE_RESOLVE_FUNCTIONS(Pair);
    DECLARE_RESOLVE_FUNCTIONS(QModelIndex);
    DECLARE_RESOLVE_FUNCTIONS(QObject);
    DECLARE_RESOLVE_FUNCTIONS(QSignalEmitter);
    DECLARE_RESOLVE_FUNCTIONS(QtEnumerator);
    DECLARE_RESOLVE_FUNCTIONS(QtJambiGuiInternal);
    DECLARE_RESOLVE_FUNCTIONS(QtJambiInternal);
    DECLARE_RESOLVE_FUNCTIONS(QtJambiObject);
    DECLARE_RESOLVE_FUNCTIONS(Short);
    DECLARE_RESOLVE_FUNCTIONS(Stack);
    DECLARE_RESOLVE_FUNCTIONS(String);
    DECLARE_RESOLVE_FUNCTIONS(System);
    DECLARE_RESOLVE_FUNCTIONS(Thread);
    DECLARE_RESOLVE_FUNCTIONS(TreeMap);
    DECLARE_RESOLVE_FUNCTIONS(URL);
    DECLARE_RESOLVE_FUNCTIONS(URLClassLoader);
    DECLARE_RESOLVE_FUNCTIONS(ValidationData);
    DECLARE_RESOLVE_FUNCTIONS(QTableArea);
    DECLARE_RESOLVE_FUNCTIONS(CellAtIndex);
    DECLARE_RESOLVE_FUNCTIONS(MetaData);
    DECLARE_RESOLVE_FUNCTIONS(Enum);
    DECLARE_RESOLVE_FUNCTIONS(Qt);
    DECLARE_RESOLVE_FUNCTIONS(QFlags);
    DECLARE_RESOLVE_FUNCTIONS(QtProperty);
    DECLARE_RESOLVE_FUNCTIONS(QtConcurrent_MapFunctor);
    DECLARE_RESOLVE_FUNCTIONS(QtConcurrent_MappedFunctor);
    DECLARE_RESOLVE_FUNCTIONS(QtConcurrent_ReducedFunctor);
    DECLARE_RESOLVE_FUNCTIONS(QtConcurrent_FilteredFunctor);
    DECLARE_RESOLVE_FUNCTIONS(QClassPathEngine);
    DECLARE_RESOLVE_FUNCTIONS(QItemEditorCreatorBase);
    DECLARE_RESOLVE_FUNCTIONS(ResolvedEntity);

public:
    static StaticCache *instance();

private:
    ~StaticCache();
    StaticCachePrivate *d;
};

#endif // QTJAMBI_CACHE_H
