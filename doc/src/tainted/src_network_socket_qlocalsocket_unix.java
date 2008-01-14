/*   Ported from: src.network.socket.qlocalsocket_unix.cpp
<snip>
//! [0]
        socket->connectToServer("market");
        if (socket->waitForConnected(1000))
            qDebug("Connected!");
//! [0]


//! [1]
        socket->disconnectFromServer();
        if (socket->waitForDisconnected(1000))
            qDebug("Disconnected!");
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


public class src_network_socket_qlocalsocket_unix {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        socket.connectToServer("market");
        if (socket.waitForConnected(1000))
            qDebug("Connected!");
//! [0]


//! [1]
        socket.disconnectFromServer();
        if (socket.waitForDisconnected(1000))
            qDebug("Disconnected!");
//! [1]


    }
}
