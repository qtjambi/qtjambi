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

package com.trolltech.examples;

import java.util.List;
import java.util.Random;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class CollidingMice extends QWidget {

    static final int MOUSE_COUNT = 7;

    public static void main(String args[]) {
        QApplication.initialize(args);

        CollidingMice collidingMice = new CollidingMice();
        collidingMice.show();
        QApplication.exec();
    }

    public CollidingMice() {
        
        QGraphicsScene scene = new QGraphicsScene();
        scene.setSceneRect(-300, -300, 600, 600);
        scene.setItemIndexMethod(QGraphicsScene.ItemIndexMethod.NoIndex);

        for (int i = 0; i < MOUSE_COUNT; ++i) {
            Mouse mouse = new Mouse(this);
            mouse.setPos(Math.sin((i * 6.28) / MOUSE_COUNT) * 200, 
                         Math.cos((i * 6.28) / MOUSE_COUNT) * 200);
            scene.addItem(mouse);
        }

        QGraphicsView view = new QGraphicsView(scene);
        view.setRenderHint(QPainter.RenderHint.Antialiasing);
        view.setBackgroundBrush(new QBrush(new QPixmap(
                "classpath:com/trolltech/examples/images/cheese.png")));
        view.setCacheMode(new QGraphicsView.CacheMode(
                QGraphicsView.CacheModeFlag.CacheBackground));
        view.setDragMode(QGraphicsView.DragMode.ScrollHandDrag);

        QGridLayout layout = new QGridLayout();
        layout.addWidget(view, 0, 0);
        setLayout(layout);

        setWindowTitle("Colliding Mice");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        resize(400, 300);
    }
    
    public class Mouse extends QGraphicsItem {

        double angle = 0;
        double speed = 0;
        double mouseEyeDirection = 0;
        QColor color = null;
        Random generator = new Random();

        static final double TWO_PI = Math.PI * 2;

        public Mouse(QObject parent) {
            color = new QColor(generator.nextInt(256), generator.nextInt(256), 
                               generator.nextInt(256));
            rotate(generator.nextDouble() * 360);

            QObject timer = new QObject(parent) {
                @Override
                protected void timerEvent(QTimerEvent arg__0) {
                    move();
                }
            };
            timer.startTimer(1000 / 33);
        }

        public QRectF boundingRect() {
            double adjust = 0.5;
            return new QRectF(-20 - adjust, -22 - adjust, 
                              40 + adjust, 83 + adjust);
        }

        public QPainterPath shape() {
            QPainterPath path = new QPainterPath();
            path.addRect(-10, -20, 20, 40);
            return path;
        }

        public void paint(QPainter painter, 
                          QStyleOptionGraphicsItem styleOptionGraphicsItem, 
                          QWidget widget) {
            QBrush brush = new QBrush(Qt.BrushStyle.SolidPattern);

            // Body
            painter.setBrush(color);
            painter.drawEllipse(-10, -20, 20, 40);

            // Eyes
            brush.setColor(QColor.white);
            painter.setBrush(brush);
            painter.drawEllipse(-10, -17, 8, 8);
            painter.drawEllipse(2, -17, 8, 8);

            // Nose
            brush.setColor(QColor.black);
            painter.setBrush(brush);
            painter.drawEllipse(new QRectF(-2, -22, 4, 4));

            // Pupils
            painter.drawEllipse(new QRectF(-8.0 + mouseEyeDirection, -17, 4, 4));
            painter.drawEllipse(new QRectF(4.0 + mouseEyeDirection, -17, 4, 4));

            // Ears
            if (scene().collidingItems(this).isEmpty())
                brush.setColor(QColor.darkYellow);
            else
                brush.setColor(QColor.red);
            painter.setBrush(brush);

            painter.drawEllipse(-17, -12, 16, 16);
            painter.drawEllipse(1, -12, 16, 16);

            // Tail
            QPainterPath path = new QPainterPath(new QPointF(0, 20));
            path.cubicTo(-5, 22, -5, 22, 0, 25);
            path.cubicTo(5, 27, 5, 32, 0, 30);
            path.cubicTo(-5, 32, -5, 42, 0, 35);
            painter.setBrush(QBrush.NoBrush);
            painter.drawPath(path);
        }

        public void move() {
            // Don't move too far away
            QLineF lineToCenter = new QLineF(new QPointF(0, 0), 
                                             mapFromScene(0, 0));
            if (lineToCenter.length() > 150) {
                double angleToCenter = Math.acos(lineToCenter.dx() 
                                                 / lineToCenter.length());
                if (lineToCenter.dy() < 0)
                    angleToCenter = TWO_PI - angleToCenter;
                angleToCenter = normalizeAngle((Math.PI - angleToCenter) 
                                               + Math.PI / 2);

                if (angleToCenter < Math.PI && angleToCenter > Math.PI / 4) {
                    // Rotate left
                    angle += (angle < -Math.PI / 2) ? 0.25 : -0.25;
                } else if (angleToCenter >= Math.PI 
                           && angleToCenter < (Math.PI + Math.PI / 2 
                                               + Math.PI / 4)) {
                    // Rotate right
                    angle += (angle < Math.PI / 2) ? 0.25 : -0.25;
                }
            } else if (Math.sin(angle) < 0) {
                angle += 0.25;
            } else if (Math.sin(angle) > 0) {
                angle -= 0.25;
            }

            // Try not to crash with any other mice

            QPolygonF polygon = new QPolygonF();
            polygon.append(mapToScene(0, 0));
            polygon.append(mapToScene(-30, -50));
            polygon.append(mapToScene(30, -50));

            List<QGraphicsItemInterface> dangerMice = scene().items(polygon);

            for (QGraphicsItemInterface item : dangerMice) {
                if (item == this)
                    continue;

                QLineF lineToMouse = new QLineF(new QPointF(0, 0), 
                                                mapFromItem(item, 0, 0));
                double angleToMouse = Math.acos(lineToMouse.dx() 
                                                / lineToMouse.length());
                if (lineToMouse.dy() < 0)
                    angleToMouse = TWO_PI - angleToMouse;
                angleToMouse = normalizeAngle((Math.PI - angleToMouse) 
                                              + Math.PI / 2);

                if (angleToMouse >= 0 && angleToMouse < (Math.PI / 2)) {
                    // Rotate right
                    angle += 0.5;
                } else if (angleToMouse <= TWO_PI 
                           && angleToMouse > (TWO_PI - Math.PI / 2)) {
                    // Rotate left
                    angle -= 0.5;
                }
            }

            // Add some random movement
            if (dangerMice.size() < 1 && generator.nextDouble() < 0.1) {
                if (generator.nextDouble() > 0.5)
                    angle += generator.nextDouble() / 5;
                else
                    angle -= generator.nextDouble() / 5;
            }

            speed += (-50 + generator.nextDouble() * 100) / 100.0;

            double dx = Math.sin(angle) * 10;
            mouseEyeDirection = (Math.abs(dx / 5) < 1) ? 0 : dx / 5;

            rotate(dx);
            setPos(mapToParent(0, -(3 + Math.sin(speed) * 3)));
        }

        private double normalizeAngle(double angle) {
            while (angle < 0)
                angle += TWO_PI;
            while (angle > TWO_PI)
                angle -= TWO_PI;
            return angle;
        }
    }
    // REMOVE-START
    
    public static String exampleName() {
        return "Colliding Mice";
    }

    public static boolean canInstantiate() {
        return true;
    }

    // REMOVE-END
}
