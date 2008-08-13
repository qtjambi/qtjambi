import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_itemviews_qitemeditorfactory {

    QColor color;

//! [0]
    @QtPropertyReader
    @QtPropertyUser
    public QColor color() { return color; }

    @QtPropertyWriter
    public void setColor(QColor color) { this.color = color; }
//! [0]

    public static void main(String args[]) {
        QApplication.initialize(args);


        /*
//! [1]
    QItemEditorCreator itemCreator =
        new QItemEditorCreator<MyEditor>("color");

    QItemEditorFactory factory = new QItemEditorFactory;
//! [1]


//! [2]
    QItemEditorFactory editorFactory = new QItemEditorFactory;
    QItemEditorCreatorBase creator = new QStandardItemEditorCreator<MyFancyDateTimeEdit>();
    editorFactory.registerEditor(QVariant.DateType, creator);
//! [2]


//! [3]
    @QtPropertyReader
    @QtPropertyUser
    public QColor color() { return color; }

    @QtPropertyWriter
    public void setColor(QColor color) { this.color = color); }
//! [3]

*/

    }
}
