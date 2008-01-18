/*   Ported from: src.gui.dialogs.qmessagebox.cpp
<snip>
//! [0]
        int ret = QMessageBox::warning(this, tr("My Application"),
                          tr("The document has been modified.\n"
                             "Do you want to save your changes?"),
                          QMessageBox::Save | QMessageBox::Discard
                          | QMessageBox::Cancel,
                          QMessageBox::Save);
//! [0]


//! [1]
        QMessageBox msgBox;
        msgBox.setStandardButtons(QMessageBox::Yes | QMessageBox::No);
        switch (msgBox.exec()) {
        case QMessageBox::Yes:
            // yes was clicked
            break;
        case QMessageBox::No:
            // no was clicked
            break;
        default:
            // should never be reached
            break;
        }
//! [1]


//! [2]
        QMessageBox msgBox;
        QPushButton *connectButton = msgBox.addButton(tr("Connect"), QMessageBox::ActionRole);
        QPushButton *abortButton = msgBox.addButton(QMessageBox::Abort);

        msgBox.exec();

        if (msgBox.clickedButton() == connectButton) {
            // connect
        } else if (msgBox.clickedButton() == abortButton) {
            // abort
        }
//! [2]


//! [3]
        QMessageBox messageBox(this);
        QAbstractButton *disconnectButton =
              messageBox.addButton(tr("Disconnect"), QMessageBox::ActionRole);
        ...
        messageBox.exec();
        if (messageBox.clickedButton() == disconnectButton) {
            ...
        }
//! [3]


//! [4]
        #include <QApplication>
        #include <QMessageBox>

        int main(int argc, char *argv[])
        {
            QT_REQUIRE_VERSION(argc, argv, "4.0.2")

            QApplication app(argc, argv);
            ...
            return app.exec();
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


public class src_gui_dialogs_qmessagebox {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        int ret = QMessageBox.warning(this, tr("My Application"),
                          tr("The document has been modified.\n"
                             "Do you want to save your changes?"),
                          QMessageBox.Save | QMessageBox.Discard
                          | QMessageBox.Cancel,
                          QMessageBox.Save);
//! [0]


//! [1]
        QMessageBox msgBox;
        msgBox.setStandardButtons(QMessageBox.Yes | QMessageBox.No);
        switch (msgBox.exec()) {
        case QMessageBox.Yes:
            // yes was clicked
            break;
        case QMessageBox.No:
            // no was clicked
            break;
        default:
            // should never be reached
            break;
        }
//! [1]


//! [2]
        QMessageBox msgBox;
        QPushButton onnectButton = msgBox.addButton(tr("Connect"), QMessageBox.ActionRole);
        QPushButton bortButton = msgBox.addButton(QMessageBox.Abort);

        msgBox.exec();

        if (msgBox.clickedButton() == connectButton) {
            // connect
        } else if (msgBox.clickedButton() == abortButton) {
            // abort
        }
//! [2]


//! [3]
        QMessageBox messageBox(this);
        QAbstractButton isconnectButton =
              messageBox.addButton(tr("Disconnect"), QMessageBox.ActionRole);
        ...
        messageBox.exec();
        if (messageBox.clickedButton() == disconnectButton) {
            ...
        }
//! [3]


//! [4]
        #include <QApplication>
        #include <QMessageBox>

        int main(int argc, char rgv[])
        {
            QT_REQUIRE_VERSION(argc, argv, "4.0.2")

            QApplication app(argc, argv);
            ...
            return app.exec();
        }
//! [4]


    }
}
