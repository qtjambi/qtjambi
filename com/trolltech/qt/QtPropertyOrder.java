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
 * The QtPropertyOrder annotation gives the property a value that can be used
 * for sorting properties. The Qt Designer sorts properties with this value.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyOrder {
    /**
     * Returns the sort order of the property.
     */
    public int value() default 0;
}
