package com.trolltech.examples.qtconcurrent;

import java.util.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class MutableInteger {
    public int value;

    public MutableInteger(int i) { value = i; }

    @Override
    public String toString() { return ((Integer)value).toString(); }
}

public class ProgressDialog implements QtConcurrent.MapFunctor<MutableInteger> {
    static final int ITERATIONS = 20;
    static final int WORK = 1000 * 1000 * 40;

    public void map(MutableInteger iteration) {
        int v = 0;
        for (int j = 0; j < WORK; ++j)
            ++v;

        System.out.println("iteration " +iteration + " in thread " + Thread.currentThread());
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        // Prepare the list.
        List<MutableInteger> list = new ArrayList<MutableInteger>();
        for (int i = 0; i < ITERATIONS; ++i)
            list.add(new MutableInteger(i));

        // Create a progress dialog.
        QProgressDialog dialog = new QProgressDialog();
        dialog.setLabelText("Progressing using " + QThreadPool.globalInstance().maxThreadCount() + " thread(s).");

        // Create a QFutureWatcher and conncect signals and slots.
        QFutureWatcherVoid futureWatcher = new QFutureWatcherVoid();
        futureWatcher.finished.connect(dialog, "reset()");
        dialog.canceled.connect(futureWatcher, "cancel()");
        futureWatcher.progressRangeChanged.connect(dialog, "setRange(int, int)");
        futureWatcher.progressValueChanged.connect(dialog, "setValue(int)");

        // Start the computation.
        futureWatcher.setFuture(QtConcurrent.map(list, new ProgressDialog()));

        // Display the dialog and start the event loop.
        dialog.exec();

        futureWatcher.waitForFinished();

        // Query the future to check if was canceled.
        if (futureWatcher.future().isCanceled())
            System.out.println("The job was canceled.");
        else
            System.out.println("The job was not canceled.");
    }
}
