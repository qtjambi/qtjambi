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

package com.trolltech.extensions.jfc;

import java.awt.*;
import java.awt.geom.*;
import java.util.Calendar;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QPolygon;

public class Util {

    public static Calendar convert(QDateTime from) {
        Calendar to = Calendar.getInstance();
        QDate fromDate = from.date();
        QTime fromTime = from.time();
        to.set(fromDate.year(), fromDate.month() - 1, fromDate.day(), fromTime.hour(), fromTime.minute(), fromTime.second());
        return to;
    }

    public static QDateTime convert(Calendar from) {
        return new QDateTime(new QDate(from.get(Calendar.YEAR), from.get(Calendar.MONTH) + 1, from.get(Calendar.DAY_OF_MONTH)), new QTime(from
                .get(Calendar.HOUR), from.get(Calendar.MINUTE), from.get(Calendar.SECOND)));
    }

    public static Rectangle2D convert(QRect from) {
        return new Rectangle(from.x(), from.y(), from.width(), from.height());
    }

    public static QRect convert(Rectangle from) {
        return new QRect(from.x, from.y, from.width, from.height);
    }

    public static Rectangle2D convert(QRectF from) {
        return new Rectangle2D.Double(from.x(), from.y(), from.width(), from.height());
    }

    public static QRectF convert(Rectangle2D.Double from) {
        return new QRectF(from.getX(), from.getY(), from.getWidth(), from.getHeight());
    }

    public static QPoint convert(Point from) {
        return new QPoint(from.x, from.y);
    }

    public static Point convert(QPoint from) {
        return new Point(from.x(), from.y());
    }

    public static QPointF convert(Point2D.Double from) {
        return new QPointF(from.x, from.y);
    }

    public static Point2D convert(QPointF from) {
        return new Point2D.Double(from.x(), from.y());
    }

    public static QLineF convert(Line2D.Double from) {
        return new QLineF(from.x1, from.y1, from.x2, from.y2);
    }

    public static Line2D convert(QLineF from) {
        return new Line2D.Double(from.x1(), from.y1(), from.x2(), from.y2());
    }

    public static QPolygon convert(Polygon from) {
        QPolygon to = new QPolygon();
        for (int i = 0; i < from.npoints; i++) {
            to.add(from.xpoints[i], from.ypoints[i]);
        }
        return to;
    }

    public static Polygon convert(QPolygon from) {
        Polygon to = new Polygon();
        for (int i = 0; i < from.size(); i++) {
            Point point = convert(from.at(i));
            to.addPoint(point.x, point.y);
        }
        return to;
    }
}
