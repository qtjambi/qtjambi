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
 * This exception is thrown in cases where a painter is used on a widget
 * outside its paintEvent function. In most cases this is because one
 * has forgotten to call QPainter.end() at the end of a paint event.
 *  
 * @author gunnar
 */
public class QPaintingOutsidePaintEventException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QPaintingOutsidePaintEventException(String message) {        
	super(message);
    }
}
