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
    public Signal1<Boolean> valid = new Signal1<Boolean>();

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

    public void setInvert(boolean b) {
        invert = b;
        resetImage();
    }

    public QSize sizeHint() {
        return new QSize(500, 500);
    }

    private QBrush brush = new QBrush();
    protected void paintEvent(QPaintEvent e) {
        if (background == null) {
            background = new QPixmap(size());
            QPainter p = new QPainter(background);
            QLinearGradient lg = new QLinearGradient(0, 0, 0, height());
            lg.setColorAt(0.5, QColor.black);
            lg.setColorAt(0.7, QColor.fromRgbF(0.5, 0.5, 0.6));
            lg.setColorAt(1, QColor.fromRgbF(0.8, 0.8, 0.9));
            p.fillRect(background.rect(), new QBrush(lg));
            p.end();
        }

        QPainter p = new QPainter(this);
        p.drawPixmap(0, 0, background);

        if (modified == null)
            updateImage();

        if (modified != null) {
            p.setViewport(rect().adjusted(10, 10, -10, -10));
            QRect rect = rectForImage(modified);

            p.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform);
            p.drawImage(rect, modified);

            p.drawImage(0, height() - reflection.height() + 10, reflection);

        }
    }

    protected void resizeEvent(QResizeEvent e) {
        if (background != null) {
            background.dispose();
            background = null;
        }

        resetImage();
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

    private void updateImage() {
        if (original == null)
            return;

        int oiw = original.width();
        int oih = original.height();

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

        if (invert) {
            p.setCompositionMode(QPainter.CompositionMode.CompositionMode_Difference);
            p.fillRect(modified.rect(), new QBrush(QColor.white));
        }

        p.end();

        reflection = createReflection(modified);
    }

    private QRect rectForImage(QImage image) {
        QSize isize = image.size();
        QSize size = size();

        size.setHeight(size.height() * 3 / 4);
        size.setWidth(size.width() * 3 / 4);

        isize.scale(size, Qt.AspectRatioMode.KeepAspectRatio);

        return new QRect(width() / 2 - isize.width() / 2,
                         size.height() / 2 - isize.height() / 2,
                         isize.width(), isize.height());
    }

    private QImage createReflection(QImage source) {
        if (source == null)
            return null;

        QRect r = rectForImage(source);

        QImage image = new QImage(width(),
                                  height() - r.height() - r.y(),
                                  QImage.Format.Format_ARGB32_Premultiplied);
        image.fill(0);

        double iw = image.width();
        double ih = image.height();

        QPainter pt = new QPainter(image);

        pt.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform);
        pt.setRenderHint(QPainter.RenderHint.Antialiasing);

        pt.save(); {
            QPolygonF imageQuad = new QPolygonF();
            imageQuad.add(0, 0);
            imageQuad.add(0, source.height());
            imageQuad.add(source.width(), source.height());
            imageQuad.add(source.width(), 0);
            QPolygonF targetQuad = new QPolygonF();
            targetQuad.add(0, ih);
            targetQuad.add(iw / 2 - r.width() / 2, 0);
            targetQuad.add(iw / 2 + r.width() / 2, 0);
            targetQuad.add(iw, ih);
            pt.setTransform(QTransform.quadToQuad(imageQuad, targetQuad));
            pt.drawImage(imageQuad.boundingRect(), source);
        } pt.restore();

        QLinearGradient lg = new QLinearGradient(0, 0, 0, image.height());
        lg.setColorAt(0.1, QColor.fromRgbF(0, 0, 0, 0.4));
        lg.setColorAt(0.6, QColor.transparent);
        pt.setCompositionMode(QPainter.CompositionMode.CompositionMode_DestinationIn);
        pt.fillRect(image.rect(), new QBrush(lg));
        pt.end();

        return image;
    }



    private int colorBalance;
    private int redCyan;
    private int greenMagenta;
    private int blueYellow;

    private boolean invert;

    private QImage original;
    private QImage modified;
    private QImage reflection;
    private QPixmap background;

    private Worker delayedUpdate = new Worker(this) {
        public void execute() {
            update();
        }
    };
}
