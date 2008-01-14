/*   Ported from: src.gui.accessible.qaccessible.cpp
<snip>
//! [0]
        QAccessibleInterface *child = 0;
        int targetChild = object->navigate(Accessible::Child, 1, &child);
        if (child) {
            // ...
            delete child;
        }
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


public class src_gui_accessible_qaccessible {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QAccessibleInterface hild = 0;
        int targetChild = object.navigate(Accessible.Child, 1, hild);
        if (child) {
            // ...
            delete child;
        }
//! [0]


    }
}
