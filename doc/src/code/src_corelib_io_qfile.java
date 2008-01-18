/*   Ported from: src.corelib.io.qfile.cpp
<snip>
//! [0]
        QFile file;
        QDir::setCurrent("/tmp");
        file.setFileName("readme.txt");
        QDir::setCurrent("/home");
        file.open(QIODevice::ReadOnly);      // opens "/home/readme.txt" under Unix
//! [0]


//! [1]
        QByteArray myEncoderFunc(const QString &fileName);
//! [1]


//! [2]
        QString myDecoderFunc(const QByteArray &localFileName);
//! [2]


//! [3]
        #include <stdio.h>

        void printError(const char* msg)
        {
            QFile file;
            file.open(stderr, QIODevice::WriteOnly);
            file.write(msg, qstrlen(msg));        // write to stderr
            file.close();
        }
//! [3]


//! [4]
    CONFIG += console
//! [4]


</snip>
*/
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
        QFile file;
        QDir.setCurrent("/tmp");
        file.setFileName("readme.txt");
        QDir.setCurrent("/home");
        file.open(QIODevice.ReadOnly);      // opens "/home/readme.txt" under Unix
//! [0]


//! [1]
        QByteArray myEncoderFunc(StringsileName);
//! [1]


//! [2]
        StringsmyDecoderFunc(QByteArray ocalFileName);
//! [2]


//! [3]
        #include <stdio.h>

        void printError(char* msg)
        {
            QFile file;
            file.open(stderr, QIODevice.WriteOnly);
            file.write(msg, qstrlen(msg));        // write to stderr
            file.close();
        }
//! [3]


//! [4]
    CONFIG += console
//! [4]


    }
}
