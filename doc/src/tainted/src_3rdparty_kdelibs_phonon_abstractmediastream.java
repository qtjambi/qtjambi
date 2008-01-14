/*   Ported from: src.3rdparty.kdelibs.phonon.abstractmediastream.h
<snip>
//! [0]

 * PushStream::PushStream(QObject *parent)
 *   : AbstractMediaStream(parent), m_timer(new QTimer(this))
 * {
 *   setStreamSize(getMediaStreamSize());
 *
 *   connect(m_timer, SIGNAL(timeout()), SLOT(moreData()));
 *   m_timer->setInterval(0);
 * }
 *
 * void PushStream::moreData()
 * {
 *   const QByteArray data = getMediaData();
 *   if (data.isEmpty()) {
 *     endOfData();
 *   } else {
 *     writeData(data);
 *   }
 * }
 *
 * void PushStream::needData()
 * {
 *   m_timer->start();
 *   moreData();
 * }
 *
 * void PushStream::enoughData()
 * {
 *   m_timer->stop();
 * }
 * //! [0]


//! [1]

 * PullStream::PullStream(QObject *parent)
 *   : AbstractMediaStream(parent)
 * {
 *   setStreamSize(getMediaStreamSize());
 * }
 *
 * void PullStream::needData()
 * {
 *   const QByteArray data = getMediaData();
 *   if (data.isEmpty()) {
 *     endOfData();
 *   } else {
 *     writeData(data);
 *   }
 * }
 * //! [1]


//! [2]

         * seekStream(0);
         * //! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_3rdparty_kdelibs_phonon_abstractmediastream {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

 * PushStream.PushStream(QObject arent)
 *   : AbstractMediaStream(parent), m_timer(new QTimer(this))
 * {
 *   setStreamSize(getMediaStreamSize());
 *
 *   connect(m_timer, SIGNAL(timeout()), SLOT(moreData()));
 *   m_timer.setInterval(0);
 * }
 *
 * void PushStream.moreData()
 * {
 *   QByteArray data = getMediaData();
 *   if (data.isEmpty()) {
 *     endOfData();
 *   } else {
 *     writeData(data);
 *   }
 * }
 *
 * void PushStream.needData()
 * {
 *   m_timer.start();
 *   moreData();
 * }
 *
 * void PushStream.enoughData()
 * {
 *   m_timer.stop();
 * }
 * //! [0]


//! [1]

 * PullStream.PullStream(QObject arent)
 *   : AbstractMediaStream(parent)
 * {
 *   setStreamSize(getMediaStreamSize());
 * }
 *
 * void PullStream.needData()
 * {
 *   QByteArray data = getMediaData();
 *   if (data.isEmpty()) {
 *     endOfData();
 *   } else {
 *     writeData(data);
 *   }
 * }
 * //! [1]


//! [2]

         * seekStream(0);
         * //! [2]


    }
}
