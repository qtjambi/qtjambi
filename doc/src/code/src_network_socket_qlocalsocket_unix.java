//depot/qtjambi/main/doc/src/tainted/src_network_socket_qlocalsocket_unix.java#1 - add change 291655 (text)
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
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;


public class src_network_socket_qlocalsocket_unix {
    public static void main(String args[]) {
        QApplication.initialize(args);
        QLocalSocket socket = null;
//! [0]
        socket.connectToServer("market");
        if (socket.waitForConnected(1000))
            System.out.println("Connected!");
//! [0]


//! [1]
        socket.disconnectFromServer();
        if (socket.waitForDisconnected(1000))
            System.out.println("Disconnected!");
//! [1]


    }
}
