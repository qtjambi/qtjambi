/*   Ported from: src.3rdparty.kdelibs.phonon.mediasource.h
<snip>
//! [0]

 * MediaObject m;
 * QString fileName("/home/foo/bar.ogg");
 * QUrl url("http://www.example.com/stream.mp3");
 * QBuffer *someBuffer;
 * m.setCurrentSource(fileName);
 * m.setCurrentSource(url);
 * m.setCurrentSource(someBuffer);
 * m.setCurrentSource(Phonon::Cd);
 * //! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_3rdparty_kdelibs_phonon_mediasource {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

 * MediaObject m;
 * StringsfileName("/home/foo/bar.ogg");
 * QUrl url("http://www.example.com/stream.mp3");
 * QBuffer omeBuffer;
 * m.setCurrentSource(fileName);
 * m.setCurrentSource(url);
 * m.setCurrentSource(someBuffer);
 * m.setCurrentSource(Phonon.Cd);
 * //! [0]


    }
}
