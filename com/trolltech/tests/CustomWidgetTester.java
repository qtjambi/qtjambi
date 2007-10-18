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

public class CustomWidgetTester extends QWidget {

    public Signal1<String> textChanged = new Signal1<String>();
    public Signal1<String> positionChanged = new Signal1<String>();
    
    public CustomWidgetTester(QWidget parent) {
        super(parent);
        resetText();
    }

    public CustomWidgetTester() {
        this(null);
    }

    
    @QtPropertyOrder(0)
    public String text() {
        return text;
    }
    
    
    public void setText(String text) {
        this.text = text;
        update();
        textChanged.emit(text);
    }
    
    
    @QtPropertyResetter
    public void resetText() {
        setText("Qt Jambi Dummy Widget..");
    }


    @QtPropertyOrder(1)
    public QPoint position() {
        return position;
    }
    
    public boolean canDesignText() {
        return position.x() >= 50;
    }
    
    
    public void setPosition(QPoint position) {
        this.position = position;
        update();
        positionChanged.emit("x=" + position.x() + ", y=" + position.y());
    }


    public void setPositionX(int x) { setPosition(new QPoint(x, position.y())); }
    public void setPositionY(int y) { setPosition(new QPoint(position.x(), y)); }
    
    @Override
    public QSize sizeHint() {
        return new QSize(200, 200);
    }
    
    @Override
    protected void paintEvent(QPaintEvent e) {
        QPainter p = new QPainter();
        p.begin(this);

        p.setPen(new QPen(palette().brush(QPalette.ColorRole.WindowText), 0));
        
        p.drawText(position.x(), position.y(), text);
        
        p.end();
    }
        
    private String text;
    private QPoint position = new QPoint(50, 50);
    
    public static void main(String args[]) throws Exception {
        QtPropertyManager.findProperties(CustomWidgetTester.class);
        
    }
}
