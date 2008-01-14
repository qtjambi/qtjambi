/*   Ported from: doc.src.qtestevent.qdoc
<snip>
//! [0]
    QTestEventList events;
    events.addKeyClick('a');
    events.addKeyClick(Qt::Key_Backspace);
    events.addDelay(200);

    QLineEdit *lineEdit = new QLineEdit(myParent);
    ...
    events.simulate(lineEdit);
    events.simulate(lineEdit);
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


public class doc_src_qtestevent {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QTestEventList events;
    events.addKeyClick('a');
    events.addKeyClick(Qt.Key_Backspace);
    events.addDelay(200);

    QLineEdit ineEdit = new QLineEdit(myParent);
    ...
    events.simulate(lineEdit);
    events.simulate(lineEdit);
//! [0]


    }
}
