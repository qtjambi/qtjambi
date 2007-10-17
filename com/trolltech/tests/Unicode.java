package com.trolltech.tests;

import com.trolltech.qt.gui.*;

public class Unicode extends QWidget {

    protected void paintEvent(QPaintEvent e) {
        QPainter p = new QPainter(this);

        p.translate(0, 20);

        renderUnicodeTable(p, new QFont("Times New Roman"));

        p.translate(240, 0);
        renderUnicodeTable(p, new QFont("Courier New"));

        p.translate(240, 0);
        renderUnicodeTable(p, new QFont("Tahoma"));

        p.translate(240, 0);
        renderUnicodeTable(p, new QFont("Comic Sans MS"));

    }

    private void renderUnicodeTable(QPainter p, QFont font) {
        p.setFont(font);
        p.drawText(0, 0, font.family());
        for (int c=0; c<8; ++c) {
            for (int r=0; r<16; ++r) {
                char character = (char) (0x2500 | (c << 4) | r);
                p.drawText(10 + c * font.pointSize() * 2, 10 + r * font.pointSize() * 2, "" + character);
            }
        }
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        Unicode code = new Unicode();
        code.show();

        QApplication.exec();
    }

}

