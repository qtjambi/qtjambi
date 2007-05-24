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

package com.trolltech.tools.designer;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

public class HourGlass extends QWidget {

    public HourGlass() {
        this(null);
    }

    public HourGlass(QWidget parent) {
        super(parent);

        double mainRadius = 8;
        double maxRadius = 2.5;
        double minRadius = 1;
        path = new QPainterPath();
        QPointF pt = new QPointF(0, mainRadius - maxRadius);
        QMatrix m = new QMatrix();
        for (int i=0; i<8; ++i) {
            m.rotate(-360 / 8);
            double size = Math.max(maxRadius * 2 - i, minRadius * 2);
            QRectF r = new QRectF(-size / 2, -size / 2, size, size);
            r.moveCenter(m.map(pt));
            path.addEllipse(r);
        }
        hide();
    }

    public void start() {
        if (running)
            return;
        running = true;
        run(true);
        show();
    }

    public void stop() {
        running = false;
        run(false);
        hide();
    }

    protected void showEvent(QShowEvent e) {
        run(true);
    }

    protected void hideEvent(QHideEvent e) {
        run(false);
    }

    protected void timerEvent(QTimerEvent e) {
        rotation += 360 / 8;
        update();
    }

    protected void paintEvent(QPaintEvent arg) {
        QPainter p = new QPainter(this);
        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.translate(rect().center());

        p.rotate(rotation);
        p.setBrush(new QBrush(QColor.gray));
        p.setPen(QPen.NoPen);

        p.fillPath(path, new QBrush(QColor.gray));
    }

    public QSize sizeHint() {
        return new QSize(16, 16);
    }

    private void run(boolean visible) {
        if (visible && running && timerId == 0) {
            timerId = startTimer(100);
        } else if (timerId != 0 && (!visible || !running)) {
            killTimer(timerId);
            timerId = 0;
        }
    }

    private boolean running;
    private int timerId;
    private int rotation;
    private QPainterPath path;
}
