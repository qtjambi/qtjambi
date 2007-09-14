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

#include "qtjambitypemanager.h"
#include "qtjambilink.h"
#include "qtjambi_core.h"
#include "qtjambi_cache.h"

#include <QThread>

Q_GLOBAL_STATIC(QReadWriteLock, gStaticDataLock)

QtJambiTypeManager::QtJambiTypeManager(JNIEnv *env) : mEnvironment(env), mConvertEnums(false)
{
}

QtJambiTypeManager::~QtJambiTypeManager()
{
    destroyConstructedInternal(mOwnedVariables_internal.keys().toVector());
}

/*!
    Returns true iff the C++ type specified by \a internalTypeName can be safely converted
    to the external type specified by \a externalTypeName.

    The default implementation uses getExternalTypeName() to convert \a internalTypeName
    and checks if the result is equal to \a externalTypeName. The c++ type 'void' is
    handled specially. If we are converting a return type and the source type is void,
    then the conversion is always safe, as the only result will be that the signal will
    return a default value (which will not be overwritten by the slot.)
*/
bool QtJambiTypeManager::canConvertInternalToExternal(const QString &internalTypeName,
    const QString &externalTypeName, VariableContext ctx) const
{
    bool result = (externalTypeName == getExternalTypeName(internalTypeName, ctx))
                   || (ctx == ReturnType && internalTypeName == QLatin1String("void"));

    return result;
}

/*!
    Returns true iff the external type specified by \a externalTypeName can be safely
    converted to the C++ type specified by \a internalTypeName.

    The default implementation uses getInternalTypeName() to convert \a externalTypeName
    and checks if the result is equal to \a internalTypeName. The C++ type 'void' is
    handled specially. If we are converting a return type and the destination type is 'void',
    then the conversion is safe, as it will simply result in the return value not being
    propagated.
*/
bool QtJambiTypeManager::canConvertExternalToInternal(const QString &externalTypeName,
    const QString &internalTypeName, VariableContext ctx) const
{
    bool result = (internalTypeName == getInternalTypeName(externalTypeName, ctx))
                   || (ctx == ReturnType && internalTypeName == QLatin1String("void"));

    return result;
}

/*!
    Converts the type names in a C++ signature to external type names and returns the converted
    signature.
*/
QString QtJambiTypeManager::internalToExternalSignature(const QString &internalSignature) const
{
    QString name;
    QVector<QString> typeList = parseSignature(internalSignature, &name);

    QString returned = getExternalTypeName(typeList.at(0), ReturnType) + QLatin1Char(' ')
                       + name + QLatin1Char('(');

    for (int i=1; i<typeList.count(); ++i) {
        if (i > 1)
            returned += QLatin1Char(',');
        returned += getExternalTypeName(typeList.at(i), ArgumentType);
    }
    returned += QLatin1Char(')');

    return returned;
}

/*!
    Converts the type names in an external signature to C++ type names and returns the converted
    signature.
*/
QString QtJambiTypeManager::externalToInternalSignature(const QString &externalSignature) const
{
    QString name;
    QVector<QString> typeList = parseSignature(externalSignature, &name);

    QString returned = getInternalTypeName(typeList.at(0), ReturnType);
    if (returned.length() > 0)
        returned += QLatin1Char(' ');
    returned += name + QLatin1Char('(');

    for (int i=1; i<typeList.count(); ++i) {
        if (i > 1)
            returned += QLatin1Char(',');
        returned += getInternalTypeName(typeList.at(i), ArgumentType);
    }
    returned += QLatin1Char(')');

    return returned;
}

/*!
    Destroys a list of values previously constructed by \a constructInternal(). The default
    implementation uses destroyInternal() to destroy the values. The method will silently ignore
    any values that has not been constructed using constructInternal() (and therefore it will
    always ignore null pointers.)
*/
void QtJambiTypeManager::destroyConstructedInternal(const QVector<void *> &in)
{
    for (int i=0; i<in.size(); ++i)
        destroyInternal(in.at(i), i == 0 ? ReturnType : ArgumentType);
}

/*!
    Destroys a list of values previously constructed by \a constructExternal(). The default
    implementation uses destroyExternal() to destroy the values. The method will silently ignore
    any values that has not been constructed using constructExternal() (and therefore it will
    always ignore null pointers.)
*/
void QtJambiTypeManager::destroyConstructedExternal(const QVector<void *> &in)
{
    for (int i=0; i<in.size(); ++i)
        destroyExternal(in.at(i), i == 0 ? ReturnType : ArgumentType);
}

/*!
    Converts a list of values from C++ types to external types. \a in should contain
    pointers to the source values, while \a out should contain pointers to the destination
    variables. The first entry of \a out is assumed to
    point to the location of the return value and will be ignored,
    while the rest of the entries are assumed to be arguments to be passed to
    an external slot.
    Each entry in \a typeList should specify the external type name of the variable
    in the corresponding entry in \a out. Thus, the first entry of \a typeList is the type name
    of the return value.
    \a in should only contain arguments, and thus must contain exactly one fewer
    entries than \a out. The method returns true on success and false on failure.
    The default implementation converts arguments using convertInternalToExternalArgument().
*/
bool QtJambiTypeManager::decodeArgumentList(const QVector<void *> &in,
                                             QVector<void *> *out, const QVector<QString> &typeList)
{
    Q_ASSERT(out != 0);
    Q_ASSERT(in.size() == out->size() - 1);
    Q_ASSERT(typeList.size() == out->size());

    bool success = true;

    void **outData = out->data();
    for (int i=0; i<in.size(); ++i) {
        void *inv = in.at(i);

        const QString &externalType = typeList.at(i + 1);
        QString internalType = getInternalTypeName(externalType, i == 0 ? ReturnType : ArgumentType);

        if (!convertInternalToExternal(inv, &outData[i + 1], internalType, externalType, ArgumentType)) {
            success = false;
            break ;
        }
    }

    return success;
}

