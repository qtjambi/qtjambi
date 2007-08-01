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


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * QtPropertyDesignable specifies wether a proprety
 * is suitable for editing in a GUI builder (e.g., the Qt Designer).
 * It is the read method of the property that must be annotated.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyDesignable {
    /**
     * The value should be true or false depending on whether the
     * property is designable. It can also be the name of a boolean
     * method in the same class as the annotated method; it must
     * return true if the property is to be designable; otherwise,
     * false.
     */
    String value() default "true";
}
