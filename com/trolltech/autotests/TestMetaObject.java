package com.trolltech.autotests;

import java.util.List;

import com.trolltech.autotests.generated.*;
import com.trolltech.qt.*;
import static org.junit.Assert.*;
import org.junit.*;

/**
 * Testing the fake meta object.
 * @author eblomfel 
 */
public class TestMetaObject extends QApplicationTest {
    
    private static class SignalsAndSlotsSubclass extends SignalsAndSlots {
        enum YoYoYo {
            Yo,
            YoYo,
            YoYoYo
        }
        
        enum HeyYo implements QtEnumerator {
            Hey(0x4),
            Yo(1123);
            
            HeyYo(int value) { this.value = value; }
            public int value() { return value; }

            private final int value;
        }
        
        enum FlipModeSquad implements QtEnumerator {
            Flip(1),
            Mode(2),
            Squad(4);
            
            FlipModeSquad(int value) { this.value = value; }
            public int value() { return value; }
            
            private final int value;
        }
        
        public static class FlipModeSquads extends com.trolltech.qt.QFlags<FlipModeSquad> {
            private static final long serialVersionUID = 1L;
            public FlipModeSquads(FlipModeSquad ... args) { super(args); }
            public FlipModeSquads(int value) { setValue(value); }            
        }
    }
    
    @Test public void regularEnumDeclarations() {
        SignalsAndSlotsSubclass sass = new SignalsAndSlotsSubclass();
        
        assertEquals(3, sass.numberOfEnumTypes());
        
        assertFalse(sass.isFlagsType("YoYoYo"));
        
        {
            List<String> names = sass.namesOfEnumType("YoYoYo");
            assertEquals(3, names.size());
            
            assertEquals("Yo", names.get(0));
            assertEquals("YoYo", names.get(1));
            assertEquals("YoYoYo", names.get(2));
        }
        
        {
            List<Integer> values = sass.valuesOfEnumType("YoYoYo");
            assertEquals(3, values.size());
            
            assertEquals(0, values.get(0));
            assertEquals(1, values.get(1));
            assertEquals(2, values.get(2));
        }                
    }
    
    @Test public void qtEnumeratorDeclarations() {
        SignalsAndSlotsSubclass sass = new SignalsAndSlotsSubclass();
                
        assertFalse(sass.isFlagsType("HeyYo"));
        
        {
            List<String> names = sass.namesOfEnumType("HeyYo");
            assertEquals(2, names.size());
            
            assertEquals("Hey", names.get(0));
            assertEquals("Yo", names.get(1));
        }
        
        {
            List<Integer> values = sass.valuesOfEnumType("HeyYo");
            assertEquals(2, values.size());
            
            assertEquals(0x4, values.get(0));
            assertEquals(1123, values.get(1));
        }                       
    }
    
    @Test public void flagsDeclarations() {
        SignalsAndSlotsSubclass sass = new SignalsAndSlotsSubclass();
        
        assertTrue(sass.isFlagsType("FlipModeSquad"));
        
        {
            List<String> names = sass.namesOfEnumType("FlipModeSquad");
            assertEquals(3, names.size());
            
            assertEquals("Flip", names.get(0));
            assertEquals("Mode", names.get(1));
            assertEquals("Squad", names.get(2));
        }
        
        {
            List<Integer> values = sass.valuesOfEnumType("FlipModeSquad");
            assertEquals(3, values.size());
            
            assertEquals(1, values.get(0));
            assertEquals(2, values.get(1));
            assertEquals(4, values.get(2));
        }                       
        
    }
}
