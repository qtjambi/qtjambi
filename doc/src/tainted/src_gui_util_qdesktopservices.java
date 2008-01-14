/*   Ported from: src.gui.util.qdesktopservices.cpp
<snip>
//! [0]
    class MyHelpHandler : public QObject
    {
        Q_OBJECT
    public:
        ...
    public slots:
        void showHelp(const QUrl &url);
    };

    QDesktopServices::setUrlHandler("help", helpInstance, "showHelp");
//! [0]


//! [1]
    mailto:user@foo.com?subject=Test&body=Just a test
//! [1]


</snip>
*/
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
    class MyHelpHandler : public QObject
    {
        Q_OBJECT
    public:
        ...
    public slots:
        void showHelp(QUrl rl);
    };

    QDesktopServices.setUrlHandler("help", helpInstance, "showHelp");
//! [0]


//! [1]
    mailto:user@foo.com?subject=Testody=Just a test
//! [1]


    }
}
