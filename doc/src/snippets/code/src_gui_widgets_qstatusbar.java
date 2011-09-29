/*   Ported from: src.gui.widgets.qstatusbar.cpp
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qstatusbar extends QMainWindow{

    class MyReadWriteIndication extends QWidget {}

    public void test() {
//! [0]
        statusBar().addWidget(new MyReadWriteIndication());
//! [0]
    }

    public static void main(String args[]) {
        QApplication.initialize(args);
        src_gui_widgets_qstatusbar bar = new src_gui_widgets_qstatusbar();
        bar.show();
        QApplication.execStatic();
        QApplication.shutdown();
    }
}
