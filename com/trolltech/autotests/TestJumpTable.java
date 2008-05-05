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

import com.trolltech.qt.core.*;
import java.io.*;
import org.junit.*;

public class TestJumpTable {


    @Test
    public void testMemberFunctions() {
        QRect r = new QRect(1, 2, 3, 4);

        Assert.assertEquals(r.x(), 1);
        Assert.assertEquals(r.y(), 2);
        Assert.assertEquals(r.width(), 3);
        Assert.assertEquals(r.height(), 4);
    }


    @Test
    public void testStaticFunctions() {
        QCoreApplication.setApplicationName("TestApp");
        Assert.assertEquals(QCoreApplication.applicationName(), "TestApp");
    }


    @Test
    public void testReplaceValueFunctions() throws Exception {
        File f = File.createTempFile("jambi", "autotest");
        f.deleteOnExit();

        String path = f.getAbsolutePath();

        QFile file = new QFile(path);
        file.open(QIODevice.OpenModeFlag.WriteOnly);
        QTextStream stream = new QTextStream(file);
        stream.writeLong(1234);
        stream.flush();
        stream.dispose();
        file.dispose();

        String content = new BufferedReader(new InputStreamReader(new FileInputStream(path))).readLine();
        Assert.assertEquals(content, "1234");
    }

}
