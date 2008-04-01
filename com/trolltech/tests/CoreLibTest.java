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

package com.trolltech.tests;

import com.trolltech.qt.core.*;

public class CoreLibTest {

    public static void main(String args[]) {
        QCoreApplication.initialize(args);

        QFile file = new QFile("file_list");
        if (file.exists()) {
            System.out.println("file_list exists...");
        } else {
            System.out.println("file_list doesn't exist...");
        }

        QCoreApplication.exec();
	 }
}
