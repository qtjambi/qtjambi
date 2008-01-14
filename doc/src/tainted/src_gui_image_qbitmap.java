/*   Ported from: src.gui.image.qbitmap.cpp
<snip>
//! [0]
        uchar arrow_bits[] = { 0x3f, 0x1f, 0x0f, 0x1f, 0x3b, 0x71, 0xe0, 0xc0 };
        QBitmap bm(8, 8, arrow_bits, true);
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


public class src_gui_image_qbitmap {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        uchar arrow_bits[] = { 0x3f, 0x1f, 0x0f, 0x1f, 0x3b, 0x71, 0xe0, 0xc0 };
        QBitmap bm(8, 8, arrow_bits, true);
//! [0]


    }
}
