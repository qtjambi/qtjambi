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

package com.trolltech.qt.internal;

/**
 * @exclude
 *
 * Special debugging methods used by autoests which are only available
 * if Qt Jambi is compiled with QTJAMBI_DEBUG_TOOLS defined.
 *
 */
public class QtJambiDebugTools {



    /**
     * Returns true if the debug tools functions are compiled in and
     * false if not.
     */
    public static native boolean hasDebugTools();




    /**
     * Same as calling resetFinalizedCount(null)
     */
    public static void reset_finalizedCount() { reset_finalizedCount(null); }

    /**
     * Same as calling finalizedCount(null)
     */
    public static int finalizedCount() { return finalizedCount(null); }

    /**
     * Resets the counter for calls to QtJambiObject.finalize()
     *
     * @param className The class name for which to reset, or null for all.
     */
    public static native void reset_finalizedCount(String className);

    /**
     * Retrieve number of calls to QtJambiObject.finalize()
     *
     * @param className The class name for which to retrieve, or null for all.
     * @return The number of QtJambiObjects that have been finalized
     */
    public static native int finalizedCount(String className);




    /**
     * Reset number of calls to the destructor of QtJambiUserData
     *
     * @param className The class name for which to reset, or null for all.
     */
    public static native void reset_userDataDestroyedCount(String className);

    /**
     * Retrieve number of calls to the destructor of QtJambiUserData
     *
     * @param className The class name for which to retrieve, or null for all.
     * @return The number of QObjects that have been destroyed
     */
    public static native int userDataDestroyedCount(String className);

    /**
     * Same as calling resetUserDataDestroyedCount(null)
     */
    public static void reset_userDataDestroyedCount() { reset_userDataDestroyedCount(null); }

    /**
     * Same as calling userDataDestroyedCount(null)
     */
    public static int userDataDestroyedCount() { return userDataDestroyedCount(null); }




    /**
     * Same as calling resetDestructorFunctionCalledCount(null)
     */
    public static void reset_destructorFunctionCalledCount() { reset_destructorFunctionCalledCount(null); }

    /**
     * Same as calling destructorFunctionCalledCount(null)
     */
    public static int destructorFunctionCalledCount() { return destructorFunctionCalledCount(null); }

    /**
     * Resets the counter for calls to destructor functions (used by QtJambiLink to delete native data)
     *
     * @param className The name of the class for which to reset or null for all
     */
    public static native void reset_destructorFunctionCalledCount(String className);

    /**
     * Retrieves the counter for calls to destructor functions (used by QtJambiLink to delete native data)
     *
     * @param className The name of the class for which to retrieve or null for all
     * @return The number of times destructor functions have been called
     */
    public static native int destructorFunctionCalledCount(String name);




    /**
     * Resets the counter for calls to shell class destructors in polymorphic classes.
     *
     * @param className The name of the class for which to reset or null for all
     */
    public static native void reset_shellDestructorCalledCount(String className);

    /**
     * Retrieves the counter for calls to shell class destructors in polymorphic classes.
     *
     * @param className The name of the class for which to retrieve or null for all
     * @return The number of times a shell class destructor has been called
     */
    public static native int shellDestructorCalledCount(String className);

    /**
     * Same as calling resetShellDestructorCalledCount(null)
     */
    public static void reset_shellDestructorCalledCount() {
        reset_shellDestructorCalledCount(null);
    }

    /**
     * Same as calling shellDestructorCalledCount(null)
     */
    public static int shellDestructorCalledCount() {
        return shellDestructorCalledCount(null);
    }




    /**
     * Resets the counter for calls to disposed() (Qt Jambi objects that have been invalidated)
     *
     * @param className The name of the class for which to reset or null for all
     */
    public static native void reset_objectInvalidatedCount(String className);

    /**
     * Retrieves the counter for calls to disposed() (Qt Jambi objects that have been invalidated)
     *
     * @param className The name of the class for which to reset or null for all
     */
    public static native int objectInvalidatedCount(String className);

    /**
     * Same as calling resetDisposedCalledCount(null)
     */
    public static void reset_objectInvalidatedCount() { reset_objectInvalidatedCount(null); }

    /**
     * Same as calling disposedCalledCount(null)
     */
    public static int objectInvalidatedCount() { return objectInvalidatedCount(null); }




    /**
     * Resets the counter for calls to dispose() (Qt Jambi objects that have been explicitly deleted by
     * Java code)
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native void reset_disposeCalledCount(String className);

    /**
     * Retrieves the counter for calls to dispose() (Qt Jambi objects that have been explicitly deleted by
     * Java code)
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native int disposeCalledCount(String className);

    /**
     * Same as calling resetDisposeCalledCount(null)
     */
    public static void reset_disposeCalledCount() { reset_disposeCalledCount(null); }

    /**
     * Same as calling disposeCalledCount(null)
     */
    public static int disposeCalledCount() { return disposeCalledCount(null); }




    /**
     * Resets the counter for calls to the QtJambiLink destructor
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native void reset_linkDestroyedCount(String className);

    /**
     * Retrieves the counter for calls to the QtJambiLink destructor
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native int linkDestroyedCount(String className);

    /**
     * Same as calling resetLinkDestroyedCount(null)
     */
    public static void reset_linkDestroyedCount() { reset_linkDestroyedCount(null); }

    /**
     * Same as calling linkDestroyedCount(null)
     */
    public static int linkDestroyedCount() { return linkDestroyedCount(null); }



    /**
     * Resets the counter for calls to the QtJambiLink destructor
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native void reset_linkConstructedCount(String className);

    /**
     * Retrieves the counter for calls to the QtJambiLink destructor
     *
     * @param className The name of class for which to reset or null for all
     */
    public static native int linkConstructedCount(String className);

    /**
     * Same as calling resetLinkDestroyedCount(null)
     */
    public static void reset_linkConstructedCount() { reset_linkConstructedCount(null); }

    /**
     * Same as calling linkDestroyedCount(null)
     */
    public static int linkConstructedCount() { return linkConstructedCount(null); }


    // puh...
}
