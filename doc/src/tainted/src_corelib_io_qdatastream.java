/*   Ported from: src.corelib.io.qdatastream.cpp
<snip>
//! [0]
        QFile file("file.dat");
        file.open(QIODevice::WriteOnly);
        QDataStream out(&file);   // we will serialize the data into the file
        out << "the answer is";   // serialize a string
        out << (qint32)42;        // serialize an integer
//! [0]


//! [1]
        QFile file("file.dat");
        file.open(QIODevice::ReadOnly);
        QDataStream in(&file);    // read the data serialized from the file
        QString str;
        qint32 a;
        in >> str >> a;           // extract "the answer is" and 42
//! [1]


//! [2]
        stream.setVersion(QDataStream::Qt_4_0);
//! [2]


//! [3]
        QFile file("file.xxx");
        file.open(QIODevice::WriteOnly);
        QDataStream out(&file);

        // Write a header with a "magic number" and a version
        out << (quint32)0xA0B0C0D0;
        out << (qint32)123;

        out.setVersion(QDataStream::Qt_4_0);

        // Write the data
        out << lots_of_interesting_data;
//! [3]


//! [4]
        QFile file("file.xxx");
        file.open(QIODevice::ReadOnly);
        QDataStream in(&file);

        // Read and check the header
        quint32 magic;
        in >> magic;
        if (magic != 0xA0B0C0D0)
            return XXX_BAD_FILE_FORMAT;

        // Read the version
        qint32 version;
        in >> version;
        if (version < 100)
            return XXX_BAD_FILE_TOO_OLD;
        if (version > 123)
            return XXX_BAD_FILE_TOO_NEW;

        if (version <= 110)
            in.setVersion(QDataStream::Qt_3_2);
        else
            in.setVersion(QDataStream::Qt_4_0);

        // Read the data
        in >> lots_of_interesting_data;
        if (version >= 120)
            in >> data_new_in_XXX_version_1_2;
        in >> other_interesting_data;
//! [4]


//! [5]
        QDataStream out(file);
        out.setVersion(QDataStream::Qt_4_0);
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


public class src_corelib_io_qdatastream {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFile file("file.dat");
        file.open(QIODevice.WriteOnly);
        QDataStream out(ile);   // we will serialize the data into the file
        out << "the answer is";   // serialize a string
        out << (qint32)42;        // serialize an integer
//! [0]


//! [1]
        QFile file("file.dat");
        file.open(QIODevice.ReadOnly);
        QDataStream in(ile);    // read the data serialized from the file
        Stringsstr;
        qint32 a;
        in >> str >> a;           // extract "the answer is" and 42
//! [1]


//! [2]
        stream.setVersion(QDataStream.Qt_4_0);
//! [2]


//! [3]
        QFile file("file.xxx");
        file.open(QIODevice.WriteOnly);
        QDataStream out(ile);

        // Write a header with a "magic number" and a version
        out << (quint32)0xA0B0C0D0;
        out << (qint32)123;

        out.setVersion(QDataStream.Qt_4_0);

        // Write the data
        out << lots_of_interesting_data;
//! [3]


//! [4]
        QFile file("file.xxx");
        file.open(QIODevice.ReadOnly);
        QDataStream in(ile);

        // Read and check the header
        quint32 magic;
        in >> magic;
        if (magic != 0xA0B0C0D0)
            return XXX_BAD_FILE_FORMAT;

        // Read the version
        qint32 version;
        in >> version;
        if (version < 100)
            return XXX_BAD_FILE_TOO_OLD;
        if (version > 123)
            return XXX_BAD_FILE_TOO_NEW;

        if (version <= 110)
            in.setVersion(QDataStream.Qt_3_2);
        else
            in.setVersion(QDataStream.Qt_4_0);

        // Read the data
        in >> lots_of_interesting_data;
        if (version >= 120)
            in >> data_new_in_XXX_version_1_2;
        in >> other_interesting_data;
//! [4]


//! [5]
        QDataStream out(file);
        out.setVersion(QDataStream.Qt_4_0);
//! [5]


    }
}
