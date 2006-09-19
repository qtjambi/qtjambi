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

class WigglyWidget extends QWidget {
    static final int sineTable[] = { 0, 38, 71, 92, 100, 92, 71, 38, 0, -38, -71, -92, -100, -92, -71, -38 };

    private QBasicTimer timer;
    private String text;
    private int step;

    WigglyWidget(QWidget parent) {
        super(parent);
        setBackgroundRole(QPalette.ColorRole.Midlight);

        QFont newFont = font();
        newFont.setPointSize(newFont.pointSize() + 20);
        setFont(newFont);

        step = 0;

        timer = new QBasicTimer();
        timer.start(60, this);
    }

    public void setText(String s) {
        text = s;
    }

    protected void paintEvent(QPaintEvent e) {
        QFontMetrics metrics = new QFontMetrics(font());
        int x = (width() - metrics.width(text)) / 2;
        int y = (height() + metrics.ascent() - metrics.descent()) / 2;
        QColor color = new QColor();

        QPainter painter = new QPainter();
        painter.begin(this);
        for (int i = 0; i < text.length(); ++i) {
            int index = (step + i) % 16;
            color.setHsv((15 - index) * 16, 255, 191);
            painter.setPen(color);
            painter.drawText(x, y - ((sineTable[index] * metrics.height()) / 400), text.substring(i, i + 1));
            x += metrics.width(text.substring(i, i + 1));
        }
        painter.end();
    }

    protected void timerEvent(QTimerEvent event) {
        if (event.timerId() == timer.timerId()) {
            ++step;
            update();
        } else {
            super.timerEvent(event);
        }
    }
}

public class Wiggly extends QDialog {
    public Wiggly(QWidget parent) {
        super(parent);

        WigglyWidget wigglyWidget = new WigglyWidget(null);
        QLineEdit lineEdit = new QLineEdit();

        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(wigglyWidget);
        layout.addWidget(lineEdit);
        setLayout(layout);

        lineEdit.textChanged.connect(wigglyWidget, "setText(String)");

        lineEdit.setText("Hello world!");

        setWindowTitle("Wiggly");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        resize(360, 145);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        Wiggly d = new Wiggly(null);
        d.show();

        QApplication.exec();
    }
}
