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
 * The super interface of all interface types in Qt Jambi.
 */
public interface QtObjectInterface {
    
    /**
     * Function that returns a unique identifier for a Qt Jambi object.
     * @return A value which uniquely identifies the native resources held by an object in their life time.
     */
    public long nativeId();    
    
    public void disableGarbageCollection();
}
