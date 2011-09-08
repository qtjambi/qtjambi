/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.internal.QtJambiDebugTools;

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
        System.err.flush();
        System.out.flush();
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

    protected final void resetAll() {
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

    protected final void test(String className,
            int finalizedCount,
            int destructorFunctionCalledCount,
            int disposeCalledCount,
            int linkConstructedCount,
            int linkDestroyedCount,
            int objectInvalidatedCount,
            int shellDestructorCalledCount,
            int userDataDestroyedCount) {

        assertEquals(finalizedCount, QtJambiDebugTools.finalizedCount(className));
        assertEquals(!isQObject() ? destructorFunctionCalledCount : 0, QtJambiDebugTools.destructorFunctionCalledCount(className));
        assertEquals(disposeCalledCount, QtJambiDebugTools.disposeCalledCount(className));
        assertEquals(linkConstructedCount, QtJambiDebugTools.linkConstructedCount(className));
        assertEquals(linkDestroyedCount, QtJambiDebugTools.linkDestroyedCount(className));
        assertEquals(objectInvalidatedCount, QtJambiDebugTools.objectInvalidatedCount(className));
        assertEquals(hasShellDestructor() ? shellDestructorCalledCount : 0, QtJambiDebugTools.shellDestructorCalledCount(className));
        assertEquals(isQObject() ? userDataDestroyedCount : 0, QtJambiDebugTools.userDataDestroyedCount(className));

    }

    protected abstract QtJambiObject createInstanceInJava();

    protected abstract QtJambiObject createInstanceInNative();

    protected abstract void deleteLastInstance();

    protected abstract QtJambiObject invalidateObject(QtJambiObject obj, final boolean returnReference);

    protected abstract boolean hasShellDestructor();

    protected abstract String className();

    protected abstract boolean hasVirtualDestructor();

    protected boolean isValueType() {
        return false;
    }

    protected boolean needsEventProcessing() {
        return false;
    }

    protected boolean isQObject() {
        return false;
    }

    protected boolean supportsSplitOwnership() {
        return true;
    }

    @Test
    public void finalize_CreatedInJava_JavaOwnership() {
        resetAll();

        {
            createInstanceInJava();
        }

        gcAndWait();

        test(className(), 1, 1, 0, 1, 1, 1, 1, 1);

    }

    @Test
    public void finalize_NotCreatedInJava_SplitOwnership() {
        if (supportsSplitOwnership()) {
            resetAll();

            createInstanceInNativeWithSplitOwnership();

            gcAndWait();

            test(className(), 1, 0, 0, 1, 1, 1, 0, 0);
        }
    }

    private void createInstanceInNativeWithSplitOwnership() {
        createInstanceInNative();
    }

    @Test
    public void finalize_NotCreatedInJava_JavaOwnership() {
        resetAll();

        createInstanceInNativeWithJavaOwnership();

        gcAndWait();

        test(className(), 1, 1, 0, 1, 1, 1, 0, 1);
    }

    private void createInstanceInNativeWithJavaOwnership() {
        QtJambiObject obj = createInstanceInNative();
        obj.setJavaOwnership();
    }

    @Test
    public void dispose_CreatedInJava_JavaOwnership() {
        resetAll();

        createInstanceInJavaAndDisposeIt();

        gcAndWait();

        test(className(), 1, 1, 1, 1, 1, 1, 1, 1);
    }

    private void createInstanceInJavaAndDisposeIt() {
        QtJambiObject obj = createInstanceInJava();
        obj.dispose();

        test(className(), 0, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void dispose_CreatedInJava_CppOwnership() {
        resetAll();

        createInJavaDisableGCAndDispose();

        gcAndWait();

        test(className(), 1, 1, 1, 1, 1, 1, 1, 1);
    }

    private void createInJavaDisableGCAndDispose() {
        QtJambiObject obj = createInstanceInJava();
        obj.disableGarbageCollection();
        obj.dispose();

        test(className(), 0, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void dispose_NotCreatedInJava_SplitOwnership() {
        if (supportsSplitOwnership()) {
            resetAll();

            createInNativeAndDispose();

            gcAndWait();

            test(className(), 1, 1, 1, 1, 1, 1, 0, 0);
        }
    }

    private void createInNativeAndDispose() {
        QtJambiObject obj = createInstanceInNative();
        obj.dispose();

        test(className(), 0, 1, 1, 1, 1, 1, 0, 0);
    }

    @Test
    public void dispose_NotCreatedInJava_JavaOwnership() {
        resetAll();

        createInNativeSetJavaOwnershipAndDispose();

        gcAndWait();

        test(className(), 1, 1, 1, 1, 1, 1, 0, 1);
    }

    private void createInNativeSetJavaOwnershipAndDispose() {
        QtJambiObject obj = createInstanceInNative();
        obj.setJavaOwnership();
        obj.dispose();

        test(className(), 0, 1, 1, 1, 1, 1, 0, 1);
    }

    @Test
    public void dispose_NotCreatedInJava_CppOwnership() {
        resetAll();

        createInNativeDisableGCAndDispose();

        gcAndWait();

        test(className(), 1, 1, 1, 1, 1, 1, 0, 1);
    }

    private void createInNativeDisableGCAndDispose() {
        QtJambiObject obj = createInstanceInNative();
        obj.disableGarbageCollection();
        obj.dispose();

        test(className(), 0, 1, 1, 1, 1, 1, 0, 1);
    }

    @Test
    public void nativeDelete_CreatedInJava_CppOwnership() {
        resetAll();

        createInJavaDisableGCAndDeleteInNative();

        gcAndWait();

        if (hasVirtualDestructor())
            test(className(), 1, 0, 0, 1, 1, 1, 1, 1);
        else
            test(className(), 0, 0, 0, 1, 0, 0, 0, 0);

    }

    private void createInJavaDisableGCAndDeleteInNative() {
        QtJambiObject obj = createInstanceInJava();
        obj.disableGarbageCollection();

        deleteLastInstance();
        if (hasVirtualDestructor()) {
            assertEquals(0L, obj.nativeId());
            test(className(), 0, 0, 0, 1, 1, 1, 1, 1);
        } else {
            test(className(), 0, 0, 0, 1, 0, 0, 0, 0);
        }
    }

    // Many objects leak in this test. Cases that lead to this scenario
    // must be specially handled to avoid memory leaks.
    @Test
    public void nativeDelete_NotCreatedInJava_CppOwnership() {
        resetAll();

        createInNativeDisableGCAndDeleteInNative();
        gcAndWait();

        test(className(), isQObject() ? 1 : 0, 0, 0, 1, isQObject() ? 1 : 0, isQObject() ? 1 : 0, 0, 1);
    }

    private void createInNativeDisableGCAndDeleteInNative() {
        QtJambiObject obj = createInstanceInNative();

        obj.disableGarbageCollection();
        test(className(), 0, 0, 0, 1, 0, 0, 0, 0);

        deleteLastInstance();
        test(className(), 0, 0, 0, 1, isQObject() ? 1 : 0, isQObject() ? 1 : 0, 0, 1);
    }


    protected final void gcAndWait() {
        gcAndWait(1);
    }

    private void gcAndWait(int finalizedCount) {
        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        while (QtJambiDebugTools.finalizedCount(className()) < finalizedCount && elapsed < TIME_LIMIT) try {
            System.gc();
            Thread.sleep(10);

            if (needsEventProcessing())
                QApplication.processEvents(new QEventLoop.ProcessEventsFlags(QEventLoop.ProcessEventsFlag.DeferredDeletion));

            elapsed = System.currentTimeMillis() - startTime;

        } catch (Exception e) {

        }
    }

    @Test
    public void nativeDelete_NotCreatedInJava_SplitOwnership() {
        if (supportsSplitOwnership()) {
            resetAll();

            createInNativeDeleteInNative();

            gcAndWait();

            test(className(), 1, 0, 0, 1, 1, 1, 0, 1);
        }
    }

    private void createInNativeDeleteInNative() {
        QtJambiObject obj = createInstanceInNative();

        deleteLastInstance();
        test(className(), 0, 0, 0, 1, 0, 0, 0, 1);
    }

    @Test
    public void invalidate_NotCreatedInJava_JavaOwnership() {
        resetAll();

        createInNativeSetJavaOwnershipAndInvalidate();

        gcAndWait(isQObject() ? 1 : 2);

        test(className(), isQObject() ? 1 : 2, isValueType() ? 2 : 1, 0, isQObject() ? 1 : 2, isQObject() ? 1 : 2, isQObject() ? 1 : 2, 0, 1);
    }

    private void createInNativeSetJavaOwnershipAndInvalidate() {
        QtJambiObject obj = createInstanceInNative();
        QtJambiObject obj2 = invalidateObject(obj, true);

        // Java owned objects are not invalidated
        assertTrue(0L != obj2.nativeId());

        test(className(), 0, 0, 0, isQObject() ? 1 : 2, 0, 0, 0, 0);
    }

    // Note that there are two Java objects in use here,
    // because there is an extra wrapper created in
    // the virtual call from C++ to Java in invalidateObject().
    @Test
    public void invalidate_NotCreatedInJava_SplitOwnership() {
        if (supportsSplitOwnership()) {
            resetAll();

            createInNativeAndInvalidate();

            gcAndWait(2);

            test(className(), 2, 0, 0, 2, 2, 2, 0, 0);
        }
    }

    private void createInNativeAndInvalidate() {
        QtJambiObject obj = createInstanceInNative();
        invalidateObject(obj, false);

        test(className(), 0, 0, 0, 2, 1, 1, 0, 0);
    }

}
