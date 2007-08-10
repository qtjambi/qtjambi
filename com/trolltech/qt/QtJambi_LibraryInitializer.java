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

abstract class QtJambi_LibraryInitializer
{
    static QClassPathFileEngineHandler handler;
    
    static {
        Utilities.loadSystemLibraries();
        Utilities.loadQtLibrary("QtCore");
        Utilities.loadQtLibrary("QtGui");
        Utilities.loadJambiLibrary("qtjambi");

        handler = new QClassPathFileEngineHandler();
      
        initialize();
        QThreadManager.initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(new QtJambi_LibraryShutdown()));
    }

    static void init() {}

    private static native void initialize();
}
