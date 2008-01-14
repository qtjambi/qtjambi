/*   Ported from: src.corelib.concurrent.qfuturesynchronizer.cpp
<snip>
//! [0]
    void someFunction()
    {
        QFutureSynchronizer<void> synchronizer;
        
        ...
        
        synchronizer.addFuture(QtConcurrent::run(anotherFunction));
        synchronizer.addFuture(QtConcurrent::map(list, mapFunction));
        
        return; // QFutureSynchronizer waits for all futures to finish
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


public class src_corelib_concurrent_qfuturesynchronizer {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    void someFunction()
    {
        QFutureSynchronizer<void> synchronizer;
        
        ...
        
        synchronizer.addFuture(QtConcurrent.run(anotherFunction));
        synchronizer.addFuture(QtConcurrent.map(list, mapFunction));
        
        return; // QFutureSynchronizer waits for all futures to finish
    }
//! [0]


    }
}
