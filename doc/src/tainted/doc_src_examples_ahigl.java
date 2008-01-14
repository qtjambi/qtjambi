/*   Ported from: doc.src.examples.ahigl.qdoc
<snip>
//! [0]
        myApplication -qws -display ahigl
//! [0]


//! [1]
        myApplication -qws -display ahigl
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


public class doc_src_examples_ahigl {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        myApplication -qws -display ahigl
//! [0]


//! [1]
        myApplication -qws -display ahigl
//! [1]


    }
}
