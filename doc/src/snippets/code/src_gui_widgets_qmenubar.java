import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qmenubar {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
public void foo(QMenuBar menubar, QMenu fileMenu) {
//! [0]
      menubar.addMenu(fileMenu);
//! [0]
}

public void foo() {
//! [1]
        QMenuBar menuBar = new QMenuBar();
//! [1]


    }
}
