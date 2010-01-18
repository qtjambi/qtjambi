/*   Ported from: src.3rdparty.kdelibs.phonon.medianode_p.h
<snip>
//! [0]

        * Q_Q(ClassName);
        * m_iface = Factory::createClassName(this);
        * return m_iface;
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


public class src_3rdparty_kdelibs_phonon_medianode_p {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

        * Q_Q(ClassName);
        * m_iface = Factory.createClassName(this);
        * return m_iface;
        * //! [0]


    }
}
