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

package com.trolltech.qt;

import com.trolltech.qt.core.QObject;

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
    public static void addSearchPathForResourceEngine(String path) {
        com.trolltech.qt.internal.fileengine.QClassPathEngine.addSearchPath(path, false);
    }

    /**
     * This method has been deprecated. Please use
     * com.trolltech.qt.core.QAbstractFileEngine.removeSearchPathForResourceEngine() instead.
     */
    @Deprecated
    public static void removeSearchPathForResourceEngine(String path) {
        com.trolltech.qt.internal.fileengine.QClassPathEngine.removeSearchPath(path);
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
    public static Object createExtendedEnum(int value, int ordinal, Class<?> cl, String name) {
        return com.trolltech.qt.internal.QtJambiInternal.createExtendedEnum(value, ordinal, cl, name);
    }

}
