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

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QMdiArea;
import com.trolltech.qt.gui.QMdiSubWindow;
import com.trolltech.qt.gui.QWidget;

import static org.junit.Assert.*;

public class TestConstruction extends QApplicationTest {

    @Test
    public void testQObjectConversionInConstructor() {
        QMdiArea area = new QMdiArea() {
            @Override
            public boolean eventFilter(QObject o, QEvent e) {
                return false;
            }
        };
        area.show();

        QMdiSubWindow subWindow = area.addSubWindow(new QWidget());

        // Although it seems unlikely, this may actually be false, trust me.
        assertTrue(subWindow instanceof QMdiSubWindow);
    }
}
