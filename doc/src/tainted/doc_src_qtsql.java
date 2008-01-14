/*   Ported from: doc.src.qtsql.qdoc
<snip>
//! [0]
        #include <QtSql>
//! [0]


//! [1]
        QT += sql
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


public class doc_src_qtsql {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        #include <QtSql>
//! [0]


//! [1]
        QT += sql
//! [1]


    }
}
