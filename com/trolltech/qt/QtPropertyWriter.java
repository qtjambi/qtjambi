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
 * QtPropertyWriter annotates a method as being a setter for a property.
 * The annotation specifies the name of the property and whether the
 * property is enabled for writing.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyWriter {
    /** Returns true if the property is enabled; otherwise, returns false. */
    boolean enabled() default true;
    /** Returnd the name of the property. */
    String name() default "";
}
