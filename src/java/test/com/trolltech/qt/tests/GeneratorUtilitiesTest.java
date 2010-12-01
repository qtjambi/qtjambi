// TODO Progress: 80%

package com.trolltech.qt.tests;

import java.lang.reflect.Field;

import com.trolltech.qt.GeneratorUtilities;
import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QImage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeneratorUtilitiesTest extends TestCase {

    private GeneratorUtilities gu1;
    private QObject o1;
    private FetchableTestClass ftc;
    private QObject o2;
    private QImage image;

    static {
	QtJambi_LibraryInitializer.init();
    }
    
    public GeneratorUtilitiesTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	super.setUp();
	gu1 = new GeneratorUtilities();
	o1 = new QObject();
	o2 = new QObject();
	ftc = new FetchableTestClass(o2);
	image = new QImage();
    }

    public void tearDown() throws Exception {
	super.tearDown();
	gu1 = null;
	o1 = null;
	ftc = null;
	o2 = null;
	image = null;
	ftc = null;
    }

    public void testThreadCheck() throws Exception {
	Field threadAssertsTest = GeneratorUtilities.class.getDeclaredField("threadAsserts");
	threadAssertsTest.setAccessible(true);
	/* 
	 * if threadAsserts is false threadCheck won't run, we test the case only when
	 * threadAsserts is true
	 */
	if (threadAssertsTest.getBoolean(gu1)) {
	    assertFalse(o1.thread() == null);
	    assertTrue(o1.thread() == Thread.currentThread());
	}
    }
    
    public void testFetchField() throws Exception {
	/*
	 * Access the class variable with it's getter method,
	 * then with GeneratorUtilities.fetchfield(QObject, Class, String);
	 * Compare the references.
	 */
	assertTrue(ftc.getFakeVar1().equals(GeneratorUtilities.fetchField(ftc, FetchableTestClass.class, "fakeVar1")));
	assertFalse(!(ftc.getFakeVar1().equals(GeneratorUtilities.fetchField(ftc, FetchableTestClass.class, "fakeVar1"))));
    }
    
    public void testSetField() throws Exception {
	GeneratorUtilities.setField(ftc, FetchableTestClass.class, "str1", "some text");
	assertTrue(ftc.getStr1().equals("some text"));
	assertFalse(!(ftc.getStr1().equals("some text")));
    }
    
    public void testCountExpense() {
	image.load("./src/com/trolltech/images/qt-logo.png");
	assertTrue(image.height()*image.bytesPerLine() <= 67108864);
	assertFalse(image.height()*image.bytesPerLine() > 67108864);
    }

    // TODO implement testCreateExtendedEnum(), example from QAbstractFileEngine.java
    public void testCreateExtendedEnum() {
	assertTrue(true);
	assertFalse(false);
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new GeneratorUtilitiesTest("testThreadCheck"));
	suite.addTest(new GeneratorUtilitiesTest("testFetchField"));
	suite.addTest(new GeneratorUtilitiesTest("testSetField"));
	suite.addTest(new GeneratorUtilitiesTest("testCountExpense"));
	suite.addTest(new GeneratorUtilitiesTest("testCreateExtendedEnum"));
	return suite;
    }

}

class FetchableTestClass {
    
    private QObject fakeVar1;
    private String str1;
    
    public FetchableTestClass(QObject fakeVar1) {
	this.fakeVar1 = fakeVar1;
    }
    
    public FetchableTestClass(String str1) {
	this.str1 = str1;
    }
    
    public void setFakeVar1(QObject fakeVar1) {
	this.fakeVar1 = fakeVar1;
    }
    
    public QObject getFakeVar1() {
	return fakeVar1;
    }
    
    public void setStr1(String str1) {
	this.str1 = str1;
    }
    
    public String getStr1() {
	return str1;
    }
    
}
