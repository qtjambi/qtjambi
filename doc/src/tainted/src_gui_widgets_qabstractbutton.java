/*   Ported from: src.gui.widgets.qabstractbutton.cpp
<snip>
//! [0]
        QPushButton *button = new QPushButton(tr("Ro&ck && Roll"), this);
//! [0]


//! [1]
        button->setIcon(QIcon(":/images/print.png"));
        button->setShortcut(tr("Alt+F7"));
//! [1]


//! [2]
void MyWidget::reactToToggle(bool checked)
{
   if (checked) {
      // Examine the new button states.
      ...
   }
}
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qabstractbutton {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QPushButton utton = new QPushButton(tr("Rok && Roll"), this);
//! [0]


//! [1]
        button.setIcon(QIcon(":/images/print.png"));
        button.setShortcut(tr("Alt+F7"));
//! [1]


//! [2]
void MyWidget.reactToToggle(booleanschecked)
{
   if (checked) {
      // Examine the new button states.
      ...
   }
}
//! [2]


    }
}
