/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

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
 * Qt Jambi checks if threading affinity is violated in each member of each QObject subclass. It is
 * possible to disable this check by setting the VM runtime parameter
 * <code>com.trolltech.qt.thread-check</code> to <code>false</code>.
 *
 * @See com.trolltech.qt.core.QObject#thread()
 * @See <a href="../threads.html">Threading support in Qt</a>
 *
 * @author gunnar
 */
public class QThreadAffinityException extends RuntimeException {


    /**
     * Creates a new QThreadAffinityException with the given message, object and thread.
     * @param message Describes the affinity exception.
     * @param object The object that was accessed.
     * @param thread The thread from which the access was made.
     */
    public QThreadAffinityException(String message, QObject object, Thread thread) {
        super(message);
        this.object = object;
        this.thread = thread;
    }


    /**
     * Returns a string representation of this exception.
     * @return This exception as a string.
     */
    @Override
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
