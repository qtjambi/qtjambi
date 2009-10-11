/****************************************************************************
 **
 ** Copyright (C) 1992-2009 Nokia. All rights reserved.
 **
 ** This file is part of Qt Jambi.
 **
 ** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

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

    static {
        try {
            // Make sure we load dependent libraries...
            Class.forName("com.trolltech.qt.QtJambi_LibraryInitializer");
        } catch (ClassNotFoundException e) { }
    }

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
        if (handlers != null){
            handlers.remove(handler);
            if(handlers.isEmpty()){
                removeMessageHandlerProxy();
                handlers = null;
            }
        }
    }

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
    private static native void removeMessageHandlerProxy();

    private static List<QMessageHandler> handlers;
}
