#include "qtjambi_global.h"
#include "qtjambi_utils.h"
#include "qtjambi_core.h"


void qtjambi_resolve_classes(JNIEnv *env, ClassData *data)
{
    // Resolve Data...
    for (int i=0; data[i].cl; ++i) {
        *data[i].cl = (jclass) env->NewGlobalRef(qtjambi_find_class(env, data[i].name));
        Q_ASSERT_X(*data[i].cl, "Failed to resolve class", data[i].name);
    }
}


void qtjambi_resolve_fields(JNIEnv *env, FieldData *data)
{
    // Resovle fields
    for (int i=0; data[i].cl; ++i) {
        *data[i].id = env->GetFieldID(*data[i].cl,
                                      data[i].name,
                                      data[i].signature);
        Q_ASSERT_X(*data[i].id,
                   data[i].name,
                   data[i].signature);
    }

}


void qtjambi_resolve_static_fields(JNIEnv *env, FieldData *data)
{
    // Resovle fields
    for (int i=0; data[i].cl; ++i) {
        *data[i].id = env->GetStaticFieldID(*data[i].cl,
                                            data[i].name,
                                            data[i].signature);
        Q_ASSERT_X(*data[i].id,
                   data[i].name,
                   data[i].signature);
    }

}


void qtjambi_resolve_methods(JNIEnv *env, MethodData *data)
{
    // Resolve member functions
    for (int i=0; data[i].cl; ++i) {
        *data[i].id = env->GetMethodID(*data[i].cl,
                                       data[i].name,
                                       data[i].signature);
        Q_ASSERT_X(*data[i].id,
                   data[i].name,
                   data[i].signature);
    }


}


void qtjambi_resolve_static_methods(JNIEnv *env, MethodData *data)
{
    // Resolve static functions
    for (int i=0; data[i].cl; ++i) {
        *data[i].id = env->GetStaticMethodID(*data[i].cl,
                                             data[i].name,
                                             data[i].signature);
        Q_ASSERT_X(*data[i].id,
                   data[i].name,
                   data[i].signature);
    }

}





