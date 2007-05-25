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

#ifndef QTJAMBI_GLOBAL_H
#define QTJAMBI_GLOBAL_H

#define _CRT_SECURE_NO_DEPRECATE

#include <qglobal.h>

#if QT_VERSION < 0x040300
#error "Qt Jambi requires 4.3"
#endif
    // ### remove for final release


#if defined(Q_WS_WIN)
#  if !defined(QTJAMBI_EXPORT) && !defined(QT_QTJAMBI_IMPORT)
#    define QTJAMBI_EXPORT
#  elif defined(QT_QTJAMBI_IMPORT)
#    if defined(QTJAMBI_EXPORT)
#      undef QTJAMBI_EXPORT
#    endif
#    define QTJAMBI_EXPORT __declspec(dllimport)
#  elif defined(QTJAMBI_EXPORT)
#    undef QTJAMBI_EXPORT
#    define QTJAMBI_EXPORT __declspec(dllexport)
#  endif
#else
# if defined(QTJAMBI_EXPORT)
#   undef QTJAMBI_EXPORT
# endif
#  define QTJAMBI_EXPORT
#endif

#if defined Q_WS_MAC
#  include <JavaVM/jni.h>
#else
#  include <jni.h>
#endif

#if defined (Q_CC_MINGW)
#  define QTJAMBI_FUNCTION_PREFIX(name) _##name
#else
#  define QTJAMBI_FUNCTION_PREFIX(name) name
#endif

typedef void (*PtrDestructorFunction)(void *);

#endif
