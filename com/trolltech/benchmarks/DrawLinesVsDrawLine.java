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

package com.trolltech.benchmarks;

import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QWidget;


public class DrawLinesVsDrawLine extends QWidget {
    
    void drawLines(QPainter p, int size) {
        double radius = 100.0;
        p.save();
        p.translate(width() / 2.0, height() / 2.0);
        p.setPen(QColor.black);
        
        long millis = System.currentTimeMillis();
        
        List<QLineF> lines = new ArrayList<QLineF>(size);
                
        for (int i=0; i<size; ++i) {
            double degree = i % 360;
            lines.add(new QLineF(Math.cos(degree) * radius, Math.sin(degree) * radius,   
                                   Math.cos(degree) * -radius, Math.sin(degree) * -radius));
        }
        
        p.drawLinesF(lines);
        
        long delta = System.currentTimeMillis() - millis;
        System.out.println("drawLines -- size == " + size + " -- time == " + delta + " ms.");
        p.restore();
    }
    
    void drawCachedLines(QPainter p, int size) {
        double radius = 100.0;
        p.save();
        p.translate(width() / 2.0, height() / 2.0);
        p.setPen(QColor.black);
        
        
        List<QLineF> lines = new ArrayList<QLineF>(size);
                
        for (int i=0; i<size; ++i) {
            double degree = i % 360;
            lines.add(new QLineF(Math.cos(degree) * radius, Math.sin(degree) * radius,   
                                   Math.cos(degree) * -radius, Math.sin(degree) * -radius));
        }
        
        long millis = System.currentTimeMillis();        
        p.drawLinesF(lines);
        
        long delta = System.currentTimeMillis() - millis;
        System.out.println("drawCachedLines -- size == " + size + " -- time == " + delta + " ms.");
        p.restore();
    }
    
    void drawLine(QPainter p, int size) {
        double radius = 100.0;
        p.save();
        p.translate(width() / 2.0, height() / 2.0);
        p.setPen(QColor.black);
                
        long millis = System.currentTimeMillis();
                       
        for (int i=0; i<size; ++i) {
            double degree = i % 360;
            p.drawLine(new QLineF(Math.cos(degree) * radius, Math.sin(degree) * radius,   
                    Math.cos(degree) * -radius, Math.sin(degree) * -radius));
        }
                
        long delta = System.currentTimeMillis() - millis;
        System.out.println("drawLine -- size == " + size + " -- time == " + delta + " ms.");
        p.restore();
    }

    void drawCachedLine(QPainter p, int size) {
        double radius = 100.0;
        p.save();
        p.translate(width() / 2.0, height() / 2.0);
        p.setPen(QColor.black);
        
        QLineF lines[] = new QLineF[size];
        for (int i=0; i<size; ++i) {
            double degree = i % 360;
            lines[i] = new QLineF(Math.cos(degree) * radius, Math.sin(degree) * radius,   
                                  Math.cos(degree) * -radius, Math.sin(degree) * -radius);
        }
                
        long millis = System.currentTimeMillis();                       
        for (int i=0; i<size; ++i) {           
            p.drawLine(lines[i]);
        }
                
        long delta = System.currentTimeMillis() - millis;
        System.out.println("drawCachedLine -- size == " + size + " -- time == " + delta + " ms.");
        p.restore();
    }
    
    private int do_this_just_once = 0;
    @Override
    protected void paintEvent(QPaintEvent e) {               
        if (do_this_just_once++ == 0) {
            QPainter p = new QPainter(this);
            for (int size=5000; size<=300000; size += size >= 50000 ? 50000 : 5000) { 
                p.fillRect(rect(), new QBrush(QColor.white));
                drawLines(p, size);                
                System.gc();
                
                p.fillRect(rect(), new QBrush(QColor.white));        
                drawLine(p, size);
                System.gc();

                p.fillRect(rect(), new QBrush(QColor.white));        
                drawCachedLines(p, size);
                System.gc();
                
                p.fillRect(rect(), new QBrush(QColor.white));        
                drawCachedLine(p, size);
                System.gc();               
            }
            p.end();
        }
        
        
    }

    public static void main(String[] args) {
        QApplication.initialize(args);
        
        QWidget w = new DrawLinesVsDrawLine();
        w.show();
        
        QApplication.exec();
    }

}
