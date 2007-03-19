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
import com.trolltech.qt.*;

public class BuddyProperty extends Property {
    public BuddyProperty(QLabel label) {
        this.label = label;

        groupName = "QLabel";

        // Dynamically figure out superclass, just in case it _ever_ changes...
        subclassLevel = 0;
        Class cl = QLabel.class;
        while(cl != QObject.class) {
            subclassLevel++;
            cl = cl.getSuperclass();
        }

        entry = new QtPropertyManager.Entry("buddy");
        try {
            entry.read = BuddyProperty.class.getMethod("read");
            entry.write = BuddyProperty.class.getMethod("write", String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read() {
        QWidget buddy = label.buddy();
        if (buddy != null)
            return buddy.objectName();
        return null;
    }

    public void write(String name) {
        QWidget window = label.window();
        QWidget w = (QWidget) window.findChild(QWidget.class, name);
        if (w != null)
            label.setBuddy(w);
    }


    @Override
    public boolean isPropertyInvokationTarget() {
        return true;
    }

    private QLabel label;
}
