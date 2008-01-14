/*   Ported from: doc.src.qtmac-as-native.qdoc
<snip>
//! [0]
        qmake -spec macx-xcode project.pro
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


public class doc_src_qtmac-as-native {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        qmake -spec macx-xcode project.pro
//! [0]


    }
}
