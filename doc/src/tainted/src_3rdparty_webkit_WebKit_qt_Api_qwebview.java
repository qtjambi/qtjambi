/*   Ported from: src.3rdparty.webkit.WebKit.qt.Api.qwebview.cpp
<snip>
//! [0]
    view->page()->history();
//! [0]


//! [1]
    view->page()->settings();
//! [1]


//! [2]
    view->triggerAction(QWebPage::Copy);
//! [2]


//! [3]
    view->page()->triggerAction(QWebPage::Stop);
//! [3]


//! [4]
    view->page()->triggerAction(QWebPage::GoBack);
//! [4]


//! [5]
    view->page()->triggerAction(QWebPage::GoForward);
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_3rdparty_webkit_WebKit_qt_Api_qwebview {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    view.page().history();
//! [0]


//! [1]
    view.page().settings();
//! [1]


//! [2]
    view.triggerAction(QWebPage.Copy);
//! [2]


//! [3]
    view.page().triggerAction(QWebPage.Stop);
//! [3]


//! [4]
    view.page().triggerAction(QWebPage.GoBack);
//! [4]


//! [5]
    view.page().triggerAction(QWebPage.GoForward);
//! [5]


    }
}
