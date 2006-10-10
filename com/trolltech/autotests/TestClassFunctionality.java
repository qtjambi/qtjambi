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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.autotests.generated.*;

import static org.junit.Assert.*;

import org.junit.*;

class OrdinarySubclass extends OrdinaryDestroyed {
    private TestClassFunctionality tc = null;

    public OrdinarySubclass(TestClassFunctionality tc) {
        this.tc = tc;
    }

    protected void disposed() {
        tc.disposed++;
    }
}

class QObjectSubclass extends QObjectDestroyed {
    private TestClassFunctionality tc = null;

    public QObjectSubclass(QObject parent, TestClassFunctionality tc) {
        super(parent);

        this.tc = tc;
    }

    protected void disposed() {
        tc.disposed++;
    }
}

class GeneralObject {
    public String data;
}

class CustomEvent extends QEvent {
    public String s;

    public CustomEvent(String param) {
        super(QEvent.Type.resolve(QEvent.Type.User.value() + 16));
        s = param;
    }
}

class MyModel extends QStandardItemModel {
    public Signal0 mySignal;

    public void mySlot() {
    }

    public boolean b_dataChangedNotified = false;
    public boolean b_mySignalNotified = false;
    public boolean b_error = false;

    protected void connectNotify(AbstractSignal signal) {
        b_dataChangedNotified = b_dataChangedNotified || signal == dataChanged;
        b_mySignalNotified = b_mySignalNotified || signal == mySignal;
    }

