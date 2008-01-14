/*   Ported from: src.gui.image.qpixmap.cpp
<snip>
//! [0]
        static const char * const start_xpm[]={
            "16 15 8 1",
            "a c #cec6bd",
        ....
//! [0]


//! [1]
        QPixmap myPixmap;
        myPixmap->setMask(myPixmap->createHeuristicMask());
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


public class src_gui_image_qpixmap {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        static char * start_xpm[]={
            "16 15 8 1",
            "a c #cec6bd",
        ....
//! [0]


//! [1]
        QPixmap myPixmap;
        myPixmap.setMask(myPixmap.createHeuristicMask());
//! [1]


    }
}
