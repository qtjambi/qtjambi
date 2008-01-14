/*   Ported from: src.gui.graphicsview.qgraphicslinearlayout.cpp
<snip>
//! [0]
        QGraphicsScene scene;
        QGraphicsWidget *textEdit = scene.addWidget(new QTextEdit);
        QGraphicsWidget *pushButton = scene.addWidget(new QPushButton);

        QGraphicsLinearLayout *layout = new QGraphicsLinearLayout;
        layout->addItem(textEdit);
        layout->addItem(pushButton);

        QGraphicsWidget *form = new QGraphicsWidget;
        form->setLayout(layout);
        scene.addItem(form);
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_graphicsview_qgraphicslinearlayout {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QGraphicsScene scene;
        QGraphicsWidget extEdit = scene.addWidget(new QTextEdit);
        QGraphicsWidget ushButton = scene.addWidget(new QPushButton);

        QGraphicsLinearLayout ayout = new QGraphicsLinearLayout;
        layout.addItem(textEdit);
        layout.addItem(pushButton);

        QGraphicsWidget orm = new QGraphicsWidget;
        form.setLayout(layout);
        scene.addItem(form);
//! [0]


    }
}
