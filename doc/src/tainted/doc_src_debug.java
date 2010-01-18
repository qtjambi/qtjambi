/*   Ported from: doc.src.debug.qdoc
<snip>
//! [0]
        qDebug() << "Widget" << widget << "at position" << widget->pos();
//! [0]


//! [1]
        char *alloc(int size)
        {
            Q_ASSERT(size > 0);
            char *ptr = new char[size];
            Q_CHECK_PTR(ptr);
            return ptr;
        }
//! [1]


//! [2]
        char *alloc(int size)
        {
            char *ptr;
            Q_CHECK_PTR(ptr = new char[size]);  // WRONG
            return ptr;
        }
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


public class doc_src_debug {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        qDebug() << "Widget" << widget << "at position" << widget.pos();
//! [0]


//! [1]
        char lloc(int size)
        {
            Q_ASSERT(size > 0);
            char tr = new char[size];
            Q_CHECK_PTR(ptr);
            return ptr;
        }
//! [1]


//! [2]
        char lloc(int size)
        {
            char tr;
            Q_CHECK_PTR(ptr = new char[size]);  // WRONG
            return ptr;
        }
//! [2]


    }
}
