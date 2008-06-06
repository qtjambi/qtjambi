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

public class CRASH {
    public static void main(String args[]) {
        QApplication.initialize(args);

        QGraphicsScene scene = new QGraphicsScene();
        QGraphicsView view = new QGraphicsView();

        view.setScene(scene);

        QGraphicsTextItem crash = new QGraphicsTextItem();

        scene.addItem(crash );



        {
            crash.children();
            crash.document();
        }
        for (int i=0; i<1000; ++i) {
            crash.setPlainText("a");
            System.gc();
        }

    }
}
