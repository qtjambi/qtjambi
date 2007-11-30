package com.trolltech.tools.designer.propertysheet;

import java.util.List;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QDialog;

public class DialogProperty extends FakeProperty {
    private boolean value;
    
    public DialogProperty() {
        super("modal");
        value = false;
        subclassLevel = decideSubclassLevel(QDialog.class);
        groupName = "QDialog";
        attribute = false;
    }
    
    public Object read() {
        return value;
    }

    public void write(Object value) {
        this.value = (Boolean) value;
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QDialog) {            
            properties.add(new DialogProperty());
        }
    }

}
