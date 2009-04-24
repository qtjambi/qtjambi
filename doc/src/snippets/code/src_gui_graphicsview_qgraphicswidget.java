import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_graphicsview_qgraphicswidget extends QGraphicsWidget {


    public int lineWidth() { return 0; }

//! [0]
        @Override
        protected void initStyleOption(QStyleOption option) {
            super.initStyleOption(option);
            if (option instanceof QStyleOptionGroupBox) {
                QStyleOptionGroupBox box = (QStyleOptionGroupBox)option;
                // Add group box specific state.
                box.setLineWidth(lineWidth());
                // ...
            }
        }
//! [0]


        QGraphicsWidget a, b, c, d;

        {
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

