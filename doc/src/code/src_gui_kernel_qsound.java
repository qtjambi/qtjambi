/*   Ported from: src.gui.kernel.qsound.cpp
<snip>
//! [0]
        QSound::play("mysounds/bells.wav");
//! [0]


//! [1]
        QSound bells("mysounds/bells.wav");
        bells.play();
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


public class src_gui_kernel_qsound {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSound.play("mysounds/bells.wav");
//! [0]


//! [1]
        QSound bells("mysounds/bells.wav");
        bells.play();
//! [1]


    }
}
