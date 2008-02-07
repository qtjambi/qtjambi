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

import com.trolltech.qt.core.*;

class QtJambi_LibraryShutdown implements Runnable {
    public void run() {
        QCoreApplication app = QCoreApplication.instance();

        if (QtJambi_LibraryInitializer.messageHandler != null)
            QMessageHandler.removeMessageHandler(QtJambi_LibraryInitializer.messageHandler);

        if (app != null) {
            Thread appThread = app.thread();
            QCoreApplication.quit();

            try {
            	if (appThread != null)
                    appThread.join(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        run_helper();
    }

    private native void run_helper();

}
