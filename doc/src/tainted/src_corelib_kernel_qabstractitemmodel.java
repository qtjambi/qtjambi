/*   Ported from: src.corelib.kernel.qabstractitemmodel.cpp
<snip>
//! [0]
    beginInsertRows(parent, 2, 4);
//! [0]


//! [1]
    beginInsertRows(parent, 4, 5);
//! [1]


//! [2]
    beginRemoveRows(parent, 2, 3);
//! [2]


//! [3]
    beginInsertColumns(parent, 4, 6);
//! [3]


//! [4]
    beginInsertColumns(parent, 6, 8);
//! [4]


//! [5]
    beginRemoveColumns(parent, 4, 6);
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_kernel_qabstractitemmodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    beginInsertRows(parent, 2, 4);
//! [0]


//! [1]
    beginInsertRows(parent, 4, 5);
//! [1]


//! [2]
    beginRemoveRows(parent, 2, 3);
//! [2]


//! [3]
    beginInsertColumns(parent, 4, 6);
//! [3]


//! [4]
    beginInsertColumns(parent, 6, 8);
//! [4]


//! [5]
    beginRemoveColumns(parent, 4, 6);
//! [5]


    }
}
