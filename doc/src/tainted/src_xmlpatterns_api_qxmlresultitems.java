/*   Ported from: src.xmlpatterns.api.qxmlresultitems.cpp
<snip>
//! [0]
  QXmlQuery query;
  query.setQuery("<e/>, 1, 'two'");
  QXmlResultItems result;

  if(query.isValid())
  {
    query.evaluateToResult(&result);
    QXmlItem item(result.next())
    while(!item.isNull())
    {
        // Use item
        item = result.next();
    }

    if(result.hasError())
        return "Runtime error!";
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


public class src_xmlpatterns_api_qxmlresultitems {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
  QXmlQuery query;
  query.setQuery("<e/>, 1, 'two'");
  QXmlResultItems result;

  if(query.isValid())
  {
    query.evaluateToResult(esult);
    QXmlItem item(result.next())
    while(!item.isNull())
    {
        // Use item
        item = result.next();
    }

    if(result.hasError())
        return "Runtime error!";
  }
//! [0]


    }
}
