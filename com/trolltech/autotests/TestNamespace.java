/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import com.trolltech.autotests.generated.*;

import static org.junit.Assert.*;

import org.junit.*;

public class TestNamespace extends QApplicationTest {

    @Test public void exists() throws ClassNotFoundException {
        assertTrue(Class.forName("com.trolltech.autotests.generated.ObjectA") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.ObjectB") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.ValueA") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_InterfaceA") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_InterfaceAInterface") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_ObjectC") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_ObjectD") != null);
        assertTrue(Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_ValueB") != null);
    }

    @Test public void testInheritance() throws ClassNotFoundException {
        Class<?> clazzC = Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_ObjectC");
        Class<?> clazzA = Class.forName("com.trolltech.autotests.generated.ObjectA");
        assertEquals(clazzA, clazzC.getSuperclass());

        Class<?> clazzD = Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_ObjectD");
        assertEquals(clazzC, clazzD.getSuperclass());

        Class<?> interfaces[] = clazzD.getInterfaces();
        assertEquals(1, interfaces.length);

        Class<?> interfaceA = Class.forName("com.trolltech.autotests.generated.NameSpace_NameSpace2_NameSpace3_InterfaceAInterface");
        assertEquals(interfaceA, interfaces[0]);
    }

    @Test public void testCalls() {
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        ValueA v = new ValueA();

        assertEquals(a.aFunc(a), a);
        assertEquals(a.bFunc(b), b);
        assertEquals(a.vFunc(v), v);

        assertEquals(a.aFuncPrefixed(a), a);
        assertEquals(a.bFuncPrefixed(b), b);
        assertEquals(a.vFuncPrefixed(v), v);

        {
            NameSpace_NameSpace2_NameSpace3_ObjectD d = new NameSpace_NameSpace2_NameSpace3_ObjectD("fooBar");
            NameSpace_NameSpace2_NameSpace3_ObjectC c = new NameSpace_NameSpace2_NameSpace3_ObjectC("barFoo");

            NameSpace_NameSpace2_NameSpace3_ObjectC temp = c.fooBar(d);
            assertEquals("fooBar", temp.str());
        }
   }

}
