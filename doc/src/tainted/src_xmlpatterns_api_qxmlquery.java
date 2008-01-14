/*   Ported from: src.xmlpatterns.api.qxmlquery.cpp
<snip>
//! [0]
    query.bindVariable(QXmlName(query.namePool(), localName), value);
//! [0]


//! [1]
    QByteArray myDocument;
    QBuffer buffer(&myDocument); // This is a QIODevice.
    buffer.open(QIODevice::ReadOnly);
    QXmlQuery query;
    query.bindVariable("myDocument", &buffer);
    query.setQuery("declare variable $myDocument external; doc($myDocument)");
//! [1]


//! [2]
    query.bindVariable(QXmlName(query.namePool(), localName), device);
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


public class src_xmlpatterns_api_qxmlquery {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    query.bindVariable(QXmlName(query.namePool(), localName), value);
//! [0]


//! [1]
    QByteArray myDocument;
    QBuffer buffer(yDocument); // This is a QIODevice.
    buffer.open(QIODevice.ReadOnly);
    QXmlQuery query;
    query.bindVariable("myDocument", uffer);
    query.setQuery("declare variable $myDocument external; doc($myDocument)");
//! [1]


//! [2]
    query.bindVariable(QXmlName(query.namePool(), localName), device);
//! [2]


    }
}
