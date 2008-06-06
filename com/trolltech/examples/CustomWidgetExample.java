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

package com.trolltech.examples;

import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Custom Widget")
public class CustomWidgetExample extends QWidget {
    private Ui_CustomWidget ui = new Ui_CustomWidget();

    public CustomWidgetExample(QWidget parent) {
        super(parent);

        ui.setupUi(this);
        QMessageBox.information(this, "Just a hint!",
                "This is an example of how you can make your own custom widgets\n"
              + "and import them in Qt Designer. Try running Qt Designer and look\n"
              + "for Custom Widget in the widget box.");
    }

    public static void main(String[] args) {
        QApplication.initialize(args);

        CustomWidgetExample widget = new CustomWidgetExample(null);
        widget.show();

        QApplication.exec();
    }

}
