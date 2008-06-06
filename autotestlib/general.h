/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef GENERAL_H
#define GENERAL_H

#include <QtGui>

class CalendarWidgetAccessor: public QCalendarWidget {
public:
    void paintCellAccess(QPainter *p) {
        paintCell(p, QRect(), QDate::currentDate());
    }
};

class General {

public:
    static void callPaintCell(QCalendarWidget *w, QPainter *painter) {
        QPainter localPainter;
        if (painter == 0)
            painter = &localPainter;

        static_cast<CalendarWidgetAccessor *>(w)->paintCellAccess(painter);
    }

    static void callPaintCellNull(QCalendarWidget *w) {
        static_cast<CalendarWidgetAccessor *>(w)->paintCellAccess(0);
    }

};

#endif
