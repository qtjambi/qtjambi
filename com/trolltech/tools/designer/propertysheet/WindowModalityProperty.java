package com.trolltech.tools.designer.propertysheet;

import java.util.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.tools.designer.*;

public class WindowModalityProperty extends FakeProperty {
    private Qt.WindowModality value;

    public WindowModalityProperty() {
        super("windowModality");
        value = Qt.WindowModality.NonModal;
        this.subclassLevel = decideSubclassLevel(QDialog.class);
        this.groupName = "QWidget";
        attribute = false;
    }

    public Object read() {
        return PropertySheet.translateEnum(value);
    }

    public void write(Object value) {
        value = Qt.WindowModality.resolve((Integer) value);
    }


    public static void initialize(List<Property> properties, QObject object) {
        if (object instanceof QDialog) {

            // remove the old...
            for (Iterator<Property> it = properties.iterator(); it.hasNext();) {
                Property p = it.next();
                if (p.entry.name.equals("windowModality"))
                    it.remove();
            }

            properties.add(new WindowModalityProperty());
        }
    }
}