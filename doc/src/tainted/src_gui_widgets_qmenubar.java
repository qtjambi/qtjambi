/*   Ported from: src.gui.widgets.qmenubar.cpp
<snip>
//! [0]
      menubar->addMenu(fileMenu);
//! [0]


//! [1]
        QMenuBar *menuBar = new QMenuBar(0);
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qmenubar {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
      menubar.addMenu(fileMenu);
//! [0]


//! [1]
        QMenuBar enuBar = new QMenuBar(0);
//! [1]


    }
}
