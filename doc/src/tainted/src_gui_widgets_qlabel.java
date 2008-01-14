/*   Ported from: src.gui.widgets.qlabel.cpp
<snip>
//! [0]
    QLabel *label = new QLabel(this);
    label->setFrameStyle(QFrame::Panel | QFrame::Sunken);
    label->setText("first line\nsecond line");
    label->setAlignment(Qt::AlignBottom | Qt::AlignRight);
//! [0]


//! [1]
    QLineEdit* phoneEdit = new QLineEdit(this);
    QLabel* phoneLabel = new QLabel("&Phone:", this);
    phoneLabel->setBuddy(phoneEdit);
//! [1]


//! [2]
    QLineEdit *nameEd  = new QLineEdit(this);
    QLabel    *nameLb  = new QLabel("&Name:", this);
    nameLb->setBuddy(nameEd);
    QLineEdit *phoneEd = new QLineEdit(this);
    QLabel    *phoneLb = new QLabel("&Phone:", this);
    phoneLb->setBuddy(phoneEd);
    // (layout setup not shown)
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


public class src_gui_widgets_qlabel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    QLabel abel = new QLabel(this);
    label.setFrameStyle(QFrame.Panel | QFrame.Sunken);
    label.setText("first line\nsecond line");
    label.setAlignment(Qt.AlignBottom | Qt.AlignRight);
//! [0]


//! [1]
    QLineEdit* phoneEdit = new QLineEdit(this);
    QLabel* phoneLabel = new QLabel("hone:", this);
    phoneLabel.setBuddy(phoneEdit);
//! [1]


//! [2]
    QLineEdit ameEd  = new QLineEdit(this);
    QLabel    ameLb  = new QLabel("ame:", this);
    nameLb.setBuddy(nameEd);
    QLineEdit honeEd = new QLineEdit(this);
    QLabel    honeLb = new QLabel("hone:", this);
    phoneLb.setBuddy(phoneEd);
    // (layout setup not shown)
//! [2]


    }
}
