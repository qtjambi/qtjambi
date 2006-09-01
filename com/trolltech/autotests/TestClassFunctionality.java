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
import com.trolltech.qtest.QTestCase;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class GeneralObject
{
    public String data;
}

class CustomEvent extends QEvent
{
    public String s;
    public CustomEvent(String param) { super(QEvent.User + 16); s = param; }    
}

class MyModel extends QStandardItemModel
{
    public Signal0 mySignal;
    public void mySlot() { }
    
    public boolean b_dataChangedNotified = false;
    public boolean b_mySignalNotified = false;
    public boolean b_error = false;
    
    protected void connectNotify(AbstractSignal signal)
    {
        b_dataChangedNotified = b_dataChangedNotified || signal == dataChanged;
        b_mySignalNotified = b_mySignalNotified || signal == mySignal;
    }
    
    protected void disconnectNotify(AbstractSignal signal)
    {
        if (signal == dataChanged && b_dataChangedNotified)
            b_dataChangedNotified = false;
        else if (signal == dataChanged)
            b_error = true;
        
        if (signal == mySignal && b_mySignalNotified)
            b_mySignalNotified = false;
        else if (signal == mySignal)
            b_error = true;
    }
}

class EventReceiver extends QWidget
{
    public String myString = null;
    public String customEventString = null;
    public QSize resizeEventSize = null;
    public QSize resizeEventOldSize = null;
    public int customEventType = 0;
    public int resizeEventType = 0;
    public int paintEventType = 0;
    public boolean paintEventCastWorked = false;
    public boolean paintRectMatched = false;
    
    public EventReceiver(QWidget parent, String str) { super(parent); myString = str; }
    
    public boolean event(QEvent event)
    {
        if (event instanceof QResizeEvent) {
            QResizeEvent rs = (QResizeEvent) event;
            resizeEventType = event.type();
            resizeEventSize = rs.size();
            resizeEventOldSize = rs.oldSize();
        } else if (event instanceof CustomEvent) {
            CustomEvent ce = (CustomEvent) event;
            customEventType = event.type();
            customEventString = ce.s;
        } else if (event.type() == QEvent.Paint) {
            QtObject new_event = null;
            try {
                new_event = QtObject.reassignNativeResources(event, QPaintEvent.class);                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            if (new_event != null && new_event instanceof QPaintEvent) {
                paintEventCastWorked = true;
                QPaintEvent paintEvent = (QPaintEvent) new_event;
                paintEventType = paintEvent.type();
                QRect paintRect = paintEvent.rect();
                paintRectMatched = paintRect.width() == width();
                paintRectMatched = paintRectMatched && paintRect.height() == height();
            }               
        }
        
        return false;
    }
}

class Accessor extends QObject
{
    public static int access_receivers(AbstractSignal signal)
    {
        return receivers(signal);
    }
    
    public static void emit_signal(AbstractSignal signal, Object ... args)
    {        
        if (signal instanceof Signal0)
            ((Signal0) signal).emit();
        else if (signal instanceof Signal1)
            ((Signal1) signal).emit(args[0]);
        else 
            throw new RuntimeException("Implement more classes");
    }
}

public class TestClassFunctionality extends QTestCase 
{    
    public static void main(String unused_args[])
    {
        String args[] = new String[3];
        args[0] = "A";
        args[1] = "B";
        args[2] = "C";
        QApplication app = new QApplication(args);
        
        runTest(new TestClassFunctionality());
    }
    
    
    /*-------------------------------------------------------------------------
     * Test that QObject.sender() returns something valid during
     * a signal emittion...
     */
    private static class SenderTester extends QObject {
        Signal0 signal = new Signal0();
        public boolean is_null, is_valid;
        public QTime timeouted;
        public long msec = 0L;
        
        public SenderTester() { timeouted = new QTime(); timeouted.start(); } 
        
        public void checkSender() { 
            is_null = QtJambiInternal.sender(this) == null; 
            is_valid = QtJambiInternal.sender(this) == this; 
        }
        public void emitSignal() { signal.emit(); }
        
        void timeoutSlot() { msec = timeouted.elapsed(); }   
    }    
    
