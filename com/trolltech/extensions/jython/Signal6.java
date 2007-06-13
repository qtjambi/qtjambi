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

public class Signal6 extends PythonSignal {
    Signal6 signal = new Signal6();

    public void emit(Object a, Object b, Object c,
                     Object d, Object e, Object f) {
        signal.emit(a, b, c, d, e, f);
    }
}

