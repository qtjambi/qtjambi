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
import com.trolltech.qt.gui.*;

import java.util.*;

public class CacheListAdds {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        int COUNT = 100000;

        ArrayList<QPair<Double, QColor>> list = new ArrayList<QPair<Double, QColor>>();
        for (int i=0; i<10; ++i) {
            QPair<Double, QColor> p = new QPair<Double, QColor>(i / 9.0, new QColor(i * 255 / 9, 0, 0));
            list.add(p);
        }

        for (int c=0; c<COUNT; ++c) {
//            QLinearGradient grad = new QLinearGradient(0, 0, 100, 0);
//            grad.setStops(list);
        }

        long time =  (System.currentTimeMillis() - t1);
        System.out.println("did " + COUNT + ", took=" + time + ", average=" + time / (double) COUNT + "ms");
    }

}
