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

package com.trolltech.tests;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class ThrowVsStack {

    static void recurseTimeException(int rec) {
        if (rec == 0)
            timeException();
        else recurseTimeException(rec - 1);
    }
    
    static void timeException()
    {
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement elements[] = e.getStackTrace();
            if (elements[1].getMethodName() == "just one of those strings...")
                System.out.println("...");
        }
    }
    
    static void recurseThread(int i) {
        if (i == 0) 
            timeThreadLookup();
        else
            recurseThread(i - 1);
    }
    
    static void timeThreadLookup()
    {
        StackTraceElement elements[] = Thread.currentThread().getStackTrace();
        if (elements[1].getMethodName() == "just one of those strings...")
            System.out.println("...");
    }
    
    static void timeSillyFunctionCall(String s)
    {
        if (s == "just one of those strings...")
            System.out.println("...");
    }
    
    static void timeSillyFunctionCall(int s)
    {
        if (s == 7891235)
            System.out.println("...");
    }
    
    public static void main(String[] args) 
    {
        int COUNT = args.length > 0 ? Integer.parseInt(args[0]) : 100000;
        
//        if (false) {
//            long t1 = System.currentTimeMillis();
//            for (int i=0; i<COUNT; ++i) {
//                recurseTimeException();
//            }   
//            System.out.println("timeException " + COUNT + " times " + (System.currentTimeMillis() - t1));
//        }
//        
//        if (false) {
//            long t1 = System.currentTimeMillis();
//            for (int i=0; i<COUNT; ++i) {
//                recurseThread();
//            }   
//            System.out.println("timeThreadLookup " + COUNT + " times " + (System.currentTimeMillis() - t1));
//        }
//        
       {
           long t1 = System.currentTimeMillis();            
           for (int i=0; i<COUNT; ++i) {
               timeSillyFunctionCall(args[1]);
           }   
           System.out.println("sullidull " + COUNT + " times " + (System.currentTimeMillis() - t1));
       }
       
       {
           long t1 = System.currentTimeMillis();            
           for (int i=0; i<COUNT; ++i) {
               timeSillyFunctionCall(args[0]);
           }   
           System.out.println("sullidull int " + COUNT + " times " + (System.currentTimeMillis() - t1));
       }
       
//       {
//           QObject o = new QObject();
//           long t1 = System.currentTimeMillis();            
//           for (int i=0; i<COUNT; ++i) {
//               o.dumpObjectInfo();
//           }   
//           System.out.println("dumpObjectInfo " + COUNT + " times " + (System.currentTimeMillis() - t1));
//       }     
       {
           QObject o = new QObject();
           long t1 = System.currentTimeMillis();            
           for (int i=0; i<COUNT; ++i) {
               o.killTimer(1000);
           }   
           System.out.println("killTimer " + COUNT + " times " + (System.currentTimeMillis() - t1));
       }
       {
           QObject o = new QObject();
           long t1 = System.currentTimeMillis();
           String s = args[1];
           for (int i=0; i<COUNT; ++i) {
               o.setObjectName(s);
           }   
           System.out.println("setObjectName " + COUNT + " times " + (System.currentTimeMillis() - t1));
       }

       if (true) {
            QApplication app = new QApplication(args);
            QPixmap pm = new QPixmap(100, 100);
            QPainter p = new QPainter();
            p.begin(pm);
            p.setPen(Qt.NoPen);
            p.setBrush(Qt.blue);
	    long ops = 100000;
            long t1 = System.currentTimeMillis();
            for (int i=0; i<ops; ++i) {
                p.drawRect(0, 0, 100, 100);
            }
            long t2 = (System.currentTimeMillis() - t1);
            System.out.println("1 million rects took=" + t2 + ", " + ops * 1000.0 / t2 + "ops/sec");
        }
    }

}
