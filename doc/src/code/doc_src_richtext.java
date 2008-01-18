/*   Ported from: doc.src.richtext.qdoc
<snip>
//! [0]
QTextDocument *newDocument = new QTextDocument;
//! [0]


//! [1]
QTextEdit *editor = new QTextEdit;
QTextDocument *editorDocument = editor->document();
//! [1]


//! [2]
    QTextEdit *editor = new QTextEdit(parent);
    editor->setHtml(aStringContainingHTMLtext);
    editor->show();
//! [2]


//! [3]
    QTextDocument *document = editor->document();
//! [3]


//! [4]
    QTextCursor cursor = editor->textCursor();
//! [4]


//! [5]
    editor->setTextCursor(cursor);
//! [5]


//! [6]
    textEdit.show();

    textCursor.beginEditBlock();

    for (int i = 0; i < 1000; ++i) {
        textCursor.insertBlock();
        textCursor.insertText(paragraphText.at(i));
    }

    textCursor.endEditBlock(); 
//! [6]


//! [7]
        <meta http-equiv="Content-Type" content="text/html; charset=EUC-JP" />
//! [7]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_richtext {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
QTextDocument newDocument = new QTextDocument();
//! [0]


//! [1]
QTextEdit editor = new QTextEdit();
QTextDocument editorDocument = editor.document();
//! [1]

    theRest();
}

public static void theRest() {
    QWidget parent = null;
    java.lang.String aStringContainingHTMLtext = "";

//! [2]
    QTextEdit editor = new QTextEdit(parent);
    editor.setHtml(aStringContainingHTMLtext);
    editor.show();
//! [2]

//! [3]
    QTextDocument document = editor.document();
//! [3]


//! [4]
    QTextCursor cursor = editor.textCursor();
//! [4]


//! [5]
    editor.setTextCursor(cursor);
//! [5]


    QTextEdit textEdit = editor;
    QTextCursor textCursor = null;
    java.lang.String  someText = null;

//! [6]
    textEdit.show();

    textCursor.beginEditBlock();

    for (int i = 0; i < 1000; ++i) {
        textCursor.insertBlock();
        textCursor.insertText(someText);
    }

    textCursor.endEditBlock(); 
//! [6]


/*
//! [7]
        <meta http-equiv="Content-Type" content="text/html; charset=EUC-JP" />

//! [7]
*/

    }
}
