/*   Ported from: src.gui.text.qtextlayout.cpp
<snip>
//! [0]
        int leading = fontMetrics.leading();
        qreal height = 0;
        qreal widthUsed = 0;
        textLayout.beginLayout();
        while (1) {
            QTextLine line = textLayout.createLine();
            if (!line.isValid())
                break;

            line.setLineWidth(lineWidth);
            height += leading;
            line.setPosition(QPointF(0, height));
            height += line.height();
            widthUsed = qMax(widthUsed, line.naturalTextWidth());
        }
        textLayout.endLayout();
//! [0]


//! [1]
        QPainter painter(this);
        textLayout.draw(&painter, QPoint(0, 0));
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


public class src_gui_text_qtextlayout {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        int leading = fontMetrics.leading();
        double height = 0;
        double widthUsed = 0;
        textLayout.beginLayout();
        while (1) {
            QTextLine line = textLayout.createLine();
            if (!line.isValid())
                break;

            line.setLineWidth(lineWidth);
            height += leading;
            line.setPosition(QPointF(0, height));
            height += line.height();
            widthUsed = qMax(widthUsed, line.naturalTextWidth());
        }
        textLayout.endLayout();
//! [0]


//! [1]
        QPainter painter(this);
        textLayout.draw(ainter, QPoint(0, 0));
//! [1]


    }
}
