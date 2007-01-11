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

import com.trolltech.launcher.Style;
import com.trolltech.qt.gui.*;

public class Test
{
    public static void main(String args[]) throws Exception
    {
        QApplication.initialize(args);
        
        System.out.println("Created groupbox...");
        QGroupBox box = new QGroupBox();
        System.out.println(" -> ok");
        
        System.out.println("Creating style...");
        QStyle style = new Style(box);
        System.out.println(" -> ok");
        box.setStyle(style);
        System.out.println(" -> ok to set style");
        box.setTitle("Hey there...");
        
        System.out.println(" -> ok to set tilte..");
        
        box.show();
        
        QApplication.exec();
    }
}
