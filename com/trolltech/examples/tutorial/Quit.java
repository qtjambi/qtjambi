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

//! [0]
public class Quit
{
    public static void main(String args[])
    {
        QApplication.initialize(args);

//! [1]
        QPushButton quit = new QPushButton("Quit");
//! [1] //! [2]
        quit.resize(80, 40);
//! [2] //! [3]
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));
//! [3]

//! [4]
        quit.clicked.connect(QApplication.instance(), "quit()");
//! [4]

        quit.setWindowTitle("Calling It Quits");
        quit.show();
        QApplication.exec();
    }
}
//! [0]
