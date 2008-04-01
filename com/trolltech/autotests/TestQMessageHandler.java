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

package com.trolltech.autotests;

import com.trolltech.autotests.generated.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QApplication;

import org.junit.*;

import static org.junit.Assert.*;

public class TestQMessageHandler extends QMessageHandler {



    @Override
    public void debug(String message) {
        lastDebug = message;
    }

    @Override
    public void warning(String message) {
        lastWarning = message;
    }

    @Override
    public void critical(String message) {
        lastCritical = message;
    }

    @Override
    public void fatal(String message) {

    }

    @BeforeClass
    public static void testInitialize() throws Exception {

        QApplication.initialize(new String[] {});
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();
    }

    @Test
    public void test() {
        QMessageHandler.installMessageHandler(this);

        MessageHandler.sendDebug("debug sent");
        assertEquals(lastDebug, "debug sent");

        MessageHandler.sendWarning("warning sent");
        assertEquals(lastWarning, "warning sent");

        MessageHandler.sendCritical("critical sent");
        assertEquals(lastCritical, "critical sent");

        // Want to send fatal, but that will shut down app...
    }

    private String lastDebug;
    private String lastWarning;
    private String lastCritical;

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestQMessageHandler.class.getName());
    }
}
