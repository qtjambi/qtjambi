/*   Ported from: src.xmlpatterns.api.qsimplexmlnodemodel.cpp
<snip>
//! [0]
  QXmlItemIteratorPointer MyTreeModel::nextFromSimpleAxis(SimpleAxis axis, const QXmlNodeModelIndex &origin) const
  {
    // Convert the QXmlNodeModelIndex to a value that is specific to what we represent.
    const MyValue value = toMyValue(ni);

    switch(axis)
    {
        case Parent:
            return toNodeIndex(value.parent());
        case FirstChild:
        case PreviousSibling:
        case NextSibling:
            // and so on
    }
  }
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


public class src_xmlpatterns_api_qsimplexmlnodemodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
  QXmlItemIteratorPointer MyTreeModel.nextFromSimpleAxis(SimpleAxis axis, QXmlNodeModelIndex rigin)
  {
    // Convert the QXmlNodeModelIndex to a value that is specific to what we represent.
    MyValue value = toMyValue(ni);

    switch(axis)
    {
        case Parent:
            return toNodeIndex(value.parent());
        case FirstChild:
        case PreviousSibling:
        case NextSibling:
            // and so on
    }
  }
//! [0]


    }
}
