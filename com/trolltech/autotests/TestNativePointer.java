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

import java.util.*;

import com.trolltech.qt.*;

import com.trolltech.autotests.generated.NativePointerTester;
import static org.junit.Assert.*;

import org.junit.*;

public class TestNativePointer extends QApplicationTest {
    @Test
    public void run_createBooleanPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Boolean);
            ptr.setBooleanValue(true);

            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Boolean);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.booleanValue(), true);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Boolean, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Boolean);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setBooleanAt(i, i % 2 == 0 ? true : false);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.booleanAt(i), i % 2 == 0 ? true : false);
        }
    }

    @Test
    public void run_createBytePointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Byte);
            ptr.setByteValue((byte) 100);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Byte);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.byteValue(), (byte) 100);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Byte, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Byte);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setByteAt(i, (byte) i);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.byteAt(i), (byte) i);
        }
    }

    @Test
    public void run_createCharPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Char);
            ptr.setCharValue('!');
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Char);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.charValue(), '!');
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Char, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Char);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setCharAt(i, (char) ('a' + i));
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.charAt(i), (char) ('a' + i));
        }
    }

    @Test
    public void run_createShortPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Short);
            ptr.setShortValue((short) 10000);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Short);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.shortValue(), (short) 10000);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Short, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Short);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setShortAt(i, (short) (i * i));
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.shortAt(i), (short) (i * i));
        }
    }

    @Test
    public void run_createIntPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Int);
            ptr.setIntValue(10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Int);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.intValue(), 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Int, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Int);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setIntAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.intAt(i), i * i);
        }
    }

    @Test
    public void run_createLongPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Long);
            ptr.setLongValue(10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Long);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.longValue(), (long) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Long, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Long);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setLongAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.longAt(i), (long) (i * i));
        }
    }

    @Test
    public void run_createFloatPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Float);
            ptr.setFloatValue(10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Float);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.floatValue(), (float) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Float, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Float);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setFloatAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.floatAt(i), (float) (i * i));
        }
    }

    @Test
    public void run_createDoublePointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Double);
            ptr.setDoubleValue(10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Double);
            assertEquals(ptr.indirections(), 1);
            assertEquals(ptr.doubleValue(), (double) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Double, 10);
            assertTrue(!ptr.isNull());
            assertEquals(ptr.type(), QNativePointer.Type.Double);
            assertEquals(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setDoubleAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                assertEquals(ptr.doubleAt(i), (double) i * i);
        }
    }

    private static final String pointerToString(QNativePointer p) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; p.byteAt(i) != 0; ++i)
            b.append((char) p.byteAt(i));
        return b.toString();
    }

    @Test
    public void run_createPointer() {
        try {
            String text[] = new String[4];
            text[0] = "once";
            text[1] = "upon";
            text[2] = "a";
            text[3] = "time";

            QNativePointer ptr = QNativePointer.createCharPointerPointer(text);

            assertEquals(pointerToString(ptr.pointerAt(0)), "once");
            assertEquals(pointerToString(ptr.pointerAt(1)), "upon");
            assertEquals(pointerToString(ptr.pointerAt(2)), "a");
            assertEquals(pointerToString(ptr.pointerAt(3)), "time");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<String> data;

    @Before
    public void setUp() {
        data = new Vector<String>();
        data.add("simple");
        data.add("A sort of long string that contains some numbers, (1....4) among other stuff...");
    }

    @Test
    public void run_testCreateCharPointer() {
        for (Iterator<String> iter = data.iterator(); iter.hasNext();) {
            String s = iter.next();

            QNativePointer np = QNativePointer.createCharPointer(s);
            assertEquals(s, pointerToString(np));
        }
    }

    @Test
    public void run_badAccess() {
        QNativePointer ptrs[] = new QNativePointer[8];
        ptrs[0] = new QNativePointer(QNativePointer.Type.Boolean);
        ptrs[1] = new QNativePointer(QNativePointer.Type.Byte);
        ptrs[2] = new QNativePointer(QNativePointer.Type.Char);
        ptrs[3] = new QNativePointer(QNativePointer.Type.Short);
        ptrs[4] = new QNativePointer(QNativePointer.Type.Int);
        ptrs[5] = new QNativePointer(QNativePointer.Type.Long);
        ptrs[6] = new QNativePointer(QNativePointer.Type.Float);
        ptrs[7] = new QNativePointer(QNativePointer.Type.Double);

        ptrs[0].setBooleanValue(true);
        ptrs[1].setByteValue((byte) 1);
        ptrs[2].setCharValue((char) 2);
        ptrs[3].setShortValue((short) 3);
        ptrs[4].setIntValue(4);
        ptrs[5].setLongValue(5);
        ptrs[6].setFloatValue(6);
        ptrs[7].setDoubleValue(7);

        for (int i = 0; i < ptrs.length; ++i) {
            QNativePointer ptr = ptrs[i];
            for (int j = 0; j < 8; ++j) {
                boolean caught = false;
                try {
                    switch (j) {
                    case 0:
                        ptr.booleanValue();
                        break;
                    case 1:
                        ptr.byteValue();
                        break;
                    case 2:
                        ptr.charValue();
                        break;
                    case 3:
                        ptr.shortValue();
                        break;
                    case 4:
                        ptr.intValue();
                        break;
                    case 5:
                        ptr.longValue();
                        break;
                    case 6:
                        ptr.floatValue();
                        break;
                    case 7:
                        ptr.doubleValue();
                        break;
                    default:
                        assertEquals(false, "unhandled case...");
                        break;
                    }
                } catch (Exception e) {
                    caught = true;
                }
                assertEquals(caught, i != j);
            }
        }
    }

    @Test
    public void run_testInOut() {
        NativePointerTester npt = new NativePointerTester();

        {
            QNativePointer np_int = new QNativePointer(QNativePointer.Type.Int);
            np_int.setIntValue(14);
            int returned = npt.testInt(np_int, 15);
            assertEquals(returned, 14);
            assertEquals(np_int.intValue(), 15);
        }

        {
            QNativePointer np_string = new QNativePointer(QNativePointer.Type.String);
            np_string.setStringValue("First");
            String returned = npt.testString(np_string, "Second");
            assertEquals(returned, "First");
            assertEquals(np_string.stringValue(), "Second");
        }
    }
}
