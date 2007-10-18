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

package com.trolltech.tests;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

public class Threads implements Runnable {
    static Reciever global_reciever = null;
    public QEventLoop eventLoop;

    public static class Reciever extends QWidget {
        @Override
        protected void customEvent(QEvent e) {
            System.out.println("got event in thread="
                    + Thread.currentThread().getId() + ", type=" + e.type());
        }
    }
    
    public class ThreadReceiver extends QObject {
        @Override
        public boolean event(QEvent e) {            
            if (e.type() == QEvent.Type.Quit) {
                System.out.println("ThreadReceiver should call quit()");
                eventLoop.quit();
            } else if (e.type().value() > QEvent.Type.User.value()) {
                System.out.println("ThreadReceiver: got user event: " + Thread.currentThread().getId() + ", id=" + e.type());
            }
            
            return super.event(e);
        }
    }

    public void run() {
        System.out.println("Thread: " + this + ", starting");

        QTime time = new QTime();
        time.start();

        int counter = 0;
        ThreadReceiver tr = new ThreadReceiver();

        while (time.elapsed() < 1000) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }

            QEvent e = new QEvent(QEvent.Type.resolve(QEvent.Type.User.value() + ++counter));
            QApplication.postEvent(tr, e);
        }

        QEvent e = new QEvent(QEvent.Type.Quit);
        QApplication.postEvent(tr, e);

        eventLoop = new QEventLoop();
        eventLoop.exec();
      
        System.out.println("Thread: " + this + ", completed");
        System.out.println("object.thread=" + tr.thread());
    }
    
    public static void runThreads(int count) {
        Thread threads[] = new Thread[count];
        for (int i = 0; i < count; ++i) {
            threads[i] = new Thread(new Threads());
            threads[i].start();            
        }
        
        for (int i=0; i<count; ++i)
            try { threads[i].join(); } catch (Exception e) { }
    }
    
    public static void main(String args[]) {
        QApplication.initialize(args);
        runThreads(10);
        
        System.gc();

        System.out.println("All done...");

        //QApplication.exec();
    }
};
