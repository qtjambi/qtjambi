/*   Ported from: src.gui.graphicsview.qgraphicssceneevent.cpp
<snip>
//! [0]

    setDropAction(proposedAction());

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


public class src_gui_graphicsview_qgraphicssceneevent {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

    setDropAction(proposedAction());

//! [0]


    }
}
