#include "qtjambiconcurrent.h"

#include <qtjambi_core.h>

#include <QtCore>

#ifndef QT_NO_CONCURRENT

class Functor {
public:
    Functor(jobject functor) : m_functor(0)
    { 
        init(functor);
    }

    Functor(const Functor &other)
    {
        init(other.m_functor);
    }
    
    virtual ~Functor()
    {
        JNIEnv *env = qtjambi_current_environment();        
        if (env != 0)
            env->DeleteGlobalRef(m_functor);
    }

protected:
    jobject m_functor;

private:
    void init(jobject functor) {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0)
            m_functor = env->NewGlobalRef(functor);
    }

    
};

class MapFunctor: public Functor {
public:
    MapFunctor(jobject javaMapFunctor) : Functor(javaMapFunctor) {}
    MapFunctor(const MapFunctor &other) : Functor(other) {}

    void operator ()(JObjectWrapper &wrapper) 
    {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_functor) {
            jobject javaObject = qtjambi_from_jobjectwrapper(env, wrapper);

            StaticCache *sc = StaticCache::instance(env);
            sc->resolveQtConcurrent_MapFunctor();

            env->CallVoidMethod(m_functor, sc->QtConcurrent_MapFunctor.map, javaObject);
        } else {
            qWarning("Map functor called with invalid data. JNI Environment == %p, java functor object == %p",
                    env, m_functor);
        }
    }
};

class MappedFunctor: public Functor {
public:
    typedef JObjectWrapper result_type;

    MappedFunctor(jobject javaMappedFunctor) : Functor(javaMappedFunctor) {}
    MappedFunctor(const MapFunctor &other) : Functor(other) {}

    JObjectWrapper operator ()(const JObjectWrapper &wrapper) 
    {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_functor) {
            jobject javaObject = qtjambi_from_jobjectwrapper(env, wrapper);

            StaticCache *sc = StaticCache::instance(env);
            sc->resolveQtConcurrent_MappedFunctor();

            jobject javaResult = env->CallObjectMethod(m_functor, sc->QtConcurrent_MappedFunctor.map, javaObject);
            return qtjambi_to_jobjectwrapper(env, javaResult);
        } else {
            qWarning("Mapped functor called with invalid data. JNI Environment == %p, java functor object == %p",
                    env, m_functor);
            return JObjectWrapper();
        }
    }
};

class ReducedFunctor: public Functor {
public:
    ReducedFunctor(jobject javaReducedFunctor) : Functor(javaReducedFunctor) {}
    ReducedFunctor(const ReducedFunctor &other) : Functor(other) {}

    void operator()(JObjectWrapper &result, const JObjectWrapper &wrapper) 
    {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_functor != 0) {
            jobject javaObject = qtjambi_from_jobjectwrapper(env, wrapper);
            jobject javaResult = qtjambi_from_jobjectwrapper(env, result);

            StaticCache *sc = StaticCache::instance(env);
            sc->resolveQtConcurrent_ReducedFunctor();

            env->CallVoidMethod(m_functor, sc->QtConcurrent_ReducedFunctor.reduce, javaResult, javaObject);
        } else {
            qWarning("Reduce functor called with invalid data. JNI Environment == %p, java functor object == %p",
                    env, m_functor);
        }  
    }

};

class FilteredFunctor: public Functor {
public:
    FilteredFunctor(jobject javaFilteredFunctor) : Functor(javaFilteredFunctor) {}
    FilteredFunctor(const FilteredFunctor &other) : Functor(other) {}

    bool operator()(const JObjectWrapper &wrapper) {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_functor != 0) {
            jobject javaObject = qtjambi_from_jobjectwrapper(env, wrapper);

            StaticCache *sc = StaticCache::instance(env);
            sc->resolveQtConcurrent_FilteredFunctor();

            return env->CallBooleanMethod(m_functor, sc->QtConcurrent_FilteredFunctor.filter, javaObject);
        } else {
            qWarning("Filtered functor called with invalid data. JNI Environment == %p, java functor object == %p",
                    env, m_functor);
            return false;
        }
    }
};

class RunFunctorBase: public Functor 
{
public:    

    RunFunctorBase(jobject javaThis, jclass declaringClass, jmethodID javaMethodId, jobjectArray javaArguments) 
        : Functor(javaThis), m_declaring_class(0), m_java_arguments(0), m_method_id(javaMethodId) 
    {
        init(declaringClass, javaArguments);
    }

    RunFunctorBase(const RunFunctorBase &other) 
        : Functor(other), m_declaring_class(0), m_java_arguments(0), m_method_id(other.m_method_id)
    { 
        init(other.m_declaring_class, other.m_java_arguments); 
    }

