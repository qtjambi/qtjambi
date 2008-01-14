/*   Ported from: src.corelib.io.qiodevice.cpp
<snip>
//! [0]
        QProcess gzip;
        gzip.start("gzip", QStringList() << "-c");
        if (!gzip.waitForStarted())
            return false;

        gzip.write("uncompressed data");

        QByteArray compressed;
        while (gzip.waitForReadyRead())
            compressed += gzip.readAll();
//! [0]


//! [1]
        qint64 CustomDevice::bytesAvailable() const
        {
            return buffer.size() + QIODevice::bytesAvailable();
        }
//! [1]


//! [2]
        QFile file("box.txt");
        if (file.open(QFile::ReadOnly)) {
            char buf[1024];
            qint64 lineLength = file.readLine(buf, sizeof(buf));
            if (lineLength != -1) {
                // the line is available in buf
            }
        }
//! [2]


//! [3]
        bool CustomDevice::canReadLine() const
        {
            return buffer.contains('\n') || QIODevice::canReadLine();
        }
//! [3]


//! [4]
        bool isExeFile(QFile *file)
        {
            char buf[2];
            if (file->peek(buf, sizeof(buf)) == sizeof(buf))
                return (buf[0] == 'M' && buf[1] == 'Z');
            return false;
        }
//! [4]


//! [5]
        bool isExeFile(QFile *file)
        {
            return file->peek(2) == "MZ";
        }
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_io_qiodevice {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QProcess gzip;
        gzip.start("gzip", List<String>() << "-c");
        if (!gzip.waitForStarted())
            return false;

        gzip.write("uncompressed data");

        QByteArray compressed;
        while (gzip.waitForReadyRead())
            compressed += gzip.readAll();
//! [0]


//! [1]
        qint64 CustomDevice.bytesAvailable()
        {
            return buffer.size() + QIODevice.bytesAvailable();
        }
//! [1]


//! [2]
        QFile file("box.txt");
        if (file.open(QFile.ReadOnly)) {
            char buf[1024];
            qint64 lineLength = file.readLine(buf, sizeof(buf));
            if (lineLength != -1) {
                // the line is available in buf
            }
        }
//! [2]


//! [3]
        booleansCustomDevice.canReadLine()
        {
            return buffer.contains('\n') || QIODevice.canReadLine();
        }
//! [3]


//! [4]
        booleansisExeFile(QFile ile)
        {
            char buf[2];
            if (file.peek(buf, sizeof(buf)) == sizeof(buf))
                return (buf[0] == 'M' && buf[1] == 'Z');
            return false;
        }
//! [4]


//! [5]
        booleansisExeFile(QFile ile)
        {
            return file.peek(2) == "MZ";
        }
//! [5]


    }
}
