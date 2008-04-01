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
import com.trolltech.qt.phonon.*;
import java.util.*;


public class src_3rdparty_kdelibs_phonon_path {
    @SuppressWarnings("unused")
    public void fooBar() {
//! [0]

        MediaObject media = new MediaObject();
        AudioOutput output = new AudioOutput(Phonon.Category.MusicCategory);
        Path path = Phonon.createPath(media, output);
        assert(path.isValid()); // for this simple case the path should always be
                                // valid - there are unit tests to ensure it

        // insert an effect
        List<EffectDescription> effectList = BackendCapabilities.availableAudioEffects();
        if (!effectList.isEmpty()) {
            Effect effect = path.insertEffect(effectList.get(0));
        }
 //! [0]

    }
}
