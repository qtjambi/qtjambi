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

#ifndef QTJAMBITYPEMANAGER_H
#define QTJAMBITYPEMANAGER_H

#include "qtjambi_global.h"

#include <QtCore/QHash>
#include <QtCore/QString>
#include <QtCore/QMetaType>
#include <QtCore/QReadWriteLock>
#include <QtCore/QVector>

class QtJambiTypeManager
{
public:
    enum Type {
        None = 0,
        Primitive       = 0x0001,
        Integer         = 0x0002,
        Long            = 0x0004,
        Boolean         = 0x0008,
        Float           = 0x0010,
        Double          = 0x0020,
        Short           = 0x0040,
        Byte            = 0x0080,
        Char            = 0x0100,

        QObjectSubclass = 0x0200,
        Object          = 0x0400,

        NativePointer   = 0x0800,

        Value           = 0x1000,
        String          = 0x2000,
        QtClass         = 0x4000,
        Enum            = 0x8000,

        TypeMask = Integer + Long + Boolean + Float + Double + Short + Byte + Char
    };

    enum VariableContext {
        ReturnType,
        ArgumentType
    };



    QtJambiTypeManager(JNIEnv *env);
    QtJambiTypeManager(JNIEnv *env, bool convertEnums);
    virtual ~QtJambiTypeManager();

    // Some convenience functions
    Type typeIdOfExternal(const QString &className, const QString &package) const;
    Type typeIdOfInternal(const QString &internalType) const;

    inline static void *jlongToPtr(jlong id);
    inline static jlong ptrToJlong(void *ptr);
    static QString demangle(const QString &mangledName);
    static QString mangle(const QString &demangledName);
    static QString toJNISignature(const QString &signature, QString *name);
    static jvalue convertToPrimitive(JNIEnv *env, jobject javaRef, Type typeId);
    static jvalue convertToComplex(JNIEnv *env, jvalue val, Type typeId,
        bool *success = 0);
    static jvalue callMethod(JNIEnv *env, jobject javaRef, jmethodID methodId,
        Type typeId, jvalue *params = 0);
    inline static QString className(const QString &qualifiedName);
    inline static QString package(const QString &qualifiedName);
    static QString closestQtSuperclass(JNIEnv *env, const QString &className,
                                       const QString &package);
    static bool isQObjectSubclass(JNIEnv *env, const QString &className, const QString &package);
    static bool isQtClass(JNIEnv *env, const QString &className, const QString &package);
    static QString complexTypeOf(Type type);
    static QString primitiveTypeOf(Type type);
    static Type valueTypePattern(const QString &javaName);

    // Reimplementations
    QString getExternalTypeName(const QString &internalTypeName, VariableContext ctx) const;
    QString getInternalTypeName(const QString &externalTypeName, VariableContext ctx) const;
    bool convertInternalToExternal(const void *in, void **out, const QString &internalTypeName,
        const QString &externalTypeName, VariableContext ctx);
    bool convertExternalToInternal(const void *in, void **out, const QString &externalTypeName,
        const QString &internalTypeName, VariableContext ctx);

    QVector<QString> parseSignature(const QString &signature, QString *name = 0) const;

    void *constructExternal(const QString &externalTypeName, VariableContext ctx,
        const void *copy = 0);
    void destroyExternal(void *value, VariableContext ctx);

    bool isEnumType(const QString &className, const QString &package) const;
    bool isEnumType(jclass clazz) const;
    int intForEnum(jobject enum_value) const;
    jobject enumForInt(int value, const QString &className, const QString &package) const;

    bool canConvertInternalToExternal(const QString &internalTypeName,
        const QString &externalTypeName, VariableContext ctx) const;
    bool canConvertExternalToInternal(const QString &externalTypeName,
        const QString &internalTypeName, VariableContext ctx) const;
    QString internalToExternalSignature(const QString &internalSignature) const;
    QString externalToInternalSignature(const QString &externalSignature) const;
    void destroyConstructedInternal(const QVector<void *> &in);
    void destroyConstructedExternal(const QVector<void *> &in);
    bool decodeArgumentList(const QVector<void *> &in,
        QVector<void *> *out, const QVector<QString> &typeList);
    bool encodeArgumentList(const QVector<void *> &in,  QVector<void *> *out,
        const QVector<QString> &typeList);
    void destroyInternal(void *value, VariableContext ctx);
    void *constructInternal(const QString &internalTypeName, VariableContext ctx,
        const void *copy = 0, int metaType=QMetaType::Void);
    int metaTypeOfInternal(const QString &internalTypeName, VariableContext) const;
    QVector<void *> initExternalToInternal(const QVector<void *> &externalVariables,
        const QVector<QString> &externalTypeNames);
    QVector<void *> initInternalToExternal(const QVector<void *> &internalVariables,
        const QVector<QString> &externalTypeNames);

    bool convertEnums() const { return mConvertEnums; }
    void setConvertEnums(bool on) { mConvertEnums = on; }

private:
    static QString processInternalTypeName(const QString &typeName, int *indirections = 0);

private:
    QHash<void *, QString> mOwnedVariables_internal;
    QHash<jvalue *, bool> mOwnedVariables_external;

    JNIEnv *mEnvironment;
    uint mConvertEnums : 1;
};

// *********** Implementations ***********
inline void *QtJambiTypeManager::jlongToPtr(jlong id) { return reinterpret_cast<void *>(static_cast<long>(id)); }
inline jlong QtJambiTypeManager::ptrToJlong(void *ptr) { return static_cast<jlong>(reinterpret_cast<long>(ptr)); }

inline QString QtJambiTypeManager::className(const QString &qualifiedName)
{
    int idx = qualifiedName.lastIndexOf(QLatin1Char('/'));
    if (idx >= 0)
        return qualifiedName.mid(idx + 1);
    else
        return qualifiedName;
}

inline QString QtJambiTypeManager::package(const QString &qualifiedName)
{
    int idx = qualifiedName.lastIndexOf(QLatin1Char('/'));
    if (idx >= 0)
        return qualifiedName.left(idx + 1);
    else
        return QString();
}

#endif
