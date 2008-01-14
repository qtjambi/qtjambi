/*   Ported from: src.gui.itemviews.qitemeditorfactory.cpp
<snip>
//! [0]
        Q_PROPERTY(QColor color READ color WRITE setColor USER true)
//! [0]


//! [1]
    QItemEditorCreator<MyEditor> *itemCreator =
        new QItemEditorCreator<MyEditor>("myProperty");

    QItemEditorFactory *factory = new QItemEditorFactory;
//! [1]


//! [2]
    QItemEditorFactory *editorFactory = new QItemEditorFactory;
    QItemEditorCreatorBase *creator = new QStandardItemEditorCreator<MyFancyDateTimeEdit>();
    editorFactory->registerEditor(QVariant::DateType, creator);
//! [2]


//! [3]
	Q_PROPERTY(QColor color READ color WRITE setColor USER true)	
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_itemviews_qitemeditorfactory {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        Q_PROPERTY(QColor color READ color WRITE setColor USER true)
//! [0]


//! [1]
    QItemEditorCreator<MyEditor> temCreator =
        new QItemEditorCreator<MyEditor>("myProperty");

    QItemEditorFactory actory = new QItemEditorFactory;
//! [1]


//! [2]
    QItemEditorFactory ditorFactory = new QItemEditorFactory;
    QItemEditorCreatorBase reator = new QStandardItemEditorCreator<MyFancyDateTimeEdit>();
    editorFactory.registerEditor(QVariant.DateType, creator);
//! [2]


//! [3]
	Q_PROPERTY(QColor color READ color WRITE setColor USER true)	
//! [3]


    }
}
