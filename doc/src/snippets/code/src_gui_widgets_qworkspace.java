import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qworkspace {
    public static void main(String args[]) {
        QApplication.initialize(args);
    {
    class MainWindow extends QMainWindow
    {
        QWorkspace workspace;
//! [0]
    public MainWindow()
    {
        workspace = new QWorkspace();
        setCentralWidget(workspace);
        // ...
    }
//! [0]
    }
    }

    }
}
