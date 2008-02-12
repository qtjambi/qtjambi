/*   Ported from: doc.src.examples.application.qdoc
<snip>
//! [0]
        application -style=windows
        application -style=motif
        application -style=cde
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


public class doc_src_examples_application {
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        
        /*
//! [0]
        application -style=windows
        application -style=motif
        application -style=cde
//! [0]

         */


    }
}
