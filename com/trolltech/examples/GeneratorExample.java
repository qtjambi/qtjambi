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

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.examples.generator.MyWidget;

public class GeneratorExample extends QWidget {
    
    public GeneratorExample(QWidget w)
    {
        QVBoxLayout layout = new QVBoxLayout();
        
        layout.addWidget(new MyWidget(this));
        
        setWindowIcon(new QIcon("classpath:com/trolltech/images/logo_32.png"));
        
        QPushButton exit = new QPushButton("Exit");
        exit.clicked.connect(this, "close()");
        layout.addWidget(exit);
        
        setLayout(layout);
    }

    public static void main(String[] args) {
        QApplication.initialize(args);
        GeneratorExample ex = new GeneratorExample(null);
        ex.show();
        
        QApplication.exec();
        
    }

}
