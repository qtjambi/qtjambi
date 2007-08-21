package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.sql.*;

class QSqlDatabase___ extends QSqlDatabase {

    public static String defaultConnection() {
        com.trolltech.qt.QNativePointer np = defaultConnection_private();

        String returned = "";
        int i = 0;
        while (np.byteAt(i) != 0)
            returned += (char) np.byteAt(i++);
        return returned;
    }

}// class
