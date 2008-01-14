/*   Ported from: src.gui.image.qimage.cpp
<snip>
//! [0]
        QImage image(3, 3, QImage::Format_RGB32);
        QRgb value;

        value = qRgb(189, 149, 39); // 0xffbd9527
        image.setPixel(1, 1, value);

        value = qRgb(122, 163, 39); // 0xff7aa327
        image.setPixel(0, 1, value);
        image.setPixel(1, 0, value);

        value = qRgb(237, 187, 51); // 0xffedba31
        image.setPixel(2, 1, value);
//! [0]


//! [1]
        QImage image(3, 3, QImage::Format_Indexed8);
        QRgb value;

        value = qRgb(122, 163, 39); // 0xff7aa327
        image.setColor(0, value);

        value = qRgb(237, 187, 51); // 0xffedba31
        image.setColor(1, value);

        value = qRgb(189, 149, 39); // 0xffbd9527
        image.setColor(2, value);

        image.setPixel(0, 1, 0);
        image.setPixel(1, 0, 0);
        image.setPixel(1, 1, 2);
        image.setPixel(2, 1, 1);
//! [1]


//! [2]
        static const char * const start_xpm[] = {
            "16 15 8 1",
            "a c #cec6bd",
        ....
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_image_qimage {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QImage image(3, 3, QImage.Format_RGB32);
        QRgb value;

        value = qRgb(189, 149, 39); // 0xffbd9527
        image.setPixel(1, 1, value);

        value = qRgb(122, 163, 39); // 0xff7aa327
        image.setPixel(0, 1, value);
        image.setPixel(1, 0, value);

        value = qRgb(237, 187, 51); // 0xffedba31
        image.setPixel(2, 1, value);
//! [0]


//! [1]
        QImage image(3, 3, QImage.Format_Indexed8);
        QRgb value;

        value = qRgb(122, 163, 39); // 0xff7aa327
        image.setColor(0, value);

        value = qRgb(237, 187, 51); // 0xffedba31
        image.setColor(1, value);

        value = qRgb(189, 149, 39); // 0xffbd9527
        image.setColor(2, value);

        image.setPixel(0, 1, 0);
        image.setPixel(1, 0, 0);
        image.setPixel(1, 1, 2);
        image.setPixel(2, 1, 1);
//! [1]


//! [2]
        static char * start_xpm[] = {
            "16 15 8 1",
            "a c #cec6bd",
        ....
//! [2]


    }
}
