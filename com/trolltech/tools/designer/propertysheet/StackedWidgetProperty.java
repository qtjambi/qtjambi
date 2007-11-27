package com.trolltech.tools.designer.propertysheet;

import java.util.List;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QStackedWidget;
import com.trolltech.qt.gui.QWidget;

public class StackedWidgetProperty extends FakeProperty {

    public static final String CURRENT_PAGE_NAME = "currentPageName";
    
    private QStackedWidget stackedWidget;
    public StackedWidgetProperty(QStackedWidget widget, String name) {
        super(name);
        stackedWidget = widget;
        subclassLevel = decideSubclassLevel(QStackedWidget.class);
        groupName = "QStackedWidget";
    }
    
    @SuppressWarnings("deprecation")
    public Object read() {
        int current = stackedWidget.currentIndex();
        if (entry.name == CURRENT_PAGE_NAME) {
            QWidget widget = stackedWidget.widget(current);
            return widget != null ? widget.objectName() : null;
        } 
        
        return null;
    }

    @SuppressWarnings("deprecation")
    public void write(Object value) {
        int current = stackedWidget.currentIndex();
        if (entry.name == CURRENT_PAGE_NAME) {
            QWidget widget = stackedWidget.widget(current);
            if (widget != null) 
                widget.setObjectName((String) value);
        } 
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QStackedWidget) {
            QStackedWidget stackedWidget = (QStackedWidget) object;
            properties.add(new StackedWidgetProperty(stackedWidget, CURRENT_PAGE_NAME));
        }
    }

}
