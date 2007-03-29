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

public class Blocks extends QWidget
{
    public Blocks()
    {
        QPushButton quit = new QPushButton(tr("Quit"));
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        QLCDNumber lcd = new QLCDNumber(2);
        lcd.setSegmentStyle(QLCDNumber.SegmentStyle.Filled);

        QSlider slider = new QSlider(Qt.Orientation.Horizontal);
        slider.setRange(0, 99);
        slider.setValue(0);

        quit.clicked.connect(QApplication.instance(), "quit()");
        slider.valueChanged.connect(lcd, "display(int)");

        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(quit);
        layout.addWidget(lcd);
        layout.addWidget(slider);
        setLayout(layout);
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
