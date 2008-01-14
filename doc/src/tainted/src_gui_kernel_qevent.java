/*   Ported from: src.gui.kernel.qevent.cpp
<snip>
//! [0]
        void MyWidget::wheelEvent(QWheelEvent *event)
        {
            int numDegrees = event->delta() / 8;
            int numSteps = numDegrees / 15;

            if (event->orientation() == Qt::Horizontal) {
                scrollHorizontally(numSteps);
            } else {
                scrollVertically(numSteps);
            }
            event->accept();
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


public class src_gui_kernel_qevent {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        void MyWidget.wheelEvent(QWheelEvent vent)
        {
            int numDegrees = event.delta() / 8;
            int numSteps = numDegrees / 15;

            if (event.orientation() == Qt.Horizontal) {
                scrollHorizontally(numSteps);
            } else {
                scrollVertically(numSteps);
            }
            event.accept();
        }
//! [0]


    }
}
