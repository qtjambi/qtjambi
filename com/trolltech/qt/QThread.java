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

/**
 * The QThread class extends the java.lang.Thread class and should be used when
 * QObjects, i.e., instances of classes that inherit from QObject, are used in
 * threads. Thread may be used for all other classes that inherit from QtJambiObject.
 * <p>
 * QObjects have object affinity, i.e., they belong to a given thread, which is
 * accessed through their thread() method. It is only allowed to access a QObject
 * from the thread to which it belongs.
 * <p>
 * The QThread class was introduced to ensure that native resources are freed when
 * QObjects are garbage collected. The garbage collector thread posts an event to
 * the native QObject, which then deletes itself. Before exiting, the thread will
 * flush all events - causing all native QObjects to be deleted. 
 * <p>
 * QThread has two convenience signals: starting and finished. Started is emitted
 * just before the runnable target is invoked. Finished is emitted just before the
 * thread shuts down - after the execution of the runnable target and the flushing
 * of the event loop.
 *   
 * @See com.trolltech.qt.core.QObject#thread()
 * @See com.trolltech.qt.QThreadAffinityException
 * @See <a href="../threads.html">Threading support in Qt</a>
 *
 * @author gunnar
 */
public final class QThread extends Thread {


    /**
     * The starting signal is emitted when before the QThread invokes
     * its runnable target. The signal is emitted from the running
     * thread.
     */
    public QSignalEmitter.Signal0 starting;


    /**
     * The finished signal is emitted after the QThread has finished
     * executing its runnable target. The signal is emitted from the
     * running thread.
     */
    public QSignalEmitter.Signal0 finished;


    /**
     * Creates a new QThread with the specified invokable target
     * @param target The invokable target.
     */
    public QThread(Runnable target) {
        super(target);
        init();
    }


    /**
     * Creates a new QThread with the specified invokable target and thread group.
     * @param group The thread group.
     * @param target The target.
     */
    public QThread(ThreadGroup group, Runnable target) {
        super(group, target);
        init();
    }


    /**
     * Creates a new QThread with the specified invokable target and the given name.
     * @param target The invokable target.
     * @param name The name.
     */
    public QThread(Runnable target, String name) {
        super(target, name);
        init();
    }


    /**
     * Creates a new QThread with the specified invokable target, name and thread group.
     * @param group The Thread group
     * @param target The invokable target
     * @param name The name.
     */
    public QThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        init();
    }


    /**
     * Creates a new QThread with the specified invokable target, name, thread group and stack size.
     * @param group The Thread group
     * @param target The invokable target
     * @param name The name.
     * @param stackSize The stack size.
     */
    public QThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        init();
    }


    /**
     * Called by the thread to invoke the runnable target.
     *
     * This method emits starting before the runnable  is called and finished after
     * the runnable is called. After the runnable is called and before finished is emitted the
     * thread will flush any pending events in this thread. This will ensure cleanup of objects
     * that are deleted via disposeLater() or similar.
     *
     * @see com.trolltech.qt.core.QObject#disposeLater()
     */
    public void run() {
        starting.emit();

        super.run();

        System.gc();
        QEventLoop e = new QEventLoop();
        e.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
        e.dispose();

        finished.emit();
    }


    private class Notifier extends QSignalEmitter {
        public Signal0 starting = new Signal0();
        public Signal0 finished = new Signal0();
    }


    private void init() {
        notifier = new Notifier();
        starting = notifier.starting;
        finished = notifier.finished;
    }


    private Notifier notifier;
}
