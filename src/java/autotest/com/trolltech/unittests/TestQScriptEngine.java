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
import com.trolltech.qt.script.QScriptString;
import com.trolltech.qt.script.QScriptSyntaxCheckResult;
import com.trolltech.qt.script.QScriptValue;
import com.trolltech.qt.script.QScriptEngine.QObjectWrapOptions;

public class TestQScriptEngine {

	private QScriptEngine testEngine;
	private QScriptEngine testEngineFromObj;
	private QObject engineParent;
	private QObject holder;
	private Object result;
	
	private QScriptProgram qsprogram;
	private QPushButton button;
	private QPushButton button1;
	private QScriptValue scriptButton;
	private QScriptValue scriptButton1;
	private QScriptValue val;
	private QScriptValue val1;
	private QScriptString testString;
	private QScriptContext scriptContext;

	@org.junit.Before
	public void setUp() {
		QApplication.initialize(new String[] {});
		testEngine = new QScriptEngine();
		qsprogram = new QScriptProgram("5 - 2");
		engineParent = new QObject();
		holder = new QObject();
		button = new QPushButton();
		button1 = new QPushButton();
		scriptButton = testEngine.newQObject(button);
	}

	@org.junit.After
	public void tearDown() {
		testEngine = null;
		button = null;
		button1 = null;
		scriptButton = null;
		scriptButton1 = null;
		val = null;
		val1 = null;
		scriptContext = null;
		QApplication.quit();
		QApplication.instance().dispose();
	}

	@org.junit.Test
	public void testQscripEngineQObjConstructor() {
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
		val = testEngine.newQObject(null);
		assertTrue(val.isNull());
	}
	
	@org.junit.Test
	public void testNewQObjectOwnerWrapperConstructor() {
		val1 = testEngine.newQObject(button, QScriptEngine.ValueOwnership.ScriptOwnership);
		assertTrue(val1.isQObject());
		val = testEngine.newQObject(button, QScriptEngine.ValueOwnership.ScriptOwnership, QScriptEngine.QObjectWrapOption.ExcludeChildObjects);
		assertTrue(val.isQObject());
	}
	
	@org.junit.Test
	public void testNewQObjectScriptValueConstructor() {
		scriptButton = testEngine.newQObject(scriptButton1, button, QScriptEngine.ValueOwnership.ScriptOwnership);
		assertTrue(scriptButton.isQObject());
		val = testEngine.newQObject(val1, button1, QScriptEngine.ValueOwnership.ScriptOwnership, QScriptEngine.QObjectWrapOption.ExcludeSlots);
		assertTrue(val.isQObject());
	}

	@org.junit.Test
	public void testNewQObjectScriptValueObjectConstructor() {
		val = testEngine.newQObject(val1, button1);
		assertTrue(val.isQObject());
	}
	
	@org.junit.Test
	public void testNewQObjectNullQObj() {
		val = testEngine.newQObject(val1, null, QScriptEngine.ValueOwnership.ScriptOwnership, QScriptEngine.QObjectWrapOption.ExcludeSlots);
		assertTrue(val.isNull());
	}
	
	@org.junit.Test
	public void testNewQObjectNullQScripVal() {
		val = testEngine.newQObject(null, button1, QScriptEngine.ValueOwnership.ScriptOwnership, QScriptEngine.QObjectWrapOption.ExcludeSlots);
		assertTrue(val.isQObject());
	}
	
	@org.junit.Test
	public void testNewQObjectBothNull() {
		val = testEngine.newQObject(null, null, QScriptEngine.ValueOwnership.ScriptOwnership, QScriptEngine.QObjectWrapOption.ExcludeSlots);
		assertTrue(val.isNull());
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
		val = testEngine.newDate(456789.0);
		assertTrue(val.isDate());
		val = testEngine.newDate(-42.42);
		assertTrue(val.isDate());
		val1 = testEngine.newDate(new QDateTime());
		assertTrue(val1.isDate());
		val1 = testEngine.newDate(null);
		assertTrue(val1.isDate());
		val1 = testEngine.newDate(new QDateTime().addSecs(456789));
		assertTrue(val1.isDate());
		val = testEngine.newDate(0.0);
		assertTrue(val.isDate());
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
		scriptButton = testEngine.newRegExp(null);
		assertTrue(scriptButton.isRegExp());
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
	 * The following method is obsolete in Qt 4.7.
	 * 
	 * How it works here:
	 * QScriptEngine.canEvaluate(java.lang.String) always
	 * returns with true, however if the program looks incomplete
	 * (Syntactically) it should return false.
	 * 
	 * Possible workarounds:
	 * 1.	use QScriptEngine.checkSyntax(java.lang.String) instead (recommended)
	 * 2.	check the return value of QScriptEngine.evaluate(java.lang.String)
	 *		because it returns "SyntaxError: Parse error" when canEvaluate(java.lang.String program)
	 *		should return false
	 */

	@org.junit.Ignore
	@org.junit.Test
	public void testCanEvaluate() {
		assertTrue(testEngine.canEvaluate("foo = 3;"));
		assertFalse(testEngine.canEvaluate("foo[\"bar\""));
	}

	@org.junit.Test
	public void testCheckSyntax() {
		assertEquals(QScriptEngine.checkSyntax("foo = 3;").state(), QScriptSyntaxCheckResult.State.Valid);
		assertEquals(QScriptEngine.checkSyntax("foo[\"bar\"").state(), QScriptSyntaxCheckResult.State.Error);
		assertEquals(QScriptEngine.checkSyntax("if (\n").state(), QScriptSyntaxCheckResult.State.Intermediate);
	}
	
	@org.junit.Test
	public void testEvaluateQScriptPorgram() {
		assertEquals(testEngine.evaluate(qsprogram).toInt32(), 3);
	}
	
	/*
	 * The following test crashes the JVM
	 */
	@org.junit.Ignore
	@org.junit.Test
	public void testObjectById() {
		val = testEngine.newQObject(button);
		assertEquals(testEngine.objectById(val.nativeId()), val);
	}
	
	@org.junit.Test
	public void testProcessEventsInterval() {
		testEngine.setProcessEventsInterval(5);
		assertEquals(testEngine.processEventsInterval(), 5);
	}
	
	@org.junit.Test
	public void testsetGlobalObject() {
		val = testEngine.newQObject(holder);
		testEngine.setGlobalObject(val);
		val.setProperty("testProperty1", new QScriptValue(5));
		assertEquals(testEngine.globalObject().property("testProperty1").toInt32(), 5);
		val.setProperty("testProperty1", new QScriptValue("test"));
		assertTrue(testEngine.globalObject().property("testProperty1").toString().equals("test"));
	}
	
	@org.junit.Test
	public void testToObject() {
		val = new QScriptValue(5);
		val1 = new QScriptValue();
		assertFalse(val1.isObject());
		val1 = testEngine.toObject(val);
		assertTrue(val1.isObject());
		val1 = testEngine.toObject(null);
		assertFalse(val1.isValid());
	}

	@org.junit.Test
	public void testToStringHandle() {
		val = testEngine.newQObject(holder);
		val.setProperty("testProperty", new QScriptValue("te$t"));
		testString = testEngine.toStringHandle("testProperty");
		assertTrue(val.property(testString).toString().equals("te$t"));
	}
	
}
