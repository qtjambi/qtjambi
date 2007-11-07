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

package com.trolltech.qt;

import com.trolltech.qt.gui.*;

import java.util.*;

/**
 * @exclude
 */
public class QtJambiGuiInternal {
    /**
     * Shows an about box for Qt Jambi
     */
    public static void aboutQtJambi() {
        QMessageBox mb = new QMessageBox(QApplication.activeWindow());
        mb.setWindowTitle("About Qt Jambi");
        mb.setText("<h3>About Qt Jambi</h3>"
                   + "<p>Qt Jambi is a Java toolkit based on Qt, a C++ toolkit for"
                   + " cross-platform application development.</p>"
                   + "<p>This program uses Qt version "
                   + QtInfo.versionString()
                   + ".</p>"
                   + "<p>Qt Jambi provides single-source "
                   + "portability across MS&nbsp;Windows, Mac&nbsp;OS&nbsp;X, "
                   + "Linux, and all major commercial Unix variants"
                   + "<p>Qt Jambi is a Trolltech product. See "
                   + "<a href=\"http://www.trolltech.com/jambi\">http://www.trolltech.com/jambi</a> for more information.</p>");
        mb.setIconPixmap(new QPixmap(
                "classpath:com/trolltech/images/qt-logo.png"));
        mb.exec();
    }



    private static HashMap<QWidget, QPainter> painters = new HashMap<QWidget, QPainter>();

    public static boolean beginPaint(QWidget widget, QPainter painter) {
        if (painters.containsKey(widget))
            throw new RuntimeException("Painter opened twice on the same widget");
        if (painter.isActive())
            throw new RuntimeException("Painter already active");
        painters.put(widget, painter);
        return painter.begin((QPaintDeviceInterface) widget);
    }

    private static void endPaint(QWidget widget) {
        QPainter p = painters.get(widget);
        if (p != null) {
            p.dispose();
            painters.remove(widget);
        }

        if (widget instanceof QAbstractScrollArea) {
            endPaint(((QAbstractScrollArea) widget).viewport());
        }
    }

}
