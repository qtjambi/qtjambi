#ifndef JNILAYER_H
#define JNILAYER_H

#include <jni.h>

#include <QtCore/QMap>
#include <QtCore/QString>
#include <QtCore/QVariant>

extern jclass class_PropertySheet;
extern jclass class_NamedIntSet;

extern jmethodID method_createPropertySheet;
extern jmethodID method_count;
extern jmethodID method_hasReset;
extern jmethodID method_indexOf;
extern jmethodID method_isAttribute;
extern jmethodID method_isChanged;
extern jmethodID method_isVisible;
extern jmethodID method_property;
extern jmethodID method_propertyGroup;
extern jmethodID method_propertyName;
extern jmethodID method_reset;
extern jmethodID method_setAttribute;
extern jmethodID method_setChanged;
extern jmethodID method_setProperty;
extern jmethodID method_setPropertyGroup;
extern jmethodID method_setVisible;

extern jfieldID field_value;
extern jfieldID field_names;
extern jfieldID field_isEnum;

void resolve(JNIEnv *env);
QMap<QString, QVariant> jmap_to_qmap(JNIEnv *env, jobject jmap);



#endif
