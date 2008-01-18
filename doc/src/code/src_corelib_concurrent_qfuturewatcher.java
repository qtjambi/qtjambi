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
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import java.lang.reflect.*;

class MyClass {}

public class src_corelib_concurrent_qfuturewatcher {
    public static void main(String args[]) {
        QApplication.initialize(args);
        Method method = null;
//! [0]
    // Instantiate the objects and connect to the finished signal.
    MyClass myObject = new MyClass();
    QFutureWatcher<Integer> watcher = new QFutureWatcher<Integer>();
    watcher.finished.connect(myObject, "handleFinished()");

    // Start the computation.
    QFuture<Integer> future = QtConcurrent.run(method);
    watcher.setFuture(future);
//! [0]


    }
}
