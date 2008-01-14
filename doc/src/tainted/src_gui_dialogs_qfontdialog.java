/*   Ported from: src.gui.dialogs.qfontdialog.cpp
<snip>
//! [0]
    bool ok;
    QFont font = QFontDialog::getFont(
                    &ok, QFont("Helvetica [Cronyx]", 10), this);
    if (ok) {
        // the user clicked OK and font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the initial
        // value, in this case Helvetica [Cronyx], 10
    }
//! [0]


//! [1]
    myWidget.setFont(QFontDialog::getFont(0, myWidget.font()));
//! [1]


//! [2]
    bool ok;
    QFont font = QFontDialog::getFont(&ok, QFont("Times", 12), this);
    if (ok) {
        // font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the initial
        // value, in this case Times, 12.
    }
//! [2]


//! [3]
    myWidget.setFont(QFontDialog::getFont(0, myWidget.font()));
//! [3]


//! [4]
    bool ok;
    QFont font = QFontDialog::getFont(&ok, this);
    if (ok) {
        // font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the default
        // application font, QApplication::font()
    }
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_dialogs_qfontdialog {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    booleansok;
    QFont font = QFontDialog.getFont(
                    k, QFont("Helvetica [Cronyx]", 10), this);
    if (ok) {
        // the user clicked OK and font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the initial
        // value, in this case Helvetica [Cronyx], 10
    }
//! [0]


//! [1]
    myWidget.setFont(QFontDialog.getFont(0, myWidget.font()));
//! [1]


//! [2]
    booleansok;
    QFont font = QFontDialog.getFont(k, QFont("Times", 12), this);
    if (ok) {
        // font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the initial
        // value, in this case Times, 12.
    }
//! [2]


//! [3]
    myWidget.setFont(QFontDialog.getFont(0, myWidget.font()));
//! [3]


//! [4]
    booleansok;
    QFont font = QFontDialog.getFont(k, this);
    if (ok) {
        // font is set to the font the user selected
    } else {
        // the user canceled the dialog; font is set to the default
        // application font, QApplication.font()
    }
//! [4]


    }
}
