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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

class PaintWidget extends QWidget {
    
    ArrayList<QPolygon> polygons = new ArrayList<QPolygon>();
    ArrayList<QColor> colors = new ArrayList<QColor>();
    QPolygon current = new QPolygon();
    QColor m_current_color = QColor.red;

    public PaintWidget() {
        this(null);
    }
    
    public PaintWidget(Qt.WindowFlags flags) {
        super(null, flags);
        this.setGeometry(new QRect(100, 100, 32 * 16 + 2, 480));
    }

    public void mousePressEvent(QMouseEvent e) {
        current = new QPolygon();
    }

    public void mouseMoveEvent(QMouseEvent e) {
        current.append(new QPoint(e.x(), e.y()));
        update();
    }
    
    public void contextMenuEvent(QContextMenuEvent e)
    {
        QMenu menu = new QMenu(this);
        QAction act = menu.addAction("Oh, nothing");
        act.triggered.connect(menu, "close()");
        act = menu.addAction("Quit");       
        act.triggered.connect(QApplication.instance(), "quit()");
                
        menu.exec(e.globalPos());        
    }

    public void mouseReleaseEvent(QMouseEvent e) {
        polygons.add(current);
        colors.add(m_current_color);
        current = null;
        update();
    }

    public void setColor(QColor c) {
        m_current_color = c;
        update();
    }
    
    public void paintEvent(QPaintEvent e) {
        QPainter p = new QPainter();
        p.begin(this);

        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.setBrush(new QBrush(new QColor(255, 255, 255)));
        p.drawRect(0, 0, width() - 1, height() - 1);

        p.setPen(Qt.PenStyle.NoPen);
        Iterator<QPolygon> it = polygons.iterator();
        Iterator<QColor> color_it = colors.iterator();
        while (it.hasNext()) {
            QPolygon stored_polygon = it.next();
            QColor stored_color = color_it.next();
            p.setPen(stored_color);
            stored_color = new QColor(stored_color.red(), stored_color.green(), stored_color.blue(), 63);
            p.setBrush(new QBrush(stored_color));
            p.drawPolygon(stored_polygon);
        }

        if (current != null) {
            p.setPen(m_current_color);
            p.drawPolyline(current);
        }

        p.setPen(Qt.PenStyle.NoPen);
        p.setBrush(new QBrush(m_current_color));

        p.drawRect(1, height() - 11, width() - 2, 10);
	p.end();
    }

    int foo;

    /*
    private static void dumpWidget(QWidget w) {
        QRect r = w.geometry();
        System.out.println(w.getClass().getName() + " x: " + r.x() + ", y: "
                + r.y() + ", w: " + r.width() + ", h: " + r.height());
    }
    */

    public static void main(String args[]) {
        QApplication.initialize(args);
        PaintWidget pw = new PaintWidget();
        pw.show();
        QApplication.exec();
    }
}
