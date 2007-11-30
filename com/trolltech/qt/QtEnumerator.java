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
 * The QtEnumerator interface servers as a base for all Qt Jambi enums
 * and flags. Its sole purpose is to unify the access to the integer
 * value of enumerators and flags using the value() method.
 *
 * If you manually implement this class, your implementation must contain a method 
 * with the following signature:
 *  
 *      public static T resolve(int value);
 *      
 * where T is your subclass. This should return the enum value corresponding to the 
 * specified int value.
 */
public interface QtEnumerator {
    /**
     * This function should return an integer value for the enum
     * values of the enumeration that implements this interface.
     */
    public int value();
}
