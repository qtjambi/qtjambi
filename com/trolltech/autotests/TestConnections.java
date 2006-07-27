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

import com.trolltech.qtest.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.autotests.generator.*;

import java.lang.reflect.*;
import java.util.*;

class SignalsAndSlotsSubclass extends SignalsAndSlots
{
    public int java_slot2_called = 0;
    public int java_slot3_2_called = 0;
    public int java_slot4_called = 0;
    
    public Signal0 signal4;
    
    public int signal1_notified = 0;
    public int signal2_notified = 0;
    public int signal3_notified = 0;
    public int signal4_notified = 0;
    
    public int d_signal1_notified = 0;
    public int d_signal2_notified = 0;
    public int d_signal3_notified = 0;
    public int d_signal4_notified = 0;
    public int d_null_notified = 0;
    
    public void emit_signal_4() { signal4.emit(); }
    
    public void slot4() { java_slot4_called++; }
    
    protected void disconnectNotify(AbstractSignal signal)
    {
        if (signal == signal1)
            d_signal1_notified ++;
        else if (signal == signal2)
            d_signal2_notified ++;
        else if (signal == signal3)
            d_signal3_notified ++;
        else if (signal == signal4)
            d_signal4_notified ++;
        else if (signal == null)
            d_null_notified ++;
    }
    
    protected void connectNotify(AbstractSignal signal)
    {
        if (signal == signal1)
            signal1_notified ++;
        else if (signal == signal2)
            signal2_notified ++;
        else if (signal == signal3)
            signal3_notified ++;
        else if (signal == signal4)
            signal4_notified ++;        
    }
    
    public void slot2(int i) 
    {
        java_slot2_called += i * 2;
        super.slot2(3);
    }
    
    public void slot3_2(String k)
    {
        java_slot3_2_called += Integer.parseInt(k);
    }
}

public class TestConnections extends QTestCase implements Qt
{    
    public TestConnections()
    {
    }
    
