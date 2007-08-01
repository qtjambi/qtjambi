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
 * This annotation is used to mark a method as a getter
 * for a Qt property. The annotation gives the name of
 * the property, and whether it is enabled for reading.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyReader {
    /** Returns true if the property is enabled; otherwise, returns false. */
    boolean enabled() default true; 
    /** Returns the name of the property. */
    String name() default "";
}
