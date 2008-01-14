/*   Ported from: src.corelib.xml.qxmlstream.cpp
<snip>
//! [0]
  QXmlStreamReader xml;
  ...
  while (!xml.atEnd()) {
        xml.readNext();
        ... // do processing
  }
  if (xml.hasError()) {
        ... // do error handling
  }
//! [0]


//! [1]
        writeStartElement(qualifiedName);
        writeCharacters(text);
        writeEndElement();
//! [1]


//! [2]
        writeStartElement(namespaceUri, name);
        writeCharacters(text);
        writeEndElement();
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


public class src_corelib_xml_qxmlstream {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
  QXmlStreamReader xml;
  ...
  while (!xml.atEnd()) {
        xml.readNext();
        ... // do processing
  }
  if (xml.hasError()) {
        ... // do error handling
  }
//! [0]


//! [1]
        writeStartElement(qualifiedName);
        writeCharacters(text);
        writeEndElement();
//! [1]


//! [2]
        writeStartElement(namespaceUri, name);
        writeCharacters(text);
        writeEndElement();
//! [2]


    }
}
