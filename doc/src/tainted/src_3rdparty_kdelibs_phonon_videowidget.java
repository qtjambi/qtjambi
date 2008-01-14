/*   Ported from: src.3rdparty.kdelibs.phonon.videowidget.h
<snip>
//! [0]

     * MediaObject *media = new MediaObject(parent);
     * VideoWidget *vwidget = new VideoWidget(parent);
     * Phonon::createPath(media, vwidget);
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


public class src_3rdparty_kdelibs_phonon_videowidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

     * MediaObject edia = new MediaObject(parent);
     * VideoWidget widget = new VideoWidget(parent);
     * Phonon.createPath(media, vwidget);
     * //! [0]


    }
}
