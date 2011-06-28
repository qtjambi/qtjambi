/**
 * Unit Test implementations for QScriptEngine.java
 * 
 * @author akoskm
 */

package com.trolltech.unittests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptValue;

public class TestQScriptEngine extends TestCase {
    
    QScriptEngine testEngine;
    QScriptValue testValue;
    QPushButton button;
    QScriptValue scriptButton;
    
    public TestQScriptEngine(String name) {
	super(name);
    }
    
    public void setUp() throws Exception {
	QApplication.initialize(new String[] {});
	testEngine = new QScriptEngine();
	button = new QPushButton();
	scriptButton = testEngine.newQObject(button);
	testEngine.globalObject().setProperty("button", scriptButton);
    }
    
    public void tearDown() throws Exception {
	testEngine = null;
	button = null;
	scriptButton = null;
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
	scriptButton.setProperty("checkable", new QScriptValue(true));
	assertTrue(testEngine.evaluate("button.checkable = true").toBoolean());
    }
    
    public void testProperty() {
	scriptButton.setProperty("checkable", new QScriptValue(true));	
	assertTrue(scriptButton.property("checkable").toBoolean());
    }
    	
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestQScriptEngine("testEvaluate"));
	suite.addTest(new TestQScriptEngine("testSetProperty"));
	suite.addTest(new TestQScriptEngine("testProperty"));
	return suite;
    }
    
}
