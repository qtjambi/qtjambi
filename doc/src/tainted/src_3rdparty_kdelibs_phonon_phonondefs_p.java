/*   Ported from: src.3rdparty.kdelibs.phonon.phonondefs_p.h
<snip>
//! [0]

     * ParentClassPrivate::setupBackendObject();
     * m_iface->setPropertyA(d->propertyA);
     * m_iface->setPropertyB(d->propertyB);
     * //! [0]


//! [1]

     * ParentClassPrivate::setupBackendObject();
     * m_iface->setPropertyA(d->propertyA);
     * m_iface->setPropertyB(d->propertyB);
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


public class src_3rdparty_kdelibs_phonon_phonondefs_p {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

     * ParentClassPrivate.setupBackendObject();
     * m_iface.setPropertyA(d.propertyA);
     * m_iface.setPropertyB(d.propertyB);
     * //! [0]


//! [1]

     * ParentClassPrivate.setupBackendObject();
     * m_iface.setPropertyA(d.propertyA);
     * m_iface.setPropertyB(d.propertyB);
     * //! [1]


    }
}
