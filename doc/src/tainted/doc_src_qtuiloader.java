/*   Ported from: doc.src.qtuiloader.qdoc
<snip>
//! [0]
        CONFIG += uitools
//! [0]


//! [1]
        #include <QtUiTools>
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


public class doc_src_qtuiloader {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        CONFIG += uitools
//! [0]


//! [1]
        #include <QtUiTools>
//! [1]


    }
}
