import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_kernel_qtimer {
        static QObject pp = null;
//! [0]
    public static void main(String args[])
    {
        QApplication.initialize(args);
        QTimer.singleShot(600000, pp, "quit()");
        // ...
        QApplication.execStatic();
        QApplication.shutdown();
    }
//! [0]


}
