package com.trolltech.autotests;

import org.junit.Test;

import com.trolltech.qt.gui.QLayoutItemInterface;
import com.trolltech.qt.gui.QVBoxLayout;

import static org.junit.Assert.*;
public class TestNullPointers extends QApplicationTest {
    
    static class MyLayout extends QVBoxLayout {

        @Override
        public QLayoutItemInterface itemAt(int arg__1) {
            return null;
        }
        
    }
    
    
    @Test
    public void testBoxLayoutAddWidget() {
        QVBoxLayout layout = new QVBoxLayout();
        
        Exception e = null;
        try {
            layout.addWidget(null);
        } catch (Exception f) {
            e = f;
        }
        
        assertTrue(e instanceof NullPointerException);
    }        
}
