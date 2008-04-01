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

public class QNonVirtualOverridingException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public QNonVirtualOverridingException(String extraMessage)
    {
        super(extraMessage.length() > 0 ? "Non-virtual function overridden: " + extraMessage : "Non-virtual function overridden");
    }
}
