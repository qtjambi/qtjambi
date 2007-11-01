package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.trolltech.qt.QtProperty;
import com.trolltech.qt.QtPropertyDesignable;
import com.trolltech.qt.QtPropertyReader;
import com.trolltech.qt.QtPropertyResetter;
import com.trolltech.qt.QtPropertyWriter;
import com.trolltech.qt.core.QObject;

public class TestProperties extends QApplicationTest {
    
    private static class FullOfProperties extends QObject 
    {
        private boolean isDesignableTest;
        private FullOfProperties(boolean isDesignableTest) {
            this.isDesignableTest = isDesignableTest;
        }
        
        public final int ordinaryProperty() { return 0; }
        public final void setOrdinaryProperty(int i) { }
        
        @QtPropertyReader(name="annotatedProperty")
        public final int fooBar() { return 0; }
        
        @QtPropertyWriter(name="annotatedProperty")
        public final void fooBarSetIt(int i) { }
                
        @QtPropertyReader()
        public final int ordinaryReadOnlyProperty() { return 0; }

        @QtPropertyReader()
        public final int readOnlyProperty() { return 0; }
        
        @QtPropertyWriter(enabled=false)
        public final void setReadOnlyProperty(int i) { }
        
        @QtPropertyDesignable(value="false")
        public final int ordinaryNonDesignableProperty() { return 0; }
        
        public final void setOrdinaryNonDesignableProperty(int i) { }
        
        @QtPropertyDesignable(value="false")
        @QtPropertyReader(name="annotatedNonDesignableProperty")
        public final int fooBarXyz() { return 0; }
        @QtPropertyWriter()
        public final void setAnnotatedNonDesignableProperty(int i) { }
                
        public final boolean hasBooleanProperty() { return false; }
        public final void setBooleanProperty(boolean b) { }
        
        public final boolean isOtherBooleanProperty() { return false; }
        public final void setOtherBooleanProperty(boolean b) { }
        
        @QtPropertyReader
        public final int resettableProperty() { return 0; }
        
        @QtPropertyWriter
        public final void setResettableProperty(int i) { }
        
        @QtPropertyResetter
        public final void resetResettableProperty() { }
    }
    
    private static class ExpectedValues {
        private boolean writable;
        private boolean resettable;
        private boolean designable;
        private String name;
        
        private ExpectedValues(String name, boolean writable, boolean resettable, boolean designable)
        {
            this.name = name;        
            this.writable = writable;
            this.resettable = resettable;
            this.designable = designable;
        }
    }
    
    @Test
    public void testProperties() {
        ExpectedValues expectedValues[] = 
        {                 
                new ExpectedValues("ordinaryProperty", true, false, true),
                new ExpectedValues("annotatedProperty", true, false, true),
                new ExpectedValues("ordinaryReadOnlyProperty", false, false, true),
                new ExpectedValues("readOnlyProperty", false, false, true),
                new ExpectedValues("ordinaryNonDesignableProperty", true, false, false),
                new ExpectedValues("annotatedNonDesignableProperty", true, false, false),
                new ExpectedValues("booleanProperty", true, false, true),
                new ExpectedValues("otherBooleanProperty", true, false, true),
                new ExpectedValues("resettableProperty", true, true, true),
                new ExpectedValues("objectName", true, false, true),
                new ExpectedValues("parent", true, false, true)
        };
                
        FullOfProperties fop = new FullOfProperties(true);
        List<QtProperty> properties = fop.properties();
        
        assertEquals(expectedValues.length, properties.size());
        for (ExpectedValues e : expectedValues) {
            System.err.println("Current property: " + e.name);
            boolean found = false;
            for (QtProperty property : properties) {
                if (property.name().equals(e.name)) {                    
                    assertEquals(e.writable, property.isWritable());
                    assertEquals(e.resettable, property.isResettable());
                    assertEquals(e.designable, property.isDesignable());
                    found = true;
                    break;
                }
            }
            assertTrue(found);            
        }        
    }
    
}
