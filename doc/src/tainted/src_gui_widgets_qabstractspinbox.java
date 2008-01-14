/*   Ported from: src.gui.widgets.qabstractspinbox.cpp
<snip>
//! [0]
        QSpinBox *spinBox = new QSpinBox(this);
        spinBox->setRange(0, 100);
        spinBox->setWrapping(true);
        spinBox->setValue(100);
        spinBox->stepBy(1);
        // value is 0
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qabstractspinbox {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSpinBox pinBox = new QSpinBox(this);
        spinBox.setRange(0, 100);
        spinBox.setWrapping(true);
        spinBox.setValue(100);
        spinBox.stepBy(1);
        // value is 0
//! [0]


    }
}
