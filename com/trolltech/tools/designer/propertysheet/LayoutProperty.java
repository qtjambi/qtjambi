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

    public static final String RIGHT_MARGIN       = "rightMargin";
    public static final String LEFT_MARGIN        = "leftMargin";
    public static final String TOP_MARGIN         = "topMargin";
    public static final String BOTTOM_MARGIN      = "bottomMargin";
    public static final String VERTICAL_SPACING   = "verticalSpacing";
    public static final String HORIZONTAL_SPACING = "horizontalSpacing";

    public LayoutProperty(QLayout layout, String name) {
        super(name);
        this.layout = layout;
        attribute = false;
    }

    public Object read() {
        if (entry.name.endsWith("Margin")) {
            QRect c = layout.contentsRect();
            QRect g = layout.geometry();
            if (entry.name == RIGHT_MARGIN) return g.right() - c.right();
            if (entry.name == LEFT_MARGIN) return c.left() - g.left();
            if (entry.name == TOP_MARGIN) return c.top() - g.top();
            if (entry.name == BOTTOM_MARGIN) return g.bottom() - c.bottom();
        }
        return null;
    }

    public void write(Object value) {
        changed = true;
        if (entry.name.endsWith("Margin")) {
            int x = (Integer) value;

            QContentsMargins margins = layout.getContentsMargins();

            if (entry.name == RIGHT_MARGIN) margins.right = x;
            else if (entry.name == LEFT_MARGIN) margins.left = x;
            else if (entry.name == TOP_MARGIN) margins.top = x;
            else if (entry.name == BOTTOM_MARGIN) margins.bottom = x;

            layout.setContentsMargins(margins);
        }
    }

    public boolean designable() {
        return true;
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QLayout) {
            QLayout l = (QLayout) object;
            properties.add(new LayoutProperty(l, RIGHT_MARGIN));
            properties.add(new LayoutProperty(l, LEFT_MARGIN));
            properties.add(new LayoutProperty(l, TOP_MARGIN));
            properties.add(new LayoutProperty(l, BOTTOM_MARGIN));
        }
    }

    private QLayout layout;
}
