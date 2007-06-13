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

package com.trolltech.extensions.signalhandler;

import com.trolltech.qt.*;

/**
 * Signal handlers are a convenience class that provides compile time type checking of signal / slot
 * connections.
 */
public abstract class QSignalHandler1<T> {

    /**
     * Creates a new signal handler that sets up a connection from the input signal to this object.
     * @param signal The signal to connect to.
     */
    public QSignalHandler1(QSignalEmitter.Signal1<T> signal) {
        signal.connect(this, "handle(Object)");
    }

    /**
     * Reimplement this function to handle responses to the signal this handler is connected to..
     * @param arg The Signal argument
     */
    public abstract void handle(T arg);
}
