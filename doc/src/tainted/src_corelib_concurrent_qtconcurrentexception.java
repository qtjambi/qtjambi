/*   Ported from: src.corelib.concurrent.qtconcurrentexception.cpp
<snip>
//! [0]

    class MyException : public QtConcurrent::Exception
    {
    public:
        void raise() const { throw *this; }
        Exception *clone() const { return new MyException(*this); }
    };

//! [0]


//! [1]

    try  {
        QtConcurrent::blockingMap(list, throwFunction); // throwFunction throws MyException
    } catch (MyException &e) {
        // handle exception
    }

    If you throw an exception that is not a subclass of QtConcurrent::Exception,
    the Qt Concurrent functions will throw a QtConcurrent::UnhandledException
    on the receiver thread side.

//! [1]


//! [2]

    void MyException::raise() const { throw *this; }

//! [2]


//! [3]

    MyException *MyException::clone() const { return new MyException(*this); }

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


public class src_corelib_concurrent_qtconcurrentexception {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

    class MyException : public QtConcurrent.Exception
    {
    public:
        void raise() { throw his; }
        Exception lone() { return new MyException(his); }
    };

//! [0]


//! [1]

    try  {
        QtConcurrent.blockingMap(list, throwFunction); // throwFunction throws MyException
    } catch (MyException ) {
        // handle exception
    }

    If you throw an exception that is not a subclass of QtConcurrent.Exception,
    the Qt Concurrent functions will throw a QtConcurrent.UnhandledException
    on the receiver thread side.

//! [1]


//! [2]

    void MyException.raise() { throw his; }

//! [2]


//! [3]

    MyException yException.clone() { return new MyException(his); }

//! [3]


    }
}
