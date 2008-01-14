/*   Ported from: doc.src.uic.qdoc
<snip>
//! [0]
        uic [options] <uifile>
//! [0]


//! [1]
        ui_%.h: %.ui
                uic $< -o $@
//! [1]


//! [2]
        ui_foo.h: foo.ui
                uic $< -o $@
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_uic {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        uic [options] <uifile>
//! [0]


//! [1]
        ui_%.h: %.ui
                uic $< -o $@
//! [1]


//! [2]
        ui_foo.h: foo.ui
                uic $< -o $@
//! [2]


    }
}
