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

import com.trolltech.qt.gui.*;

public class OnConnection extends QWidget
{

    public OnConnection() {
    QVBoxLayout layout = new QVBoxLayout(this);

    QPushButton ok = new QPushButton("OK", this);
    ok.setObjectName("okButton");
    ok.setCheckable(true);
    layout.addWidget(ok);

    QPushButton cancel = new QPushButton("cancel", this);
    cancel.setObjectName("cancelButton");
    layout.addWidget(cancel);

    connectSlotsByName();
    }

    public void on_okButton_clicked() {
    System.out.println("ok button clicked");
    }

    public void on_cancelButton_clicked() {
    System.out.println("cancel button clicked");
    QApplication.quit();
    }

    public void on_okButton_toggled(boolean ok)
    {
    System.out.println("ok button toggled: " + ok);
    }

    public static void main(String args[])
    {
    QApplication.initialize(args);

    OnConnection oc = new OnConnection();
    oc.show();

    QApplication.exec();
    }
}
