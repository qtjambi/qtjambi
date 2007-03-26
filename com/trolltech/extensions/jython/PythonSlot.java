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

package com.trolltech.extensions.jython;

import org.python.core.*;

class PythonSlot {

    public PythonSlot(PyMethod slot) {
        this.slot = slot;
    }

    private PyJavaInstance wrap(Object x) { return new PyJavaInstance(x); }

    public void execute_0() {
        slot.__call__();
    }

    public void execute_1(Object a) {
        slot.__call__(wrap(a));
    }

    public void execute_2(Object a, Object b) {
        slot.__call__(wrap(a), wrap(b));
    }

    public void execute_3(Object a, Object b, Object c) {
        slot.__call__(wrap(a), wrap(b), wrap(c));
    }

    public void execute_4(Object a, Object b, Object c, Object d) {
        slot.__call__(wrap(a), wrap(b), wrap(c), wrap(d));
    }

    private void genericExecute(Object ... vals) {
        PyObject pys[] = new PyObject[vals.length];
        for (int i=0; i<vals.length; ++i) {
            pys[i] = wrap(vals[i]);
        }
        slot.__call__(pys);
    }

    public void execute_5(Object a, Object b, Object c, Object d, Object e) {
        genericExecute(a, b, c, d, e);
    }

    public void execute_6(Object a, Object b, Object c, Object d, Object e, Object f) {
        genericExecute(a, b, c, d, e, f);
    }

    public void execute_7(Object a, Object b, Object c, Object d, Object e, Object f, Object g) {
        genericExecute(a, b, c, d, e, f, g);
    }

    public void execute_8(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h) {
        genericExecute(a, b, c, d, e, f, g, h);
    }

    public void execute_9(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i) {
        genericExecute(a, b, c, d, e, f, g, h, i);
    }

    private PyMethod slot;
}

