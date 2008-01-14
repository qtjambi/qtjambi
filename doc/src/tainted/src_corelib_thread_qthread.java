/*   Ported from: src.corelib.thread.qthread.cpp
<snip>
//! [0]
        class MyThread : public QThread
        {
        public:
            void run();
        };

        void MyThread::run()
        {
            QTcpSocket socket;
            // connect QTcpSocket's signals somewhere meaningful
            ...
            socket.connectToHost(hostName, portNumber);
            exec();
        }
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


public class src_corelib_thread_qthread {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        class MyThread : public QThread
        {
        public:
            void run();
        };

        void MyThread.run()
        {
            QTcpSocket socket;
            // connect QTcpSocket's signals somewhere meaningful
            ...
            socket.connectToHost(hostName, portNumber);
            exec();
        }
//! [0]


    }
}
