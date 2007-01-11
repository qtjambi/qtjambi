package com.trolltech.autotests;

import org.junit.*;

import com.trolltech.qt.gui.QApplication;

public abstract class QApplicationTest {
  
    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();        
    }
}
