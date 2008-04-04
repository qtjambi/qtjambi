package com.trolltech.manualtests;

import com.trolltech.qt.opengl.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

public class VideoInGraphicsView extends ZoomableGraphicsView {

    public VideoInGraphicsView(String file) {
        setRenderHint(QPainter.RenderHint.Antialiasing);

        // Video widget
        VideoWidget videoWidget = new VideoWidget();

        // Set up video player
        final MediaObject mediaObject = new MediaObject(this);
        Phonon.createPath(mediaObject, videoWidget);
        mediaObject.setCurrentSource(new MediaSource(file));
        mediaObject.play();

        mediaObject.finished.connect(new Object() {
                public void start() {
                    mediaObject.seek(0);
                    mediaObject.play();
                }
            }, "start()");

        QGraphicsProxyWidget w = new QGraphicsProxyWidget();
        w.setWidget(videoWidget);
        w.setVisible(true);
        setRenderHint(QPainter.RenderHint.SmoothPixmapTransform);

        // Put ellipse on top
        w.setZValue(-1);

        // Set up a graphics scene
        QGraphicsScene scene = new QGraphicsScene();

        scene.addEllipse(10, 10, 100, 100, new QPen(QColor.black), new QBrush(QColor.red)).setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        scene.addItem(w);
        QGraphicsTextItem item = new QGraphicsTextItem();


        QFile f = new QFile("test.html");
        f.open(QIODevice.OpenModeFlag.ReadOnly);
        QByteArray ba = f.readAll();
        System.err.println(ba.toString());
        f.close();
        item.setPos(w.boundingRect().bottomLeft());
        item.setHtml(ba.toString());
        item.setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
        item.setVisible(true);
        item.setTextInteractionFlags(Qt.TextInteractionFlag.TextEditorInteraction);
        scene.addItem(item);

        setScene(scene);

        // Rotate the scene 45 degrees
        rotate(45);

        setViewport(new QGLWidget());

        startTimer(100);

    }

    protected void timerEvent(QTimerEvent e) {
        rotate(0.1);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

                String file = null;
                if (args.length > 0) {
                    file = args[0];

                    if (!new QFileInfo(file).exists()) {
                        System.err.println("File does not exist: " + file);
                        return;
                    }
                } else {
                    System.err.println("Please specify a movie file...");
                }

        VideoInGraphicsView view = new VideoInGraphicsView(file);
        view.show();

        QApplication.exec();
    }

}
