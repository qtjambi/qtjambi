package com.trolltech.tests;

import com.trolltech.qt.gui.*;

public class CRASH {    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        QGraphicsScene scene = new QGraphicsScene();
        QGraphicsView view = new QGraphicsView();
        
        view.setScene(scene);
        
        QGraphicsTextItem crash = new QGraphicsTextItem();
        
        scene.addItem(crash );
        

        
        {
            crash.children();
            crash.document();
        }
        for (int i=0; i<1000; ++i) {
            crash.setPlainText("a");
            System.gc();
        }                                                
        
    }
}
