/*   Ported from: src.network.ssl.qsslsocket.cpp
<snip>
//! [0]
        QSslSocket *socket = new QSslSocket(this);
        connect(socket, SIGNAL(encrypted()), this, SLOT(ready()));

        socket->connectToHostEncrypted("imap.example.com", 993);
//! [0]


//! [1]
        void SslServer::incomingConnection(int socketDescriptor)
        {
            QSslSocket *serverSocket = new QSslSocket;
            if (serverSocket->setSocketDescriptor(socketDescriptor)) {
                connect(serverSocket, SIGNAL(encrypted()), this, SLOT(ready()));
                serverSocket->startServerEncryption();
            } else {
                delete serverSocket;
            }
        }
//! [1]


//! [2]
        QSslSocket socket;
        socket.connectToHostEncrypted("http.example.com", 443);
        if (!socket.waitForEncrypted()) {
            qDebug() << socket.errorString();
            return false;
        }

        socket.write("GET / HTTP/1.0\r\n\r\n");
        while (socket.waitForReadyRead())
            qDebug() << socket.readAll().data();
//! [2]


//! [3]
        QSslSocket socket;
        connect(&socket, SIGNAL(encrypted()), receiver, SLOT(socketEncrypted()));

        socket.connectToHostEncrypted("imap", 993);
        socket->write("1 CAPABILITY\r\n");
//! [3]


//! [4]
        QSslSocket socket;
        socket.setCiphers("DHE-RSA-AES256-SHA:DHE-DSS-AES256-SHA:AES256-SHA");
//! [4]


//! [5]
        socket->connectToHostEncrypted("imap", 993);
        if (socket->waitForEncrypted(1000))
            qDebug("Encrypted!");
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_ssl_qsslsocket {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSslSocket ocket = new QSslSocket(this);
        connect(socket, SIGNAL(encrypted()), this, SLOT(ready()));

        socket.connectToHostEncrypted("imap.example.com", 993);
//! [0]


//! [1]
        void SslServer.incomingConnection(int socketDescriptor)
        {
            QSslSocket erverSocket = new QSslSocket;
            if (serverSocket.setSocketDescriptor(socketDescriptor)) {
                connect(serverSocket, SIGNAL(encrypted()), this, SLOT(ready()));
                serverSocket.startServerEncryption();
            } else {
                delete serverSocket;
            }
        }
//! [1]


//! [2]
        QSslSocket socket;
        socket.connectToHostEncrypted("http.example.com", 443);
        if (!socket.waitForEncrypted()) {
            qDebug() << socket.errorString();
            return false;
        }

        socket.write("GET / HTTP/1.0\r\n\r\n");
        while (socket.waitForReadyRead())
            qDebug() << socket.readAll().data();
//! [2]


//! [3]
        QSslSocket socket;
        connect(ocket, SIGNAL(encrypted()), receiver, SLOT(socketEncrypted()));

        socket.connectToHostEncrypted("imap", 993);
        socket.write("1 CAPABILITY\r\n");
//! [3]


//! [4]
        QSslSocket socket;
        socket.setCiphers("DHE-RSA-AES256-SHA:DHE-DSS-AES256-SHA:AES256-SHA");
//! [4]


//! [5]
        socket.connectToHostEncrypted("imap", 993);
        if (socket.waitForEncrypted(1000))
            qDebug("Encrypted!");
//! [5]


    }
}
