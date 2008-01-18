/*   Ported from: src.gui.widgets.qradiobutton.cpp
<snip>
//! [0]
        QRadioButton *button = new QRadioButton("Search from the &cursor", this);
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


public class src_gui_widgets_qradiobutton extends QWidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
public void foo() {
//! [0]
        QRadioButton button = new QRadioButton(tr("Search from the &cursor"), this);
//! [0]


    }
}
