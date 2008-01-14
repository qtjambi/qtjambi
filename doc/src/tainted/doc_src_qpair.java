/*   Ported from: doc.src.qpair.qdoc
<snip>
//! [0]
        QPair<QString, double> pair;
//! [0]


//! [1]
        pair.first = "pi";
        pair.second = 3.14159265358979323846;
//! [1]


//! [2]
        QList<QPair<int, double> > list;
        list.append(qMakePair(66, 3.14159));
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


public class doc_src_qpair {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QPair<QString, double> pair;
//! [0]


//! [1]
        pair.first = "pi";
        pair.second = 3.14159265358979323846;
//! [1]


//! [2]
        QList<QPair<int, double> > list;
        list.append(qMakePair(66, 3.14159));
//! [2]


    }
}
