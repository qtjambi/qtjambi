import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_tools_qrect {
    public static void main(String args[])
    {
        QApplication.initialize(args);
        {
//! [0]
        QRect r1 = new QRect(100, 200, 11, 16);
        QRect r2 = new QRect(new QPoint(100, 200), new QSize(11, 16));
//! [0]
        }
        {
//! [1]
        QRectF r1 = new QRectF(100, 200, 11, 16);
        QRectF r2 = new QRectF(new QPointF(100, 200), new QSizeF(11, 16));
//! [1]
        }

    }
}
