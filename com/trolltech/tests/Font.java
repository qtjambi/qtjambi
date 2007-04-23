package com.trolltech.tests;

import com.trolltech.qt.gui.*;

public class Font extends QLabel 
{
    public Font() 
    {
        super();

        QFontDatabase.addApplicationFont("classpath:com/trolltech/tests/A Charming Font Outline.ttf");

        setFont(new QFont("A Charming Font Outline", 70));
        setText("Test font");
    }

        public static void main(String args[]) 
        {
            QApplication.initialize(args);

            Font font = new Font();
            font.show();

            QApplication.exec();
        }
}
