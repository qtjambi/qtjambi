/*   Ported from: src.gui.graphicsview.qgraphicswidget.cpp
<snip>
//! [0]
        void MyGroupBoxWidget::initStyleOption(QStyleOption *option) const
        {
            QGraphicsWidget::initStyleOption(option);
            if (QStyleOptionGroupBox *box = qstyleoption_cast<QStyleOptionGroupBox *>(option)) {
                // Add group box specific state.
                box->flat = isFlat();
                ...
            }
        }
//! [0]


//! [1]
        setTabOrder(a, b); // a to b
        setTabOrder(b, c); // a to b to c
        setTabOrder(c, d); // a to b to c to d
//! [1]


//! [2]
        // WRONG
        setTabOrder(c, d); // c to d
        setTabOrder(a, b); // a to b AND c to d
        setTabOrder(b, c); // a to b to c, but not c to d
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


public class src_gui_graphicsview_qgraphicswidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        void MyGroupBoxWidget.initStyleOption(QStyleOption ption)
        {
            QGraphicsWidget.initStyleOption(option);
            if (QStyleOptionGroupBox ox = qstyleoption_cast<QStyleOptionGroupBox *>(option)) {
                // Add group box specific state.
                box.flat = isFlat();
                ...
            }
        }
//! [0]


//! [1]
        setTabOrder(a, b); // a to b
        setTabOrder(b, c); // a to b to c
        setTabOrder(c, d); // a to b to c to d
//! [1]


//! [2]
        // WRONG
        setTabOrder(c, d); // c to d
        setTabOrder(a, b); // a to b AND c to d
        setTabOrder(b, c); // a to b to c, but not c to d
//! [2]


    }
}
