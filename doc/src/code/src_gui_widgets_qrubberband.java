/*   Ported from: src.gui.widgets.qrubberband.cpp
<snip>
//! [0]
        void Widget::mousePressEvent(QMouseEvent *event)
        {
            origin = event->pos();
            if (!rubberBand)
                rubberBand = new QRubberBand(QRubberBand::Rectangle, this);
            rubberBand->setGeometry(QRect(origin, QSize()));
            rubberBand->show();
        }

        void Widget::mouseMoveEvent(QMouseEvent *event)
        {
            rubberBand->setGeometry(QRect(origin, event->pos()).normalized());
        }

        void Widget::mouseReleaseEvent(QMouseEvent *event)
        {
            rubberBand->hide();
            // determine selection, for example using QRect::intersects()
            // and QRect::contains().
        }
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qrubberband extends QWidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
    QPoint origin;
    QRubberBand rubberBand;
//! [0]
        protected void mousePressEvent(QMouseEvent event)
        {
            origin = event.pos();
            if (rubberBand != null)
                rubberBand = new QRubberBand(QRubberBand.Shape.Rectangle, this);
            rubberBand.setGeometry(new QRect(origin, new QSize()));
            rubberBand.show();
        }

        protected void mouseMoveEvent(QMouseEvent event)
        {
            rubberBand.setGeometry(new QRect(origin, event.pos()).normalized());
        }

        protected void mouseReleaseEvent(QMouseEvent event)
        {
            rubberBand.hide();
            // determine selection, for example using QRect.intersects()
            // and QRect.contains().
        }
//! [0]
}
