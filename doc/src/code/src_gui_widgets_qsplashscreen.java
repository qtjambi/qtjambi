import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qsplashscreen {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
       QPixmap pixmap = new QPixmap(":/splash.png");
       QSplashScreen splash = new QSplashScreen(pixmap);
       splash.show();

       // Loading some items ...
       splash.showMessage("Loaded modules");

       QApplication.processEvents();

       // Establishing connections ...
       splash.showMessage("Established connections");

       QApplication.processEvents();
//! [0]


    }
}
