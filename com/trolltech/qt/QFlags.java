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
 * This class manages a set of QtEnumerator values. Each enum value
 * is treated as a flag that is either set or unset. You can set and
 * clear flags, and query which flags are set.
 */
public abstract class QFlags<T extends QtEnumerator>
    implements QtEnumerator,
               java.io.Serializable,
               Cloneable
{
     /**
      * Creates a new QFlags where the flags in <tt>args</tt> are set.
      */
     protected QFlags(T ... args) {
         set(args);
     }

     /**
      * Sets the flag <tt>other</tt>
      */
     public final void set(QFlags<T> other) {
         value |= other.value();
     }

     /**
      * Sets the flags in <tt>ts</tt>.
      */
     public final void set(T ... ts) {
         for (T t : ts)
             value |= t.value();
     }

    /**
     * Returns true if flag <tt>other</tt> is set; otherwise, returns
     * false.
     */
     public final boolean isSet(QFlags<T> other) {
         return (value & other.value()) == other.value();
     }

     /**
      * Returns true if all <tt>ts</tt> flags are set; otherwise,
      * returns false.
      */
     public final boolean isSet(T ... ts) {
         for (T t : ts) {
             if ((t.value() & value) != t.value())
                  return false;
         }
         return true;
     }

    /**
     * Clears the flag <tt>other</tt>.
     */
     public final void clear(QFlags<T> other) {
         value &= ~other.value();
     }

     /**
      * Clears all flags in <tt>ts</tt>.
      *
      */
     public final void clear(T ... ts) {
         for (T t : ts)
             value &= ~t.value();
     }

    /**
     * Clears all flags.
     */
     public final void clearAll() {
         value = 0;
     }

    /**
     * Sets the value of this QFlags.
     */
     public final void setValue(int value) {
         this.value = value;
     }

    /**
     * Returns the value of this QFlags.
     */
     public final int value() {
         return value;
     }

    /**
     * {@inheritDoc}
     */
     public final boolean equals(Object object) {
         return object != null
             && object.getClass() == getClass()
             && ((QFlags) object).value() == value();
     }

    /**
     * {@inheritDoc}
     */
     public final String toString() {
         String hexString = Integer.toHexString(value);
         return "0x" + ("00000000".substring(hexString.length())) + hexString;
     }

     private int value;
}

