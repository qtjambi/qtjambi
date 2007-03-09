#ifndef QTJAMBI_UTILS_H
#define QTJAMBI_UTILS_H

struct ClassData {
    jclass *cl;
    const char *name;
};

struct MethodData {
    jclass *cl;
    jmethodID *id;
    const char *name;
    const char *signature;
};

struct FieldData {
    jclass *cl;
    jfieldID *id;
    const char *name;
    const char *signature;
};

QTJAMBI_EXPORT bool qtjambi_resolve_classes(JNIEnv *env, ClassData *data);
QTJAMBI_EXPORT void qtjambi_resolve_fields(JNIEnv *env, FieldData *data);
QTJAMBI_EXPORT void qtjambi_resolve_static_fields(JNIEnv *env, FieldData *data);
QTJAMBI_EXPORT void qtjambi_resolve_methods(JNIEnv *env, MethodData *data);
QTJAMBI_EXPORT void qtjambi_resolve_static_methods(JNIEnv *env, MethodData *data);

#endif // QTJAMBI_UTILS_H
