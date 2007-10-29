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

import java.lang.annotation.*;

/**
 * This annotation has been deprecated and will be removed from
 * the next minor release of Qt Jambi. A change in the implementation
 * of the property system for the next minor release has obsoleted
 * the use of this annotation.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyOrder {
    /**
     * Returns the sort order of the property.
     */
    public int value() default 0;
}
