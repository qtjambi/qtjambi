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

/**
 * This class assists Java applications trying to target both
 * ARM CPU and non-ARM CPUs with the least modification to Java
 * source code.
 *
 * Example usage, instead of using '1.0' which is a 'double' type
 * in the Java language you instead use this class to ensure it is
 * the correct type for Qt APIs:
 *   QReal.valueOf(1.0).platformType()
 *
 */
public class QReal {
    private double dvalue;
    private float fvalue;
    private boolean dset;
    private boolean fset;

    public QReal(double value) {
        this.dvalue = value;
        this.dset = true;
    }

    public QReal(float value) {
        this.fvalue = value;
        this.fset = true;
    }

    public static QReal valueOf(double value) {
        return new QReal(value);
    }

    public static QReal valueOf(float value) {
        return new QReal(value);
    }

// MARKER_BEGIN_NOT_ARM
    // On non-ARM platforms (Intel,PPC)
    public QReal() {
        this(0);
    }

    public double platformValue() {
        return doubleValue();
    }

    public Double platformBoxedValue() {
        return Double.valueOf(platformValue());
    }

    public static Class<? extends Number> platformType() {
        return Double.class;
    }
// MARKER_END_NOT_ARM

    public float floatValue() {
        if(dset)
            return (float) dvalue;
        return fvalue;
    }

    public double doubleValue() {
        if(fset)
            return (double) fvalue;
        return dvalue;
    }

}
