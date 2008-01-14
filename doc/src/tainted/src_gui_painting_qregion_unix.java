/*   Ported from: src.gui.painting.qregion_unix.cpp
<snip>
//! [0]
        QRegion r1(10, 10, 20, 20);
        r1.isNull();                // false
        r1.isEmpty();               // false

        QRegion r2(40, 40, 20, 20);
        QRegion r3;
        r3.isNull();                // true
        r3.isEmpty();               // true

        r3 = r1.intersected(r2);    // r3: intersection of r1 and r2
        r3.isNull();                // false
        r3.isEmpty();               // true

        r3 = r1.united(r2);         // r3: union of r1 and r2
        r3.isNull();                // false
        r3.isEmpty();               // false
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


public class src_gui_painting_qregion_unix {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QRegion r1(10, 10, 20, 20);
        r1.isNull();                // false
        r1.isEmpty();               // false

        QRegion r2(40, 40, 20, 20);
        QRegion r3;
        r3.isNull();                // true
        r3.isEmpty();               // true

        r3 = r1.intersected(r2);    // r3: intersection of r1 and r2
        r3.isNull();                // false
        r3.isEmpty();               // true

        r3 = r1.united(r2);         // r3: union of r1 and r2
        r3.isNull();                // false
        r3.isEmpty();               // false
//! [0]


    }
}
