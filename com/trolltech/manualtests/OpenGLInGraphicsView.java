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

package com.trolltech.manualtests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.opengl.*;
import com.trolltech.qt.gui.*;

public class OpenGLInGraphicsView extends ZoomableGraphicsView {

    public OpenGLInGraphicsView(boolean gl) {
        QGLFormat format = new QGLFormat();
        format.setSampleBuffers(true);

        if (gl) {
            setViewport(new QGLWidget(format));
            setWindowTitle("GL window");
        } else {
            setWindowTitle("Non GL Window");
        }

        QGraphicsScene scene = new QGraphicsScene();

        QGraphicsPixmapItem pixmapItem = new QGraphicsPixmapItem(new QPixmap("classpath:com/trolltech/images/chip-demo.png")) {

            @Override
            public void paint(QPainter painter,
                    QStyleOptionGraphicsItem option, QWidget widget) {
                //painter.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform, true);
                //super.paint(painter, option, widget);
                painter.drawPixmap(boundingRect().toRect(), this.pixmap());
            }

        };
        pixmapItem.setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        pixmapItem.setTransformationMode(Qt.TransformationMode.SmoothTransformation);
        scene.addItem(pixmapItem);

        QGraphicsPixmapItem otherPixmapItem = scene.addPixmap(new QPixmap("classpath:com/trolltech/images/chip-demo.png"));
        otherPixmapItem.setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        otherPixmapItem.setTransformationMode(Qt.TransformationMode.SmoothTransformation);

        scene.addText("Hello World").setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);

        setScene(scene);
        rotate(45);

        setRenderHint(QPainter.RenderHint.SmoothPixmapTransform);

        //setRenderHint(QPainter.RenderHint.Antialiasing, true);
        //setRenderHint(QPainter.RenderHint.HighQualityAntialiasing, true);
        //setRenderHint(QPainter.RenderHint.TextAntialiasing, true);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        OpenGLInGraphicsView widget1 = new OpenGLInGraphicsView(true);
        widget1.show();

        OpenGLInGraphicsView widget2 = new OpenGLInGraphicsView(false);
        widget2.show();

        QApplication.exec();
    }

}