    public void run_senderNotNull() {
        SenderTester tester = new SenderTester();
        tester.signal.connect(tester, "checkSender()");
        
        tester.emitSignal();
        
        QVERIFY(!tester.is_null);
        QVERIFY(tester.is_valid);
    }
    
    
    public void run_cppAndJavaObjects()
    {
        CustomEvent event1 = new CustomEvent("this is my stuff");
        QResizeEvent event2 = new QResizeEvent(new QSize(101, 102), new QSize(103, 104));
        
        QWidget parentWidget = new QWidget(null);
        EventReceiver someQObject = new EventReceiver(parentWidget, "some stuff");
                
        List<QObject> children = parentWidget.children();
        QCOMPARE(children.size(), 1);
        
        QObject child = children.get(0);
        QVERIFY(child != null);
        QVERIFY(child instanceof EventReceiver);
        QVERIFY(someQObject == child);
        
        
        EventReceiver receiver = (EventReceiver) child;
        QVERIFY(someQObject == receiver);
        QCOMPARE(receiver.myString, "some stuff");
        QVERIFY(receiver.parent() == parentWidget);
        
        QApplication.postEvent(receiver, event1);
        QApplication.postEvent(someQObject, event2);
        parentWidget.show();

        QApplication.processEvents();
        
        QCOMPARE(receiver.customEventString, "this is my stuff");
        QCOMPARE(receiver.customEventType, QEvent.User + 16);
        QCOMPARE(receiver.resizeEventType, QEvent.Resize);
        QCOMPARE(receiver.resizeEventSize.width(), 101);
        QCOMPARE(receiver.resizeEventSize.height(), 102);
        QCOMPARE(receiver.resizeEventOldSize.width(), 103);
        QCOMPARE(receiver.resizeEventOldSize.height(), 104);
        QVERIFY(receiver.paintEventCastWorked);
        QVERIFY(receiver.paintRectMatched);
        QCOMPARE(receiver.paintEventType, QEvent.Paint);
        
        String[] expected = {
                "com.trolltech.qt.gui.QSizeGrip", 
                "com.trolltech.qt.gui.QGridLayout",
                "com.trolltech.qt.gui.QDirModel",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QListView",
                "com.trolltech.qt.gui.QTreeView",
                "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QLabel",
                "com.trolltech.qt.gui.QLabel",
                "com.trolltech.qt.gui.QLabel",
                "com.trolltech.qt.gui.QDialogButtonBox",
                "com.trolltech.qt.gui.QComboBox",
                "com.trolltech.qt.gui.QLineEdit",
                "com.trolltech.qt.gui.QComboBox" };
                                
        QFileDialog d = new QFileDialog();
        children = d.children();
                
        QCOMPARE(children.size(), expected.length);
        
        int i = 0;
        for (QObject c : children) {
            QCOMPARE(c.getClass().getName(), expected[i++]);
            
            // Test one of them with instanceof, just to be on the safe side
            if (i == 25) {
                QVERIFY(c instanceof QLineEdit);
                
                QLineEdit le = (QLineEdit) c;
                QVERIFY(le.editingFinished != null);
                        
                QCOMPARE(Accessor.access_receivers(le.returnPressed), 1);
            }
        }
        
        QWidget some_widget = new QWidget();
        {
        	List<QAction> actions = new ArrayList<QAction>();        
        	actions.add(new QAction("bite", d));
        	actions.add(new QAction("me", d));
        
        	some_widget.addActions(actions);
        }
        
        {
        	List<QAction> read_back = some_widget.actions();
    		
        	QCOMPARE(read_back.size(), 2);
        	QCOMPARE(read_back.get(0).text(), "bite");
        	QCOMPARE(read_back.get(1).text(), "me");
        }
        some_widget.dispose();        
    }
    
