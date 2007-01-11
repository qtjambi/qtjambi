/**
 * 
 */
package com.trolltech.qt;

import com.trolltech.qt.core.QObject;


/**
 * The QThreadAffinityException class is thrown when a QObject is used outside
 * its own thread.
 * 
 * Each QObject has thread affinity, a thread that it belongs to, which is
 * accessible through its thread() method. Accessing an object from outside
 * this thread is forbidden to avoid concurrency problems.
 * 
 * @See com.trolltech.qt.QObject#thread() 
 * @See <a href="../threads.html">Threading support in Qt</a>
 * 
 * @author gunnar
 */
public class QThreadAffinityException extends RuntimeException {
    
    public QThreadAffinityException(String message, QObject object, Thread thread) {
        super(message);
        this.object = object;
        this.thread = thread;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getMessage());
        s.append(", object=").append(object);
        s.append(", objectThread=").append(object.thread());
        s.append(", currentThread=").append(thread); 
        return s.toString();
    }

    private QObject object;
    private Thread thread;
    
    private static final long serialVersionUID = 1L;
}
