/*   Ported from: doc.src.qt4-sql.qdoc
<snip>
//! [0]
        QSqlQueryModel model;
        model.setQuery("select * from person");

        QTableView view;
        view.setModel(&model);
        view.show();
//! [0]


//! [1]
        QSqlTableModel model;
        model.setTable("person");
        model.select();

        QTableView view;
        view.setModel(&model);
        view.show();
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


public class doc_src_qt4-sql {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSqlQueryModel model;
        model.setQuery("select * from person");

        QTableView view;
        view.setModel(odel);
        view.show();
//! [0]


//! [1]
        QSqlTableModel model;
        model.setTable("person");
        model.select();

        QTableView view;
        view.setModel(odel);
        view.show();
//! [1]


    }
}
