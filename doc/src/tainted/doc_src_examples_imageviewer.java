/*   Ported from: doc.src.examples.imageviewer.qdoc
<snip>
//! [0]
        imageLabel->resize(imageLabel->pixmap()->size());
//! [0]


//! [1]
        if (!imageLabel->pixmap())
             qFatal("ASSERT: "imageLabel->pixmap()" in file ...");
//! [1]


//! [2]
        qmake "CONFIG += debug" foo.pro
//! [2]


//! [3]
        qmake "CONFIG += release" foo.pro
//! [3]


//! [4]
        scrollBar->setValue(int(factor * scrollBar->value()));
//! [4]


</snip>
*/
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
//! [0]
        imageLabel.resize(imageLabel.pixmap().size());
//! [0]


//! [1]
        if (!imageLabel.pixmap())
             qFatal("ASSERT: "imageLabel.pixmap()" in file ...");
//! [1]


//! [2]
        qmake "CONFIG += debug" foo.pro
//! [2]


//! [3]
        qmake "CONFIG += release" foo.pro
//! [3]


//! [4]
        scrollBar.setValue(int(factor * scrollBar.value()));
//! [4]


    }
}
