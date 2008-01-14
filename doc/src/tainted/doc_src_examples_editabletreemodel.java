/*   Ported from: doc.src.examples.editabletreemodel.qdoc
<snip>
//! [0]
    QVariant a = model->index(0, 0, QModelIndex()).data();
//! [0]


//! [1]
    QVariant b = model->index(1, 0, QModelIndex()).data();
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


public class doc_src_examples_editabletreemodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QVariant a = model.index(0, 0, QModelIndex()).data();
//! [0]


//! [1]
    QVariant b = model.index(1, 0, QModelIndex()).data();
//! [1]


    }
}
