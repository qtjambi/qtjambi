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

public class ConnectedSliders extends QWidget
{
    public ConnectedSliders()
    {
        QPushButton quit = new QPushButton(tr("Quit"));
//! [0]
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));
//! [0]

        quit.clicked.connect(QApplication.instance(), "quit()");

        QGridLayout grid = new QGridLayout();
//! [1]
        LCDRange previousRange = null;
//! [1] //! [2]

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                LCDRange lcdRange = new LCDRange();
                grid.addWidget(lcdRange, row, column);

            if (previousRange != null)
                lcdRange.valueChanged.
                connect(previousRange, "setValue(int)");
//! [2]

//! [3]
                previousRange = lcdRange;
//! [3] //! [4]
            }
//! [4] //! [5]
        }
//! [5]
        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(quit);
        layout.addLayout(grid);
        setLayout(layout);
        setWindowTitle(tr("One Thing Leads to Another"));
    }

    class LCDRange extends QWidget
    {
        private QSlider slider;
        private int value;

//! [6]
        public final Signal1<Integer> valueChanged = new Signal1<Integer>();
//! [6]

//! [7]
        public LCDRange()
//! [7] //! [8]
        {
//! [8]
            QLCDNumber lcd = new QLCDNumber(2);
            lcd.setSegmentStyle(QLCDNumber.SegmentStyle.Filled);

            slider = new QSlider(Qt.Orientation.Horizontal);
            slider.setRange(0, 99);
            slider.setValue(0);

//! [9]
            slider.valueChanged.connect(lcd, "display(int)");
//! [9] //! [10]
            slider.valueChanged.connect(valueChanged);
//! [10]

            QVBoxLayout layout = new QVBoxLayout();
            layout.addWidget(lcd);
            layout.addWidget(slider);
            setLayout(layout);
//! [11]
        }
//! [11]

//! [12]
        public int value()
        {
            return value;
        }
//! [12]

//! [13]
        public void setValue(int value)
        {
            slider.setValue(value);
        }
//! [13]
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        ConnectedSliders widget = new ConnectedSliders();
        widget.show();

        QApplication.exec();
    }
}
