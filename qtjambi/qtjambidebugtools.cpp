#include "qtjambidebugtools_p.h"

#if defined(QTJAMBI_DEBUG_TOOLS)

#include "qtjambi_global.h"

#include <QHash>

    // ---- TOOLS
    typedef QHash<QString, int> CountsForName;

    static int sum_of(const CountsForName &countsForName)
    {
        int sum = 0;
        foreach (int value, countsForName.values())
            sum += value;
        return sum;
    }

    static void reset(CountsForName &countsForName, const QString &className)
    {
        if (className.isEmpty())
            countsForName.clear();
        else
            countsForName[className] = 0;
    }

    static int count(const CountsForName &countsForName, const QString &className)
    {
        if (className.isEmpty())
            return sum_of(countsForName);
        else
            return countsForName.value(className, 0);
    }

    // ---- MACROS
#define COUNTER_IMPLEMENTATION(NAME) \
    \
    Q_GLOBAL_STATIC(CountsForName, g_##NAME) \
    \
    static void qtjambi_reset_##NAME(const QString &className) \
    { \
        reset(*g_##NAME(), className); \
    } \
    \
    static int qtjambi_##NAME(const QString &className) \
    { \
        return count(*g_##NAME(), className); \
    } \
    \
    void qtjambi_increase_##NAME(const QString &className) \
    { \
        Q_ASSERT(!className.isEmpty()); \
    \
        (*g_##NAME())[className] ++; \
    } \
    \
    extern "C" JNIEXPORT void JNICALL \
    QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiDebugTools_reset_1##NAME ) \
    (JNIEnv *env, \
    jclass, \
    jstring className)\
    { \
        qtjambi_reset_##NAME(qtjambi_to_qstring(env, className)); \
    } \
    \
    extern "C" JNIEXPORT jint JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_internal_QtJambiDebugTools_##NAME) \
    (JNIEnv *env, jclass, \
    jstring className) \
    { \
        return qtjambi_##NAME(qtjambi_to_qstring(env, className)); \
    }


    // ---- IMPLEMENTATIONS

    COUNTER_IMPLEMENTATION(finalizedCount)
    COUNTER_IMPLEMENTATION(userDataDestroyedCount)
    COUNTER_IMPLEMENTATION(destructorFunctionCalledCount)
    COUNTER_IMPLEMENTATION(shellDestructorCalledCount)
    COUNTER_IMPLEMENTATION(objectInvalidatedCount)
    COUNTER_IMPLEMENTATION(disposeCalledCount)
    COUNTER_IMPLEMENTATION(linkDestroyedCount)
    COUNTER_IMPLEMENTATION(linkConstructedCount)

#else // QTJAMBI_DEBUG_TOOLS
#  error Don't include this file without QTJAMBI_DEBUG_TOOLS defined
#endif

