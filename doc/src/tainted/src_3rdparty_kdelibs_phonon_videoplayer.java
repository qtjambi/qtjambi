/*   Ported from: src.3rdparty.kdelibs.phonon.videoplayer.h
<snip>
//! [0]

 * VideoPlayer *player = new VideoPlayer(Phonon::VideoCategory, parentWidget);
 * connect(player, SIGNAL(finished()), player, SLOT(deleteLater()));
 * player->play(url);
 * //! [0]


//! [1]

         * audioPlayer->load(url);
         * audioPlayer->play();
         * //! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_3rdparty_kdelibs_phonon_videoplayer {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

 * VideoPlayer layer = new VideoPlayer(Phonon.VideoCategory, parentWidget);
 * connect(player, SIGNAL(finished()), player, SLOT(deleteLater()));
 * player.play(url);
 * //! [0]


//! [1]

         * audioPlayer.load(url);
         * audioPlayer.play();
         * //! [1]


    }
}
