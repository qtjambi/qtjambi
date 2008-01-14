/*   Ported from: src.gui.text.qtextcursor.cpp
<snip>
//! [0]
    cursor.clearSelection();
    cursor.movePosition(QTextCursor::NextWord, QTextCursor::KeepAnchor);
    cursor.insertText("Hello World");
//! [0]


//! [1]
    QImage img = ...
    textDocument->addResource(QTextDocument::ImageResource, QUrl("myimage"), img);
    cursor.insertImage("myimage");
//! [1]


//! [2]
    QTextCursor cursor(textDocument);
    cursor.beginEditBlock();
    cursor.insertText("Hello");
    cursor.insertText("World");
    cursor.endEditBlock();

    textDocument->undo();
//! [2]


//! [3]
    QTextCursor cursor(textDocument);
    cursor.beginEditBlock();
    cursor.insertText("Hello");
    cursor.insertText("World");
    cursor.endEditBlock();

    ...

    cursor.joinPreviousEditBlock();
    cursor.insertText("Hey");
    cursor.endEditBlock();

    textDocument->undo();
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


public class src_gui_text_qtextcursor {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    cursor.clearSelection();
    cursor.movePosition(QTextCursor.NextWord, QTextCursor.KeepAnchor);
    cursor.insertText("Hello World");
//! [0]


//! [1]
    QImage img = ...
    textDocument.addResource(QTextDocument.ImageResource, QUrl("myimage"), img);
    cursor.insertImage("myimage");
//! [1]


//! [2]
    QTextCursor cursor(textDocument);
    cursor.beginEditBlock();
    cursor.insertText("Hello");
    cursor.insertText("World");
    cursor.endEditBlock();

    textDocument.undo();
//! [2]


//! [3]
    QTextCursor cursor(textDocument);
    cursor.beginEditBlock();
    cursor.insertText("Hello");
    cursor.insertText("World");
    cursor.endEditBlock();

    ...

    cursor.joinPreviousEditBlock();
    cursor.insertText("Hey");
    cursor.endEditBlock();

    textDocument.undo();
//! [3]


    }
}
