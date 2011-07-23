/**
 * Unit Test implementations for Utilities.java
 * 
 * @author akoskm
 */

package com.trolltech.generatortests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.Utilities;
import com.trolltech.qt.Utilities.Configuration;
import com.trolltech.qt.Utilities.OperatingSystem;

public class TestUtilities extends TestCase {
    
    private String singleString;
    private String failString;
    private String strings;
    private String validLibrary;
    private String falseLibrary;
    private String thisOs;
    private String configuration;
    
    static {
	QtJambi_LibraryInitializer.init();
    }
    
    public TestUtilities(String name) {
	super(name);
    }

    @org.junit.Before
    public void setUp() {
	    singleString = "test";
	    failString = "bad";
	    strings = "test test1 test2";
	    validLibrary = "com_trolltech_qt_core";
	    falseLibrary = "com_trolltech_qt_kore";
	    thisOs = System.getProperty("os.name").toLowerCase();
	    configuration = System.getProperty("com.trolltech.qt.debug");
    }
    
    @org.junit.After
    public void tearDown() {
	singleString = null;
	failString = null;
	strings = null;
	validLibrary = null;
	falseLibrary = null;
	thisOs = null;
	
    }
    
    @org.junit.Test
    public void testMatchProperty() {
	System.setProperty(singleString, strings);
	assertTrue(Utilities.matchProperty(singleString, strings));
	assertFalse(Utilities.matchProperty(failString, strings));
    }
    
    @org.junit.Test
    public void testLoadLibrary() {
	assertTrue(Utilities.loadLibrary(validLibrary));
	assertFalse(Utilities.loadLibrary(falseLibrary));
    }
    
    @org.junit.Test
    public void testDecideOperatingSystem() {
	if(thisOs.startsWith("linux"))
	    assertTrue(Utilities.operatingSystem.equals(OperatingSystem.Linux));
	else if(thisOs.startsWith("mac os x"))
	    assertTrue(Utilities.operatingSystem.equals(OperatingSystem.MacOSX));
	else if(thisOs.startsWith("windows"))
	    assertTrue(Utilities.operatingSystem.equals(OperatingSystem.Windows));
	else
	    assertFalse(true);
    }
    
    @org.junit.Test
    public void testDecideConfiguration() {
	if(configuration != null)
	    assertTrue(Utilities.configuration.equals(Configuration.Debug));
	else
	    assertTrue(Utilities.configuration.equals(Configuration.Release));
    }
    
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new TestUtilities("testMatchProperty"));
	suite.addTest(new TestUtilities("testLoadLibrary"));
	suite.addTest(new TestUtilities("testDecideOperatingSystem"));
	suite.addTest(new TestUtilities("testDecideConfiguration"));
	return suite;
    }
    
}