    protected void disconnectNotify(AbstractSignal signal) {
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

class EventReceiver extends QWidget {
    public String myString = null;
    public String customEventString = null;
    public QSize resizeEventSize = null;
    public QSize resizeEventOldSize = null;
    public QEvent.Type customEventType;
    public QEvent.Type resizeEventType;
    public QEvent.Type paintEventType;
    public boolean paintEventCastWorked = false;
    public boolean paintRectMatched = false;

    public EventReceiver(QWidget parent, String str) {
        super(parent);
        myString = str;
    }

    public boolean event(QEvent event) {
        if (event instanceof QResizeEvent) {
            QResizeEvent rs = (QResizeEvent) event;
            resizeEventType = event.type();
            resizeEventSize = rs.size();
            resizeEventOldSize = rs.oldSize();
        } else if (event instanceof CustomEvent) {
            CustomEvent ce = (CustomEvent) event;
            customEventType = event.type();
            customEventString = ce.s;
        } else if (event.type() == QEvent.Type.Paint) {
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
    @SuppressWarnings("unchecked") 
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

public class TestClassFunctionality extends QApplicationTest {
    
    @BeforeClass
    public static void testInitialize() throws Exception {
        String args[] = new String[3];
        args[0] = "A";
        args[1] = "B";
        args[2] = "C";
        QApplication.initialize(new String[] {});
    }
    
    static class TestQObject extends QObject {
        private Signal0 a = new Signal0();

        public boolean slot_called = false;

        public boolean signalIsNull() {
            return a == null;
        }

        public boolean signalIsEqualTo(AbstractSignal signal) {
            return a == signal;
        }

        @SuppressWarnings("unused")
        private void slot() {
            slot_called = true;
        }
    }

    @Test
    public void run_testCallQtJambiInternalNativeFunctions() {

        Method method = null;
        try {
            method = QtJambiInternal.class.getDeclaredMethod("setField", QObject.class, Field.class, QObject.AbstractSignal.class);
        } catch (NoSuchMethodException e) {
            assertEquals(e, null);
        }

        assertTrue(method != null);

        Field field = null;
        try {
            field = TestQObject.class.getDeclaredField("a");
        } catch (NoSuchFieldException e) {
            assertEquals(e, null);
        }

        assertTrue(field != null);

        TestQObject test_qobject = new TestQObject();
        try {
            method.setAccessible(true);
            method.invoke(null, test_qobject, field, null);
        } catch (Exception e) {
            assertEquals(e, null);
        }

        assertTrue(test_qobject.signalIsNull());

        try {
            method = QtJambiInternal.class.getDeclaredMethod("fetchSignal", QObject.class, Field.class);
        } catch (NoSuchMethodException e) {
            assertEquals(e, null);
        }

        test_qobject = new TestQObject();
        try {
            method.setAccessible(true);
            QObject.AbstractSignal signal = (QObject.AbstractSignal) method.invoke(null, test_qobject, field);
            assertTrue(test_qobject.signalIsEqualTo(signal));
        } catch (Exception e) {
            assertEquals(e, null);
        }

        long method_long = 0;
        try {
            method = QtJambiInternal.class.getDeclaredMethod("resolveSlot", Method.class);
        } catch (NoSuchMethodException e) {
            assertEquals(e, null);
        }

        try {
            Method slotMethod = TestQObject.class.getDeclaredMethod("slot", (Class[]) null);
            method.setAccessible(true);
            method_long = (Long) method.invoke(null, slotMethod);
            assertTrue(method_long != 0);
        } catch (Exception e) {
            assertEquals(e, null);
        }

        try {
            method = QtJambiInternal.class.getDeclaredMethod("invokeSlot", Object.class, Long.TYPE, Byte.TYPE, Object[].class, int[].class);
        } catch (NoSuchMethodException e) {
            assertEquals(e, null);
        }

        assertEquals(test_qobject.slot_called, false);
        try {
            method.setAccessible(true);
            method.invoke(null, test_qobject, method_long, (byte) 'V', new Object[] {}, new int[] {});
        } catch (Exception e) {
            assertEquals(e, null);
        }

        assertEquals(test_qobject.slot_called, true);
    }

    public int disposed = 0;

    @Test
    public void run_testDestruction() {
        // Delete from Java
        {
            disposed = 0;
            {
                new OrdinarySubclass(this);
            }
            System.gc();
            try {
                Thread.sleep(600);
            } catch (Exception e) {
            }
            ;

            assertEquals(disposed, 1);
        }

        // Delete from C++
        {
            disposed = 0;
            OrdinarySubclass sc = new OrdinarySubclass(this);
            OrdinaryDestroyed.deleteFromCpp(sc);
            assertEquals(disposed, 1);
            assertEquals(sc.nativeId(), 0L);
        }

        // Delete through virtual destructor
        {
            disposed = 0;
            OrdinarySuperclass sc = new OrdinarySubclass(this);
            OrdinaryDestroyed.deleteFromCppOther(sc);
            assertEquals(disposed, 1);
            assertEquals(sc.nativeId(), 0L);
        }

        // Delete QObject from Java
        {
            disposed = 0;
            QObjectSubclass qobject = new QObjectSubclass(null, this);
            qobject.dispose();
            assertEquals(disposed, 1);
            assertEquals(qobject.nativeId(), 0L);
        }

        // Delete QObject from parent
        {
            disposed = 0;
            QObject parent = new QObject();
            QObject qobject = new QObjectSubclass(parent, this);
            parent.dispose();
            assertEquals(disposed, 1);
            assertEquals(qobject.nativeId(), 0L);
        }

        // Delete QObject later
        {
            disposed = 0;
            QObject qobject = new QObjectSubclass(null, this);
            qobject.disposeLater();
            QApplication.processEvents(new QEventLoop.ProcessEventsFlags(QEventLoop.ProcessEventsFlag.DeferredDeletion));
            assertEquals(disposed, 1);
            assertEquals(qobject.nativeId(), 0L);
        }

        // Delete QObject from C++
        {
            disposed = 0;
            QObjectSubclass qobject = new QObjectSubclass(null, this);
            QObjectDestroyed.deleteFromCpp(qobject);
            assertEquals(disposed, 1);
            assertEquals(qobject.nativeId(), 0L);
        }

        // Delete QObject from C++ through virtual destructor
        {
            disposed = 0;
            QObject qobject = new QObjectSubclass(null, this);
            QObjectDestroyed.deleteFromCppOther(qobject);
            assertEquals(disposed, 1);
            assertEquals(qobject.nativeId(), 0L);
        }
    }

    /**
     * Test that calling a private virtual function gives you an exception
     */
    @SuppressWarnings("deprecation")
    @Test
    public void run_callPrivateVirtualFunction() {
        QTableWidget w = new QTableWidget();

        boolean gotException = false;
        try {
            w.setModel(null);
        } catch (QNoImplementationException e) {
            gotException = true;
        }

        assertTrue(gotException);
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

        public SenderTester() {
            timeouted = new QTime();
            timeouted.start();
        }

        public void checkSender() {
            is_null = QtJambiInternal.sender(this) == null;
            is_valid = QtJambiInternal.sender(this) == this;
        }

        public void emitSignal() {
            signal.emit();
        }

        void timeoutSlot() {
            msec = timeouted.elapsed();
        }
    }

    @Test
    public void run_senderNotNull() {
        SenderTester tester = new SenderTester();
        tester.signal.connect(tester, "checkSender()");

        tester.emitSignal();

        assertTrue(!tester.is_null);
        assertTrue(tester.is_valid);
    }

    @Test
    public void run_cppAndJavaObjects() {
        CustomEvent event1 = new CustomEvent("this is my stuff");
        QResizeEvent event2 = new QResizeEvent(new QSize(101, 102), new QSize(103, 104));

        QWidget parentWidget = new QWidget(null);
        EventReceiver someQObject = new EventReceiver(parentWidget, "some stuff");

        List<QObject> children = parentWidget.children();
        assertEquals(children.size(), 1);

        QObject child = children.get(0);
        assertTrue(child != null);
        assertTrue(child instanceof EventReceiver);
        assertTrue(someQObject == child);

        EventReceiver receiver = (EventReceiver) child;
        assertTrue(someQObject == receiver);
        assertEquals(receiver.myString, "some stuff");
        assertTrue(receiver.parent() == parentWidget);

        QApplication.postEvent(receiver, event1);
        QApplication.postEvent(someQObject, event2);
        parentWidget.show();

        QApplication.processEvents();

        assertEquals(receiver.customEventString, "this is my stuff");
        assertEquals(receiver.customEventType, QEvent.Type.resolve(QEvent.Type.User.value() + 16));
        assertEquals(receiver.resizeEventType, QEvent.Type.Resize);
        assertEquals(receiver.resizeEventSize.width(), 101);
        assertEquals(receiver.resizeEventSize.height(), 102);
        assertEquals(receiver.resizeEventOldSize.width(), 103);
        assertEquals(receiver.resizeEventOldSize.height(), 104);
        assertTrue(receiver.paintEventCastWorked);
        assertTrue(receiver.paintRectMatched);
        assertEquals(receiver.paintEventType, QEvent.Type.Paint);

        String[] expected = { "com.trolltech.qt.gui.QSizeGrip", "com.trolltech.qt.gui.QGridLayout", "com.trolltech.qt.gui.QDirModel", "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction",
                "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QAction", "com.trolltech.qt.gui.QListView", "com.trolltech.qt.gui.QTreeView", "com.trolltech.qt.gui.QToolButton",
                "com.trolltech.qt.gui.QToolButton", "com.trolltech.qt.gui.QToolButton", "com.trolltech.qt.gui.QToolButton", "com.trolltech.qt.gui.QToolButton", "com.trolltech.qt.gui.QLabel",
                "com.trolltech.qt.gui.QLabel", "com.trolltech.qt.gui.QLabel", "com.trolltech.qt.gui.QDialogButtonBox", "com.trolltech.qt.gui.QComboBox", "com.trolltech.qt.gui.QLineEdit",
                "com.trolltech.qt.gui.QComboBox" };

        QFileDialog d = new QFileDialog();
        children = d.children();

        assertEquals(children.size(), expected.length);

        int i = 0;
        for (QObject c : children) {
            assertEquals(c.getClass().getName(), expected[i++]);

            // Test one of them with instanceof, just to be on the safe side
            if (i == 25) {
                assertTrue(c instanceof QLineEdit);

                QLineEdit le = (QLineEdit) c;
                assertTrue(le.editingFinished != null);
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

            assertEquals(read_back.size(), 2);
            assertEquals(read_back.get(0).text(), "bite");
            assertEquals(read_back.get(1).text(), "me");
        }
        some_widget.dispose();
    }

    @Test
    public void run_injectedCode() {
        QObject obj = new QObject();
        QAction act = new QAction(obj);
        act.setShortcut("Ctrl+A");
        QKeySequence seq = act.shortcut();

        assertEquals(seq.count(), 1);
        assertEquals(seq.operator_subscript(0), Qt.Modifier.CTRL.value() | Qt.Key.Key_A.value());

        // The result can be checked in the resulting
        // "tmp__testclass_func_result.png" file
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
        } catch (Exception e) {
            assertTrue(false);
        }

        assertTrue(tester.msec >= 1000);
        assertTrue(tester.msec <= 1500);
    }

    @Test
    public void run_copyConstructor() {
        QFileInfo file1 = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        assertTrue(file1.exists());

        QFileInfo file2 = new QFileInfo(file1);

        assertEquals(file2.size(), file1.size());
        assertEquals(file2.exists(), file1.exists());
        assertEquals(file2.fileName(), file1.fileName());
    }

    @Test
    public void run_connectNotify() {
        MyModel model = new MyModel();
        assertTrue(!model.b_error);
        assertTrue(!model.b_dataChangedNotified);
        assertTrue(!model.b_mySignalNotified);

        model.mySignal.connect(model, "mySlot()");
        assertTrue(!model.b_error);
        assertTrue(!model.b_dataChangedNotified);
        assertTrue(model.b_mySignalNotified);

        QListView v = new QListView();
        v.setModel(model);
        assertTrue(!model.b_error);
        assertTrue(model.b_dataChangedNotified);
        assertTrue(model.b_mySignalNotified);

        model.mySignal.disconnect(model, "mySlot()");
        assertTrue(!model.b_error);
        assertTrue(model.b_dataChangedNotified);
        assertTrue(!model.b_mySignalNotified);

        v.dispose();
        assertTrue(!model.b_error);
        assertTrue(!model.b_dataChangedNotified);
        assertTrue(!model.b_mySignalNotified);

        model.dispose();
    }

    @Test
    public void run_settersAndGetters() {
        QStyleOptionButton so = new QStyleOptionButton();

        assertTrue(so.icon().isNull());

        QPixmap pm = new QPixmap(100, 100);
        pm.fill(QColor.red);
        QIcon icon = new QIcon(pm);
        so.setIcon(icon);
        so.setText("A travelling salesman walks into a bar");

        assertEquals(so.text(), "A travelling salesman walks into a bar");
        assertTrue(!so.icon().isNull());

        so.setIcon(new QIcon());
        assertTrue(so.icon().isNull());

        so.setText("A priest and a rabbi sitting in a bar");
        assertEquals(so.text(), "A priest and a rabbi sitting in a bar");

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
        assertEquals(b2.length, 8);
        for (int i = 0; i < 8; ++i)
            assertEquals(b2[i], b_orig[i]);

        // Wrong number of entries
        Exception fe = null;
        try {
            byte b[] = new byte[6];
            uuid.setData4(b);
        } catch (Exception e) {
            fe = e;
        }

        assertTrue(fe != null);
        assertTrue(fe instanceof IllegalArgumentException);

        // Make sure it wasn't set after all
        b2 = uuid.data4();
        assertEquals(b2.length, 8);
        for (int i = 0; i < 8; ++i)
            assertEquals(b2[i], b_orig[i]);

        fe = null;
        try {
            byte b[] = new byte[100];
            uuid.setData4(b);
        } catch (Exception e) {
            fe = e;
        }

        assertTrue(fe != null);
        assertTrue(fe instanceof IllegalArgumentException);
    }

    // Tests the ownership transfer that we need to have objects like
    // QEvent stay alive after they are posted to the event queue...
    // The verifecation here is basically that the vm doesn't crash.
    private static class OwnershipTransferReceiver extends QObject {
        public QEvent.Type event_id;

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

        protected void disposed() {
            super.disposed();
            finalized = true;
        }
    }

    @Test
    public void run_testOwnershipTranfer() {
        OwnershipTransferReceiver receiver = new OwnershipTransferReceiver();

        for (int i = 0; i < 1; ++i) {
            QRect rect = new QRect(1, 2, 3, 4);
            QApplication.postEvent(receiver, new CustomPaintEvent(rect));
        }

        // To attemt deletion of the QPaintEvent, should not happen at
        // this time...
        System.gc();
        assertEquals(CustomPaintEvent.finalized, false);

        // Process the event, thus also deleting it...
        QApplication.processEvents();

        // To provoke collection of the java side of the object, now
        // that C++ has released its hold on it
        System.gc();
        try {
            Thread.sleep(600);
        } catch (Exception e) {
        }
        ;

        assertEquals(CustomPaintEvent.finalized, true);

        // Sanity check the data...
        assertEquals(receiver.event_id, QEvent.Type.Paint);
        assertEquals(1, receiver.rect.x());
        assertEquals(2, receiver.rect.y());
        assertEquals(3, receiver.rect.width());
        assertEquals(4, receiver.rect.height());
    }

    // Check that const char *[] is handled properly by the generated code
    @Test
    public void run_XPMConstructors() {
        String qt_plastique_radio[] = { "13 13 2 1", "X c #000000", ". c #ffffff", "....XXXXX....", "..XX.....XX..", ".X.........X.", ".X.........X.", "X...........X", "X...........X",
                "X...........X", "X...........X", "X...........X", ".X.........X.", ".X.........X.", "..XX.....XX..", "....XXXXX...." };

        QNativePointer p = QNativePointer.createCharPointerPointer(qt_plastique_radio);
        QImage img = new QImage(p);
        assertEquals(img.width(), 13);
        assertEquals(img.height(), 13);

        assertEquals(img.pixel(2, 1), 0xff000000);
        assertEquals(img.pixel(0, 0), 0xffffffff);

        QPixmap pm = new QPixmap(p);
        QImage img2 = pm.toImage();
        assertEquals(img2.pixel(2, 1), 0xff000000);
        assertEquals(img2.pixel(12, 12), 0xffffffff);
    }

    /*
     * Tests for QInvokable and QCoreApplication.invokeLater(Runnable);
     */

    /**
     * The run() function sets the executing thread.
     */
    private static class Invokable implements Runnable {
        public void run() {
            thread = Thread.currentThread();
        }

        public boolean wasRun() {
            return thread != null;
        }

        public Thread thread;
    }

    /**
     * Create an invokable object and post it. Then verify that its not executed
     * before after we call processEvents()
     */
    @Test
    public void run_invokeLater_mainThread() {
        Invokable invokable = new Invokable();
        QCoreApplication.invokeLater(invokable);

        assertTrue(!invokable.wasRun());
        QCoreApplication.processEvents();
        assertTrue(invokable.wasRun());
    }

    @SuppressWarnings("unused")
    private static boolean invokeLater_in_otherThread;

    private static Invokable invokable_in_otherThread;

    /**
     * Same as the test above, except that the invokable is now created in a
     * different thread and we wait for this thread to finish before testing if
     * the invokable was run. We also in this case check that the invokable is
     * executed by the correct thread.
     */
    //FIXME @Test
    public void run_invokeLater_otherThread() {
        Thread thread = new Thread() {
            public void run() {
                invokable_in_otherThread = new Invokable();
                QApplication.invokeLater(invokable_in_otherThread);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
        }

        assertTrue(!invokable_in_otherThread.wasRun());
        QCoreApplication.processEvents();
        assertTrue(invokable_in_otherThread.wasRun());
        assertEquals(invokable_in_otherThread.thread, QCoreApplication.instance().thread());

    }
}
