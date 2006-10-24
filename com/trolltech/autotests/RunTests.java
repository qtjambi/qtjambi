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

import java.io.FileNotFoundException;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class RunTests {
    public static void main(String[] args) throws Exception {
        QDir dir = new QDir("com/trolltech/autotests");
        if (!dir.exists())
            throw new FileNotFoundException("com/trolltech/autotests");

        List<String> filters = new ArrayList<String>();
        filters.add("*.class");
        List<QFileInfo> infos = dir.entryInfoList(filters);

        for (QFileInfo info : infos) {
            String className = "com.trolltech.autotests." + info.baseName();
            Class cl = Class.forName(className);

            Method methods[] = cl.getMethods();
            boolean hasTestFunctions = false;
            for (Method m : methods) {
                if (m.isAnnotationPresent(org.junit.Test.class)) {
                    hasTestFunctions = true;
                    break;
                }
            }

            if (hasTestFunctions) {
                List<String> cmds = new ArrayList<String>();
                cmds.add("org.junit.runner.JUnitCore");
                cmds.add(cl.getName());
                System.out.println();
                for (int i=0; i<72; ++i) {
                    System.out.print("*");
                }
                System.out.println("\nRunning test: " + cl.getName());
                QProcess.execute("java", cmds);
            }
        }
    }
}
