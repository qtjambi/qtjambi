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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample (name = "Extension")
//! [0]
public class ExtensionDialog extends QDialog
{
    private QLabel label;
    private QLineEdit lineEdit;
    private QCheckBox caseCheckBox;
    private QCheckBox fromStartCheckBox;
    private QCheckBox wholeWordsCheckBox;
    private QCheckBox searchSelectionCheckBox;
    private QCheckBox backwardCheckBox;
    private QDialogButtonBox buttonBox;
    private QPushButton findButton;
    private QPushButton moreButton;
    private QWidget extension;
//! [0]

//! [1]
    public ExtensionDialog()
    {
        label = new QLabel(tr("Find &what:"));
        lineEdit = new QLineEdit();
        label.setBuddy(lineEdit);

        caseCheckBox = new QCheckBox(tr("Match &case"));
        fromStartCheckBox = new QCheckBox(tr("Search from &start"));
        fromStartCheckBox.setChecked(true);

        findButton = new QPushButton(tr("&Find"));
        findButton.setDefault(true);

        moreButton = new QPushButton(tr("&More"));
        moreButton.setCheckable(true);
//! [1]
        moreButton.setAutoDefault(false);

        buttonBox = new QDialogButtonBox(Qt.Orientation.Vertical);
        buttonBox.addButton(findButton, QDialogButtonBox.ButtonRole.ActionRole);
        buttonBox.addButton(moreButton, QDialogButtonBox.ButtonRole.ActionRole);

//! [2]
        extension = new QWidget();

        wholeWordsCheckBox = new QCheckBox(tr("&Whole words"));
        backwardCheckBox = new QCheckBox(tr("Search &backward"));
        searchSelectionCheckBox = new QCheckBox(tr("Search se&lection"));
//! [2]

//! [3]
        moreButton.toggled.connect(extension, "setVisible(boolean)");

        QVBoxLayout extensionLayout = new QVBoxLayout();
        extensionLayout.setMargin(0);
        extensionLayout.addWidget(wholeWordsCheckBox);
        extensionLayout.addWidget(backwardCheckBox);
        extensionLayout.addWidget(searchSelectionCheckBox);
        extension.setLayout(extensionLayout);
//! [3]

//! [4]
        QHBoxLayout topLeftLayout = new QHBoxLayout();
        topLeftLayout.addWidget(label);
        topLeftLayout.addWidget(lineEdit);

        QVBoxLayout leftLayout = new QVBoxLayout();
        leftLayout.addLayout(topLeftLayout);
        leftLayout.addWidget(caseCheckBox);
        leftLayout.addWidget(fromStartCheckBox);
        leftLayout.addStretch(1);

        QGridLayout mainLayout = new QGridLayout();
        mainLayout.setSizeConstraint(QLayout.SizeConstraint.SetFixedSize);
        mainLayout.addLayout(leftLayout, 0, 0);
        mainLayout.addWidget(buttonBox, 0, 1);
        mainLayout.addWidget(extension, 1, 0, 1, 2);
        setLayout(mainLayout);

        setWindowTitle(tr("Extension"));
//! [4] //! [5]
        extension.hide();
    }
//! [5]

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new ExtensionDialog().show();

        QApplication.exec();
    }
}
