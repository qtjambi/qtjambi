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

public class Quit
{
    public static void main(String args[])
    {
        QApplication.initialize(args);

        QPushButton quit = new QPushButton("Quit");
        quit.resize(80, 40);
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        quit.clicked.connect(QApplication.instance(), "quit()");

        quit.setWindowTitle("Calling It Quits");
        quit.show();
        QApplication.exec();
    }
}
