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