/*!
    Converts a list of values from external types to internal types. \in should contain
    pointers to the source values, and \a out should contain pointers to variables capable
    of storing the destination type. The first entry of \a out is assumed to
    point the location of the return value and will be ignored,
    while the rest of the entries are assumed to be arguments to be passed to
    a C++ slot. Each entry in \a typeList should specify the equivalent external type name
    of the variable
    in the corresponding entry in \a out. Thus, the first entry of \a typeList is the type name
    of the return value.
    \a in should only contain arguments, and thus must contain exactly one fewer
    entries than \a out. The method returns true on success and false on failure.
    The default implementation converts arguments using convertExternalToInternalArgument().
*/
bool QtJambiTypeManager::encodeArgumentList(const QVector<void *> &in,  QVector<void *> *out,
                                             const QVector<QString> &typeList)
{
    Q_ASSERT(out != 0);
    Q_ASSERT(in.size() == out->size() - 1);
    Q_ASSERT(typeList.size() == out->size());

    bool success = true;
    void **outData = out->data();
    for (int i=0; i<in.size(); ++i) {
        void *inv = in.at(i);

        const QString &externalType = typeList.at(i + 1);
        QString internalType = getInternalTypeName(externalType, ArgumentType);

        if (!convertExternalToInternal(inv, &outData[i + 1], externalType, internalType, ArgumentType)) {
            success = false;
            break ;
        }
    }

    return success;
}

/*!
    Destroys a value previously constructed using constructInternal(). \a value should
    point to the value to be destroyed. If \a value points to null or any value which
    was not constructed using constructInternal(), the method will return taking no action.
    The default implementation uses QMetaType::destroy() to destroy the value, and
    metaTypeOfInternal() to find the meta type ID.
    If you reimplement this method, you must also reimplement constructInternal().
*/
void QtJambiTypeManager::destroyInternal(void *value, VariableContext ctx)
{
    if (value != 0) {
        if (mOwnedVariables_internal.contains(value)) {
            QString typeName = mOwnedVariables_internal.value(value);
            int metaType = metaTypeOfInternal(typeName, ctx);
            if (metaType != int(QMetaType::Void)
                && (metaType < QMetaType::User || QMetaType::isRegistered(metaType))) {
                QMetaType::destroy(metaType, value);

                mOwnedVariables_internal.remove(value);
            }
        }
    }
}

/*!
    Default constructs a value for the C++ type given by \a internalTypeName, and returns
    a pointer to the value. The default implementation uses the return value of
    metaTypeOfInternal() called with \a internalTypeName and \a ctx and uses
    QMetaType::construct() to construct a value. Returns null on failure.
    If this function returns a valid pointer, the user must later call
    destroyInternal() when the value is no longer in use. If you reimplement this method,
    you must also reimplement destroyInternal().
*/
void *QtJambiTypeManager::constructInternal(const QString &internalTypeName, 
                                            VariableContext ctx,
                                            const void *copy,
                                            int metaType)
{
    if (metaType == QMetaType::Void)
        metaType = metaTypeOfInternal(internalTypeName, ctx);

    void *returned = 0;
    if (metaType != int(QMetaType::Void)
        && (metaType < QMetaType::User || QMetaType::isRegistered(metaType))) {

        returned = QMetaType::construct(metaType, copy);

        // We need to initialize everything to zero by default
        if (copy == 0) {
            switch (metaType) {
            case QMetaType::VoidStar:
            case QMetaType::QObjectStar:
            case QMetaType::QWidgetStar:
                *(reinterpret_cast<void **>(returned)) = 0; break ;
            case QMetaType::Long:
                *(reinterpret_cast<long *>(returned)) = 0; break ;
            case QMetaType::Int:
                *(reinterpret_cast<int *>(returned)) = 0; break ;
            case QMetaType::Short:
                *(reinterpret_cast<short *>(returned)) = 0; break ;
            case QMetaType::Char:
                *(reinterpret_cast<char *>(returned)) = 0; break ;
            case QMetaType::ULong:
                *(reinterpret_cast<ulong *>(returned)) = 0; break ;
            case QMetaType::UInt:
                *(reinterpret_cast<uint *>(returned)) = 0; break ;
            case QMetaType::UShort:
                *(reinterpret_cast<ushort *>(returned)) = 0; break ;
            case QMetaType::UChar:
                *(reinterpret_cast<uchar *>(returned)) = 0; break ;
            case QMetaType::Bool:
                *(reinterpret_cast<bool *>(returned)) = false; break ;
            case QMetaType::Float:
                *(reinterpret_cast<float *>(returned)) = 0.0; break ;
            case QMetaType::Double:
                *(reinterpret_cast<double *>(returned)) = 0.0; break ;
            default:
                if (QMetaType::type("qint64") == metaType)
                    *(reinterpret_cast<qint64 *>(returned)) = 0; break ;
                break ;
            };
        }
    }

    if (returned != 0)
        mOwnedVariables_internal[returned] = internalTypeName;

    return returned;
}

