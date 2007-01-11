package com.trolltech.manualtests;

import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trolltech.qt.gui.QApplication;


public abstract class ManualTests {
    
    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
    }    

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();        
    }
    
    
    public void run() {
        Class cls = this.getClass();
        Method methods[] = cls.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class))
                try { m.invoke(this); } catch (Throwable e) { e.printStackTrace(); }
        }
    }
}
