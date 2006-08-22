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

import com.trolltech.qtest.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import com.trolltech.autotests.generated.NativePointerTester;


public class TestNativePointer extends QTestCase {
    public void run_createBooleanPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Boolean);
            ptr.setBooleanValue(true);

            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Boolean);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.booleanValue(), true);
        }

        {
            QNativePointer ptr = new QNativePointer(
                    QNativePointer.Type.Boolean, 10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Boolean);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setBooleanAt(i, i % 2 == 0 ? true : false);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.booleanAt(i), i % 2 == 0 ? true : false);
        }
    }

    public void run_createBytePointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Byte);
            ptr.setByteValue((byte) 100);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Byte);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.byteValue(), (byte) 100);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Byte,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Byte);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setByteAt(i, (byte) i);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.byteAt(i), (byte) i);
        }
    }

    public void run_createCharPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Char);
            ptr.setCharValue((char) '!');
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Char);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.charValue(), '!');
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Char,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Char);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setCharAt(i, (char) ('a' + i));
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.charAt(i), (char) ('a' + i));
        }
    }

    public void run_createShortPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Short);
            ptr.setShortValue((short) 10000);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Short);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.shortValue(), (short) 10000);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Short,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Short);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setShortAt(i, (short) (i * i));
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.shortAt(i), (short) (i * i));
        }
    }

    public void run_createIntPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Int);
            ptr.setIntValue(10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Int);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.intValue(), 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Int, 10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Int);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setIntAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.intAt(i), i * i);
        }
    }

    public void run_createLongPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Long);
            ptr.setLongValue(10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Long);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.longValue(), (long) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Long,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Long);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setLongAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.longAt(i), (long) (i * i));
        }
    }

    public void run_createFloatPointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Float);
            ptr.setFloatValue(10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Float);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.floatValue(), (float) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Float,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Float);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setFloatAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.floatAt(i), (float) (i * i));
        }
    }

    public void run_createDoublePointer() {
        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Double);
            ptr.setDoubleValue(10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Double);
            QCOMPARE(ptr.indirections(), 1);
            QCOMPARE(ptr.doubleValue(), (double) 10);
        }

        {
            QNativePointer ptr = new QNativePointer(QNativePointer.Type.Double,
                    10);
            QVERIFY(!ptr.isNull());
            QCOMPARE(ptr.type(), QNativePointer.Type.Double);
            QCOMPARE(ptr.indirections(), 1);
            for (int i = 0; i < 10; ++i)
                ptr.setDoubleAt(i, i * i);
            for (int i = 0; i < 10; ++i)
                QCOMPARE(ptr.doubleAt(i), (double) i * i);
        }
    }

    private static final String pointerToString(QNativePointer p) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; p.byteAt(i) != 0; ++i)
            b.append((char) p.byteAt(i));
        return b.toString();
    }

    public void run_createPointer() {
        try {
            String text[] = new String[4];
            text[0] = "once";
            text[1] = "upon";
            text[2] = "a";
            text[3] = "time";

            QNativePointer ptr = QNativePointer.createCharPointerPointer(text);

            QCOMPARE(pointerToString(ptr.pointerAt(0)), "once");
            QCOMPARE(pointerToString(ptr.pointerAt(1)), "upon");
            QCOMPARE(pointerToString(ptr.pointerAt(2)), "a");
            QCOMPARE(pointerToString(ptr.pointerAt(3)), "time");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void data_testCreateCharPointer() {
        defineDataStructure(String.class, "value");
        addDataSet("simpl", "simple");
        addDataSet("complex", "A sort of long string that contains some numbers, (1....4) among other stuff...");
    }    
    public void run_testCreateCharPointer() {
        String s = getParameter("value");
        QNativePointer np = QNativePointer.createCharPointer(s);
        QCOMPARE(s, pointerToString(np));
    }
    
    
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
                        QVERIFY(false, "unhandled case...");
                        break;
                    }
                } catch (Exception e) {
                    caught = true;
                }
                QCOMPARE(caught, i != j);
            }
        }
    }
    
    public void run_testInOut() {
        NativePointerTester npt = new NativePointerTester();
        
        {
            QNativePointer np_int = new QNativePointer(QNativePointer.Type.Int);
            np_int.setIntValue(14);
            int returned = npt.testInt(np_int, 15);
            QCOMPARE(returned, 14);
            QCOMPARE(np_int.intValue(), 15);
        }
        
        {
            QNativePointer np_string = new QNativePointer(QNativePointer.Type.String);
            np_string.setStringValue("First");
            String returned = npt.testString(np_string, "Second");
            QCOMPARE(returned, "First");
            QCOMPARE(np_string.stringValue(), "Second");
        }
    }
    
    public static void main(String args[])
    {
        runTest(new TestNativePointer());
    }    
}
