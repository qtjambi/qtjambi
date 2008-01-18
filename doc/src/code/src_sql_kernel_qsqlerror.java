/*   Ported from: src.sql.kernel.qsqlerror.cpp
<snip>
//! [0]
    QSqlQueryModel model;
    model.setQuery("select * from myTable");
    if (model.lastError().isValid())
        qDebug() << model.lastError();
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


public class src_sql_kernel_qsqlerror {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QSqlQueryModel model;
    model.setQuery("select * from myTable");
    if (model.lastError().isValid())
        qDebug() << model.lastError();
//! [0]


    }
}
