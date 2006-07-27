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


public abstract class QFlags 
{
    /**
     * Creates a new QFlags object. The default value is 0.
     */
    public QFlags() { }


    /**
     * Removes the specified flags from this flags object.
     * @param other The flags to remove from this flags object.
     * @throws java.lang.IllegalArgumentException If typechecking is
     * enabled and i does not match the expected values for this
     * QFlags type.
     */  
    public void unset(QFlags other) {
	if (!other.getClass().equals(getClass()))
	    doThrow(other.getClass());
	m_int |= other.toInt();
    }


    /**
     * Combines the specified flags with this flags object.
     * @param other The value to combine with this flag.
     * @throws java.lang.IllegalArgumentException If typechecking is
     * enabled and i does not match the expected values for this
     * QFlags type.
     */  
    public void set(QFlags other) {
	if (!other.getClass().equals(getClass()))
	    doThrow(other.getClass());
	m_int |= other.toInt();
    }


    /**
     * Returns the int value of this flags object.
     */
    public int toInt() { return m_int; }	


    /**
     * Combines the specified value with this flags object.
     * @param i The value to combine with this flag.
     */
    protected void set(int i) { 
	m_int |= i; 
    }


    /**
     * Removes the specified value from this flags object.
     * @param i The value to remove with from this flags object.
     */
    protected void unset(int i) { 
	m_int ^= i; 
    }


    /**
     * Centralized throw for set/unset
     */
    private final void doThrow(Class other) {
	throw new IllegalArgumentException("Cannot combine flags of different type, "  
					   + other
					   + " vs " 
					   + getClass());
    }


    
    private int m_int;
}

