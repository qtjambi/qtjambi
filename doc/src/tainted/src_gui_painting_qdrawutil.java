/*   Ported from: src.gui.painting.qdrawutil.cpp
<snip>
//! [0]
        QFrame frame:
        frame.setFrameStyle(QFrame::HLine | QFrame::Sunken);
//! [0]


//! [1]
        QFrame frame:
        frame.setFrameStyle(QFrame::Box | QFrame::Raised);
//! [1]


//! [2]
        QFrame frame:
        frame.setFrameStyle( QFrame::Panel | QFrame::Sunken);
//! [2]


//! [3]
        QFrame frame:
        frame.setFrameStyle(QFrame::WinPanel | QFrame::Raised);
//! [3]


//! [4]
        QFrame frame:
        frame.setFrameStyle(QFrame::Box | QFrame::Plain);
//! [4]


//! [5]
        QFrame frame:
        frame.setFrameStyle(QFrame::HLine | QFrame::Sunken);
//! [5]


//! [6]
        QFrame frame:
        frame.setFrameStyle(QFrame::Box | QFrame::Raised);
//! [6]


//! [7]
        QFrame frame:
        frame.setFrameStyle( QFrame::Panel | QFrame::Sunken);
//! [7]


//! [8]
        QFrame frame:
        frame.setFrameStyle(QFrame::WinPanel | QFrame::Raised);
//! [8]


//! [9]
        QFrame frame:
        frame.setFrameStyle(QFrame::Box | QFrame::Plain);
//! [9]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_painting_qdrawutil {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFrame frame:
        frame.setFrameStyle(QFrame.HLine | QFrame.Sunken);
//! [0]


//! [1]
        QFrame frame:
        frame.setFrameStyle(QFrame.Box | QFrame.Raised);
//! [1]


//! [2]
        QFrame frame:
        frame.setFrameStyle( QFrame.Panel | QFrame.Sunken);
//! [2]


//! [3]
        QFrame frame:
        frame.setFrameStyle(QFrame.WinPanel | QFrame.Raised);
//! [3]


//! [4]
        QFrame frame:
        frame.setFrameStyle(QFrame.Box | QFrame.Plain);
//! [4]


//! [5]
        QFrame frame:
        frame.setFrameStyle(QFrame.HLine | QFrame.Sunken);
//! [5]


//! [6]
        QFrame frame:
        frame.setFrameStyle(QFrame.Box | QFrame.Raised);
//! [6]


//! [7]
        QFrame frame:
        frame.setFrameStyle( QFrame.Panel | QFrame.Sunken);
//! [7]


//! [8]
        QFrame frame:
        frame.setFrameStyle(QFrame.WinPanel | QFrame.Raised);
//! [8]


//! [9]
        QFrame frame:
        frame.setFrameStyle(QFrame.Box | QFrame.Plain);
//! [9]


    }
}
