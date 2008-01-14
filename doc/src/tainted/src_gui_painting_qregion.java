/*   Ported from: src.gui.painting.qregion.cpp
<snip>
//! [0]
        void MyWidget::paintEvent(QPaintEvent *)
        {
            QRegion r1(QRect(100, 100, 200, 80),    // r1: elliptic region
                       QRegion::Ellipse);
            QRegion r2(QRect(100, 120, 90, 30));    // r2: rectangular region
            QRegion r3 = r1.intersected(r2);        // r3: intersection

            QPainter painter(this);
	    painter.setClipRegion(r3);
            ...                                     // paint clipped graphics
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


public class src_gui_painting_qregion {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        void MyWidget.paintEvent(QPaintEvent *)
        {
            QRegion r1(QRect(100, 100, 200, 80),    // r1: elliptic region
                       QRegion.Ellipse);
            QRegion r2(QRect(100, 120, 90, 30));    // r2: rectangular region
            QRegion r3 = r1.intersected(r2);        // r3: intersection

            QPainter painter(this);
	    painter.setClipRegion(r3);
            ...                                     // paint clipped graphics
        }
//! [0]


    }
}
