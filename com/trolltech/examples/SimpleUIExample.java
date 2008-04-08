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

@QtJambiExample(name = "Qt Designer and JUIC")
//! [0]
public class SimpleUIExample extends QMainWindow {

//! [1]
    public static void main(String[] args) {
        QApplication.initialize(args);
        SimpleUIExample mainw = new SimpleUIExample();
        mainw.show();
        QApplication.exec();
    }
//! [1]

    Ui_SimpleUIExampleMainWindow mainWindowUi = new Ui_SimpleUIExampleMainWindow();
//! [0]

//! [2]
    public SimpleUIExample() {
        // Place what you made in Designer onto the main window.
        mainWindowUi.setupUi(this);
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        // Connect the OpenDialog button to the showDialog method.
        mainWindowUi.pushButton_OpenDialog.clicked.connect(this, "showDialog()");
    }
//! [2]

//! [3]
    private void showDialog() {
        // Make the dialog.
        Ui_SimpleUIExample dialogUi = new Ui_SimpleUIExample();
        QDialog dialog = new QDialog(this);
        dialogUi.setupUi(dialog);
//! [3]

//! [4]
        String result = "";
        if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {
            result += "Name: " + dialogUi.lineEdit_Name.text() + "\n";
            result += "E-Mail: " + dialogUi.lineEdit_Email.text() + "\n";
            // Get rest of dialog information here

        } else {
            result = "Cancelled by user.";
        }

        mainWindowUi.textBrowser.setText(result);
//! [4] //! [5]
    }
//! [5] //! [6]
}
//! [6]
