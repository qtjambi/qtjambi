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

#include <QtCore/QCoreApplication>

class QObjectPrivateAccessor : public QObjectData
{
public:
    virtual ~QObjectPrivateAccessor() { }
    int thread;
    QObject *currentSender;
};


class QObjectAccessor {
public:
    virtual ~QObjectAccessor() { }
    QObjectPrivateAccessor *d_ptr;
};


extern "C" JNIEXPORT void JNICALL
Java_com_trolltech_qt_QtJambi_1LibraryInitializer_initialize(JNIEnv *, jclass)
{
    QCoreApplication::postEvent((QObject *) 0xFeedFace, (QEvent *) 0x00c0ffee);
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_trolltech_qt_QThreadManager_releaseNativeResources(JNIEnv *env, jclass)
{
    return qtjambi_release_threads(env);
}


extern "C" JNIEXPORT jobject JNICALL
Java_com_trolltech_qt_QtJambiInternal_nativeSwapQObjectSender
(JNIEnv *env, jclass, jlong r, jlong s)
{
    QObject *the_receiver = reinterpret_cast<QObject *>(qtjambi_from_jlong(r));
    QObject *the_sender = reinterpret_cast<QObject *>(qtjambi_from_jlong(s));
    if (the_receiver == 0)
        return 0;

    QObjectPrivateAccessor *d = (reinterpret_cast<QObjectAccessor *>(the_receiver))->d_ptr;
    if (d == 0)
        return 0;

    QObject *prev = d->currentSender;
    d->currentSender = the_sender;
    return qtjambi_from_qobject(env, prev, "QObject", "com/trolltech/qt/core/");
}


extern "C" JNIEXPORT jobject JNICALL
Java_com_trolltech_qt_QtJambiInternal_sender(JNIEnv *env, jclass, jobject obj)
{
    QObject *qobject = qtjambi_to_qobject(env, obj);
    QObjectPrivateAccessor *d = (reinterpret_cast<QObjectAccessor *>(qobject))->d_ptr;
    return qtjambi_from_qobject(env, d->currentSender, "QObject", "com.trolltech.qt.core");
}


extern "C" JNIEXPORT jobject JNICALL Java_com_trolltech_qt_QtJambiInternal_createExtendedEnum(JNIEnv *env, jclass, jint value, jint ordinal, jclass enumClass, jstring name)
{
    jmethodID methodId = env->GetMethodID(enumClass, "<init>", "(Ljava/lang/String;II)V");
    jobject object = env->NewObject(enumClass, methodId, name, ordinal, value);
    return object;
}

