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

import org.junit.Test;

import com.trolltech.autotests.generated.OrdinaryDestroyed;
import com.trolltech.qt.gui.QApplication;

import static org.junit.Assert.*;

class MyOrdinaryDestroyed extends OrdinaryDestroyed
{

    @Override
    public OrdinaryDestroyed virtualGetObjectCppOwnership() {
        return new MyOrdinaryDestroyed();
    }

    @Override
    public OrdinaryDestroyed virtualGetObjectJavaOwnership() {
        return new MyOrdinaryDestroyed();
    }

    @Override
    public void virtualSetDefaultOwnership(OrdinaryDestroyed arg__1) {
        // nothing
    }

}

public class TestDestruction extends QApplicationTest {
    @Test
    public void testJavaCreationJavaOwnership()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        {
            MyOrdinaryDestroyed d = new MyOrdinaryDestroyed();
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(1, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testJavaCreationCppOwnership()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        MyOrdinaryDestroyed dontBeDeleted = new MyOrdinaryDestroyed();

        {
            // Garbage collection has now been disabled on d
            OrdinaryDestroyed d = MyOrdinaryDestroyed.callGetObjectCppOwnership(dontBeDeleted);
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(0, MyOrdinaryDestroyed.disposedCount);
        assertEquals(0, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testJavaCreationDefaultOwnershipThroughNative()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        MyOrdinaryDestroyed dontBeDeleted = new MyOrdinaryDestroyed();

        {
            // Garbage collection has now been disabled on d
            OrdinaryDestroyed d = MyOrdinaryDestroyed.callGetObjectCppOwnership(dontBeDeleted);

            // Set default ownership on d (should be "java")
            MyOrdinaryDestroyed.setDefaultOwnership(d);
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(1, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testJavaCreationDefaultOwnershipThroughShell()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        MyOrdinaryDestroyed dontBeDeleted = new MyOrdinaryDestroyed();

        {
            // Garbage collection has now been disabled on d
            OrdinaryDestroyed d = MyOrdinaryDestroyed.callGetObjectCppOwnership(dontBeDeleted);

            // Set default ownership on d (should be "java")
            MyOrdinaryDestroyed.callSetDefaultOwnership(dontBeDeleted, d);
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(1, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testCppCreationSplitOwnership() {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }
        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        {
            // Split ownership: Java object should be collected, but the c++ object should not be baleeted
            OrdinaryDestroyed d = MyOrdinaryDestroyed.getObjectSplitOwnership();
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(0, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testCppCreationCppOwnership() {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }
        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        {
            OrdinaryDestroyed d = MyOrdinaryDestroyed.getObjectCppOwnership();
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(0, MyOrdinaryDestroyed.disposedCount);
        assertEquals(0, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testCppCreationJavaOwnership() {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }
        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        {
            OrdinaryDestroyed d = MyOrdinaryDestroyed.getObjectJavaOwnership();
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(1, MyOrdinaryDestroyed.destroyedCount());
    }

    @Test
    public void testCppCreationDefaultOwnershipThroughShell()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        MyOrdinaryDestroyed dontBeDeleted = new MyOrdinaryDestroyed();

        {
            // Garbage collection has now been disabled on d
            OrdinaryDestroyed d = MyOrdinaryDestroyed.getObjectCppOwnership();

            // Set default ownership on d (should be "split")
            MyOrdinaryDestroyed.callSetDefaultOwnership(dontBeDeleted, d);
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(0, MyOrdinaryDestroyed.destroyedCount());
    }


    @Test
    public void testCppCreationDefaultOwnershipThroughNative()
    {
        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);

        MyOrdinaryDestroyed dontBeDeleted = new MyOrdinaryDestroyed();

        {
            // Garbage collection has now been disabled on d
            OrdinaryDestroyed d = MyOrdinaryDestroyed.getObjectCppOwnership();

            // Set default ownership on d (should be "split")
            MyOrdinaryDestroyed.setDefaultOwnership(d);
        }

        for (int i=0; i<10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // exceptions are an idiotic concept
            }
            System.gc();
        }

        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(0, MyOrdinaryDestroyed.destroyedCount());
    }

    static public void main(String args[]) {
        QApplication.initialize(args);
        TestDestruction test = new TestDestruction();
        test.testCppCreationCppOwnership();
    }
}