/*!
    Returns the QMetaType::Type value for the C++ type given by \a internalTypeName. This is a
    convenience function used by constructInternal() and the default implementation simply returns
    the result of QMetaType::type() called on the type name when the type name has first been
    converted to latin 1. \a ctx indicates the intended context of use for the type.
*/
int QtJambiTypeManager::metaTypeOfInternal(const QString &internalTypeName,
                                            VariableContext) const
{
    return QMetaType::type(internalTypeName.toLatin1().constData());
}


QVector<void *> QtJambiTypeManager::initExternalToInternal(const QVector<void *> &externalVariables,
                                                 const QVector<QString> &externalTypeNames)
{
    QVector<void *> convertedArguments(externalTypeNames.size(), 0);
    encodeArgumentList(externalVariables, &convertedArguments, externalTypeNames);
    convertedArguments[0] =
        constructInternal(getInternalTypeName(externalTypeNames.at(0), ReturnType), ReturnType);
    return convertedArguments;
}

QVector<void *> QtJambiTypeManager::initInternalToExternal(const QVector<void *> &internalVariables,
                                                          const QVector<QString> &externalTypeNames)
{
    QVector<void *> convertedArguments(externalTypeNames.count(), 0);
    decodeArgumentList(internalVariables, &convertedArguments, externalTypeNames);
    convertedArguments[0] =
        constructExternal(externalTypeNames.at(0), ReturnType);
    return convertedArguments;
}

QString QtJambiTypeManager::complexTypeOf(Type type)
{
    switch (type) {
    case Integer: return QLatin1String("java/lang/Integer");
    case Long: return QLatin1String("java/lang/Long");
    case Boolean: return QLatin1String("java/lang/Boolean");
    case Short: return QLatin1String("java/lang/Short");
    case Char: return QLatin1String("java/lang/Character");
    case Byte: return QLatin1String("java/lang/Byte");
    case Float: return QLatin1String("java/lang/Float");
    case Double: return QLatin1String("java/lang/Double");
    default:
        break;
    };

    qWarning("Cannot find complex type of type id '%d'", int(type));
    return QString();
}

QString QtJambiTypeManager::primitiveTypeOf(Type type)
{
    switch (type) {
    case Integer: return QLatin1String("int");
    case Long: return QLatin1String("long");
    case Boolean: return QLatin1String("boolean");
    case Short: return QLatin1String("short");
    case Char: return QLatin1String("char");
    case Byte: return QLatin1String("byte");
    case Float: return QLatin1String("float");
    case Double: return QLatin1String("double");
    default:
        break;
    };

    qWarning("Cannot find complex type of type id '%d'", int(type));
    return QString();
}

QString QtJambiTypeManager::mangle(const QString &_typeName)
{
    int indirections = _typeName.count("[]");
    QString typeName = _typeName.left(_typeName.length() - indirections * 2);
    QString strIndirections(indirections, QLatin1Char('['));

    static QHash<QString, QString> mangleHash;
    QString returned;
    bool empty = true;
    {
        QReadLocker locker(gStaticDataLock());
        empty = mangleHash.isEmpty();
        returned = mangleHash.value(typeName, QString());
    }

    if (empty) {
        QWriteLocker locker(gStaticDataLock());

        mangleHash["int"] = QLatin1String("I");
        mangleHash["long"] =  QLatin1String("J");
        mangleHash["boolean"] =  QLatin1String("Z");
        mangleHash["float"] =  QLatin1String("F");
        mangleHash["double"] =  QLatin1String("D");
        mangleHash["char"] =  QLatin1String("C");
        mangleHash["short"] =  QLatin1String("S");
        mangleHash["byte"] = QLatin1String("B");
        mangleHash["void"] = QLatin1String("V");

        returned = mangleHash.value(typeName, QString());
    }

    if (returned.isEmpty()) {
        return strIndirections + QLatin1Char('L') + QString(typeName).replace(QLatin1Char('.'),
               QLatin1Char('/')) + QLatin1Char(';');
    } else {
        return strIndirections + returned;
    }
}

// Converts a JVM compatible type specification (J, N, I, Z, B, etc) to a
// type name.
QString QtJambiTypeManager::demangle(const QString &_typeName)
{
    int indirections = _typeName.count("[");
    QString typeName = _typeName.right(_typeName.length() - indirections);
    QString strIndirections;
    QString brackets = QLatin1String("[]");
    for (int i=0; i<indirections; ++i)
        strIndirections += brackets;

    switch (typeName.at(0).toLatin1()) {
    case 'I': return QLatin1String("int") + strIndirections;
    case 'Z': return QLatin1String("boolean") + strIndirections;
    case 'B': return QLatin1String("byte") + strIndirections;
    case 'J': return QLatin1String("long") + strIndirections;
    case 'C': return QLatin1String("char") + strIndirections;
    case 'D': return QLatin1String("double") + strIndirections;
    case 'F': return QLatin1String("float") + strIndirections;
    case 'S': return QLatin1String("short") + strIndirections;
    case 'V': return QLatin1String("void") + strIndirections;
    case 'L':
        return typeName.mid(1, typeName.length() - 2)
                       .replace(QLatin1Char('/'), QLatin1Char('.')) + strIndirections;
    default:
        qWarning("demangle: Unrecognized java type specification: %s",
            qPrintable(typeName));
        break ;
    };

    return QString();
}

