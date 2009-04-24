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

    {
    QSqlQueryModel myModel = new QSqlQueryModel();

//! [0]
    while (myModel.canFetchMore(null))
        myModel.fetchMore(null);
//! [0]
    }
    {
//! [1]
    QSqlQueryModel model = new QSqlQueryModel();
    model.setQuery("select * from MyTable");
    if (model.lastError().isValid())
        System.out.println(model.lastError());
//! [1]
    }

    }
}
