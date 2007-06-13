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

public class Signal1 extends PythonSignal {
    Signal1<Object> signal = new Signal1<Object>();

    public void emit(Object a) { signal.emit(a); }
}
