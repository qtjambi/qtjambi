import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class doc_src_examples_editabletreemodel {
    public static void main(String args[]) {
        QApplication.initialize(args);

        QAbstractItemModel model = null;
//! [0]
    Object a = model.index(0, 0, null).data();
//! [0]


//! [1]
    Object b = model.index(1, 0, null).data();
//! [1]
    if (b != null && a != null) {}


    }
}
