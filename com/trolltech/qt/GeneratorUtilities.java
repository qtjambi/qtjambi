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

import com.trolltech.qt.core.QObject;

public class GeneratorUtilities {
    private static final boolean threadAsserts;
    static {
        threadAsserts = !Utilities.matchProperty("com.trolltech.qt.thread-check", "false", "no");
    }

    public static void threadCheck(QObject obj) {
        if (threadAsserts)

            if (obj.thread() != null && obj.thread() != Thread.currentThread()) {
                throw new QThreadAffinityException("QObject used from outside its own thread",
                                                   obj,
                                                   Thread.currentThread());
            }
    }

    public static Object fetchField(Object owner, Class<?> declaringClass, String fieldName) {
        return com.trolltech.qt.internal.QtJambiInternal.fetchField(owner, declaringClass, fieldName);
    }

    public static void setField(Object owner, Class<?> declaringClass, String fieldName, Object newValue) {
        com.trolltech.qt.internal.QtJambiInternal.setField(owner, declaringClass, fieldName, newValue);
    }

    public static void countExpense(Class<?> cl, int cost, int limit) {
        com.trolltech.qt.internal.QtJambiInternal.countExpense(cl, cost, limit);
    }

    public static Object createExtendedEnum(int value, int ordinal,
            Class<?> cl, String name) {
        return com.trolltech.qt.internal.QtJambiInternal.createExtendedEnum(value, ordinal, cl, name);
    }
}
