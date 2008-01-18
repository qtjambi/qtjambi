import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_io_qfile {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFile file = new QFile();
        QDir.setCurrent("/tmp");
        file.setFileName("readme.txt");
        QDir.setCurrent("/home");
        file.open(QIODevice.OpenModeFlag.ReadOnly);      // opens "/home/readme.txt" under Unix
//! [0]

    }
}
