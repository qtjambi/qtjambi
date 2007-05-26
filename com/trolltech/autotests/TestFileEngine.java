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

import com.trolltech.qt.QtJambiInternal;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

import static org.junit.Assert.*;

import org.junit.*;

public class TestFileEngine extends QApplicationTest{

    @Test
    public void run_classPathFileEngine() {
        QFileInfo info = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        assertTrue(info.exists());

        String search_path = info.canonicalFilePath();
        QtJambiInternal.addSearchPathForResourceEngine(search_path);

        QFileInfo ne_info = new QFileInfo("classpath:*#TestClassFunctionality_nosuchfile.txt");
        assertTrue(!ne_info.exists());

        QFileInfo pm_info = new QFileInfo("classpath:TestClassFunctionality_picture.jpg");
        assertTrue(pm_info.exists());
        assertEquals(pm_info.size(), 11769L);

        QPixmap pm = new QPixmap("classpath:*#TestClassFunctionality_picture.jpg");
        assertEquals(pm.width(), 200);
        assertEquals(pm.height(), 242);

        QLabel label = new QLabel();
        label.setPixmap(pm);
        label.show();

        info = new QFileInfo("classpath:TestClassFunctionality_test.txt");
        assertTrue(info.exists());
        assertEquals(info.size(), 8L);
        assertTrue(info.absolutePath().endsWith("#") && info.absolutePath().startsWith("classpath:"));
        assertTrue(info.absoluteFilePath().endsWith("#TestClassFunctionality_test.txt") && info.absoluteFilePath().startsWith("classpath:"));
        assertNotNull(info.absoluteDir().absolutePath().endsWith("TestClassFunctionality.jar#."));

        assertTrue(QFile.exists("classpath:TestClassFunctionality_test.txt"));

        QFile file = new QFile("classpath:TestClassFunctionality_test.txt");
        assertTrue(file.exists());

        assertTrue(file.open(new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly)));
        assertTrue(file.isOpen());

        QByteArray ba = file.readAll();
        ba.append("");
        String s = ba.toString();
        assertTrue(s.startsWith("Qt rocks"));
        assertEquals(file.bytesAvailable(), 0L);

        file.reset();
        file.seek(3);

        assertEquals(file.bytesAvailable(), 5L);

        ba = file.read(1000);
        ba.append("");
        s = ba.toString();
        assertTrue(s.startsWith("rocks"));

        file.reset();
        assertEquals(file.bytesAvailable(), 8L);
        file.seek(1);

        assertEquals(file.bytesAvailable(), 7L);
        ba = file.read(1);
        ba.append("");
        s = ba.toString();
        assertEquals(s, "t");

        assertEquals(file.bytesAvailable(), 6L);
        ba = file.read(1);
        ba.append("");
        s = ba.toString();
        assertEquals(s, " ");
        assertEquals(file.bytesAvailable(), 5L);

        file.close();
        assertTrue(!file.isOpen());

        assertTrue(file.open(new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly, QIODevice.OpenModeFlag.Text)));
        assertTrue(file.isOpen());
        file.reset();

        ba = file.readLine();
        ba.append("");
        s = ba.toString();
        assertEquals(s, "Qt rocks");

        file.close();
        assertTrue(!file.isOpen());

        info = new QFileInfo("classpath:*#TestClassFunctionality_dir");
        assertTrue(info.exists());
        assertTrue(info.isDir());

        QDir dir = new QDir("classpath:TestClassFunctionality_dir/");
        assertTrue(dir.exists());

        assertEquals(dir.entryList().size(), 1);
        assertTrue(dir.entryList().get(0).equals("TestClassFunctionality_dir2"));

        List<QFileInfo> entryInfoList = dir.entryInfoList();
        assertEquals(entryInfoList.size(), 1);

        info = entryInfoList.get(0);
        assertTrue(info.exists());
        assertTrue(info.isDir());
        assertEquals(info.fileName(), "TestClassFunctionality_dir2");
        String abs = info.absoluteFilePath();
        assertTrue(abs.startsWith("classpath:") && abs.endsWith("TestClassFunctionality_dir/TestClassFunctionality_dir2"));

        info = new QFileInfo("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2");
        assertTrue(info.exists());
        assertTrue(info.isDir());

        dir = new QDir("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2");
        assertTrue(dir.exists());
        assertTrue(dir.isReadable());
        assertTrue(!dir.isRoot());
        assertEquals(dir.entryList().size(), 1);

        file = new QFile("classpath:TestClassFunctionality_dir/TestClassFunctionality_dir2/TestClassFunctionality_indir.txt");
        assertTrue(file.exists());
        assertTrue(file.permissions().isSet(QFile.Permission.ReadUser));
        assertEquals(file.size(), 13L);

        QtJambiInternal.removeSearchPathForResourceEngine(search_path);
    }
}
