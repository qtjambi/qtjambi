import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class main
{

//! [0] //! [1]
public static void main(String args[])
{
    QApplication.initialize(args);

    QSplitter splitter = new QSplitter();

//! [2] //! [3]
    QDirModel model = new QDirModel();
//! [0] //! [2] //! [4] //! [5]
    QTreeView tree = new QTreeView(splitter);
//! [3] //! [6]
    tree.setModel(model);
//! [4] //! [6] //! [7]
    tree.setRootIndex(model.index(QDir.currentPath()));
//! [7]

    QListView list = new QListView(splitter);
    list.setModel(model);
    list.setRootIndex(model.index(QDir.currentPath()));

//! [5]
    QItemSelectionModel selection = new QItemSelectionModel(model);
    tree.setSelectionModel(selection);
    list.setSelectionModel(selection);

//! [8]
    splitter.setWindowTitle("Two views onto the same directory model");
    splitter.show();

    QApplication.execStatic();
    QApplication.shutdown();
}
//! [1] //! [8]

}
