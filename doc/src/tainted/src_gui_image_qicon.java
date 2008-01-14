/*   Ported from: src.gui.image.qicon.cpp
<snip>
//! [0]
    QToolButton *button = new QToolButton;
    button->setIcon(QIcon("open.xpm"));
//! [0]


//! [1]
   button->setIcon(QIcon());
//! [1]


//! [2]
    void MyWidget::drawIcon(QPainter *painter, QPoint pos)
    {
        QPixmap pixmap = icon.pixmap(QSize(22, 22),
                                       isEnabled() ? QIcon::Normal
                                                   : QIcon::Disabled,
                                       isOn() ? QIcon::On
                                              : QIcon::Off);
        painter->drawPixmap(pos, pixmap);
    }
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


public class src_gui_image_qicon {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QToolButton utton = new QToolButton;
    button.setIcon(QIcon("open.xpm"));
//! [0]


//! [1]
   button.setIcon(QIcon());
//! [1]


//! [2]
    void MyWidget.drawIcon(QPainter ainter, QPoint pos)
    {
        QPixmap pixmap = icon.pixmap(QSize(22, 22),
                                       isEnabled() ? QIcon.Normal
                                                   : QIcon.Disabled,
                                       isOn() ? QIcon.On
                                              : QIcon.Off);
        painter.drawPixmap(pos, pixmap);
    }
//! [2]


    }
}
