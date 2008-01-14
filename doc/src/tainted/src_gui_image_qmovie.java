/*   Ported from: src.gui.image.qmovie.cpp
<snip>
//! [0]
        QLabel label;
        QMovie *movie = new QMovie("animations/fire.gif");

        label.setMovie(movie);
        movie->start();
//! [0]


//! [1]
        QMovie movie("racecar.gif");
        movie.setSpeed(200); // 2x speed
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_image_qmovie {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QLabel label;
        QMovie ovie = new QMovie("animations/fire.gif");

        label.setMovie(movie);
        movie.start();
//! [0]


//! [1]
        QMovie movie("racecar.gif");
        movie.setSpeed(200); // 2x speed
//! [1]


    }
}
