/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** $END_LICENSE$

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
