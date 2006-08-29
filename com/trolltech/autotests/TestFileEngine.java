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

import com.trolltech.qt.QtJambiUtils;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qtest.QTestCase;

import java.util.*;

public class TestFileEngine extends QTestCase {
    
    public void run_classPathFileEngine()
    {
        QFileInfo info = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        QVERIFY(info.exists());
                
        String search_path = info.canonicalFilePath();
        QtJambiUtils.addSearchPathForResourceEngine(search_path);
        
        QFileInfo ne_info = new QFileInfo("classpath:*#TestClassFunctionality_nosuchfile.txt");
        QVERIFY(!ne_info.exists());
        
        QFileInfo pm_info = new QFileInfo("classpath:TestClassFunctionality_picture.jpg");
        QVERIFY(pm_info.exists());
        QCOMPARE(pm_info.size(), 11769L);

        QPixmap pm = new QPixmap("classpath:*#TestClassFunctionality_picture.jpg");
        QCOMPARE(pm.width(), 200);
        QCOMPARE(pm.height(), 242);
        
        QLabel label = new QLabel();
        label.setPixmap(pm);
        label.show();
        
        info = new QFileInfo("classpath:TestClassFunctionality_test.txt");
        QVERIFY(info.exists());
        QCOMPARE(info.size(), 8L);
        QVERIFY(info.absolutePath().endsWith("#") && info.absolutePath().startsWith("classpath:"));
        QVERIFY(info.absoluteFilePath().endsWith("#TestClassFunctionality_test.txt") && info.absoluteFilePath().startsWith("classpath:"));
        QCOMPARE(info.absoluteDir().absolutePath().endsWith("TestClassFunctionality.jar#."));
        
        QVERIFY(QFile.exists("classpath:TestClassFunctionality_test.txt"));
        
        QFile file = new QFile("classpath:TestClassFunctionality_test.txt");
        QVERIFY(file.exists());
        
        QVERIFY(file.open(QFile.ReadOnly));
        QVERIFY(file.isOpen());
        
        QByteArray ba = file.readAll();
        ba.append("");
        String s = ba.toString();
        QVERIFY(s.startsWith("Qt rocks"));        
        QCOMPARE(file.bytesAvailable(), 0L);
        
        file.reset();
        file.seek(3);
        
        QCOMPARE(file.bytesAvailable(), 5L);
        
        ba = file.read(1000);
        ba.append("");
        s = ba.toString();
        QVERIFY(s.startsWith("rocks"));
        
        file.reset();
        QCOMPARE(file.bytesAvailable(), 8L);
        file.seek(1);
        
        QCOMPARE(file.bytesAvailable(), 7L);
        ba = file.read(1);
        ba.append("");
        s = ba.toString();
        QCOMPARE(s, "t");
        
        QCOMPARE(file.bytesAvailable(), 6L);
        ba = file.read(1);
        ba.append("");
        s = ba.toString();
        QCOMPARE(s, " ");
        QCOMPARE(file.bytesAvailable(), 5L);
        
        file.close();
        QVERIFY(!file.isOpen());
        
        QVERIFY(file.open(QFile.ReadOnly | QFile.Text));
        QVERIFY(file.isOpen());
        file.reset();
        
        ba = file.readLine();
        ba.append("");
        s = ba.toString();
        QCOMPARE(s, "Qt rocks");
        
        file.close();
        QVERIFY(!file.isOpen());
        
        info = new QFileInfo("classpath:*#TestClassFunctionality_dir");
        QVERIFY(info.exists());
        QVERIFY(info.isDir());
        
        
        QDir dir = new QDir("classpath:TestClassFunctionality_dir/");
        QVERIFY(dir.exists());

        List<String> ss = dir.entryList();
        QCOMPARE(dir.entryList().size(), 1);
        QVERIFY(dir.entryList().get(0).equals("TestClassFunctionality_dir2"));
        
        List<QFileInfo> entryInfoList = dir.entryInfoList();
        QCOMPARE(entryInfoList.size(), 1);
        
        
        info = entryInfoList.get(0);
        QVERIFY(info.exists());
        QVERIFY(info.isDir());
        QCOMPARE(info.fileName(), "TestClassFunctionality_dir2");        
        String abs = info.absoluteFilePath();
        QVERIFY(abs.startsWith("classpath:") && abs.endsWith("TestClassFunctionality_dir/TestClassFunctionality_dir2"));
       
        info = new QFileInfo("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2");
        QVERIFY(info.exists());
        QVERIFY(info.isDir());
        
        dir = new QDir("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2");
        QVERIFY(dir.exists());
        QVERIFY(dir.isReadable());        
        QVERIFY(!dir.isRoot());
        QCOMPARE(dir.entryList().size(), 1);
        
        file = new QFile("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2/TestClassFunctionality_indir.txt");
        QVERIFY(file.exists());
        QVERIFY((file.permissions() & QAbstractFileEngine.ReadUserPerm) != 0);
        QCOMPARE(file.size(), 13L);
        
        QtJambiUtils.removeSearchPathForResourceEngine(search_path);
    }


    public static void main(String[] args) {
        QApplication app = new QApplication(args);
        runTest(new TestFileEngine());
    }
}
