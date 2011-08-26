/**
 * Unit Test implementations for QPair.java
 * 
 */

package com.trolltech.generatortests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.trolltech.qt.QPair;

public class TestQPair<T, S> {

	QPair<Integer, Integer> qp1;
	QPair<Integer, Integer> qp2;
	QPair<Integer, Boolean> qp3;
	QPair<Integer, Boolean> qp4;
	Object someObject;

	@org.junit.Before
	public void setUp() {
		qp1 = new QPair<Integer, Integer>(3, 5);
		qp2 = new QPair<Integer, Integer>(5, 3);
		qp3 = new QPair<Integer, Boolean>(1, null);
		qp4 = new QPair<Integer, Boolean>(null, true);
	}

	@org.junit.After
	public void tearDown() {
		qp1 = null;
		qp2 = null;
		qp3 = null;
		qp4 = null;
	}

	@org.junit.Test
	public void testEquals() {
		assertTrue(qp1.equals(qp1));
		assertFalse(qp1.equals(qp2));
		assertFalse(qp1.equals(qp3));
		assertFalse(qp1.equals(someObject));
	}

	@org.junit.Test
	public void testToString() {
		assertEquals(qp1.toString(), "Pair(3,5)");
		assertEquals(qp3.toString(), "Pair(1,null)");
		assertEquals(qp4.toString(), "Pair(null,true)");
	}

	@org.junit.Test
	public void testClone() {
		qp1 = qp2.clone();
		assertEquals(qp1, qp2);
	}

}
