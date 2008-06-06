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

@QtJambiExample(name = "Digital Clock")
public class DigitalClock extends QLCDNumber
{
    public DigitalClock()
    {
        setSegmentStyle(SegmentStyle.Filled);

        QTimer timer = new QTimer(this);
        timer.timeout.connect(this, "showTime()");
        timer.start(1000);

        showTime();

        setWindowTitle(tr("Digital Clock"));
        resize(150, 60);
    }

    public void showTime()
    {
        QTime time = QTime.currentTime();
        StringBuffer text = new StringBuffer(time.toString("hh:mm"));
        if ((time.second() % 2) == 0)
            text.setCharAt(2, ' ');
        display(text.toString());
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new DigitalClock().show();

        QApplication.exec();
    }
}
