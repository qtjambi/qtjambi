/*   Ported from: src.corelib.tools.qlocale.cpp
<snip>
//! [0]
        QLocale egyptian(QLocale::Arabic, QLocale::Egypt);
        QString s1 = egyptian.toString(1.571429E+07, 'e');
        QString s2 = egyptian.toString(10);

        double d = egyptian.toDouble(s1);
        int i = egyptian.toInt(s2);
//! [0]


//! [1]
        QLocale::setDefault(QLocale(QLocale::Hebrew, QLocale::Israel));
        QLocale hebrew; // Constructs a default QLocale
        QString s1 = hebrew.toString(15714.3, 'e');

        bool ok;
        double d;

        QLocale::setDefault(QLocale::C);
        d = QString("1234,56").toDouble(&ok);   // ok == false
        d = QString("1234.56").toDouble(&ok);   // ok == true, d == 1234.56

        QLocale::setDefault(QLocale::German);
        d = QString("1234,56").toDouble(&ok);   // ok == true, d == 1234.56
        d = QString("1234.56").toDouble(&ok);   // ok == true, d == 1234.56

        QLocale::setDefault(QLocale(QLocale::English, QLocale::UnitedStates));
        str = QString("%1 %L2 %L3")
              .arg(12345).arg(12345).arg(12345, 0, 16);
        // str == "12345 12,345 3039"
//! [1]


//! [2]
        QLocale korean("ko");
        QLocale swiss("de_CH");
//! [2]


//! [3]
        bool ok;
        double d;

        QLocale c(QLocale::C);
        d = c.toDouble( "1234.56", &ok );  // ok == true, d == 1234.56
        d = c.toDouble( "1,234.56", &ok ); // ok == true, d == 1234.56
        d = c.toDouble( "1234,56", &ok );  // ok == false

        QLocale german(QLocale::German);
        d = german.toDouble( "1234,56", &ok );  // ok == true, d == 1234.56
        d = german.toDouble( "1.234,56", &ok ); // ok == true, d == 1234.56
        d = german.toDouble( "1234.56", &ok );  // ok == false

        d = german.toDouble( "1.234", &ok );    // ok == true, d == 1234.0
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_tools_qlocale {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QLocale egyptian(QLocale.Arabic, QLocale.Egypt);
        Stringss1 = egyptian.toString(1.571429E+07, 'e');
        Stringss2 = egyptian.toString(10);

        double d = egyptian.toDouble(s1);
        int i = egyptian.toInt(s2);
//! [0]


//! [1]
        QLocale.setDefault(QLocale(QLocale.Hebrew, QLocale.Israel));
        QLocale hebrew; // Constructs a default QLocale
        Stringss1 = hebrew.toString(15714.3, 'e');

        booleansok;
        double d;

        QLocale.setDefault(QLocale.C);
        d = QString("1234,56").toDouble(k);   // ok == false
        d = QString("1234.56").toDouble(k);   // ok == true, d == 1234.56

        QLocale.setDefault(QLocale.German);
        d = QString("1234,56").toDouble(k);   // ok == true, d == 1234.56
        d = QString("1234.56").toDouble(k);   // ok == true, d == 1234.56

        QLocale.setDefault(QLocale(QLocale.English, QLocale.UnitedStates));
        str = QString("%1 %L2 %L3")
              .arg(12345).arg(12345).arg(12345, 0, 16);
        // str == "12345 12,345 3039"
//! [1]


//! [2]
        QLocale korean("ko");
        QLocale swiss("de_CH");
//! [2]


//! [3]
        booleansok;
        double d;

        QLocale c(QLocale.C);
        d = c.toDouble( "1234.56", k );  // ok == true, d == 1234.56
        d = c.toDouble( "1,234.56", k ); // ok == true, d == 1234.56
        d = c.toDouble( "1234,56", k );  // ok == false

        QLocale german(QLocale.German);
        d = german.toDouble( "1234,56", k );  // ok == true, d == 1234.56
        d = german.toDouble( "1.234,56", k ); // ok == true, d == 1234.56
        d = german.toDouble( "1234.56", k );  // ok == false

        d = german.toDouble( "1.234", k );    // ok == true, d == 1234.0
//! [3]


    }
}
