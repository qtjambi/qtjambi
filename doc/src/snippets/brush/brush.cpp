#include <QtGui>

int main()
{
    QWidget anyPaintDevice;
    {
        // PEN SNIPPET
        QPainter painter;
        QPen pen(Qt::red, 2);                 // red solid line, 2 pixels wide
        painter.begin(&anyPaintDevice);   // paint something
        painter.setPen(pen);              // set the red, wide pen
        painter.drawRect(40,30, 200,100); // draw a rectangle
        painter.setPen(Qt::blue);             // set blue pen, 0 pixel width
        painter.drawLine(40,30, 240,130); // draw a diagonal in rectangle
        painter.end();                    // painting done
    }

    {
        // BRUSH SNIPPET
        QPainter painter;
        QBrush brush(Qt::yellow);           // yellow solid pattern
        painter.begin(&anyPaintDevice);   // paint something
        painter.setBrush(brush);          // set the yellow brush
        painter.setPen(Qt::NoPen);        // do not draw outline
        painter.drawRect(40,30, 200,100); // draw filled rectangle
        painter.setBrush(Qt::NoBrush);    // do not fill
        painter.setPen(Qt::black);            // set black pen, 0 pixel width
        painter.drawRect(10,10, 30,20);   // draw rectangle outline
        painter.end();                    // painting done
    }

    // LINEAR
//! [0]
    QLinearGradient linearGrad(QPointF(100, 100), QPointF(200, 200));
    linearGrad.setColorAt(0, Qt::black);
    linearGrad.setColorAt(1, Qt::white);
//! [0]

    // RADIAL
//! [1]
    QRadialGradient radialGrad(QPointF(100, 100), 100);
    radialGrad.setColorAt(0, Qt::red);
    radialGrad.setColorAt(0.5, Qt::blue);
    radialGrad.setColorAt(1, Qt::green);
//! [1]
}
