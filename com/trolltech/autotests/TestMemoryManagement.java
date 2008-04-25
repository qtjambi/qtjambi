package com.trolltech.autotests;

import static org.junit.Assert.*;
import org.junit.*;

import com.trolltech.autotests.generated.PolymorphicObjectType;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.internal.QtJambiDebugTools;

// Attempt at complete test for general memory leaks and crashes
// Should test that all the general cases work as intended by default.
public class TestMemoryManagement {

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

    /**
     * Test a polymorphic object type created in and owned by Java,. being deleted by finalizer
     */
    @Test
    public void finalize_PolymorphicObject_CreatedInJava_JavaOwnership() {
        resetAll();

        {
            new PolymorphicObjectType();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount("PolymorphicObjectType") == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test("PolymorphicObjectType", 1, 1, 0, 1, 1, 1, 1, 0);

    }

    @Test
    public void finalize_PolymorphicObject_NotCreatedInJava_SplitOwnership() {
        resetAll();

        {
            PolymorphicObjectType.newInstance();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount("PolymorphicObjectType") == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test("PolymorphicObjectType", 1, 0, 0, 1, 1, 1, 0, 0);

    }

    @Test
    public void finalize_PolymorphicObject_NotCreatedInJava_JavaOwnership() {
        resetAll();

        {
            PolymorphicObjectType pot = PolymorphicObjectType.newInstance();
            pot.setJavaOwnership();
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount("PolymorphicObjectType") == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test("PolymorphicObjectType", 1, 1, 0, 1, 1, 1, 0, 0);
    }

    @Test
    public void dispose_PolymorphicObject_CreatedInJava_JavaOwnership() {
        resetAll();

        {
            PolymorphicObjectType pot = new PolymorphicObjectType();
            pot.dispose();

            test("PolymorphicObjectType", 0, 1, 1, 1, 1, 1, 1, 0);
        }

        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount("PolymorphicObjectType") == 0 && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }

        test("PolymorphicObjectType", 1, 1, 1, 1, 1, 1, 1, 0);
    }
}
