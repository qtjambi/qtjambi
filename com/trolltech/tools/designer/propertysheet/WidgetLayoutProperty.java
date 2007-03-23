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
import com.trolltech.tools.designer.*;

import java.util.*;

public class WidgetLayoutProperty extends FakeProperty {

    public static final String LAYOUT_LEFT_MARGIN = "layoutLeftMargin";
    public static final String LAYOUT_RIGHT_MARGIN = "layoutRightMargin";
    public static final String LAYOUT_BOTTOM_MARGIN = "layoutBottomMargin";
    public static final String LAYOUT_TOP_MARGIN = "layoutTopMargin";
    public static final String LAYOUT_HORIZONTAL_SPACING = "layoutHorizontalSpacing";
    public static final String LAYOUT_VERTICAL_SPACING = "layoutVerticalSpacing";

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QTabWidget
            || object instanceof QStackedWidget
            || object instanceof QToolBox
            || object instanceof QCalendarWidget)
            return;

        if (object instanceof QWidget) {
            QWidget widget = (QWidget) object;
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_LEFT_MARGIN));
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_RIGHT_MARGIN));
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_BOTTOM_MARGIN));
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_TOP_MARGIN));
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_HORIZONTAL_SPACING));
            properties.add(new WidgetLayoutProperty(widget, LAYOUT_VERTICAL_SPACING));
        }
    }


    public WidgetLayoutProperty(QWidget widget, String name) {
        super(name);
        this.widget = widget;
        groupName = "Layout";
        subclassLevel = 1024; // Just an arbitrary high number...

        if (name == LAYOUT_LEFT_MARGIN) alterEgoName = LayoutProperty.LEFT_MARGIN;
        else if (name == LAYOUT_RIGHT_MARGIN) alterEgoName = LayoutProperty.RIGHT_MARGIN;
        else if (name == LAYOUT_BOTTOM_MARGIN) alterEgoName = LayoutProperty.BOTTOM_MARGIN;
        else if (name == LAYOUT_TOP_MARGIN) alterEgoName = LayoutProperty.TOP_MARGIN;
        else if (name == LAYOUT_HORIZONTAL_SPACING) alterEgoName = LayoutProperty.HORIZONTAL_SPACING;
        else if (name == LAYOUT_VERTICAL_SPACING) alterEgoName = LayoutProperty.VERTICAL_SPACING;
    }

    public Object read() {
        PropertySheet sheet = layoutPropertySheet();
        return sheet != null ? sheet.property(sheet.indexOf(alterEgoName)) : null;
    }

    public void write(Object value) {
        PropertySheet sheet = layoutPropertySheet();
        if (sheet != null) {
            sheet.setProperty(sheet.indexOf(alterEgoName), value);
        }
    }

    public boolean designable() {
        PropertySheet sheet = layoutPropertySheet();
        return sheet != null ? sheet.isVisible(sheet.indexOf(alterEgoName)) : false;
    }

    private PropertySheet layoutPropertySheet() {
        QLayout layout = widget.layout();
        return layout != null ? PropertySheet.get(layout) : null;
    }

    private QWidget widget;
    private String alterEgoName;
}
