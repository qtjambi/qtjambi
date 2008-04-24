#ifndef QTJAMBI_DEBUG_TOOLS_H
#define QTJAMBI_DEBUG_TOOLS_H

#if defined(QTJAMBI_DEBUG_TOOLS)

#include "qtjambi_global.h"

#define QTJAMBI_COUNTER_DECLARATIONS(NAME) \
    void QTJAMBI_EXPORT qtjambi_increase_##NAME(const QString &className)

    QTJAMBI_COUNTER_DECLARATIONS(finalizedCount);
    QTJAMBI_COUNTER_DECLARATIONS(userDataDestroyedCount);
    QTJAMBI_COUNTER_DECLARATIONS(destructorFunctionCalledCount);
    QTJAMBI_COUNTER_DECLARATIONS(shellDestructorCalledCount);
    QTJAMBI_COUNTER_DECLARATIONS(objectInvalidatedCount);
    QTJAMBI_COUNTER_DECLARATIONS(disposeCalledCount);
    QTJAMBI_COUNTER_DECLARATIONS(linkDestroyedCount);
    QTJAMBI_COUNTER_DECLARATIONS(linkConstructedCount);

#else
#  error Don't include this file without QTJAMBI_DEBUG_TOOLS defined
#endif

#endif
