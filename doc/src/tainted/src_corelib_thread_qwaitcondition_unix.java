/*   Ported from: src.corelib.thread.qwaitcondition_unix.cpp
<snip>
//! [0]
        forever {
            mutex.lock();
            keyPressed.wait(&mutex);
            do_something();
            mutex.unlock();
        }
//! [0]


//! [1]
        forever {
            getchar();
            keyPressed.wakeAll();
        }
//! [1]


//! [2]
        forever {
            mutex.lock();
            keyPressed.wait(&mutex);
            ++count;
            mutex.unlock();

            do_something();

            mutex.lock();
            --count;
            mutex.unlock();
        }
//! [2]


//! [3]
        forever {
            getchar();

            mutex.lock();
            // Sleep until there are no busy worker threads
            while (count > 0) {
                mutex.unlock();
                sleep(1);
                mutex.lock();
            }
            keyPressed.wakeAll();
            mutex.unlock();          
        }
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


public class src_corelib_thread_qwaitcondition_unix {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        forever {
            mutex.lock();
            keyPressed.wait(utex);
            do_something();
            mutex.unlock();
        }
//! [0]


//! [1]
        forever {
            getchar();
            keyPressed.wakeAll();
        }
//! [1]


//! [2]
        forever {
            mutex.lock();
            keyPressed.wait(utex);
            ++count;
            mutex.unlock();

            do_something();

            mutex.lock();
            --count;
            mutex.unlock();
        }
//! [2]


//! [3]
        forever {
            getchar();

            mutex.lock();
            // Sleep until there are no busy worker threads
            while (count > 0) {
                mutex.unlock();
                sleep(1);
                mutex.lock();
            }
            keyPressed.wakeAll();
            mutex.unlock();          
        }
//! [3]


    }
}
