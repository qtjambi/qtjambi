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
 * This exception is deprecated. It is no longer thrown by any
 * methods due to a change in the property system.
 */
@Deprecated
public class QNoSuchPropertyException extends QPropertyException {
    private static final long serialVersionUID = 1L;

    public QNoSuchPropertyException(String message) {
        super(message);
    }
}
