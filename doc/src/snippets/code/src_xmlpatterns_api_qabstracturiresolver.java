import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QApplication;

public class src_xmlpatterns_api_qabstracturiresolver {

    static QUrl baseURI = new QUrl();

    public static void main(String args[]) {
        QApplication.initialize(args);
        test(new QUrl());
    }

    public static QUrl test(QUrl relative) {
//! [0]
    return baseURI.resolved(relative);
//! [0]
    }


}
