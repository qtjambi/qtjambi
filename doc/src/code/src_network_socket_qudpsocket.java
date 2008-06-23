import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_socket_qudpsocket {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
//! [0]
    public class Server extends QObject {
    private QUdpSocket udpSocket;

        public void initSocket()
        {
            udpSocket = new QUdpSocket(this);
            udpSocket.bind(new QHostAddress(QHostAddress.SpecialAddress.LocalHost), 7755);

            udpSocket.readyRead.connect(this, "readPendingDatagrams()");
        }

        public void readPendingDatagrams()
        {
            while (udpSocket.hasPendingDatagrams()) {
                byte[] datagram = new byte[(int)udpSocket.pendingDatagramSize()];
        QUdpSocket.HostInfo senderInfo = new QUdpSocket.HostInfo();

                udpSocket.readDatagram(datagram, senderInfo);

                // process the datagram
            }
        }
    }
//! [0]

}
