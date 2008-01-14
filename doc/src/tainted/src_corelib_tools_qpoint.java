/*   Ported from: src.corelib.tools.qpoint.cpp
<snip>
//! [0]
        QPoint p;

        p.setX(p.x() + 1);
        p += QPoint(1, 0);
        p.rx()++;
//! [0]


//! [1]
        QPoint p(1, 2);
        p.rx()--;   // p becomes (0, 2)
//! [1]


//! [2]
        QPoint p(1, 2);
        p.ry()++;   // p becomes (1, 3)
//! [2]


//! [3]
        QPoint p( 3, 7);
        QPoint q(-1, 4);
        p += q;    // p becomes (2, 11)
//! [3]


//! [4]
        QPoint p( 3, 7);
        QPoint q(-1, 4);
        p -= q;    // p becomes (4, 3)
//! [4]


//! [5]
        QPoint p(-1, 4);
        p *= 2.5;    // p becomes (-3, 10)
//! [5]


//! [6]
        QPoint p(-3, 10);
        p /= 2.5;           // p becomes (-1, 4)
//! [6]


//! [7]
        QPoint oldPosition;

        MyWidget::mouseMoveEvent(QMouseEvent *event)
        {
            QPoint point = event->pos() - oldPosition;
            if (point.manhattanLength() > 3)
                // the mouse has moved more than 3 pixels since the oldPosition
        }
//! [7]


//! [8]
        int trueManhattanLength = sqrt(pow(x(), 2) + pow(y(), 2));
//! [8]


//! [9]
        QPointF p;

        p.setX(p.x() + 1.0);
        p += QPoint(1.0, 0.0);
        p.rx()++;
//! [9]


//! [10]
         QPoint p(1.1, 2.5);
         p.rx()--;   // p becomes (0.1, 2.5)
//! [10]


//! [11]
        QPoint p(1.1, 2.5);
        p.ry()++;   // p becomes (1.1, 3.5)
//! [11]


//! [12]
        QPoint p( 3.1, 7.1);
        QPoint q(-1.0, 4.1);
        p += q;    // p becomes (2.1, 11.2)
//! [12]


//! [13]
        QPoint p( 3.1, 7.1);
        QPoint q(-1.0, 4.1);
        p -= q;    // p becomes (4.1, 3.0)
//! [13]


//! [14]
         QPoint p(-1.1, 4.1);
         p *= 2.5;    // p becomes (-2.75,10.25)
//! [14]


//! [15]
        QPoint p(-2.75, 10.25);
        p /= 2.5;           // p becomes (-1.1,4.1)
//! [15]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_tools_qpoint {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QPoint p;

        p.setX(p.x() + 1);
        p += QPoint(1, 0);
        p.rx()++;
//! [0]


//! [1]
        QPoint p(1, 2);
        p.rx()--;   // p becomes (0, 2)
//! [1]


//! [2]
        QPoint p(1, 2);
        p.ry()++;   // p becomes (1, 3)
//! [2]


//! [3]
        QPoint p( 3, 7);
        QPoint q(-1, 4);
        p += q;    // p becomes (2, 11)
//! [3]


//! [4]
        QPoint p( 3, 7);
        QPoint q(-1, 4);
        p -= q;    // p becomes (4, 3)
//! [4]


//! [5]
        QPoint p(-1, 4);
        p *= 2.5;    // p becomes (-3, 10)
//! [5]


//! [6]
        QPoint p(-3, 10);
        p /= 2.5;           // p becomes (-1, 4)
//! [6]


//! [7]
        QPoint oldPosition;

        MyWidget.mouseMoveEvent(QMouseEvent vent)
        {
            QPoint point = event.pos() - oldPosition;
            if (point.manhattanLength() > 3)
                // the mouse has moved more than 3 pixels since the oldPosition
        }
//! [7]


//! [8]
        int trueManhattanLength = sqrt(pow(x(), 2) + pow(y(), 2));
//! [8]


//! [9]
        QPointF p;

        p.setX(p.x() + 1.0);
        p += QPoint(1.0, 0.0);
        p.rx()++;
//! [9]


//! [10]
         QPoint p(1.1, 2.5);
         p.rx()--;   // p becomes (0.1, 2.5)
//! [10]


//! [11]
        QPoint p(1.1, 2.5);
        p.ry()++;   // p becomes (1.1, 3.5)
//! [11]


//! [12]
        QPoint p( 3.1, 7.1);
        QPoint q(-1.0, 4.1);
        p += q;    // p becomes (2.1, 11.2)
//! [12]


//! [13]
        QPoint p( 3.1, 7.1);
        QPoint q(-1.0, 4.1);
        p -= q;    // p becomes (4.1, 3.0)
//! [13]


//! [14]
         QPoint p(-1.1, 4.1);
         p *= 2.5;    // p becomes (-2.75,10.25)
//! [14]


//! [15]
        QPoint p(-2.75, 10.25);
        p /= 2.5;           // p becomes (-1.1,4.1)
//! [15]


    }
}
