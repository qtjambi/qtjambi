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

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.util.*;

public class TabWidgetProperty extends FakeProperty {

    public static final String CURRENT_TAB_NAME = "currentTabName";
    public static final String CURRENT_TAB_TEXT = "currentTabText";
    public static final String CURRENT_TAB_TOOLTIP = "currentTabToolTip";
    public static final String CURRENT_TAB_ICON = "currentTabIcon";

    public TabWidgetProperty(QTabWidget widget, String name) {
        super(name);
        tab = widget;
        subclassLevel = decideSubclassLevel(QTabWidget.class);
        groupName = "QTabWidget";
    }


    public Object read() {
        int current = tab.currentIndex();
        if (entry.name == CURRENT_TAB_NAME) {
            QWidget widget = tab.widget(current);
            return widget != null ? widget.objectName() : null;
        }
        if (entry.name == CURRENT_TAB_TEXT) return tab.tabText(current);
        if (entry.name == CURRENT_TAB_TOOLTIP) return tab.tabToolTip(current);
        if (entry.name == CURRENT_TAB_ICON) return tab.tabIcon(current);
        return null;
    }

    public void write(Object value) {
        int current = tab.currentIndex();
        if (entry.name == CURRENT_TAB_NAME) tab.widget(current).setObjectName((String) value);
        if (entry.name == CURRENT_TAB_TEXT) tab.setTabText(current, (String) value);
        if (entry.name == CURRENT_TAB_TOOLTIP) tab.setTabToolTip(current, (String) value);
        if (entry.name == CURRENT_TAB_ICON) tab.setTabIcon(current, (QIcon) value);
    }

    private QTabWidget tab;

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QTabWidget) {
            QTabWidget tab = (QTabWidget) object;
            properties.add(new TabWidgetProperty(tab, CURRENT_TAB_NAME));
            properties.add(new TabWidgetProperty(tab, CURRENT_TAB_TEXT));
            properties.add(new TabWidgetProperty(tab, CURRENT_TAB_TOOLTIP));
            properties.add(new TabWidgetProperty(tab, CURRENT_TAB_ICON));
        }
    }
}
