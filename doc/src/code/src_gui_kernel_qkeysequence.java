import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qkeysequence {
    public static void main(String args[]) {
        QApplication.initialize(args);
    class Dummy extends QWidget {
    public void foo() {
//! [0]
    new QKeySequence(QKeySequence.StandardKey.Print);
    new QKeySequence(tr("Ctrl+P"));
    new QKeySequence(tr("Ctrl+p"));
    new QKeySequence(Qt.Modifier.CTRL.value() + Qt.Key.Key_P.value());
//! [0]


//! [1]
    new QKeySequence(tr("Ctrl+X, Ctrl+C"));
    new QKeySequence(Qt.Modifier.CTRL.value() + Qt.Key.Key_X.value(),
                     Qt.Modifier.CTRL.value() + Qt.Key.Key_C.value());
//! [1]


//! [2]
        QMenu file = new QMenu(this);
        file.addAction(tr("pen..."), this, "open()",
                          new QKeySequence(tr("Ctrl+O", "File|Open")));
//! [2]
    } // method
    } // class

    }
}
