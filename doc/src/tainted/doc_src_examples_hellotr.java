/*   Ported from: doc.src.examples.hellotr.qdoc
<snip>
//! [0]
        lupdate -verbose hellotr.pro
//! [0]


//! [1]
        <!DOCTYPE TS><TS>
        <context>
            <name>QPushButton</name>
            <message>
                <source>Hello world!</source>
                <translation type="unfinished"></translation>
            </message>
        </context>
        </TS>
//! [1]


//! [2]
        linguist hellotr_la.ts
//! [2]


//! [3]
        <translation type='unfinished'></translation>
//! [3]


//! [4]
        <translation>Orbis, te saluto!</translation>
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_examples_hellotr {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        lupdate -verbose hellotr.pro
//! [0]


//! [1]
        <!DOCTYPE TS><TS>
        <context>
            <name>QPushButton</name>
            <message>
                <source>Hello world!</source>
                <translation type="unfinished"></translation>
            </message>
        </context>
        </TS>
//! [1]


//! [2]
        linguist hellotr_la.ts
//! [2]


//! [3]
        <translation type='unfinished'></translation>
//! [3]


//! [4]
        <translation>Orbis, te saluto!</translation>
//! [4]


    }
}
