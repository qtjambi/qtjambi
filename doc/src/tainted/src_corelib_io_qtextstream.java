/*   Ported from: src.corelib.io.qtextstream.cpp
<snip>
//! [0]
        QFile data("output.txt");
        if (data.open(QFile::WriteOnly | QFile::Truncate)) {
            QTextStream out(&data);
            out << "Result: " << qSetFieldWidth(10) << left << 3.14 << 2.7 << endl;
            // writes "Result: 3.14      2.7       \n"
        }
//! [0]


//! [1]
        QTextStream stream(stdin);
        QString line;
        do {
            line = stream.readLine();
        } while (!line.isNull());
//! [1]


//! [2]
        QTextStream in("0x50 0x20");
        int firstNumber, secondNumber;

        in >> firstNumber;             // firstNumber == 80
        in >> dec >> secondNumber;     // secondNumber == 0

        char ch;
        in >> ch;                      // ch == 'x'
//! [2]


//! [3]
        int main(int argc, char *argv[])
        {
            // read numeric arguments (123, 0x20, 4.5...)
            for (int i = 1; i < argc; ++i) {
                  int number;
                  QTextStream in(argv[i]);
                  in >> number;
                  ...
            }
        }
//! [3]


//! [4]
        QString str;
        QTextStream in(stdin);
        in >> str;
//! [4]


//! [5]
        QString s;
        QTextStream out(&s);
        out.setFieldWidth(10);
        out.setPadChar('-');
        out << "Qt" << endl << "rocks!" << endl;
//! [5]


//! [6]
        ----Qt----
        --rocks!--
//! [6]


//! [7]
        QTextStream in(file);
        QChar ch1, ch2, ch3;
        in >> ch1 >> ch2 >> ch3;
//! [7]


//! [8]
        QTextStream out(stdout);
        out << "Qt rocks!" << endl;
//! [8]


//! [9]
        stream << '\n' << flush;
//! [9]


//! [10]
        QTextStream out(&file);
        out.setCodec("UTF-8");
//! [10]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_io_qtextstream {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFile data("output.txt");
        if (data.open(QFile.WriteOnly | QFile.Truncate)) {
            QTextStream out(ata);
            out << "Result: " << qSetFieldWidth(10) << left << 3.14 << 2.7 << endl;
            // writes "Result: 3.14      2.7       \n"
        }
//! [0]


//! [1]
        QTextStream stream(stdin);
        Stringsline;
        do {
            line = stream.readLine();
        } while (!line.isNull());
//! [1]


//! [2]
        QTextStream in("0x50 0x20");
        int firstNumber, secondNumber;

        in >> firstNumber;             // firstNumber == 80
        in >> dec >> secondNumber;     // secondNumber == 0

        char ch;
        in >> ch;                      // ch == 'x'
//! [2]


//! [3]
        int main(int argc, char rgv[])
        {
            // read numeric arguments (123, 0x20, 4.5...)
            for (int i = 1; i < argc; ++i) {
                  int number;
                  QTextStream in(argv[i]);
                  in >> number;
                  ...
            }
        }
//! [3]


//! [4]
        Stringsstr;
        QTextStream in(stdin);
        in >> str;
//! [4]


//! [5]
        Stringss;
        QTextStream out();
        out.setFieldWidth(10);
        out.setPadChar('-');
        out << "Qt" << endl << "rocks!" << endl;
//! [5]


//! [6]
        ----Qt----
        --rocks!--
//! [6]


//! [7]
        QTextStream in(file);
        QChar ch1, ch2, ch3;
        in >> ch1 >> ch2 >> ch3;
//! [7]


//! [8]
        QTextStream out(stdout);
        out << "Qt rocks!" << endl;
//! [8]


//! [9]
        stream << '\n' << flush;
//! [9]


//! [10]
        QTextStream out(ile);
        out.setCodec("UTF-8");
//! [10]


    }
}
