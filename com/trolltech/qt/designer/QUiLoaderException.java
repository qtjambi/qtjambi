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

package com.trolltech.qt.designer;

@SuppressWarnings("serial")
public class QUiLoaderException extends Exception {

    public QUiLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public QUiLoaderException(String message) {
        super(message);
    }
}
