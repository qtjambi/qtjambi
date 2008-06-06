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
 * QtPropertyUser specifies that a proprety
 * is designated as the user-facing or user-editable property for the class. e.g.,
 * QAbstractButton.checked is the user editable property for (checkable) buttons.
 * Note that QItemDelegate gets and sets a widget's USER property.
 * This annotation should be used with the read method of the property.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyUser { }
