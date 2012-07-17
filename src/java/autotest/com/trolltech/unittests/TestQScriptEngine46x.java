/**
 * Unit Test implementations for QScriptEngine.java
 * 
 * TODO write tests for memory, etc.
 * 
 */

package com.trolltech.unittests;

import static com.trolltech.unittests.support.MatcherQtVersion.isQtGe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import com.trolltech.autotests.QApplicationTest;
import com.trolltech.qt.script.QScriptEngine;
import com.trolltech.qt.script.QScriptProgram;
import com.trolltech.unittests.support.QtVersion;

/**
 * This class tests the features that are marked as arriving
 *  since a certain Qt version as per classname.
 * These tests are isolated into a different *.java file to
 *  allow for excluding from compilation, since they will fail
 *  due to missing symbols.
 */
public class TestQScriptEngine46x extends QApplicationTest {

	private QScriptEngine testEngine;

	// Qt documentation says API since 4.7.x+ but generator says different
	private QScriptProgram qsprogram; // API since 4.6.x+

	@org.junit.Before
	public void setUp() {
		testEngine = new QScriptEngine();
		qsprogram = new QScriptProgram("5 - 2");
	}

	@org.junit.After
	public void tearDown() {
		testEngine = null;
	}

	@org.junit.Test
	public void testEvaluateQScriptPorgram() {
		assumeThat(QtVersion.getQtVersion(), isQtGe(QtVersion.VERSION_4_6));  // Version check

		assertEquals("qsprogram", 3, testEngine.evaluate(qsprogram).toInt32());
	}

}
