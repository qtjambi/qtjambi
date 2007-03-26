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

import com.trolltech.qt.gui.*;
import org.python.util.*;

public class Interpreter {

    public static void main(String args[]) {
        QApplication.initialize(args);

        PythonInterpreter ip = new PythonInterpreter();

        ConnectFunction connect = new ConnectFunction();
        ip.set("qConnect", connect);

        if (args.length != 1) {
            System.err.println("input script name, please...");
            return;
        }

        ip.execfile(args[0]);

        QApplication.exec();
    }
}
