import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_io_qtemporaryfile {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        {
            QTemporaryFile file = new QTemporaryFile();
            if (file.open()) {
                // file.fileName() returns the unique file name
            }

            // the QTemporaryFile destructor removes the temporary file
        }
//! [0]


    }
}
