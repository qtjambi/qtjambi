import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qabstractbutton extends QObject {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }

void snipped()
{
//! [0]
        QPushButton button = new QPushButton(tr("Ro&ck && Roll"));
//! [0]


//! [1]
        button.setIcon(new QIcon(":/images/print.png"));
        button.setShortcut(tr("Alt+F7"));
//! [1]
}

//! [2]
void reactToToggle(boolean checked)
{
   if (checked) {
      // Examine the new button states ...
   }
}
//! [2]
}
