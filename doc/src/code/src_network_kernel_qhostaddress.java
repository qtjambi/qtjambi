import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_network_kernel_qhostaddress {
    public static void main(String args[]) {
        QApplication.initialize(args);
    QHostAddress hostAddr = new QHostAddress();
//! [0]
        QIPv6Address addr = hostAddr.toIPv6Address();
        // addr contains 16 unsigned characters

        for (int i = 0; i < 16; ++i) {
            // process addr.c[i]
        }
//! [0]


    }
}
