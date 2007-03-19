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

public class Property implements Comparable {
    public QtPropertyManager.Entry entry;

    public String groupName;
    public int subclassLevel;
    public boolean changed;
    public boolean visible;

    public boolean isPropertyInvokationTarget() {
        return false;
    }

    public int compareTo(Object arg0) {
        assert arg0 instanceof Property;
        Property p = (Property) arg0;

        if (subclassLevel > p.subclassLevel)
            return -1;
        else if (subclassLevel < p.subclassLevel)
            return 1;
        else {
            int order = entry.sortOrder - p.entry.sortOrder;
            if (order == 0)
                return entry.name.compareTo(p.entry.name);
            return order;
        }
    }

    public String toString() {
        return "{" + groupName + ":" + entry.name + "}";
    }
}
