/*   Ported from: src.network.kernel.qnetworkproxy.cpp
<snip>
//! [0]
        QNetworkProxy proxy;
        proxy.setType(QNetworkProxy::Socks5Proxy);
        proxy.setHostName("proxy.example.com");
        proxy.setPort(1080);
        proxy.setUser("username");
        proxy.setPassword("password");
        QNetworkProxy::setApplicationProxy(proxy);
//! [0]


//! [1]
        serverSocket->setProxy(QNetworkProxy::NoProxy);
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


public class src_network_kernel_qnetworkproxy {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QNetworkProxy proxy;
        proxy.setType(QNetworkProxy.Socks5Proxy);
        proxy.setHostName("proxy.example.com");
        proxy.setPort(1080);
        proxy.setUser("username");
        proxy.setPassword("password");
        QNetworkProxy.setApplicationProxy(proxy);
//! [0]


//! [1]
        serverSocket.setProxy(QNetworkProxy.NoProxy);
//! [1]


    }
}
