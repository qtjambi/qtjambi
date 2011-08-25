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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;

public class TestFileEngine extends QApplicationTest{

    @Test
    public void run_classPathFileEngine() {
        QAbstractFileEngine.addSearchPathForResourceEngine(".");
        QFileInfo info = new QFileInfo("classpath:com/trolltech/autotests/TestClassFunctionality.jar");
        assertTrue(info.exists());

        QFile af = new QFile(info.absoluteFilePath());
        assertTrue(af.exists());
        assertTrue(af.open(QIODevice.OpenModeFlag.ReadOnly));
        af.close();

        String search_path = info.canonicalFilePath();
        QAbstractFileEngine.addSearchPathForResourceEngine(search_path);

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

        QAbstractFileEngine.removeSearchPathForResourceEngine(search_path);
    }
}
