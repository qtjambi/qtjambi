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

import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.util.*;

public class ThreadedSenderAccess extends QObject implements Runnable {

    QSignalEmitter.Signal0 signal = new QSignalEmitter.Signal0();
    boolean doBlock = false;
    private int i;
    private ThreadedSenderAccess other;
    HashMap<QSignalEmitter, Integer> stuff = new HashMap<QSignalEmitter, Integer>();

    public ThreadedSenderAccess(int i) {
        this.i = i;
    }

    public void run() {
        signal.connect(this, "accessSender()");
        System.out.println("executing thread: " + Thread.currentThread().getId());

        while (1 + 1 == 2) {
            signal.emit();
        }
    }

    public void accessSender() {
        if (signalSender() != other) {
            throw new RuntimeException("Bad signalSender()");
        }

        if (doBlock)
            return;

        ThreadedSenderAccess that = new ThreadedSenderAccess(-1);
        that.doBlock = true;
        that.other = that;
        that.signal.connect(that, "accessSender()");
        that.signal.emit();
        that.dispose();

        if (signalSender() != other) {
            throw new RuntimeException("Bad signalSender()");
        }
    }

    public void stuff() {
        QSignalEmitter emitter = QSignalEmitter.signalSender();

        Integer callTimes = stuff.get(emitter);
        int ct = callTimes != null ? callTimes.intValue() : 0;
        stuff.put(emitter, ct + 1);

        if (ct % 100 == 0) {
            System.out.println("called 100 times from: " + emitter);
        }
    }

    @Override
    public String toString() { return "Accessor(" + i + ")"; }

    public static void main(String args[]) {
        QApplication.initialize(args);

        ThreadedSenderAccess me = new ThreadedSenderAccess(0);

        for (int i=0; i<10; ++i) {
            ThreadedSenderAccess access = new ThreadedSenderAccess(i+1);
            access.other = access;
            access.signal.connect(me, "stuff()");

            Thread t = new Thread(access);
            access.moveToThread(t);
            t.start();

        }

        QApplication.exec();
    }
}
