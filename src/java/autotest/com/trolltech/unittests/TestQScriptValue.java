/**
 * Unit Test implementations for QScriptValue.java
 * 
 * TODO write tests for memory, etc.
 * 
 */

package com.trolltech.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptValue;
import com.trolltech.qt.script.QScriptValue.ResolveFlags;
import com.trolltech.qt.script.QScriptValue.SpecialValue;
import com.trolltech.qt.script.QScriptValue.ResolveFlag;

public class TestQScriptValue extends QApplicationTest {

	private QScriptValue scriptButton;
	private QScriptValue val;
	private QScriptEngine testEngine;
	private QPushButton button;
	private SpecialValue specVal0;
	private SpecialValue specVal1;
	private ResolveFlag resFlag0;
	private ResolveFlag resFlag1;
	private ResolveFlag resFlag2;
	private ResolveFlag resFlag3;
	private ResolveFlags resFlags0;
	private ResolveFlags resFlags1;
	
	@org.junit.Before
	public void setUp() {
		specVal0 = SpecialValue.NullValue;
		specVal1 = SpecialValue.UndefinedValue;
		testEngine = new QScriptEngine();
		button = new QPushButton();
		scriptButton = testEngine.newQObject(button);
		resFlag0 = ResolveFlag.ResolveLocal;
		resFlag1 = ResolveFlag.ResolvePrototype;
		resFlag2 = ResolveFlag.ResolveScope;
		resFlag3 = ResolveFlag.ResolveFull;
		resFlags0 = ResolveFlag.createQFlags(resFlag0);
		resFlags1 = new ResolveFlags(3);
	}
	
	@org.junit.After
	public void tearDown() {
		testEngine = null;
		button = null;
		scriptButton = null;
		val = null;
		specVal0 = null;
		specVal1 = null;
		resFlag0 = null;
		resFlag1 = null;
		resFlag2 = null;
		resFlag3 = null;
		resFlags0 = null;
	}
	
	@org.junit.Test
	public void test_ResolveFlag_resolve() {
		assertEquals(ResolveFlag.resolve(0), resFlag0);
		assertEquals(ResolveFlag.resolve(1), resFlag1);
		assertEquals(ResolveFlag.resolve(2), resFlag2);
		assertEquals(ResolveFlag.resolve(3), resFlag3);
	}
	
	@org.junit.Test(expected = com.trolltech.qt.QNoSuchEnumValueException.class)
	public void test_ResolveFlag_resolveException() {
		assertEquals(ResolveFlag.resolve(-1), resFlag1);
	}
	
	@org.junit.Test
	public void test_ResolveFlag_value() {
		assertEquals(resFlag0.value(), 0);
		assertEquals(resFlag1.value(), 1);
		assertEquals(resFlag2.value(), 2);
		assertEquals(resFlag3.value(), 3);
	}
	
	@org.junit.Test
	public void test_ResolveFlag_createQFlags() {
		assertEquals(resFlags0.value(), resFlag0.value());
		assertEquals(resFlags1.value(), 3);
	}
	
	@org.junit.Test
	public void testSpecialValue_resolve() {
		assertEquals(SpecialValue.resolve(0), specVal0);
		assertEquals(SpecialValue.resolve(1), specVal1);
	}
	
	@org.junit.Test(expected = com.trolltech.qt.QNoSuchEnumValueException.class)
	public void testSpecialValue_resolveException() {
		assertEquals(SpecialValue.resolve(2), specVal0);
	}
	
	@org.junit.Test
	public void testSpecialValue_value() {
		assertEquals(specVal0.value(), 0);
		assertEquals(specVal1.value(), 1);
	}
	
	@org.junit.Test
	public void testToObject() {
		val = scriptButton.toObject();
		assertTrue("val.isObject()", val.isObject());
	}
	
	@org.junit.Test
	public void testToNumber() {
		val = testEngine.evaluate("5 - 2");
		assertTrue("val.isNumber()", val.isNumber());
	}
	
}
