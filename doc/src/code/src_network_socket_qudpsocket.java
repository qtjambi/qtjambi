/*   Ported from: src.network.socket.qudpsocket.cpp
<snip>
//! [0]
        void Server::initSocket()
        {
            udpSocket = new QUdpSocket(this);
            udpSocket->bind(QHostAddress::LocalHost, 7755);

            connect(udpSocket, SIGNAL(readyRead()),
                    this, SLOT(readPendingDatagrams()));
        }

        void Server::readPendingDatagrams()
        {
            while (udpSocket->hasPendingDatagrams()) {
                QByteArray datagram;
                datagram.resize(udpSocket->pendingDatagramSize());
                QHostAddress sender;
                quint16 senderPort;

                udpSocket->readDatagram(datagram.data(), datagram.size(),
                                        &sender, &senderPort);

                processTheDatagram(datagram);
            }
        }
//! [0]


</snip>
*/
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
//! [0]
        void Server.initSocket()
        {
            udpSocket = new QUdpSocket(this);
            udpSocket.bind(QHostAddress.LocalHost, 7755);

            connect(udpSocket, SIGNAL(readyRead()),
                    this, SLOT(readPendingDatagrams()));
        }

        void Server.readPendingDatagrams()
        {
            while (udpSocket.hasPendingDatagrams()) {
                QByteArray datagram;
                datagram.resize(udpSocket.pendingDatagramSize());
                QHostAddress sender;
                quint16 senderPort;

                udpSocket.readDatagram(datagram.data(), datagram.size(),
                                        ender, enderPort);

                processTheDatagram(datagram);
            }
        }
//! [0]


    }
}
