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

#include <qtjambi_core.h>

int main(int argc, char **argv)
{
    qtjambi_initialize_vm();

    JNIEnv *env = qtjambi_current_environment();

    if (argc < 2) {
        printf("Usage:\n"
               "    jambi [classname] options\n");
        return 0;
    }

    QString class_name = QLatin1String(argv[1]);

    jclass java_class = env->FindClass(class_name.replace('.', '/').toLatin1());
    if (!java_class) {
        printf("failed to find class: '%s'\n", qPrintable(class_name));
        return 0;
    }

    jmethodID main_id = env->GetStaticMethodID(java_class, "main", "([Ljava/lang/String;)V");
    if (!main_id) {
        printf("failed to find main(String[])\n");
        return 0;
    }

    jclass string_class = env->FindClass("java/lang/String");
    Q_ASSERT(string_class);


    jobjectArray args = env->NewObjectArray(argc - 2, string_class, 0);
    for (int i = 2; i < argc; ++i) {
        env->SetObjectArrayElement(args, i - 2, qtjambi_from_qstring(env, QLatin1String(argv[i])));
    }

    env->CallStaticVoidMethod(java_class, main_id, args);

    qtjambi_exception_check(env);

    qtjambi_destroy_vm();

    return 0;
}
