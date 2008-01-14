/*   Ported from: doc.src.qt4-styles.qdoc
<snip>
//! [0]
        const QStyleOptionFocusRect *focusRectOption =
                qstyleoption_cast<const QStyleOptionFocusRect *>(option);
        if (focusRectOption) {
            ...
        }
//! [0]


//! [1]
        void MyWidget::paintEvent(QPaintEvent *event)
        {
            QPainter painter(this);
            ...

            QStyleOptionFocusRect option(1);
            option.init(this);
            option.backgroundColor = palette().color(QPalette::Window);

            style().drawPrimitive(QStyle::PE_FrameFocusRect, &option, &painter,
                                  this);
        }
//! [1]


//! [2]
        void drawControl(ControlElement element,
                         QPainter *painter,
                         const QWidget *widget,
                         const QRect &rect,
                         const QColorGroup &colorGroup,
                         SFlags how = Style_Default,
                         const QStyleOption &option = QStyleOption::Default) const;
//! [2]


//! [3]
        void drawControl(ControlElement element,
                         const QStyleOption *option,
                         QPainter *painter,
                         const QWidget *widget = 0) const;
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_qt4-styles {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QStyleOptionFocusRect ocusRectOption =
                qstyleoption_cast<QStyleOptionFocusRect *>(option);
        if (focusRectOption) {
            ...
        }
//! [0]


//! [1]
        void MyWidget.paintEvent(QPaintEvent vent)
        {
            QPainter painter(this);
            ...

            QStyleOptionFocusRect option(1);
            option.init(this);
            option.backgroundColor = palette().color(QPalette.Window);

            style().drawPrimitive(QStyle.PE_FrameFocusRect, ption, ainter,
                                  this);
        }
//! [1]


//! [2]
        void drawControl(ControlElement element,
                         QPainter ainter,
                         QWidget idget,
                         QRect ect,
                         QColorGroup olorGroup,
                         SFlags how = Style_Default,
                         QStyleOption ption = QStyleOption.Default);
//! [2]


//! [3]
        void drawControl(ControlElement element,
                         QStyleOption ption,
                         QPainter ainter,
                         QWidget idget = 0);
//! [3]


    }
}
