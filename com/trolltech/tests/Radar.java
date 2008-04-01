
package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Radar extends QWidget {

    private static QColor dark = new QColor(25, 50, 25);
    private static QColor light = new QColor(0, 127, 0);
    private static QColor mark = new QColor(0, 50, 0);

    public Radar() {
        startTimer(50);

        QConicalGradient gradient = new QConicalGradient(0, 0, 0);
        gradient.setColorAt(0, dark);
        gradient.setColorAt(0.01, light);
        gradient.setColorAt(0.25, dark);
        brush = new QBrush(gradient);

        pen = new QPen(mark);
    }

    protected void timerEvent(QTimerEvent e) {
        rotation += 2;
        update();
    }

    protected void paintEvent(QPaintEvent e) {
        QPainter p = new QPainter(this);

        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.scale(width() * 0.5, height() * 0.5);
        p.translate(1, 1);

        // Draw radar gradient background
        p.save();
          p.rotate(rotation);
          p.setBrush(brush);
          p.setPen(QPen.NoPen);
          p.drawEllipse(-1, -1, 2, 2);
        p.restore();

        // Draw the lines...
        p.setPen(pen);
        int circles = 6;
        for (int i=circles; i>0; --i) {
            double r = i / (double) circles;
            p.drawEllipse(new QRectF(-r, -r, r*2, r*2));
        }

    }

    public QSize sizeHint() {
        return new QSize(200, 200);
    }

    private QPen pen;
    private QBrush brush;
    private double rotation;

    public static void main(String args[]) {
        QApplication.initialize(args);

        Radar r = new Radar();
        r.show();

        QApplication.exec();
    }
}
