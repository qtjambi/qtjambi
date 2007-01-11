package com.trolltech.qt;

import com.trolltech.qt.core.*;

public final class QThread extends Thread {

    private class Notifier extends QSignalEmitter {
        public Signal0 starting = new Signal0();
        public Signal0 finished = new Signal0();
    }

    public QSignalEmitter.Signal0 starting;
    public QSignalEmitter.Signal0 finished;

    public QThread(Runnable target) {
        super(target);
        init();
    }

    public QThread(ThreadGroup group, Runnable target) {
        super(group, target);
        init();
    }

    public QThread(Runnable target, String name) {
        super(target, name);
        init();
    }

    public QThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        init();
    }

    public QThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        init();
    }

    private void init() {
        notifier = new Notifier();
        starting = notifier.starting;
        finished = notifier.finished;
    }

    public void run() {
        starting.emit();

        super.run();

        System.gc();
        QEventLoop e = new QEventLoop();
        e.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
        e.dispose();

        finished.emit();
    }

    private Notifier notifier;
}
