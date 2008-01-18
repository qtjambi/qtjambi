/*   Ported from: src.corelib.thread.qreadwritelock.cpp
<snip>
//! [0]
        QReadWriteLock lock;

        void ReaderThread::run()
        {
            ...
            lock.lockForRead();
            read_file();
            lock.unlock();
            ...
        }

        void WriterThread::run()
        {
            ...
            lock.lockForWrite();
            write_file();
            lock.unlock();
            ...
        }
//! [0]


//! [1]
        QReadWriteLock lock;

        QByteArray readData()
        {
            QReadLocker locker(&lock);
            ...
            return data;
        }
//! [1]


//! [2]
        QReadWriteLock lock;

        QByteArray readData()
        {
            locker.lockForRead();
            ...
            locker.unlock();
            return data;
        }
//! [2]


//! [3]
        QReadWriteLock lock;

        void writeData(const QByteArray &data)
        {
            QWriteLocker locker(&lock);
            ...
        }
//! [3]


//! [4]
        QReadWriteLock lock;

        void writeData(const QByteArray &data)
        {
            locker.lockForWrite();
            ...
            locker.unlock();
        }
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_thread_qreadwritelock {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QReadWriteLock lock;

        void ReaderThread.run()
        {
            ...
            lock.lockForRead();
            read_file();
            lock.unlock();
            ...
        }

        void WriterThread.run()
        {
            ...
            lock.lockForWrite();
            write_file();
            lock.unlock();
            ...
        }
//! [0]


//! [1]
        QReadWriteLock lock;

        QByteArray readData()
        {
            QReadLocker locker(ock);
            ...
            return data;
        }
//! [1]


//! [2]
        QReadWriteLock lock;

        QByteArray readData()
        {
            locker.lockForRead();
            ...
            locker.unlock();
            return data;
        }
//! [2]


//! [3]
        QReadWriteLock lock;

        void writeData(QByteArray ata)
        {
            QWriteLocker locker(ock);
            ...
        }
//! [3]


//! [4]
        QReadWriteLock lock;

        void writeData(QByteArray ata)
        {
            locker.lockForWrite();
            ...
            locker.unlock();
        }
//! [4]


    }
}
