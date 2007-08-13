#ifndef QDYNAMICMETAOBJECT_H
#define QDYNAMICMETAOBJECT_H

#include <QtCore/QByteArray>
#include <QtCore/QMetaObject>

#include <jni.h>

class QDynamicMetaObject: public QMetaObject
{
public:
    QDynamicMetaObject(JNIEnv *jni_env, jclass java_class, const QMetaObject *original_meta_object, jobject object);

private:
    void initialize(JNIEnv *jni_env, jclass java_class, const QMetaObject *original_meta_object, jobject object);

};

#endif // QDYNAMICMETAOBJECT_H
