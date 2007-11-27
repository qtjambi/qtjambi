package com.trolltech.tools.designer.propertysheet;

import java.util.List;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QToolBox;
import com.trolltech.qt.gui.QWidget;

public class ToolBoxProperty extends FakeProperty {
    public static final String CURRENT_ITEM_TEXT = "currentItemText";
    public static final String CURRENT_ITEM_NAME = "currentItemName";
    public static final String CURRENT_ITEM_ICON = "currentItemIcon";
    public static final String CURRENT_ITEM_TOOLTIP = "currentItemToolTip";
    
    private QToolBox toolBox;
    public ToolBoxProperty(QToolBox widget, String name) {
        super(name);
        toolBox = widget;
        subclassLevel = decideSubclassLevel(QToolBox.class);
        groupName = "QToolBox";
    }
    
    @SuppressWarnings("deprecation")
    public Object read() {
        int current = toolBox.currentIndex();
        if (entry.name == CURRENT_ITEM_NAME) {
            QWidget widget = toolBox.widget(current);
            return widget != null ? widget.objectName() : null;
        } else if (entry.name == CURRENT_ITEM_TEXT) {
            return toolBox.itemText(current);
        } else if (entry.name == CURRENT_ITEM_TOOLTIP) {
            return toolBox.itemToolTip(current); 
        } else if (entry.name == CURRENT_ITEM_ICON) { 
            return toolBox.itemIcon(current);
        }
        
        return null;
    }

    @SuppressWarnings("deprecation")
    public void write(Object value) {
        int current = toolBox.currentIndex();
        if (entry.name == CURRENT_ITEM_NAME) {
            QWidget widget = toolBox.widget(current);
            if (widget != null) 
                widget.setObjectName((String) value);
        } else if (entry.name == CURRENT_ITEM_TEXT) { 
            toolBox.setItemText(current, (String) value);
        } else if (entry.name == CURRENT_ITEM_TOOLTIP) {
            toolBox.setItemToolTip(current, (String) value);
        } else if (entry.name == CURRENT_ITEM_ICON) { 
            toolBox.setItemIcon(current, (QIcon) value);
        }
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QToolBox) {
            QToolBox toolBox = (QToolBox) object;
            properties.add(new ToolBoxProperty(toolBox, CURRENT_ITEM_NAME));
            properties.add(new ToolBoxProperty(toolBox, CURRENT_ITEM_TEXT));
            properties.add(new ToolBoxProperty(toolBox, CURRENT_ITEM_TOOLTIP));
            properties.add(new ToolBoxProperty(toolBox, CURRENT_ITEM_ICON));
        }
    }
    
}
