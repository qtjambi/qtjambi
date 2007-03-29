/****************************************************************************
**
** Copyright (C) 2005-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.examples.tutorial;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

public class HelloWorld
{
    public static void main(String args[])
    {
        QApplication.initialize(args);

        QPushButton hello = new QPushButton("Hello World!");
        hello.resize(120, 40);
        hello.setWindowTitle("Hello World");
        hello.show();

        QApplication.exec();
    }
}