bool QtJambiTypeManager::isQtClass(JNIEnv *env, const QString &className, const QString &package)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiObject();
    sc->resolveQtJambiInternal();
    jclass clazz = resolveClass(env, className.toUtf8().constData(), package.toUtf8().constData());
    return (clazz != 0 && env->CallStaticBooleanMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.isGeneratedClass, clazz));
}

bool QtJambiTypeManager::isQObjectSubclass(JNIEnv *env, const QString &className, const QString &package)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQObject();
    jclass clazz = resolveClass(env, className.toUtf8().constData(), package.toUtf8().constData());
    return (clazz != 0 && bool(env->IsAssignableFrom(clazz, sc->QObject.class_ref)));
}

QString QtJambiTypeManager::closestQtSuperclass(JNIEnv *env, const QString &className,
                                               const QString &package)
{
    jclass clazz = resolveClass(env, className.toUtf8().constData(), package.toUtf8().constData());
    jclass sc = resolveClosestQtSuperclass(env, clazz);

    if (sc != 0)
        return QtJambiLink::nameForClass(env, sc).replace(QLatin1Char('.'), QLatin1Char('/'));
    else
        return QString();
}

jvalue QtJambiTypeManager::convertToComplex(JNIEnv *env, jvalue val, Type typeId,
                                           bool *success)
{
    if (success != 0)
        *success = false;

    jvalue returned;
    returned.j = 0;
    if (typeId != None) {
        char jniType;
        switch (typeId) {
        case Integer: jniType = 'I'; break ;
        case Long: jniType = 'J'; break ;
        case Float: jniType = 'F'; break ;
        case Double: jniType = 'D'; break ;
        case Boolean: jniType = 'Z'; break ;
        case Short: jniType = 'S'; break ;
        case Char: jniType = 'C'; break ;
        case Byte: jniType = 'B'; break ;
        default:
            qWarning("QtJambiTypeManager::convertToComplex: Unhandled type id '%d'",
                int(typeId));
            jniType = (char) 0; // silence gcc
        };

        QString qualifiedName = complexTypeOf(typeId);

        QByteArray strClassName = className(qualifiedName).toUtf8();
        QByteArray strPackage = package(qualifiedName).toUtf8();

        jclass clazz = resolveClass(env, strClassName.constData(), strPackage.constData());

        if (clazz != 0) {
            char sig[] = { '(', jniType, ')', 'V', 0 };
            jmethodID methodId = resolveMethod(env, "<init>", sig, strClassName, strPackage);
            if (methodId != 0) {
                returned.l = env->NewObjectA(clazz, methodId, &val);

                if (success != 0)
                    *success = true;
            }
        }
    }

    return returned;
}

jvalue QtJambiTypeManager::callMethod(JNIEnv *env, jobject javaRef, jmethodID methodId,
                                     Type typeId, jvalue *param)
{
    jvalue returned;
    returned.j = 0;

    switch (typeId) {
    case Integer:
        returned.i = param
            ? env->CallIntMethodA(javaRef, methodId, param)
            : env->CallIntMethod(javaRef, methodId);
        break ;

    case Long:
        returned.j = param
            ? env->CallLongMethodA(javaRef, methodId, param)
            : env->CallLongMethod(javaRef, methodId);
        break ;

    case Boolean:
        returned.z = param
            ? env->CallBooleanMethodA(javaRef, methodId, param)
            : env->CallBooleanMethod(javaRef, methodId);
        break ;

    case Short:
        returned.s = param
            ? env->CallShortMethodA(javaRef, methodId, param)
            : env->CallShortMethod(javaRef, methodId);
        break ;

    case Float:
        returned.f = param
            ? env->CallFloatMethodA(javaRef, methodId, param)
            : env->CallFloatMethod(javaRef, methodId);
        break ;

    case Double:
        returned.d = param
            ? env->CallDoubleMethodA(javaRef, methodId, param)
            : env->CallDoubleMethod(javaRef, methodId);
        break ;

    case Byte:
        returned.b = param
            ? env->CallByteMethodA(javaRef, methodId, param)
            : env->CallByteMethod(javaRef, methodId);
        break ;

    case Char:
        returned.c = param
            ? env->CallCharMethodA(javaRef, methodId, param)
            : env->CallCharMethod(javaRef, methodId);
        break ;

    case None:
        returned.j = 0;
        param
            ? env->CallVoidMethodA(javaRef, methodId, param)
            : env->CallVoidMethod(javaRef, methodId);
        break ;
    default: // avoid compiler warnings
        qWarning("QtJambiTypeManager::callMethod: Unknown primitive id '%d'", int(typeId));
        break ;
    };

    return returned;
}

