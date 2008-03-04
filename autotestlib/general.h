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
