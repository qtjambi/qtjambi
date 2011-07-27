/**
 * Unit Test implementations for QScriptValue.java
 * 
 * TODO write tests for memory, etc.
 * 
 */

package com.trolltech.unittests;

import static org.junit.Assert.assertTrue;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptValue;

public class TestQScriptValue {

	private QScriptValue scriptButton;
	private QScriptValue val;
	private QScriptEngine testEngine;
	private QPushButton button;
	
	@org.junit.BeforeClass
	public static void setUpClass() {
		QApplication.initialize(new String[] {});
	}
	
	@org.junit.Before
	public void setUp() {
		testEngine = new QScriptEngine();
		button = new QPushButton();
		scriptButton = testEngine.newQObject(button);
	}
	
	@org.junit.After
	public void tearDown() {
		testEngine = null;
		button = null;
		scriptButton = null;
		val = null;
	}
	
	@org.junit.Test
	public void testToObject() {
		val = scriptButton.toObject();
		assertTrue(val.isObject());
	}
	
	@org.junit.Test
	public void testToNumber() {
		val = testEngine.evaluate("5 - 2");
		assertTrue(val.isNumber());
	}
	
}
