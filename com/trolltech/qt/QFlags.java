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

public abstract class QFlags<T extends QtEnumerator> 
    implements QtEnumerator,
               java.io.Serializable, 
               Cloneable
{
    
     protected QFlags(QFlags<T> other) {
         set(other);
     }
    
     protected QFlags(T ... args) {
         set(args);
     }

     public final void set(QFlags<T> other) {
         value |= other.value();
     }
     
     public final void set(T ... ts) {
         for (T t : ts)
             value |= t.value();
     }
     
     public final boolean isSet(QFlags<T> other) {
         return (value & other.value()) == other.value();  
     }

     public final boolean isSet(T ... ts) {
         for (T t : ts) {
             if ((t.value() & value) != t.value())
                  return false;
         }
         return true;
     }
     
     public final void clear(QFlags<T> other) {
         value &= ~other.value();
     }
     
     public final void clear(T ... ts) {
         for (T t : ts)
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

