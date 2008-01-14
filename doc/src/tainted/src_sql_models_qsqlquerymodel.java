/*   Ported from: src.sql.models.qsqlquerymodel.cpp
<snip>
//! [0]
    while (myModel->canFetchMore())
        myModel->fetchMore();
//! [0]


//! [1]
    QSqlQueryModel model;
    model.setQuery("select * from MyTable");
    if (model.lastError().isValid())
        qDebug() << model.lastError();
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


public class src_sql_models_qsqlquerymodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    while (myModel.canFetchMore())
        myModel.fetchMore();
//! [0]


//! [1]
    QSqlQueryModel model;
    model.setQuery("select * from MyTable");
    if (model.lastError().isValid())
        qDebug() << model.lastError();
//! [1]


    }
}
