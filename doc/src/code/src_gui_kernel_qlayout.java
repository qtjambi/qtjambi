/*   Ported from: src.gui.kernel.qlayout.cpp
<snip>
//! [0]
        static void paintLayout(QPainter *painter, QLayoutItem *item)
        {
            QLayout *layout = item->layout();
            if (layout) {
                for (int i = 0; i < layout->count(); ++i)
                    paintLayout(painter, layout->itemAt(i));
            }
            painter->drawRect(layout->geometry());
        }

        void MyWidget::paintEvent(QPaintEvent *)
        {
            QPainter painter(this);
            if (layout())
                paintLayout(&painter, layout());
        }
//! [0]


//! [1]
        QLayoutItem *child;
        while ((child = layout->takeAt(0)) != 0) {
            ...
            delete child;
        }
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qlayout {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        static void paintLayout(QPainter ainter, QLayoutItem tem)
        {
            QLayout ayout = item.layout();
            if (layout) {
                for (int i = 0; i < layout.count(); ++i)
                    paintLayout(painter, layout.itemAt(i));
            }
            painter.drawRect(layout.geometry());
        }

        void MyWidget.paintEvent(QPaintEvent *)
        {
            QPainter painter(this);
            if (layout())
                paintLayout(ainter, layout());
        }
//! [0]


//! [1]
        QLayoutItem hild;
        while ((child = layout.takeAt(0)) != 0) {
            ...
            delete child;
        }
//! [1]


    }
}
