/*   Ported from: src.gui.dialogs.qwizard.cpp
<snip>
//! [0]
        int LicenseWizard::nextId() const
        {
            switch (currentId()) {
            case Page_Intro:
                if (field("intro.evaluate").toBool()) {
                    return Page_Evaluate;
                } else {
                    return Page_Register;
                }
            case Page_Evaluate:
                return Page_Conclusion;
            case Page_Register:
                if (field("register.upgradeKey").toString().isEmpty()) {
                    return Page_Details;
                } else {
                    return Page_Conclusion;
                }
            case Page_Details:
                return Page_Conclusion;
            case Page_Conclusion:
            default:
                return -1;
            }
        }
//! [0]


//! [1]
        MyWizard::MyWizard(QWidget *parent)
            : QWizard(parent)
        {
            ...
            QList<QWizard::WizardButton> layout;
            layout << QWizard::Stretch << QWizard::BackButton << QWizard::CloseButton
                   << QWizard::NextButton << QWizard::FinishButton;
            setButtonLayout(layout);
            ...
        }
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


public class src_gui_dialogs_qwizard {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        int LicenseWizard.nextId()
        {
            switch (currentId()) {
            case Page_Intro:
                if (field("intro.evaluate").toBool()) {
                    return Page_Evaluate;
                } else {
                    return Page_Register;
                }
            case Page_Evaluate:
                return Page_Conclusion;
            case Page_Register:
                if (field("register.upgradeKey").toString().isEmpty()) {
                    return Page_Details;
                } else {
                    return Page_Conclusion;
                }
            case Page_Details:
                return Page_Conclusion;
            case Page_Conclusion:
            default:
                return -1;
            }
        }
//! [0]


//! [1]
        MyWizard.MyWizard(QWidget arent)
            : QWizard(parent)
        {
            ...
            QList<QWizard.WizardButton> layout;
            layout << QWizard.Stretch << QWizard.BackButton << QWizard.CloseButton
                   << QWizard.NextButton << QWizard.FinishButton;
            setButtonLayout(layout);
            ...
        }
//! [1]


    }
}
