/*   Ported from: src.gui.text.qfontmetrics.cpp
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
{
//! [0]
    QFont font = new QFont("times", 24);
    QFontMetrics fm = new QFontMetrics(font);
    int pixelsWide = fm.width("What's the width of this text?");
    int pixelsHigh = fm.height();
//! [0]
}

{
//! [1]
    QFont font = new QFont("times", 24);
    QFontMetricsF fm = new QFontMetricsF(font);
    double pixelsWide = fm.width("What's the width of this text?");
    double pixelsHigh = fm.height();
//! [1]
}

    }
}
