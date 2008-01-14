/*   Ported from: src.gui.widgets.qmenu.cpp
<snip>
//! [0]
      exec(QCursor::pos());
//! [0]


//! [1]
      exec(somewidget.mapToGlobal(QPoint(0,0)));
//! [1]


//! [2]
      exec(e->globalPos());
//! [2]


//! [3]
        exec(QCursor::pos());
//! [3]


//! [4]
        exec(somewidget.mapToGlobal(QPoint(0, 0)));
//! [4]


//! [5]
      exec(e->globalPos());
//! [5]


//! [6]
       QMenu menu;
       QAction *at = actions[0]; // Assumes actions is not empty
       foreach (QAction *a, actions)
          menu.addAction(a);
       menu.exec(pos, at);
//! [6]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qmenu {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
      exec(QCursor.pos());
//! [0]


//! [1]
      exec(somewidget.mapToGlobal(QPoint(0,0)));
//! [1]


//! [2]
      exec(e.globalPos());
//! [2]


//! [3]
        exec(QCursor.pos());
//! [3]


//! [4]
        exec(somewidget.mapToGlobal(QPoint(0, 0)));
//! [4]


//! [5]
      exec(e.globalPos());
//! [5]


//! [6]
       QMenu menu;
       QAction t = actions[0]; // Assumes actions is not empty
       for (QAction , actions)
          menu.addAction(a);
       menu.exec(pos, at);
//! [6]


    }
}
