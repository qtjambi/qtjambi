import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_dialogs_qabstractprintdialog {
    public static void main(String args[]) {
        QApplication.initialize(args);
        QWidget parent = new QWidget();
        QPrinter printer = new QPrinter();
//! [0]
        QPrintDialog printDialog = new QPrintDialog(printer, parent);
        if (printDialog.exec() == QDialog.DialogCode.Accepted.value()) {
            // print ...
        }
//! [0]


    }
}
