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
import java.lang.reflect.*;
import java.util.*;

public class src_corelib_concurrent_qfuturesynchronizer {
    
    private Method anotherMethod;
    private List<String> list;
    private QtConcurrent.MapFunctor<String> mapFunctor;
    
//! [0]
    public final void someFunction()
    {
        QFutureSynchronizerVoid synchronizer = new QFutureSynchronizerVoid();
        
        /* ... */
        
        synchronizer.addFuture(QtConcurrent.run(anotherMethod));
        synchronizer.addFuture(QtConcurrent.map(list, mapFunctor));
        
        return; // QFutureSynchronizer waits for all futures to finish
    }
//! [0]

}
