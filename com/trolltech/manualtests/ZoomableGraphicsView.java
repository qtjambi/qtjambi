package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class ZoomableGraphicsView extends QGraphicsView {
    // Zoom on wheel events
    protected void wheelEvent(QWheelEvent event) {
        double scaleFactor = Math.pow(2, -event.delta() / 240.0);

        if (event.modifiers().isSet(Qt.KeyboardModifier.ControlModifier)) {
            rotate(event.delta() * 0.01);
        } else {
            QMatrix m = matrix();
            m.scale(scaleFactor, scaleFactor);
            double factor = m.mapRect(new QRectF(0, 0, 1, 1)).width();
            if (factor < 0.07 || factor > 100)
                return;

            scale(scaleFactor, scaleFactor);
        }

    }

}
