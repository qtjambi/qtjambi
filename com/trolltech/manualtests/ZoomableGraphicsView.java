package com.trolltech.manualtests;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMatrix;
import com.trolltech.qt.gui.QWheelEvent;

public class ZoomableGraphicsView extends QGraphicsView {
    // Zoom on wheel events
    protected void wheelEvent(QWheelEvent event) {
        double scaleFactor = Math.pow(2, -event.delta() / 240.0);
        QMatrix m = matrix();
        m.scale(scaleFactor, scaleFactor);
        double factor = m.mapRect(new QRectF(0, 0, 1, 1)).width();
        if (factor < 0.07 || factor > 100)
            return;

        scale(scaleFactor, scaleFactor);

    }

}
