/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

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
