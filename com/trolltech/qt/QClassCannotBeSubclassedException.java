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

public class QClassCannotBeSubclassedException extends Exception {

    private static final long serialVersionUID = 1L;

    public QClassCannotBeSubclassedException(Class<?> clazz) {
        super("Class '" + clazz.getName() + "' is not intended to be subclassed, and attempts to do so will lead to run time errors.");
    }

}
