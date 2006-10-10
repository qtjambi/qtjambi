package com.trolltech.tests;
 
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class CustomWidgetTester extends QWidget {
    
    public CustomWidgetTester(QWidget parent) {
        super(parent);
        resetText();
    }

    
//    @QtPropertyDesignable("true")
    public String text() {
        return text;
    }
    
    
    public void setText(String text) {
        this.text = text;
        update();
    }   
    
    
    @QtPropertyResetter
    public void resetText() {
        this.text = "Qt Jambi Dummy Widget..";
    }
    
    public void setFoobar(String foobar) {
        
    }
    
    
    public QPoint position() {
        return position;
    }
    
    public boolean canDesignText() {
        return position.x() >= 50;
    }
    
    
    public void setPosition(QPoint position) {
        this.position = position;
        update();
    }
    
    public QSize sizeHint() {
        return new QSize(200, 200);
    }
    
    protected void paintEvent(QPaintEvent e) {
        QPainter p = new QPainter();
        p.begin(this);
        
        p.drawText(position.x(), position.y(), text);
        
        p.end();
    }
        
    private String text;
    private QPoint position = new QPoint(50, 50);
    
    public static void main(String args[]) throws Exception {
        QtPropertyManager m = new QtPropertyManager();
        
        m.findProperties(CustomWidgetTester.class);
        
    }
}
