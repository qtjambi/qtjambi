/*   Ported from: src.gui.text.qfontmetrics.cpp
<snip>
//! [0]
    QFont font("times", 24);
    QFontMetrics fm(font);
    int pixelsWide = fm.width("What's the width of this text?");
    int pixelsHigh = fm.height();
//! [0]


//! [1]
    QFont font("times", 24);
    QFontMetricsF fm(font);
    qreal pixelsWide = fm.width("What's the width of this text?");
    qreal pixelsHigh = fm.height();
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_text_qfontmetrics {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QFont font("times", 24);
    QFontMetrics fm(font);
    int pixelsWide = fm.width("What's the width of this text?");
    int pixelsHigh = fm.height();
//! [0]


//! [1]
    QFont font("times", 24);
    QFontMetricsF fm(font);
    double pixelsWide = fm.width("What's the width of this text?");
    double pixelsHigh = fm.height();
//! [1]


    }
}
