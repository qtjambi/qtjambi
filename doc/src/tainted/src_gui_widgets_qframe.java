/*   Ported from: src.gui.widgets.qframe.cpp
<snip>
//! [0]
    QLabel label(...);
    label.setFrameStyle(QFrame::Panel | QFrame::Raised);
    label.setLineWidth(2);

    QProgressBar pbar(...);
    label.setFrameStyle(QFrame::NoFrame);
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


public class src_gui_widgets_qframe {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QLabel label(...);
    label.setFrameStyle(QFrame.Panel | QFrame.Raised);
    label.setLineWidth(2);

    QProgressBar pbar(...);
    label.setFrameStyle(QFrame.NoFrame);
//! [0]


    }
}
