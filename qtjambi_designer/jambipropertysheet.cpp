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

#include "jambipropertysheet.h"

#include "qtjambi_core.h"
#include "qtjambi_utils.h"

#include <private/qdesigner_utils_p.h>

jclass class_NamedIntSet;

jfieldID field_value;
jfieldID field_names;
jfieldID field_isEnum;

using namespace qdesigner_internal;

QMap<QString, QVariant> jmap_to_qmap(JNIEnv *env, jobject jmap) {
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveMap();
    jobjectArray entrySet = qtjambi_map_entryset_array(env, jmap);
    int size = env->CallIntMethod(jmap, sc->Map.size);
    QMap<QString, QVariant> qmap;
    for (int i=0; i<size; ++i) {
        QPair<jobject, jobject> entry = qtjambi_entryset_array_get(env, entrySet, i);
        QString name = qtjambi_to_qstring(env, (jstring) entry.first);
        int value = env->CallIntMethod(entry.second, sc->Integer.intValue);
        qmap.insert(name, value);
    }
    return qmap;
}

static void resolve(JNIEnv *env)
{
    static int resolved = 0;
    if (resolved)
        return;
    resolved = 1;

    ClassData classes[] = {
        { &class_NamedIntSet, "com/trolltech/tools/designer/NamedIntSet" },
        { 0, 0 }
    };
    qtjambi_resolve_classes(env, classes);

    FieldData fields[] = {
        { &class_NamedIntSet, &field_value,  "value",  "I" },
        { &class_NamedIntSet, &field_names,  "names",  "Ljava/util/Map;" },
        { &class_NamedIntSet, &field_isEnum, "isEnum", "Z" },
        { 0, 0, 0, 0 }
    };
    qtjambi_resolve_fields(env, fields);
}

static QVariant qVariantEnum(JNIEnv *env, jobject value)
{
    EnumType et;
    et.value = (int) env->GetIntField(value, field_value);
    et.items = jmap_to_qmap(env, env->GetObjectField(value, field_names));

    QVariant variant;
    qVariantSetValue(variant, et);

    return variant;
}


static QVariant qVariantFlag(JNIEnv *env, jobject value)
{
    FlagType ft;
    ft.value = (int) env->GetIntField(value, field_value);
    ft.items = jmap_to_qmap(env, env->GetObjectField(value, field_names));

    QVariant variant;
    qVariantSetValue(variant, ft);

    return variant;
}


JambiPropertySheet::JambiPropertySheet(QObject *parent):
    QObject(parent)
{
    resolve(qtjambi_current_environment());
}


QVariant JambiPropertySheet::property(int index) const
{
    QVariant var = readProperty(index);

    if (var.isValid()) {
        JNIEnv *env = qtjambi_current_environment();
        jobject value = qtjambi_from_qvariant(env, var);

        if (env->IsSameObject(env->GetObjectClass(value), class_NamedIntSet)) {
            if (env->GetBooleanField(value, field_isEnum))
                return qVariantEnum(env, value);
            else
                return qVariantFlag(env, value);
        }
    }

    return var;
}

void JambiPropertySheet::setProperty(int index, const QVariant &value)
{
    writeProperty(index, value);
}

