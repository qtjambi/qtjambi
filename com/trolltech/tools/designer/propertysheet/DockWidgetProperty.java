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
import com.trolltech.tools.designer.PropertySheet;

import java.util.List;

public class DockWidgetProperty extends FakeProperty {

    private static final String WINDOW_TITLE = "windowTitle";
    private static final String DOCKED = "docked";
    private static final String DOCKWIDGET_AREA = "dockWidgetArea";


    public DockWidgetProperty(QDockWidget widget, String name) {
        super(name);
        groupName = "QDockWidget";
        subclassLevel = 2;
        this.widget = widget;
    }


    public Object read() {
        if (entry.name == DOCKWIDGET_AREA)
            return PropertySheet.translateEnum(Qt.DockWidgetArea.resolve((Integer) widget.property(entry.name)));
        return widget.property(entry.name);
    }


    public void write(Object value) {
        widget.setProperty(entry.name, value);
    }

    public void reset() {
        if (entry.name == DOCKED) write(false);
        else if (entry.name == WINDOW_TITLE) write(null);
        else if (entry.name == DOCKWIDGET_AREA) write(Qt.DockWidgetArea.LeftDockWidgetArea);
    }

    public boolean designable() {
        if (entry.name == DOCKWIDGET_AREA)
            return (Boolean) widget.property(DOCKED);
        return true;
    }

    public static void initialize(List<Property> properties, QObject object) {
         if (object instanceof QDockWidget) {
             QDockWidget widget = (QDockWidget) object;

             properties.add(new DockWidgetProperty(widget, WINDOW_TITLE));
             properties.add(new DockWidgetProperty(widget, DOCKED));
             properties.add(new DockWidgetProperty(widget, DOCKWIDGET_AREA));
         }
    }


    private QDockWidget widget;
}
