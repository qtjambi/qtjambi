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
import com.trolltech.qt.script.QScriptValue.PropertyFlag;
import com.trolltech.qt.script.QScriptValue.PropertyFlags;
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
	
	private PropertyFlag propFlag1;
	private PropertyFlag propFlag2;
	private PropertyFlag propFlag4;
	private PropertyFlag propFlag8;
	private PropertyFlag propFlag16;
	private PropertyFlag propFlag32;
	private PropertyFlag propFlag2048;
	private PropertyFlag propFlag_16777216;
	
	private PropertyFlags propFlags1;
	private PropertyFlags propFlags32;
	
	@org.junit.Before
	public void setUp() {
		testEngine = new QScriptEngine();
		button = new QPushButton();
		scriptButton = testEngine.newQObject(button);
		
		specVal0 = SpecialValue.NullValue;
		specVal1 = SpecialValue.UndefinedValue;
		
		resFlag0 = ResolveFlag.ResolveLocal;
		resFlag1 = ResolveFlag.ResolvePrototype;
		resFlag2 = ResolveFlag.ResolveScope;
		resFlag3 = ResolveFlag.ResolveFull;
		
		resFlags0 = ResolveFlag.createQFlags(resFlag0);
		resFlags1 = new ResolveFlags(3);
		
		propFlag1 = PropertyFlag.ReadOnly;
		propFlag2 = PropertyFlag.Undeletable;
		propFlag4 = PropertyFlag.SkipInEnumeration;
		propFlag8 = PropertyFlag.PropertyGetter;
		propFlag16 = PropertyFlag.PropertySetter;
		propFlag32 = PropertyFlag.QObjectMember;
		propFlag2048 = PropertyFlag.KeepExistingFlags;
		propFlag_16777216 = PropertyFlag.UserRange;
		
		propFlags1 = PropertyFlag.createQFlags(propFlag1);
		propFlags32 = new PropertyFlags(32);
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
		resFlags1 = null;
		
		propFlag1 = null;
		propFlag2 = null;
		propFlag4 = null;
		propFlag8 = null;
		propFlag16 = null;
		propFlag32 = null;
		propFlag2048 = null;
		propFlag_16777216 = null;
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
	public void test_PropertyFlag_resolve() {
		assertEquals(PropertyFlag.resolve(1), propFlag1);
		assertEquals(PropertyFlag.resolve(2), propFlag2);
		assertEquals(PropertyFlag.resolve(4), propFlag4);
		assertEquals(PropertyFlag.resolve(8), propFlag8);
		assertEquals(PropertyFlag.resolve(16), propFlag16);
		assertEquals(PropertyFlag.resolve(32), propFlag32);
		assertEquals(PropertyFlag.resolve(2048), propFlag2048);
		assertEquals(PropertyFlag.resolve(-16777216), propFlag_16777216);
	}
	
	@org.junit.Test(expected = com.trolltech.qt.QNoSuchEnumValueException.class)
	public void test_PorpertyFlag_resolveException() {
		assertEquals(PropertyFlag.resolve(-1), propFlag1);
	}
	
	@org.junit.Test
	public void test_PropertyFlag_value() {
		assertEquals(propFlag1.value(), 1);
		assertEquals(propFlag2.value(), 2);
		assertEquals(propFlag4.value(), 4);
		assertEquals(propFlag8.value(), 8);
		assertEquals(propFlag16.value(), 16);
		assertEquals(propFlag32.value(), 32);
		assertEquals(propFlag2048.value(), 2048);
		assertEquals(propFlag_16777216.value(), -16777216);
	}
	
	@org.junit.Test
	public void test_PropertyFlag_createQFlags() {
		assertEquals(propFlags1.value(), propFlag1.value());
		assertEquals(propFlags32.value(), 32);
	}
	
	@org.junit.Test
	public void testSpecialValue_resolve() {
		assertEquals(SpecialValue.resolve(0), specVal0);
		assertEquals(SpecialValue.resolve(1), specVal1);
	}
	
	@org.junit.Test(expected = com.trolltech.qt.QNoSuchEnumValueException.class)
	public void testSpecialValue_resolveException() {
		assertEquals(SpecialValue.resolve(-1), specVal0);
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
