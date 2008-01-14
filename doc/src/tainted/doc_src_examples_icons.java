/*   Ported from: doc.src.examples.icons.qdoc
<snip>
//! [0]
        if (!condition)
             qFatal("ASSERT: "condition" in file ...");
//! [0]


//! [1]
        qmake "CONFIG += debug" icons.pro
//! [1]


//! [2]
        qmake "CONFIG += release" icons.pro
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


public class doc_src_examples_icons {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        if (!condition)
             qFatal("ASSERT: "condition" in file ...");
//! [0]


//! [1]
        qmake "CONFIG += debug" icons.pro
//! [1]


//! [2]
        qmake "CONFIG += release" icons.pro
//! [2]


    }
}
