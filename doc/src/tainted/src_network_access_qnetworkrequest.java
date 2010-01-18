/*   Ported from: src.network.access.qnetworkrequest.cpp
<snip>
//! [0]
      request.setRawHeader("Last-Modified", "Sun, 06 Nov 1994 08:49:37 GMT");
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


public class src_network_access_qnetworkrequest {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
      request.setRawHeader("Last-Modified", "Sun, 06 Nov 1994 08:49:37 GMT");
//! [0]


    }
}
