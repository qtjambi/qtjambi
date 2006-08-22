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

import java.lang.reflect.Method;
import java.util.*;

import com.trolltech.autotests.generated.Variants;
import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;
import com.trolltech.qtest.QTestCase;


public class TestQVariant extends QTestCase {

    public void data_QVariant() 
    {
        defineDataStructure(Object(), "object", 
                            Double(), "expectedDouble",
                            Boolean(), "canConvertDouble",
                            String(), "expectedString",
                            Boolean(), "canConvertString",
                            QByteArray.class, "expectedByteArray",
                            Boolean(), "canConvertByteArray",
                            Integer(), "expectedInt",
                            Boolean(), "canConvertInt",
                            Boolean(), "expectedBool",
                            Boolean(), "canConvertBool",
                            QBitArray.class, "expectedBitArray",
                            Boolean(), "canConvertBitArray",
                            Character(), "expectedChar",
                            Boolean(), "canConvertChar",
                            QDate.class, "expectedDate",
                            Boolean(), "canConvertDate",
                            QTime.class, "expectedTime",
                            Boolean(), "canConvertTime",
                            QDateTime.class, "expectedDateTime",
                            Boolean(), "canConvertDateTime",
                            QPoint.class, "expectedPoint",
                            Boolean(), "canConvertPoint",
                            QPointF.class, "expectedPointF",
                            Boolean(), "canConvertPointF",
                            QRect.class, "expectedRect",
                            Boolean(), "canConvertRect",
                            QRectF.class, "expectedRectF",
                            Boolean(), "canConvertRectF",
                            QRegExp.class, "expectedRegExp",
                            Boolean(), "canConvertRegExp",
                            QSize.class, "expectedSize",
                            Boolean(), "canConvertSize",
                            QSizeF.class, "expectedSizeF",
                            Boolean(), "canConvertSizeF",
                            List.class, "expectedList",
                            Boolean(), "canConvertList", 
                            Map.class, "expectedMap",
                            Boolean(), "canConvertMap");
                
        
        
        // java.lang.String
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
        addDataSet("Normal string", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, true,
                expectedTime, true,
                expectedDateTime, true,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
        // QByteArray
        variant = new QByteArray("this is my string");
        expectedBool = false;
        expectedTime = new QTime();
        expectedDateTime = new QDateTime();
        expectedByteArray = new QByteArray("this is my string");
        addDataSet("QByteArray", variant, 
                expectedDouble, false, 
                expectedString, true, 
                expectedByteArray, true,
                expectedInt, false,
                expectedBool, false,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, false,
                expectedTime, false,
                expectedDateTime, false,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
        // java.lang.Object
        variant = new Object();
        expectedString = variant.toString();
        expectedDate = new QDate();
        expectedTime = new QTime();
        expectedDateTime = new QDateTime(expectedDate, expectedTime);
        expectedByteArray = new QByteArray("");
        expectedBool = false;
        addDataSet("java.lang.Object", variant, 
                expectedDouble, false, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, false,
                expectedBool, false,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, false,
                expectedTime, false,
                expectedDateTime, false,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false); 
        
        // String double
        variant = "123.456";
        expectedString = "123.456";
        expectedByteArray = new QByteArray("");
        expectedDouble = 123.456;
        expectedTime = new QTime(12, 0, 6);
        expectedDate = new QDate();
        expectedDateTime = new QDateTime();        
        expectedBool = true;
        addDataSet("String double", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, true,
                expectedTime, true,
                expectedDateTime, true,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);        
        
        // Byte array double
        variant = new QByteArray("456.789");
        expectedString = "456.789";
        expectedByteArray = new QByteArray("456.789");
        expectedTime = new QTime();
        expectedDate = new QDate();
        expectedDateTime = new QDateTime(expectedDate, expectedTime);
        expectedDouble = 0;
        expectedBool = false;
        addDataSet("Byte array double", variant, 
                expectedDouble, false, 
                expectedString, true, 
                expectedByteArray, true,
                expectedInt, false,
                expectedBool, false,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, false,
                expectedTime, false,
                expectedDateTime, false,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
        // String integer
        variant = "321";
        expectedString = "321";
        expectedByteArray = new QByteArray("");
        expectedInt = 321;
        expectedDouble = 321.0;
        expectedBool = true;
        expectedChar = 0;
        addDataSet("String integer", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, true,
                expectedTime, true,
                expectedDateTime, true,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
        // String boolean
        variant = "FaLsE";
        expectedString = "FaLsE";
        expectedByteArray = new QByteArray("");
        expectedDate = new QDate();
        expectedTime = new QTime(0, 0);
        expectedDateTime = new QDateTime();
        expectedBool = false;
        expectedInt = 0;
        expectedDouble = 0.0;
        expectedChar = 0;
        addDataSet("String boolean", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, true,
                expectedTime, true,
                expectedDateTime, true,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
        // String boolean (true)
        variant = "tRUe";
        expectedString = "tRUe";
        expectedByteArray = new QByteArray("");
        expectedDate = new QDate();
        expectedTime = new QTime(0, 0);
        expectedDateTime = new QDateTime();        
        expectedBool = true;
        addDataSet("String boolean (true)", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, true,
                expectedTime, true,
                expectedDateTime, true,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);
        
       
        // Double variant
        variant = 123.567;
        expectedString = "123.567";
        expectedByteArray = new QByteArray("");
        expectedDouble = 123.567;
        expectedDate = new QDate();
        expectedTime = new QTime();
        expectedDateTime = new QDateTime(expectedDate, expectedTime);        
        expectedInt = 123;
        expectedBool = true;
        addDataSet("Double", variant, 
                expectedDouble, true, 
                expectedString, true, 
                expectedByteArray, false,
                expectedInt, true,
                expectedBool, true,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, false,
                expectedTime, false,
                expectedDateTime, false,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);        
        
        variant = null;
        expectedString = "";
        expectedByteArray = new QByteArray("");
        expectedDouble = 0.0;
        expectedInt = 0;
        expectedBool = false;
        expectedTime = new QTime();
        expectedDate = new QDate();
        expectedDateTime = new QDateTime();
        addDataSet("Null", variant, 
                expectedDouble, false, 
                expectedString, false, 
                expectedByteArray, false,
                expectedInt, false,
                expectedBool, false,
                expectedBitArray, false,
                expectedChar, false,
                expectedDate, false,
                expectedTime, false,
                expectedDateTime, false,
                expectedPoint, false,
                expectedPointF, false,
                expectedRect, false,
                expectedRectF, false,
                expectedRegExp, false,
                expectedSize, false,
                expectedSizeF, false,
                expectedList, false,
                expectedMap, false);        
       
        
    }    

    public void run_QVariant()
    {
        Object object = getParameter("object");
        String expectedString = getParameter("expectedString");
        Double expectedDouble = getParameter("expectedDouble");
        QByteArray expectedByteArray = getParameter("expectedByteArray");
        Integer expectedInt = getParameter("expectedInt");
        Boolean expectedBool = getParameter("expectedBool");
        QBitArray expectedBitArray = getParameter("expectedBitArray");
        Character expectedChar = getParameter("expectedChar");
        QDate expectedDate = getParameter("expectedDate");
        QTime expectedTime = getParameter("expectedTime");
        QDateTime expectedDateTime = getParameter("expectedDateTime");
        QPoint expectedPoint = getParameter("expectedPoint");
        QPointF expectedPointF = getParameter("expectedPointF");
        QRect expectedRect = getParameter("expectedRect");
        QRectF expectedRectF = getParameter("expectedRectF");
        QRegExp expectedRegExp = getParameter("expectedRegExp");
        QSize expectedSize = getParameter("expectedSize");
        QSizeF expectedSizeF = getParameter("expectedSizeF");
        List<Object> expectedList = getParameter("expectedList");
        Map<String, Object> expectedMap = getParameter("expectedMap");
        
        Boolean canConvertString = getParameter("canConvertString");
        Boolean canConvertDouble = getParameter("canConvertDouble");
        Boolean canConvertByteArray = getParameter("canConvertByteArray");
        Boolean canConvertInt = getParameter("canConvertInt");
        Boolean canConvertBool = getParameter("canConvertBool");
        Boolean canConvertBitArray = getParameter("canConvertBitArray");
        Boolean canConvertChar = getParameter("canConvertChar");
        Boolean canConvertDate = getParameter("canConvertDate");
        Boolean canConvertTime = getParameter("canConvertTime");
        Boolean canConvertDateTime = getParameter("canConvertDateTime");
        Boolean canConvertPoint = getParameter("canConvertPoint");
        Boolean canConvertPointF = getParameter("canConvertPointF");
        Boolean canConvertRect = getParameter("canConvertRect");
        Boolean canConvertRectF = getParameter("canConvertRectF");
        Boolean canConvertRegExp = getParameter("canConvertRegExp");
        Boolean canConvertSize = getParameter("canConvertSize");
        Boolean canConvertSizeF = getParameter("canConvertSizeF");
        Boolean canConvertList = getParameter("canConvertList");
        Boolean canConvertMap = getParameter("canConvertMap");
        
        QCOMPARE(QVariant.toString(object), expectedString);
        QCOMPARE(QVariant.toDouble(object), expectedDouble);
        QCOMPARE(QVariant.toInt(object), expectedInt);
        QCOMPARE(QVariant.toBoolean(object), expectedBool);
        QVERIFY(QVariant.toBitArray(object).operator_equal(expectedBitArray));
        QCOMPARE(QVariant.toChar(object), expectedChar);
        QVERIFY(QVariant.toDate(object).operator_equal(expectedDate));
        QTime t = QVariant.toTime(object);
        QVERIFY(QVariant.toTime(object).operator_equal(expectedTime));        
        QVERIFY(QVariant.toDateTime(object).operator_equal(expectedDateTime));
        QVariant.toDateTime(object);
        QCOMPARE(QVariant.toPoint(object).x(), expectedPoint.x());
        QCOMPARE(QVariant.toPoint(object).y(), expectedPoint.y());
        QCOMPARE(QVariant.toPointF(object).x(), expectedPointF.x());
        QCOMPARE(QVariant.toPointF(object).y(), expectedPointF.y());
        QCOMPARE(QVariant.toRect(object).left(), expectedRect.left());
        QCOMPARE(QVariant.toRect(object).top(), expectedRect.top());
        QCOMPARE(QVariant.toRect(object).right(), expectedRect.right());
        QCOMPARE(QVariant.toRect(object).bottom(), expectedRect.bottom());        
        QCOMPARE(QVariant.toRectF(object).left(), expectedRectF.left());
        QCOMPARE(QVariant.toRectF(object).top(), expectedRectF.top());
        QCOMPARE(QVariant.toRectF(object).bottom(), expectedRectF.bottom());
        QCOMPARE(QVariant.toRectF(object).right(), expectedRectF.right());
        QVERIFY(QVariant.toRegExp(object).operator_equal(expectedRegExp));
        QCOMPARE(QVariant.toSize(object).width(), expectedSize.width());
        QCOMPARE(QVariant.toSize(object).height(), expectedSize.height());
        QCOMPARE(QVariant.toSizeF(object).width(), expectedSizeF.width());
        QCOMPARE(QVariant.toSizeF(object).height(), expectedSizeF.height());
        QCOMPARE(QVariant.toList(object), expectedList);
        QCOMPARE(QVariant.toMap(object), expectedMap);
        {
             QByteArray ba = QVariant.toByteArray(object);
             QCOMPARE(ba.size(), expectedByteArray.size());
             for (int i=0; i<ba.size(); ++i) {
                 QCOMPARE(ba.at(i), expectedByteArray.at(i));
             }
        }
        QCOMPARE(QVariant.canConvertToString(object), canConvertString);
        QCOMPARE(QVariant.canConvertToDouble(object), canConvertDouble);
        QCOMPARE(QVariant.canConvertToByteArray(object), canConvertByteArray);
        QCOMPARE(QVariant.canConvertToInt(object), canConvertInt);
        QCOMPARE(QVariant.canConvertToBoolean(object), canConvertBool);
        QCOMPARE(QVariant.canConvertToBitArray(object), canConvertBitArray);
        QCOMPARE(QVariant.canConvertToChar(object), canConvertChar);
        QCOMPARE(QVariant.canConvertToDate(object), canConvertDate);
        QCOMPARE(QVariant.canConvertToTime(object), canConvertTime);
        QCOMPARE(QVariant.canConvertToDateTime(object), canConvertDateTime);
        QCOMPARE(QVariant.canConvertToPoint(object), canConvertPoint);
        QCOMPARE(QVariant.canConvertToPointF(object), canConvertPointF);
        QCOMPARE(QVariant.canConvertToRect(object), canConvertRect);
        QCOMPARE(QVariant.canConvertToRectF(object), canConvertRectF);
        QCOMPARE(QVariant.canConvertToRegExp(object), canConvertRegExp);
        QCOMPARE(QVariant.canConvertToSize(object), canConvertSize);
        QCOMPARE(QVariant.canConvertToSizeF(object), canConvertSizeF);
        QCOMPARE(QVariant.canConvertToList(object), canConvertList);
        QCOMPARE(QVariant.canConvertToMap(object), canConvertMap);        
    }
    
    
    public void run_QByteArray_toString() {
        QNativePointer np = new QNativePointer(QNativePointer.Type.Byte, 6);
        np.setByteAt(0, (byte) 'a');
        np.setByteAt(1, (byte) 'e');
        np.setByteAt(2, (byte) 'r');
        np.setByteAt(3, (byte) 'y');
        np.setByteAt(4, (byte) 'n');
        np.setByteAt(5, (byte) 0);
        
        // Normal construction
        QCOMPARE(new QByteArray(np).toString(), "aeryn");
        
        // Using char *, int constructor
        np.setByteAt(5, (byte) 'c');
        QCOMPARE(new QByteArray(np, 5).toString(), "aeryn");
        
        // Using int, char constructor
        QCOMPARE(new QByteArray(5, (byte) 'a').toString(), "aaaaa");
    }
    
    
    public void data_primitives() {
        defineDataStructure(Object.class, "value", String.class, "methodName");
              
        addDataSet("qint64",    new Long(3),          "currentQInt64"); 
        addDataSet("quint64",   new Long(4),          "currentQUInt64"); 
        addDataSet("qint32",    new Integer(5),       "currentQInt32"); 
        addDataSet("quint32",   new Integer(6),       "currentQUInt32"); 
        addDataSet("qint16",    new Character((char) 7), "currentQInt16"); 
        addDataSet("quint16",   new Character((char) 8), "currentQUInt16"); 
        addDataSet("qint8",     new Byte((byte) 9),     "currentQInt8"); 
        addDataSet("quint8",    new Byte((byte) 10),    "currentQUInt8"); 
        addDataSet("float",     new Float((float) 11),  "currentFloat"); 
        addDataSet("double",    new Double(12),         "currentDouble"); 
    }    
    
    public void run_primitives() {       
        Variants v = new Variants();
        
        Object value = getParameter("value");
        String methodName = getParameter("methodName");
        
        Object returned = v.pushThrough(value);
        
        QVERIFY(v.isValid());
        
        Object nativlySet = null;
        
        try {
            Method method = Variants.class.getMethod(methodName);
            nativlySet = method.invoke(v);
        } catch (Exception e) {
            System.err.println("Failed to call: " + methodName);
            e.printStackTrace();
        }
        
        QCOMPARE(nativlySet, value);
        QCOMPARE(returned, value);
    }
    
    public void run_primitives2() {
        Variants v = new Variants();

        int value = 1;
                
        long the_long = ++value;
        v.pushThrough(the_long);
        QCOMPARE(v.currentQInt64(), the_long);
        
        long the_ulong = ++value;
        v.pushThrough(the_ulong);
        QCOMPARE(v.currentQUInt64(), the_ulong);
        
        int the_int = ++value;
        v.pushThrough(the_int);
        QCOMPARE(v.currentQInt32(), the_int);
        
        int the_uint = ++value;
        v.pushThrough(the_uint);
        QCOMPARE(v.currentQUInt32(), the_uint);

        char the_short = (char) ++value;
        v.pushThrough(the_short);
        QCOMPARE(v.currentQInt16(), the_short);
        
        char the_ushort = (char) ++value;
        v.pushThrough(the_ushort);
        QCOMPARE(v.currentQUInt16(), the_ushort);

        byte the_byte = (byte) ++value;
        v.pushThrough(the_byte);
        QCOMPARE(v.currentQInt8(), the_byte);
        
        byte the_ubyte = (byte) ++value;
        v.pushThrough(the_ubyte);
        QCOMPARE(v.currentQUInt8(), the_ubyte);
    }
    
    public static void main(String[] args) {
        QApplication app = new QApplication(args);
        runTest(new TestQVariant());
    }

}
