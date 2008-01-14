/*   Ported from: src.opengl.qglcolormap.cpp
<snip>
//! [0]
    #include <QApplication>
    #include <QGLColormap>

    int main()
    {
        QApplication app(argc, argv);

        MySuperGLWidget widget;     // a QGLWidget in color-index mode
        QGLColormap colormap;

        // This will fill the colormap with colors ranging from
        // black to white.
        for (int i = 0; i < colormap.size(); i++)
            colormap.setEntry(i, qRgb(i, i, i));

        widget.setColormap(colormap);
        widget.show();
        return app.exec();
    }
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


public class src_opengl_qglcolormap {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
    #include <QApplication>
    #include <QGLColormap>

    int main()
    {
        QApplication app(argc, argv);

        MySuperGLWidget widget;     // a QGLWidget in color-index mode
        QGLColormap colormap;

        // This will fill the colormap with colors ranging from
        // black to white.
        for (int i = 0; i < colormap.size(); i++)
            colormap.setEntry(i, qRgb(i, i, i));

        widget.setColormap(colormap);
        widget.show();
        return app.exec();
    }
//! [0]


    }
}
