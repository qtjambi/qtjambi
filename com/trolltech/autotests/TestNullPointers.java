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

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QLayoutItemInterface;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

import static org.junit.Assert.*;
public class TestNullPointers extends QApplicationTest {

    @Test
    public void testBoxLayoutAddWidget() {
        QVBoxLayout layout = new QVBoxLayout();

        Exception e = null;
        try {
            layout.addWidget(null);
        } catch (Exception f) {
            e = f;
        }

        assertTrue(e instanceof NullPointerException);
    }

    static class MyLayout extends QLayout {

        @Override
        public QLayoutItemInterface itemAt(int arg__1) {
            return null;
        }

        @Override
        public void addItem(QLayoutItemInterface arg__1) {
            // TODO Auto-generated method stub

        }

        @Override
        public int count() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void setGeometry(QRect arg__1) {
            // TODO Auto-generated method stub

        }

        @Override
        public QSize sizeHint() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public QLayoutItemInterface takeAt(int index) {
            return null;
        }

    }


    @Test
    public void testBlah() {
        QWidget w = new QWidget();

        MyLayout l = new MyLayout();
        w.setLayout(l);
        l.addWidget(new QWidget());

        w.show();

        QApplication.processEvents();
    }


    public static void main(String args[]) {
        QApplication.initialize(args);
        TestNullPointers p = new TestNullPointers();
        p.testBlah();

    }

}
