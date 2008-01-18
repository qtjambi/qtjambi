/*   Ported from: src.3rdparty.kdelibs.phonon.videowidget.h
<snip>
//! [0]

     * MediaObject *media = new MediaObject(parent);
     * VideoWidget *vwidget = new VideoWidget(parent);
     * Phonon::createPath(media, vwidget);
     * //! [0]


</snip>
*/
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;


public class src_3rdparty_kdelibs_phonon_videowidget {
    public void fooBar(QWidget parent) {
//! [0]

     MediaObject media = new MediaObject(parent);
     VideoWidget vwidget = new VideoWidget(parent);
     Phonon.createPath(media, vwidget);
//! [0]


    }
}
