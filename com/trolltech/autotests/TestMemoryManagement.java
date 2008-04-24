package com.trolltech.autotests;

import static org.junit.Assert.*;
import org.junit.*;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.internal.QtJambiDebugTools;

// Attempt at complete test for general memory leaks and crashes
// Should test that all the general cases work as intended by default.
public class TestMemoryManagement {
	
    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
        if (!QtJambiDebugTools.hasDebugTools())
        	throw new RuntimeException("These tests can only be run when Qt Jambi is compiled with DEFINES += QTJAMBI_DEBUG_TOOLS");                	
    }
    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();
    }
	
	
	// Types: Normal object type with shell class
	//        Normal object type without shell class
	//        Normal value type
	//        QObject
	//        GUI object type
	//        GUI value type
	//
	// Creation: In Java
	//           In Native
	//
	// Ownership: Java Ownership 
	//            Split Ownership
	//            Native Ownership
	//
	// Ways to delete: finalization	
	//                 dispose
	//                 Deleted in native (split and native ownership)
	//                 invalidate (non-Java-Ownership only)
	//
	// Things to check: 
	//    1. disposed() is called when it needs to be
	//    2. The destructor of the class is called (when applicable, i.e. not for non-polymorphic types
	//       that are deleted from C++) Both for the destructor of the shell class and the destructor
	//       function of the class (if it doesn't have a virtual destructor)
	//    3. The QtJambiLink for the object is deleted.
    //    4. The Java object is finalized.
    //
    // For these tests you need to compile Qt Jambi with DEFINES += QTJAMBI_DEBUG_TOOLS so that you
    // get compiled-in invocation counts. 
    	
	@Test
	public void finalize_NormalObject_CreatedInJava_JavaOwnership() {
		
	}
	
}
