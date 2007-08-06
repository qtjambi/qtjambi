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

import org.junit.*;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QApplication;

public abstract class QApplicationTest extends QSignalEmitter {
  
    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();        
    }
}
