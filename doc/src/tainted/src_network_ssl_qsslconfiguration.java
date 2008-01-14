/*   Ported from: src.network.ssl.qsslconfiguration.cpp
<snip>
//! [0]
        QSslConfiguration config = sslSocket.sslConfiguration();
        config.setProtocol(QSsl::TlsV1);
        sslSocket.setSslConfiguration(config);
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


public class src_network_ssl_qsslconfiguration {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSslConfiguration config = sslSocket.sslConfiguration();
        config.setProtocol(QSsl.TlsV1);
        sslSocket.setSslConfiguration(config);
//! [0]


    }
}
