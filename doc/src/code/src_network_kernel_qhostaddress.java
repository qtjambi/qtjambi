//depot/qtjambi/main/doc/src/tainted/src_network_kernel_qhostaddress.java#1 - add change 291655 (text)
/*   Ported from: src.network.kernel.qhostaddress.cpp
<snip>
//! [0]
        Q_IPV6ADDR addr = hostAddr.toIPv6Address();
        // addr contains 16 unsigned characters

        for (int i = 0; i < 16; ++i) {
            // process addr[i]
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


public class src_network_kernel_qhostaddress {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        Q_IPV6ADDR addr = hostAddr.toIPv6Address();
        // addr contains 16 unsigned characters

        for (int i = 0; i < 16; ++i) {
            // process addr[i]
        }
//! [0]


    }
}
