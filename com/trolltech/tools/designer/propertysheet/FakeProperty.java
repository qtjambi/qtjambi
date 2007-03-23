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

package com.trolltech.tools.designer.propertysheet;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

public abstract class FakeProperty extends Property {

    public FakeProperty(String name) {
        attribute = true;
        entry = new QtPropertyManager.Entry(name);
        try {
            entry.read = FakeProperty.class.getMethod("read");
            entry.write = FakeProperty.class.getMethod("write", Object.class);
            entry.designable = FakeProperty.class.getMethod("designable");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPropertyInvokationTarget() {
        return true;
    }

    public abstract Object read();
    public abstract void write(Object value);

    public boolean designable() { return true; }

    protected int decideSubclassLevel(Class cl) {
        // Dynamically figure out superclass, just in case it _ever_ changes...
        int level = 0;
        while(cl != QObject.class) {
            level++;
            cl = cl.getSuperclass();
        }
        return level;
    }
    
}
