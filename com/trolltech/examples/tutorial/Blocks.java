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
import com.trolltech.qt.core.*;

//! [0]
public class Blocks extends QWidget
{
//! [1]
    public Blocks()
    {
        QPushButton quit = new QPushButton(tr("Quit"));
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        QLCDNumber lcd = new QLCDNumber(2);
        lcd.setSegmentStyle(QLCDNumber.SegmentStyle.Filled);
//! [1]

//! [2]
        QSlider slider = new QSlider(Qt.Orientation.Horizontal);
//! [2] //! [3]
        slider.setRange(0, 99);
//! [3] //! [4]
        slider.setValue(0);
//! [4]

        quit.clicked.connect(QApplication.instance(), "quit()");
//! [5]
        slider.valueChanged.connect(lcd, "display(int)");
//! [5]

//! [6]
        QVBoxLayout layout = new QVBoxLayout();
//! [6] //! [7]
        layout.addWidget(quit);
        layout.addWidget(lcd);
        layout.addWidget(slider);
        setLayout(layout);
//! [7]
        setWindowTitle(tr("Building Blocks"));
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        Blocks widget = new Blocks();
        widget.show();

        QApplication.exec();
    }
}
//! [0]
