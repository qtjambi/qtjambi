/*   Ported from: src.sql.kernel.qsqlquery.cpp
<snip>
//! [0]
        SELECT forename, surname FROM people;
//! [0]


//! [1]
    QSqlQuery q("select * from employees");
    QSqlRecord rec = q.record();

    qDebug() << "Number of columns: " << rec.count();

    int nameCol = rec.indexOf("name"); // index of the field "name"
    while (q.next())
        qDebug() << q.value(nameCol).toString(); // output all names
//! [1]


//! [2]
        QSqlQuery q;
        q.prepare("insert into myTable values (?, ?)");

        QVariantList ints;
        ints << 1 << 2 << 3 << 4;
        q.addBindValue(ints);

        QVariantList names;
        names << "Harald" << "Boris" << "Trond" << QVariant(QVariant::String);
        q.addBindValue(names);

        if (!q.execBatch())
            qDebug() << q.lastError();
//! [2]


//! [3]
        1  Harald
        2  Boris
        3  Trond
        4  NULL
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_sql_kernel_qsqlquery {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        SELECT forename, surname FROM people;
//! [0]


//! [1]
    QSqlQuery q("select * from employees");
    QSqlRecord rec = q.record();

    qDebug() << "Number of columns: " << rec.count();

    int nameCol = rec.indexOf("name"); // index of the field "name"
    while (q.next())
        qDebug() << q.value(nameCol).toString(); // output all names
//! [1]


//! [2]
        QSqlQuery q;
        q.prepare("insert into myTable values (?, ?)");

        QVariantList ints;
        ints << 1 << 2 << 3 << 4;
        q.addBindValue(ints);

        QVariantList names;
        names << "Harald" << "Boris" << "Trond" << QVariant(QVariant.String);
        q.addBindValue(names);

        if (!q.execBatch())
            qDebug() << q.lastError();
//! [2]


//! [3]
        1  Harald
        2  Boris
        3  Trond
        4  NULL
//! [3]


    }
}
