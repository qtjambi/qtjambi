import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_socket_qtcpserver {
    public static void main(String args[]) {
        QApplication.initialize(args);
        QTcpServer server = new QTcpServer();
//! [0]
        server.setProxy(new QNetworkProxy(QNetworkProxy.ProxyType.NoProxy, new String(), 0));
//! [0]


    }
}
