/*   Ported from: src.corelib.codecs.qtextcodec.cpp
<snip>
//! [0]
        QByteArray encodedString = "...";
        QTextCodec *codec = QTextCodec::codecForName("KOI8-R");
        QString string = codec->toUnicode(encodedString);
//! [0]


//! [1]
        QString string = "...";
        QTextCodec *codec = QTextCodec::codecForName("KOI8-R");
        QByteArray encodedString = codec->fromUnicode(string);
//! [1]


//! [2]
        QTextCodec *codec = QTextCodec::codecForName("Shift-JIS");
        QTextDecoder *decoder = codec->makeDecoder();

        QString string;
        while (new_data_available()) {
            QByteArray chunk = get_new_data();
            string += decoder->toUnicode(chunk);
        }
//! [2]


//! [3]
        int main(int argc, char *argv[])
        {
            QApplication app(argc, argv);
            QTextCodec::setCodecForTr(QTextCodec::codecForName("eucKR"));
            ...
        }
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_codecs_qtextcodec {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QByteArray encodedString = "...";
        QTextCodec odec = QTextCodec.codecForName("KOI8-R");
        Stringsstring = codec.toUnicode(encodedString);
//! [0]


//! [1]
        Stringsstring = "...";
        QTextCodec odec = QTextCodec.codecForName("KOI8-R");
        QByteArray encodedString = codec.fromUnicode(string);
//! [1]


//! [2]
        QTextCodec odec = QTextCodec.codecForName("Shift-JIS");
        QTextDecoder ecoder = codec.makeDecoder();

        Stringsstring;
        while (new_data_available()) {
            QByteArray chunk = get_new_data();
            string += decoder.toUnicode(chunk);
        }
//! [2]


//! [3]
        int main(int argc, char rgv[])
        {
            QApplication app(argc, argv);
            QTextCodec.setCodecForTr(QTextCodec.codecForName("eucKR"));
            ...
        }
//! [3]


    }
}
