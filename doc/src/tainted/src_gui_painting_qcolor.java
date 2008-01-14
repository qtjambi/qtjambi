/*   Ported from: src.gui.painting.qcolor.cpp
<snip>
//! [0]
    // Specify semi-transparent red
    painter.setBrush(QColor(255, 0, 0, 127));
    painter.drawRect(0, 0, width()/2, height());

    // Specify semi-transparent blue
    painter.setBrush(QColor(0, 0, 255, 127));
    painter.drawRect(0, 0, width(), height()/2);
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


public class src_gui_painting_qcolor {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    // Specify semi-transparent red
    painter.setBrush(QColor(255, 0, 0, 127));
    painter.drawRect(0, 0, width()/2, height());

    // Specify semi-transparent blue
    painter.setBrush(QColor(0, 0, 255, 127));
    painter.drawRect(0, 0, width(), height()/2);
//! [0]


    }
}
