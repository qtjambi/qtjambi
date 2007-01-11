#ifndef JNILAYER_H
#define JNILAYER_H

#include <jni.h>

#include <QtCore/QMap>
#include <QtCore/QString>
#include <QtCore/QVariant>

extern jclass class_ResourceBrowser;

extern jmethodID method_ResourceBrowser;

void jni_resolve(JNIEnv *env);


#endif
