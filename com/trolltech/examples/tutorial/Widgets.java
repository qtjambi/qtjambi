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

public class Widgets extends QWidget
{
    public Widgets()
    {
        setFixedSize(200, 120);

        QPushButton quit = new QPushButton(tr("Quit"), this);
        quit.setGeometry(62, 40, 75, 30);
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        quit.clicked.connect(QApplication.instance(), "quit()");

        setWindowTitle(tr("Let There Be Widgets"));
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        Widgets widget = new Widgets();
        widget.show();

        QApplication.exec();
    }
}
