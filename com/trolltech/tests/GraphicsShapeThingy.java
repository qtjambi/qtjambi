package com.trolltech.tests;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;


public class GraphicsShapeThingy extends QGraphicsView {

    private QPainterPath path;
    private QGraphicsPathItem pathItem;
    private QGraphicsLineItem lineItem;

    public GraphicsShapeThingy() {

        QGraphicsScene scene = new QGraphicsScene();
        setScene(scene);

        // some initial setup...
        path = new QPainterPath();
        path.moveTo(100, 100);
        path.lineTo(200, 100);
        path.lineTo(300, 150);

        pathItem = scene.addPath(path);
        lineItem = scene.addLine(300, 150, 0, 0, new QPen(QColor.black, 0, Qt.PenStyle.DotLine));
    }



    protected void mouseMoveEvent(QMouseEvent e) {
        // Update the end point of the line
        QPointF pos = mapToScene(e.pos());
        QLineF line = lineItem.line();
        lineItem.setLine(line.x1(), line.y1(), pos.x(), pos.y());
    }

    protected void mouseReleaseEvent(QMouseEvent e) {
        // push the end point of the line to the path.
        QLineF line = lineItem.line();
        path.lineTo(line.x2(), line.y2());
        pathItem.setPath(path);

        // make the end point of the path the start of the line
        lineItem.setLine(line.x2(), line.y2(), line.x2(), line.y2());
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        QGraphicsView view = new GraphicsShapeThingy();

        view.setRenderHints(QPainter.RenderHint.Antialiasing);
        view.resize(1024, 768);
        view.show();

        QApplication.exec();
    }
}
