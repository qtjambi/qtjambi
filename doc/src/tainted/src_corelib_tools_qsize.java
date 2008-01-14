/*   Ported from: src.corelib.tools.qsize.cpp
<snip>
//! [0]
        QSize t1(10, 12);
        t1.scale(60, 60, Qt::IgnoreAspectRatio);
        // t1 is (60, 60)

        QSize t2(10, 12);
        t2.scale(60, 60, Qt::KeepAspectRatio);
        // t2 is (50, 60)

        QSize t3(10, 12);
        t3.scale(60, 60, Qt::KeepAspectRatioByExpanding);
        // t3 is (60, 72)
//! [0]


//! [1]
        QSize size(100, 10);
        size.rwidth() += 20;

        // size becomes (120,10)
//! [1]


//! [2]
        QSize size(100, 10);
        size.rheight() += 5;

        // size becomes (100,15)
//! [2]


//! [3]
        QSize s( 3, 7);
        QSize r(-1, 4);
        s += r;

        // s becomes (2,11)
//! [3]


//! [4]
        QSize s( 3, 7);
        QSize r(-1, 4);
        s -= r;

        // s becomes (4,3)
//! [4]


//! [5]
        QSizeF t1(10, 12);
        t1.scale(60, 60, Qt::IgnoreAspectRatio);
        // t1 is (60, 60)

        QSizeF t2(10, 12);
        t2.scale(60, 60, Qt::KeepAspectRatio);
        // t2 is (50, 60)

        QSizeF t3(10, 12);
        t3.scale(60, 60, Qt::KeepAspectRatioByExpanding);
        // t3 is (60, 72)
//! [5]


//! [6]
        QSizeF size(100.3, 10);
        size.rwidth() += 20.5;

         // size becomes (120.8,10)
//! [6]


//! [7]
        QSizeF size(100, 10.2);
        size.rheight() += 5.5;

        // size becomes (100,15.7)
//! [7]


//! [8]
        QSizeF s( 3, 7);
        QSizeF r(-1, 4);
        s += r;

        // s becomes (2,11)
//! [8]


//! [9]
        QSizeF s( 3, 7);
        QSizeF r(-1, 4);
        s -= r;

        // s becomes (4,3)
//! [9]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_tools_qsize {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSize t1(10, 12);
        t1.scale(60, 60, Qt.IgnoreAspectRatio);
        // t1 is (60, 60)

        QSize t2(10, 12);
        t2.scale(60, 60, Qt.KeepAspectRatio);
        // t2 is (50, 60)

        QSize t3(10, 12);
        t3.scale(60, 60, Qt.KeepAspectRatioByExpanding);
        // t3 is (60, 72)
//! [0]


//! [1]
        QSize size(100, 10);
        size.rwidth() += 20;

        // size becomes (120,10)
//! [1]


//! [2]
        QSize size(100, 10);
        size.rheight() += 5;

        // size becomes (100,15)
//! [2]


//! [3]
        QSize s( 3, 7);
        QSize r(-1, 4);
        s += r;

        // s becomes (2,11)
//! [3]


//! [4]
        QSize s( 3, 7);
        QSize r(-1, 4);
        s -= r;

        // s becomes (4,3)
//! [4]


//! [5]
        QSizeF t1(10, 12);
        t1.scale(60, 60, Qt.IgnoreAspectRatio);
        // t1 is (60, 60)

        QSizeF t2(10, 12);
        t2.scale(60, 60, Qt.KeepAspectRatio);
        // t2 is (50, 60)

        QSizeF t3(10, 12);
        t3.scale(60, 60, Qt.KeepAspectRatioByExpanding);
        // t3 is (60, 72)
//! [5]


//! [6]
        QSizeF size(100.3, 10);
        size.rwidth() += 20.5;

         // size becomes (120.8,10)
//! [6]


//! [7]
        QSizeF size(100, 10.2);
        size.rheight() += 5.5;

        // size becomes (100,15.7)
//! [7]


//! [8]
        QSizeF s( 3, 7);
        QSizeF r(-1, 4);
        s += r;

        // s becomes (2,11)
//! [8]


//! [9]
        QSizeF s( 3, 7);
        QSizeF r(-1, 4);
        s -= r;

        // s becomes (4,3)
//! [9]


    }
}
