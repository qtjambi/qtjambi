/*   Ported from: src.corelib.kernel.qabstractitemmodel.cpp
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_kernel_qabstractitemmodel extends QStandardItemModel{
    static void main(String args[]) {
        QApplication.initialize(args);

        src_corelib_kernel_qabstractitemmodel itemModel = new src_corelib_kernel_qabstractitemmodel();

        //! [0]
        itemModel.beginInsertRows(null, 2, 4);
        //! [0]


        //! [1]
        itemModel.beginInsertRows(null, 4, 5);
        //! [1]


        //! [2]
        itemModel.beginRemoveRows(null, 2, 3);
        //! [2]


        //! [3]
        itemModel.beginInsertColumns(null, 4, 6);
        //! [3]


        //! [4]
        itemModel.beginInsertColumns(null, 6, 8);
        //! [4]


        //! [5]
        itemModel.beginRemoveColumns(null, 4, 6);
        //! [5]
    }
}
