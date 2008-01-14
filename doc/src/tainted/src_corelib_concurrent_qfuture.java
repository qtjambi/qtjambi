/*   Ported from: src.corelib.concurrent.qfuture.cpp
<snip>
//! [0]
        QFuture<QString> future = ...;

        QFuture<QString>::const_iterator i;
        for (i = future.constBegin(); i != future.constEnd(); ++i)
            cout << *i << endl;
//! [0]


//! [1]
        QFuture<QString> future;
        ...
        QFutureIterator<QString> i(future);
        while (i.hasNext())
            qDebug() << i.next();
//! [1]


//! [2]
        QFutureIterator<QString> i(future);
        i.toBack();
        while (i.hasPrevious())
	    qDebug() << i.previous();
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_concurrent_qfuture {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFuture<QString> future = ...;

        QFuture<QString>.const_iterator i;
        for (i = future.constBegin(); i != future.constEnd(); ++i)
            cout <<  << endl;
//! [0]


//! [1]
        QFuture<QString> future;
        ...
        QFutureIterator<QString> i(future);
        while (i.hasNext())
            qDebug() << i.next();
//! [1]


//! [2]
        QFutureIterator<QString> i(future);
        i.toBack();
        while (i.hasPrevious())
	    qDebug() << i.previous();
//! [2]


    }
}
