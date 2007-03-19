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
import com.trolltech.qt.gui.*;

public class LayoutProperty extends Property {
    private QWidget object;

    public static String LAYOUT_LEFT_MARGIN = "layoutLeftMargin";
    public static String LAYOUT_RIGHT_MARGIN = "layoutRightMargin";
    public static String LAYOUT_BOTTOM_MARGIN = "layoutBottomMargin";
    public static String LAYOUT_TOP_MARGIN = "layoutTopMargin";
    public static String LAYOUT_HORIZONTAL_SPACING = "layoutHorizontalSpacing";
    public static String LAYOUT_VERTICAL_SPACING = "layoutVerticalSpacing";

    public LayoutProperty(QWidget widget, String name) {
        this.object = widget;
        groupName = "Layout";
        subclassLevel = 1024; // Just an arbitrary high number...

        entry = new QtPropertyManager.Entry(name);
        try {
            entry.read = LayoutProperty.class.getMethod("read");
            entry.write = LayoutProperty.class.getMethod("write", int.class);
            entry.designable = LayoutProperty.class.getMethod("designable");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int read() {
        if (!designable())
            return 0;
        QLayout layout = object.layout();
        if (entry.name == LAYOUT_RIGHT_MARGIN) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
            layout.getContentsMargins(null, null, np, null);
            return np.intValue();
        }
        if (entry.name == LAYOUT_LEFT_MARGIN) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
            layout.getContentsMargins(np, null, null, null);
            return np.intValue();
        }
        if (entry.name == LAYOUT_BOTTOM_MARGIN) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
            layout.getContentsMargins(null, null, null, np);
            return np.intValue();
        }
        if (entry.name == LAYOUT_TOP_MARGIN) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
            layout.getContentsMargins(null, np, null, null);
            return np.intValue();
        }
        if (entry.name == LAYOUT_HORIZONTAL_SPACING) return ((QGridLayout) layout).horizontalSpacing();
        if (entry.name == LAYOUT_VERTICAL_SPACING) return ((QGridLayout) layout).verticalSpacing();
        return 0;
    }

    public void write(int x) {
        QLayout layout = object.layout();
        if (entry.name.endsWith("Margin")) {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

            layout.getContentsMargins(left, top, right, bottom);

            if (entry.name == LAYOUT_RIGHT_MARGIN) right.setIntValue(x);
            if (entry.name == LAYOUT_LEFT_MARGIN) left.setIntValue(x);
            if (entry.name == LAYOUT_TOP_MARGIN) top.setIntValue(x);
            if (entry.name == LAYOUT_BOTTOM_MARGIN) bottom.setIntValue(x);

            layout.setContentsMargins(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
        } else if (entry.name == LAYOUT_HORIZONTAL_SPACING) {
            ((QGridLayout) layout).setHorizontalSpacing(x);
        } else if (entry.name == LAYOUT_VERTICAL_SPACING) {
            ((QGridLayout) layout).setVerticalSpacing(x);
        }
    }

    public boolean designable() {
        if (object.layout() != null) {
            QLayout l = object.layout();
            return entry.name == LAYOUT_HORIZONTAL_SPACING
                    || entry.name == LAYOUT_VERTICAL_SPACING
                    ? l instanceof QGridLayout
                    : true;
        }
        return false;
    }
}
