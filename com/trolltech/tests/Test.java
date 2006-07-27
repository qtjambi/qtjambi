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

package com.trolltech.tests;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


import java.util.*;

public class Test
{

    private static final boolean VERBOSE = true;
    
    static class CollectedQObject extends QFile {
        public CollectedQObject() { 
            super("");
            if (VERBOSE) System.out.println("Created Collected QObject");
        }
        protected void finalize() {
            if (VERBOSE) System.out.println("QFile finalized...");
        }
    }
    
    static class CollectedObject extends QColor {
        public CollectedObject() {
            if (VERBOSE) System.out.println("Created Collected Object");
        }
        protected void finalize() {
            if (VERBOSE) System.out.println("Object finalized...");
        }        
    }
    
    static class NonCollectedQObject extends QObject {
        public NonCollectedQObject() {
	    if (VERBOSE) System.out.println("Created QObject that is not collected and explicitly disposed");
        }
        protected void finalize() {
            if (VERBOSE) System.out.println("Non-Collected object finalized...");
        }
    }

    static class Timer extends QObject {
	public Timer() {
	    startTimer(10);
	}

	protected void timerEvent(QTimerEvent e) {
  	    byte x[] = new byte[1024 * 1024];
	    new CollectedObject();
	}
    }
    
    public static void main(String args[]) throws Exception
    {
        QApplication app = new QApplication(args);

	Timer t = new Timer();

	app.exec();
        
// 	while (true) {
//             System.out.println();
//             QtObject col_obj = new CollectedObject();
//             System.out.println();
//             QtObject ncol_qobj = new NonCollectedQObject();
//             System.out.println();
//             QtObject col_qobj = new CollectedQObject();            
//             System.out.println();
//             ncol_qobj.dispose();
//             ncol_qobj.dispose();
//         }

    }
}
