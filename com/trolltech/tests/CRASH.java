package com.trolltech.tests;

import com.trolltech.qt.gui.*;

public class CRASH extends QTextEdit {
    CRASH(QWidget parent) {
        super(parent);

        connectSlotsByName();
        loadStyleSheet("Coffee");
    }
        
    void loadStyleSheet(final String sheetName) {
        String styleSheet;
        styleSheet = "hello"; 
        
        for (int i=0; i<1000; ++i) {
            setPlainText(styleSheet);
            System.gc();
        }                                                
    }
    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        CRASH crash = new CRASH(null);
        crash.show();
        
        QApplication.exec();
    }
}
