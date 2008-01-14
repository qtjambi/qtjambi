/*   Ported from: src.gui.graphicsview.qgraphicsproxywidget.cpp
<snip>
//! [0]
    int main(int argc, char **argv)
    {
        QApplication app(argc, argv);

        QTabWidget *tabWidget = new QTabWidget;

        QGraphicsScene scene;
        QGraphicsProxyWidget *tabWidget = scene.addWidget(tabWidget);

        QGraphicsView view(&scene);
        view.show();

        return app.exec();
    }
//! [0]


//! [1]
        QGraphicsScene scene;

        QLineEdit *edit = new QLineEdit;
        QGraphicsProxyWidget *proxy = scene.addWidget(edit);

        edit->isVisible();  // returns false, as QWidget is hidden by default
        proxy->isVisible(); // also returns false

        edit->show();

        edit->isVisible(); // returns true
        proxy->isVisible(); // returns true
    }
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


public class src_gui_graphicsview_qgraphicsproxywidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    int main(int argc, char *rgv)
    {
        QApplication app(argc, argv);

        QTabWidget abWidget = new QTabWidget;

        QGraphicsScene scene;
        QGraphicsProxyWidget abWidget = scene.addWidget(tabWidget);

        QGraphicsView view(cene);
        view.show();

        return app.exec();
    }
//! [0]


//! [1]
        QGraphicsScene scene;

        QLineEdit dit = new QLineEdit;
        QGraphicsProxyWidget roxy = scene.addWidget(edit);

        edit.isVisible();  // returns false, as QWidget is hidden by default
        proxy.isVisible(); // also returns false

        edit.show();

        edit.isVisible(); // returns true
        proxy.isVisible(); // returns true
    }
//! [1]


    }
}
