/*   Ported from: doc.src.examples.svgalib.qdoc
<snip>
//! [0]
        myApplication -qws -display svgalib
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


public class doc_src_examples_svgalib {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        myApplication -qws -display svgalib
//! [0]


    }
}
