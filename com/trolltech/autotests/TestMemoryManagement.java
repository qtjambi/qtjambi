package com.trolltech.autotests;

import static org.junit.Assert.*;
import org.junit.*;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.internal.QtJambiDebugTools;
import com.trolltech.qt.QtJambiObject;

// Attempt at complete test for general memory leaks and crashes
// Should test that all the general cases work as intended by default.
public abstract class TestMemoryManagement {

    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
        if (!QtJambiDebugTools.hasDebugTools())
            throw new RuntimeException("These tests can only be run when Qt Jambi is compiled with DEFINES += QTJAMBI_DEBUG_TOOLS");
    }
    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();
    }


    // Types: Normal object type with shell class
    //        Normal object type without shell class
    //        Normal value type
    //        QObject
    //        GUI object type
    //        GUI value type
    //
    // Creation: In Java
    //           In Native
    //
    // Ownership: Java Ownership
    //            Split Ownership
    //            Native Ownership
    //
    // Ways to delete: finalization
    //                 dispose
    //                 Deleted in native (split and native ownership)
    //                 invalidate (non-Java-Ownership only)
    //
    // Things to check:
    //    1. disposed() is called when it needs to be
    //    2. The destructor of the class is called (when applicable, i.e. not for non-polymorphic types
    //       that are deleted from C++) Both for the destructor of the shell class and the destructor
    //       function of the class (if it doesn't have a virtual destructor)
    //    3. The QtJambiLink for the object is deleted.
    //    4. The Java object is finalized.
    //
    // For these tests you need to compile Qt Jambi with DEFINES += QTJAMBI_DEBUG_TOOLS so that you
    // get compiled-in invocation counts.

    private void resetAll() {
        QtJambiDebugTools.reset_destructorFunctionCalledCount();
        QtJambiDebugTools.reset_disposeCalledCount();
        QtJambiDebugTools.reset_finalizedCount();
        QtJambiDebugTools.reset_linkConstructedCount();
        QtJambiDebugTools.reset_linkDestroyedCount();
        QtJambiDebugTools.reset_objectInvalidatedCount();
        QtJambiDebugTools.reset_shellDestructorCalledCount();
        QtJambiDebugTools.reset_userDataDestroyedCount();
    }


    private static final int TIME_LIMIT = 1000;

    private void test(String className,
            int finalizedCount,
            int destructorFunctionCalledCount,
            int disposeCalledCount,
            int linkConstructedCount,
            int linkDestroyedCount,
            int objectInvalidatedCount,
            int shellDestructorCalledCount,
            int userDataDestroyedCount) {

        assertEquals(finalizedCount, QtJambiDebugTools.finalizedCount(className));
        assertEquals(destructorFunctionCalledCount, QtJambiDebugTools.destructorFunctionCalledCount(className));
        assertEquals(disposeCalledCount, QtJambiDebugTools.disposeCalledCount(className));
        assertEquals(linkConstructedCount, QtJambiDebugTools.linkConstructedCount(className));
        assertEquals(linkDestroyedCount, QtJambiDebugTools.linkDestroyedCount(className));
        assertEquals(objectInvalidatedCount, QtJambiDebugTools.objectInvalidatedCount(className));
        assertEquals(shellDestructorCalledCount, QtJambiDebugTools.shellDestructorCalledCount(className));
        assertEquals(userDataDestroyedCount, QtJambiDebugTools.userDataDestroyedCount(className));

    }

    protected abstract QtJambiObject createInstanceInJava();

    protected abstract QtJambiObject createInstanceInNative();

    protected abstract void deleteLastInstance();

    protected abstract void invalidateObject(QtJambiObject obj);

    protected abstract String className();

    @Test
    public void finalize_CreatedInJava_JavaOwnership() {
        resetAll();

        {
            createInstanceInJava();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 0, 1, 1, 1, 1, 0);

    }

    @Test
    public void finalize_NotCreatedInJava_SplitOwnership() {
        resetAll();

        {
            createInstanceInNative();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 0, 0, 1, 1, 1, 0, 0);

    }

    @Test
    public void finalize_NotCreatedInJava_JavaOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            obj.setJavaOwnership();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 0, 1, 1, 1, 0, 0);
    }

    @Test
    public void dispose_CreatedInJava_JavaOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInJava();
            obj.dispose();

            test(className(), 0, 1, 1, 1, 1, 1, 1, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 1, 1, 1, 1, 1, 0);
    }

    @Test
    public void dispose_CreatedInJava_CppOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInJava();
            obj.disableGarbageCollection();
            obj.dispose();

            test(className(), 0, 1, 1, 1, 1, 1, 1, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 1, 1, 1, 1, 1, 0);
    }

    @Test
    public void dispose_NotCreatedInJava_SplitOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            obj.dispose();

            test(className(), 0, 1, 1, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 1, 1, 1, 1, 0, 0);
    }

    @Test
    public void dispose_NotCreatedInJava_JavaOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            obj.setJavaOwnership();
            obj.dispose();

            test(className(), 0, 1, 1, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 1, 1, 1, 1, 0, 0);
    }

    @Test
    public void dispose_NotCreatedInJava_CppOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            obj.disableGarbageCollection();
            obj.dispose();

            test(className(), 0, 1, 1, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 1, 1, 1, 1, 0, 0);
    }

    @Test
    public void nativeDelete_CreatedInJava_CppOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInJava();
            obj.disableGarbageCollection();

            deleteLastInstance();
            assertEquals(0, obj.nativeId());
            test(className(), 0, 0, 0, 1, 1, 1, 1, 0);

        }


        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 0, 0, 1, 1, 1, 1, 0);
    }

    @Test
    public void nativeDelete_NotCreatedInJava_CppOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();

            // If the object is not created in Java and not a QObject, we
            // have no way of knowing when it is deleted, so we need to
            // sever our ties immediately when disableGC is called. The
            // reason is that disable-gc in this case means c++ may try
            // to delete the object at any given time and will fail to alert
            // us about it, so we can get dangling pointers in our jambilink.
            obj.disableGarbageCollection();
            assertEquals(0, obj.nativeId());
            test(className(), 0, 0, 0, 1, 1, 1, 0, 0);

            deleteLastInstance();
            test(className(), 0, 0, 0, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 0, 0, 1, 1, 1, 0, 0);
    }

    @Test
    public void nativeDelete_NotCreatedInJava_SplitOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();

            deleteLastInstance();
            assertEquals(0, obj.nativeId());
            test(className(), 0, 0, 0, 1, 0, 0, 0, 0);

        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 0, 0, 1, 1, 1, 0, 0);
    }

    @Test
    public void invalidate_NotCreatedInJava_JavaOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            obj.setJavaOwnership();
            invalidateObject(obj);

            test(className(), 0, 1, 0, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 1, 0, 1, 1, 1, 0, 0);
    }

    @Test
    public void invalidate_NotCreatedInJava_SplitOwnership() {
        resetAll();

        {
            QtJambiObject obj = createInstanceInNative();
            invalidateObject(obj);

            test(className(), 0, 0, 0, 1, 1, 1, 0, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test(className(), 1, 0, 0, 1, 1, 1, 0, 0);
    }

}
