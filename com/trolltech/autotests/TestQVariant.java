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
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

public class TestQVariant extends QApplicationTest {

    @Test
    public void run_QVariantString() {
        Object variant = "this is my string";
        String expectedString = "this is my string";
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = true;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime(0, 0);
        QDateTime expectedDateTime = new QDateTime(expectedDate, expectedTime);
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("Normal string", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true, expectedBitArray, false, expectedChar,
                false, expectedDate, true, expectedTime, true, expectedDateTime, true, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false, expectedRegExp, false,
                expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantByteArray() {
        Object variant = new QByteArray("this is my string");
        String expectedString = "this is my string";
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("this is my string");
        int expectedInt = 0;
        boolean expectedBool = false;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime();
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("QByteArray", variant, expectedDouble, true, expectedString, true, expectedByteArray, true, expectedInt, true, expectedBool, false, expectedBitArray,
                false, expectedChar, false, expectedDate, false, expectedTime, false, expectedDateTime, false, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantObject() {
        Object variant = new QObject();
        String expectedString = "";
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = false;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime(expectedDate, expectedTime);
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("java.lang.Object", variant, expectedDouble, false, expectedString, true, expectedByteArray, false, expectedInt, false, expectedBool, false,
                expectedBitArray, false, expectedChar, false, expectedDate, false, expectedTime, false, expectedDateTime, false, expectedPoint, false, expectedPointF, false, expectedRect, false,
                expectedRectF, false, expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantStringDouble() {
        Object variant = "123.456";
        String expectedString = "123.456";
        double expectedDouble = 123.456;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = true;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime(12, 0, 6);
        QDateTime expectedDateTime = new QDateTime();
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("String double", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true, expectedBitArray,
                false, expectedChar, false, expectedDate, true, expectedTime, true, expectedDateTime, true, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantByteArrayDouble() {
        Object variant = new QByteArray("456.789");;
        String expectedString = "456.789";
        double expectedDouble = 456.789;
        QByteArray expectedByteArray = new QByteArray("456.789");
        int expectedInt = 0;
        boolean expectedBool = false;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime(expectedDate, expectedTime);
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("Byte array double", variant, expectedDouble, true, expectedString, true, expectedByteArray, true, expectedInt, true, expectedBool, false,
                expectedBitArray, false, expectedChar, false, expectedDate, false, expectedTime, false, expectedDateTime, false, expectedPoint, false, expectedPointF, false, expectedRect, false,
                expectedRectF, false, expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantStringInteger() {
        Object variant = "321";
        String expectedString = "321";
        double expectedDouble = 321.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 321;
        boolean expectedBool = true;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime(expectedDate, expectedTime);
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("String integer", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true, expectedBitArray,
                false, expectedChar, false, expectedDate, true, expectedTime, true, expectedDateTime, true, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantStringBooleanFalse() {
        Object variant = "FaLsE";
        String expectedString = "FaLsE";
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = false;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime(0, 0);
        QDateTime expectedDateTime = new QDateTime();
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("String boolean", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true, expectedBitArray,
                false, expectedChar, false, expectedDate, true, expectedTime, true, expectedDateTime, true, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantBooleanTrue() {
        Object variant = "tRUe";;
        String expectedString = "tRUe";;
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = true;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime(0, 0);
        QDateTime expectedDateTime = new QDateTime();
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("String boolean (true)", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true,
                expectedBitArray, false, expectedChar, false, expectedDate, true, expectedTime, true, expectedDateTime, true, expectedPoint, false, expectedPointF, false, expectedRect, false,
                expectedRectF, false, expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantDouble() {
        Object variant = 123.567;
        String expectedString = "123.567";
        double expectedDouble = 123.567;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 123;
        boolean expectedBool = true;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime(expectedDate, expectedTime);
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("Double", variant, expectedDouble, true, expectedString, true, expectedByteArray, false, expectedInt, true, expectedBool, true, expectedBitArray, false,
                expectedChar, false, expectedDate, false, expectedTime, false, expectedDateTime, false, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);
    }

    @Test
    public void run_QVariantNull() {
        Object variant = null;
        String expectedString = "";
        double expectedDouble = 0.0;
        QByteArray expectedByteArray = new QByteArray("");
        int expectedInt = 0;
        boolean expectedBool = false;
        QBitArray expectedBitArray = new QBitArray();
        char expectedChar = 0;
        QDate expectedDate = new QDate();
        QTime expectedTime = new QTime();
        QDateTime expectedDateTime = new QDateTime();
        QPoint expectedPoint = new QPoint();
        QPointF expectedPointF = new QPointF();
        QRect expectedRect = new QRect();
        QRectF expectedRectF = new QRectF();
        QRegExp expectedRegExp = new QRegExp();
        QSize expectedSize = new QSize();
        QSizeF expectedSizeF = new QSizeF();
        List<Object> expectedList = new ArrayList<Object>();
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        testQVariant("Null", variant, expectedDouble, false, expectedString, false, expectedByteArray, false, expectedInt, false, expectedBool, false, expectedBitArray, false,
                expectedChar, false, expectedDate, false, expectedTime, false, expectedDateTime, false, expectedPoint, false, expectedPointF, false, expectedRect, false, expectedRectF, false,
                expectedRegExp, false, expectedSize, false, expectedSizeF, false, expectedList, false, expectedMap, false);

    }

    public void testQVariant(String name, Object object, Double expectedDouble, Boolean canConvertDouble, String expectedString, Boolean canConvertString, QByteArray expectedByteArray,
            Boolean canConvertByteArray, Integer expectedInt, Boolean canConvertInt, Boolean expectedBool, Boolean canConvertBool, QBitArray expectedBitArray, Boolean canConvertBitArray,
            Character expectedChar, Boolean canConvertChar, QDate expectedDate, Boolean canConvertDate, QTime expectedTime, Boolean canConvertTime, QDateTime expectedDateTime,
            Boolean canConvertDateTime, QPoint expectedPoint, Boolean canConvertPoint, QPointF expectedPointF, Boolean canConvertPointF, QRect expectedRect, Boolean canConvertRect,
            QRectF expectedRectF, Boolean canConvertRectF, QRegExp expectedRegExp, Boolean canConvertRegExp, QSize expectedSize, Boolean canConvertSize, QSizeF expectedSizeF, Boolean canConvertSizeF,
            List expectedList, Boolean canConvertList, Map expectedMap, Boolean canConvertMap) {

        assertEquals(QVariant.toString(object), expectedString);
        assertEquals(QVariant.toDouble(object), expectedDouble);
        assertEquals(QVariant.toInt(object), expectedInt);
        assertEquals(QVariant.toBoolean(object), expectedBool);
        assertTrue(QVariant.toBitArray(object).equals(expectedBitArray));
        assertEquals(QVariant.toChar(object), expectedChar);
        assertTrue(QVariant.toDate(object).equals(expectedDate));
        assertTrue(QVariant.toTime(object).equals(expectedTime));
        assertTrue(QVariant.toDateTime(object).equals(expectedDateTime));
        QVariant.toDateTime(object);
        assertEquals(QVariant.toPoint(object).x(), expectedPoint.x());
        assertEquals(QVariant.toPoint(object).y(), expectedPoint.y());
        assertEquals(QVariant.toPointF(object).x(), expectedPointF.x());
        assertEquals(QVariant.toPointF(object).y(), expectedPointF.y());
        assertEquals(QVariant.toRect(object).left(), expectedRect.left());
        assertEquals(QVariant.toRect(object).top(), expectedRect.top());
        assertEquals(QVariant.toRect(object).right(), expectedRect.right());
        assertEquals(QVariant.toRect(object).bottom(), expectedRect.bottom());
        assertEquals(QVariant.toRectF(object).left(), expectedRectF.left());
        assertEquals(QVariant.toRectF(object).top(), expectedRectF.top());
        assertEquals(QVariant.toRectF(object).bottom(), expectedRectF.bottom());
        assertEquals(QVariant.toRectF(object).right(), expectedRectF.right());
        assertTrue(QVariant.toRegExp(object).equals(expectedRegExp));
        assertEquals(QVariant.toSize(object).width(), expectedSize.width());
        assertEquals(QVariant.toSize(object).height(), expectedSize.height());
        assertEquals(QVariant.toSizeF(object).width(), expectedSizeF.width());
        assertEquals(QVariant.toSizeF(object).height(), expectedSizeF.height());
        assertEquals(QVariant.toList(object), expectedList);
        assertEquals(QVariant.toMap(object), expectedMap);
        {
            QByteArray ba = QVariant.toByteArray(object);
            assertEquals(ba.size(), expectedByteArray.size());
            for (int i = 0; i < ba.size(); ++i) {
                assertEquals(ba.at(i), expectedByteArray.at(i));
            }
        }

        assertEquals(QVariant.canConvertToString(object), canConvertString);
        assertEquals(QVariant.canConvertToDouble(object), canConvertDouble);
        assertEquals(QVariant.canConvertToByteArray(object), canConvertByteArray);
        assertEquals(QVariant.canConvertToInt(object), canConvertInt);
        assertEquals(QVariant.canConvertToBoolean(object), canConvertBool);
        assertEquals(QVariant.canConvertToBitArray(object), canConvertBitArray);
        assertEquals(QVariant.canConvertToChar(object), canConvertChar);
        assertEquals(QVariant.canConvertToDate(object), canConvertDate);
        assertEquals(QVariant.canConvertToTime(object), canConvertTime);
        assertEquals(QVariant.canConvertToDateTime(object), canConvertDateTime);
        assertEquals(QVariant.canConvertToPoint(object), canConvertPoint);
        assertEquals(QVariant.canConvertToPointF(object), canConvertPointF);
        assertEquals(QVariant.canConvertToRect(object), canConvertRect);
        assertEquals(QVariant.canConvertToRectF(object), canConvertRectF);
        assertEquals(QVariant.canConvertToRegExp(object), canConvertRegExp);
        assertEquals(QVariant.canConvertToSize(object), canConvertSize);
        assertEquals(QVariant.canConvertToSizeF(object), canConvertSizeF);
        assertEquals(QVariant.canConvertToList(object), canConvertList);
        assertEquals(QVariant.canConvertToMap(object), canConvertMap);
    }

    @Test
    public void run_QByteArray_toString() {
        byte data[] = {'a', 'e', 'r', 'y', 'n'};
        
        // Normal construction
        assertEquals(new QByteArray(data).toString(), "aeryn");
        
        // String construction
        assertEquals(new QByteArray("aeryn").toString(), "aeryn");

        // Using int, char constructor
        assertEquals(new QByteArray(5, (byte) 'a').toString(), "aaaaa");
    }

    private class DataPrimitive {
        public Object value;
        public String methodName;

        public DataPrimitive(Object value, String methodName) {
            this.value = value;
            this.methodName = methodName;
        }
    }

    @Test
    public void run_primitives() {
        DataPrimitive[] data = { new DataPrimitive(new Long(3), "currentQInt64"),
                new DataPrimitive(new Long(4), "currentQUInt64"), new DataPrimitive(new Integer(5), "currentQInt32"),
                new DataPrimitive(new Integer(6), "currentQUInt32"),
                new DataPrimitive(new Character((char) 7), "currentQInt16"),
                new DataPrimitive(new Character((char) 8), "currentQUInt16"),
                new DataPrimitive(new Byte((byte) 9), "currentQInt8"),
                new DataPrimitive(new Byte((byte) 10), "currentQUInt8"),
                new DataPrimitive(new Float((float) 11), "currentFloat"),
                new DataPrimitive(new Double((double)12), "currentDouble") };

        for (int i = 0; i < data.length; i++) {
            Object value = data[i].value;
            String methodName = data[i].methodName;
            Variants v = new Variants();
            Object returned = v.pushThrough(value);
            assertTrue(v.isValid());
            Object nativlySet = null;

            try {
                Method method = Variants.class.getMethod(methodName);
                nativlySet = method.invoke(v);
            } catch (Exception e) {
                System.err.println("Failed to call: " + methodName);
                e.printStackTrace();
            }
            assertEquals(nativlySet, value);
            assertEquals(returned, value);
        }
    }

    @Test
    public void run_primitives2() {
        Variants v = new Variants();

        int value = 1;

        long the_long = ++value;
        v.pushThrough(the_long);
        assertEquals(v.currentQInt64(), the_long);

        long the_ulong = ++value;
        v.pushThrough(the_ulong);
        assertEquals(v.currentQUInt64(), the_ulong);

        int the_int = ++value;
        v.pushThrough(the_int);
        assertEquals(v.currentQInt32(), the_int);

        int the_uint = ++value;
        v.pushThrough(the_uint);
        assertEquals(v.currentQUInt32(), the_uint);

        char the_short = (char) ++value;
        v.pushThrough(the_short);
        
        assertEquals(v.currentQInt16(), the_short);

        char the_ushort = (char) ++value;
        v.pushThrough(the_ushort);
        assertEquals(v.currentQUInt16(), the_ushort);

        byte the_byte = (byte) ++value;
        v.pushThrough(the_byte);
        assertEquals(v.currentQInt8(), the_byte);

        byte the_ubyte = (byte) ++value;
        v.pushThrough(the_ubyte);
        assertEquals(v.currentQUInt8(), the_ubyte);
    }
}
