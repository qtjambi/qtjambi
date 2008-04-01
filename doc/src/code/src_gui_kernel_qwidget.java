/*   Ported from: src.gui.kernel.qwidget.cpp
<snip>
//! [0]
        w.setWindowState(w.windowState() ^ Qt.WindowFullScreen);
//! [0]


//! [1]
        w.setWindowState(w.windowState() & ~Qt.WindowMinimized | Qt.WindowActive);
//! [1]


//! [2]
        width = baseSize().width() + i * sizeIncrement().width();
        height = baseSize().height() + j * sizeIncrement().height();
//! [2]


//! [3]
        aWidget.window().setWindowTitle("New Window Title");
//! [3]


//! [4]
        QFont font("Helvetica", 12, QFont.Bold);
        setFont(font);
//! [4]


//! [5]
        QFont font;
        font.setBold(false);
        setFont(font);
//! [5]


//! [6]
        setCursor(Qt.IBeamCursor);
//! [6]


//! [7]
        QPixmap pixmap(widget.size());
        widget.render(ixmap);
//! [7]


//! [8]
        QPainter painter(this);
        ...
        painter.end();
        myWidget.render(this);
//! [8]


//! [9]
        setTabOrder(a, b); // a to b
        setTabOrder(b, c); // a to b to c
        setTabOrder(c, d); // a to b to c to d
//! [9]


//! [10]
        // WRONG
        setTabOrder(c, d); // c to d
        setTabOrder(a, b); // a to b AND c to d
        setTabOrder(b, c); // a to b to c, but not c to d
//! [10]


//! [11]
    void MyWidget.closeEvent(QCloseEvent vent)
    {
        QSettings settings("MyCompany", "MyApp");
        settings.setValue("geometry", saveGeometry());
        QWidget.closeEvent(event);
    }
//! [11]


//! [12]
        QSettings settings("MyCompany", "MyApp");
        myWidget.restoreGeometry(settings.value("myWidget/geometry").toByteArray());
//! [12]


//! [13]
        setUpdatesEnabled(false);
        bigVisualChanges();
        setUpdatesEnabled(true);
//! [13]


//! [14]
    ...
    extern void qt_x11_set_global_double_buffer(bool);
    qt_x11_set_global_double_buffer(false);
    ...
//! [14]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qwidget extends QWidget{
    public static void main(String args[]) {
        QApplication.initialize(args);

        if (true)
        {
        QWidget w = new QWidget();
//! [0]
        Qt.WindowStates state = w.windowState();

        if (w.windowState().isSet(Qt.WindowState.WindowFullScreen))
            state.set(Qt.WindowState.WindowFullScreen);
        else
            state.clear(Qt.WindowState.WindowFullScreen);

        w.setWindowState(state);
//! [0]
        }


        if (true)
        {
            QWidget w = new QWidget();
//! [1]
        Qt.WindowStates state= w.windowState();
        state.setValue(state.value() & ~Qt.WindowState.WindowMinimized.value() |
                                      Qt.WindowState.WindowActive.value());
        w.setWindowState(state);
//! [1]
        }


        if (true)
        {
                QWidget aWidget = new QWidget();
//! [3]
        aWidget.window().setWindowTitle("New Window Title");
//! [3]
        }

        if (true)
        {
                QWidget w = new QWidget();
                QFont font = new QFont();
//! [4]
        QFont fonti = new QFont("Helvetica", 12, QFont.Weight.Bold.value());
        w.setFont(font);
//! [4]
        }

        if (true)
        {
                QWidget w = new QWidget();
//! [5]
        QFont font = new QFont();
        font.setBold(false);
        w.setFont(font);
//! [5]
        }

        if (true)
        {
            QWidget w = new QWidget();
//! [6]
        w.setCursor(new QCursor(Qt.CursorShape.IBeamCursor));
//! [6]
        }

        if (true)
        {
                QWidget widget = new QWidget();
//! [7]
        QPixmap pixmap = new QPixmap(widget.size());
        widget.render(pixmap);
//! [7]
        }

    if (true)
    {
            QWidget myWidget = new QWidget();
//! [12]
        QSettings settings = new QSettings("MyCompany", "MyApp");
        myWidget.restoreGeometry((QByteArray) settings.value("myWidget/geometry"));
//! [12]
    }

/*
//! [14]
    This code is not relevant for Qt Jambi
    ...
    extern void qt_x11_set_global_double_buffer(bool);
    qt_x11_set_global_double_buffer(false);
    ...
//! [14]
*/

    }
    void tull()
    {
        QWidget a = null, b = null, c = null, d = null;

        if (true)
        {
                QWidget myWidget = null;
//! [8]
        QPainter painter = new QPainter(this);
        //...
        painter.end();
        myWidget.render(this);
//! [8]

//! [9]
        setTabOrder(a, b); // a to b
        setTabOrder(b, c); // a to b to c
        setTabOrder(c, d); // a to b to c to d
//! [9]


//! [10]
        // WRONG
        setTabOrder(c, d); // c to d
        setTabOrder(a, b); // a to b AND c to d
        setTabOrder(b, c); // a to b to c, but not c to d
//! [10]

//! [13]
        setUpdatesEnabled(false);
        bigVisualChanges();
        setUpdatesEnabled(true);
//! [13]
        if (true)
        {
                int width, height, i = 0, j = 0;
//! [2]
        width = baseSize().width() + i * sizeIncrement().width();
        height = baseSize().height() + j * sizeIncrement().height();
//! [2]
        }

        }
    }
//! [11]
    protected void closeEvent(QCloseEvent event)
    {
        QSettings settings = new QSettings("MyCompany", "MyApp");
        settings.setValue("geometry", saveGeometry());
        super.closeEvent(event);
    }
//! [11]

    static void bigVisualChanges() {}

}
