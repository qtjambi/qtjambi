package com.trolltech.tools.designer.propertysheet;

import java.util.List;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QLCDNumber;

public class LCDNumberProperty extends FakeProperty {
    
    public static final String INT_VALUE = "intValue";
    
    private QLCDNumber lcdNumber;
    public LCDNumberProperty(QLCDNumber widget, String name) {
        super(name);
        lcdNumber = widget;
        subclassLevel = decideSubclassLevel(QLCDNumber.class);
        groupName = "QLCDNumber";
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public Object read() {
        if (entry.name == INT_VALUE)
            return (int) lcdNumber.value();
        else
            return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void write(Object value) {
        if (entry.name == INT_VALUE)
            lcdNumber.display((Integer) value);
    }

    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QLCDNumber) {
            QLCDNumber lcdNumber = (QLCDNumber) object;
            properties.add(new LCDNumberProperty(lcdNumber, INT_VALUE));
        }
    }    

}
