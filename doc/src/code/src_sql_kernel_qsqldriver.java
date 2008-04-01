import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_sql_kernel_qsqldriver {
    public static void main(String args[]) {
        QApplication.initialize(args);
/*
//! [0]
    QSqlDatabase db = QSqlDatabase.addDatabase("QPSQL");
    QVariant v = db.driver().handle();
    if (v.isValid() && qstrcmp(v.typeName(), "sqlite3*")) {
        // v.data() returns a pointer to the handle
        sqlite3 handle = static_cast<sqlite3 **>(v.data());
        if (handle != 0) { // check that it is not NULL
            ;// ...
        }
    }
//! [0]
*/
/*
//! [1]
    if (v.typeName() == "PGconn*") {
        PGconn handle = tatic_cast<PGconn **>(v.data());
        if (handle != 0)
        ; // ...
    }

    if (v.typeName() == "MYSQL*") {
        MYSQL handle = static_cast<MYSQL **>(v.data());
        if (handle != 0)
        ; // ...
    }
//! [1]
*/

    }
}
