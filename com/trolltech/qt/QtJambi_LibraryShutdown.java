package com.trolltech.qt;

import com.trolltech.qt.core.*;

public class QtJambi_LibraryShutdown implements Runnable {
    public void run() {
        QCoreApplication app = QCoreApplication.instance();

        if (app != null) {
            Thread appThread = app.thread();
            QCoreApplication.quit();
            try {
                appThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        run_helper();
    }

    private native void run_helper();

}
