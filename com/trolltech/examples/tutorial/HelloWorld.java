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

//! [0]
package com.trolltech.examples.tutorial;

import com.trolltech.qt.gui.*;
//! [0]

//! [1]
public class HelloWorld
{
//! [2]
    public static void main(String args[])
    {
//! [2] //! [3]
        QApplication.initialize(args);
//! [3]

//! [4]
        QPushButton hello = new QPushButton("Hello World!");
//! [4] //! [5]
        hello.resize(120, 40);
//! [5] //! [6]
        hello.setWindowTitle("Hello World");
//! [6] //! [7]
        hello.show();
//! [7]

//! [8]
        QApplication.exec();
//! [8] //! [9]
    }
//! [9]
}
//! [1]
