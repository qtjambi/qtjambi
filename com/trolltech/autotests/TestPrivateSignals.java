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

import java.util.*;
import java.lang.reflect.*;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import static org.junit.Assert.*;
import org.junit.*;

public class TestPrivateSignals extends QApplicationTest {

    private static class DummyModel extends QAbstractItemModel {
        public int columnCount(QModelIndex parent) {
            return 1;
        }
        public int rowCount(QModelIndex parent) {
            return 1;
        }
        public Object data(QModelIndex index, int role) {
            return null;
        }
        public QModelIndex index(int row, int column, QModelIndex parent) {
            return createIndex(row, column);
        }
        public QModelIndex parent(QModelIndex child) {
            return null;
        }
        public void doReset() {
            reset();
        }
    }

    private boolean triggered_modelReset;
    private boolean triggered_modelAboutToBeReset;

    public void handle_modelReset() {
        triggered_modelReset = true & triggered_modelAboutToBeReset;
    }
    public void handle_modelAboutToBeReset() {
        triggered_modelAboutToBeReset = true;
    }

    @Test
    public void test_emit_PrivateSignal0() {
        DummyModel model = new DummyModel();

        triggered_modelReset = false;
        triggered_modelAboutToBeReset = false;

        model.doReset();

        assertFalse(triggered_modelReset);
        assertFalse(triggered_modelAboutToBeReset);

        model.modelReset.connect(this, "handle_modelReset()");
        model.modelAboutToBeReset.connect(this, "handle_modelAboutToBeReset()");

        model.doReset();

        assertTrue(triggered_modelAboutToBeReset);
        assertTrue(triggered_modelReset);
    }

    @Test
    public void test_privateSignal_is_PrivateSignal() {
        Field signal = null;
        try {
            signal = QAbstractItemModel.class.getField("modelReset");
        } catch (Exception e) { }

        assertTrue(signal != null);
        assertEquals(signal.getType().getName(), "com.trolltech.qt.QSignalEmitter$PrivateSignal0");
    }



}
