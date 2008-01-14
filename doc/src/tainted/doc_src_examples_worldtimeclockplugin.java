/*   Ported from: doc.src.examples.worldtimeclockplugin.qdoc
<snip>
//! [0]
        target.path = $$[QT_INSTALL_PLUGINS]/designer
        INSTALLS += target
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


public class doc_src_examples_worldtimeclockplugin {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        target.path = $$[QT_INSTALL_PLUGINS]/designer
        INSTALLS += target
//! [0]


    }
}
