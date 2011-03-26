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

    QPair<T, S> qp1;
    QPair<T ,S> qp2;

    public TestQPair(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	qp1 = new QPair(3, 5);
	qp2 = new QPair(5, 3);
    }

    public void tearDown() throws Exception {
	qp1 = null;
	qp2 = null;
    }

    public void testEquals() {
	assertTrue(qp1.equals(qp1));
	assertFalse(qp1.equals(qp2));
    }

    public void testToString() {
	assertTrue(qp1.toString().equals("Pair(3,5)"));
	assertFalse(!(qp1.toString().equals("Pair(3,5)")));
    }

    public void testClone() {
	qp1 = qp2.clone();
	assertTrue(qp1.equals(qp2));
	assertFalse(!(qp1.equals(qp2)));
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestQPair("testEquals"));
	suite.addTest(new TestQPair("testToString"));
	suite.addTest(new TestQPair("testClone"));
	return suite;
    }

}
