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


    }
    class MyWidget extends QWidget
    {
        void scrollHorizontally(int i) {}
        void scrollVertically(int i) {}

//! [0]
        public void wheelEvent(QWheelEvent event)
        {
            int numDegrees = event.delta() / 8;
            int numSteps = numDegrees / 15;

            if (event.orientation().equals(Qt.Orientation.Horizontal)) {
                scrollHorizontally(numSteps);
            } else {
                scrollVertically(numSteps);
            }
            event.accept();
        }
//! [0]
    }
}
