package com.trolltech.tests;

import java.util.List;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.*;

public class CRASH {    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        QTextEdit crash = new QTextEdit();
        {
            List<QObject> children = crash.findChildren();
        }
        for (int i=0; i<1000; ++i) {
            crash.setPlainText("a");
            System.gc();
        }                                                
        
    }
}
