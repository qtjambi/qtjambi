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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @exclude
 */
@Deprecated
public class QtJambiInternal {

    static {
        QtJambi_LibraryInitializer.init();
    }

    /**
     * This method has been deprecated. Please use com.trolltech.qt.QSignalEmitter.connectSlotsByName()
     * instead.
     */
    @Deprecated
    public static void connectSlotsByName(QObject object) {
        com.trolltech.qt.internal.QtJambiInternal.connectSlotsByName(object);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.core.QAbstractFileEngine.addSearchPathForResourceEngine() instead.
     */
    @Deprecated
    public static void addSearchPathForResourceEngine(String path)
    {
        com.trolltech.qt.internal.QClassPathEngine.addSearchPath(path);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.core.QAbstractFileEngine.removeSearchPathForResourceEngine() instead.
     */
    @Deprecated
    public static void removeSearchPathForResourceEngine(String path)
    {
        com.trolltech.qt.internal.QClassPathEngine.removeSearchPath(path);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.GeneratorUtilities.threadCheck() instead.
     */
    @Deprecated
    public static void threadCheck(QObject obj) {
        GeneratorUtilities.threadCheck(obj);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.GeneratorUtilities.fetchField() instead.
     */
    @Deprecated
    public static Object fetchField(Object owner, Class<?> declaringClass, String fieldName) {
        return com.trolltech.qt.internal.QtJambiInternal.fetchField(owner, declaringClass, fieldName);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.GeneratorUtilities.setField() instead.
     */
    @Deprecated
    public static void setField(Object owner, Class<?> declaringClass, String fieldName, Object newValue) {
        com.trolltech.qt.internal.QtJambiInternal.setField(owner, declaringClass, fieldName, newValue);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.GeneratorUtilities.countExpense() instead.
     */
    @Deprecated
    public static void countExpense(Class<?> cl, int cost, int limit) {
        com.trolltech.qt.internal.QtJambiInternal.countExpense(cl, cost, limit);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.GeneratorUtilities.createExtendedEnum() instead.
     */
    @Deprecated
    public static Object createExtendedEnum(int value, int ordinal,
            Class<?> cl, String name) {
        return com.trolltech.qt.internal.QtJambiInternal.createExtendedEnum(value, ordinal, cl, name);
    }

}
