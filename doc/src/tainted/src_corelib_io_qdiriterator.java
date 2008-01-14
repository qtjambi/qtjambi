/*   Ported from: src.corelib.io.qdiriterator.cpp
<snip>
//! [0]
        QDirIterator it("/etc", QDirIterator::Subdirectories);
        while (it.hasNext()) {
            qDebug() << it.next();

            // /etc/.
            // /etc/..
            // /etc/X11
            // /etc/X11/fs
            // ...
        }
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


public class src_corelib_io_qdiriterator {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QDirIterator it("/etc", QDirIterator.Subdirectories);
        while (it.hasNext()) {
            qDebug() << it.next();

            // /etc/.
            // /etc/..
            // /etc/X11
            // /etc/X11/fs
            // ...
        }
//! [0]


    }
}
