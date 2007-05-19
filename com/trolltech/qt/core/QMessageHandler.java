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

package com.trolltech.qt.core;

import java.util.*;

/**
 * The QMessageHandler class provides a means of receiving notifications when the C++ side
 * of Qt Jambi enters a state where it produces warnings and debug messages and similar.
 */
public abstract class QMessageHandler {

    /**
     * Implement this method to be notified about debug messages.
     */
    public abstract void debug(String message);

    /**
     * Implement this method to be notified about warnings.
     */
    public abstract void warning(String message);

    /**
     * Implement this method to be notified about critical messages
     */
    public abstract void critical(String message);

    /**
     * Implement this method to be notified about fatal messages. After receiving a fatal
     * message the application will immediatly shut down.
     */
    public abstract void fatal(String message);

    /**
     * Installs the specified message handler as a receiver for message notification.
     */
    public static void installMessageHandler(QMessageHandler handler) {
        if (handlers == null) {
            handlers = new ArrayList<QMessageHandler>();
            installMessageHandlerProxy();
        }
        handlers.add(handler);
    }

    /**
     * Removes the specified message handler as a receiver for message notification.
     */
    public static void removeMessageHandler(QMessageHandler handler) {
        if (handlers != null)
            handlers.remove(handler);
    }
    
    @SuppressWarnings("unused")
    private static boolean process(int id, String message) {
        if (handlers == null)
            return false;
        switch (id) {
            case 0: for (QMessageHandler h : handlers) h.debug(message); break;
            case 1: for (QMessageHandler h : handlers) h.warning(message); break;
            case 2: for (QMessageHandler h : handlers) h.critical(message); break;
            case 3: for (QMessageHandler h : handlers) h.fatal(message); break;
        }
        return true;
    }

    private static native void installMessageHandlerProxy();

    private static List<QMessageHandler> handlers;
}
