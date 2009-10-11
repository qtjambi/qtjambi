/*   Ported from: doc.src.modules.qdoc
<snip>
//! [0]
        QT -= gui
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


public class doc_src_modules {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QT -= gui
//! [0]


    }
}
