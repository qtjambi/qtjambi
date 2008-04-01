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

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPushButton;



public class Button extends QPushButton {

    private int counter = 0;
    Signal1<String> clicked_string = new Signal1<String>();

    @Override
    public void mousePressEvent(QMouseEvent e) {
        setText("[" + e.x() + ", " + e.y() + "]");
        super.mousePressEvent(e);
    }

    @Override
    public void mouseMoveEvent(QMouseEvent e) {
        setText("[" + e.x() + ", " + e.y() + "]");
        super.mouseMoveEvent(e);
    }

    @Override
    public void mouseReleaseEvent(QMouseEvent e) {
        setText("Clicked " + String.valueOf(++counter) + " times...");
        clicked_string.emit("hello");
        super.mouseReleaseEvent(e);
    }

    @Override
    protected void disposed() {
        System.out.println("object disposed...\n");
        super.disposed();
    }

    protected void testString(String s) {
        System.out.println("s = " + s);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        Button b = new Button();
        b.setText("Not clicked");
        b.clicked_string.connect(b, "testString(String)");
        b.show();

        b.setProperty("text", "Text was set dynamically...");
        System.out.println("text is: " + b.property("text"));

        QApplication.exec();
    }

}
