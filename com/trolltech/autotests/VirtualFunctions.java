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
import com.trolltech.qtest.QTestCase;
import com.trolltech.autotests.generator.*;

class JavaNonAbstractSubclass extends AbstractClass
{

    public void abstractFunction(String something) 
    {
        setS("Even more " + something);
    }

    public AbstractClass getAbstractClass() {
        return new JavaNonAbstractSubclass();
    }
    
}

public class VirtualFunctions extends QTestCase {

    class WidgetClass1 extends QWidget {
        public void setJavaSizeHint(QSize size) { m_size = size; }
        public QSize sizeHint() { return m_size; } 
        private QSize m_size = new QSize(0, 0);
    }
    
    class WidgetClass2 extends QWidget {
        public void setJavaSizeHint(QSize size) { m_size = size; }
        private QSize m_size = new QSize(0, 0);
    }
    
    public void run_testOneSubclass() {
        WidgetClass1 w = new WidgetClass1();
        w.setJavaSizeHint(new QSize(123, 456));
        QCOMPARE(w.sizeHint().width(), 123);
        QCOMPARE(w.sizeHint().height(), 456);        
    }
    
    public void run_testTwoSubclasses() {
        WidgetClass1 w1 = new WidgetClass1();
        w1.setJavaSizeHint(new QSize(123, 456));
        
        QCOMPARE(w1.sizeHint().width(), 123);
        QCOMPARE(w1.sizeHint().height(), 456);        
        
        WidgetClass2 w2 = new WidgetClass2();
        w2.setJavaSizeHint(new QSize(654, 321));
        QWidget w = new QWidget();        
        QCOMPARE(w2.sizeHint().width(), w.sizeHint().width());
        QCOMPARE(w2.sizeHint().height(), w.sizeHint().height());    
    }
    
    /**
     * The purpose of this test is to verify the correct virtual functions
     * are being called for objects created in C++ and Java.
     * 
     * This test relies on some hardcoded values in the styles so if those
     * change the tests will break, but wth...
     */
    public void run_cppCreatedObjects() throws Exception {
        QStyle java_plastique = new QPlastiqueStyle();      
        QStyle java_windowsstyle = new QWindowsStyle();
        
        // Verify that the values are as expected...
        QCOMPARE(java_plastique.pixelMetric(QStyle.PM_SliderThickness), 15);
        QCOMPARE(java_windowsstyle.pixelMetric(QStyle.PM_SliderThickness), 16);
        
        QStyle cpp_plastique = QStyleFactory.create("plastique");
        QVERIFY(cpp_plastique != null);
        QVERIFY(cpp_plastique instanceof QPlastiqueStyle);
        
        // The actual test...
        QCOMPARE(java_plastique.pixelMetric(QStyle.PM_SliderThickness),
                 cpp_plastique.pixelMetric(QStyle.PM_SliderThickness));      
        
        QWindowsStyle windows_style = (QPlastiqueStyle) cpp_plastique;
        QCOMPARE(windows_style.pixelMetric(QStyle.PM_SliderThickness),
                 java_plastique.pixelMetric(QStyle.PM_SliderThickness));
    }
    
    // A QObject subclass to call super.paintEngine();
    private interface CallCounter {
    	public int callCount();
    }
    
    private static class Widget extends QWidget implements CallCounter {
    	public int called;
    	public int callCount() { return called; }
    	public QPaintEngine paintEngine() {
    		++called;
    		if (called > 1) {
    			return null;
    		}
    		return super.paintEngine();
    	}
    }
    
    // A non QObject subclass to call super.paintEngine();
    private static class Image extends QImage implements CallCounter {
    	public int called;
    	public Image() { super(100, 100, QImage.Format_ARGB32_Premultiplied); }
    	public int callCount() { return called; }
    	public QPaintEngine paintEngine() {
    		++called;
    		if (called > 1) {
    			return null;
    		}
    		return super.paintEngine();
    	}
    }
    
    /**
     * The purpose of this test is to verify that we can do things like
     * super.paintEngine() in a QPaintDevice subclass and not get recursion.
     */
    public void data_testSupercall() {
    	defineDataStructure(QPaintDeviceInterface.class, "paintDevice");
    	
    	addDataSet("QObject subclass", new Widget());
    	addDataSet("non-QObject subclass", new Image());
    }
    
    public void run_testSupercall() {
    	QPaintDeviceInterface device = getParameter("paintDevice");
    	QPainter p = new QPainter();
    	p.begin(device);
    	p.end();
    	QCOMPARE(((CallCounter) device).callCount(), 1);
    }
    
    /**
     * The purpose of this test is to verify that we are calling virtual
     * functions using the correct environment for the current thread.
     * We create a QObject in the main thread and pass it to an executing
     * thread and trigger a virtual function call
     */

    private static class PaintThread extends Thread {
    	public Image image;
    	public void run() {
    		QPainter p = new QPainter();
        	p.begin(image);
        	p.end();
    	}
    }    

    public void run_testEnvironment() {
    	PaintThread thread = new PaintThread();
    	thread.image = new Image();
    	thread.start();    	
    	try { thread.join(); } catch (Exception e) { e.printStackTrace(); }
    	
    	QCOMPARE(((CallCounter) thread.image).callCount(), 1);
    }
    
    public void run_abstractClasses() {
        AnotherNonAbstractSubclass obj = new AnotherNonAbstractSubclass();
                
        obj.setS("a string");
        try {
            obj.abstractFunction("a super-string");
            QVERIFY(false); // we should never get here
        } catch (QNoImplementationException e) {
            obj.setS("a non-super string");
        }
        QCOMPARE(obj.getS(), "a non-super string");
        
        obj.doVirtualCall(obj, "a super-string");
        QCOMPARE(obj.getS(), "Not a super-string");
        
        AbstractClass cls = obj.getAbstractClass();
        QCOMPARE(cls.getClass().getName(), "com.trolltech.autotests.generator._AbstractClass");
        
        cls.abstractFunction("my super-string");
        QCOMPARE(cls.getS(), "my super-string");        
        QCOMPARE(cls.getAbstractClass(), null);
        obj.doVirtualCall(cls, "my non-super string");
        QCOMPARE(cls.getS(), "my non-super string");
        
        JavaNonAbstractSubclass foo = new JavaNonAbstractSubclass();
        QVERIFY(foo.getAbstractClass() instanceof JavaNonAbstractSubclass);
        
        foo.abstractFunction("of my super strings");
        QCOMPARE(foo.getS(), "Even more of my super strings");
        
        obj.doVirtualCall(foo, "of my non-super strings");
        QCOMPARE(foo.getS(), "Even more of my non-super strings");
    }
 
    
    public static void main(String args[]) {
        QApplication app = new QApplication(args);
        runTest(new VirtualFunctions());
    }
}
