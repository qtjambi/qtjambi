/*   Ported from: src.corelib.tools.qtimeline.cpp
<snip>
//! [0]
        ...
        progressBar = new QProgressBar(this);
        progressBar->setRange(0, 100);

        // Construct a 1-second timeline with a frame range of 0 - 100
        QTimeLine *timeLine = new QTimeLine(1000, this);
        timeLine->setFrameRange(0, 100);
        connect(timeLine, SIGNAL(frameChanged(int)), progressBar, SLOT(setValue(int)));

        // Clicking the push button will start the progress bar animation
        pushButton = new QPushButton(tr("Start animation"), this);
        connect(pushButton, SIGNAL(clicked()), timeLine, SLOT(start()));
        ...
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


public class src_corelib_tools_qtimeline {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        ...
        progressBar = new QProgressBar(this);
        progressBar.setRange(0, 100);

        // Construct a 1-second timeline with a frame range of 0 - 100
        QTimeLine imeLine = new QTimeLine(1000, this);
        timeLine.setFrameRange(0, 100);
        connect(timeLine, SIGNAL(frameChanged(int)), progressBar, SLOT(setValue(int)));

        // Clicking the push button will start the progress bar animation
        pushButton = new QPushButton(tr("Start animation"), this);
        connect(pushButton, SIGNAL(clicked()), timeLine, SLOT(start()));
        ...
//! [0]


    }
}
