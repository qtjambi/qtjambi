import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qclipboard {
    static void main(String args[]) {
        QApplication.initialize(args);

        //! [0]
        QClipboard clipboard = QApplication.clipboard();
        String originalText = clipboard.text();
        String newText = "new words";
        clipboard.setText(newText);
        //! [0]

        QImage image = new QImage();

        //! [1]
        QMimeData data = new QMimeData();
        data.setImageData(image);
        clipboard.setMimeData(data, QClipboard.Mode.Clipboard);
        //! [1]
    }
}
