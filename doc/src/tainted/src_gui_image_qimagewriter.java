/*   Ported from: src.gui.image.qimagewriter.cpp
<snip>
//! [0]
        QImageWriter writer;
        writer.setFormat("png"); // same as writer.setFormat("PNG");
//! [0]


//! [1]
        QImage image("some/image.jpeg");
        QImageWriter writer("images/outimage.png", "png");
        writer.setText("Author", "John Smith");
        writer.write(image);
//! [1]


//! [2]
        QImageWriter writer(fileName);
        if (writer.supportsOption(QImageIOHandler::Description))
            writer.setText("Author", "John Smith");
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


public class src_gui_image_qimagewriter {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QImageWriter writer;
        writer.setFormat("png"); // same as writer.setFormat("PNG");
//! [0]


//! [1]
        QImage image("some/image.jpeg");
        QImageWriter writer("images/outimage.png", "png");
        writer.setText("Author", "John Smith");
        writer.write(image);
//! [1]


//! [2]
        QImageWriter writer(fileName);
        if (writer.supportsOption(QImageIOHandler.Description))
            writer.setText("Author", "John Smith");
//! [2]


    }
}
