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
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class src_corelib_concurrent_qfuture {
    public static void main(String args[]) {
        QApplication.initialize(args);
        /* const_iterator doesn't exist in Jambi
//! [0]
        QFuture<String> future = new QFuture<String>();

        QFutureIterator<String> i = new QFutureIterator<String>(future);
        while (i.hasNext())
            System.out.println(i.next());
//! [0]
     */

        {
//! [1]
        QFuture<String> future = new QFuture<String>();
        /* ... */
        
        QFutureIterator<String> i = new QFutureIterator<String>(future);
        while (i.hasNext())
            System.out.println(i.next());
//! [1]
        }


        {
        QFuture<String> future = new QFuture<String>();
//! [2]
        QFutureIterator<String> i = new QFutureIterator<String>(future);
        i.toBack();
        while (i.hasPrevious())
            System.out.println(i.previous());
//! [2]
        }


    }
}
