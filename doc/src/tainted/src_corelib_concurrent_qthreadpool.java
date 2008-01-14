/*   Ported from: src.corelib.concurrent.qthreadpool.cpp
<snip>
//! [0]
        class HelloWorldTask : public QRunnable
        {
            void run()
            {
                qDebug() << "Hello world from thread" << QThread::currentThread();
            }
        }

        HelloWorldTask *hello = new HelloWorldTask();
        // QThreadPool takes ownership and deletes 'hello' automatically
        QThreadPool::globalInstance()->start(hello);
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


public class src_corelib_concurrent_qthreadpool {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        class HelloWorldTask : public QRunnable
        {
            void run()
            {
                qDebug() << "Hello world from thread" << QThread.currentThread();
            }
        }

        HelloWorldTask ello = new HelloWorldTask();
        // QThreadPool takes ownership and deletes 'hello' automatically
        QThreadPool.globalInstance().start(hello);
//! [0]


    }
}
