import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class whatsthis extends QMainWindow
{
    public whatsthis()
    {
    //! [0]
        newAct = new QAction(tr("New"), this);
        newAct.setShortcut(tr("Ctrl+N"));
        newAct.setStatusTip(tr("Create a new file"));
        newAct.setWhatsThis(tr("Click this option to create a new file."));
    //! [0]
    }

    private QAction newAct;

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new whatsthis().show();

        QApplication.execStatic();
        QApplication.shutdown();
    }

}
