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
public abstract class QSignalHandler7<A, B, C, D, E, F, G> {

    /**
     * Creates a new signal handler that sets up a connection from the input signal to this object.
     * @param signal The signal to connect to.
     */
    public QSignalHandler7(QSignalEmitter.Signal7<A, B, C, D, E, F, G> signal) {
        signal.connect(this, "handle(Object, Object, Object, Object, Object, Object, Object)");
    }

    /**
     * Reimplement this function to handle responses to the signal this handler is connected to..
     * @param arg1 The first signal argument.
     * @param arg2 The second signal argument.
     * @param arg3 The third signal argument.
     * @param arg4 The fourth signal argument.
     * @param arg5 The fifth signal argument.
     * @param arg6 The sixth signal argument.
     * @param arg7 The seventh signal argument.
     */
    public abstract void handle(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7);
}
