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

#include <QtCore/QMetaType>
#include <QtCore/QRect>
#include <QtCore/QTime>
#include <QtCore/QSize>
#include <QtCore/QBasicTimer>
#include <QtCore/QTextStream>
#include <QtCore/QFileInfo>

#include <QtGui/QMouseEvent>
#include <QtGui/QColor>
#include <QtGui/QPalette>
#include <QtGui/QCursor>
#include <QtGui/QIcon>
#include <QtGui/QPainter>
#include <QtGui/QPolygon>

#define QTQUALIFY(RETURNTYPE, METHOD) QTJAMBINAME(qt, QtObject, RETURNTYPE, METHOD)

QTQUALIFY(void, dispose)(JNIEnv *env, jobject java)
{
    QtJambiLink *link = QtJambiLink::findLink(env, java);
    if (link) {
        link->setEnvironment(env);
        link->javaObjectDisposed();
    }
}

QTQUALIFY(jobject,nativePointer)(JNIEnv *env, jobject javaRef)
{
    QtJambiLink *link = QtJambiLink::findLink(env, javaRef);

    if (link != 0)
        return qtjambi_from_cpointer(env, link->object(), 8, 1);
    else
        return 0;
}

QTQUALIFY(jobject,_1_1qt_1reassignLink)(JNIEnv *env, jclass, jlong old_native_id, jclass clazz, jobject constructor)
{
    QtJambiLink *link = reinterpret_cast<QtJambiLink *>(old_native_id);
    Q_ASSERT(link);

    jmethodID methodId = env->FromReflectedMethod(constructor);
    Q_ASSERT(methodId);

    jobject new_object = env->NewObject(clazz, methodId, 0);
    Q_ASSERT(new_object);

    QtJambiLink *new_link = 0;
    if (link->isQObject()) {
        QObject *qobject = link->qobject();
        link->resetObject();
        new_link = QtJambiLink::createLinkForQObject(env, new_object, qobject, !link->isGlobalReference());
    } else {
        void *ptr = link->pointer();
        bool wasCached = link->isCached();
        QString java_name = qtjambi_class_name(env, clazz);        
        link->resetObject();

        // Create new link. 
        new_link = QtJambiLink::createLinkForObject(env, new_object, ptr, java_name, wasCached);
        new_link->setMetaType(link->metaType());        
    }

    delete link;
    return new_object;
}

QTQUALIFY(void,finalize)(JNIEnv *env, jobject java)
{
    Q_ASSERT(env != 0);

    if (QtJambiLink *link = QtJambiLink::findLink(env, java)) {
        link->setEnvironment(env);
        link->javaObjectFinalized();
    }
}

QTQUALIFY(void, disableGarbageCollection)(JNIEnv *env, jobject object)
{
    if (QtJambiLink *link = QtJambiLink::findLink(env, object)) {
        link->disableGarbageCollection(env, object);
    }
}
