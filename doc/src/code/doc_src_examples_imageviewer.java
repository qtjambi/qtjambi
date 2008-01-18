import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_examples_imageviewer {
    public static void main(String args[]) {
        QApplication.initialize(args);

        QLabel imageLabel = new QLabel();
//! [0]
        imageLabel.resize(imageLabel.pixmap().size());
//! [0]


//! [1]
        if (imageLabel.pixmap().isNull())
             throw new RuntimeException("Missing pixmap in file ...");
//! [1]


        QScrollBar scrollBar = new QScrollBar();
        double factor = 0;
//! [4]
        scrollBar.setValue((int)(factor * scrollBar.value()));
//! [4]


    }
}
