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

package com.trolltech.qt;

import com.trolltech.qt.core.QMessageHandler;

public abstract class QtJambi_LibraryInitializer
{
    static QMessageHandler messageHandler;

    static {
        Utilities.loadSystemLibraries();
        Utilities.loadQtLibrary("QtCore");
        Utilities.loadJambiLibrary("qtjambi");

        QClassPathFileEngineHandler.initialize();
        installMessageHandlerForExceptions(System.getProperty("com.trolltech.qt.exceptions-for-messages"));

        initialize();
        QThreadManager.initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(new QtJambi_LibraryShutdown()));
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
