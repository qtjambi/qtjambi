/*   Ported from: src.gui.widgets.qscrollarea.cpp
<snip>
//! [0]
        QLabel *imageLabel = new QLabel;
        QImage image("happyguy.png");
        imageLabel->setPixmap(QPixmap::fromImage(image));

        scrollArea = new QScrollArea;
        scrollArea->setBackgroundRole(QPalette::Dark);
        scrollArea->setWidget(imageLabel);
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


public class src_gui_widgets_qscrollarea {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QLabel mageLabel = new QLabel;
        QImage image("happyguy.png");
        imageLabel.setPixmap(QPixmap.fromImage(image));

        scrollArea = new QScrollArea;
        scrollArea.setBackgroundRole(QPalette.Dark);
        scrollArea.setWidget(imageLabel);
//! [0]


    }
}
