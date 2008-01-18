import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qcheckbox extends QWidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }

    public void createCheckBox() {
//! [0]
        QCheckBox checkbox = new QCheckBox("C&ase sensitive", this);
//! [0]
    }
}