jvalue QtJambiTypeManager::convertToPrimitive(JNIEnv *env, jobject javaRef,
                                             Type typeId)
{
    jvalue returned;
    returned.l = 0;

    char *methodName;
    char *signature;
    switch (typeId) {
    case Integer:
        methodName = "intValue";
        signature = "()I";
        break ;
    case Long:
        methodName = "longValue";
        signature = "()J";
        break ;
    case Boolean:
        methodName = "booleanValue";
        signature = "()Z";
        break ;
    case Short:
        methodName = "shortValue";
        signature = "()S";
        break ;
    case Float:
        methodName = "floatValue";
        signature = "()F";
        break ;
    case Double:
        methodName = "doubleValue";
        signature = "()D";
        break ;
    case Byte:
        methodName = "byteValue";
        signature = "()B";
        break ;
    case Char:
        methodName = "charValue";
        signature = "()C";
        break ;
    case None:
        return returned;
    default: // nothing
        qWarning("QtJambiTypeManager::convertToPrimitive: Primitive id '%d' not recognized",
            int(typeId));
        return returned;
    };

    jmethodID methodId = 0;
    jclass clazz = env->GetObjectClass(javaRef);
    if (clazz != 0)
        methodId = resolveMethod(env, methodName, signature, clazz);
    if (methodId != 0)
        returned = callMethod(env, javaRef, methodId, typeId);

    return returned;
}

/*!
    \internal
*/
QString QtJambiTypeManager::toJNISignature(const QString &signature, QString *name)
{
    QString tmp(signature);

    // Remove generic parameters from signature
    int genericIdx = tmp.indexOf(QLatin1Char('<'));
    while (genericIdx >= 0) {
        int paramNum = 1;
        while (paramNum > 0 && genericIdx < tmp.length()) {
            tmp.remove(genericIdx, 1);
            paramNum += int(tmp.at(genericIdx) == QLatin1Char('<'));
            paramNum -= int(tmp.at(genericIdx) == QLatin1Char('>'));
        }

        tmp.replace(genericIdx, 1, QLatin1Char(' '));
        genericIdx = tmp.indexOf('<');
    }

    tmp = tmp.trimmed();

    int endName = tmp.indexOf(QLatin1Char('('), 1) - 1;
    int firstParen = tmp.indexOf(QLatin1Char(' '), 1);
    int startName = 0;
    QString returnType(QLatin1String("V"));
    if (firstParen >= 0 && firstParen < endName) {
        returnType = mangle(tmp.left(firstParen).trimmed());
        startName = firstParen + 1;
    }

    if (name != 0)
        *name = tmp.mid(startName, endName - startName + 1);

    int end = tmp.indexOf(QLatin1Char(')'), endName + 1);
    QString outSignature(QLatin1String("("));
    int pos = endName + 2;
    while (pos < end) {
        int comma = tmp.indexOf(QLatin1Char(','), pos + 1);
        if (comma <= 0)
            comma = end;
        outSignature += mangle(tmp.mid(pos, comma - pos).trimmed());
        pos = comma + 1;
    }
    outSignature += QLatin1String(")") + returnType;

    return outSignature;
}

QtJambiTypeManager::Type QtJambiTypeManager::valueTypePattern(const QString &javaName)
{
    int type = Value;

    static QHash<QString, Type> valueTypeHash;
    bool empty = true;
    Type t = None;
    {
        QReadLocker locker(gStaticDataLock());
        t = valueTypeHash.value(javaName, None);
        empty = valueTypeHash.isEmpty();
    }

    if (empty) {
        QWriteLocker locker(gStaticDataLock());
        valueTypeHash["boolean"] = Type(Boolean | Primitive);
        valueTypeHash["byte"] = Type(Byte | Primitive);
        valueTypeHash["char"] = Type(Char | Primitive);
        valueTypeHash["double"] = Type(Double | Primitive);
        valueTypeHash["float"] = Type(Float | Primitive);
        valueTypeHash["int"] = Type(Integer | Primitive);
        valueTypeHash["long"] = Type(Long | Primitive);
        valueTypeHash["short"] = Type(Short | Primitive);

        valueTypeHash["java/lang/Boolean"] = Type(Boolean);
        valueTypeHash["java/lang/Byte"] = Type(Byte);
        valueTypeHash["java/lang/Character"] = Type(Char);
        valueTypeHash["java/lang/Double"] = Type(Double);
        valueTypeHash["java/lang/Float"] = Type(Float);
        valueTypeHash["java/lang/Integer"] = Type(Integer);
        valueTypeHash["java/lang/Long"] = Type(Long);
        valueTypeHash["java/lang/Short"] = Type(Short);
        valueTypeHash["java/lang/String"] = Type(String);

        t = valueTypeHash.value(javaName, None);
    }

    return Type(type | t);
}

QString QtJambiTypeManager::processInternalTypeName(const QString &_internalTypeName,
                                                      int *_indirections)
{
    int indirections = _internalTypeName.count(QLatin1Char('*'));
    if (_indirections != 0)
        *_indirections = indirections; 
    return _internalTypeName.left(_internalTypeName.length() - indirections);
}

QtJambiTypeManager::Type QtJambiTypeManager::typeIdOfInternal(JNIEnv *env,
                                                            const QString &_internalTypeName)
{
    // For "void" we always return None.
    if (_internalTypeName == QLatin1String("void"))
        return None;

    int indirections = 0;
    QString internalTypeName = processInternalTypeName(_internalTypeName, &indirections);

    // If we have more than one indirection we always use the QNativePointer pattern
    if (indirections > 1)
        return NativePointer;

    // Or we need to resolve the type
    QString javaName = getJavaName(internalTypeName);
    QString strClassName = className(javaName);
    QString strPackage = package(javaName);
    int type = 0;
    int value_type_pattern = valueTypePattern(javaName);
    if (((value_type_pattern & Primitive) == 0) && isQtClass(env, strClassName, strPackage))
        type |= QtClass;
   
    int metaType = QMetaType::type(internalTypeName.toLatin1().constData());
    if (metaType != QMetaType::Void) {
        type |= value_type_pattern;
    } else {
        type |= Object;


        if (isQObjectSubclass(env, strClassName, strPackage))
            type |= QObjectSubclass;
    }


    // Pointers to value types are native pointers
    if ((type & Value) && indirections > 0)
        type = NativePointer;

    return Type(type);
}

