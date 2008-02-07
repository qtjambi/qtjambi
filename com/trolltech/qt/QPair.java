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
  * QPair keeps two generic values. They are public with the names
  * <tt>first</tt> and <tt>second</tt>.
  *
  */
public class QPair <T, S> implements Cloneable {    
    
    /** First value of the pair. */
    public T first;
    /** Second value of the pair. */
    public S second;
    
    /**
     * Constructs a pair.
     * @param t The first parameter.
     * @param s The second parameter.
     */
    public QPair(T t, S s) {
        first = t;        
        second = s;
    }
    
    
    /**
     * Returns true if this pair is the same as the other pair. If any
     * of the first or second members are null the result is false regardless.
     * @param The other parameter
     * @return True if they are equal.
     */
    @Override
    public boolean equals(Object o) {
        QPair<?, ?> other = o instanceof QPair ? (QPair<?, ?>) o : null; 
        if (other == null || first == null || second == null || other.first == null || other.second == null)
            return false;
        return first.equals(other.first) && second.equals(other.second);    
    }
    
    
    /**
     * Returns a string representation of this pair.
     */
    @Override
    public String toString() {
        return "Pair(" + (first != null ? first.toString() : "null")
         + "," + (second != null ? second.toString() : "null") + ")";
    }
    
    
    /**
     * Returns a copy of this object.
     */
    @Override
    public QPair<T, S> clone() {
        return new QPair<T, S>(first, second);
    }
}
