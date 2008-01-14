/*   Ported from: doc.src.graphicsview.qdoc
<snip>
//! [0]
            QGraphicsScene scene;
            QGraphicsRectItem *rect = scene.addRect(QRectF(0, 0, 100, 100));

            QGraphicsItem *item = scene.itemAt(50, 50);
            // item == rect
//! [0]


//! [1]
            QGraphicsScene scene;
            myPopulateScene(&scene);

            QGraphicsView view(&scene);
            view.show();
//! [1]


//! [2]
            class View : public QGraphicsView
            {
            Q_OBJECT
                ...
            public slots:
                void zoomIn() { scale(1.2, 1.2); }
                void zoomOut() { scale(1 / 1.2, 1 / 1.2); }
                void rotateLeft() { rotate(-10); }
                void rotateRight() { rotate(10); }
                ...
            };
//! [2]


//! [3]
            QGraphicsScene scene;
            scene.addRect(QRectF(0, 0, 100, 200), Qt::black, QBrush(Qt::green));

            QPrinter printer;
            if (QPrintDialog(&printer).exec() == QDialog::Accepted) {
                QPainter painter(&printer);
                painter.setRenderHint(QPainter::Antialiasing);
                scene.render(&painter);
            }
//! [3]


//! [4]
            QGraphicsScene scene;
            scene.addRect(QRectF(0, 0, 100, 200), Qt::black, QBrush(Qt::green));

            QPixmap pixmap;
            QPainter painter(&pixmap);
            painter.setRenderHint(QPainter::Antialiasing);
            scene.render(&painter);
            painter.end();

            pixmap.save("scene.png");
//! [4]


//! [5]
            void CustomItem::mousePressEvent(QGraphicsSceneMouseEvent *event)
            {
                QMimeData *data = new QMimeData;
                data->setColor(Qt::green);

                QDrag *drag = new QDrag(event->widget());
                drag->setMimeData(data);
                drag->start();
            }
//! [5]


//! [6]
            QGraphicsView view(&scene);
            view.setViewport(new QGLWidget(QGLFormat(QGL::SampleBuffers)));
//! [6]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_graphicsview {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
            QGraphicsScene scene;
            QGraphicsRectItem ect = scene.addRect(QRectF(0, 0, 100, 100));

            QGraphicsItem tem = scene.itemAt(50, 50);
            // item == rect
//! [0]


//! [1]
            QGraphicsScene scene;
            myPopulateScene(cene);

            QGraphicsView view(cene);
            view.show();
//! [1]


//! [2]
            class View : public QGraphicsView
            {
            Q_OBJECT
                ...
            public slots:
                void zoomIn() { scale(1.2, 1.2); }
                void zoomOut() { scale(1 / 1.2, 1 / 1.2); }
                void rotateLeft() { rotate(-10); }
                void rotateRight() { rotate(10); }
                ...
            };
//! [2]


//! [3]
            QGraphicsScene scene;
            scene.addRect(QRectF(0, 0, 100, 200), Qt.black, QBrush(Qt.green));

            QPrinter printer;
            if (QPrintDialog(rinter).exec() == QDialog.Accepted) {
                QPainter painter(rinter);
                painter.setRenderHint(QPainter.Antialiasing);
                scene.render(ainter);
            }
//! [3]


//! [4]
            QGraphicsScene scene;
            scene.addRect(QRectF(0, 0, 100, 200), Qt.black, QBrush(Qt.green));

            QPixmap pixmap;
            QPainter painter(ixmap);
            painter.setRenderHint(QPainter.Antialiasing);
            scene.render(ainter);
            painter.end();

            pixmap.save("scene.png");
//! [4]


//! [5]
            void CustomItem.mousePressEvent(QGraphicsSceneMouseEvent vent)
            {
                QMimeData ata = new QMimeData;
                data.setColor(Qt.green);

                QDrag rag = new QDrag(event.widget());
                drag.setMimeData(data);
                drag.start();
            }
//! [5]


//! [6]
            QGraphicsView view(cene);
            view.setViewport(new QGLWidget(QGLFormat(QGL.SampleBuffers)));
//! [6]


    }
}
