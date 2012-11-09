/****************************************************************************
**
** Copyright (C) 2012 Darryl L. Miles.  All rights reserved.
** Copyright (C) 2012 D L Miles Consulting Ltd.  All rights reserved.
**
** This file is part of Qt Jambi.
**
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
**
** In addition, as a special exception, the copyright holders grant you
** certain additional rights. These rights are described in the Nokia Qt
** LGPL Exception version 1.0, included in the file LGPL_EXCEPTION.txt in
** this package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 2.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL2 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 2.0 requirements will be
** met: http://www.gnu.org/licenses/gpl-2.0.html
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL3 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qt.qreal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class assists Java applications trying to target both
 * ARM CPU and non-ARM CPUs with the least modification to Java
 * source code.
 *
 * Example usage, instead of using '1.0' which is a 'double' type
 * in the Java language you instead use this class to ensure it is
 * the correct type for Qt APIs:
 *   List<Double> list = new ArrayList<Double>();
 *   list.add(Double.valueOf(1.0));
 *   QRealList.listOfDouble(list).platformValue()
 *
 */
public class QRealList {
    private List<Double> dlist;
    private List<Float> flist;

    private QRealList() {
    }

    public static QRealList listOfDouble(List<Double> list) {
        QRealList rl = new QRealList();
        rl.dlist = list;
        return rl;
    }

    public static QRealList listOf(List<Float> list) {
        QRealList rl = new QRealList();
        rl.flist = list;
        return rl;
    }

// MARKER_BEGIN_ARM
    // On ARM
    public List<Float> platformValueList() {
        return floatValueList();
    }
// MARKER_END_ARM

    public List<Float> floatValueList() {
        if(dlist != null) {
            List<Float> newList = new ArrayList<Float>();
            for(Double d : dlist) {
                if(d == null)
                    newList.add(null);
                else
                    newList.add(Float.valueOf(d.floatValue()));
            }
            return newList;
        }
        if(flist == null)
            return null;
        return Collections.unmodifiableList(flist);
    }

    public List<Double> doubleValueList() {
        if(flist != null) {
            List<Double> newList = new ArrayList<Double>();
            for(Float f : flist) {
                if(f == null)
                    newList.add(null);
                else
                    newList.add(Double.valueOf(f.doubleValue()));
            }
            return newList;
        }
        if(dlist == null)
            return null;
        return Collections.unmodifiableList(dlist);
    }

}
