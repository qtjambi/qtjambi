/*   Ported from: src.3rdparty.kdelibs.phonon.effect.h
<snip>
//! [0]

     * Path path = Phonon::createPath(...);
     * Effect *effect = new Effect(this);
     * path.insertEffect(effect);
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


public class src_3rdparty_kdelibs_phonon_effect {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

     * Path path = Phonon.createPath(...);
     * Effect ffect = new Effect(this);
     * path.insertEffect(effect);
     * //! [0]


    }
}
