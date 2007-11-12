/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qt;

/**
 * The QNoNativeResourceException is used to indicate that the C++
 * native resources of a Java object are destroyed while it is still
 * referenced in Java.
 * <p>
 * Most Qt Jambi objects consist of a C++ part, i.e., native C++
 * resources, and a Java peer that wraps the public functions of the
 * native objects. In most cases one can rely on the Java memory
 * model and let objects be garbage collected, in which case this
 * kind of exception will never occur (the garbage collector will
 * only collect unreferenced memory in Java).
 * <p>
 * In some cases, the ownership of an object is transferred to C++,
 * for instance, when QObjects are passed a parent or items are added
 * to a QGraphicsScene.  In these cases the C++ part of the object
 * can be deleted explicitly by, for instance, using the C++ "delete"
 * keyword. When the C++ part of the object has been deleted it can
 * no longer be used and this exception is thrown.
 * <p>
 * It is possible to check if an object has a valid C++ part or not
 * by checking that the value of nativeId() is non-null.
 */
public class QNoNativeResourcesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QNoNativeResourcesException(String message)
    {
        super(message);
    }
}
