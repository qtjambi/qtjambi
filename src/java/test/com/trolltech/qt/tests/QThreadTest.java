/**
 * Unit Test implementations for QThread.java
 * 
 * @author akoskm
 */

package com.trolltech.qt.tests;

import com.trolltech.examples.qtconcurrent.Map;
import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QThread;
import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QSignalMapper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class QThreadTest extends TestCase {
    
    private QThread qthread;
    
    private Runnable r;
    
    static {
	QtJambi_LibraryInitializer.init();
    }
    
    public QThreadTest(String name) {
	super(name);
    }

    public void setUp() {
	qthread = new QThread(r);
    }
    
    public void tearDown() {
	qthread = null;
    }
    
    public void testRun() {
	qthread.run();
	assertTrue(!(qthread.isAlive()));
	assertFalse(qthread.isAlive());
    }
    
    public void testInit() {
	assertTrue(qthread.starting != null);
	assertTrue(qthread.finished != null);
    }
    
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new QThreadTest("testInit"));
	suite.addTest(new QThreadTest("testRun"));
	return suite;
    }
    
}
