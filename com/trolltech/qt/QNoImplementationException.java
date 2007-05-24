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

public class QNoImplementationException extends RuntimeException 
{
    private static final long serialVersionUID = 1L;
    private static final String NO_IMPLEMENTATION_STRING = 
        "The method has no implementation and only exists for compatibility";
    
    public QNoImplementationException() 
    {
        super(NO_IMPLEMENTATION_STRING);
    }

    public QNoImplementationException(Throwable throwable) 
    {
        super(NO_IMPLEMENTATION_STRING, throwable);
    } 
}
