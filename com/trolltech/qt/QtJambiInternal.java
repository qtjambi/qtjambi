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

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;

public class QtJambiInternal {

    static {
        QtJambi_LibraryInitializer.init();
    }

    private static native QObject nativeSwapQObjectSender(long receiver_id, long sender_id);
    public static QObject swapQObjectSender(QObject receiver, QObject newSender) {
        return nativeSwapQObjectSender(receiver.nativeId(),
                                       newSender != null ? newSender.nativeId() : 0);
    }

    public static native QObject sender(QObject receiver);

    public static void aboutQtJambi() {
        QMessageBox mb = new QMessageBox(QApplication.activeWindow());
        mb.setWindowTitle("About Qt Jambi");
        mb.setText("<h3>About Qt Jambi</h3>"
                   + "<p>Qt Jambi is a Java toolkit based on Qt, a C++ toolkit for"
                   + " cross-platform application development.</p>"
                   + "<p>This program uses Qt version " + QtInfo.versionString() + ".</p>"
                   + "<p>Qt Jambi provides single-source "
                   + "portability across MS&nbsp;Windows, Mac&nbsp;OS&nbsp;X, "
                   + "Linux, and all major commercial Unix variants"
                   + "<p>Qt Jambi is a Trolltech product. See "
                   + "<tt>http://www.trolltech.com/</tt> for more information.</p>");
        mb.setIconPixmap(new QPixmap("classpath:com/trolltech/images/qt-logo.png"));
        mb.exec();
        mb.dispose();
    }
    
    public native static Object createExtendedEnum(int value, int ordinal, Class cl, String name);
}
