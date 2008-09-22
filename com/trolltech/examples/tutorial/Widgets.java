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

package com.trolltech.examples.tutorial;

import com.trolltech.qt.gui.*;

//! [0] //! [1]
public class Widgets extends QWidget
{
//! [0] //! [2]
    public Widgets()
    {
//! [2] //! [3]
        setFixedSize(200, 120);
//! [3] //! [4]

        QPushButton quit = new QPushButton(tr("Quit"), this);
        quit.setGeometry(62, 40, 75, 30);
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));
//! [4]

        quit.clicked.connect(QApplication.instance(), "quit()");

        setWindowTitle(tr("Let There Be Widgets"));
    }

//! [5]
    public static void main(String args[])
    {
        QApplication.initialize(args);

        Widgets widget = new Widgets();
        widget.show();

        QApplication.exec();
    }
//! [5]
}
//! [1]
