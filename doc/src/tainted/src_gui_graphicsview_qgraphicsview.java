/*   Ported from: src.gui.graphicsview.qgraphicsview.cpp
<snip>
//! [0]
        QGraphicsScene scene;
        scene.addText("Hello, world!");

        QGraphicsView view(&scene);
        view.show();
//! [0]


//! [1]
        QGraphicsScene scene;
        scene.addRect(QRectF(-10, -10, 20, 20));

        QGraphicsView view(&scene);
        view.setRenderHints(QPainter::Antialiasing | QPainter::SmoothPixmapTransform);
        view.show();
//! [1]


//! [2]
        QGraphicsView view;
        view.setBackgroundBrush(QImage(":/images/backgroundtile.png"));
        view.setCacheMode(QGraphicsView::CacheBackground);
//! [2]


//! [3]
        QGraphicsScene scene;
        scene.addText("GraphicsView rotated clockwise");

        QGraphicsView view(&scene);
        view.rotate(90); // the text is rendered with a 90 degree clockwise rotation
        view.show();
//! [3]


//! [4]
        QGraphicsScene scene;
        scene.addItem(...
        ...

        QGraphicsView view(&scene);
        view.show();
        ...

        QPrinter printer(QPrinter::HighResolution);
        printer.setPageSize(QPrinter::A4);
        QPainter painter(&printer);

        // print, fitting the viewport contents into a full page
        view.render(&painter);

        // print the upper half of the viewport into the lower.
        // half of the page.
        QRect viewport = view.viewport()->rect();
        view.render(&painter,
                    QRectF(0, printer.height() / 2,
                           printer.width(), printer.height() / 2),
                    viewport.adjusted(0, 0, 0, -viewport.height() / 2));

//! [4]


//! [5]
        void CustomView::mousePressEvent(QMouseEvent *event)
        {
            qDebug() << "There are" << items(event->pos()).size()
                     << "items at position" << mapToScene(event->pos());
        }
//! [5]


//! [6]
        void CustomView::mousePressEvent(QMouseEvent *event)
        {
            if (QGraphicsItem *item = itemAt(event->pos())) {
                qDebug() << "You clicked on item" << item;
            } else {
                qDebug() << "You didn't click on an item.";
            }
        }
//! [6]


//! [7]
        QGraphicsScene scene;
        scene.addText("GraphicsView rotated clockwise");

        QGraphicsView view(&scene);
        view.rotate(90); // the text is rendered with a 90 degree clockwise rotation
        view.show();
//! [7]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_graphicsview_qgraphicsview {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QGraphicsScene scene;
        scene.addText("Hello, world!");

        QGraphicsView view(cene);
        view.show();
//! [0]


//! [1]
        QGraphicsScene scene;
        scene.addRect(QRectF(-10, -10, 20, 20));

        QGraphicsView view(cene);
        view.setRenderHints(QPainter.Antialiasing | QPainter.SmoothPixmapTransform);
        view.show();
//! [1]


//! [2]
        QGraphicsView view;
        view.setBackgroundBrush(QImage(":/images/backgroundtile.png"));
        view.setCacheMode(QGraphicsView.CacheBackground);
//! [2]


//! [3]
        QGraphicsScene scene;
        scene.addText("GraphicsView rotated clockwise");

        QGraphicsView view(cene);
        view.rotate(90); // the text is rendered with a 90 degree clockwise rotation
        view.show();
//! [3]


//! [4]
        QGraphicsScene scene;
        scene.addItem(...
        ...

        QGraphicsView view(cene);
        view.show();
        ...

        QPrinter printer(QPrinter.HighResolution);
        printer.setPageSize(QPrinter.A4);
        QPainter painter(rinter);

        // print, fitting the viewport contents into a full page
        view.render(ainter);

        // print the upper half of the viewport into the lower.
        // half of the page.
        QRect viewport = view.viewport().rect();
        view.render(ainter,
                    QRectF(0, printer.height() / 2,
                           printer.width(), printer.height() / 2),
                    viewport.adjusted(0, 0, 0, -viewport.height() / 2));

//! [4]


//! [5]
        void CustomView.mousePressEvent(QMouseEvent vent)
        {
            qDebug() << "There are" << items(event.pos()).size()
                     << "items at position" << mapToScene(event.pos());
        }
//! [5]


//! [6]
        void CustomView.mousePressEvent(QMouseEvent vent)
        {
            if (QGraphicsItem tem = itemAt(event.pos())) {
                qDebug() << "You clicked on item" << item;
            } else {
                qDebug() << "You didn't click on an item.";
            }
        }
//! [6]


//! [7]
        QGraphicsScene scene;
        scene.addText("GraphicsView rotated clockwise");

        QGraphicsView view(cene);
        view.rotate(90); // the text is rendered with a 90 degree clockwise rotation
        view.show();
//! [7]


    }
}
