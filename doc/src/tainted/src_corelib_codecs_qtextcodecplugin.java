/*   Ported from: src.corelib.codecs.qtextcodecplugin.cpp
<snip>
//! [0]
        QList<QByteArray> MyCodecPlugin::names() const
        {
            return QList<QByteArray> << "IBM01140" << "hp15-tw";
        }

        QTextCodec *MyCodecPlugin::createForName(const QByteArray &name)
        {
            if (name == "IBM01140") {
                return new Ibm01140Codec;
            } else if (name == "hp15-tw") {
                return new Hp15TwCodec;
            }
            return 0;
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


public class src_corelib_codecs_qtextcodecplugin {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QList<QByteArray> MyCodecPlugin.names()
        {
            return QList<QByteArray> << "IBM01140" << "hp15-tw";
        }

        QTextCodec yCodecPlugin.createForName(QByteArray ame)
        {
            if (name == "IBM01140") {
                return new Ibm01140Codec;
            } else if (name == "hp15-tw") {
                return new Hp15TwCodec;
            }
            return 0;
        }
//! [0]


    }
}
