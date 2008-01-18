/*   Ported from: src.gui.text.qtextdocument.cpp
<snip>
//! [0]
        QString plain = "#include <QtCore>"
        QString html = Qt::escape(plain);
        // html == "#include &lt;QtCore&gt;"
//! [0]


//! [1]
    <html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head><body>...
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


public class src_gui_text_qtextdocument {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
}
