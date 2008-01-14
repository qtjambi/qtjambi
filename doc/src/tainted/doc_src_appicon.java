/*   Ported from: doc.src.appicon.qdoc
<snip>
//! [0]
        IDI_ICON1               ICON    DISCARDABLE     "myappico.ico"
//! [0]


//! [1]
        RC_FILE = myapp.rc
//! [1]


//! [2]
        ICON = myapp.icns
//! [2]


//! [3]
        kde-config --path icon
//! [3]


//! [4]
        gnome-config --datadir
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_appicon {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        IDI_ICON1               ICON    DISCARDABLE     "myappico.ico"
//! [0]


//! [1]
        RC_FILE = myapp.rc
//! [1]


//! [2]
        ICON = myapp.icns
//! [2]


//! [3]
        kde-config --path icon
//! [3]


//! [4]
        gnome-config --datadir
//! [4]


    }
}
