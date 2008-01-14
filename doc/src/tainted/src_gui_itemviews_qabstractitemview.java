/*   Ported from: src.gui.itemviews.qabstractitemview.cpp
<snip>
//! [0]
        void MyView::resizeEvent(QResizeEvent *event) {
            horizontalScrollBar()->setRange(0, realWidth - width());
            ...
        }
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


public class src_gui_itemviews_qabstractitemview {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        void MyView.resizeEvent(QResizeEvent vent) {
            horizontalScrollBar().setRange(0, realWidth - width());
            ...
        }
//! [0]


    }
}
