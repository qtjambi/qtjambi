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

public class BuddyProperty extends FakeProperty {
    public BuddyProperty(QLabel label) {
        super("buddy");
        this.label = label;

        groupName = "QLabel";
        subclassLevel = decideSubclassLevel(QLabel.class);
    }

    public Object read() {
        QWidget buddy = label.buddy();
        if (buddy != null)
            return buddy.objectName();
        return null;
    }

    public void write(Object name) {
        QWidget window = label.window();
        QWidget w = (QWidget) window.findChild(QWidget.class, (String) name);
        if (w != null)
            label.setBuddy(w);
    }

    private QLabel label;
}
