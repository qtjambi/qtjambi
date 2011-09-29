import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_painting_qcolor extends QWidget {

    protected void paintEvent(QPaintEvent e) {
        QPainter painter = new QPainter(this);

//! [0]
        // Specify semi-transparent red
        painter.setBrush(new QColor(255, 0, 0, 127));
        painter.drawRect(0, 0, width()/2, height());

        // Specify semi-transparent blue
        painter.setBrush(new QColor(0, 0, 255, 127));
        painter.drawRect(0, 0, width(), height()/2);
//! [0]

    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        QWidget w = new src_gui_painting_qcolor();
        w.show();

        QApplication.execStatic();
        QApplication.shutdown();

    }
}
