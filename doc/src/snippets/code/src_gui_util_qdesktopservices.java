
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_util_qdesktopservices {


    public static void main(String args[]) {
        QApplication.initialize(args);

//! [0]
        QDesktopServices.setUrlHandler("help", new QDesktopServices.UrlHandler() {
            public void handleUrl(QUrl url) {
                // respond to url...
            }
            });
//! [0]


    /*
//! [1]
    mailto:user@foo.com?subject=Testody=Just a test
//! [1]
    */

    }
}
