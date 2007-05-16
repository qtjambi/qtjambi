package com.trolltech.autotests;

import java.util.*;

import com.trolltech.qt.*;
import com.trolltech.autotests.generated.*;

import static org.junit.Assert.*;

import org.junit.*;

public class TestNamespace extends QApplicationTest {

    @Test public void exists() {
        try {
            assertTrue(Class.forName("com.trolltech.autotests.generated.ObjectA") != null);
            assertTrue(Class.forName("com.trolltech.autotests.generated.ObjectB") != null);
            assertTrue(Class.forName("com.trolltech.autotests.generated.ValueA") != null);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test public void testCalls() {
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        ValueA v = new ValueA();
        ValueA v2 = a.vFunc(v);

        assertEquals(a.aFunc(a), a);
        assertEquals(a.bFunc(b), b);
        assertEquals(a.vFunc(v), v);

        assertEquals(a.aFuncPrefixed(a), a);
        assertEquals(a.bFuncPrefixed(b), b);
        assertEquals(a.vFuncPrefixed(v), v);
   }

}
