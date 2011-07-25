/**
 * Unit Test implementations for GeneratorUtilities.java
 * 
 * TODO: testCreateExtendedEnum()
 * 
 * @author akoskm
 */

package com.trolltech.generatortests;

import java.lang.reflect.Field;

import com.trolltech.qt.GeneratorUtilities;
import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QImage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestGeneratorUtilities extends TestCase {

    private GeneratorUtilities gu1;
    private QObject o1;
    private FetchableTestClass ftc;
    private QObject o2;
    private QImage image;

    static {
	QtJambi_LibraryInitializer.init();
    }
    
    public TestGeneratorUtilities(String name) {
		super(name);
    }

    @org.junit.Before
    public void setUp() throws Exception {
	super.setUp();
	gu1 = new GeneratorUtilities();
	o1 = new QObject();
	o2 = new QObject();
	ftc = new FetchableTestClass(o2);
	image = new QImage();
    }

    @org.junit.After
    public void tearDown() throws Exception {
	super.tearDown();
	gu1 = null;
	o1 = null;
	ftc = null;
	o2 = null;
	image = null;
	ftc = null;
    }

    @org.junit.Test
    public void testThreadCheck() throws Exception {
	Field threadAssertsTest = GeneratorUtilities.class.getDeclaredField("threadAsserts");
	threadAssertsTest.setAccessible(true);
	/* 
	 * if threadAsserts is false threadCheck won't run, we test the case only when
	 * threadAsserts is true
	 */
	if (threadAssertsTest.getBoolean(gu1)) {
	    assertNotNull(o1.thread());
	    assertEquals(o1.thread(), Thread.currentThread());
	}
    }
    
    @org.junit.Test
    public void testFetchField() throws Exception {
	/*
	 * Access the class variable with it's getter method,
	 * then with GeneratorUtilities.fetchfield(QObject, Class, String);
	 * Compare the references.
	 */
	assertTrue(ftc.getFakeVar1().equals(GeneratorUtilities.fetchField(ftc, FetchableTestClass.class, "fakeVar1")));
    }
    
    @org.junit.Test
    public void testSetField() throws Exception {
	GeneratorUtilities.setField(ftc, FetchableTestClass.class, "str1", "some text");
	assertEquals(ftc.getStr1(), "some text");
    }
    
    @org.junit.Test
    public void testCountExpense() {
	image.load("./src/java/qtjambi/com/trolltech/images/qt-logo.png");
	assertEquals(image.height()*image.bytesPerLine(), 4096);
    }

    // TODO implement testCreateExtendedEnum(), example from QAbstractFileEngine.java
    @org.junit.Test
    public void testCreateExtendedEnum() {
	assertTrue(true);
	assertFalse(false);
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestGeneratorUtilities("testThreadCheck"));
	suite.addTest(new TestGeneratorUtilities("testFetchField"));
	suite.addTest(new TestGeneratorUtilities("testSetField"));
	suite.addTest(new TestGeneratorUtilities("testCountExpense"));
	suite.addTest(new TestGeneratorUtilities("testCreateExtendedEnum"));
	return suite;
    }

}

class FetchableTestClass {
    
    private QObject fakeVar1;
    private String str1;
    
    public FetchableTestClass(QObject fakeVar1) {
	this.fakeVar1 = fakeVar1;
    }
    
    public QObject getFakeVar1() {
	return fakeVar1;
    }
    
    public String getStr1() {
	return str1;
    }
    
}
