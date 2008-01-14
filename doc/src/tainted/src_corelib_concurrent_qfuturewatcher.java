/*   Ported from: src.corelib.concurrent.qfuturewatcher.cpp
<snip>
//! [0]
    // Instantiate the objects and connect to the finished signal.
    MyClass myObject;
    QFutureWatcher<int> watcher;
    connect(&watcher, SIGNAL(finished()), &myObject, SLOT(handleFinished()));

    // Start the computation.
    QFuture<int> future = QtConcurrent::run(...);
    watcher.setFuture(future);
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


public class src_corelib_concurrent_qfuturewatcher {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    // Instantiate the objects and connect to the finished signal.
    MyClass myObject;
    QFutureWatcher<int> watcher;
    connect(atcher, SIGNAL(finished()), yObject, SLOT(handleFinished()));

    // Start the computation.
    QFuture<int> future = QtConcurrent.run(...);
    watcher.setFuture(future);
//! [0]


    }
}
