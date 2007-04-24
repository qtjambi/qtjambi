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
#include "qtjambi_core.h"
#include "qtjambitypemanager.h"

#include <QtCore/QVariant>
#include <QtCore/QPoint>
#include <QtCore/QPointF>
#include <QtCore/QSize>
#include <QtCore/QSizeF>
#include <QtCore/QDateTime>
#include <QtCore/QDate>
#include <QtCore/QTime>
#include <QtCore/QLine>
#include <QtCore/QLineF>
#include <QtCore/QRegExp>
#include <QtCore/QRect>
#include <QtCore/QRectF>
#include <QtCore/QStringList>
#include <QtCore/QLocale>
#include <QtCore/QChar>
#include <QtCore/QBitArray>
#include <QtCore/QByteArray>

extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1convert)
    (JNIEnv *env, jclass, jint type, jobject object)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    if (v.convert(QVariant::Type(type)))
        return qtjambi_from_qvariant(env, v);
    else
        return 0;
}

extern "C" JNIEXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1canConvert)
    (JNIEnv *env, jclass, jobject obj, jint type)
{
    QVariant v = qtjambi_to_qvariant(env, obj);
    return v.canConvert(QVariant::Type(type));
}

static inline void setOk(JNIEnv *env, jobjectArray ok, bool isOk)
{
    if (!env->IsSameObject(ok, 0) && env->GetArrayLength(ok)) {
        StaticCache *sc = StaticCache::instance(env);
        sc->resolveBoolean();
        jfieldID fieldId = isOk ? sc->Boolean.field_TRUE : sc->Boolean.field_FALSE;
        jobject boolObject = env->GetStaticObjectField(sc->Boolean.class_ref, fieldId);
        env->SetObjectArrayElement(ok, 0, boolObject);
    }
}

extern "C" JNIEXPORT jdouble JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toDouble)
    (JNIEnv *env, jclass, jobject object, jobjectArray ok)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    bool isOk = false;
    double returned = v.toDouble(&isOk);
    setOk(env, ok, isOk);
    return returned;
}

extern "C" JNIEXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toInt)
    (JNIEnv *env, jclass, jobject object, jobjectArray ok)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    bool isOk = false;
    int returned = v.toInt(&isOk);
    setOk(env, ok, isOk);
    return returned;
}

extern "C" JNIEXPORT jlong JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toLong)
    (JNIEnv *env, jclass, jobject object, jobjectArray ok)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    bool isOk = false;
    qlonglong returned = v.toLongLong(&isOk);
    setOk(env, ok, isOk);
    return returned;
}

extern "C" JNIEXPORT jobject JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toString)
    (JNIEnv *env, jclass, jobject object)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    return QtJambiTypeManager::qStringToJstring(env, v.toString());
}

extern "C" JNIEXPORT jboolean JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toBoolean)
    (JNIEnv *env, jclass, jobject object)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    return v.toBool();
}

extern "C" JNIEXPORT jchar JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QVariant__1_1qt_1toChar)
    (JNIEnv *env, jclass, jobject object)
{
    QVariant v = qtjambi_to_qvariant(env, object);
    QChar c = v.toChar();
    return c.unicode();
}
