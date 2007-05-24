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

#ifndef JNILAYER_H
#define JNILAYER_H

#include <jni.h>

#include <QtCore/QMap>
#include <QtCore/QString>
#include <QtCore/QVariant>

extern jclass class_ResourceBrowser;

extern jmethodID method_ResourceBrowser;

void jni_resolve(JNIEnv *env);


#endif
