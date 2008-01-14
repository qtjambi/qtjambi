/*   Ported from: src.gui.widgets.qcheckbox.cpp
<snip>
//! [0]
        QCheckBox *checkbox = new QCheckBox("C&ase sensitive", this);
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


public class src_gui_widgets_qcheckbox {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QCheckBox heckbox = new QCheckBox("Cse sensitive", this);
//! [0]


    }
}
