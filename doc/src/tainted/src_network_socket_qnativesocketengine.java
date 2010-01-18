/*   Ported from: src.network.socket.qnativesocketengine.cpp
<snip>
//! [0]
        QNativeSocketEngine socketLayer;
        socketLayer.initialize(QAbstractSocket::TcpSocket, QAbstractSocket::IPv4Protocol);
        socketLayer.connectToHost(QHostAddress::LocalHost, 22);
        // returns false

        socketLayer.waitForWrite();
        socketLayer.connectToHost(QHostAddress::LocalHost, 22);
        // returns true
//! [0]


//! [1]
        QNativeSocketEngine socketLayer;
        socketLayer.bind(QHostAddress::Any, 4000);
        socketLayer.listen();
        if (socketLayer.waitForRead()) {
            int clientSocket = socketLayer.accept();
            // a client is connected
        }
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_socket_qnativesocketengine {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QNativeSocketEngine socketLayer;
        socketLayer.initialize(QAbstractSocket.TcpSocket, QAbstractSocket.IPv4Protocol);
        socketLayer.connectToHost(QHostAddress.LocalHost, 22);
        // returns false

        socketLayer.waitForWrite();
        socketLayer.connectToHost(QHostAddress.LocalHost, 22);
        // returns true
//! [0]


//! [1]
        QNativeSocketEngine socketLayer;
        socketLayer.bind(QHostAddress.Any, 4000);
        socketLayer.listen();
        if (socketLayer.waitForRead()) {
            int clientSocket = socketLayer.accept();
            // a client is connected
        }
//! [1]


    }
}
