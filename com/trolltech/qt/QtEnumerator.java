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
 * Interface for Qt Jambi's internal enums. If you manually implement this class, 
 * your implementation must contain a method with the following signature:
 *  
 *      public static T resolve(int value);
 *      
 * where T is your subclass. This should return the enum value corresponding to the 
 * specified int value.
 *  
 * @exclude 
 */
public interface QtEnumerator {
    public int value();
}
