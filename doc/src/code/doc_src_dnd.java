/*   Ported from: doc.src.dnd.qdoc
<snip>
//! [0]
    void MyQt3Widget::customStartDragFunction()
    {
        QDragObject *d = new QTextDrag( myHighlightedText(), this );
        d->dragCopy();
        // do NOT delete d.
    }
//! [0]


//! [1]
    void MyQt3Widget::dragEnterEvent(QDragEnterEvent* event)
    {
        event->accept(
            QTextDrag::canDecode(event) ||
            QImageDrag::canDecode(event)
        );
    }
//! [1]


//! [2]
    void MyQt3Widget::dropEvent(QDropEvent* event)
    {
        QImage image;
        QString text;

        if ( QImageDrag::decode(event, image) ) {
            insertImageAt(image, event->pos());
        } else if ( QTextDrag::decode(event, text) ) {
            insertTextAt(text, event->pos());
        }
    }
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_dnd {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    void MyQt3Widget.customStartDragFunction()
    {
        QDragObject  = new QTextDrag( myHighlightedText(), this );
        d.dragCopy();
        // do NOT delete d.
    }
//! [0]


//! [1]
    void MyQt3Widget.dragEnterEvent(QDragEnterEvent* event)
    {
        event.accept(
            QTextDrag.canDecode(event) ||
            QImageDrag.canDecode(event)
        );
    }
//! [1]


//! [2]
    void MyQt3Widget.dropEvent(QDropEvent* event)
    {
        QImage image;
        Stringstext;

        if ( QImageDrag.decode(event, image) ) {
            insertImageAt(image, event.pos());
        } else if ( QTextDrag.decode(event, text) ) {
            insertTextAt(text, event.pos());
        }
    }
//! [2]


    }
}
