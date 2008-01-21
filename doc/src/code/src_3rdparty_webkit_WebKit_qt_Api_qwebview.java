import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.webkit.QWebPage;
import com.trolltech.qt.webkit.QWebView;


public class src_3rdparty_webkit_WebKit_qt_Api_qwebview {
    public static void main(String args[]) {
        QApplication.initialize(args);
        QWebView view = new QWebView();
//! [0]
    view.page().history();
//! [0]


//! [1]
    view.page().settings();
//! [1]


//! [2]
    view.triggerAction(QWebPage.WebAction.Copy);
//! [2]


//! [3]
    view.page().triggerAction(QWebPage.WebAction.Stop);
//! [3]


//! [4]
    view.page().triggerAction(QWebPage.WebAction.GoBack);
//! [4]


//! [5]
    view.page().triggerAction(QWebPage.WebAction.GoForward);
//! [5]


    }
}
