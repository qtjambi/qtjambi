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
import com.trolltech.qt.core.*;
import com.trolltech.qt.phonon.*;

public class src_3rdparty_kdelibs_phonon_mediasource {
    public static void main(String args[]) {
//! [0]
        MediaObject m = new MediaObject();
        String fileName = "/home/foo/bar.ogg";
        QUrl url = new QUrl("http://www.example.com/stream.mp3");
        QBuffer someBuffer = new QBuffer();
        m.setCurrentSource(new MediaSource(fileName));
        m.setCurrentSource(new MediaSource(url));
        m.setCurrentSource(new MediaSource(someBuffer));
        m.setCurrentSource(new MediaSource(Phonon.DiscType.Cd));
 //! [0]
    }
}
