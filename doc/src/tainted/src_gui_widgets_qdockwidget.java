/*   Ported from: src.gui.widgets.qdockwidget.cpp
<snip>
//! [0]
       QDockWidget *dockWidget = qobject_cast<QDockWidget*>(parentWidget());
       if (dockWidget->features() & QDockWidget::DockWidgetVerticalTitleBar) {
           // I need to be vertical
       } else {
           // I need to be horizontal
       }
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


public class src_gui_widgets_qdockwidget {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
       QDockWidget ockWidget = qobject_cast<QDockWidget*>(parentWidget());
       if (dockWidget.features() & QDockWidget.DockWidgetVerticalTitleBar) {
           // I need to be vertical
       } else {
           // I need to be horizontal
       }
//! [0]


    }
}
