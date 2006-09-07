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

package com.trolltech.demos.imageviewer;

import com.trolltech.launcher.Worker;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class View extends QWidget
{
    public Signal1<Boolean> valid;

    public View(QWidget parent) {
        super(parent);

        int size = 40;
        QPixmap bg = new QPixmap(size, size);
        bg.fill(QColor.white);
        QPainter p = new QPainter();
        p.begin(bg);
        p.fillRect(0, 0, size/2, size/2, new QBrush(QColor.lightGray));
        p.fillRect(size/2, size/2, size/2, size/2, new QBrush(QColor.lightGray));
        p.end();

        QPalette pal = palette();
        pal.setBrush(backgroundRole(), new QBrush(bg));
        setPalette(pal);

        setAutoFillBackground(true);

        delayedUpdate.setDelay(10);
    }

    public QImage image() {
        return original;
    }

    public void setImage(QImage original) {
        this.original = original != null ? original.convertToFormat(QImage.Format.Format_ARGB32_Premultiplied) : null;
        zoom = 1;
        calculatePos(original);
        resetImage();

        valid.emit(original != null);
    }

    public QImage modifiedImage() {
        return modified;
    }

    public void setColorBalance(int c) {
        colorBalance = c;
        resetImage();
    }

    public void setRedCyan(int c) {
        redCyan = c;
        resetImage();
    }

    public void setGreenMagenta(int c) {
        greenMagenta = c;
        resetImage();
    }

    public void setBlueYellow(int c) {
        blueYellow = c;
        resetImage();
    }

    public void increaseZoom() {
        zoom = Math.min(1000, zoom * 1.1);
        resetImage();
    }

    public void decreaseZoom() {
        zoom = Math.max(1/1000.0, zoom * 0.9);
        resetImage();
    }

    public QSize sizeHint() {
        return new QSize(500, 500);
    }

    protected void paintEvent(QPaintEvent e) {
        try {
        if (modified == null)
            updateImage();

        QPainter p = new QPainter();
        p.begin(this);

        if (modified != null) {
            p.save();
            p.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform);

            if (p.paintEngine().hasFeature(new QPaintEngine.PaintEngineFeatures(QPaintEngine.PaintEngineFeature.PixmapTransform)))
                p.drawImage(new QRect(posx, posy, currentWidth, currentHeight), modified);
            else {
                p.translate(posx, posy);
                p.scale(zoom, zoom);
                p.setPen(QPen.NoPen);
                p.setBrush(new QBrush(QPixmap.fromImage(modified)));
                p.drawRect(0, 0, modified.width(), modified.height());
            }
            p.restore();
        }

        p.drawRect(0, 0, width()-1, height()-1);
        p.end();
        } catch (Exception ex) { ex.printStackTrace(); };
    }

    protected void resizeEvent(QResizeEvent e) {
        calculatePos(modified);
    }

    protected void mousePressEvent(QMouseEvent e) {
        mposx = e.x();
        mposy = e.y();
    }

    protected void mouseMoveEvent(QMouseEvent e) {
        posx += e.x() - mposx;
        posy += e.y() - mposy;

        mposx = e.x();
        mposy = e.y();

        update();
    }

    protected void wheelEvent(QWheelEvent e) {
        if (original == null)
            return;

        int mposx = e.x();
        int mposy = e.y();


        int odx = mposx - posx;
        int ody = mposy - posy;

        double ozoom = zoom;

        if (e.delta() > 0)
            increaseZoom();
        else
            decreaseZoom();

        // Don't do relative scale if the mouse is outside the image
        if (mposx < posx || mposy < posy
                || mposx > posx + original.width() * ozoom
                || mposy > posy + original.height() * ozoom)
            return;

        posx -= (int) Math.round(odx / ozoom * zoom) - odx;
        posy -= (int) Math.round(ody / ozoom * zoom) - ody;

    }

    private final void resetImage() {
        if (modified != null)
            modified.dispose();
        modified = null;
        delayedUpdate.start();
    }

    private static final QColor decideColor(int value, QColor c1, QColor c2) {
        QColor c = value < 0 ? c1 : c2;
        double sign = value < 0 ? -1.0 : 1.0;
        return QColor.fromRgbF(c.redF(), c.greenF(), c.blueF(), sign * value * 0.5 / 100);
    }

    private void calculatePos(QPaintDeviceInterface img) {
        if (img == null)
            return;
        int iw = img.width();
        int ih = img.height();
        int w = width();
        int h = height();

        posx = w / 2 - iw / 2;
        posy = h / 2 - ih / 2;
    }

    private void updateImage() {
        if (original == null)
            return;

        int oiw = original.width();
        int oih = original.height();

        currentWidth = (int)(oiw * zoom);
        currentHeight = (int)(oih * zoom);

        if (modified != null)
            modified.dispose();

        modified = original.copy();

        QPainter p = new QPainter();
        p.begin(modified);
        p.setCompositionMode(QPainter.CompositionMode.CompositionMode_SourceAtop);
        if (redCyan != 0) {
            QColor c = decideColor(redCyan, QColor.cyan, QColor.red);
            p.fillRect(0, 0, modified.width(), modified.height(), new QBrush(c));
        }
        if (greenMagenta != 0) {
            QColor c = decideColor(greenMagenta, QColor.magenta, QColor.green);
            p.fillRect(0, 0, modified.width(), modified.height(), new QBrush(c));
        }
        if (blueYellow != 0) {
            QColor c = decideColor(blueYellow, QColor.yellow, QColor.blue);
            p.fillRect(0, 0, modified.width(), modified.height(), new QBrush(c));
        }
        if (colorBalance != 0) {
            QColor c = decideColor(colorBalance, QColor.white, QColor.black);
            p.fillRect(0, 0, modified.width(), modified.height(), new QBrush(c));
        }
        p.end();
    }

    private int colorBalance;
    private int redCyan;
    private int greenMagenta;
    private int blueYellow;

    private int posx;
    private int posy;
    private int currentWidth;
    private int currentHeight;

    private int mposx;
    private int mposy;

    double zoom = 1;

    private QImage original;
    private QImage modified;

    private Worker delayedUpdate = new Worker() {
        public void execute() {
            update();
        }
    };
}
