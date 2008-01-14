/*   Ported from: src.network.socket.qabstractsocket.cpp
<snip>
//! [0]
        socket->connectToHost("imap", 143);
        if (socket->waitForConnected(1000))
            qDebug("Connected!");
//! [0]


//! [1]
        socket->disconnectFromHost();
            if (socket->state() == QAbstractSocket::UnconnectedState || 
                socket->waitForDisconnected(1000))
                qDebug("Disconnected!");
//! [1]


//! [2]
         // This slot is connected to QAbstractSocket::readyRead()
         void SocketClass::readyReadSlot()
         {
             while (!socket.atEnd()) {
                 QByteArray data = socket.read(100);
                 ....
             }
         }
//! [2]


//! [3]
        socket->setProxy(QNetworkProxy::NoProxy);
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_socket_qabstractsocket {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        socket.connectToHost("imap", 143);
        if (socket.waitForConnected(1000))
            qDebug("Connected!");
//! [0]


//! [1]
        socket.disconnectFromHost();
            if (socket.state() == QAbstractSocket.UnconnectedState || 
                socket.waitForDisconnected(1000))
                qDebug("Disconnected!");
//! [1]


//! [2]
         // This slot is connected to QAbstractSocket.readyRead()
         void SocketClass.readyReadSlot()
         {
             while (!socket.atEnd()) {
                 QByteArray data = socket.read(100);
                 ....
             }
         }
//! [2]


//! [3]
        socket.setProxy(QNetworkProxy.NoProxy);
//! [3]


    }
}
