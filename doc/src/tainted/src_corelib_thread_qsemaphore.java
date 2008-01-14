/*   Ported from: src.corelib.thread.qsemaphore.cpp
<snip>
//! [0]
        QSemaphore sem(5);      // sem.available() == 5

        sem.acquire(3);         // sem.available() == 2
        sem.acquire(2);         // sem.available() == 0
        sem.release(5);         // sem.available() == 5
        sem.release(5);         // sem.available() == 10

        sem.tryAcquire(1);      // sem.available() == 9, returns true
        sem.tryAcquire(250);    // sem.available() == 9, returns false
//! [0]


//! [1]
        QSemaphore sem(5);      // a semaphore that guards 5 resources
        sem.acquire(5);         // acquire all 5 resources
        sem.release(5);         // release the 5 resources
        sem.release(10);        // "create" 10 new resources
//! [1]


//! [2]
        QSemaphore sem(5);      // sem.available() == 5
        sem.tryAcquire(250);    // sem.available() == 5, returns false
        sem.tryAcquire(3);      // sem.available() == 2, returns true
//! [2]


//! [3]
        QSemaphore sem(5);            // sem.available() == 5
        sem.tryAcquire(250, 1000);    // sem.available() == 5, waits 1000 milliseconds and returns false
        sem.tryAcquire(3, 30000);     // sem.available() == 2, returns true without waiting
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


public class src_corelib_thread_qsemaphore {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QSemaphore sem(5);      // sem.available() == 5

        sem.acquire(3);         // sem.available() == 2
        sem.acquire(2);         // sem.available() == 0
        sem.release(5);         // sem.available() == 5
        sem.release(5);         // sem.available() == 10

        sem.tryAcquire(1);      // sem.available() == 9, returns true
        sem.tryAcquire(250);    // sem.available() == 9, returns false
//! [0]


//! [1]
        QSemaphore sem(5);      // a semaphore that guards 5 resources
        sem.acquire(5);         // acquire all 5 resources
        sem.release(5);         // release the 5 resources
        sem.release(10);        // "create" 10 new resources
//! [1]


//! [2]
        QSemaphore sem(5);      // sem.available() == 5
        sem.tryAcquire(250);    // sem.available() == 5, returns false
        sem.tryAcquire(3);      // sem.available() == 2, returns true
//! [2]


//! [3]
        QSemaphore sem(5);            // sem.available() == 5
        sem.tryAcquire(250, 1000);    // sem.available() == 5, waits 1000 milliseconds and returns false
        sem.tryAcquire(3, 30000);     // sem.available() == 2, returns true without waiting
//! [3]


    }
}
