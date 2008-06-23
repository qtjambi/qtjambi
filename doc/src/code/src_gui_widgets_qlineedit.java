import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_widgets_qlineedit extends QLineEdit {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
//! [0]
        protected void contextMenuEvent(QContextMenuEvent event)
        {
            QMenu menu = createStandardContextMenu();
            menu.addAction(tr("My Menu Item"));
            //...
            menu.exec(event.globalPos());
        }
//! [0]

}
