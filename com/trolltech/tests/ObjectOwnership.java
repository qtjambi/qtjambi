package com.trolltech.tests;

import com.trolltech.qt.core.*;

public class ObjectOwnership {

    public static class DyingObject extends QObject {

        public DyingObject() {
            System.out.println("created: " + id);
            setObjectName("DyingObject(" + id + ")");
        }

        public void disposed() {
            System.out.println(" -> disposed: " + id);
        }

        static int counter = 0;
        public int id = ++counter;
    }

    public static void main(String args[]) throws InterruptedException {

        QCoreApplication.initialize(args);

        space();
        System.out.println("Creating an object in scope and calling system gc...");
        oneObject();
        System.gc();

        space();
        System.out.println("Creating an object with parent in scope and calling system gc..");
        objectWithParent();
        System.gc();
        QCoreApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);

        space();
        System.out.println("Creating an object with parent, then unparent it and calling system gc..");
        objectWithUnParent();
        System.gc();
        QCoreApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);

        space();
        System.out.println("Creating an object in a thread and explicitly dispose it");
        objectInThreadExplicitDelete();
        System.gc();

        space();
        System.out.println("Creating an object in a thread and rely on gc");
        objectInThreadImplicitDelete();
        System.gc();
        QCoreApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);

        QCoreApplication.invokeLater(new Runnable() {
            public void run() {
                QCoreApplication.quit();
            }
        });
        QCoreApplication.exec();
    }

    private static void space() {
        System.out.println("\n\n");
    }

    private static void oneObject() {
        new DyingObject();
    }

    private static void objectWithParent() {
        DyingObject p = new DyingObject();
        new DyingObject().setParent(p);
    }

    private static void objectWithUnParent() {
        DyingObject p = new DyingObject();
        DyingObject o = new DyingObject();
        o.setParent(p);
        o.setParent(null);
    }

    private static void objectInThreadExplicitDelete() {
        Thread t = new Thread() {
            public void run() {
                DyingObject o = new DyingObject();
                o.dispose();
            }
        };

        t.start();
        try {
            t.join(1000);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void objectInThreadImplicitDelete() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                oneObject();
                objectWithParent();
                objectWithUnParent();
            }
        });

        t.start();
        try {
            t.join(1000);
        } catch (Exception e) { e.printStackTrace(); }
    }


}
