/*   Ported from: src.gui.widgets.qtextbrowser.cpp
<snip>
//! [0]
    backaction.setToolTip(browser.historyTitle(-1));
    forwardaction.setToolTip(browser.historyTitle(+1));
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


public class src_gui_widgets_qtextbrowser {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    backaction.setToolTip(browser.historyTitle(-1));
    forwardaction.setToolTip(browser.historyTitle(+1));
//! [0]


    }
}
