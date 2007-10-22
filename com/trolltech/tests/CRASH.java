package com.trolltech.tests;

import java.util.List;

import com.trolltech.qt.core.QObject;
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
            List<QObject> children = crash.children();
            QTextDocument document = crash.document();
        }
        for (int i=0; i<1000; ++i) {
            crash.setPlainText("a");
            System.gc();
        }                                                
        
    }
}
