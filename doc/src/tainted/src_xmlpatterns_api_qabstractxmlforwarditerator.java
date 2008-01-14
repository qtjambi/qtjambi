/*   Ported from: src.xmlpatterns.api.qabstractxmlforwarditerator.cpp
<snip>
//! [0]
   OutputType inputToOutputItem(const InputType &inputType) const;
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


public class src_xmlpatterns_api_qabstractxmlforwarditerator {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
   OutputType inputToOutputItem(InputType nputType);
//! [0]


    }
}
