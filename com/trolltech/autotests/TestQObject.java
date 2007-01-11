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

package com.trolltech.autotests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.*;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class TestQObject extends QApplicationTest{

    private static class TestObject extends QObject {
        public TestObject(QObject parent) {
            super(parent);
        }
    }

    private QObject root;
    private QFile file1, file2;
    private TestObject test1, test2, child11, child12, child21, child22;
    
    @Before
    public void setUp() {
        root = new QObject();
        root.setObjectName("root");

        file1 = new QFile(root);
        file1.setObjectName("file1");

        file2 = new QFile(root);
        file2.setObjectName("file2");

        test1 = new TestObject(root);
        test1.setObjectName("test1");

        test2 = new TestObject(root);
        test2.setObjectName("test2");

        child11 = new TestObject(test1);
        child11.setObjectName("child11");

        child12 = new TestObject(test1);
        child12.setObjectName("child");

        child21 = new TestObject(test2);
        child21.setObjectName("child21");

        child22 = new TestObject(test2);
        child22.setObjectName("child");
    }

    @Test
    public void run_findChildren() {
        {
            List<QObject> c = root.findChildren();
            assertEquals(c.size(), 8);

            assertTrue(c.contains(file1));
            assertTrue(c.contains(file2));
            assertTrue(c.contains(test1));
            assertTrue(c.contains(test2));
            assertTrue(c.contains(child11));
            assertTrue(c.contains(child12));
            assertTrue(c.contains(child21));
            assertTrue(c.contains(child22));
        }

        {
            List<QObject> c = root.findChildren(QFile.class);
            assertEquals(c.size(), 2);

            assertTrue(c.contains(file1));
            assertTrue(c.contains(file2));
        }

        {
            List<QObject> c = root.findChildren(null, "child");

            assertEquals(c.size(), 2);
            assertTrue(c.contains(child12));
            assertTrue(c.contains(child22));
        }

        {
            List<QObject> c = root.findChildren(null, new QRegExp("child"));

            assertEquals(c.size(), 4);
            assertTrue(c.contains(child11));
            assertTrue(c.contains(child12));
            assertTrue(c.contains(child21));
            assertTrue(c.contains(child22));
        }
    }

    @Test
    public void run_findChild() {
        assertEquals(root.findChild(QFile.class, "file1"), file1);
        assertNotNull(root.findChild(QFile.class));
        assertNotNull(root.findChild());
    }

    public static class DyingObject extends QObject {
        public DyingObject() { alive.add(id); }
        public void disposed() { alive.remove(alive.indexOf(id)); }
        static List<Integer> alive = new ArrayList<Integer>();
        static int counter = 0;
        public int id = ++counter;
    }


    private static void oneObject() {
        oneObject_helper();
        System.gc();
    }

    private static void oneObject_helper() {
        new DyingObject();
    }

    private static void objectWithParent() {
        objectWithParent_helper();
        System.gc();
    }

    private static void objectWithParent_helper() {
        new DyingObject().setParent(new DyingObject());
    }

    private static void objectWithUnParent() {
        objectWithUnParent_helper();
        System.gc();
    }

    private static void objectWithUnParent_helper() {
        DyingObject p = new DyingObject();
        DyingObject o = new DyingObject();
        o.setParent(p);
        o.setParent(null);
    }

    private static void threadExecutor(Runnable r, boolean qthread) {
        Thread t = qthread ? new QThread(r) : new Thread(r);
        t.start();
        try {
            t.join(1000);
        } catch (Exception e) { e.printStackTrace(); }
        System.gc();
    }

    @Test
    public void disposal_oneObject() {
        DyingObject.alive.clear();
        oneObject();
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_objectWithParent() {
        DyingObject.alive.clear();
        objectWithParent();
        QCoreApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_objectWithUnParent() {
        DyingObject.alive.clear();
        objectWithUnParent();
        QCoreApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_disposeInThread() {
        DyingObject.alive.clear();
        threadExecutor(runnable_disposeInThread, false);
        assertEquals(0, DyingObject.alive.size());

        threadExecutor(runnable_disposeInThread, true);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInQThread_oneObject() {
        DyingObject.alive.clear();
        threadExecutor(runnable_oneObject, true);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInQThread_objectWithParent() {
        DyingObject.alive.clear();
        threadExecutor(runnable_objectWithParent, true);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInQThread_objectWithUnParent() {
        DyingObject.alive.clear();
        threadExecutor(runnable_objectWithUnParent, true);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInThread_oneObject() {
        // Will warn about leaking the C++ object.
        DyingObject.alive.clear();
        threadExecutor(runnable_oneObject, false);
        assertEquals(0, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInThread_objectWithParent() {
        // Will warn once about leaking the C++ object. The child will not be collected
        DyingObject.alive.clear();
        threadExecutor(runnable_objectWithParent, false);
        assertEquals(1, DyingObject.alive.size());
    }

    @Test
    public void disposal_gcInThread_objectWithUnParent() {
        // Will warn twice about leaking the C++ object, but both objects will be collected
        DyingObject.alive.clear();
        threadExecutor(runnable_objectWithUnParent, false);
        assertEquals(0, DyingObject.alive.size());
    }

    private Runnable runnable_disposeInThread = new Runnable() {
        public void run() { new DyingObject().dispose(); }
    };

    private Runnable runnable_oneObject = new Runnable() {
        public void run() { oneObject(); }
    };

    private Runnable runnable_objectWithParent = new Runnable() {
        public void run() { objectWithParent(); }
    };

    private Runnable runnable_objectWithUnParent = new Runnable() {
        public void run() { objectWithUnParent(); }
    };
}
