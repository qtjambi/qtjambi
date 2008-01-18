import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_examples_simpledommodel {
    public static void main(String args[]) {
        QApplication.initialize(args);

        QFile file = new QFile();

//! [0]
    // file is an open QFile object.
    QDomDocument document = new QDomDocument();
    if (document.setContent(file) != null) {

        QDomElement documentElement = document.documentElement();
        String text = "";
        QDomNode node = documentElement.firstChild();

        while (!node.isNull()) {
            if (node.isText())
                text += node.nodeValue();
            else if (node.hasChildNodes()) {
                // Examine the node's children and read any text found.
                // ...
            }
            node = node.nextSibling();
        }
    }
//! [0]


    }
}
