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

#ifndef QDYNAMICMETAOBJECT_H
#define QDYNAMICMETAOBJECT_H

#include "qtjambi_global.h"

#include <QtCore/QString>
#include <QtCore/QByteArray>
#include <QtCore/QMetaObject>

class QtDynamicMetaObjectPrivate;

class QTJAMBI_EXPORT QtDynamicMetaObject: public QMetaObject
{
public:
    QtDynamicMetaObject(JNIEnv *jni_env, jclass java_class, const QMetaObject *original_meta_object);
    ~QtDynamicMetaObject();

    int invokeSignalOrSlot(JNIEnv *env, jobject object, int _id, void **_a) const;
    int readProperty(JNIEnv *env, jobject object, int _id, void **_a) const;
    int writeProperty(JNIEnv *env, jobject object, int _id, void **_a) const;
    int resetProperty(JNIEnv *env, jobject object, int _id, void **_a) const;
    int queryPropertyDesignable(JNIEnv *env, jobject object, int _id, void **_a) const;

    int originalSignalOrSlotSignature(JNIEnv *env, int _id, QString *signature) const;

private:
    QtDynamicMetaObjectPrivate *d_ptr;
    Q_DECLARE_PRIVATE(QtDynamicMetaObject);
};

#endif // QDYNAMICMETAOBJECT_H
