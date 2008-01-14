/*   Ported from: src.gui.painting.qmatrix.cpp
<snip>
//! [0]
        x' = m11*x + m21*y + dx
        y' = m22*y + m12*x + dy
//! [0]


//! [1]
        x' = m11*x + m21*y + dx
        y' = m22*y + m12*x + dy
//! [1]


//! [2]
        x' = m11*x + m21*y + dx
        y' = m22*y + m12*x + dy
//! [2]


//! [3]
        x' = m11*x + m21*y + dx
        y' = m22*y + m12*x + dy
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


public class src_gui_painting_qmatrix {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        x' = m11 + m21 + dx
        y' = m22 + m12 + dy
//! [0]


//! [1]
        x' = m11 + m21 + dx
        y' = m22 + m12 + dy
//! [1]


//! [2]
        x' = m11 + m21 + dx
        y' = m22 + m12 + dy
//! [2]


//! [3]
        x' = m11 + m21 + dx
        y' = m22 + m12 + dy
//! [3]


    }
}