QtJambiTypeManager::Type QtJambiTypeManager::typeIdOfExternal(JNIEnv *env, const QString &className,
                                                            const QString &package)
{
    // For "void" we always return None.
    if (className == QLatin1String("void"))
        return None;

    // Native pointers are native pointers
    if (package == QLatin1String("com/trolltech/qt/") &&
        className == QLatin1String("QNativePointer")) {
        return NativePointer;
    }

    QString qtName = getQtName(package + className);

    int metaType = QMetaType::Void;
    if (!qtName.isEmpty() && !qtName.endsWith(QLatin1Char('*')))
        metaType = QMetaType::type(qtName.toLatin1().constData());

    // Value types are resolved and object types that inherit QtJambiObject are attempted to be resolved.
    // We will attempt to map any class which begins with java/lang to a value type.
    // Other types become variants.
    int type = 0;
    int value_type_pattern = valueTypePattern(package + className);
    if (((value_type_pattern & Primitive) == 0) && isQtClass(env, className, package))
        type |= QtClass;

    if (metaType != QMetaType::Void || package.startsWith("java/lang/")) {
        type |= value_type_pattern;
    } else if (type & QtClass) {
        type |= Object;

        if (isQObjectSubclass(env, className, package))
            type |= QObjectSubclass;
    } else {
        type |= Value; // Try to go with jobject
    }

    return Type(type);
}

/*!
    Parses a signature and returns a vector containing the names of the return and argument types
    in the signature. The first entry in the returned QVector will contain the name of the
    return type, the subsequent entries will contain the names of the argument types in order.
    If \a name is different from null, the string it points to will contain the name of the
    function after when the call to parseSignature() returns.
*/
QVector<QString> QtJambiTypeManager::parseSignature(const QString &signature,
                                                   QString *name) const
{
    QString tmp(signature.trimmed());
    tmp.replace(QLatin1Char('.'), QLatin1Char('/'));

    QString returnType = QLatin1String("void");
    int idxFirstSpace = tmp.indexOf(QLatin1Char(' '));
    int idxFirstParen = tmp.indexOf(QLatin1Char('('));

    // Does it have a return type?
    if (idxFirstSpace >= 0 && idxFirstSpace < idxFirstParen) {
        returnType = tmp.left(idxFirstSpace).trimmed();
        tmp = tmp.remove(0, idxFirstSpace + 1).trimmed();
    }

    QVector<QString> returned;
    returned.append(returnType);

    idxFirstParen = tmp.indexOf(QLatin1Char('('));
    if (name != 0)
        *name = tmp.left(idxFirstParen).trimmed();

    tmp = tmp.remove(0, idxFirstParen + 1).trimmed();
    int idxComma = tmp.indexOf(QLatin1Char(','));
    while (idxComma >= 0) {
        QString argType(tmp.left(idxComma).trimmed());
        if (argType.length() > 0)
            returned.append(argType);

        tmp = tmp.remove(0, idxComma + 1);
        idxComma = tmp.indexOf(QLatin1Char(','));
    }

    idxComma = tmp.indexOf(QLatin1Char(')'));
    QString argType(tmp.left(idxComma).trimmed());
    if (argType.length() > 0)
        returned.append(argType);

    return returned;
}


/*!
    \reimp

    Returns the C++ equivalent of a Java type.

    The translation is as follows:
      * Primitive types:
        long -> qint64
        boolean -> bool
        char -> QChar
        byte -> char
        ... otherwise the same type name is used
      * Box types:
        java/lang/Long -> qint64
        ... etc. (equivalent to primitive types)
      * Reference types, subclasses of QObject:
        The simple class name with an asterisk appended to it
      * Reference types, non-QObjects in the Qt package (value types):
        The simple class name by itself
      * Reference types, regular Java Objects
        jobjects
*/
QString QtJambiTypeManager::getInternalTypeName(const QString &externalTypeName,
                                               VariableContext /*ctx*/) const
{
    // First we try a simple look up. This will handle any type in the type system,
    // so: primitives, boxed primitives and direct mappings of Qt types
    QString qtName = getQtName(externalTypeName);
    Type t = valueTypePattern(externalTypeName);
    if ((!qtName.isEmpty() && (t & Primitive)) || externalTypeName.endsWith("[]"))
        return qtName;
    
    // If not we must do some more work.
    QString strClassName = className(externalTypeName);
    QString strPackage = package(externalTypeName);
    Type type = typeIdOfExternal(mEnvironment, strClassName, strPackage);
    QTJAMBI_EXCEPTION_CHECK(mEnvironment);
    if (!qtName.isEmpty() && type & Value)
        return qtName;

    if (type == None) {
        return QString();
    } else if (type & TypeMask) {
        return getQtName(primitiveTypeOf(Type(type & TypeMask)));
    } else if (type & NativePointer) {
        return QLatin1String("void *");
    } else if (type & Object) {
        return className(closestQtSuperclass(mEnvironment, strClassName, strPackage))
            + QLatin1Char('*');
    } else { // All java types can be converted to jobjects
        return QLatin1String("JObjectWrapper");
    }
}

