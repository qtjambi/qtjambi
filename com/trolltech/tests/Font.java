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
