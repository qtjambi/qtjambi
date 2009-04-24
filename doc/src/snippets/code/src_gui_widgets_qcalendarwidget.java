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
        QDate min = new QDate(), max = new QDate();
        if (true)
        {
//! [0]
            QCalendarWidget calendar = new QCalendarWidget();
            calendar.setGridVisible(true);
//! [0]
        }

    if (true)
    {
//! [1]
    QCalendarWidget calendar = new QCalendarWidget();
    calendar.setGridVisible(true);
    calendar.setMinimumDate(new QDate(2006, 6, 19));
//! [1]
    }

    if (true)
    {
//! [2]
    QCalendarWidget calendar = new QCalendarWidget();
    calendar.setGridVisible(true);
    calendar.setMaximumDate(new QDate(2006, 7, 3));
//! [2]
    }

    if (true)
    {
//! [3]
        QCalendarWidget calendar = new QCalendarWidget();

        calendar.setDateRange(min, max);
//! [3]
    }

    if (true)
    {
//! [4]
        QCalendarWidget calendar = new QCalendarWidget();

        calendar.setMinimumDate(min);
        calendar.setMaximumDate(max);
//! [4]
    }

    if (true)
    {
//! [5]
            QCalendarWidget calendar = new QCalendarWidget();
            calendar.setGridVisible(true);
//! [5]
    }

    }
}
