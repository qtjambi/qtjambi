/****************************************************************************
**
** Copyright (C) 2006-$THISYEAR$ $TROLLTECH$. All rights reserved.
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

//! [0] //! [1]
public class BlocksGalore extends QWidget
{
    public BlocksGalore()
    {
        QPushButton quit = new QPushButton(tr("Quit"));
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        quit.clicked.connect(QApplication.instance(), "quit()");
//! [0]

//! [2]
        QGridLayout grid = new QGridLayout();
        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(quit);
        layout.addLayout(grid);
        setLayout(layout);
        setWindowTitle(tr("Building Blocks Galore"));

//! [2] //! [3]
        for (int row = 0; row < 3; ++row) {
//! [3] //! [4]
            for (int column = 0; column < 3; ++column) {
                LCDRange lcdRange = new LCDRange();
                grid.addWidget(lcdRange, row, column);
            }
        }
    }
//! [4]

//! [5]
    class LCDRange extends QWidget
    {
//! [5] //! [6]
        public LCDRange()
        {
            QLCDNumber lcd = new QLCDNumber(2);
            lcd.setSegmentStyle(QLCDNumber.SegmentStyle.Filled);

            QSlider slider = new QSlider(Qt.Orientation.Horizontal);
            slider.setRange(0, 99);
            slider.setValue(0);

            slider.valueChanged.connect(lcd, "display(int)");

            QVBoxLayout layout = new QVBoxLayout();
            layout.addWidget(lcd);
            layout.addWidget(slider);
            setLayout(layout);
        }
//! [6]
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        BlocksGalore widget = new BlocksGalore();
        widget.show();

        QApplication.exec();
    }
}
//! [1]
