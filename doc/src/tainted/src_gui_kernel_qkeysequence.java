/*   Ported from: src.gui.kernel.qkeysequence.cpp
<snip>
//! [0]
    QKeySequence(QKeySequence::Print}
    QKeySequence(tr("Ctrl+P"))
    QKeySequence(tr("Ctrl+p"))
    QKeySequence(Qt::CTRL + Qt::Key_P)
//! [0]


//! [1]
    QKeySequence(tr("Ctrl+X, Ctrl+C"))
    QKeySequence(Qt::CTRL + Qt::Key_X, Qt::CTRL + Qt::Key_C)
//! [1]


//! [2]
        QMenu *file = new QMenu(this);
        file->addAction(tr("&Open..."), this, SLOT(open()),
                          QKeySequence(tr("Ctrl+O", "File|Open")));
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qkeysequence {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QKeySequence(QKeySequence.Print}
    QKeySequence(tr("Ctrl+P"))
    QKeySequence(tr("Ctrl+p"))
    QKeySequence(Qt.CTRL + Qt.Key_P)
//! [0]


//! [1]
    QKeySequence(tr("Ctrl+X, Ctrl+C"))
    QKeySequence(Qt.CTRL + Qt.Key_X, Qt.CTRL + Qt.Key_C)
//! [1]


//! [2]
        QMenu ile = new QMenu(this);
        file.addAction(tr("pen..."), this, SLOT(open()),
                          QKeySequence(tr("Ctrl+O", "File|Open")));
//! [2]


    }
}
