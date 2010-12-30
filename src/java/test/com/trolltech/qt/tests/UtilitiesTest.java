/**
 * Unit Test implementations for Utilities.java
 * 
 * @author akoskm
 */

package com.trolltech.qt.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.Utilities;
import com.trolltech.qt.Utilities.Configuration;
import com.trolltech.qt.Utilities.OperatingSystem;

public class UtilitiesTest extends TestCase {
    
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
    
    public UtilitiesTest(String name) {
	super(name);
    }

    public void setUp() {
	    singleString = "test";
	    failString = "bad";
	    strings = "test test1 test2";
	    validLibrary = "com_trolltech_qt_core";
	    falseLibrary = "com_trolltech_qt_kore";
	    thisOs = System.getProperty("os.name").toLowerCase();
	    configuration = System.getProperty("com.trolltech.qt.debug");
    }
    
    public void tearDown() {
	singleString = null;
	failString = null;
	strings = null;
	validLibrary = null;
	falseLibrary = null;
	thisOs = null;
	
    }
    
    public void testMatchProperty() {
	System.setProperty(singleString, strings);
	assertTrue(Utilities.matchProperty(singleString, strings));
	assertFalse(Utilities.matchProperty(failString, strings));
    }
    
    public void testLoadLibrary() {
	assertTrue(Utilities.loadLibrary(validLibrary));
	assertFalse(Utilities.loadLibrary(falseLibrary));
    }
    
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
    
    public void testDecideConfiguration() {
	if(configuration != null)
	    assertTrue(Utilities.configuration.equals(Configuration.Debug));
	else
	    assertTrue(Utilities.configuration.equals(Configuration.Release));
    }
    
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new UtilitiesTest("testMatchProperty"));
	suite.addTest(new UtilitiesTest("testLoadLibrary"));
	suite.addTest(new UtilitiesTest("testDecideOperatingSystem"));
	suite.addTest(new UtilitiesTest("testDecideConfiguration"));
	return suite;
    }
    
}
