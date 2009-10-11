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
    QUuid IID_MyInterface = new QUuid(0x67c8770b, (char) 0x44f1, (char) 0x410a, (byte) 0xab,
                      (byte) 0x9a, (byte) 0xf9, (byte) 0xb5, (byte) 0x44,
                      (byte) 0x6f, (byte) 0x13, (byte) 0xee);
//! [0]


    }
}
