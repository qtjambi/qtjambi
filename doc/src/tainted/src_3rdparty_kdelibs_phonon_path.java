/*   Ported from: src.3rdparty.kdelibs.phonon.path.h
<snip>
//! [0]

MediaObject *media = new MediaObject;
AudioOutput *output = new AudioOutput(Phonon::MusicCategory);
Path path = Phonon::createPath(media, output);
Q_ASSERT(path.isValid()); // for this simple case the path should always be
                          //valid - there are unit tests to ensure it
// insert an effect
QList<EffectDescription> effectList = BackendCapabilities::availableAudioEffects();
if (!effectList.isEmpty()) {
    Effect *effect = path.insertEffect(effectList.first());
}
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


public class src_3rdparty_kdelibs_phonon_path {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

MediaObject edia = new MediaObject;
AudioOutput utput = new AudioOutput(Phonon.MusicCategory);
Path path = Phonon.createPath(media, output);
Q_ASSERT(path.isValid()); // for this simple case the path should always be
                          //valid - there are unit tests to ensure it
// insert an effect
QList<EffectDescription> effectList = BackendCapabilities.availableAudioEffects();
if (!effectList.isEmpty()) {
    Effect ffect = path.insertEffect(effectList.first());
}
 * //! [0]


    }
}
