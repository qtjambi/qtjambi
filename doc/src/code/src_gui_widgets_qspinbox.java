/*   Ported from: src.gui.widgets.qspinbox.cpp
<snip>
//! [0]
        sb->setPrefix("$");
//! [0]


//! [1]
        sb->setSuffix(" km");
//! [1]


//! [2]
    setRange(minimum, maximum);
//! [2]


//! [3]
    setMinimum(minimum);
    setMaximum(maximum);
//! [3]


//! [4]
        spinbox->setPrefix("$");
//! [4]


//! [5]
        spinbox->setSuffix(" km");
//! [5]


//! [6]
    setRange(minimum, maximum);
//! [6]


//! [7]
    setMinimum(minimum);
    setMaximum(maximum);
//! [7]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qspinbox {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        sb.setPrefix("$");
//! [0]


//! [1]
        sb.setSuffix(" km");
//! [1]


//! [2]
    setRange(minimum, maximum);
//! [2]


//! [3]
    setMinimum(minimum);
    setMaximum(maximum);
//! [3]


//! [4]
        spinbox.setPrefix("$");
//! [4]


//! [5]
        spinbox.setSuffix(" km");
//! [5]


//! [6]
    setRange(minimum, maximum);
//! [6]


//! [7]
    setMinimum(minimum);
    setMaximum(maximum);
//! [7]


    }
}
