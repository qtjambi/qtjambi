/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include <QDebug>

#include "qtjambivariant.h"

static const uint JOBJECTWRAPPER_TYPE = qMetaTypeId<JObjectWrapper>();
const QVariant::Handler *QtJambiVariant::lastHandler = 0;

Q_CORE_EXPORT const QVariant::Handler *qcoreVariantHandler();

inline static const JObjectWrapper *cast_to_object_wrapper(const QVariant::Private *d) 
{
    if (d->type == JOBJECTWRAPPER_TYPE) {
        if (d->is_shared) return reinterpret_cast<const JObjectWrapper *>(d->data.shared->ptr);
        else return reinterpret_cast<const JObjectWrapper *>(&d->data.c);
    }
    return 0;
}

static void construct(QVariant::Private *x, const void *copy)
{
    if (QtJambiVariant::getLastHandler())
        QtJambiVariant::getLastHandler()->construct(x, copy);
    else if (qcoreVariantHandler())
        qcoreVariantHandler()->construct(x, copy);
}

static void clear(QVariant::Private *d)
{
    if (QtJambiVariant::getLastHandler())
        QtJambiVariant::getLastHandler()->clear(d);
    else if (qcoreVariantHandler())
        qcoreVariantHandler()->clear(d);
}

static bool isNull(const QVariant::Private *d)
{
    if(d->type == JOBJECTWRAPPER_TYPE)
        return false;
    else if (QtJambiVariant::getLastHandler())
        return QtJambiVariant::getLastHandler()->isNull(d);
    else if (qcoreVariantHandler())
        return qcoreVariantHandler()->isNull(d);

    return false;
}


static bool convert(const QVariant::Private *d, QVariant::Type t,
                 void *result, bool *ok)
{
    const JObjectWrapper *wrapper = cast_to_object_wrapper(d);

    if(wrapper != 0) {
        JNIEnv *env = qtjambi_current_environment();
        StaticCache *sc = StaticCache::instance(env);
        jobject java_object =  wrapper->object;

        switch (t) {
        case QVariant::Int :
            sc->resolveQtEnumerator();           
            if (env->IsInstanceOf(java_object, sc->QtEnumerator.class_ref)) {
                *((int*)result) = env->CallIntMethod(java_object, sc->QtEnumerator.value);
                return true;
            }
            break;
                    
        case QVariant::String :
            sc->resolveObject();
            *((QString*)result) = qtjambi_to_qstring(env, static_cast<jstring>(env->CallObjectMethod(java_object, sc->Object.toString)));
            return true;
            break;
            
        default :
            return false;
        }
    }        

    if (QtJambiVariant::getLastHandler())
        return QtJambiVariant::getLastHandler()->convert(d, t, result, ok);
    else if (qcoreVariantHandler())
        return qcoreVariantHandler()->convert(d, t, result, ok);

    return false;
}

static bool compare(const QVariant::Private *a, const QVariant::Private *b)
{
    Q_ASSERT(a->type == b->type);
    const JObjectWrapper *wrapper_a = cast_to_object_wrapper(a);
    if (wrapper_a) {
        const JObjectWrapper *wrapper_b = cast_to_object_wrapper(a);
        JNIEnv *env = qtjambi_current_environment();
        StaticCache *sc = StaticCache::instance(env);
        return env->CallBooleanMethod(wrapper_a->object, sc->Object.equals, wrapper_b->object);
    }
    if (QtJambiVariant::getLastHandler())
        return QtJambiVariant::getLastHandler()->compare(a, b);
    else if (qcoreVariantHandler())
        return qcoreVariantHandler()->compare(a, b);
    
    return false;
}


#if !defined(QT_NO_DEBUG_STREAM) && !defined(Q_BROKEN_DEBUG_STREAM)
static void streamDebug(QDebug dbg, const QVariant &v)
{

    if((uint)v.userType() == JOBJECTWRAPPER_TYPE) {
        const JObjectWrapper wrapper = v.value<JObjectWrapper>();
        JNIEnv *env = qtjambi_current_environment();
        StaticCache *sc = StaticCache::instance(env);
        sc->resolveObject();
        jobject java_object =  wrapper.object;
        dbg << qtjambi_to_qstring(env, static_cast<jstring>(env->CallObjectMethod(java_object, sc->Object.toString)));
        return;
    }
    else if (QtJambiVariant::getLastHandler()) {
        QtJambiVariant::getLastHandler()->debugStream(dbg, v);
        return;
    }
    else if (qcoreVariantHandler()) {
        qcoreVariantHandler()->debugStream(dbg, v);
        return;
    }
}
#endif

const QVariant::Handler QtJambiVariant::handler = {
    ::construct,
    ::clear,
    ::isNull,
#ifndef QT_NO_DATASTREAM
    0,
    0,
#endif
    ::compare,
    ::convert,
    0,
#if !defined(QT_NO_DEBUG_STREAM) && !defined(Q_BROKEN_DEBUG_STREAM)
    ::streamDebug
#else
    0
#endif
};
