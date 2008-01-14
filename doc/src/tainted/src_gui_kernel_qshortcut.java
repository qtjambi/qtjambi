/*   Ported from: src.gui.kernel.qshortcut.cpp
<snip>
//! [0]
        shortcut = new QShortcut(QKeySequence(tr("Ctrl+O", "File|Open")),
                                 parent);
//! [0]


//! [1]
        setKey(0);                  // no signal emitted
        setKey(QKeySequence());     // no signal emitted
        setKey(0x3b1);              // Greek letter alpha
        setKey(Qt::Key_D);              // 'd', e.g. to delete
        setKey('q');                // 'q', e.g. to quit
        setKey(Qt::CTRL + Qt::Key_P);       // Ctrl+P, e.g. to print document
        setKey("Ctrl+P");           // Ctrl+P, e.g. to print document
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


public class src_gui_kernel_qshortcut {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        shortcut = new QShortcut(QKeySequence(tr("Ctrl+O", "File|Open")),
                                 parent);
//! [0]


//! [1]
        setKey(0);                  // no signal emitted
        setKey(QKeySequence());     // no signal emitted
        setKey(0x3b1);              // Greek letter alpha
        setKey(Qt.Key_D);              // 'd', e.g. to delete
        setKey('q');                // 'q', e.g. to quit
        setKey(Qt.CTRL + Qt.Key_P);       // Ctrl+P, e.g. to print document
        setKey("Ctrl+P");           // Ctrl+P, e.g. to print document
//! [1]


    }
}
