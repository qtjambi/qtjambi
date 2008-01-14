/*   Ported from: src.gui.kernel.qaction.cpp
<snip>
//! [0]
    QApplication app(argc, argv);
    app.setAttribute(Qt::AA_DontShowIconsInMenus);  // Icons are *no longer shown* in menus
    // ...
    QAction *myAction = new QAction();
    // ...
    myAction->setIcon(SomeIcon);
    myAction->setIconVisibleInMenu(true);   // Icon *will* be shown in menus for *this* action.
//! [0]


</snip>
*/
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
//! [0]
    QApplication app(argc, argv);
    app.setAttribute(Qt.AA_DontShowIconsInMenus);  // Icons are o longer shown* in menus
    // ...
    QAction yAction = new QAction();
    // ...
    myAction.setIcon(SomeIcon);
    myAction.setIconVisibleInMenu(true);   // Icon ill* be shown in menus for his* action.
//! [0]


    }
}
