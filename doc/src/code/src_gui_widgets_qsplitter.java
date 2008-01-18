/*   Ported from: src.gui.widgets.qsplitter.cpp
<snip>
//! [0]
        QWidget *widget = splitter->widget(index);
        QSizePolicy policy = widget->sizePolicy();
        policy.setHorizontalStretch(stretch);
        policy.setVerticalStretch(stretch);
        widget->setSizePolicy(policy);
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


public class src_gui_widgets_qsplitter {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QWidget widget = splitter.widget(index);
        QSizePolicy policy = widget.sizePolicy();
        policy.setHorizontalStretch(stretch);
        policy.setVerticalStretch(stretch);
        widget.setSizePolicy(policy);
//! [0]


    }
}
