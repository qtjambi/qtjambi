/*   Ported from: src.corelib.thread.qatomic.h
<snip>
//! [0]
        MyClass &MyClass:operator=(const MyClass &other)
        { qAtomicAssign(d, other.d); return *this; }
//! [0]


//! [1]
        void MyClass::detach()
        { qAtomicDetach(d); }
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


public class src_corelib_thread_qatomic {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        MyClass yClass:operator=(MyClass ther)
        { qAtomicAssign(d, other.d); return his; }
//! [0]


//! [1]
        void MyClass.detach()
        { qAtomicDetach(d); }
//! [1]


    }
}
