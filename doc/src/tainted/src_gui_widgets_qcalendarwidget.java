/*   Ported from: src.gui.widgets.qcalendarwidget.cpp
<snip>
//! [0]
            QCalendarWidget *calendar;
            calendar->setGridVisible(true);
//! [0]


//! [1]
    QCalendarWidget *calendar;
    calendar->setGridVisible(true);
    calendar->setMinimumDate(QDate(2006, 6, 19));
//! [1]


//! [2]
    QCalendarWidget *calendar;
    calendar->setGridVisible(true);
    calendar->setMaximumDate(QDate(2006, 7, 3));
//! [2]


//! [3]
        QCalendarWidget *calendar;

        calendar->setDateRange(min, max);
//! [3]


//! [4]
        QCalendarWidget *calendar;

        calendar->setMinimumDate(min);
        calendar->setMaximumDate(max);
//! [4]


//! [5]
            QCalendarWidget *calendar;
            calendar->setGridVisible(true);
//! [5]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qcalendarwidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
            QCalendarWidget alendar;
            calendar.setGridVisible(true);
//! [0]


//! [1]
    QCalendarWidget alendar;
    calendar.setGridVisible(true);
    calendar.setMinimumDate(QDate(2006, 6, 19));
//! [1]


//! [2]
    QCalendarWidget alendar;
    calendar.setGridVisible(true);
    calendar.setMaximumDate(QDate(2006, 7, 3));
//! [2]


//! [3]
        QCalendarWidget alendar;

        calendar.setDateRange(min, max);
//! [3]


//! [4]
        QCalendarWidget alendar;

        calendar.setMinimumDate(min);
        calendar.setMaximumDate(max);
//! [4]


//! [5]
            QCalendarWidget alendar;
            calendar.setGridVisible(true);
//! [5]


    }
}
