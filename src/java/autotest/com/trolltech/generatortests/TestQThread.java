/**
 * Unit Test implementations for QThread.java
 * 
 * @author akoskm
 */

package com.trolltech.generatortests;

import com.trolltech.qt.QThread;
import com.trolltech.qt.QtJambi_LibraryInitializer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestQThread extends TestCase {
    
    private QThread qthread;
    
    private Runnable r;
    
    static {
	QtJambi_LibraryInitializer.init();
    }
    
    public TestQThread(String name) {
	super(name);
    }

    @org.junit.Before
    public void setUp() {
	qthread = new QThread(r);
    }
    
    @org.junit.After
    public void tearDown() {
	qthread = null;
    }
    
    @org.junit.Test
    public void testRun() {
	qthread.run();
	assertFalse(qthread.isAlive());
    }
    
    public void testInit() {
	assertNotNull(qthread.starting);
	assertNotNull(qthread.finished);
    }
    
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestQThread("testInit"));
	suite.addTest(new TestQThread("testRun"));
	return suite;
    }
    
}