    public void run_injectedCode()
    {
        QObject obj = new QObject();
        QAction act = new QAction(obj);
        act.setShortcut("Ctrl+A");
        QKeySequence seq = act.shortcut();
        
        QCOMPARE(seq.count(), 1);
        QCOMPARE(seq.operator_subscript(0), Qt.CTRL | Qt.Key_A);
        
        // The result can be checked in the resulting "tmp__testclass_func_result.png" file 
        QPixmap pm = new QPixmap(100, 100);
        pm.fill(QColor.blue);
        
        QPainter p = new QPainter();
        p.begin(pm);
        p.setPen(new QPen(QColor.red));
        p.setBrush(new QBrush(QColor.green));
        
        QRect rects[] = new QRect[2];
        rects[0] = new QRect(0, 0, 10, 10);
        rects[1] = new QRect(90, 90, 10, 10);
        p.drawRects(rects);
        
        QRectF rectfs[] = new QRectF[1];
        rectfs[0] = new QRectF(10.1, 10.1, 10.1, 10.1);
        p.drawRects(rectfs);
        
        QPoint points[] = new QPoint[4];
        points[0] = new QPoint(45, 45);
        points[1] = new QPoint(55, 45);
        points[2] = new QPoint(55, 55);
        points[3] = new QPoint(45, 55);
        p.drawPolygon(points);
        
        QLineF linefs[] = new QLineF[2];
        linefs[0] = new QLineF(100, 0, 90, 10);
        linefs[1] = new QLineF(90, 10, 100, 20);
        p.drawLines(linefs);
        p.end();
        
        pm.save("tmp__testclass_func_result.png", "PNG");
        
        SenderTester tester = new SenderTester();
        QTimer.singleShot(1000, tester, "timeoutSlot()");
        
        try { 
            while (tester.timeouted.elapsed() < 1500) {
                QApplication.processEvents();
            }
        } catch (Exception e) { QVERIFY(false); }
        
        QVERIFY(tester.msec >= 1000);
        QVERIFY(tester.msec <= 1500);        
    }
    
    public void run_copyConstructor()
    {
        QFileInfo file1 = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        QVERIFY(file1.exists());
        
        QFileInfo file2 = new QFileInfo(file1);
        
        QCOMPARE(file2.size(), file1.size());
        QCOMPARE(file2.exists(), file1.exists());
        QCOMPARE(file2.fileName(), file1.fileName());
    }
    
    public void run_connectNotify()
    {
        MyModel model = new MyModel();
        QVERIFY(!model.b_error);
        QVERIFY(!model.b_dataChangedNotified);
        QVERIFY(!model.b_mySignalNotified);
       
        model.mySignal.connect(model, "mySlot()");
        QVERIFY(!model.b_error);
        QVERIFY(!model.b_dataChangedNotified);
        QVERIFY(model.b_mySignalNotified);
        
        
        QListView v = new QListView();
        v.setModel(model);
        QVERIFY(!model.b_error);
        QVERIFY(model.b_dataChangedNotified);
        QVERIFY(model.b_mySignalNotified);

        model.mySignal.disconnect(model, "mySlot()");
        QVERIFY(!model.b_error);
        QVERIFY(model.b_dataChangedNotified);
        QVERIFY(!model.b_mySignalNotified);
        
        v.dispose();        
        QVERIFY(!model.b_error);
        QVERIFY(!model.b_dataChangedNotified);
        QVERIFY(!model.b_mySignalNotified);        
                               
        model.dispose();                    
    }
    
