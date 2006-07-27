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

#include <qglobal.h>

#if defined(Q_WS_WIN)
#  if !defined(QT_QTJAMBI_EXPORT) && !defined(QT_QTJAMBI_IMPORT)
#    define QT_QTJAMBI_EXPORT
#  elif defined(QT_QTJAMBI_IMPORT)
#    if defined(QT_QTJAMBI_EXPORT)
#      undef QT_QTJAMBI_EXPORT
#    endif
#    define QT_QTJAMBI_EXPORT __declspec(dllimport)
#  elif defined(QT_QTJAMBI_EXPORT)
#    undef QT_QTJAMBI_EXPORT
#    define QT_QTJAMBI_EXPORT __declspec(dllexport)
#  endif
#else
# if defined(QT_QTJAMBI_EXPORT)
#   undef QT_QTJAMBI_EXPORT
# endif
#  define QT_QTJAMBI_EXPORT
#endif

#if defined Q_WS_MAC
#  include <JavaVM/jni.h>
#else
#  include <jni.h>
#endif

#endif
