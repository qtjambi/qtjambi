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
 * QtPropertyResetter annotates a method as being a resetter for
 * a property.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyResetter {
    /** Returns true if the property is enabled; otherwise, false. */
    boolean enabled() default false;
    /** Returns the name of the property. */
    String name() default "";
}
