/*   Ported from: doc.src.examples.textfinder.qdoc
<snip>
//! [0]
    CONFIG      += uitools
    HEADERS     = textfinder.h
    RESOURCES   = textfinder.qrc
    SOURCES     = textfinder.cpp main.cpp
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


public class doc_src_examples_textfinder {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    CONFIG      += uitools
    HEADERS     = textfinder.h
    RESOURCES   = textfinder.qrc
    SOURCES     = textfinder.cpp main.cpp
//! [0]


    }
}
