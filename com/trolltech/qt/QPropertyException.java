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
 * QPropertyException's are thrown when the reading, writing, or reseting
 * of Qt Jambi properties fails. See the QtPropertyManager class description
 * for further information on when this excaption is thrown.
 *
 */
public class QPropertyException extends ConnectionException
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new QPropertyException with the specified
     * <tt>extraMessage</tt>.
     */
    public QPropertyException(String extraMessage)
    {
        super(extraMessage);
    }
 }
