/*   Ported from: src.xml.sax.qxml.cpp
<snip>
//! [0]
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
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


public class src_xml_sax_qxml {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
//! [0]


    }
}
