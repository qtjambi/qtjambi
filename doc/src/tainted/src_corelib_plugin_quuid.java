/*   Ported from: src.corelib.plugin.quuid.cpp
<snip>
//! [0]
    // {67C8770B-44F1-410A-AB9A-F9B5446F13EE}
    QUuid IID_MyInterface(0x67c8770b, 0x44f1, 0x410a, 0xab, 0x9a, 0xf9, 0xb5, 0x44, 0x6f, 0x13, 0xee)
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


public class src_corelib_plugin_quuid {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    // {67C8770B-44F1-410A-AB9A-F9B5446F13EE}
    QUuid IID_MyInterface(0x67c8770b, 0x44f1, 0x410a, 0xab, 0x9a, 0xf9, 0xb5, 0x44, 0x6f, 0x13, 0xee)
//! [0]


    }
}
