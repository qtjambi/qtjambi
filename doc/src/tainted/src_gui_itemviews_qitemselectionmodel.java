/*   Ported from: src.gui.itemviews.qitemselectionmodel.cpp
<snip>
//! [0]
    QItemSelection *selection = new QItemSelection(topLeft, bottomRight);
//! [0]


//! [1]
    QItemSelection *selection = new QItemSelection();
    ...
    selection->select(topLeft, bottomRight);
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


public class src_gui_itemviews_qitemselectionmodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QItemSelection election = new QItemSelection(topLeft, bottomRight);
//! [0]


//! [1]
    QItemSelection election = new QItemSelection();
    ...
    selection.select(topLeft, bottomRight);
//! [1]


    }
}