/*!
    \reimp

    Returns the Java equivalent of C++ type \a internalTypeName.

    The translation is as follows:
        qint64 -> java/lang/Long
        bool -> java/lang/Boolean
        QChar -> java/lang/Character
        char -> java/lang/Byte
        .. etc for other types that map to Java primitive types. All these types are boxed.
      * Reference types with equivalents in Qt package:
        Translated to equivalent java class, fully qualified.
*/
QString QtJambiTypeManager::getExternalTypeName(const QString &internalTypeName,
                                               VariableContext ctx) const
{
    // Handle types in the type system, so: primitives and direct mappings.
    // Primitives need to be converted to objects for return types, so we
    // have to do some more work for them.
    QString javaName = getJavaName(processInternalTypeName(internalTypeName));
    if (ctx == ArgumentType && !javaName.isEmpty())
        return javaName;

    Type type = typeIdOfInternal(mEnvironment, internalTypeName);

    // If it's a primitive, then we're doing a return type and we convert to an object
    if (type & TypeMask)
        return complexTypeOf(Type(type & TypeMask));
    else if (type & NativePointer)
        return QLatin1String("com/trolltech/qt/QNativePointer");
    else if (type & Object)
        return javaName;
    else if (type == None)
        return QLatin1String("void");

    qWarning("QtJambiTypeManager::getExternalTypeName: Couldn't convert '%s' to Java type",
        qPrintable(internalTypeName));
    return QString();
}

/*!
    \reimp

    Converts a C++ value to a Java value. The destination type is given by \a externalTypeName,
    while the source type is given by getInternalTypeName() when passed the external type name.

    For return types, primitive types are automatically boxed, since we should always return
    objects in Java signals. For arguments they are copied directly into equivalent Java types.

    \a in is expected to point to a value of the type returned by getInternalTypeName(), mentioned
    above.
    \a out is expected to be a pointer to a jvalue struct.
*/
bool QtJambiTypeManager::convertInternalToExternal(const void *in, void **out,
                                                  const QString &internalTypeName,
                                                  const QString &externalTypeName,
                                                  VariableContext ctx)
{
    if (in == 0)
        return true;

    if (internalTypeName == QLatin1String("void"))
        return true;

    Q_ASSERT(out != 0);
    Q_ASSERT(*out == 0);

    *out = constructExternal(externalTypeName, ctx);
    jvalue *p = reinterpret_cast<jvalue *>(*out);
    p->j = 0;

    bool success = false;

    QString strClassName = className(externalTypeName);
    QString strPackage = package(externalTypeName);

    // Find usage pattern
    Type type = typeIdOfExternal(mEnvironment, strClassName, strPackage);
    if (type & TypeMask) {
        jvalue val;
        switch (type & TypeMask) {
        case Integer: memcpy(&val.i, in, 4); break ;
        case Long: memcpy(&val.j, in, 8); break ;
        case Boolean: memcpy(&val.z, in, 1); break ;
        case Short: memcpy(&val.s, in, 2); break ;
        case Float: memcpy(&val.f, in, 4); break ;
        case Double: memcpy(&val.d, in, 8); break ;
        case Byte: memcpy(&val.b, in, 1); break ;
        case Char: memcpy(&val.c, in, 2); break ;
        default:
            val.j = 0;
            break ;
        };

        if (ctx == ReturnType) {
            (*p) = convertToComplex(mEnvironment, val, Type(type & TypeMask), &success);
        } else {
            memcpy(p, &val, sizeof(jvalue));
            success = true;
        }
    } else if (type & String) {
        const QString *strp = reinterpret_cast<const QString *>(in);
        p->l = qtjambi_from_qstring(mEnvironment, *strp);
        success = true;
    } else if (type & NativePointer) {
        const void * const*in_p = reinterpret_cast<const void * const*>(in);
        p->l = qtjambi_from_cpointer(mEnvironment, *in_p, 8, 1);
        success = true;
    } else if ((type & QtClass) && (((type & Object) || (type & Value)))) {
        jobject javaObject = 0;

        // If we're dealing with a QObject, then we try to find the original java instance
        if (type & QObjectSubclass) {
            QObject * const *qobject = reinterpret_cast<QObject * const *>(in);
            if (*qobject != 0) {
                QtJambiLink *link = QtJambiLink::findLinkForQObject(*qobject);
                if (link != 0) {
                    javaObject = link->javaObject(mEnvironment);
                    success = true;
                }
            } else {
                success = true;
            }
        }

        // If we're not dealing with a QObject or a link does not exist,
        // we need to create the link for the pointer. Unless we have a value type,
        // in which case we need to construct a new value and copy the old one and
        // then create link.
        if (!success) {
            QByteArray utfClassName = strClassName.toUtf8();
            QByteArray utfPackage = strPackage.toUtf8();

            jclass clazz = resolveClass(mEnvironment, utfClassName.constData(), utfPackage.constData());
            if (clazz != 0) {
                javaObject = mEnvironment->AllocObject(clazz);

                if (type & QObjectSubclass) {
                    QObject * const *qobject = reinterpret_cast<QObject * const *>(in);

                    // Map null pointers to null object
                    if (*qobject != 0)
                        success = qtjambi_construct_qobject(mEnvironment, javaObject, *qobject);
                    else
                        success = true;
                } else if (type & Value) {
                    int metaType = QMetaType::type(internalTypeName.toLatin1().constData());

                    if (metaType != QMetaType::Void) {
                        void *object = QMetaType::construct(metaType, in);
                        success = qtjambi_construct_object(mEnvironment, javaObject, object, metaType);
                    }
                } else {
                    void * const *pointer = reinterpret_cast<void * const *>(in);

                    if (*pointer != 0)
                        success = qtjambi_construct_object(mEnvironment, javaObject, *pointer);
                    else
                        success = true;
                }
            }
        }

        if (success)
            p->l = javaObject;
    }

    if (!success) {
        qWarning("QtJambiTypeManager::convertInternalToExternal: Cannot convert to type '%s' from '%s'",
            qPrintable(externalTypeName), qPrintable(internalTypeName));
    }

    return success;
}

