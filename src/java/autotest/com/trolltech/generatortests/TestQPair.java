/**
 * Unit Test implementations for QPair.java
 * 
 * @author akoskm
 */

package com.trolltech.generatortests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.trolltech.qt.QPair;

public class TestQPair <T, S> extends TestCase {

    QPair<Integer, Integer> qp1;
    QPair<Integer, Integer> qp2;

    public TestQPair(String name) {
	super(name);
    }

    @org.junit.Before
    public void setUp() throws Exception {
	qp1 = new QPair<Integer, Integer>(3, 5);
	qp2 = new QPair<Integer, Integer>(5, 3);
    }

    @org.junit.After
    public void tearDown() throws Exception {
	qp1 = null;
	qp2 = null;
    }

    @org.junit.Test
    public void testEquals() {
	assertTrue(qp1.equals(qp1));
	assertFalse(qp1.equals(qp2));
    }

    @org.junit.Test
    public void testToString() {
	assertEquals(qp1.toString(), "Pair(3,5)");
    }

    @org.junit.Test
    public void testClone() {
	qp1 = qp2.clone();
	assertEquals(qp1, qp2);
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestQPair<Object, Object>("testEquals"));
	suite.addTest(new TestQPair<Object, Object>("testToString"));
	suite.addTest(new TestQPair<Object, Object>("testClone"));
	return suite;
    }

}
