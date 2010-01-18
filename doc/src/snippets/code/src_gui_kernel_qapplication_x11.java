import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qapplication_x11 {
    public static void calculateHugeMandelbrot() {
    }

    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QApplication.setOverrideCursor(new QCursor(Qt.CursorShape.WaitCursor));
        calculateHugeMandelbrot();              // lunch time...
        QApplication.restoreOverrideCursor();
//! [0]


    }
}
