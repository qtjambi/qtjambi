/*   Ported from: src.gui.widgets.qdatetimeedit.cpp
<snip>
//! [0]
  QDateTimeEdit *dateEdit = new QDateTimeEdit(QDate::currentDate());
  dateEdit->setMinimumDate(QDate::currentDate().addDays(-365));
  dateEdit->setMaximumDate(QDate::currentDate().addDays(365));
  dateEdit->setDisplayFormat("yyyy.MM.dd");
//! [0]


//! [1]
  setDateTimeRange(min, max);
//! [1]


//! [2]
  setMinimumDateTime(min);
  setMaximumDateTime(max);
//! [2]


//! [3]
  setDateRange(min, max);
//! [3]


//! [4]
  setMinimumDate(min);
  setMaximumDate(max);
//! [4]


//! [5]
  setTimeRange(min, max);
//! [5]


//! [6]
  setMinimumTime(min);
  setMaximumTime(max);
//! [6]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qdatetimeedit {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
  QDateTimeEdit ateEdit = new QDateTimeEdit(QDate.currentDate());
  dateEdit.setMinimumDate(QDate.currentDate().addDays(-365));
  dateEdit.setMaximumDate(QDate.currentDate().addDays(365));
  dateEdit.setDisplayFormat("yyyy.MM.dd");
//! [0]


//! [1]
  setDateTimeRange(min, max);
//! [1]


//! [2]
  setMinimumDateTime(min);
  setMaximumDateTime(max);
//! [2]


//! [3]
  setDateRange(min, max);
//! [3]


//! [4]
  setMinimumDate(min);
  setMaximumDate(max);
//! [4]


//! [5]
  setTimeRange(min, max);
//! [5]


//! [6]
  setMinimumTime(min);
  setMaximumTime(max);
//! [6]


    }
}