    public void run_settersAndGetters()
    {
        QStyleOptionButton so = new QStyleOptionButton();
        
        QVERIFY(so.icon().isNull());
        
        QPixmap pm = new QPixmap(100, 100);
        pm.fill(QColor.red);
        QIcon icon = new QIcon(pm);
        so.setIcon(icon);
        so.setText("A travelling salesman walks into a bar");
        
        QCOMPARE(so.text(), "A travelling salesman walks into a bar");
        QVERIFY(!so.icon().isNull());
                
        so.setIcon(new QIcon());
        QVERIFY(so.icon().isNull());
        
        so.setText("A priest and a rabbi sitting in a bar");
        QCOMPARE(so.text(), "A priest and a rabbi sitting in a bar");
        
        QUuid uuid = new QUuid();
        
        byte b_orig[] = new byte[8];
        b_orig[0] = 2;
        b_orig[1] = 3;
        b_orig[3] = 5;
        b_orig[4] = 7;
        b_orig[5] = 11;
        b_orig[6] = 13;
        b_orig[7] = 17;
        uuid.setData4(b_orig);

        byte b2[] = uuid.data4();
        QCOMPARE(b2.length, 8);        
        for (int i=0; i<8; ++i)
            QCOMPARE(b2[i], b_orig[i]);
        
        // Wrong number of entries
        Exception fe = null; 
        try {
            byte b[] = new byte[6];
            uuid.setData4(b);
        } catch (Exception e) {
            fe = e;
        }
        
        QVERIFY(fe != null);
        QVERIFY(fe instanceof IllegalArgumentException);

        // Make sure it wasn't set after all
        b2 = uuid.data4();
        QCOMPARE(b2.length, 8);        
        for (int i=0; i<8; ++i)
            QCOMPARE(b2[i], b_orig[i]);

        fe = null;
        try {
            byte b[] = new byte[100];
            uuid.setData4(b);
        } catch (Exception e) {
            fe = e;
        }
        
        QVERIFY(fe != null);
        QVERIFY(fe instanceof IllegalArgumentException);
    }
        

    // Tests the ownership transfer that we need to have objects like
    // QEvent stay alive after they are posted to the event queue...
    // The verifecation here is basically that the vm doesn't crash.
    private static class OwnershipTransferReceiver extends QObject {
        public int event_id;

        public QRect rect;

        public boolean event(QEvent e) {
            event_id = e.type();

            QPaintEvent pe = null;
            try {
                pe = (QPaintEvent) e;
                rect = pe.rect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return super.event(e);
        }
    }

    private static class CustomPaintEvent extends QPaintEvent {
        public static boolean finalized = false;

        public CustomPaintEvent(QRect rect) {
            super(rect);
        }

        public void finalize() {
        	super.finalize();
            finalized = true;
        }
    }

    public void run_testOwnershipTranfer() {
        OwnershipTransferReceiver receiver = new OwnershipTransferReceiver();

        for (int i = 0; i < 1; ++i) {
            QRect rect = new QRect(1, 2, 3, 4);
            QApplication.postEvent(receiver, new CustomPaintEvent(rect));
        }

        // To attemt deletion of the QPaintEvent, should not happen at
        // this time...
        System.gc();
        QCOMPARE(CustomPaintEvent.finalized, false);

        // Process the event, thus also deleting it...
        QApplication.processEvents();

        // To provoke collection of the java side of the object, now
        // that C++ has released its hold on it
        System.gc();
        try {
            Thread.sleep(600);
        } catch (Exception e) { };
        
        QCOMPARE(CustomPaintEvent.finalized, true);

        // Sanity check the data...
        QCOMPARE(receiver.event_id, QEvent.Paint);
        QCOMPARE(1, receiver.rect.x());
        QCOMPARE(2, receiver.rect.y());
        QCOMPARE(3, receiver.rect.width());
        QCOMPARE(4, receiver.rect.height());
    }
    
    // Check that const char *[] is handled properly by the generated code 
    public void run_XPMConstructors()
    {
        String qt_plastique_radio[] = 
        { 
            "13 13 2 1",
            "X c #000000",
            ". c #ffffff",
            "....XXXXX....",
            "..XX.....XX..",
            ".X.........X.",
            ".X.........X.",
            "X...........X",
            "X...........X",
            "X...........X",
            "X...........X",
            "X...........X",
            ".X.........X.",
            ".X.........X.",
            "..XX.....XX..",
            "....XXXXX...." 
        };

        QNativePointer p = QNativePointer.createCharPointerPointer(qt_plastique_radio);
        QImage img = new QImage(p);
        QCOMPARE(img.width(), 13);
        QCOMPARE(img.height(), 13);
        
        QCOMPARE(img.pixel(2,1), 0xff000000);
        QCOMPARE(img.pixel(0,0), 0xffffffff);
        
        QPixmap pm = new QPixmap(p);
        QImage img2 = pm.toImage();
        QCOMPARE(img2.pixel(2,1), 0xff000000);
        QCOMPARE(img2.pixel(12,12), 0xffffffff);
    }
}
