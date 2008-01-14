/*   Ported from: src.corelib.thread.qmutexpool.cpp
<snip>
//! [0]
    class Number {
    public:
        Number(double n) : num (n) { }

        void setNumber(double n) { num = n; }
        double number() const { return num; }

    private:
        double num;
    };
//! [0]


//! [1]
    void calcSquare(Number *num)
    {
        QMutexLocker locker(mutexpool.get(num));
        num.setNumber(num.number() * num.number());
    }
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_thread_qmutexpool {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    class Number {
    public:
        Number(double n) : num (n) { }

        void setNumber(double n) { num = n; }
        double number() { return num; }

    private:
        double num;
    };
//! [0]


//! [1]
    void calcSquare(Number um)
    {
        QMutexLocker locker(mutexpool.get(num));
        num.setNumber(num.number() * num.number());
    }
//! [1]


    }
}
