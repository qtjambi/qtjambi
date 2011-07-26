/**
 * Unit Test implementations for QScriptEngine.java
 * 
 * TODO write tests for memory, etc.
 * 
 * @author akoskm
 */

package com.trolltech.unittests;

import static org.junit.Assert.*;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.script.QScriptContext;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptProgram;
import com.trolltech.qt.script.QScriptValue;

public class TestQScriptEngine extends Thread {

	private QScriptEngine testEngine;
	private QScriptEngine testEngineFromObj;
	private QObject engineParent;

	private QScriptProgram qsprogram;
	private QPushButton button;
	private QScriptValue scriptButton;
	private QScriptValue scriptButton1;
	private QScriptValue val;
	private QScriptValue val1;
	private QScriptContext scriptContext;

	@org.junit.Before
	public void setUp() {
		QApplication.initialize(new String[] {});
		testEngine = new QScriptEngine();
		qsprogram = new QScriptProgram("5 - 2");
		engineParent = new QObject();
		button = new QPushButton();
		scriptButton = testEngine.newQObject(button);
	}

	@org.junit.After
	public void tearDown() {
		testEngine = null;
		button = null;
		scriptButton = null;
		scriptButton1 = null;
		val = null;
		val1 = null;
		scriptContext = null;
		QApplication.quit();
		QApplication.instance().dispose();
	}

	@org.junit.Test
	public void testQscripEngineQObjConst() {
		testEngineFromObj = new QScriptEngine(engineParent);
		assertEquals(testEngineFromObj.parent(), engineParent);
		testEngineFromObj = new QScriptEngine(null);
		assertEquals(testEngineFromObj.parent(), null);
	}

	@org.junit.Test
	public void testEvaluate() {
		assertTrue(testEngine.evaluate("1 + 2").toString().equals("3"));
		assertEquals(testEngine.evaluate("1 + 2").toInt32(), 3);
		assertFalse(testEngine.evaluate("1 == 2").toBoolean());
		assertEquals(testEngine.evaluate("var res = 1; for(var i = 1; i <= 5; i++)res = res*i;").toInt32(), 120);
		assertEquals(testEngine.evaluate("1 + 2").toInteger(), 3.00, 0);
		assertEquals(testEngine.evaluate("3.14").toNumber(), 3.14, 0);
	}

	@org.junit.Test
	public void testSetProperty() {
		testEngine.globalObject().setProperty("button", scriptButton);
		scriptButton.setProperty("checkable", new QScriptValue(true));
		assertTrue(testEngine.evaluate("button.checkable = true").toBoolean());
	}

	@org.junit.Test
	public void testProperty() {
		scriptButton.setProperty("aBooleanProperty", new QScriptValue(true));
		assertTrue(scriptButton.property("aBooleanProperty").toBoolean());
	}

	@org.junit.Test
	public void testNewQObject() {
		scriptButton1 = testEngine.newQObject(button);
		assertTrue(scriptButton1.isQObject());
	}

	@org.junit.Test
	public void testNewQArray() {
		val = testEngine.newArray(1);
		assertTrue(val.isArray());
		val1 = testEngine.newArray();
		assertTrue(val1.isArray());
	}

	@org.junit.Test
	public void testNewDate() {
		val = testEngine.newDate(456789);
		assertTrue(val.isDate());
		val1 = testEngine.newDate(new QDateTime());
		assertTrue(val1.isDate());
	}

	@org.junit.Test
	public void testNewObject() {
		val = testEngine.newObject();
		assertTrue(val.isObject());
	}

	@org.junit.Test
	public void testNewRegExp() {
		val = testEngine.newRegExp(new QRegExp());
		assertTrue(val.isRegExp());
		val1 = testEngine.newRegExp("ABCD", "g");
		assertTrue(val1.isRegExp());
	}

	@org.junit.Test
	public void testNewVariant() {
		val = testEngine.newVariant(new QVariant());
		assertTrue(val.isVariant());
		val1 = testEngine.newVariant(new QScriptValue(), new Object());
		assertTrue(val1.isVariant());
	}

	@org.junit.Test
	public void testNullValue() {
		val = testEngine.nullValue();
		assertTrue(val.isNull());
	}

	@org.junit.Test
	public void testPushPopContext() {
		testEngine.setProperty("global", new QScriptValue(3));
		scriptContext = testEngine.pushContext();
		scriptContext.activationObject().setProperty("local", new QScriptValue(5));
		assertEquals(testEngine.evaluate("global = 6; global + local;").toInt32(), 11);
		testEngine.popContext();
		assertEquals(((QScriptValue) testEngine.property("global")).toInt32(), 3);
	}

	/*
	 * TODO fix: public final boolean canEvaluate(java.lang.String program)
	 * always returns with true, however if the program looks incomplete
	 * (Syntactically) it should return false.
	 * 
	 * Possible workaround is to check the return value of
	 * evaluate(java.lang.String program) because it returns
	 * "SyntaxError: Parse error" when canEvaluate(java.lang.String program)
	 * should return false
	 * 
	 * ps.: anyway this method is obsolete member of QScriptEngine class
	 */
	@org.junit.Test
	public void testCanEvaluate() {
		assertTrue(testEngine.canEvaluate("foo = 3;"));
		assertFalse(testEngine.canEvaluate("foo[\"bar\""));
	}

	@org.junit.Test
	public void testEvaluateQScriptPorgram() {
		assertEquals(testEngine.evaluate(qsprogram).toInt32(), 3);
	}

}
