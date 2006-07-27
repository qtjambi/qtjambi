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

import com.trolltech.qtest.QTestCase;
import com.trolltech.qt.core.*;

import java.util.*;

public class TestQObject extends QTestCase {

	private static class TestObject extends QObject {
		public TestObject(QObject parent) {
			super(parent);
		}
	}
	
	private QObject root;
	private QFile file1, file2;
	private TestObject test1, test2, child11, child12, child21, child22;
	
	public TestQObject() {
		root = new QObject();
		root.setObjectName("root");
		
		file1 = new QFile(root);
		file1.setObjectName("file1");
		
		file2 = new QFile(root);
		file2.setObjectName("file2");
		
		test1 = new TestObject(root);
		test1.setObjectName("test1");
		
		test2 = new TestObject(root);
		test2.setObjectName("test2");
		
		child11 = new TestObject(test1);
		child11.setObjectName("child11");
		
		child12 = new TestObject(test1);
		child12.setObjectName("child");
		
		child21 = new TestObject(test2);
		child21.setObjectName("child21");
		
		child22 = new TestObject(test2); 
		child22.setObjectName("child");
	}
	
	public void run_findChildren() {
		{ 
			List<QObject> c = root.findChildren();
			QCOMPARE(c.size(), 8);
			
			QVERIFY(c.contains(file1));
			QVERIFY(c.contains(file2));
			QVERIFY(c.contains(test1));
			QVERIFY(c.contains(test2));
			QVERIFY(c.contains(child11));
			QVERIFY(c.contains(child12));
			QVERIFY(c.contains(child21));
			QVERIFY(c.contains(child22));			
		}
		
		{
			List<QObject> c = root.findChildren(QFile.class);
			QCOMPARE(c.size(), 2);
			
			QVERIFY(c.contains(file1));
			QVERIFY(c.contains(file2));
		}
		
		{
			List<QObject> c = root.findChildren(null, "child");
			
			QCOMPARE(c.size(), 2);
			QVERIFY(c.contains(child12));
			QVERIFY(c.contains(child22));
		}
		
		{
			List<QObject> c = root.findChildren(null, new QRegExp("child"));
			
			QCOMPARE(c.size(), 4);
			QVERIFY(c.contains(child11));
			QVERIFY(c.contains(child12));
			QVERIFY(c.contains(child21));
			QVERIFY(c.contains(child22));
		}		
	}
	
	public void run_findChild() {
		QCOMPARE(root.findChild(QFile.class, "file1"), file1);
		QCOMPARE(root.findChild(QFile.class));
		QVERIFY(root.findChild() != null);
	}
	
	public static void main(String[] args) {
        runTest(new TestQObject());

	}

}
