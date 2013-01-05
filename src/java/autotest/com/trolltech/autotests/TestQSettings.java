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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.junit.After;
import org.junit.Test;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.core.QSettings.Format;

public class TestQSettings extends QApplicationTest {

    private File tmpFile;
    private QSettings settings;
    private long initialLength;

    private QSettings createTmpFile() throws Exception {
        if(tmpFile != null)
            deleteTmpFile();
        tmpFile = File.createTempFile("TestQSettings", ".junit");
        assertEquals(0, tmpFile.length());
        // was organization="Trolltech", application="Test"
        settings = new QSettings(tmpFile.getAbsolutePath(), Format.IniFormat);
        return settings;
    }

    private QSettings findOrCreateTmpFile() throws Exception {
        if(settings != null)
            return settings;
        return createTmpFile();
    }

    private void deleteTmpFile() {
        settings = null;  // no delete operation ?

        if(tmpFile != null) {
            if(tmpFile.delete() == false)
                throw new RuntimeException(tmpFile.getAbsolutePath() + " delete failed");
            tmpFile = null;
        }
    }

    private void sync(boolean initialFlag) {
        if(settings != null)
            settings.sync();
        if(initialFlag)
            initialLength = tmpFile.length();
    }

    @After
    public void tearDown() throws Exception {
        deleteTmpFile();
    }

    public void writeSettingsSimple() throws Exception {
        QSettings settings = createTmpFile();
        sync(true);

        settings.setValue("int", 5);
        settings.setValue("double", 5.000001d);
        settings.setValue("String", "String");

        sync(false);
        assertTrue(tmpFile.length() > initialLength);
    }

    // This method depends on the write method completing work first
    public void readSettingsSimple() throws Exception {
        QSettings settings = findOrCreateTmpFile();
        sync(true);

        assertEquals(5, QVariant.toInt(settings.value("int")));
        assertEquals(5.000001d, QVariant.toDouble(settings.value("double").toString()), 0.0);
        assertEquals("String", QVariant.toString(settings.value("String")));
    }

    @Test
    public void testSettingsSimple() throws Exception {
        writeSettingsSimple();
        readSettingsSimple();
    }

    public void writeSettingsCollection() throws Exception {
        QSettings settings = createTmpFile();
        sync(true);

        List<String> list = new Vector<String>();
        for (int i = 0; i < 10; i++) {
            list.add("entry-" + i);
        }
        settings.setValue("test", list);
        sync(false);
        assertTrue(tmpFile.length() > initialLength);
    }

    // This method depends on the write method completing work first
    public void readSettingsCollection() throws Exception {
        QSettings settings = findOrCreateTmpFile();
        sync(true);

        List<?> list = (List<?>) settings.value("test", new Vector<String>());

        assertTrue(list.size() >= 10);
        for (int i = 0; i < 10; i++) {
            assertEquals("entry-" + i, list.get(i));
        }
    }

    @Test
    public void testSettingsCollection() throws Exception {
        writeSettingsCollection();
        readSettingsCollection();
    }

    @Test
    public void readSettingsEmpty() throws Exception {
        QSettings settings = createTmpFile();
        sync(true);

        String res = (String) settings.value("empty", "ok");
        assertEquals("ok", res);

        res = (String) settings.value("empty");
        assertNull(res);
    }

    public static class Custom implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        int integer;
        Custom object;
    }

    public void writeSettingsCustomClass() throws Exception {
        QSettings settings = createTmpFile();
        sync(true);

        Custom custom = new Custom();
        custom.name = "abc";
        custom.integer = 123;
        custom.object = new Custom();

        settings.setValue("custom", custom);
        sync(false);
        assertTrue(tmpFile.length() > initialLength);
    }

    // This method depends on the write method completing work first
    public void readSettingsCustomClass() throws Exception {
        QSettings settings = findOrCreateTmpFile();
        sync(true);

        Custom custom = (Custom) settings.value("custom");
        assertNotNull(custom);
        assertEquals(custom.name, "abc");
        assertEquals(custom.integer, 123);
        assertEquals(custom.object.getClass(), Custom.class);
    }

    @Test
    public void testSettingsCustomClass() throws Exception {
        writeSettingsCustomClass();
        readSettingsCustomClass();
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestQSettings.class.getName());
    }
}
