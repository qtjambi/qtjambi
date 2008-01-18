/*   Ported from: src.corelib.kernel.qtimer.cpp
<snip>
//! [0]
        #include <QApplication>
        #include <QTimer>

        int main(int argc, char *argv[])
        {
            QApplication app(argc, argv);
            QTimer::singleShot(600000, &app, SLOT(quit()));
            ...
            return app.exec();
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


public class src_corelib_kernel_qtimer {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        #include <QApplication>
        #include <QTimer>

        int main(int argc, char rgv[])
        {
            QApplication app(argc, argv);
            QTimer.singleShot(600000, pp, SLOT(quit()));
            ...
            return app.exec();
        }
//! [0]


    }
}
