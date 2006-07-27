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

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qtest.*;

import java.util.*;

public class TestSignalMapper extends QTestCase {

    /**
     * Receiver class for the various mapped signals in this test.
     */
    private static class Receiver extends QObject {
        public int lastInteger;
        public String lastString; 
        public QObject lastQObject;
        public QWidget lastQWidget;
        
        public void slotInteger(int i) { lastInteger = i; }        
        public void slotString(String s) { lastString = s; }
        public void slotQObject(QObject o) { lastQObject = o; }
        public void slotQWidget(QWidget w) { lastQWidget = w; }
    }
    
    /**
     * Emitter class for triggering the vairous mapped signals... 
     */
    private static class Emitter extends QObject {
        Signal0 signal = new Signal0();
        public void emitSignal() { signal.emit(); }
    }
    
    
    
    public void run_mappedInt() {        
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];
        
        for (int i=0; i<emitters.length; ++i) {            
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], i);           
        }
        mapper.mappedInteger.connect(receiver, "slotInteger(int)");

        for (int i=0; i<10; ++i) {
            emitters[i].emitSignal();
            QCOMPARE(receiver.lastInteger, i); 
        }
    }
    
    
    public void run_mappedString() {        
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];
        
        for (int i=0; i<emitters.length; ++i) {            
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], "id(" + i + ")");           
        }
        mapper.mappedString.connect(receiver, "slotString(String)");

        for (int i=0; i<10; ++i) {
            emitters[i].emitSignal();
            QCOMPARE(receiver.lastString, "id(" + i + ")"); 
        }
    }
    
    
    public void run_mappedQObject() {        
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];
        
        for (int i=0; i<emitters.length; ++i) {            
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], emitters[i]);           
        }
        mapper.mappedQObject.connect(receiver, "slotQObject(QObject)");

        for (int i=0; i<10; ++i) {
            emitters[i].emitSignal();
            QCOMPARE(receiver.lastQObject, emitters[i]); 
        }
    }
    
    
    public void run_mappedQWidget() {        
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];
        QWidget widgets[] = new QWidget[10];
        
        for (int i=0; i<emitters.length; ++i) {            
            emitters[i] = new Emitter();
            widgets[i] = new QWidget();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], widgets[i]);           
        }
        mapper.mappedQWidget.connect(receiver, "slotQWidget(QWidget)");

        for (int i=0; i<10; ++i) {
            emitters[i].emitSignal();
            QCOMPARE(receiver.lastQWidget, widgets[i]); 
        }
    }
    
    public static void main(String[] args) {
        QApplication app = new QApplication(args);
        
        runTest(new TestSignalMapper());
    }

}
