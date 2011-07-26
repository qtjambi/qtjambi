/**
 * Unit Test implementations for Utilities.java
 * 
 * @author akoskm
 */

package com.trolltech.generatortests;

import static org.junit.Assert.*;

import java.io.File;

import com.trolltech.qt.QtJambi_LibraryInitializer;
import com.trolltech.qt.Utilities;
import com.trolltech.qt.Utilities.Configuration;
import com.trolltech.qt.Utilities.OperatingSystem;

public class TestUtilities {

	private String singleString;
	private String failString;
	private String strings;
	private String[] emptyStrings;
	private String validLibrary;
	private String falseLibrary;
	private static String thisOs;
	private static String configuration;
	private File tmpDir;
	private static String user;
	private static String arch;

	@org.junit.BeforeClass
	public static void setUpClass() {
		QtJambi_LibraryInitializer.init();
		user = System.getProperty("user.name");
		arch = System.getProperty("os.arch");
		thisOs = System.getProperty("os.name").toLowerCase();
		configuration = System.getProperty("com.trolltech.qt.debug");
	}

	@org.junit.Before
	public void setUp() {
		System.out.println("*");
		singleString = "test";
		failString = "bad";
		strings = "test test1 test2";
		emptyStrings = new String[0];
		validLibrary = "com_trolltech_qt_core";
		falseLibrary = "com_trolltech_qt_kore";
	}

	@org.junit.After
	public void tearDown() {
		singleString = null;
		failString = null;
		strings = null;
		emptyStrings = null;
		validLibrary = null;
		falseLibrary = null;

	}

	/*
	 * return value != null; //isn't covered
	 */
	@org.junit.Test
	public void testMatchProperty() {
		System.setProperty(singleString, strings);
		assertTrue(Utilities.matchProperty(singleString, strings));
		assertFalse(Utilities.matchProperty(failString, strings));
		assertTrue(Utilities.matchProperty(singleString, emptyStrings));
		assertFalse(Utilities.matchProperty(failString, emptyStrings));
		assertTrue(Utilities.matchProperty(singleString, (String[]) null));
		assertFalse(Utilities.matchProperty(failString, (String[]) null));
		assertFalse(Utilities.matchProperty(singleString, failString));
	}

	@org.junit.Test
	public void testJambiTempDir() {
		tmpDir = Utilities.jambiTempDir();
		assertTrue(tmpDir.getName().startsWith("QtJambi_" + user + "_" + arch + "_" + Utilities.VERSION_STRING));
	}

	@org.junit.Test
	public void testLoadLibrary() {
		assertTrue(Utilities.loadLibrary(validLibrary));
		assertFalse(Utilities.loadLibrary(falseLibrary));
	}

	@org.junit.Test
	public void testUncpackPlugins() {
		assertNull(Utilities.unpackPlugins());
	}

	@org.junit.Test
	public void testDecideOperatingSystem() {
		if (thisOs.startsWith("linux"))
			assertTrue(Utilities.operatingSystem.equals(OperatingSystem.Linux));
		else if (thisOs.startsWith("mac os x"))
			assertTrue(Utilities.operatingSystem.equals(OperatingSystem.MacOSX));
		else if (thisOs.startsWith("windows"))
			assertTrue(Utilities.operatingSystem.equals(OperatingSystem.Windows));
		else
			assertFalse(true);
	}

	@org.junit.Test
	public void testDecideConfiguration() {
		if (configuration != null)
			assertTrue(Utilities.configuration.equals(Configuration.Debug));
		else
			assertTrue(Utilities.configuration.equals(Configuration.Release));
	}

}
