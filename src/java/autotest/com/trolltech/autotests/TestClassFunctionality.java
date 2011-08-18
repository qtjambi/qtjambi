/****************************************************************************
 **
 ** Copyright (C) 1992-2009 Nokia. All rights reserved.
 **
 ** This file is part of Qt Jambi.
 **
 ** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.autotests;

import com.trolltech.autotests.generated.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.internal.QtJambiInternal;
import com.trolltech.qt.internal.QSignalEmitterInternal;
import com.trolltech.qt.QtJambiObject;
import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QNoImplementationException;
import static org.junit.Assert.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

class OrdinarySubclass extends OrdinaryDestroyed {
    private TestClassFunctionality tc = null;

    public OrdinarySubclass(TestClassFunctionality tc) {
        this.tc = tc;
    }

    @Override
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

    @Override
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

    @Override
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
            QtJambiObject new_event = null;
            try {
                new_event = QtJambiObject.reassignNativeResources(event, QPaintEvent.class);
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

public class TestClassFunctionality extends QApplicationTest {

    public static void main(String args[]) {
        QApplication.initialize(args);

        TestClassFunctionality test = new TestClassFunctionality();
	test.run_cppAndJavaObjects();
	test.testGraphicsSceneDrawItemsInjections();
	/*
        test.testVirtualCallToFixup();
        test.testFinalCallToFixup();
        test.testFinalCallToValidate();
        test.testVirtualCallToValidate();
        test.run_callPrivateVirtualFunction();
        test.run_copyConstructor();
        test.run_cppAndJavaObjects();
        test.run_injectedCode();
        test.run_invokeLater_mainThread();
        test.run_senderNotNull();
        test.run_settersAndGetters();
        test.run_testCallQtJambiInternalNativeFunctions();
        test.run_testDestruction();
        test.run_testOwnershipTranfer();
        test.run_XPMConstructors();*/
    }

    @BeforeClass
    public static void testInitialize() throws Exception {
        String args[] = new String[3];
        args[0] = "A";
        args[1] = "B";
        args[2] = "C";
        QApplication.initialize(new String[] {});
    }

    static class GraphicsSceneSubclassSubclass extends GraphicsSceneSubclass {
        public static QGraphicsItemInterface items[];
        public static QStyleOptionGraphicsItem options[];

        @Override
        protected void drawItems(QPainter painter, QGraphicsItemInterface[] items, QStyleOptionGraphicsItem[] options, QWidget widget) {
            GraphicsSceneSubclassSubclass.items = items;
            GraphicsSceneSubclassSubclass.options = options;

            options[1].setLevelOfDetail(3.0);
            super.drawItems(painter, items, options, widget);
        }
    }

    @Test
    public void testGraphicsSceneDrawItemsInjections() {
        GraphicsSceneSubclassSubclass gsss = new GraphicsSceneSubclassSubclass();
        QGraphicsEllipseItem item1 = new QGraphicsEllipseItem(new QRectF(1.0, 2.0, 3.0, 4.0));
        item1.setZValue(2.0);
        QGraphicsEllipseItem item2 = new QGraphicsEllipseItem(new QRectF(2.0, 3.0, 4.0, 5.0));
        item2.setZValue(1.0);
        gsss.addItem(item1);
        gsss.addItem(item2);

        QGraphicsView view = new QGraphicsView();
        view.setScene(gsss);
        // Since Qt 4.6 GraphicsScene#drawItems(...) is not called unless IndirectPainting is set.
        view.setOptimizationFlag(QGraphicsView.OptimizationFlag.IndirectPainting);
        view.show();

	long t = System.currentTimeMillis();
	while (t + 1000 > System.currentTimeMillis() && GraphicsSceneSubclassSubclass.items == null) 
	    QApplication.processEvents();

        assertTrue(GraphicsSceneSubclassSubclass.items != null);
        assertTrue(GraphicsSceneSubclassSubclass.options != null);
        assertEquals(2, GraphicsSceneSubclassSubclass.items.length);
        assertEquals(2, GraphicsSceneSubclassSubclass.options.length);
        assertTrue(GraphicsSceneSubclassSubclass.items[0] == item2);
        assertTrue(GraphicsSceneSubclassSubclass.items[1] == item1);

        QRectF brect = GraphicsSceneSubclassSubclass.items[0].boundingRect();
        assertEquals(2.0, brect.left(), 0.0001);
        assertEquals(3.0, brect.top(), 0.0001);
        assertEquals(4.0, brect.width(), 0.0001);
        assertEquals(5.0, brect.height(), 0.0001);

        brect = GraphicsSceneSubclassSubclass.items[1].boundingRect();
        assertEquals(1.0, brect.left(), 0.0001);
        assertEquals(2.0, brect.top(), 0.0001);
        assertEquals(3.0, brect.width(), 0.0001);
        assertEquals(4.0, brect.height(), 0.0001);

        assertEquals(2, gsss.numItems());

        brect = gsss.firstBoundingRect();
        assertEquals(2.0, brect.left(), 0.0001);
        assertEquals(3.0, brect.top(), 0.0001);
        assertEquals(4.0, brect.width(), 0.0001);
        assertEquals(5.0, brect.height(), 0.0001);

        brect = gsss.secondBoundingRect();
        assertEquals(1.0, brect.left(), 0.0001);
        assertEquals(2.0, brect.top(), 0.0001);
        assertEquals(3.0, brect.width(), 0.0001);
        assertEquals(4.0, brect.height(), 0.0001);

        assertTrue(gsss.firstItem() == item2);
        assertTrue(gsss.secondItem() == item1);

        assertEquals(QStyleOption.OptionType.SO_GraphicsItem.value(), gsss.firstStyleOptionType());
        assertEquals(QStyleOption.OptionType.SO_GraphicsItem.value(), gsss.secondStyleOptionType());
        assertEquals(QStyleOptionGraphicsItem.StyleOptionVersion.Version.value(), gsss.firstStyleOptionVersion());
        assertEquals(QStyleOptionGraphicsItem.StyleOptionVersion.Version.value(), gsss.secondStyleOptionVersion());

        QStyleOption option = gsss.firstStyleOption();
        assertTrue(option instanceof QStyleOptionGraphicsItem);
        assertEquals(((QStyleOptionGraphicsItem) option).levelOfDetail(), 1.0, 0.0001);

        option = gsss.secondStyleOption();
        assertTrue(option instanceof QStyleOptionGraphicsItem);
        assertEquals(((QStyleOptionGraphicsItem) option).levelOfDetail(), 3.0, 0.0001);

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

        private void slot() {
            slot_called = true;
        }
    }

    @Test
    public void testEquals()
    {
        QHostAddress address1 = new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        QHostAddress address2 = new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        QHostAddress address3 = new QHostAddress(QHostAddress.SpecialAddress.Broadcast);
        QByteArray array = new QByteArray("127.0.0.1");

        assertFalse(address1 == address2);
        assertFalse(address2 == address3);
        assertTrue(address1.equals(address2));
        assertTrue(address2.equals(address1));
        assertTrue(address3.equals(address3));
        assertEquals(false, address1.equals(address3));
        assertFalse(address2.equals(array));
    }

    @Test
    public void testHashCodeAndEquals()
    {
        Hashtable<QHostAddress, QByteArray> address_hash = new Hashtable<QHostAddress, QByteArray>();

        QHostAddress address1 = new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        QHostAddress address2 = new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        QHostAddress address3 = new QHostAddress(QHostAddress.SpecialAddress.Broadcast);

        QByteArray ba_address1 = new QByteArray("127.0.0.1 - 1");
        QByteArray ba_address2 = new QByteArray("127.0.0.1 - 2");
        QByteArray ba_address3 = new QByteArray("255.255.255.255");

        address_hash.put(address1, ba_address1);
        assertFalse(address_hash.containsKey(new QHostAddress(QHostAddress.SpecialAddress.Broadcast)));
        assertTrue(address_hash.containsKey(new QHostAddress(QHostAddress.SpecialAddress.LocalHost)));
        assertTrue(address_hash.get(new QHostAddress(QHostAddress.SpecialAddress.LocalHost)) == ba_address1);

        address_hash.put(address2, ba_address2); // overwrites the first entry of this type
        address_hash.put(address3, ba_address3);
        assertTrue(address_hash.containsKey(address1));
        assertTrue(address_hash.containsKey(new QHostAddress(QHostAddress.SpecialAddress.Broadcast)));

        QHostAddress lookup_key1 = new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        QHostAddress lookup_key2 = new QHostAddress(QHostAddress.SpecialAddress.Broadcast);

        QByteArray value = address_hash.get(lookup_key1);
        assertTrue(value == ba_address2);

        value = address_hash.get(lookup_key2);
        assertTrue(value == ba_address3);
    }

    @Test
    public void testToString()
    {
        QByteArray ba = new QByteArray("Pretty flowers æøå");
        assertEquals("Pretty flowers æøå", ba.toString());
    }

    @Test
    public void run_testCallQtJambiInternalNativeFunctions() {

        Field field = null;
        try {
            field = TestQObject.class.getDeclaredField("a");
        } catch (NoSuchFieldException e) {
            assertEquals(e, null);
        }
        assertTrue(field != null);

        Method method = null;
        try {
            method = QtJambiInternal.class.getDeclaredMethod("fetchSignal", QSignalEmitterInternal.class, Field.class);
        } catch (NoSuchMethodException e) {
            assertEquals(e, null);
        }

        TestQObject test_qobject = new TestQObject();
        try {
            method.setAccessible(true);
            QSignalEmitter.AbstractSignal signal = (QObject.AbstractSignal) method.invoke(null, test_qobject, field);
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
            QApplication.sendPostedEvents(null, QEvent.Type.DeferredDelete.value());

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
            is_null = com.trolltech.qt.internal.QtJambiInternal.sender(this) == null;
            is_valid = com.trolltech.qt.internal.QtJambiInternal.sender(this) == this;
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

	long t = System.currentTimeMillis();
	while (t + 1000 > System.currentTimeMillis() && !receiver.paintEventCastWorked)
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

        QFileDialog d = new QFileDialog();

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
    public void testDialog(){
        String[] childrenClassList = {"QGridLayout","QLabel","QComboBox","QLabel","QComboBox","QLabel","QComboBox","QPushButton","QTableWidget","QLabel", "QPushButton"};

        TestDialog dialog = new TestDialog();
        dialog.show();
        int i = 0;
        for (QObject o : dialog.children()) {
            assertTrue(o.getClass().getName().endsWith("." + childrenClassList[i]));
            i++;
        }
        dialog.hide();
    }

    @Test
    public void run_injectedCode() {
        QObject obj = new QObject();
        QAction act = new QAction(obj);
        act.setShortcut("Ctrl+A");
        QKeySequence seq = act.shortcut();

        assertEquals(seq.count(), 1);
        assertEquals(seq.at(0), Qt.KeyboardModifier.ControlModifier.value() | Qt.Key.Key_A.value());

        SenderTester tester = new SenderTester();
        QTimer.singleShot(1000, tester, "timeoutSlot()");

        System.gc();

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
        QAbstractFileEngine.addSearchPathForResourceEngine(".");
        QFileInfo file1 = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        assertTrue(file1.exists());

        QFileInfo file2 = new QFileInfo(file1);

        assertEquals(file2.size(), file1.size());
        assertEquals(file2.exists(), file1.exists());
        assertEquals(file2.fileName(), file1.fileName());
    }

    @Test
    public void run_settersAndGetters() {
        QStyleOptionButton so = new QStyleOptionButton();

        assertTrue(so.icon().isNull());

        QPixmap pm = new QPixmap(100, 100);
        pm.fill(new QColor(com.trolltech.qt.core.Qt.GlobalColor.red));
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

    static class MySpinBox extends QDoubleSpinBox {

        public String receivedString;
        public int receivedPos;

        @Override
        public String fixup(String input) {
            receivedString = input;
            return "As aught of " + input.substring(2, 8) + " birth";
        }

        @Override
        public QValidator.State validate(QValidator.QValidationData data)
        {
            receivedString = data.string;
            receivedPos = data.position;

            data.string = "The " + data.string.substring(9, 13) + " where Death has set his seal";
            data.position += 13;

            return QValidator.State.Acceptable;
        }


    }

    @Test public void testVirtualCallToFixup() {
        MySpinBox spinBox = new MySpinBox();
        SpinBoxHandler handler = new SpinBoxHandler();

        handler.tryFixup(spinBox, "Immortal love, forever full");

        assertEquals("Immortal love, forever full", spinBox.receivedString);
        assertEquals("As aught of mortal birth", handler.my_returned_string());
    }

    @Test public void testVirtualCallToValidate() {
        MySpinBox spinBox = new MySpinBox();
        SpinBoxHandler handler = new SpinBoxHandler();

        handler.tryValidate(spinBox, "Immortal love, forever full", 15);
        assertEquals("Immortal love, forever full", spinBox.receivedString);
        assertEquals(15, spinBox.receivedPos);
        assertEquals("The love where Death has set his seal", handler.my_returned_string());
        assertEquals(28, handler.my_returned_pos());
        assertEquals(QValidator.State.Acceptable, handler.my_returned_state());
    }

    @Test public void testFinalCallToFixup() {
        SpinBoxSubclass sbs = new SpinBoxSubclass();

        String returned = sbs.fixup("Thou dost hang canary birds in parlour windows");
        assertEquals("And Thou art dead", returned);
        assertEquals("Thou dost hang canary birds in parlour windows", sbs.my_received_string());
    }

    @Test public void testFinalCallToValidate() {
        SpinBoxSubclass sbs = new SpinBoxSubclass();

        QValidator.QValidationData data = new QValidator.QValidationData("dream and you have a sloppy body from being brought to bed of crocuses", 14);
        QValidator.State returned = sbs.validate(data);

        assertEquals(QValidator.State.Intermediate, returned);
        assertEquals("dream and you have a sloppy body from being brought to bed of crocuses", sbs.my_received_string());
        assertEquals(14, sbs.my_received_pos());
        assertEquals("The silence of that dreamless sleep", data.string);
        assertEquals(27, data.position);
    }

    // Tests the ownership transfer that we need to have objects like
    // QEvent stay alive after they are posted to the event queue...
    // The verifecation here is basically that the vm doesn't crash.
    private static class OwnershipTransferReceiver extends QObject {
        public QEvent.Type event_id;

        public QRect rect;

        @Override
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

        @Override
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

        QImage img = new QImage(qt_plastique_radio);
        assertEquals(img.width(), 13);
        assertEquals(img.height(), 13);

        assertEquals(img.pixel(2, 1), 0xff000000);
        assertEquals(img.pixel(0, 0), 0xffffffff);

        QPixmap pm = new QPixmap(qt_plastique_radio);
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

    public QPainter painterReference;

    @Test
    public void resetAfterUseTemporary() {
        painterReference = null;

        QCalendarWidget w = new QCalendarWidget() {
            @Override
            public void paintCell(QPainter painter, QRect rect, QDate date) {
                painterReference = painter;
            }
        };

        // painter == null passes a temporary C++ painter
        General.callPaintCell(w, null);

        assertTrue(painterReference != null);
        assertEquals(0, painterReference.nativeId());
    }

    @Test
    public void resetAfterUseNonTemporary() {
        painterReference = null;

        QCalendarWidget w = new QCalendarWidget() {
            @Override
            public void paintCell(QPainter painter, QRect rect, QDate date) {
                painterReference = painter;
            }
        };

        QPainter p = new QPainter();
        General.callPaintCell(w, p);

        assertTrue(painterReference != null);
        assertTrue(0 != painterReference.nativeId());
        assertEquals(p, painterReference);
    }

    @Test
    public void resetAfterUseNull() {
        painterReference = new QPainter();

        QCalendarWidget w = new QCalendarWidget() {
            @Override
            public void paintCell(QPainter painter, QRect rect, QDate date) {
                painterReference = painter;
            }
        };

        General.callPaintCellNull(w);

        assertEquals(null, painterReference);
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

    private static boolean invokeLater_in_otherThread;

    private static Invokable invokable_in_otherThread;

    /**
     * Same as the test above, except that the invokable is now created in a
     * different thread and we wait for this thread to finish before testing if
     * the invokable was run. We also in this case check that the invokable is
     * executed by the correct thread.
     */
    //FIXME @Test
    /*public void run_invokeLater_otherThread() {
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

    }*/

    @Test
    public void invokeAndWaitSameThread() {
        Invokable i = new Invokable();
        QCoreApplication.invokeAndWait(i);
        assertTrue(i.wasRun());
    }
}
