/*   Ported from: doc.src.coordsys.qdoc
<snip>
//! [0]
    QPainter painter(this);

    painter.setPen(Qt::darkGreen);
    painter.drawRect(1, 2, 6, 4);
//! [0]


//! [1]
    QPainter painter(this);

    painter.setPen(Qt::darkGreen);
    painter.drawLine(2, 7, 6, 1);
//! [1]


//! [2]
    QPainter painter(this);
    painter.setRenderHint(
        QPainter::Antialiasing);
    painter.setPen(Qt::darkGreen);
    painter.drawRect(1, 2, 6, 4);
//! [2]


//! [3]
    QPainter painter(this);
    painter.setRenderHint(
        QPainter::Antialiasing);
    painter.setPen(Qt::darkGreen);
    painter.drawLine(2, 7, 6, 1);
//! [3]


//! [4]
        QPainter painter(this);
        painter.setWindow(QRect(-50, -50, 100, 100));
//! [4]


//! [5]
        int side = qMin(width(), height())
        int x = (width() - side / 2);
        int y = (height() - side / 2);

        painter.setViewport(x, y, side, side);
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_coordsys {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QPainter painter(this);

    painter.setPen(Qt.darkGreen);
    painter.drawRect(1, 2, 6, 4);
//! [0]


//! [1]
    QPainter painter(this);

    painter.setPen(Qt.darkGreen);
    painter.drawLine(2, 7, 6, 1);
//! [1]


//! [2]
    QPainter painter(this);
    painter.setRenderHint(
        QPainter.Antialiasing);
    painter.setPen(Qt.darkGreen);
    painter.drawRect(1, 2, 6, 4);
//! [2]


//! [3]
    QPainter painter(this);
    painter.setRenderHint(
        QPainter.Antialiasing);
    painter.setPen(Qt.darkGreen);
    painter.drawLine(2, 7, 6, 1);
//! [3]


//! [4]
        QPainter painter(this);
        painter.setWindow(QRect(-50, -50, 100, 100));
//! [4]


//! [5]
        int side = qMin(width(), height())
        int x = (width() - side / 2);
        int y = (height() - side / 2);

        painter.setViewport(x, y, side, side);
//! [5]


    }
}
