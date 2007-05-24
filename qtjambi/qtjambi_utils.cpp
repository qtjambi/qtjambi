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

#include "qtjambi_global.h"
#include "qtjambi_utils.h"
#include "qtjambi_core.h"

bool qtjambi_resolve_classes(JNIEnv *env, ClassData *data)
{
    // Resolve Data...
    for (int i=0; data[i].cl; ++i) {
        jclass cl = qtjambi_find_class(env, data[i].name);

        if (cl == 0) return false;
        *data[i].cl =(jclass) env->NewGlobalRef(cl);
    }

    return true;
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





