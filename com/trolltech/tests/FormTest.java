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

public class FormTest extends QDialog
{
    private ComplexForm ui = new ComplexForm();

    public FormTest() {
	ui.setupUi(this);
    }

    public static void main(String args[]) 
    {
	QApplication app = new QApplication(args);

	new FormTest().show();

	app.exec();
    }
}
