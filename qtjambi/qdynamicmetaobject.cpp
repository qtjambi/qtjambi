#include "qdynamicmetaobject.h"
#include "qtjambi_core.h"

#include <QtCore/QHash>


QDynamicMetaObject::QDynamicMetaObject(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object, jobject object) 
{
    Q_ASSERT(env != 0);
    Q_ASSERT(java_class != 0);

    initialize(env, java_class, original_meta_object, object);
}

void QDynamicMetaObject::initialize(JNIEnv *env, jclass java_class, const QMetaObject *original_meta_object, jobject object)
{
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveQtJambiInternal();

    jobject meta_data_struct = env->CallStaticObjectMethod(sc->QtJambiInternal.class_ref, sc->QtJambiInternal.buildMetaData, java_class, object);
    Q_ASSERT(meta_data_struct);

    sc->resolveMetaData();
    jintArray meta_data = (jintArray) env->GetObjectField(meta_data_struct, sc->MetaData.metaData);
    Q_ASSERT(meta_data);

    jbyteArray string_data = (jbyteArray) env->GetObjectField(meta_data_struct, sc->MetaData.stringData);
    Q_ASSERT(string_data);

    d.superdata = qtjambi_metaobject_for_class(env, env->GetSuperclass(java_class), original_meta_object, object);

    int string_data_len = env->GetArrayLength(string_data);
    d.stringdata = new char[string_data_len];

    int meta_data_len = env->GetArrayLength(meta_data);
    d.data = new uint[meta_data_len];
    d.extradata = 0;

    env->GetByteArrayRegion(string_data, 0, string_data_len, (jbyte *) d.stringdata);
    env->GetIntArrayRegion(meta_data, 0, meta_data_len, (jint *) d.data);

    fprintf(stderr, "0: ");
    for (int i=0; i<string_data_len; ++i) {
        if (d.stringdata[i] == 0) fprintf(stderr, "\n%d: ", i);
        else fprintf(stderr, "%c", d.stringdata[i]);
    }
    fprintf(stderr, "\n\n");
}
