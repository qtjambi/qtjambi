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
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;


public class src_3rdparty_kdelibs_phonon_videoplayer {
    public void fooBar(QWidget parentWidget) {
        QUrl url = new QUrl();
//! [0]

        VideoPlayer player = new VideoPlayer(Phonon.Category.VideoCategory, parentWidget);
        player.finished.connect(player, "disposeLater()");
        player.play(new MediaSource(url));
//! [0]


        VideoPlayer audioPlayer = player;
//! [1]

        audioPlayer.load(new MediaSource(url));
        audioPlayer.play();
//! [1]


    }
}
