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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qtest.QTestCase;

public class RunTests {
    public static void main(String[] args) throws Exception {
        QApplication app = new QApplication(args);
        
        QDir dir = new QDir("com/trolltech/autotests");
        if (!dir.exists())
            throw new FileNotFoundException("com/trolltech/autotests");
        
        List<String> filters = new ArrayList<String>();
        filters.add("*.class");
        List<QFileInfo> infos = dir.entryInfoList(filters);
        
        for (QFileInfo info : infos) {
            String className = "com.trolltech.autotests." + info.baseName();
            Class cl = Class.forName(className);
            if (QTestCase.class.isAssignableFrom(cl)) {
                
                try {
                    QTestCase testCase = (QTestCase) cl.newInstance();
                    System.out.println("Running test: " + info.baseName());
                    QTestCase.runTest(testCase);
                    System.out.println("Test done...\n");
                    System.out.flush();                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
