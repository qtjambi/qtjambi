import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.xmlpatterns.QXmlFormatter;
import com.trolltech.qt.xmlpatterns.QXmlQuery;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_xmlpatterns_api_qxmlformatter {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
   QXmlQuery query = new QXmlQuery();
   query.setQuery("doc('index.html')/html/body/p[1]");

   QXmlFormatterPointer formatter(new QXmlFormatter(query, myOutputDevice));
   formatter.setIndentationDepth(2);
   query.evaluateToReceiver(formatter);
//! [0]


    }
}