/*!
    Always returns a pointer to a jvalue
*/
void *QtJambiTypeManager::constructExternal(const QString &, VariableContext, const void *copy)
{
    jvalue *val = new jvalue;
    val->j = 0;

    if (copy != 0)
        memcpy(val, copy, sizeof(jvalue));

    mOwnedVariables_external.insert(val, true);
    return val;
}

void QtJambiTypeManager::destroyExternal(void *p, VariableContext )
{
    jvalue *val = reinterpret_cast<jvalue *>(p);
    if (mOwnedVariables_external.contains(val)) {
        delete val;
        mOwnedVariables_external.remove(val);
    }
}

/*!
    \reimp

    Converts a Java value to a C++ value.

    \a in is expected to be a pointer to a jvalue union.
    \a out is expected to be a pointer to memory capable of holding the conversion. The
    destination type is given by getInternalTypeName() when passed \a externalTypeName.
*/
bool QtJambiTypeManager::convertExternalToInternal(const void *in, void **out,
                                                  const QString &externalTypeName,
                                                  const QString &internalTypeName,
                                                  VariableContext ctx)
{
    if (in == 0)
        return true;

    if (externalTypeName == QLatin1String("void"))
        return true;

    Q_ASSERT(out != 0);

    const jvalue *pval = reinterpret_cast<const jvalue *>(in);

    QString strClassName = className(externalTypeName);
    QString strPackage = package(externalTypeName);
    Type type = typeIdOfExternal(mEnvironment, strClassName, strPackage);
    int metaType = metaTypeOfInternal(internalTypeName, ctx);
    jvalue dummyVal;

    Type typemasked = Type(type & TypeMask);
    bool convert = typemasked != None;

    convert = convert && ((ctx == ReturnType && ((type & Primitive) == None))
                          || ctx == ArgumentType);
    if (convert) {
        dummyVal = convertToPrimitive(mEnvironment, pval->l, typemasked);
        pval = &dummyVal;
        type = Type(type | Primitive);
    }


    bool success = true;

    // Some temporary containers for copying
    void *temp = 0;
    QVariant v;
    const void *copy = 0;
    QString strCopy;   
    JObjectWrapper wrapper; // for catch all jobject conversion

    if (typemasked) {
        switch (typemasked) {
        case None: break ;
        case Integer: copy = &pval->i; break ;
        case Long: copy = &pval->j; break ;
        case Boolean: copy = &pval->z; break ;
        case Short: copy = &pval->s; break ;
        case Float: copy = &pval->f; break ;
        case Double: copy = &pval->d; break ;
        case Byte: copy = &pval->b; break ;
        case Char: copy = &pval->c; break ;
        default:
            success = false;
            break ;
        }
    } else if (type & String) {
        strCopy = qtjambi_to_qstring(mEnvironment, reinterpret_cast<jstring>(pval->l));
        copy = &strCopy;
    } else if ((type & NativePointer) == NativePointer) {
        temp = qtjambi_to_cpointer(mEnvironment, pval->l, 1);
        copy = &temp;
    } else if ((type & QtClass) && ((type & Value) || (type & Object))) {
        QtJambiLink *link = QtJambiLink::findLink(mEnvironment, pval->l);

        if (link == 0 || link->pointer() == 0) {
            qWarning("Java object of type '%s' not linked to C++ object",
                qPrintable(externalTypeName));
            success = false;
        } else if (type & Value) {
            copy = link->pointer();
        } else {
            temp = link->pointer();
            copy = &temp;
        }   
    } else if ((type & Value) || (type & Object)) {
        metaType = qMetaTypeId<JObjectWrapper>();
        wrapper = JObjectWrapper(mEnvironment, pval->l);
        copy = &wrapper;
    } else {
        success = false;
    }

    if (!success) {
        qWarning("QtJambiTypeManager::convertExternalToInternal: Couldn't convert external type "
            " '%s'", qPrintable(externalTypeName));
    } else {
        if (*out == 0) { // Construct a new one
            *out = constructInternal(internalTypeName, ctx, copy, metaType);
        } else { // Use QDataStream to copy the value over            
            if (metaType != int(QMetaType::Void)
                && (metaType < QMetaType::User || QMetaType::isRegistered(metaType))) {

                QByteArray ba;
                /* write the copy to the stream */ {
                    QDataStream stream(&ba, QIODevice::WriteOnly);
                    QMetaType::save(stream, metaType, copy);
                }

                /* read it back into the destination */ {
                    QDataStream stream(&ba, QIODevice::ReadOnly);
                    QMetaType::load(stream, metaType, *out);
                }
            }
        }
    }

    return success;
}
