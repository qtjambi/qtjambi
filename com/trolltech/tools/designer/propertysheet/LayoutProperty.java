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
import com.trolltech.qt.*;

import java.util.*;

public class LayoutProperty extends FakeProperty {

    private static final String RIGHT_MARGIN       = "rightMargin";
    private static final String LEFT_MARGIN        = "leftMargin";
    private static final String TOP_MARGIN         = "topMargin";
    private static final String BOTTOM_MARGIN      = "bottomMargin";
    private static final String VERTICAL_SPACING   = "verticalSpacing";
    private static final String HORIZONTAL_SPACING = "horizontalSpacing";

    public LayoutProperty(QLayout layout, String name) {
        super(name);
        this.layout = layout;

        attribute = false;

        System.out.println("created " + name + " for " + layout);
    }

    public Object read() {
        if (entry.name.endsWith("Margin")) {
            QRect c = layout.contentsRect();
            QRect g = layout.geometry();
            if (entry.name == RIGHT_MARGIN) return g.right() - c.right();
            if (entry.name == LEFT_MARGIN) return g.left() - c.left();
            if (entry.name == TOP_MARGIN) return g.top() - c.top();
            if (entry.name == BOTTOM_MARGIN) return g.bottom() - c.bottom();
        }
        if (entry.name == VERTICAL_SPACING) return ((QGridLayout) layout).verticalSpacing();
        if (entry.name == HORIZONTAL_SPACING) return ((QGridLayout) layout).horizontalSpacing();
        return null;
    }

    public void write(Object value) {
        System.out.println("writin property: " + entry.name + " to " + layout.objectName());
        changed = true;
        if (entry.name.endsWith("Margin")) {
            int x = (Integer) value;
            QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

            layout.getContentsMargins(left, top, right, bottom);

            if (entry.name == RIGHT_MARGIN) right.setIntValue(x);
            if (entry.name == LEFT_MARGIN) left.setIntValue(x);
            if (entry.name == TOP_MARGIN) top.setIntValue(x);
            if (entry.name == BOTTOM_MARGIN) bottom.setIntValue(x);

            layout.setContentsMargins(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
        }
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QLayout) {
            QLayout l = (QLayout) object;
            properties.add(new LayoutProperty(l, RIGHT_MARGIN));
            properties.add(new LayoutProperty(l, LEFT_MARGIN));
            properties.add(new LayoutProperty(l, TOP_MARGIN));
            properties.add(new LayoutProperty(l, BOTTOM_MARGIN));


            if (object instanceof QGridLayout) {
                properties.add(new LayoutProperty(l, VERTICAL_SPACING));
                properties.add(new LayoutProperty(l, HORIZONTAL_SPACING));
            }
        }
    }

    private QLayout layout;
}
