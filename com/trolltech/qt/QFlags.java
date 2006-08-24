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

public abstract class QFlags<T extends QtEnumerator<T>> 
    implements QtEnumerator<T>, 
               java.io.Serializable, 
               Cloneable
{
    
     protected QFlags(QtEnumerator<T> ... args) {
         for (QtEnumerator<T> t : args)
             set(t);
     }

     public final void set(QtEnumerator<T> ... ts) {
         for (QtEnumerator<T> t : ts)
             value |= t.value();
     }

     public final boolean isSet(QtEnumerator<T> ... ts) {
         for (QtEnumerator<T> t : ts)
             if ((t.value() & value) != t.value())
                  return false;
         return true;
     }
     
     public final void clear(QtEnumerator<T> ... ts) {
         for (QtEnumerator<T> t : ts)
             value &= ~t.value(); 
     }
         
     public final void clearAll() { 
         value = 0; 
     }

     public final void setValue(int value) { 
         this.value = value;
     }
     
     public final int value() {
         return value; 
     }

     public final boolean equals(Object object) {
         return object != null
             && object.getClass() == getClass()
             && ((QFlags) object).value() == value();
     }

     public final String toString() {
         String hexString = Integer.toHexString(value);
         return "0x" + ("00000000".substring(hexString.length())) + hexString;
     }
     
     private int value;
}

