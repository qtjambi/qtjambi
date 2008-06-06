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

package com.trolltech.tests;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* Instantiate with current keyboard focus manager, and look at the display.
 * Found on Internet http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4684090,
 * so not my copyright.  */


public class FocusOwnerTracer implements PropertyChangeListener {
    public static final String FOCUS_OWNER_PROPERTY = "focusOwner";
    protected KeyboardFocusManager focusManager;

    public FocusOwnerTracer(KeyboardFocusManager focusManager) {
        this.focusManager = focusManager;
        startListening();
    }

    public void startListening() {
        if (focusManager != null) {

            focusManager.addPropertyChangeListener(FOCUS_OWNER_PROPERTY, this);
        }
    }

    public void stopListening() {
        if (focusManager != null) {

            focusManager.removePropertyChangeListener(FOCUS_OWNER_PROPERTY, this);
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        Component oldOwner = (Component) e.getOldValue();
        Component newOwner = (Component) e.getNewValue();
        ebug("focusOwner changed: ", "");
        debugClass("  old: ", oldOwner);
        debugClass("  new: ", newOwner);
    }

    protected void ebug(String text, Object o) {
        System.err.println(text + o);
    }

    protected void debugClass(String msg, Object o) {
        ebug(msg, o != null ? o.getClass() : null);
    }
}
