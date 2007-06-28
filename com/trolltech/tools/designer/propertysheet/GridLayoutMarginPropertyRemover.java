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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class GridLayoutMarginPropertyRemover extends Property {

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QGridLayout) {
            for (Iterator<Property> it = properties.iterator(); it.hasNext();) {
                Property p = it.next();
                if (p.entry.name.equals("margin"))
                    it.remove();
            }
        }
    }


}
