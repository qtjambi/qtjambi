/*   Ported from: src.corelib.tools.qstring.cpp
<snip>
//! [0]
        DEFINES += QT_NO_CAST_FROM_ASCII \
                   QT_NO_CAST_TO_ASCII
//! [0]


//! [1]
        QString url = QLatin1String("http://www.unicode.org/");
//! [1]


//! [2]
        double d = 12.34;
        QString str = QString("delta: %1").arg(d, 0, 'E', 3);
        // str == "delta: 1.234E+01"
//! [2]


//! [3]
        if (str == "auto" || str == "extern"
                || str == "static" || str == "register") {
            ...
        }
//! [3]


//! [4]
        if (str == QString("auto") || str == QString("extern")
                || str == QString("static") || str == QString("register")) {
            ...
        }
//! [4]


//! [5]
        if (str == QLatin1String("auto")
                || str == QLatin1String("extern")
                || str == QLatin1String("static")
                || str == QLatin1String("register") {
            ...
        }
//! [5]


//! [6]
        QLabel *label = new QLabel(QLatin1String("MOD"), this);
//! [6]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_tools_qstring {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        DEFINES += QT_NO_CAST_FROM_ASCII \
                   QT_NO_CAST_TO_ASCII
//! [0]


//! [1]
        Stringsurl = QLatin1String("http://www.unicode.org/");
//! [1]


//! [2]
        double d = 12.34;
        Stringsstr = QString("delta: %1").arg(d, 0, 'E', 3);
        // str == "delta: 1.234E+01"
//! [2]


//! [3]
        if (str == "auto" || str == "extern"
                || str == "static" || str == "register") {
            ...
        }
//! [3]


//! [4]
        if (str == QString("auto") || str == QString("extern")
                || str == QString("static") || str == QString("register")) {
            ...
        }
//! [4]


//! [5]
        if (str == QLatin1String("auto")
                || str == QLatin1String("extern")
                || str == QLatin1String("static")
                || str == QLatin1String("register") {
            ...
        }
//! [5]


//! [6]
        QLabel abel = new QLabel(QLatin1String("MOD"), this);
//! [6]


    }
}
