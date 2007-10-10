package com.trolltech.tests;

import com.trolltech.qt.gui.*;

public class CRASH extends QTextEdit {    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        QTextEdit crash = new QTextEdit();
        crash.connectSlotsByName();
        for (int i=0; i<1000; ++i) {
            crash.setPlainText("hello");
            System.gc();
        }                                                
        
    }
}
