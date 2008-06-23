import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qabstractspinbox extends QWidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }

    public void foo() {
//! [0]
        QSpinBox spinBox = new QSpinBox(this);
        spinBox.setRange(0, 100);
        spinBox.setWrapping(true);
        spinBox.setValue(100);
        spinBox.stepBy(1);
        // value is 0
//! [0]


    }
}
