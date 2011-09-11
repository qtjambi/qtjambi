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

package com.trolltech.qt;

import java.lang.ref.WeakReference;

import com.trolltech.qt.core.QMessageHandler;
import com.trolltech.qt.internal.QClassPathFileEngineHandler;

public abstract class QtJambi_LibraryInitializer
{
    static QMessageHandler messageHandler;
    // It is upto the JVM to keep a hard reference to
    static WeakReference<Thread> shutdownHookThread;

    static {
        try {
            Utilities.loadSystemLibraries();
            Utilities.loadQtLibrary("QtCore");
            Utilities.loadJambiLibrary("qtjambi");

            QClassPathFileEngineHandler.initialize();
            installMessageHandlerForExceptions(System.getProperty("com.trolltech.qt.exceptions-for-messages"));

            initialize();
            QThreadManager.initialize();

            Thread thread = new Thread(new QtJambi_LibraryShutdown());	// hard reference
            thread.setName("qtjambi-" + QtJambi_LibraryShutdown.class.getName());
            Runtime.getRuntime().addShutdownHook(thread);
            shutdownHookThread = new WeakReference<Thread>(thread);	// weak reference
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    private static void installMessageHandlerForExceptions(String config) {

        if (config != null) {
            config = config.trim().toUpperCase();
            final boolean all = config.equals("") || config.equals("ALL") || config.equals("TRUE");
            final boolean critical = config.contains("CRITICAL");
            final boolean debug = config.contains("DEBUG");
            final boolean fatal = config.contains("FATAL");
            final boolean warning = config.contains("WARNING");

            if (all || critical || debug || fatal || warning) {
                messageHandler = new QMessageHandler() {

                    @Override
                    public void critical(String message) {
                        if (critical || all)
                            throw new RuntimeException("Critical: " + message);
                        else
                            System.err.println("Critical: " + message);
                    }

                    @Override
                    public void debug(String message) {
                        if (debug || all)
                            throw new RuntimeException("Debug: " + message);
                        else
                            System.err.println("Debug: " + message);
                    }

                    @Override
                    public void fatal(String message) {
                        if (fatal || all)
                            throw new RuntimeException("Fatal: " + message);
                        else
                            System.err.println("Fatal: " + message);
                    }

                    @Override
                    public void warning(String message) {
                        if (warning || all)
                            throw new RuntimeException("Warning: " + message);
                        else
                            System.err.println("Warning: " + message);
                    }
                };
                QMessageHandler.installMessageHandler(messageHandler);
            }
        }
    }

    public static void init() {}

    private static native void initialize();
}
