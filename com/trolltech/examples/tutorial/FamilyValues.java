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

public class FamilyValues
{
    public static void main(String args[])
    {
        QApplication.initialize(args);

        QWidget window = new QWidget();
        window.resize(200, 120);

        QPushButton quit = new QPushButton("Quit", window);
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));
        quit.setGeometry(10, 40, 180, 40);

        quit.clicked.connect(QApplication.instance(), "quit()");

        window.setWindowTitle("FamilyValues");
        window.show();
        QApplication.exec();
    }
}