    ~RunFunctorBase() { 
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0) {
            if (m_java_arguments != 0) 
                env->DeleteGlobalRef(m_java_arguments);
            if (m_declaring_class != 0)
                env->DeleteGlobalRef(m_declaring_class);
        }
    }


private:
    void init(jclass declaringClass, jobjectArray javaArguments) {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && javaArguments != 0)
            m_java_arguments = reinterpret_cast<jobjectArray>(env->NewGlobalRef(javaArguments));
        if (env != 0 && declaringClass != 0)
            m_declaring_class = reinterpret_cast<jclass>(env->NewGlobalRef(declaringClass));

        int len = env->GetArrayLength(m_java_arguments);
        m_arguments = QVarLengthArray<jvalue, 16>(len);
        for (int i=0; i<len; ++i)
            m_arguments[i].l = env->GetObjectArrayElement(m_java_arguments, i);
    }

protected:
    jclass m_declaring_class;
    jobjectArray m_java_arguments;
    jmethodID m_method_id;
    QVarLengthArray<jvalue, 16> m_arguments;
};

class RunFunctor: public RunFunctorBase {
public:
    typedef JObjectWrapper result_type;

    RunFunctor(jobject javaThis, jclass declaringClass, jmethodID javaMethodId, jobjectArray javaArguments) 
        : RunFunctorBase(javaThis, declaringClass, javaMethodId, javaArguments) 
    {
    }

    RunFunctor(const RunFunctor &other) 
        : RunFunctorBase(other) 
    {
    }

    JObjectWrapper operator()() {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_method_id != 0) {
            jobject javaResult = 0;
            if (m_functor != 0)
                javaResult = env->CallObjectMethod(m_functor, m_method_id, m_arguments.data());
            else
                javaResult = env->CallStaticObjectMethod(m_declaring_class, m_method_id, m_arguments.data());
            return JObjectWrapper(env, javaResult);
        }
    }
};

class RunVoidFunctor: public RunFunctorBase {
public:
    typedef void result_type;

    RunVoidFunctor(jobject javaThis, jclass declaringClass, jmethodID javaMethodId, jobjectArray javaArguments) 
        : RunFunctorBase(javaThis, declaringClass, javaMethodId, javaArguments) 
    {
    }

    RunVoidFunctor(const RunFunctor &other) 
        : RunFunctorBase(other) 
    {
    }

    void operator()() {
        JNIEnv *env = qtjambi_current_environment();
        if (env != 0 && m_method_id != 0) {
            if (m_functor != 0)
                env->CallVoidMethod(m_functor, m_method_id, m_arguments.data());
            else
                env->CallStaticVoidMethod(m_declaring_class, m_method_id, m_arguments.data());
        }
    }
};

static QList<JObjectWrapper> convertJavaSequenceToCpp(JNIEnv *env, jobject javaSequence) 
{
    jobjectArray array = qtjambi_collection_toArray(env, javaSequence);
    jsize arraySize = env->GetArrayLength(array);

    QList<JObjectWrapper> returned;
    for (int i=0; i<arraySize; ++i) {
        jobject javaElement = env->GetObjectArrayElement(array, i);
        JObjectWrapper wrapper = qtjambi_to_jobjectwrapper(env, javaElement);
        QTJAMBI_EXCEPTION_CHECK(env);
        returned << wrapper;
    }

    return returned;
}

static jobject convertCppSequenceToJava(JNIEnv *env, const QList<JObjectWrapper> &sequence)
{
    jobject returned = qtjambi_arraylist_new(env, sequence.size());
    for (int i=0; i<sequence.size(); ++i)
        qtjambi_collection_add(env, returned, qtjambi_from_jobjectwrapper(env, sequence.at(i)));

    return returned;
}

static jobject convertCppFutureToJava(JNIEnv *env, const QFuture<JObjectWrapper> &future)
{
    return qtjambi_from_object(env, &future, "QFuture", "com/trolltech/qt/core/", true);
}

static jobject convertCppFutureVoidToJava(JNIEnv *env, const QFuture<void> &future)
{
    return qtjambi_from_object(env, &future, "QFutureVoid", "com/trolltech/qt/core/", true);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_map)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMapFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MapFunctor mapFunctor(javaMapFunctor);
    QFuture<void> future = QtConcurrent::map(sequence, mapFunctor);

    return convertCppFutureVoidToJava(__jni_env, future);
}

extern "C" JNIEXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_blockingMap)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMapFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MapFunctor mapFunctor(javaMapFunctor);
    QtConcurrent::blockingMap(sequence, mapFunctor);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_mapped)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMappedFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MappedFunctor mappedFunctor(javaMappedFunctor);
    QFuture<JObjectWrapper> result = QtConcurrent::mapped(sequence, mappedFunctor);

    return convertCppFutureToJava(__jni_env, result);
}


extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_blockingMapped)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMappedFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MappedFunctor mappedFunctor(javaMappedFunctor);
    QList<JObjectWrapper> result = QtConcurrent::blockingMapped(sequence, mappedFunctor);

    return convertCppSequenceToJava(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_blockingMappedReduced)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMappedFunctor,
 jobject javaReducedFunctor,
 jint options)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MappedFunctor mappedFunctor(javaMappedFunctor);
    ReducedFunctor reduceFunctor(javaReducedFunctor);
    JObjectWrapper result = QtConcurrent::blockingMappedReduced<JObjectWrapper, QList<JObjectWrapper>, MappedFunctor, ReducedFunctor>(sequence, mappedFunctor, reduceFunctor, QtConcurrent::ReduceOptions(options));

    return qtjambi_from_jobjectwrapper(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_mappedReduced)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaMappedFunctor,
 jobject javaReducedFunctor,
 jint options)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);
    
    MappedFunctor mappedFunctor(javaMappedFunctor);
    ReducedFunctor reduceFunctor(javaReducedFunctor);
    QFuture<JObjectWrapper> result = QtConcurrent::mappedReduced<JObjectWrapper, QList<JObjectWrapper>, MappedFunctor, ReducedFunctor>(sequence, mappedFunctor, reduceFunctor, QtConcurrent::ReduceOptions(options));

    return convertCppFutureToJava(__jni_env, result);
}

// ### run not impl.
/*extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_run)
(JNIEnv *__jni_env,
 jclass,
 jobject method,
 jobjectArray args)
{
    RunFunctor runFunctor(method, args);
    QFuture<JObjectWrapper> result = QtConcurrent::run(runFunctor);

    return convertCppFutureToJava(__jni_env, result);
}*/

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_filtered)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaFilteredFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);

    FilteredFunctor filteredFunctor(javaFilteredFunctor);
    QFuture<JObjectWrapper> result = QtConcurrent::filtered(sequence, filteredFunctor);

    return convertCppFutureToJava(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_blockingFiltered)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaFilteredFunctor)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);

    FilteredFunctor filteredFunctor(javaFilteredFunctor);
    QList<JObjectWrapper> result = QtConcurrent::blockingFiltered(sequence, filteredFunctor);

    return convertCppSequenceToJava(__jni_env, result);
}


extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_filteredReduced)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaFilteredFunctor,
 jobject javaReducedFunctor,
 jint options)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);

    FilteredFunctor filteredFunctor(javaFilteredFunctor);
    ReducedFunctor reducedFunctor(javaReducedFunctor);
    QFuture<JObjectWrapper> result = QtConcurrent::filteredReduced<JObjectWrapper, QList<JObjectWrapper>, FilteredFunctor, ReducedFunctor>(sequence, filteredFunctor, reducedFunctor, QtConcurrent::ReduceOptions(options));

    return convertCppFutureToJava(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_blockingFilteredReduced)
(JNIEnv *__jni_env,
 jclass,
 jobject javaSequence,
 jobject javaFilteredFunctor,
 jobject javaReducedFunctor,
 jint options)
{
    QList<JObjectWrapper> sequence = convertJavaSequenceToCpp(__jni_env, javaSequence);

    FilteredFunctor filteredFunctor(javaFilteredFunctor);
    ReducedFunctor reducedFunctor(javaReducedFunctor);
    JObjectWrapper result = QtConcurrent::blockingFilteredReduced<JObjectWrapper, QList<JObjectWrapper>, FilteredFunctor, ReducedFunctor>(sequence, filteredFunctor, reducedFunctor, QtConcurrent::ReduceOptions(options));

    return qtjambi_from_jobjectwrapper(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_runPrivate)
(JNIEnv *__jni_env,
 jclass,
 jobject javaThis,
 jclass declaringClass,
 jobject javaMethod,
 jobjectArray javaArgs)
{
    jmethodID methodId = __jni_env->FromReflectedMethod(javaMethod);
    Q_ASSERT(methodId);

    RunFunctor runFunctor(javaThis, declaringClass, methodId, javaArgs);
    QFuture<JObjectWrapper> result = QtConcurrent::run(runFunctor);

    return convertCppFutureToJava(__jni_env, result);
}

extern "C" JNIEXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_core_QtConcurrent_runVoidMethodPrivate)
(JNIEnv *__jni_env,
 jclass,
 jobject javaThis,
 jclass declaringClass,
 jobject javaMethod,
 jobjectArray javaArgs)
{
    jmethodID methodId = __jni_env->FromReflectedMethod(javaMethod);
    Q_ASSERT(methodId);

    RunVoidFunctor runFunctor(javaThis, declaringClass, methodId, javaArgs);
    QFuture<void> result = QtConcurrent::run(runFunctor);

    return convertCppFutureVoidToJava(__jni_env, result);
}


#endif // QT_NO_CONCURRENT
