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

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class Speed
{
    private static final int many = 300000;
    
    public static void main(String[] args)
    {
        QApplication app = new QApplication(args);        
        QWidget w = new QWidget();
        QRect rects[] = new QRect[many];
        
        long l = System.currentTimeMillis();
        for (int i=0; i<many; ++i) {
            rects[i] = w.geometry();
        }
        System.out.println(System.currentTimeMillis() - l);
        
        
    }
}
