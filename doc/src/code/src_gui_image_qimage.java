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

        if (true)
        {
//! [0]
        QImage image = new QImage(3, 3, QImage.Format.Format_RGB32);
        int value;

        value = new QColor(189, 149, 39).rgb(); // 0xffbd9527
        image.setPixel(1, 1, value);

        value = new QColor(122, 163, 39).rgb(); // 0xff7aa327
        image.setPixel(0, 1, value);
        image.setPixel(1, 0, value);

        value = new QColor(237, 187, 51).rgb(); // 0xffedba31
        image.setPixel(2, 1, value);
//! [0]
        }

        if (true)
        {
//! [1]
        QImage image = new QImage(3, 3, QImage.Format.Format_Indexed8);
        int value;

        value = new QColor(122, 163, 39).rgb(); // 0xff7aa327
        image.setColor(0, value);

        value = new QColor(237, 187, 51).rgb(); // 0xffedba31
        image.setColor(1, value);

        value = new QColor(189, 149, 39).rgb(); // 0xffbd9527
        image.setColor(2, value);

        image.setPixel(0, 1, 0);
        image.setPixel(1, 0, 0);
        image.setPixel(1, 1, 2);
        image.setPixel(2, 1, 1);
//! [1]
        }

//! [2]
      /*  String start_xpm[] = {
            "16 15 8 1",
            "a c #cec6bd",
            .... };*/
//! [2]


    }
}
