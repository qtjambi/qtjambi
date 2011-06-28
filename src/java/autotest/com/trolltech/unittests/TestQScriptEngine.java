/**
 * Unit Test implementations for QScriptEngine.java
 * 
 * @author akoskm
 */

package com.trolltech.unittests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptValue;

public class TestQScriptEngine extends TestCase {
    
    QScriptEngine testEngine;
    QScriptValue testValue;
    QPushButton button;
    QScriptValue scriptButton;
    QScriptValue scriptButton1;
    QScriptValue val;
    QScriptValue val1;
    
    public TestQScriptEngine(String name) {
	super(name);
    }
    
    public void setUp() throws Exception {
	QApplication.initialize(new String[] {});
	testEngine = new QScriptEngine();
	button = new QPushButton();
	scriptButton = testEngine.newQObject(button);
    }
    
    public void tearDown() throws Exception {
	testEngine = null;
	button = null;
	scriptButton = null;
	scriptButton1 = null;
	val = null;
	val1 = null;
	QApplication.quit();
	QApplication.instance().dispose();
    }
    
    public void testEvaluate() {
	assertTrue(testEngine.evaluate("1 + 2").toString().equals("3"));
	assertTrue(testEngine.evaluate("1 + 2").toInteger() == 3);
	assertTrue(testEngine.evaluate("1 == 2").toBoolean() == false);
	assertTrue(testEngine.evaluate("var res = 1; for(var i = 1; i <= 5; i++)res = res*i;").toInt32() == 120);
    }
    
    public void testSetProperty() {
	testEngine.globalObject().setProperty("button", scriptButton);
	scriptButton.setProperty("checkable", new QScriptValue(true));
	assertTrue(testEngine.evaluate("button.checkable = true").toBoolean());
    }
    
    public void testProperty() {
	scriptButton.setProperty("aBooleanProperty", new QScriptValue(true));	
	assertTrue(scriptButton.property("aBooleanProperty").toBoolean());
    }
    
    public void testNewQObject() {
	scriptButton1 = testEngine.newQObject(button);
	assertTrue(scriptButton1.isQObject());
    }
    
    public void testNewQArray() {
	val = testEngine.newArray(1);
	assertTrue(val.isArray());
    }
    
    public void testNewDate() {
	val = testEngine.newDate(456789);
	assertTrue(val.isDate());
	val1 = testEngine.newDate(new QDateTime());
	assertTrue(val1.isDate());
    }
    
    public void testNewObject() {
	val = testEngine.newObject();
	assertTrue(val.isObject());
    }
    
    public void testNewRegExp() {
	val = testEngine.newRegExp(new QRegExp());
	assertTrue(val.isRegExp());
	val1 = testEngine.newRegExp("ABCD", "g");
	assertTrue(val1.isRegExp());
    }
    
    public void testNewVariant() {
	val = testEngine.newVariant(new QVariant());
	assertTrue(val.isVariant());
	val1 = testEngine.newVariant(new QScriptValue(), new Object());
	assertTrue(val1.isVariant());
    }
    
    public void testNullValue() {
	val = testEngine.nullValue();
	assertTrue(val.isNull());
    }
    
    /* TODO
     * write tests for pop/push context, memory, etc.
     */
    
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestQScriptEngine("testEvaluate"));
	suite.addTest(new TestQScriptEngine("testNewQObject"));
	suite.addTest(new TestQScriptEngine("testProperty"));
	suite.addTest(new TestQScriptEngine("testSetProperty"));
	suite.addTest(new TestQScriptEngine("testNewQArray"));
	suite.addTest(new TestQScriptEngine("testNewDate"));
	suite.addTest(new TestQScriptEngine("testNewObject"));
	suite.addTest(new TestQScriptEngine("testNewRegExp"));
	suite.addTest(new TestQScriptEngine("testNullValue"));
	suite.addTest(new TestQScriptEngine("testNewVariant"));
	return suite;
    }
    
}