    public void run_CppSignals()
    {
        {
            SignalsAndSlots obj1 = new SignalsAndSlots();            
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 0);
            QCOMPARE(obj1.get_slot3_called(), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 0);
        
            SignalsAndSlots obj2 = new SignalsAndSlots();
            QCOMPARE(obj2.get_slot1_1_called(), 0);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            QCOMPARE(obj2.get_slot2_called(), 0);
            QCOMPARE(obj2.get_slot3_called(), 0);
            QCOMPARE(Accessor.access_receivers(obj2.signal1), 0);
            QCOMPARE(Accessor.access_receivers(obj2.signal2), 0);
            QCOMPARE(Accessor.access_receivers(obj2.signal3), 0);
        
            obj1.setupSignals(obj2, 0);
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 0);
            QCOMPARE(obj1.get_slot3_called(), 0);
            QCOMPARE(obj2.get_slot1_1_called(), 0);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            QCOMPARE(obj2.get_slot2_called(), 0);
            QCOMPARE(obj2.get_slot3_called(), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 1);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 1);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 1);
            QCOMPARE(Accessor.access_receivers(obj2.signal1), 0);
            QCOMPARE(Accessor.access_receivers(obj2.signal2), 0);
            QCOMPARE(Accessor.access_receivers(obj2.signal3), 0);
            
            SignalsAndSlots obj3 = new SignalsAndSlots();
            obj1.setupSignals(obj3, 1);
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 0);
            QCOMPARE(obj1.get_slot3_called(), 0);
            QCOMPARE(obj3.get_slot1_1_called(), 0);
            QCOMPARE(obj3.get_slot1_2_called(), 0);
            QCOMPARE(obj3.get_slot1_3_called(), 0);
            QCOMPARE(obj3.get_slot2_called(), 0);
            QCOMPARE(obj3.get_slot3_called(), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 2);
            QCOMPARE(Accessor.access_receivers(obj3.signal1), 0);
            QCOMPARE(Accessor.access_receivers(obj3.signal2), 0);
            QCOMPARE(Accessor.access_receivers(obj3.signal3), 0);
            
            QVERIFY(obj1.signal1.connect(obj1, "slot1_1()"));
            QVERIFY(obj1.signal1.connect(obj1, "slot1_2()"));
            QVERIFY(obj1.signal1.connect(obj1, "slot1_3()"));
            QVERIFY(obj1.signal2.connect(obj1, "slot2(int)"));
            QVERIFY(obj1.signal3.connect(obj1, "slot3(String)"));
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 0);
            QCOMPARE(obj1.get_slot3_called(), 0);
            QCOMPARE(obj2.get_slot1_1_called(), 0);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            QCOMPARE(obj2.get_slot2_called(), 0);
            QCOMPARE(obj2.get_slot3_called(), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 6);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);

            Accessor.emit_signal(obj1.signal1);
            QCOMPARE(obj1.get_slot1_1_called(), 1);
            QCOMPARE(obj1.get_slot1_2_called(), 1);
            QCOMPARE(obj1.get_slot1_3_called(), 1);

            QCOMPARE(obj2.get_slot1_1_called(), 1);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            
            QCOMPARE(obj3.get_slot1_1_called(), 1);
            QCOMPARE(obj3.get_slot1_2_called(), 1);
            QCOMPARE(obj3.get_slot1_3_called(), 0);
            
            Accessor.emit_signal(obj1.signal1);
            QCOMPARE(obj1.get_slot1_1_called(), 2);
            QCOMPARE(obj1.get_slot1_2_called(), 2);
            QCOMPARE(obj1.get_slot1_3_called(), 2);

            QCOMPARE(obj2.get_slot1_1_called(), 2);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            
            QCOMPARE(obj3.get_slot1_1_called(), 2);
            QCOMPARE(obj3.get_slot1_2_called(), 2);
            QCOMPARE(obj3.get_slot1_3_called(), 0);
            
            QVERIFY(obj1.signal1.connect(obj2, "slot1_1()"));
            QVERIFY(obj1.signal1.connect(obj1, "slot1_2()"));
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 8);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);
            
            Accessor.emit_signal(obj1.signal1);
            QCOMPARE(obj1.get_slot1_1_called(), 3);
            QCOMPARE(obj1.get_slot1_2_called(), 4);
            QCOMPARE(obj1.get_slot1_3_called(), 3);

            QCOMPARE(obj2.get_slot1_1_called(), 4);
            QCOMPARE(obj2.get_slot1_2_called(), 0);
            QCOMPARE(obj2.get_slot1_3_called(), 0);
            
            QCOMPARE(obj3.get_slot1_1_called(), 3);
            QCOMPARE(obj3.get_slot1_2_called(), 3);
            QCOMPARE(obj3.get_slot1_3_called(), 0);
            
            obj1.dispose();
            obj2.dispose();
            obj3.dispose();
        }
        
        {
            SignalsAndSlots obj1 = new SignalsAndSlots();
            obj1.setupSignals(obj1, 2);

            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 1);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 1);
            
            QVERIFY(obj1.signal3.connect(obj1, "slot3(String)"));

            Accessor.emit_signal(obj1.signal2, 15);
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 15);
            QCOMPARE(obj1.get_slot3_called(), 0);
            
            obj1.emit_signal_2(20);
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 35);
            QCOMPARE(obj1.get_slot3_called(), 0);
            
            obj1.emit_signal_3("11");
            QCOMPARE(obj1.get_slot1_1_called(), 0);
            QCOMPARE(obj1.get_slot1_2_called(), 0);
            QCOMPARE(obj1.get_slot1_3_called(), 0);
            QCOMPARE(obj1.get_slot2_called(), 35);
            QCOMPARE(obj1.get_slot3_called(), 22);
            
            obj1.dispose();
        }
        
        {
            SignalsAndSlotsSubclass obj1 = new SignalsAndSlotsSubclass();
            QCOMPARE(obj1.signal1_notified, 0);
            QCOMPARE(obj1.signal2_notified, 0);
            QCOMPARE(obj1.signal3_notified, 0);
            QCOMPARE(obj1.signal4_notified, 0);
            QCOMPARE(obj1.d_signal1_notified, 0);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
                
            
            QVERIFY(obj1.signal2.connect(obj1, "slot2(int)"));
            QCOMPARE(obj1.signal1_notified, 0);
            QCOMPARE(obj1.signal2_notified, 1);
            QCOMPARE(obj1.signal3_notified, 0);
            QCOMPARE(obj1.signal4_notified, 0);
            QCOMPARE(obj1.d_signal1_notified, 0);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
            
            obj1.setupSignals(obj1, 2);
            QCOMPARE(obj1.signal1_notified, 3);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 1);
            QCOMPARE(obj1.signal4_notified, 0);
            QCOMPARE(obj1.d_signal1_notified, 0);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);

            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 1);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 0);
            
            obj1.emit_signal_2(13);
            QCOMPARE(obj1.java_slot2_called, 52);
            QCOMPARE(obj1.get_slot2_called(), 6);
            
            Accessor.emit_signal(obj1.signal2, 4);
            QCOMPARE(obj1.java_slot2_called, 68);
            QCOMPARE(obj1.get_slot2_called(), 12);
            
            QVERIFY(obj1.signal3.connect(obj1, "slot3_2(String)"));
            QVERIFY(obj1.signal3.connect(obj1, "slot3_2(String)"));
            QCOMPARE(obj1.signal1_notified, 3);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 0);
            QCOMPARE(obj1.d_signal1_notified, 0);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
            
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 0);
            
            obj1.emit_signal_3("17");
            QCOMPARE(obj1.java_slot3_2_called, 34);
            QCOMPARE(obj1.get_slot3_called(), 17);
            
            QVERIFY(obj1.signal4.connect(obj1, "slot4()"));
            QVERIFY(obj1.signal1.connect(obj1.signal4));
            QCOMPARE(obj1.signal1_notified, 4);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 1);
            QCOMPARE(obj1.d_signal1_notified, 0);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
            
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 4);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 1);
            
            obj1.emit_signal_1();
            QCOMPARE(obj1.get_slot1_1_called(), 1);
            QCOMPARE(obj1.get_slot1_2_called(), 1);
            QCOMPARE(obj1.get_slot1_3_called(), 1);
            QCOMPARE(obj1.java_slot4_called, 1);
            
            QVERIFY(obj1.signal1.disconnect(obj1.signal4));
            QCOMPARE(obj1.signal1_notified, 4);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 1);
            QCOMPARE(obj1.d_signal1_notified, 1);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
            
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 1);
            
            QVERIFY(obj1.signal4.connect(obj1.signal1));
            QCOMPARE(obj1.signal1_notified, 4);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 2);
            QCOMPARE(obj1.d_signal1_notified, 1);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 0);
            QCOMPARE(obj1.d_signal4_notified, 0);
            
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 3);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 2);
            
            obj1.emit_signal_4();
            QCOMPARE(obj1.java_slot4_called, 2);
            QCOMPARE(obj1.get_slot1_1_called(), 2);
            QCOMPARE(obj1.get_slot1_2_called(), 2);
            QCOMPARE(obj1.get_slot1_3_called(), 2);
            
            obj1.disconnectSignals(obj1);
            QCOMPARE(obj1.signal1_notified, 4);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 2);
            QCOMPARE(obj1.d_signal1_notified, 2);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 1);
            QCOMPARE(obj1.d_signal4_notified, 0);
            QCOMPARE(obj1.d_null_notified, 0);
            
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 2);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 2);
            
            obj1.emit_signal_1();
            QCOMPARE(obj1.get_slot1_1_called(), 2);
            QCOMPARE(obj1.get_slot1_2_called(), 3);
            QCOMPARE(obj1.get_slot1_3_called(), 3);
            QCOMPARE(obj1.java_slot4_called, 2);
            
            obj1.emit_signal_4();
            QCOMPARE(obj1.get_slot1_1_called(), 2);
            QCOMPARE(obj1.get_slot1_2_called(), 4);
            QCOMPARE(obj1.get_slot1_3_called(), 4);
            QCOMPARE(obj1.java_slot4_called, 3);            
                        
            obj1.disconnectAll();
            System.out.println("Contains commented out tests that will fail currently");
            /* QEXPECT_FAIL("See task 117918");
            QCOMPARE(Accessor.access_receivers(obj1.signal1), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal2), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal3), 0);
            QCOMPARE(Accessor.access_receivers(obj1.signal4), 0);
            QCOMPARE(obj1.signal1_notified, 4);
            QCOMPARE(obj1.signal2_notified, 2);
            QCOMPARE(obj1.signal3_notified, 3);
            QCOMPARE(obj1.signal4_notified, 2);
            QCOMPARE(obj1.d_signal1_notified, 2);
            QCOMPARE(obj1.d_signal2_notified, 0);
            QCOMPARE(obj1.d_signal3_notified, 1);
            QCOMPARE(obj1.d_signal4_notified, 0);
            QCOMPARE(obj1.d_null_notified, 1);
            
            obj1.emit_signal_4();
            obj1.emit_signal_1();
            QCOMPARE(obj1.get_slot1_1_called(), 2);
            QCOMPARE(obj1.get_slot1_2_called(), 4);
            QCOMPARE(obj1.get_slot1_3_called(), 4);
            QCOMPARE(obj1.java_slot4_called, 3);*/            
            
        }
    }
        
    static MyQObject something = null;
    public void data_createDestroy()    
    {
        defineDataStructure(QObject.class, "parent",
                            Integer(), "count");                    

        QObject parent = new QObject();
        something = new MyQObject();
        
        MyQObject objects[] = new MyQObject[1000];
        for (int i=0; i<1000; ++i) {
            objects[i] = new MyQObject(parent);
            objects[i].destroyed.connect(something, "increaseFinalized()");
        }

        MyQObject.finalizedCount = 0;
                
        addDataSet("A lot of objects and widgets", parent, 1000);        
    }
        
    public void run_createDestroy()
    {
        QObject oParent = getParameter("parent");
        int count = (Integer) getParameter("count");
        
        oParent.dispose();       
        System.gc();        
        try {
            Thread.sleep(600);
        } catch (Exception e) { };
        QApplication.processEvents(QEventLoop.DeferredDeletion);
        
        QCOMPARE(MyQObject.finalizedCount, count);
    }
     
    public void run_connectJavaQt()
    {        
        QWidget widget = new QWidget();
        QVERIFY(!widget.isVisible());
        QVERIFY(widget.isEnabled());
        
        MyQObject obj = new MyQObject();        
        QVERIFY(obj.signalMyQObject.connect(widget, "show()"));
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        QVERIFY(obj.signalNoParams.connect(widget, "hide()"));
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        QVERIFY(obj.signalBoolean.connect(widget, "setEnabled(boolean)"));
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        QVERIFY(obj.signalInteger.connect(widget, "close()"));
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        obj.javaSignalMyQObject(obj);                
        QVERIFY(widget.isVisible());
        
        obj.javaSignalboolean(false);               
        QVERIFY(!widget.isEnabled());
        
        obj.javaSignalBoolean(true); // both Boolean-functions emit the signalBoolean signal
        QVERIFY(widget.isEnabled());
        
        obj.javaSignalNoParams();
        QVERIFY(!widget.isVisible());

        QVERIFY(obj.signalMyQObject.disconnect(widget, "show()"));
        QVERIFY(obj.signalNoParams.disconnect(widget, "hide()"));
        QVERIFY(obj.signalBoolean.disconnect(widget, "setEnabled(boolean)"));

        obj.javaSignalboolean(false);
        QVERIFY(widget.isEnabled());
        
        obj.javaSignalboolean(false);
        QVERIFY(widget.isEnabled());
        
        obj.javaSignalMyQObject(obj);
        QVERIFY(!widget.isVisible());                
        
        widget.show();
        obj.javaSignalNoParams();
        QVERIFY(widget.isVisible());                
                
        obj.javaSignalint(10);
        QVERIFY(obj.signalInteger.disconnect(widget, "close()"));
        
        widget = new QWidget();
        QPushButton b1 = new QPushButton(widget);
        QPushButton b2 = new QPushButton(widget);
        QLineEdit le = new QLineEdit(widget);
        QVERIFY(!widget.isVisible());
        QVERIFY(!b1.isVisible());
        QVERIFY(!b2.isVisible());
        QVERIFY(!le.isVisible());
        QVERIFY(widget.isEnabled());
        QVERIFY(b1.isEnabled());
        QVERIFY(b2.isEnabled());
        QVERIFY(le.isEnabled());

        widget.show();
        QVERIFY(widget.isVisible());        
        QVERIFY(b1.isVisible());
        QVERIFY(b2.isVisible());
        QVERIFY(le.isVisible());
        widget.setGeometry(new QRect(200, 300, 400, 500));
        QRect rect = widget.geometry();        
        QCOMPARE(rect.x(), 200);
        QCOMPARE(rect.y(), 300);
        QCOMPARE(rect.width(), 400);
        QCOMPARE(rect.height(), 500);
        QSize test = new QSize();
        QCOMPARE(test.width(), -1);
        QCOMPARE(test.height(), -1);
        
        QSize sz = new QSize(10, 20);
        QCOMPARE(sz.width(), 10);
        QCOMPARE(sz.height(), 20);
        b1.setIconSize(sz);
        QSize sz2 = b1.iconSize();
        QCOMPARE(sz2.width(), sz.width());
        QCOMPARE(sz2.height(), sz.height());
        
        QCOMPARE(obj.signalIntegerReceivers(), 0);
        QVERIFY(obj.signalInteger.connect(b1, "setFocus()"));
        QCOMPARE(obj.signalIntegerReceivers(), 1);
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        QVERIFY(obj.signalNoParams.connect(b2, "setFocus()"));
        QCOMPARE(obj.signalDoubleReceivers(), 0);
        QVERIFY(obj.signalDouble.connect(b1, "hide()"));
        QVERIFY(obj.signalDouble.connect(b2, "hide()"));
        QCOMPARE(obj.signalDoubleReceivers(), 2);
        QVERIFY(obj.signalDoubleInteger.connect(widget, "close()"));
        QVERIFY(obj.signalLong.connect(b1, "show()"));
        QVERIFY(obj.signalLong.connect(b2, "show()"));
        QVERIFY(obj.signalMixed1.connect(widget, "hide()"));
        QCOMPARE(obj.signalStringReceivers(), 0);
        QVERIFY(obj.signalString.connect(le, "setText(String)"));
        QCOMPARE(obj.signalStringReceivers(), 1);
        QVERIFY(le.textChanged.connect(obj, "javaSlotString(String)"));
        QVERIFY(QApplication.instance().focusChanged.connect(obj, "javaSlotFocusChanged(QWidget, QWidget)"));
        QVERIFY(obj.signalQSize.connect(b2, "setIconSize(QSize)"));
                                    
        obj.javaSignalint(123);
        QCOMPARE(QApplication.focusWidget(), b1);
        QVERIFY(QApplication.focusWidget() == b1);
        obj.javaSignalNoParams();
        QCOMPARE(QApplication.focusWidget(), b2);
        QVERIFY(QApplication.focusWidget() == b2);
        QCOMPARE(obj.slotResult, b1); // set by javaSlotFocusChanged()
        QVERIFY(obj.slotResult == b1);
        QCOMPARE(obj.slotResult2, b2); // ditto
        QVERIFY(obj.slotResult2 == b2);
        
        obj.javaSignalDouble(1.0);
        QVERIFY(widget.isVisible());
        QVERIFY(!b1.isVisible());
        QVERIFY(!b2.isVisible());
        QVERIFY(le.isVisible());
        
        obj.javaSignalLong(123L);
        QVERIFY(widget.isVisible());
        QVERIFY(b1.isVisible());
        QVERIFY(b2.isVisible());
        QVERIFY(le.isVisible());     
        
        b1.setText("button 1");
        b2.setText("button 2");
        QCOMPARE(b1.text(), "button 1");
        QCOMPARE(b2.text(), "button 2");
        QVERIFY(!b1.text().equals(b2.text()));   
        
        obj.javaSignalString("Line edit text");
        QCOMPARE(le.text(), "Line edit text");
        QCOMPARE(obj.slotResult, "Line edit text");
        

        QVERIFY(obj.signalString.disconnect(le, "setText(String)"));
        QCOMPARE(obj.signalStringReceivers(), 0);
        QVERIFY(obj.signalString.connect(le, "selectAll()"));
        QCOMPARE(obj.signalStringReceivers(), 1);
        
        obj.javaSignalString("ABC");        
        QCOMPARE(le.text(), "Line edit text");
        QCOMPARE(obj.slotResult, "Line edit text");

        QVERIFY(obj.signalString.disconnect(le, "selectAll()"));
        QCOMPARE(obj.signalStringReceivers(), 0);
        QVERIFY(obj.signalString.connect(le, "cut()"));
        QCOMPARE(obj.signalStringReceivers(), 1);
        
        obj.javaSignalString("DEF");
        QCOMPARE(le.text(), "");
        QCOMPARE(obj.slotResult, "");
        
        QVERIFY(obj.signalString.connect(le, "paste()"));
        QCOMPARE(obj.signalStringReceivers(), 2);
        obj.javaSignalString("GHI");
        QCOMPARE(le.text(), "Line edit text");
        QCOMPARE(obj.slotResult, "Line edit text");        
        
        obj.javaSignalTwoParameters(123.0, 456); // the signalDoubleInteger signal
        QVERIFY(!widget.isVisible());
        QVERIFY(!b1.isVisible());
        QVERIFY(!b2.isVisible());                        
                
        sz = new QSize(40, 60);
        QCOMPARE(sz.width(), 40);
        QCOMPARE(sz.height(), 60);
        obj.javaSignalQSize(sz);
        
        QSize sz3 = b2.iconSize();
        QCOMPARE(sz3.width(), sz.width());
        QCOMPARE(sz3.height(), sz.height());
        
        {
            obj = new MyQObject();
            MyQObject obj2 = new MyQObject();
        
            QVERIFY(obj.signalBoolean.connect(obj2.signalBoolean));
            QVERIFY(obj2.signalBoolean.connect(obj, "javaSlotboolean(boolean)"));
            
            obj.javaSignalboolean(false);
            QVERIFY(obj.slotResult instanceof Boolean);
            QCOMPARE((Boolean) obj.slotResult, false);
            
            obj.javaSignalboolean(true);
            QVERIFY(obj.slotResult instanceof Boolean);
            QCOMPARE((Boolean) obj.slotResult, true);      
            
            obj.blockSignals(true);
            obj.javaSignalboolean(false);
            QVERIFY(obj.slotResult instanceof Boolean);
            QCOMPARE((Boolean) obj.slotResult, true);      
    
            obj.blockSignals(false);
            obj.javaSignalboolean(false);
            QVERIFY(obj.slotResult instanceof Boolean);
            QCOMPARE((Boolean) obj.slotResult, false);      
            
            obj2.blockSignals(true);
            obj.javaSignalboolean(true);
            QVERIFY(obj.slotResult instanceof Boolean);
            QCOMPARE((Boolean) obj.slotResult, false);            
        }
    }
         
     public void run_javaToJavaConnect()
     {
        String signalFunctionName = getParameter("signalFunctionName");
        Object signal = getParameter("signal");
        MyQObject sender = getParameter("sender");
        String slot = getParameter("slot");
        MyQObject receiver = getParameter("receiver");
        Object[] parameters = getParameter("parameters");
        Object expectedSlotResult = getParameter("expectedSlotResult");
        Boolean checkReturnedReference = getParameter("checkReturnedReference");
        Class[] parameterTypes = getParameter("parameterTypes");
        
        QVERIFY(((QObject.AbstractSignal)signal).connect(receiver, slot));
        if (parameters == null)
            parameters = new Object[0];
        
        try {                
            Method m = sender.getClass().getMethod(signalFunctionName, parameterTypes);
            m.invoke(sender, parameters);
        } catch (Throwable e) {
            
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException)e;
                e = ite.getTargetException();
            }
                                    
            QFAIL(e.toString());
        }
        
        QCOMPARE(expectedSlotResult, receiver.slotResult);        
        QVERIFY(!checkReturnedReference.booleanValue() || receiver.slotResult == expectedSlotResult);        
     }
     
     public void data_javaToJavaConnect()
     {               
        defineDataStructure(MyQObject.class, "sender",
                            Object.class, "signal",
                            String.class, "signalFunctionName",
                            MyQObject.class, "receiver",
                            String.class, "slot",
                            ObjectArray(), "parameters",
                            Object.class, "expectedSlotResult",
                            Boolean.class, "checkReturnedReference",
                            ClassArray(), "parameterTypes");
        
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = Long();               
        
        Object[] params = new Object[1];
        params[0] = Long.MAX_VALUE;        
        
        MyQObject sender = new MyQObject();
        MyQObject receiver = new MyQObject();
        addDataSet("Long signal/slot MAX_VALUE", sender, sender.signalLong, "javaSignalLong",
                   receiver, "javaSlotLong(Long)", params, params[0], true, parameterTypes);

        params = new Object[1];
        params[0] = Long.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Long signal/slot MIN_VALUE", sender, sender.signalLong, "javaSignalLong",
                receiver, "javaSlotLong(Long)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Integer();

        params = new Object[1];            
        params[0] = Integer.MAX_VALUE;        
        addDataSet("Integer signal/slot MAX_VALUE", sender, sender.signalInteger, "javaSignalInteger",
                   receiver, "javaSlotInteger(Integer)", params, params[0], true, parameterTypes); 

        params = new Object[1];            
        params[0] = Integer.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Integer signal/slot MIN_VALUE", sender, sender.signalInteger, "javaSignalInteger",
                receiver, "javaSlotInteger(Integer)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Boolean();
        
        params = new Object[1];            
        params[0] = true;
        addDataSet("Boolean signal/slot true", sender, sender.signalBoolean, "javaSignalBoolean",
                receiver, "javaSlotBoolean(Boolean)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = false;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Boolean signal/slot false", sender, sender.signalBoolean, "javaSignalBoolean",
                receiver, "javaSlotBoolean(Boolean)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Double();
                              
        params = new Object[1];            
        params[0] = Double.MAX_VALUE;
        addDataSet("Double signal/slot MAX_VALUE", sender, sender.signalDouble, "javaSignalDouble",
                receiver, "javaSlotDouble(Double)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = Double.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Double signal/slot MIN_VALUE", sender, sender.signalDouble, "javaSignalDouble",
                receiver, "javaSlotDouble(Double)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Short();
            
        params = new Object[1];            
        params[0] = Short.MAX_VALUE;
        addDataSet("Short signal/slot MAX_VALUE", sender, sender.signalShort, "javaSignalShort",
                receiver, "javaSlotShort(Short)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = Short.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Short signal/slot MIN_VALUE", sender, sender.signalShort, "javaSignalShort",
                receiver, "javaSlotShort(Short)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Float();

        params = new Object[1];            
        params[0] = Float.MAX_VALUE;
        addDataSet("Float signal/slot MAX_VALUE", sender, sender.signalFloat, "javaSignalFloat",
                receiver, "javaSlotFloat(Float)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = Float.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Float signal/slot MIN_VALUE", sender, sender.signalFloat, "javaSignalFloat",
                receiver, "javaSlotFloat(Float)", params, params[0], true, parameterTypes);

        parameterTypes = new Class[1];
        parameterTypes[0] = Character();

        params = new Object[1];            
        params[0] = Character.MAX_VALUE;        
        addDataSet("Character signal/slot MAX_VALUE", sender, sender.signalCharacter, "javaSignalCharacter",
                receiver, "javaSlotCharacter(Character)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = Character.MIN_VALUE;
        sender = new MyQObject();
        receiver = new MyQObject();
        addDataSet("Character signal/slot MIN_VALUE", sender, sender.signalCharacter, "javaSignalCharacter",
                receiver, "javaSlotCharacter(java.lang.Character)", params, params[0], true, parameterTypes);

        params = new Object[1];            
        params[0] = new MyQObject();
         parameterTypes = new Class[1];
        parameterTypes[0] = params[0].getClass();
        addDataSet("MyQObject signal/slot", sender, sender.signalMyQObject, "javaSignalMyQObject",
                receiver, "javaSlotMyQObject(MyQObject)", params, params[0], true, parameterTypes);
            
       // Signal has more parameters than slot
       parameterTypes = new Class[2];
       parameterTypes[0] = _double();
       parameterTypes[1] = _int();
       params = new Object[2];
       params[0] = Double.MIN_VALUE;
       params[1] = Integer.MAX_VALUE;       
       addDataSet("Signal two parameters/slot one parameter",
                  sender, sender.signalDoubleInteger, "javaSignalTwoParameters",
                  receiver, "javaSlotdouble(double)", params, params[0], false, parameterTypes);
                   
       // No parameters
       parameterTypes = new Class[0];
       String s1 = "Signal no params";                   
       String s2 = "Slot no params";
       sender = new MyQObject();
       receiver = new MyQObject();
       addDataSet("No parameters, void returning signal/slot",
                  sender, sender.signalNoParams, "javaSignalNoParams",
                  receiver, "javaSlotNoParams()", null, "Slot no params", false, parameterTypes);
                                     
       // Mixed parameter types (PODs and complex types)
       parameterTypes = new Class[4];
       parameterTypes[0] = String();
       parameterTypes[1] = _double();
       parameterTypes[2] = _int();
       parameterTypes[3] = Integer();       
       params = new Object[4];
       params[0] = "A String";
       params[1] = Double.MAX_VALUE;
       params[2] = Integer.MAX_VALUE;
       params[3] = Integer.MAX_VALUE;             
       addDataSet("Mixed parameters, returning Integer",
                  sender, sender.signalMixed1, "javaSignalMixed1",
                  receiver, "javaSlotMixed1(String, double, int, Integer)", params, params[0], true, parameterTypes);
       
       // QObject array
       parameterTypes = new Class[1];
       parameterTypes[0] = QObjectArray();
       params = new Object[1];
       QObject array[] = new QObject[10];
       for (int i=0; i<10; ++i)
           array[i] = new QObject(sender);
       params[0] = array;
       addDataSet("QObject array", sender, sender.signalQObjectArray, "javaSignalQObjectArray",
               receiver, "javaSlotQObjectArray(QObject [])", params, params[0], true, parameterTypes);
       
       // Many arrays
       parameterTypes = new Class[3];       
       Integer[][][] param = new Integer[1][1][1];              
       parameterTypes[0] = param[0][0].getClass();
       parameterTypes[1] = param[0].getClass();
       parameterTypes[2] = param.getClass();
       params = new Object[3];
       params[0] = param[0][0];
       params[1] = param[0];
       params[2] = param;
       addDataSet("Many integer arrays", sender, sender.signalManyArrays, "javaSignalManyArrays",
               receiver, "javaSlotManyArrays(java.lang.Integer[], Integer [] [], Integer[]   [] [    ])", params, param, true, parameterTypes);
       
       // Signal -> Signal
       parameterTypes = new Class[1];
       parameterTypes[0] = Integer.class;
       params = new Object[1];
       params[0] = new Integer(654);
       sender = new MyQObject();
       receiver = new MyQObject();
       sender.signalInteger.connect(receiver.signalInteger2);
       addDataSet("Signal to signal to slot", sender, receiver.signalInteger2, "javaSignalInteger",
               receiver, "javaSlotInteger(Integer)", params, params[0], true, parameterTypes);
       
       // MyQObject -> QObject
       parameterTypes = new Class[1];
       parameterTypes[0] = MyQObject.class;
       params = new Object[1];
       params[0] = new MyQObject();
       sender = new MyQObject();
       receiver = new MyQObject();
       addDataSet("MyQObject -> QObject cast", sender, sender.signalMyQObject, "javaSignalMyQObject", 
               receiver, "javaSlotQObject(QObject)", params, params[0], true, parameterTypes);
       
       // List<String>
       parameterTypes = new Class[1];
       parameterTypes[0] = List.class;
       params = new Object[1];
       
       LinkedList<String> stringList = new LinkedList<String>();
       stringList.add("You got you got you got what I need");
       params[0] = stringList;
       addDataSet("String list parameter", sender, sender.signalStringList, "javaSignalStringList",
               receiver, "javaSlotStringList(List)", params, stringList.get(0), true, parameterTypes);
       
       // List<List<String>>
       parameterTypes = new Class[1];
       parameterTypes[0] = List.class;
       List<LinkedList<String>> stringListList = new LinkedList<LinkedList<String>>();
       stringList.add("Now shake that thang");
       stringListList.add(stringList);
       params = new Object[1];
       params[0] = stringListList;
       addDataSet("List of list of String", sender, sender.signalStringListList, "javaSignalStringListList",
               receiver, "javaSlotStringListList(List)", params, stringList.get(1), true, parameterTypes);       
     }
     
     public void run_borkedConnections()
     {
         MyQObject sender = getParameter("sender");
         QObject receiver = getParameter("receiver");
         Object signal = getParameter("signal");
         String slotSignature = getParameter("slotSignature");
         Class<?> expectedExceptionType = getParameter("expectedExceptionType");
         String expectedExceptionMessage = getParameter("expectedExceptionMessage");
         
         Class<?> ce = null;
         String msg = null;
         try {
             boolean b = ((QObject.AbstractSignal)signal).connect(receiver, slotSignature);
             QCOMPARE(b, false);
         } catch (Exception e) {
             if (e instanceof QTestException) {
                 throw (QTestException)e;
             } else {
                 ce = e.getClass();
                 msg = e.getMessage();
             }
         }
         QCOMPARE(expectedExceptionType, ce);
         if (expectedExceptionMessage != null)
             QCOMPARE(msg.substring(0, expectedExceptionMessage.length()), expectedExceptionMessage);             
     }
     
     public void data_borkedConnections()
     {
         defineDataStructure(MyQObject.class, "sender",
             Object.class, "signal",
             QObject.class, "receiver",
             String.class, "slotSignature",
             Class.class, "expectedExceptionType",
             String.class, "expectedExceptionMessage"); 
         
         MyQObject sender = new MyQObject();
         MyQObject receiver = new MyQObject(null);
         
         addDataSet("Slot signature with return type", sender, sender.signalBoolean,
                    receiver, "Boolean javaSlotBoolean(Boolean)", 
                    RuntimeException.class, "Do not specify return type in slot signature");
                 
         addDataSet("Slot signature with missing end parenthesis", sender, sender.signalBoolean, 
                    receiver, "javaSlotBoolean(Boolean", 
                    RuntimeException.class, "Wrong syntax in slot signature");

         addDataSet("Slot signature with unknown argument type", sender, sender.signalBoolean,
                 receiver, "javaSlotBoolean(ean)", 
                 QNoSuchSlotException.class, null);
         
         addDataSet("Slot signature with unknown slot name", sender, sender.signalBoolean, 
                    receiver, "javaSlotBool(Boolean)", QNoSuchSlotException.class, null);
         
         addDataSet("Null signal", sender, null, receiver, "javaSlotBoolean(Boolean)",
                 NullPointerException.class, null);
         
         addDataSet("Null slot signature", sender, sender.signalBoolean, receiver, null, NullPointerException.class, null);
         
         addDataSet("Null receiver", sender, sender.signalBoolean, null, "javaSlotBoolean(Boolean)", 
                 NullPointerException.class, null);
         
         addDataSet("Too many slot params", sender, sender.signalBoolean, 
                 receiver, "javaSlotMixed1(String, double, int, Integer)", null, null);
         
         addDataSet("Wrong parameter types, narrowing", sender, sender.signalDouble, 
                 receiver, "javaSlotInteger(Integer)", null, null);
         
         addDataSet("Wrong parameter types, widening", sender, sender.signalInteger,
                 receiver, "javaSlotDouble(Double)", null, null);
     }
     
     
      public void run_queuedConnection() {
    	  MyQObject sender = new MyQObject();
    	  MyQObject receiver = new MyQObject();
    	  
    	  QVERIFY(sender.signalBoolean.connect(receiver, "javaSlotboolean(boolean)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalBoolean.connect(receiver, "javaSlotBoolean(java.lang.Boolean)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalCharacter.connect(receiver, "javaSlotchar(char)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalCharacter.connect(receiver, "javaSlotCharacter(java.lang.Character)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalShort.connect(receiver, "javaSlotshort(short)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalShort.connect(receiver, "javaSlotShort(java.lang.Short)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalInteger.connect(receiver, "javaSlotint(int)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalInteger.connect(receiver, "javaSlotInteger(java.lang.Integer)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalLong.connect(receiver, "javaSlotlong(long)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalLong.connect(receiver, "javaSlotLong(java.lang.Long)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalFloat.connect(receiver, "javaSlotfloat(float)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalFloat.connect(receiver, "javaSlotFloat(java.lang.Float)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalDouble.connect(receiver, "javaSlotdouble(double)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalDouble.connect(receiver, "javaSlotDouble(java.lang.Double)", Qt.QueuedConnection));
    	  QVERIFY(sender.signalString.connect(receiver, "javaSlotString(java.lang.String)", Qt.QueuedConnection));

    	  // Boolean
    	  receiver.slotResult = null;
    	  sender.javaSignalboolean(true); 
    	  QCOMPARE(receiver.slotResult, null);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Boolean(true));    	  
    	  sender.javaSignalBoolean(false);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Boolean(false));
    	  
    	  // Byte ?
    	  
    	  // Character
    	  sender.javaSignalchar('x');
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Character('x'));
    	  sender.javaSignalCharacter(new Character('y'));
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Character('y'));
    	  
    	  // Shorts
    	  sender.javaSignalShort((short) 40);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Short((short) 40));
    	  sender.javaSignalShort((short) 41);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Short((short) 41));

    	  // Integer
    	  sender.javaSignalint(42);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Integer(42));
    	  sender.javaSignalInteger(43);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Integer(43));

    	  // Long
    	  sender.javaSignalLong((long)44);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Long(44));
    	  sender.javaSignalLong((long) 45);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Long(45));
    	  
    	  // Float
    	  sender.javaSignalFloat((float) 3.14);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Float(3.14));
    	  sender.javaSignalFloat((float) 3.15);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Float(3.15));
    	  
    	  // Double
    	  sender.javaSignalDouble(3.16);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Double(3.16));
    	  sender.javaSignalDouble(3.17);
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, new Double(3.17));
    	  
    	  // Strings 
    	  sender.javaSignalString("once upon a time...");
    	  QCoreApplication.processEvents();
    	  QCOMPARE(receiver.slotResult, "once upon a time...");
      }
     
     
     public static void main(String args[])
     {
         QApplication app = new QApplication(args);
         runTest(new TestConnections());
     }
}
