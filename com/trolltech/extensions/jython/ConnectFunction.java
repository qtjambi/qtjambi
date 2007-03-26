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

import com.trolltech.qt.*;
import org.python.core.*;

public class ConnectFunction extends PyObject {

    private String signature(int count) {
        StringBuilder s = new StringBuilder();
        s.append("execute_").append(count).append('(');
        for (int i=0; i<count; ++i) {
            if (i != 0)
                s.append(',');
            s.append("Object");
        }
        s.append(')');
        return s.toString();
    }

    @Override
    public PyObject __call__(PyObject[] pyObjects, String[] strings) {

        Object opaqueSignal = pyObjects[0].__tojava__(QSignalEmitter.AbstractSignal.class);
        if (opaqueSignal.equals(Py.NoConversion))
            throw new RuntimeException("QtJambi connect: First argument is not a signal");
        QSignalEmitter.AbstractSignal signal = (QSignalEmitter.AbstractSignal) opaqueSignal;

        PyMethod slot = null;
        if (pyObjects[1] instanceof PyMethod)
            slot = (PyMethod) pyObjects[1];
        else
            throw new RuntimeException("QtJambi connect: Second argument is not a member function");

        PythonSlot receiver = new PythonSlot(slot);
        if (signal instanceof QSignalEmitter.Signal0)
            ((QSignalEmitter.Signal0) signal).connect(receiver, signature(0));
        else if (signal instanceof QSignalEmitter.Signal1)
            ((QSignalEmitter.Signal1) signal).connect(receiver, signature(1));
        else if (signal instanceof QSignalEmitter.Signal2)
            ((QSignalEmitter.Signal2) signal).connect(receiver, signature(2));
        else if (signal instanceof QSignalEmitter.Signal3)
            ((QSignalEmitter.Signal3) signal).connect(receiver, signature(3));
        else if (signal instanceof QSignalEmitter.Signal4)
            ((QSignalEmitter.Signal4) signal).connect(receiver, signature(4));
        else if (signal instanceof QSignalEmitter.Signal5)
            ((QSignalEmitter.Signal5) signal).connect(receiver, signature(5));
        else if (signal instanceof QSignalEmitter.Signal6)
            ((QSignalEmitter.Signal6) signal).connect(receiver, signature(6));
        else if (signal instanceof QSignalEmitter.Signal7)
            ((QSignalEmitter.Signal7) signal).connect(receiver, signature(7));
        else if (signal instanceof QSignalEmitter.Signal8)
            ((QSignalEmitter.Signal8) signal).connect(receiver, signature(8));
        else if (signal instanceof QSignalEmitter.Signal9)
            ((QSignalEmitter.Signal9) signal).connect(receiver, signature(9));

        return null;
    }
}
