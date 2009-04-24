import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qaction {
    public static void main(String args[]) {
    QApplication.initialize(args);
    QWidget window = new QWidget();
    QIcon SomeIcon = new QIcon("myicon.xpm");
//! [0]
    QApplication.initialize(args);
    QApplication.setAttribute(Qt.ApplicationAttribute.AA_DontShowIconsInMenus);  // Icons are *no longer shown* in menus
    // ...
    QAction myAction = new QAction(window);
    // ...
    myAction.setIcon(SomeIcon);
    myAction.setIconVisibleInMenu(true);   // Icon *will* be shown in menus for *this* action.
//! [0]
    }
}
