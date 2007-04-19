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

#include "qtjambi_core.h"

extern "C" JNIEXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtInfo_majorVersion(JNIEnv *, jclass))
{
    return (QT_VERSION & 0x00ff0000) >> 16;
}

extern "C" JNIEXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtInfo_minorVersion(JNIEnv *, jclass))
{
    return (QT_VERSION & 0x0000ff00) >> 8;
}

extern "C" JNIEXPORT jint JNICALL
QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_QtInfo_patchVersion(JNIEnv *, jclass))
{
    return (QT_VERSION & 0x000000ff);
}
